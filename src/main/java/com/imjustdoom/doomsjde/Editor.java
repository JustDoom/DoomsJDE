package com.imjustdoom.doomsjde;

import com.imjustdoom.doomsjde.filetypes.*;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Editor extends JFrame {
    private static final Comparator<File> TREE_ORDER = Comparator
            .comparing(File::isFile)
            .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER);

    private static final Map<String, ReadableType> READABLE_TYPES = new HashMap<>();

    public Editor(File directory) throws IOException {
        registerFileTypes(new BatReader(), new CSVReader(), new DockerfileReader(), new EnvReader(), new IniReader(),
                new JavaReader(), new JsonReader(), new KotlinReader(), new MarkdownReader(), new PowershellReader(),
                new PropertiesReader(), new SQLReader(), new XMLReader(), new YAMLReader());
        ReadableType genericReader = new GenericReader();

        File zip = new File("english_dic.zip");
        SpellingParser parser = SpellingParser.createEnglishSpellingParser(zip, true);

        JPanel cp = new JPanel(new BorderLayout());

        DefaultMutableTreeNode root = buildFileTree(directory);

        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
        textArea.setFont(new Font("JetBrainsMono NFM", Font.PLAIN, 24));
        textArea.setCodeFoldingEnabled(true);
        textArea.setInsertPairedCharacters(true);
        textArea.setCloseCurlyBraces(true);
        textArea.setMarginLineEnabled(true);
        textArea.setMarginLinePosition(80);
        textArea.setCodeFoldingEnabled(true);

        textArea.addParser(parser);

        // RSyntaxTextArea ignores the LaF. It has its own theme system
        Theme.load(getClass().getResourceAsStream(
                "/org/fife/ui/rsyntaxtextarea/themes/dark.xml")).apply(textArea);

        RTextScrollPane sp = new RTextScrollPane(textArea);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, sp);

        File[] openFile = new File[1];
        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        textArea.getActionMap().put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (openFile[0] == null) {
                    return;
                }

                try {
                    Files.writeString(openFile[0].toPath(), textArea.getText());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(Editor.this, "Unable to save " + openFile[0].getName() + ": " + ex.getMessage(),
                            "Save failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JTree fileTree = new JTree(new DefaultTreeModel(root));
        fileTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode) {
                    Object obj = ((DefaultMutableTreeNode) value).getUserObject();
                    if (obj instanceof File) {
                        setText(((File) obj).getName());
                    }
                }
                return this;
            }
        });
        fileTree.addTreeSelectionListener(e -> {
            if (!e.isAddedPath()) {
                return;
            }

            TreePath path = fileTree.getSelectionPath();
            if (path != null) {
                // Convert the path to the actual TreeNode
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (!(node.getUserObject() instanceof File file) || file.isDirectory()) {
                    return;
                }

                String[] split = file.getName().split("\\.");
                ReadableType readableType = READABLE_TYPES.getOrDefault(split[split.length - 1].toLowerCase(), genericReader);

                try {
                    splitPane.setRightComponent(sp);
                    textArea.setText(readableType.readFile(file));
                    textArea.setCaretPosition(0);
                    textArea.setSyntaxEditingStyle(readableType.getSyntaxHighlighter());
                    textArea.setEditable(true);
                    openFile[0] = file;
                } catch (IOException ex) {
                    // Unable to read the file type :(
                    final String text = String.format("Unable to read the file '%s', it will need a Reader written for it or will remain unsupported", file.getName());
                    // HTML so the label wraps to the width it's given
                    JLabel label = new JLabel("<html><div align='center'>" + text + "</div></html>", SwingConstants.CENTER);
                    splitPane.setRightComponent(label);
                    textArea.setEditable(false);
                    openFile[0] = null;
                    System.err.println(text);
                }
            }
        });

        fileTree.setRootVisible(true);
        JScrollPane treeScrollPane = new JScrollPane(fileTree);
        treeScrollPane.setPreferredSize(new Dimension(200, 0));

        splitPane.setResizeWeight(0.2);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);

        splitPane.setLeftComponent(treeScrollPane);

        cp.add(splitPane, BorderLayout.CENTER);

        CompletionProvider provider = createCompletionProvider();
        AutoCompletion ac = new AutoCompletion(provider);
        ac.install(textArea);

        setContentPane(cp);
        setTitle("Dooms JDE");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private DefaultMutableTreeNode buildFileTree(File directory) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(directory.getName());
        File[] children = directory.listFiles();
        if (children == null) {
            return root;
        }

        Arrays.sort(children, TREE_ORDER);
        for (File child : children) {
            root.add(child.isDirectory() ? buildFileTree(child) : new DefaultMutableTreeNode(child));
        }

        return root;
    }

    private CompletionProvider createCompletionProvider() {
        DefaultCompletionProvider provider = new DefaultCompletionProvider();

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
}
