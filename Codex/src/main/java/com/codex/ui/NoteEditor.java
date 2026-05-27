package main.java.com.codex.ui;
// ─────────────────────────────────────────────────────────────────────────────
//  IMPORTS
// ─────────────────────────────────────────────────────────────────────────────

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  NoteEditor — the right panel of Codex.
//  Contains filter tabs [All | Pinned | Tagged | Recent] at the top,
//  a note title field, and a large text editor area below.
// ─────────────────────────────────────────────────────────────────────────────

public class NoteEditor extends JPanel {

    // ─────────────────────────────────────────────────────────────────────────
    //  ATTRIBUTES / FIELDS
    // ─────────────────────────────────────────────────────────────────────────

    private static final Color  PANEL_BACKGROUND   = Color.WHITE;
    private static final Color  FILTER_BAR_BG      = new Color(250, 250, 250);
    private static final Color  ACTIVE_TAB_COLOR   = new Color(60, 60, 60);
    private static final Color  INACTIVE_TAB_COLOR = new Color(160, 160, 160);
    private static final Color  ACTIVE_TAB_BORDER  = new Color(60, 60, 60);
    private static final Color  TITLE_COLOR        = new Color(30, 30, 30);
    private static final Color  EDITOR_COLOR       = new Color(50, 50, 50);
    private static final Color  DIVIDER_COLOR      = new Color(220, 220, 220);
    private static final Color  PLACEHOLDER_COLOR  = new Color(190, 190, 190);

    private static final Font   FILTER_FONT        = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font   TITLE_FONT         = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font   EDITOR_FONT        = new Font("Segoe UI", Font.PLAIN, 14);

    private static final String TITLE_PLACEHOLDER  = "Note title...";
    private static final String EDITOR_PLACEHOLDER = "Start writing your note here...";

    // Filter tab labels
    private static final String[] FILTER_LABELS = {"All", "Pinned", "Tagged", "Recent"};

    // Filter tab bar
    private JPanel      filterBar;
    private JButton[]   filterTabs;
    private int         activeFilterIndex = 0;

    // Note title
    private JTextField  titleField;

    // Note body editor
    private JTextArea   editorArea;
    private JScrollPane editorScroll;

    // Content wrapper
    private JPanel      contentPanel;

    // Reference to StatusBar for live word count updates
    private StatusBar   statusBar;

    // ─────────────────────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────────────────

