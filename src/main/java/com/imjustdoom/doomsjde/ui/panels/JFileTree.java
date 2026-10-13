package com.imjustdoom.doomsjde.ui.panels;

import com.imjustdoom.doomsjde.ui.ProjectWindow;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class JFileTree extends JScrollPane {
    private static final Comparator<File> TREE_ORDER = Comparator
            .comparing(File::isFile)
            .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER);

    private final JTree fileTree;

    private final ProjectWindow projectWindow;
    private final File directory;

    public JFileTree(ProjectWindow projectWindow, File directory) {
        JTree fileTree = new JTree();
        super(fileTree);
        this.fileTree = fileTree;
        this.projectWindow = projectWindow;
        this.directory = directory;

        fileTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode node) {
                    Object obj = node.getUserObject();
                    if (obj instanceof File file) {
                        setText((file).getName());
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
            if (path == null) {
                return;
            }

            // Convert the path to the actual TreeNode
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (!(node.getUserObject() instanceof File file) || file.isDirectory()) {
                return;
            }

            this.projectWindow.setEditorFile(this.directory, file);
        });

        fileTree.setRootVisible(true);

        setPreferredSize(new Dimension(200, 0));
    }

    // Loads and builds the file tree
    public void init() {
        this.fileTree.setModel(new DefaultTreeModel(buildFileTree(this.directory)));
    }

    private DefaultMutableTreeNode buildFileTree(File directory) {
        return buildFileTree(directory, directory.getName());
    }

    private DefaultMutableTreeNode buildFileTree(File directory, String path) {
        File[] children = directory.listFiles();
        if (children == null || children.length == 0) {
            return new DefaultMutableTreeNode(path);
        }

        Arrays.sort(children, TREE_ORDER);

        if (children.length == 1 && children[0].isDirectory()) {
            File child = children[0];
            return buildFileTree(child, path + "/" + child.getName());
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(path);
        for (File child : children) {
            if (child.isDirectory()) {
                root.add(buildFileTree(child));
            } else {
                root.add(new DefaultMutableTreeNode(child));
            }
        }
        return root;
    }
}
