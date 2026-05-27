package main.java.com.codex.ui;
// ─────────────────────────────────────────────────────────────────────────────
//  IMPORTS
// ─────────────────────────────────────────────────────────────────────────────

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  DocumentViewer — a panel that previews PDF and DOCX files.
//  Shown in the right panel when a note has an attachment open.
//  PDFBox and Apache POI rendering will be wired in once util handlers exist.
// ─────────────────────────────────────────────────────────────────────────────

public class DocumentViewer extends JPanel {

    // ─────────────────────────────────────────────────────────────────────────
    //  ATTRIBUTES / FIELDS
    // ─────────────────────────────────────────────────────────────────────────

    private static final Color PANEL_BACKGROUND = new Color(230, 230, 230);
    private static final Color CARD_BACKGROUND  = Color.WHITE;
    private static final Color TEXT_COLOR       = new Color(100, 100, 100);
    private static final Font  LABEL_FONT       = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font  FILENAME_FONT    = new Font("Segoe UI", Font.BOLD, 14);

    // Toolbar
    private JPanel      viewerToolbar;
    private JButton     btnClose;
    private JButton     btnPrevPage;
    private JButton     btnNextPage;
    private JLabel      lblPageInfo;
    private JLabel      lblFileName;

    // Document render area
    private JPanel      documentCanvas;
    private JScrollPane canvasScroll;

    // Placeholder label (shown when no document is loaded)
    private JLabel      lblPlaceholder;

    // State
    private int         currentPage  = 1;
    private int         totalPages   = 0;
    private String      loadedFile   = null;

    // ─────────────────────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────────────────

    public DocumentViewer() {
        initComponents();
        initLayout();
        initListeners();
        showPlaceholder();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  COMPONENT INITIALIZATION
    // ─────────────────────────────────────────────────────────────────────────

    private void initComponents() {
        // --- Viewer toolbar
        viewerToolbar = new JPanel(new BorderLayout());
        viewerToolbar.setBackground(new Color(245, 245, 245));
        viewerToolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));

        lblFileName  = new JLabel("No document open");
        lblFileName.setFont(FILENAME_FONT);
        lblFileName.setForeground(new Color(50, 50, 50));

        // Pagination controls
        btnPrevPage  = createViewerButton("◀");
        btnNextPage  = createViewerButton("▶");
        lblPageInfo  = new JLabel("—");
        lblPageInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPageInfo.setForeground(TEXT_COLOR);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        paginationPanel.setOpaque(false);
        paginationPanel.add(btnPrevPage);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNextPage);

        // Close button
        btnClose = createViewerButton("✕  Close");
        btnClose.setForeground(new Color(180, 60, 60));

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightControls.setOpaque(false);
        rightControls.add(paginationPanel);
        rightControls.add(btnClose);

        viewerToolbar.add(lblFileName,   BorderLayout.WEST);
        viewerToolbar.add(rightControls, BorderLayout.EAST);

        // --- Document canvas (renders document pages)
        documentCanvas = new JPanel();
        documentCanvas.setBackground(PANEL_BACKGROUND);
        documentCanvas.setLayout(new BoxLayout(documentCanvas, BoxLayout.Y_AXIS));

        canvasScroll = new JScrollPane(documentCanvas);
        canvasScroll.setBorder(null);
        canvasScroll.setBackground(PANEL_BACKGROUND);
        canvasScroll.getViewport().setBackground(PANEL_BACKGROUND);

        // --- Placeholder label
        lblPlaceholder = new JLabel("No document open", SwingConstants.CENTER);
        lblPlaceholder.setFont(LABEL_FONT);
        lblPlaceholder.setForeground(TEXT_COLOR);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  LAYOUT / ASSEMBLY
    // ─────────────────────────────────────────────────────────────────────────

    private void initLayout() {
        setLayout(new BorderLayout());
        setBackground(PANEL_BACKGROUND);

        add(viewerToolbar, BorderLayout.NORTH);
        add(canvasScroll,  BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  EVENT LISTENERS
    // ─────────────────────────────────────────────────────────────────────────

    private void initListeners() {
        btnPrevPage.addActionListener(e -> goToPreviousPage());
        btnNextPage.addActionListener(e -> goToNextPage());
        btnClose.addActionListener(e -> closeDocument());
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Factory method — creates a styled flat button for the viewer toolbar.
     */
    private JButton createViewerButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Shows the placeholder state when no document is loaded.
     */
    private void showPlaceholder() {
        documentCanvas.removeAll();
        documentCanvas.add(lblPlaceholder);
        lblFileName.setText("No document open");
        lblPageInfo.setText("—");
        updatePaginationButtons();
        revalidate();
        repaint();
    }

    /**
     * Updates page info label and enables/disables pagination buttons.
     */
    private void updatePaginationButtons() {
        boolean hasPrev = currentPage > 1;
        boolean hasNext = currentPage < totalPages;
        btnPrevPage.setEnabled(hasPrev);
        btnNextPage.setEnabled(hasNext);

        if (totalPages > 0) {
            lblPageInfo.setText("Page " + currentPage + " of " + totalPages);
        } else {
            lblPageInfo.setText("—");
        }
    }

    /**
     * Navigates to the previous page.
     */
    private void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            renderCurrentPage();
        }
    }

    /**
     * Navigates to the next page.
     */
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            renderCurrentPage();
        }
    }

    /**
     * Renders the current page onto the canvas.
     * TODO: wire to PDFHandler or DocxHandler once util layer is ready.
     */
    private void renderCurrentPage() {
        updatePaginationButtons();
        // TODO: render page from PDFHandler / DocxHandler
        System.out.println("[DocumentViewer] Rendering page " + currentPage + " of " + loadedFile);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUBLIC METHODS
    //  Called by NoteEditor or AttachFileDialog to open/close documents.
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Opens a document file for preview.
     * Supports PDF and DOCX — rendering delegated to util handlers (TODO).
     *
     * @param filePath absolute path to the file
     * @param fileName display name of the file
     * @param pages    total number of pages in the document
     */
    public void openDocument(String filePath, String fileName, int pages) {
        this.loadedFile  = filePath;
        this.currentPage = 1;
        this.totalPages  = pages;

        lblFileName.setText(fileName);
        updatePaginationButtons();

        documentCanvas.removeAll();

        // Placeholder card shown until real rendering is wired in
        JPanel placeholderCard = new JPanel(new BorderLayout());
        placeholderCard.setBackground(CARD_BACKGROUND);
        placeholderCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        placeholderCard.setMaximumSize(new Dimension(700, 900));
        placeholderCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cardLabel = new JLabel(
            "<html><center>📄 " + fileName + "<br><br>" +
            "<span style='color:gray;font-size:11px'>Document rendering will appear here.<br>" +
            "Wire PDFHandler / DocxHandler to complete.</span></center></html>",
            SwingConstants.CENTER
        );
        cardLabel.setFont(LABEL_FONT);
        placeholderCard.add(cardLabel, BorderLayout.CENTER);

        documentCanvas.add(Box.createVerticalStrut(24));
        documentCanvas.add(placeholderCard);
        documentCanvas.add(Box.createVerticalStrut(24));

        revalidate();
        repaint();
    }

    /**
     * Closes the currently open document and resets to placeholder state.
     */
    public void closeDocument() {
        this.loadedFile  = null;
        this.currentPage = 1;
        this.totalPages  = 0;
        showPlaceholder();
    }

}