    public NoteEditor() {
        initComponents();
        initLayout();
        initListeners();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  COMPONENT INITIALIZATION
    // ─────────────────────────────────────────────────────────────────────────

    private void initComponents() {
        // --- Filter tab bar
        filterBar  = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        filterBar.setBackground(FILTER_BAR_BG);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER_COLOR),
            BorderFactory.createEmptyBorder(10, 16, 0, 16)
        ));

        filterTabs = new JButton[FILTER_LABELS.length];
        for (int i = 0; i < FILTER_LABELS.length; i++) {
            filterTabs[i] = createFilterTab(FILTER_LABELS[i], i);
            filterBar.add(filterTabs[i]);
        }

        setActiveTab(0);

        // --- Note title field
        titleField = new JTextField(TITLE_PLACEHOLDER);
        titleField.setFont(TITLE_FONT);
        titleField.setForeground(PLACEHOLDER_COLOR);
        titleField.setBorder(BorderFactory.createEmptyBorder(24, 32, 8, 32));
        titleField.setBackground(PANEL_BACKGROUND);

        // --- Note body editor
        editorArea = new JTextArea(EDITOR_PLACEHOLDER);
        editorArea.setFont(EDITOR_FONT);
        editorArea.setForeground(PLACEHOLDER_COLOR);
        editorArea.setBackground(PANEL_BACKGROUND);
        editorArea.setLineWrap(true);
        editorArea.setWrapStyleWord(true);
        editorArea.setBorder(BorderFactory.createEmptyBorder(8, 32, 32, 32));
        editorArea.setCaretColor(new Color(60, 60, 60));

        // --- Scroll pane for editor
        editorScroll = new JScrollPane(editorArea);
        editorScroll.setBorder(null);
        editorScroll.setBackground(PANEL_BACKGROUND);
        editorScroll.getViewport().setBackground(PANEL_BACKGROUND);
        editorScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        editorScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // --- Content panel wrapping title + editor
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(PANEL_BACKGROUND);
        contentPanel.add(titleField,  BorderLayout.NORTH);
        contentPanel.add(editorScroll, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  LAYOUT / ASSEMBLY
    //
    //  ┌───────────────────────────────────────────┐
    //  │  [All] [Pinned] [Tagged] [Recent]          │  ← filterBar (NORTH)
    //  ├───────────────────────────────────────────┤
    //  │  Note Title                               │
    //  │  ─────────────────────────────────────    │  ← contentPanel (CENTER)
    //  │  Note body text area...                   │
    //  └───────────────────────────────────────────┘
    // ─────────────────────────────────────────────────────────────────────────

    private void initLayout() {
        setLayout(new BorderLayout());
        setBackground(PANEL_BACKGROUND);

        add(filterBar,    BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  EVENT LISTENERS
    // ─────────────────────────────────────────────────────────────────────────

    private void initListeners() {
        // --- Title field placeholder behavior
        titleField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (titleField.getText().equals(TITLE_PLACEHOLDER)) {
                    titleField.setText("");
                    titleField.setForeground(TITLE_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (titleField.getText().isBlank()) {
                    titleField.setText(TITLE_PLACEHOLDER);
                    titleField.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });

        // --- Editor area placeholder behavior
        editorArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (editorArea.getText().equals(EDITOR_PLACEHOLDER)) {
                    editorArea.setText("");
                    editorArea.setForeground(EDITOR_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (editorArea.getText().isBlank()) {
                    editorArea.setText(EDITOR_PLACEHOLDER);
                    editorArea.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });

        // --- Live word/char count pushed to StatusBar
        editorArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { updateWordCount(); }
            @Override public void removeUpdate(DocumentEvent e)  { updateWordCount(); }
            @Override public void changedUpdate(DocumentEvent e) { updateWordCount(); }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Factory method — creates a styled filter tab button.
     */
    private JButton createFilterTab(String label, int index) {
        JButton tab = new JButton(label);
        tab.setFont(FILTER_FONT);
        tab.setFocusPainted(false);
        tab.setBorderPainted(false);
        tab.setContentAreaFilled(false);
        tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tab.setMargin(new Insets(6, 12, 8, 12));

        tab.addActionListener(e -> setActiveTab(index));

        return tab;
    }

    /**
     * Highlights the active filter tab and deactivates others.
     */
    private void setActiveTab(int index) {
        activeFilterIndex = index;

        for (int i = 0; i < filterTabs.length; i++) {
            if (i == index) {
                filterTabs[i].setForeground(ACTIVE_TAB_COLOR);
                filterTabs[i].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACTIVE_TAB_BORDER));
            } else {
                filterTabs[i].setForeground(INACTIVE_TAB_COLOR);
                filterTabs[i].setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
            }
        }

        onFilterChanged(FILTER_LABELS[index]);
    }

    /**
     * Called when a filter tab is selected.
     * Will filter the note list — wired to DB layer later.
     */
    private void onFilterChanged(String filter) {
        // TODO: filter notes by All / Pinned / Tagged / Recent
        System.out.println("[NoteEditor] Filter: " + filter);
    }

    /**
     * Calculates word and character count and pushes it to the StatusBar.
     */
    private void updateWordCount() {
        if (statusBar == null) return;

        String text = editorArea.getText();
        if (text.equals(EDITOR_PLACEHOLDER) || text.isBlank()) {
            statusBar.setWordCount(0, 0);
            return;
        }

        int chars = text.length();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        statusBar.setWordCount(words, chars);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUBLIC METHODS
    //  Called by SectionTree or dialogs to load/save note content.
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Loads a note into the editor (title + content).
     */
    public void loadNote(String title, String content) {
        titleField.setText(title);
        titleField.setForeground(TITLE_COLOR);
        editorArea.setText(content);
        editorArea.setForeground(EDITOR_COLOR);
        editorArea.setCaretPosition(0);
    }

    /**
     * Clears the editor back to placeholder state.
     */
    public void clearEditor() {
        titleField.setText(TITLE_PLACEHOLDER);
        titleField.setForeground(PLACEHOLDER_COLOR);
        editorArea.setText(EDITOR_PLACEHOLDER);
        editorArea.setForeground(PLACEHOLDER_COLOR);
    }

    /**
     * Returns the current note title.
     */
    public String getNoteTitle() {
        String text = titleField.getText();
        return text.equals(TITLE_PLACEHOLDER) ? "" : text;
    }

    /**
     * Returns the current note content.
     */
    public String getNoteContent() {
        String text = editorArea.getText();
        return text.equals(EDITOR_PLACEHOLDER) ? "" : text;
    }

    /**
     * Links the StatusBar so the editor can push live word count updates.
     */
    public void linkStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

}