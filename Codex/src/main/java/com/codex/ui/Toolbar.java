package main.java.com.codex.ui;

// ─────────────────────────────────────────────────────────────────────────────
//  IMPORTS
// ─────────────────────────────────────────────────────────────────────────────

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  Toolbar — the top bar of the Codex application.
//  Contains: [+ Section] [+ Note] [Search...] (spacer) [Sync]
//  Uses a horizontal BoxLayout for clean left-to-right alignment.
// ─────────────────────────────────────────────────────────────────────────────

public class Toolbar extends JPanel {

    // ─────────────────────────────────────────────────────────────────────────
    //  ATTRIBUTES / FIELDS
    // ─────────────────────────────────────────────────────────────────────────

    // Toolbar height
    private static final int TOOLBAR_HEIGHT = 48;

    // Button labels
    private static final String LABEL_NEW_SECTION = "+ Section";
    private static final String LABEL_NEW_NOTE    = "+ Note";
    private static final String LABEL_SYNC        = "⟳  Sync";

    // Search placeholder text
    private static final String SEARCH_PLACEHOLDER = "Search notes...";

    // Component sizing
    private static final int SEARCH_WIDTH  = 220;
    private static final int SEARCH_HEIGHT = 30;
    private static final int BUTTON_HEIGHT = 30;

    // Toolbar buttons
    private JButton btnNewSection;
    private JButton btnNewNote;
    private JButton btnSync;

    // Search field
    private JTextField searchField;

    // ─────────────────────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────────────────

    public Toolbar() {
        initComponents();
        initLayout();
        initListeners();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  COMPONENT INITIALIZATION
    //  Build and style each toolbar component individually.
    // ─────────────────────────────────────────────────────────────────────────

    private void initComponents() {
        // --- Buttons
        btnNewSection = createToolbarButton(LABEL_NEW_SECTION);
        btnNewNote    = createToolbarButton(LABEL_NEW_NOTE);
        btnSync       = createToolbarButton(LABEL_SYNC);

        // --- Search field
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(SEARCH_WIDTH, SEARCH_HEIGHT));
        searchField.setMaximumSize(new Dimension(SEARCH_WIDTH, SEARCH_HEIGHT));
        searchField.setToolTipText("Search notes by title or content");
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(Color.GRAY);
        searchField.setText(SEARCH_PLACEHOLDER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  LAYOUT / ASSEMBLY
    //  Arrange components left-to-right with a glue spacer before Sync.
    //
    //  [ + Section ][ + Note ][ Search... ] ----spacer---- [ Sync ]
    // ─────────────────────────────────────────────────────────────────────────

    private void initLayout() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(0, TOOLBAR_HEIGHT));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(0, 16, 0, 16)
        ));

        // Left-side controls
        add(Box.createVerticalGlue());
        add(btnNewSection);
        add(Box.createHorizontalStrut(8));
        add(btnNewNote);
        add(Box.createHorizontalStrut(16));
        add(searchField);

        // Push Sync button to the right
        add(Box.createHorizontalGlue());
        add(btnSync);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  EVENT LISTENERS
    //  Placeholder actions — wire these to real logic once dialogs are ready.
    // ─────────────────────────────────────────────────────────────────────────

    private void initListeners() {
        // --- New Section button
        btnNewSection.addActionListener(e -> onNewSection());

        // --- New Note button
        btnNewNote.addActionListener(e -> onNewNote());

        // --- Sync button
        btnSync.addActionListener(e -> onSync());

        // --- Search field: clear placeholder on focus
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(SEARCH_PLACEHOLDER)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isBlank()) {
                    searchField.setText(SEARCH_PLACEHOLDER);
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // --- Search field: trigger search on each keystroke
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().trim();
                if (!query.equals(SEARCH_PLACEHOLDER)) {
                    onSearch(query);
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Factory method — creates a consistently styled flat toolbar button.
     */
    private JButton createToolbarButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(button.getPreferredSize().width + 16, BUTTON_HEIGHT));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setContentAreaFilled(true);
                button.setBackground(new Color(240, 240, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setContentAreaFilled(false);
            }
        });

        return button;
    }

    /**
     * Called when [+ Section] is clicked.
     * Will open NewSectionDialog — hooked in later.
     */
    private void onNewSection() {
        // TODO: open NewSectionDialog
        System.out.println("[Toolbar] New Section clicked");
    }

    /**
     * Called when [+ Note] is clicked.
     * Will open NewNoteDialog — hooked in later.
     */
    private void onNewNote() {
        // TODO: open NewNoteDialog
        System.out.println("[Toolbar] New Note clicked");
    }

    /**
     * Called when [Sync] is clicked.
     * Will trigger SyncService — hooked in later.
     */
    private void onSync() {
        // TODO: trigger SyncService.push()
        System.out.println("[Toolbar] Sync clicked");
    }

    /**
     * Called on each keystroke in the search field.
     * Will filter notes in SectionTree/NoteEditor — hooked in later.
     *
     * @param query the current search string
     */
    private void onSearch(String query) {
        // TODO: filter notes by query
        System.out.println("[Toolbar] Searching: " + query);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GETTERS
    //  Expose components that other classes may need to interact with.
    // ─────────────────────────────────────────────────────────────────────────

    public JButton getBtnNewSection() { return btnNewSection; }
    public JButton getBtnNewNote()    { return btnNewNote; }
    public JButton getBtnSync()       { return btnSync; }
    public JTextField getSearchField(){ return searchField; }

}