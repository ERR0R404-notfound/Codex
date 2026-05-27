package main.java.com.codex.ui;
// ─────────────────────────────────────────────────────────────────────────────
//  IMPORTS
// ─────────────────────────────────────────────────────────────────────────────

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  SectionTree — the left panel of Codex.
//  Displays a nested JTree of sections and notes.
//  Supports expand/collapse, selection, and right-click context menu.
// ─────────────────────────────────────────────────────────────────────────────

public class SectionTree extends JPanel {

    // ─────────────────────────────────────────────────────────────────────────
    //  ATTRIBUTES / FIELDS
    // ─────────────────────────────────────────────────────────────────────────

    private static final Color  PANEL_BACKGROUND    = new Color(248, 248, 248);
    private static final Color  TREE_BACKGROUND     = new Color(248, 248, 248);
    private static final Color  SELECTION_COLOR     = new Color(225, 235, 255);
    private static final Color  SELECTION_BORDER    = new Color(180, 210, 255);
    private static final Color  TEXT_COLOR          = new Color(40, 40, 40);
    private static final Color  SECTION_LABEL_COLOR = new Color(130, 130, 130);
    private static final Font   TREE_FONT           = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font   SECTION_FONT        = new Font("Segoe UI", Font.BOLD, 11);

    // Tree components
    private JTree               tree;
    private DefaultTreeModel    treeModel;
    private DefaultMutableTreeNode rootNode;

    // Context menu
    private JPopupMenu          contextMenu;
    private JMenuItem           menuAddSection;
    private JMenuItem           menuAddNote;
    private JMenuItem           menuRename;
    private JMenuItem           menuDelete;

    // Header label
    private JLabel              lblHeader;

    // Scroll container
    private JScrollPane         scrollPane;

    // ─────────────────────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────────────────

