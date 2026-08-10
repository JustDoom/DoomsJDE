package com.imjustdoom.doomsjde;

import com.imjustdoom.doomsjde.filetypes.GenericReader;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class Editor extends JFrame {
    private static final Comparator<File> TREE_ORDER = Comparator
            .comparing(File::isFile)
            .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER);

    public Editor(File directory) throws IOException {
        File zip = new File("english_dic.zip");
        SpellingParser parser = SpellingParser.createEnglishSpellingParser(zip, true);

        JPanel cp = new JPanel(new BorderLayout());

        DefaultMutableTreeNode root = buildFileTree(directory);

        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setFont(new Font("JetBrainsMono NFM", Font.PLAIN, 24));
        textArea.setCodeFoldingEnabled(true);
        textArea.setInsertPairedCharacters(true);
        textArea.setCloseCurlyBraces(true);
        textArea.setMarginLineEnabled(true);
        textArea.setMarginLinePosition(80);
        textArea.setCodeFoldingEnabled(true);

        textArea.setEditable(false);

        textArea.addParser(parser);

        // RSyntaxTextArea ignores the LaF. It has its own theme system
        Theme.load(getClass().getResourceAsStream(
                "/org/fife/ui/rsyntaxtextarea/themes/dark.xml")).apply(textArea);

        RTextScrollPane sp = new RTextScrollPane(textArea);

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
                File file = (File) node.getUserObject();
                try {
                    textArea.setText(new GenericReader().readFile(file));
                    textArea.setCaretPosition(0);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        fileTree.setRootVisible(true);
        JScrollPane treeScrollPane = new JScrollPane(fileTree);
        treeScrollPane.setPreferredSize(new Dimension(200, 0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, sp);
        splitPane.setResizeWeight(0.2);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);

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
}
