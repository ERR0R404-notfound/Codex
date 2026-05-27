package com.codex.ui;
// ─────────────────────────────────────────────────────────────────────────────
//  IMPORTS
// ─────────────────────────────────────────────────────────────────────────────

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.codex.util.UITheme;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  StatusBar — the bottom bar of the Codex application.
//  Displays: [Section Path] | [Word/Char Count] | [Last Saved] | [Sync Status] | [DB Status]
// ─────────────────────────────────────────────────────────────────────────────

public class StatusBar extends JPanel {

    // ─────────────────────────────────────────────────────────────────────────
    //  ATTRIBUTES / FIELDS
    // ─────────────────────────────────────────────────────────────────────────

    private static final DateTimeFormatter TIME_FORMAT =
        DateTimeFormatter.ofPattern("MMM d, yyyy  h:mm a");

    // Status labels
    private JLabel lblSectionPath;
    private JLabel lblWordCount;
    private JLabel lblLastSaved;
    private JLabel lblSyncStatus;
    private JLabel lblDbStatus;

    // ─────────────────────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────────────────

    public StatusBar() {
        initComponents();
        initLayout();
        setDefaults();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  COMPONENT INITIALIZATION
    // ─────────────────────────────────────────────────────────────────────────

    private void initComponents() {
        lblSectionPath = createStatusLabel("—", SwingConstants.LEFT);
        lblWordCount   = createStatusLabel("Words: 0   Chars: 0", SwingConstants.CENTER);
        lblLastSaved   = createStatusLabel("Not saved yet", SwingConstants.CENTER);
        lblSyncStatus  = createStatusLabel("● Local only", SwingConstants.CENTER);
        lblDbStatus    = createStatusLabel("● DB Offline", SwingConstants.RIGHT);

        lblSyncStatus.setForeground(UITheme.UNSYNCED_COLOR);
        lblDbStatus.setForeground(UITheme.DB_OFFLINE_COLOR);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  LAYOUT / ASSEMBLY
    //  Five zones separated by thin vertical separators.
    //
    //  [ Section Path ] | [ Words / Chars ] | [ Last Saved ] | [ Sync ] | [ DB ]
    // ─────────────────────────────────────────────────────────────────────────

    private void initLayout() {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(0, UITheme.STATUS_HEIGHT));
        setBackground(UITheme.BAR_BACKGROUND);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.SEPARATOR_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(0, 12, 0, 12);
        gbc.gridy   = 0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        // Zone 1 — Section path (stretches)
        gbc.gridx   = 0;
        gbc.weightx = 1.0;
        add(lblSectionPath, gbc);

        // Separator
        gbc.gridx   = 1;
        gbc.weightx = 0;
        gbc.insets  = new Insets(6, 0, 6, 0);
        add(createSeparator(), gbc);

        // Zone 2 — Word / char count
        gbc.gridx  = 2;
        gbc.insets = new Insets(0, 12, 0, 12);
        add(lblWordCount, gbc);

        // Separator
        gbc.gridx  = 3;
        gbc.insets = new Insets(6, 0, 6, 0);
        add(createSeparator(), gbc);

        // Zone 3 — Last saved
        gbc.gridx  = 4;
        gbc.insets = new Insets(0, 12, 0, 12);
        add(lblLastSaved, gbc);

        // Separator
        gbc.gridx  = 5;
        gbc.insets = new Insets(6, 0, 6, 0);
        add(createSeparator(), gbc);

        // Zone 4 — Sync status
        gbc.gridx  = 6;
        gbc.insets = new Insets(0, 12, 0, 12);
        add(lblSyncStatus, gbc);

        // Separator
        gbc.gridx  = 7;
        gbc.insets = new Insets(6, 0, 6, 0);
        add(createSeparator(), gbc);

        // Zone 5 — DB status
        gbc.gridx  = 8;
        gbc.insets = new Insets(0, 12, 0, 12);
        add(lblDbStatus, gbc);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Factory method — creates a consistently styled status label.
     */
    private JLabel createStatusLabel(String text, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(UITheme.STATUS_FONT);
        label.setForeground(UITheme.TEXT_COLOR);
        return label;
    }

    /**
     * Creates a thin vertical separator for between status zones.
     */
    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, UITheme.STATUS_HEIGHT - 12));
        sep.setForeground(UITheme.SEPARATOR_COLOR);
        return sep;
    }

    /**
     * Sets the initial default state of all status labels.
     */
    private void setDefaults() {
        setSectionPath("—");
        setWordCount(0, 0);
        setLastSaved(null);
        setSyncStatus(false);
        setDbStatus(false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUBLIC UPDATE METHODS
    //  Called by other components to push status updates into the bar.
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Updates the section breadcrumb path.
     * Example: "School > ITS132P > Lab Notes"
     */
    public void setSectionPath(String path) {
        lblSectionPath.setText(path == null || path.isBlank() ? "—" : path);
    }

    /**
     * Updates the word and character count display.
     */
    public void setWordCount(int words, int chars) {
        lblWordCount.setText("Words: " + words + "   Chars: " + chars);
    }

    /**
     * Updates the last saved timestamp.
     * Pass null to show "Not saved yet".
     */
    public void setLastSaved(LocalDateTime dateTime) {
        if (dateTime == null) {
            lblLastSaved.setText("Not saved yet");
        } else {
            lblLastSaved.setText("Saved: " + dateTime.format(TIME_FORMAT));
        }
    }

    /**
     * Updates the sync status indicator.
     *
     * @param synced true = synced to Firebase, false = local only
     */
    public void setSyncStatus(boolean synced) {
        if (synced) {
            lblSyncStatus.setText("● Synced");
            lblSyncStatus.setForeground(UITheme.SYNCED_COLOR);
        } else {
            lblSyncStatus.setText("● Local only");
            lblSyncStatus.setForeground(UITheme.UNSYNCED_COLOR);
        }
    }

    /**
     * Updates the database connection status indicator.
     *
     * @param connected true = MySQL connected, false = offline
     */
    public void setDbStatus(boolean connected) {
        if (connected) {
            lblDbStatus.setText("● DB Connected");
            lblDbStatus.setForeground(UITheme.DB_ONLINE_COLOR);
        } else {
            lblDbStatus.setText("● DB Offline");
            lblDbStatus.setForeground(UITheme.DB_OFFLINE_COLOR);
        }
    }

}