    public SectionTree() {
        initComponents();
        initLayout();
        initListeners();
        loadSampleData();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  COMPONENT INITIALIZATION
    // ─────────────────────────────────────────────────────────────────────────

    private void initComponents() {
        // --- Header label
        lblHeader = new JLabel("SECTIONS");
        lblHeader.setFont(SECTION_FONT);
        lblHeader.setForeground(SECTION_LABEL_COLOR);
        lblHeader.setBorder(BorderFactory.createEmptyBorder(14, 14, 6, 14));

        // --- Tree model with invisible root
        rootNode  = new DefaultMutableTreeNode("root");
        treeModel = new DefaultTreeModel(rootNode);

        // --- JTree setup
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setBackground(TREE_BACKGROUND);
        tree.setFont(TREE_FONT);
        tree.setRowHeight(28);
        tree.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        tree.setCellRenderer(new CodexTreeCellRenderer());

        // --- Scroll pane wrapping the tree
        scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(null);
        scrollPane.setBackground(TREE_BACKGROUND);
        scrollPane.getViewport().setBackground(TREE_BACKGROUND);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // --- Context menu
        contextMenu  = new JPopupMenu();
        menuAddSection = new JMenuItem("Add Section");
        menuAddNote    = new JMenuItem("Add Note");
        menuRename     = new JMenuItem("Rename");
        menuDelete     = new JMenuItem("Delete");

        menuDelete.setForeground(new Color(200, 60, 60));

        contextMenu.add(menuAddSection);
        contextMenu.add(menuAddNote);
        contextMenu.addSeparator();
        contextMenu.add(menuRename);
        contextMenu.addSeparator();
        contextMenu.add(menuDelete);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  LAYOUT / ASSEMBLY
    // ─────────────────────────────────────────────────────────────────────────

    private void initLayout() {
        setLayout(new BorderLayout());
        setBackground(PANEL_BACKGROUND);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        add(lblHeader,  BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  EVENT LISTENERS
    // ─────────────────────────────────────────────────────────────────────────

    private void initListeners() {
        // --- Right-click to show context menu
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tree.getRowForLocation(e.getX(), e.getY());
                    if (row != -1) tree.setSelectionRow(row);
                    contextMenu.show(tree, e.getX(), e.getY());
                }
            }
        });

        // --- Tree selection: load note into editor
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selected = getSelectedNode();
            if (selected != null) {
                onNodeSelected(selected);
            }
        });

        // --- Context menu actions
        menuAddSection.addActionListener(e -> onAddSection());
        menuAddNote.addActionListener(e -> onAddNote());
        menuRename.addActionListener(e -> onRename());
        menuDelete.addActionListener(e -> onDelete());
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  SAMPLE DATA
    //  Populates the tree with placeholder data for UI testing.
    //  Replace with DB-loaded data once DatabaseManager is ready.
    // ─────────────────────────────────────────────────────────────────────────

    private void loadSampleData() {
        DefaultMutableTreeNode school   = new DefaultMutableTreeNode("📁  School");
        DefaultMutableTreeNode its132p  = new DefaultMutableTreeNode("📁  ITS132P - Data Warehousing");
        DefaultMutableTreeNode asm      = new DefaultMutableTreeNode("📁  Assembly Language");
        DefaultMutableTreeNode personal = new DefaultMutableTreeNode("📁  Personal");

        its132p.add(new DefaultMutableTreeNode("📄  Lab 1.1 Notes"));
        its132p.add(new DefaultMutableTreeNode("📄  ETL Concepts"));
        asm.add(new DefaultMutableTreeNode("📄  Win32 GUI Notes"));
        asm.add(new DefaultMutableTreeNode("📄  MASM Macros"));
        personal.add(new DefaultMutableTreeNode("📄  Ideas"));

        school.add(its132p);
        school.add(asm);

        rootNode.add(school);
        rootNode.add(personal);

        treeModel.reload();

        // Expand all nodes by default
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the currently selected tree node, or null if none.
     */
    public DefaultMutableTreeNode getSelectedNode() {
        TreePath path = tree.getSelectionPath();
        if (path == null) return null;
        return (DefaultMutableTreeNode) path.getLastPathComponent();
    }

    /**
     * Called when a tree node is selected.
     * Will load note content into NoteEditor — wired in later.
     */
    private void onNodeSelected(DefaultMutableTreeNode node) {
        // TODO: if node is a note leaf, load note into NoteEditor
        System.out.println("[SectionTree] Selected: " + node.getUserObject());
    }

    /**
     * Called when "Add Section" is chosen from the context menu.
     */
    private void onAddSection() {
        // TODO: open NewSectionDialog
        System.out.println("[SectionTree] Add Section");
    }

    /**
     * Called when "Add Note" is chosen from the context menu.
     */
    private void onAddNote() {
        // TODO: open NewNoteDialog
        System.out.println("[SectionTree] Add Note");
    }

    /**
     * Called when "Rename" is chosen from the context menu.
     */
    private void onRename() {
        DefaultMutableTreeNode node = getSelectedNode();
        if (node == null) return;

        String newName = JOptionPane.showInputDialog(
            this,
            "Rename:",
            node.getUserObject().toString()
        );

        if (newName != null && !newName.isBlank()) {
            node.setUserObject(newName);
            treeModel.nodeChanged(node);
        }
    }

    /**
     * Called when "Delete" is chosen from the context menu.
     */
    private void onDelete() {
        DefaultMutableTreeNode node = getSelectedNode();
        if (node == null) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete \"" + node.getUserObject() + "\"? This cannot be undone.",
            "Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            treeModel.removeNodeFromParent(node);
        }
    }

    /**
     * Adds a new section node under the currently selected node (or root).
     */
    public void addSectionNode(String name) {
        DefaultMutableTreeNode parent = getSelectedNode();
        if (parent == null) parent = rootNode;

        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("📁  " + name);
        treeModel.insertNodeInto(newNode, parent, parent.getChildCount());
        tree.scrollPathToVisible(new TreePath(newNode.getPath()));
    }

    /**
     * Adds a new note node under the currently selected node (or root).
     */
    public void addNoteNode(String title) {
        DefaultMutableTreeNode parent = getSelectedNode();
        if (parent == null) parent = rootNode;

        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("📄  " + title);
        treeModel.insertNodeInto(newNode, parent, parent.getChildCount());
        tree.scrollPathToVisible(new TreePath(newNode.getPath()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  INNER CLASS — Custom Tree Cell Renderer
//  Gives each tree node a clean, minimal look with selection highlight.
    // ─────────────────────────────────────────────────────────────────────────

    private static class CodexTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(
                JTree tree, Object value, boolean selected,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(TEXT_COLOR);
            setBorderSelectionColor(null);
            setLeafIcon(null);
            setOpenIcon(null);
            setClosedIcon(null);

            if (selected) {
                setBackground(SELECTION_COLOR);
                setOpaque(true);
            } else {
                setOpaque(false);
            }

            return this;
        }
    }

}