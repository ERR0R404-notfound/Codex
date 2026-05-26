package main.java.com.codex.ui;

// ─────────────────────────────────────────────────────────────────────────────
//  IMPORTS
// ─────────────────────────────────────────────────────────────────────────────
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 
// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  MainFrame — the root application window for Codex.
//  Hosts the Toolbar, SectionTree (left panel), NoteEditor (right panel),
//  and StatusBar. Uses a BorderLayout as the top-level container.
// ─────────────────────────────────────────────────────────────────────────────
 
public class MainFrame extends JFrame {
 
    // ─────────────────────────────────────────────────────────────────────────
    //  ATTRIBUTES / FIELDS
    // ─────────────────────────────────────────────────────────────────────────
 
    private static final String APP_TITLE       = "Codex";
    private static final int    WINDOW_WIDTH     = 1100;
    private static final int    WINDOW_HEIGHT    = 700;
    private static final int    MIN_WIDTH        = 800;
    private static final int    MIN_HEIGHT       = 500;
    private static final int    LEFT_PANEL_WIDTH = 250;
 
    // Core UI panels
    private Toolbar     toolbar;
    private SectionTree sectionTree;
    private NoteEditor  noteEditor;
    private StatusBar   statusBar;
 
    // Layout containers
    private JSplitPane  splitPane;
    private JPanel      leftPanel;
    private JPanel      rightPanel;
 
    // ─────────────────────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────────────────
 
    public MainFrame() {
        initComponents();
        initLayout();
        initListeners();
        applyWindowSettings();
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    //  COMPONENT INITIALIZATION
    //  Instantiate all child components before assembling the layout.
    // ─────────────────────────────────────────────────────────────────────────
 
    private void initComponents() {
        toolbar     = new Toolbar();
        sectionTree = new SectionTree();
        noteEditor  = new NoteEditor();
        statusBar   = new StatusBar();
 
        leftPanel  = new JPanel(new BorderLayout());
        rightPanel = new JPanel(new BorderLayout());
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    //  LAYOUT / ASSEMBLY
    //  Assemble the panels and attach them to the JFrame.
    //
    //  Structure:
    //  ┌──────────────────────────────────────────────┐
    //  │  Toolbar                                     │  ← NORTH
    //  ├──────────────┬───────────────────────────────┤
    //  │  SectionTree │  NoteEditor                   │  ← CENTER (SplitPane)
    //  ├──────────────┴───────────────────────────────┤
    //  │  StatusBar                                   │  ← SOUTH
    //  └──────────────────────────────────────────────┘
    // ─────────────────────────────────────────────────────────────────────────
 
    private void initLayout() {
        // --- Left panel: section tree only
        leftPanel.add(sectionTree, BorderLayout.CENTER);
        leftPanel.setMinimumSize(new Dimension(200, 0));
        leftPanel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, 0));
 
        // --- Right panel: note editor only
        rightPanel.add(noteEditor, BorderLayout.CENTER);
        rightPanel.setMinimumSize(new Dimension(400, 0));
 
        // --- Split pane: left | right
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(LEFT_PANEL_WIDTH);
        splitPane.setDividerSize(1);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);
 
        // --- Assemble main frame
        setLayout(new BorderLayout());
        add(toolbar,   BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    //  EVENT LISTENERS
    //  Window-level events (close, resize, focus).
    // ─────────────────────────────────────────────────────────────────────────
 
    private void initListeners() {
        // Prompt user before closing if there are unsaved changes
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleAppClose();
            }
        });
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    //  WINDOW SETTINGS
    //  Configure JFrame properties (size, title, centering, icon).
    // ─────────────────────────────────────────────────────────────────────────
 
    private void applyWindowSettings() {
        setTitle(APP_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setLocationRelativeTo(null);    // center on screen
        setVisible(true);
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Handles application close — prompts user if there are unsaved changes.
     * For now, exits directly; hook in unsaved-change detection later.
     */
    private void handleAppClose() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit Codex?",
            "Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
 
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
 
    /**
     * Exposes the StatusBar so child components can push status updates.
     */
    public StatusBar getStatusBar() {
        return statusBar;
    }
 
    /**
     * Exposes the NoteEditor so the SectionTree can load notes into it.
     */
    public NoteEditor getNoteEditor() {
        return noteEditor;
    }
 
}