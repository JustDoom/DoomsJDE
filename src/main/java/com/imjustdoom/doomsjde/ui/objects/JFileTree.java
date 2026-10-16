package com.imjustdoom.doomsjde.ui.objects;

import com.imjustdoom.doomsjde.ui.ProjectWindow;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Comparator;

public class JFileTree extends JScrollPane {
    private static final Comparator<File> TREE_ORDER = Comparator
            .comparing(File::isFile)
            .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER);

    private final JPopupMenu popupMenu;
    private final JTree fileTree;

    private final ProjectWindow projectWindow;
    private final File directory;

    public JFileTree(ProjectWindow projectWindow, File directory) {
        this.popupMenu = new JPopupMenu();
        this.fileTree = new JTree() {
            @Override
            public Point getPopupLocation(MouseEvent e) {
                if (e == null) return null;
                TreePath path = getClosestPathForLocation(e.getX(), e.getY());
                if (path == null) return null;
                buildPopupMenu((DefaultMutableTreeNode) path.getLastPathComponent());
                return e.getPoint();
            }

        };
        this.projectWindow = projectWindow;
        this.directory = directory;

        setViewportView(this.fileTree); // Sets the fileTree inside the JScrollPane
        this.fileTree.setComponentPopupMenu(this.popupMenu);

        this.fileTree.setCellRenderer(new DefaultTreeCellRenderer() {
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
        this.fileTree.addTreeSelectionListener(e -> {
            if (!e.isAddedPath()) {
                return;
            }

            TreePath path = this.fileTree.getSelectionPath();
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

        this.fileTree.setRootVisible(true);

        setPreferredSize(new Dimension(200, 0));
    }

    // Loads and builds the file tree
    public void init() {
        this.fileTree.setModel(new DefaultTreeModel(buildFileTree(this.directory)));
    }

    private DefaultMutableTreeNode buildFileTree(File directory) {
        File[] children = directory.listFiles();
        if (children == null || children.length == 0) {
            return new DefaultMutableTreeNode(directory);
        }

        Arrays.sort(children, TREE_ORDER);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(directory);
        for (File child : children) {
            if (child.isDirectory()) {
                root.add(buildFileTree(child));
            } else {
                root.add(new DefaultMutableTreeNode(child));
            }
        }
        return root;
    }

    private void buildPopupMenu(DefaultMutableTreeNode node) {
        this.popupMenu.removeAll();

        File object = (File) node.getUserObject();

        if (object.isDirectory()) {
            this.popupMenu.add(item("Create file", _ -> createFile(node, object)));
            this.popupMenu.add(item("Create Directory", _ -> createDirectory(node, object)));
        }
        this.popupMenu.add(item("Delete", e -> delete(node, object)));
    }

    private JMenuItem item(String text, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(listener);
        return menuItem;
    }

    private void createFile(DefaultMutableTreeNode node, File directory) {
        String name = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(this), "File name");
        if (name == null || name.isBlank()) {
            return;
        }

        final Path relative;
        try {
            relative = Path.of(name);
        } catch (InvalidPathException exception) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Invalid path: " + name);
            return;
        }

        Path target = directory.toPath().resolve(relative).normalize();

        try {
            if (target.getParent() != null) {
                Files.createDirectories(target.getParent());
            }
            Files.createFile(target);
        } catch (FileAlreadyExistsException exception) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "File already exists");
            return;
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "An error occurred :(");
            exception.printStackTrace();
            return;
        }

        DefaultTreeModel model = (DefaultTreeModel) this.fileTree.getModel();

        DefaultMutableTreeNode parentNode = node;
        Path current = directory.toPath();
        for (int i = 0; i < relative.getNameCount() - 1; i++) {
            current = current.resolve(relative.getName(i));
            parentNode = findOrCreateChild(model, parentNode, current.toFile());
        }

        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(target.toFile());
        insertSorted(model, parentNode, fileNode);
    }

    private void createDirectory(DefaultMutableTreeNode node, File directory) {
        String name = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(this), "Directory name");
        if (name == null || name.isBlank()) {
            return;
        }

        final Path relative;
        try {
            relative = Path.of(name);
        } catch (InvalidPathException exception) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Invalid path: " + name);
            return;
        }

        Path target = directory.toPath().resolve(relative).normalize();

        try {
            if (Files.exists(target)) {
                throw new FileAlreadyExistsException("The directory already exists");
            }
            Files.createDirectories(target);
        } catch (FileAlreadyExistsException exception) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Directory already exists");
            return;
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "An error occurred :(");
            exception.printStackTrace();
            return;
        }

        DefaultTreeModel model = (DefaultTreeModel) this.fileTree.getModel();

        DefaultMutableTreeNode parentNode = node;
        Path current = directory.toPath();
        for (int i = 0; i < relative.getNameCount() - 1; i++) {
            current = current.resolve(relative.getName(i));
            parentNode = findOrCreateChild(model, parentNode, current.toFile());
        }

        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(target.toFile());
        insertSorted(model, parentNode, fileNode);
    }

    public void delete(DefaultMutableTreeNode node, File file) {
        int response = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this), "Are you sure you want to delete this?", "Confirm Delete",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (response != JOptionPane.OK_OPTION) return;

        if (file.isDirectory()) {
            deleteRecursively(file);
        }

        file.delete();
        DefaultTreeModel model = (DefaultTreeModel) this.fileTree.getModel();
        model.removeNodeFromParent(node);
    }

    private void deleteRecursively(File file) {
        for (File child : file.listFiles()) {
            if (child.isDirectory()) {
                if (child.listFiles().length == 0) {
                    child.delete();
                    continue;
                }
                deleteRecursively(child);
            }

            child.delete();
        }
    }

    private DefaultMutableTreeNode findOrCreateChild(DefaultTreeModel model, DefaultMutableTreeNode parent, File child) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode candidate = (DefaultMutableTreeNode) parent.getChildAt(i);
            if (candidate.getUserObject() instanceof File file && file.getName().equals(child.getName())) {
                return candidate;
            }
        }

        DefaultMutableTreeNode created = new DefaultMutableTreeNode(child);
        insertSorted(model, parent, created);
        return created;
    }

    private void insertSorted(DefaultTreeModel model, DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        File file = (File) child.getUserObject();
        int index = 0;
        while (index < parent.getChildCount()
                && TREE_ORDER.compare((File) ((DefaultMutableTreeNode) parent.getChildAt(index)).getUserObject(), file) <= 0) {
            index++;
        }
        model.insertNodeInto(child, parent, index);
    }
}
