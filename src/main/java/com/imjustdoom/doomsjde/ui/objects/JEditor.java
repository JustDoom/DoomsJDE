package com.imjustdoom.doomsjde.ui.objects;

import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.api.storage.objects.Settings;
import com.imjustdoom.doomsjde.api.storage.objects.WorkspaceState;
import com.imjustdoom.doomsjde.api.syntax.GenericReader;
import com.imjustdoom.doomsjde.api.syntax.ReadableType;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class JEditor extends RTextScrollPane {
    private static final Map<String, ReadableType> READABLE_TYPES = new HashMap<>();
    private static final int MIN_FONT_SIZE = 6;
    private static final int MAX_FONT_SIZE = 48;

    private final JDE instance;
    private final RSyntaxTextArea textArea;

    private File openFile;

    public JEditor(JDE instance) throws IOException {
        this.instance = instance;

        registerFileTypes(new GenericReader(SyntaxConstants.SYNTAX_STYLE_NONE, "*"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH, "bat"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_CSV, "csv"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_DOCKERFILE, "dockerfile"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_ENV, "env"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_INI, "ini"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_JAVA, "java"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_JSON, "json"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_KOTLIN, "kt", "kts"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_MARKDOWN, "md"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_POWERSHELL, "ps1"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE, "properties"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_SQL, "sql"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_XML, "xml"),
                new GenericReader(SyntaxConstants.SYNTAX_STYLE_YAML, "yml", "yaml"));

        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
        super(textArea); // Can't reference class textArea before super is called

        this.textArea = textArea;
        loadSettings();

        // Custom keybinds
        this.textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        this.textArea.getActionMap().put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // If the textArea is not editable, it is not a file that can be saved either
                if (openFile == null || !textArea.isEditable()) {
                    return;
                }

                try {
                    Files.writeString(openFile.toPath(), textArea.getText());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(JEditor.this, "Unable to save " + openFile.getName() + ": " + ex.getMessage(),
                            "Save failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        CompletionProvider provider = createCompletionProvider();
        AutoCompletion autoComplete = new AutoCompletion(provider);
        autoComplete.setAutoActivationEnabled(true);
        autoComplete.setAutoActivationDelay(200);
        autoComplete.install(this.textArea);
    }

    public void setEditorFile(File directory, File file) throws IOException {
        String[] split = file.getName().split("\\.");
        ReadableType readableType = READABLE_TYPES.getOrDefault(split[split.length - 1].toLowerCase(),  READABLE_TYPES.get("*"));

        try {
            this.textArea.setText(readableType.readFile(file));
            this.textArea.setCaretPosition(0);
            this.textArea.setSyntaxEditingStyle(readableType.getSyntaxHighlighter());
            this.textArea.setEditable(true);
        } finally {
            this.openFile = file;
            this.instance.getStorageManager().addWorkspaceState(directory.getName(), new WorkspaceState(file.getAbsolutePath().replace(directory.getAbsolutePath(), "")));
        }
    }

    private void loadSettings() throws IOException {
        final Settings settings = this.instance.getSettings();

        this.textArea.setCodeFoldingEnabled(true);
        this.textArea.setInsertPairedCharacters(true);
        this.textArea.setCloseCurlyBraces(true);
        this.textArea.setMarginLineEnabled(settings.marginLinePosition != 0);
        this.textArea.setMarginLinePosition(settings.marginLinePosition);
        this.textArea.setCodeFoldingEnabled(true);
        this.textArea.setWhitespaceVisible(settings.visibleWhitespace);

        File zip = new File("english_dic.zip");
        SpellingParser parser = SpellingParser.createEnglishSpellingParser(zip, true);
        this.textArea.addParser(parser);

        // RSyntaxTextArea ignores the LaF. It has its own theme system
        Theme.load(getClass().getResourceAsStream(
                "/org/fife/ui/rsyntaxtextarea/themes/dark.xml")).apply(this.textArea);

        // Apply this after the theme otherwise it is overridden
        this.textArea.setFont(this.textArea.getFont().deriveFont((float) this.instance.getSettings().zoomLevel));
    }

    private CompletionProvider createCompletionProvider() {
        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        provider.setAutoActivationRules(true, ".");

        provider.addCompletion(new BasicCompletion(provider, "abstract"));
        provider.addCompletion(new BasicCompletion(provider, "assert"));
        provider.addCompletion(new BasicCompletion(provider, "break"));
        provider.addCompletion(new BasicCompletion(provider, "case"));
        provider.addCompletion(new BasicCompletion(provider, "transient"));
        provider.addCompletion(new BasicCompletion(provider, "try"));
        provider.addCompletion(new BasicCompletion(provider, "void"));
        provider.addCompletion(new BasicCompletion(provider, "volatile"));
        provider.addCompletion(new BasicCompletion(provider, "while"));

        provider.addCompletion(new ShorthandCompletion(provider, "sout", "System.out.println();", "System.out.println();"));
        provider.addCompletion(new ShorthandCompletion(provider, "serr", "System.err.println();", "System.err.println();"));

        return provider;
    }

    private static void registerFileTypes(ReadableType... readableTypes) {
        for (ReadableType readableType : readableTypes) {
            registerFileType(readableType);
        }
    }

    private static void registerFileType(ReadableType readableType) {
        for (String fileType : readableType.recognisedFileTypes()) {
            READABLE_TYPES.put(fileType, readableType);
        }
    }

    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e) {
        if (!e.isControlDown()) {
            super.processMouseWheelEvent(e);
            return;
        }
        int rot = e.getWheelRotation();
        if (rot == 0) return;
        Font font = this.textArea.getFont();
        int size = Math.max(MIN_FONT_SIZE, Math.min(MAX_FONT_SIZE, font.getSize() - rot));
        this.textArea.setFont(font.deriveFont((float) size));

        Settings settings = this.instance.getSettings();
        settings.zoomLevel = size;
        this.instance.getStorageManager().setSettings(settings);
    }
}
