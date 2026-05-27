package com.codex;

// ─────────────────────────────────────────────────────────────────────────────
//  IMPORTS
// ─────────────────────────────────────────────────────────────────────────────

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.codex.ui.MainFrame;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  Main — entry point for the Codex application.
//  Bootstraps the Swing UI on the Event Dispatch Thread (EDT).
// ─────────────────────────────────────────────────────────────────────────────

public class Main {

    // ─────────────────────────────────────────────────────────────────────────
    //  MAIN METHOD
    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // Apply system look and feel before launching the window
        applyLookAndFeel();

        // Launch the main window on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            // Link NoteEditor to StatusBar for live word count updates
            frame.getNoteEditor().linkStatusBar(frame.getStatusBar());
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Applies the system look and feel.
     * FlatLaf will replace this once the dependency is available via Maven.
     * To switch to FlatLaf, replace with:
     *   FlatLightLaf.setup();
     */
    private static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("[Main] Failed to apply look and feel: " + e.getMessage());
        }
    }

}