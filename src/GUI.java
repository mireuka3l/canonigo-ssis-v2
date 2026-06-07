public class GUI extends javax.swing.JFrame {

    // ── Sort state ────────────────────────────────────────────────────────────
    private String studentSortBy = "id";    private boolean studentSortDesc = false;
    private String programSortBy = "code";  private boolean programSortDesc = false;
    private String collegeSortBy = "code";  private boolean collegeSortDesc = false;

    // ── Filter state ──────────────────────────────────────────────────────────
    private String studentNameQuery    = "";
    private String studentProgramFilter = "All Programs";
    private String studentCollegeFilter = "All Colleges";
    private String studentYearFilter    = "All Year Levels";
    private String programNameQuery    = "";
    private String programCollegeFilter = "All Colleges";
    private String collegeNameQuery    = "";

    // ── Pagination state ──────────────────────────────────────────────────────
    private int currentStudentPage = 1;
    private int currentProgramPage = 1;
    private int currentCollegePage = 1;
    private static final int PAGE_SIZE = 15;

    // ── Hover-row tracking (NEW) ──────────────────────────────────────────────
    private int hoveredStudentRow = -1;
    private int hoveredProgramRow = -1;
    private int hoveredCollegeRow = -1;

    // ── Design tokens (NEW) ───────────────────────────────────────────────────
    private static final java.awt.Color C_ACCENT     = new java.awt.Color(232,  79,  39);
    private static final java.awt.Color C_NAV        = new java.awt.Color( 26,  26,  46);
    private static final java.awt.Color C_NAV_HOVER  = new java.awt.Color( 44,  44,  60);
    private static final java.awt.Color C_NAV_ACT    = new java.awt.Color( 18,  18,  32);
    private static final java.awt.Color C_CREAM      = new java.awt.Color(240, 238, 233);
    private static final java.awt.Color C_ROW_EVEN   = java.awt.Color.WHITE;
    private static final java.awt.Color C_ROW_ODD    = new java.awt.Color(248, 247, 244);
    private static final java.awt.Color C_ROW_HOVER  = new java.awt.Color(255, 243, 220);
    private static final java.awt.Color C_HDR_BG     = new java.awt.Color(245, 244, 241);
    private static final java.awt.Color C_BORDER     = new java.awt.Color(237, 235, 231);
    private static final java.awt.Color C_TEXT       = new java.awt.Color( 26,  26,  46);
    private static final java.awt.Color C_TEXT_DIM   = new java.awt.Color(150, 150, 170);
    private static final java.awt.Color C_GREEN      = new java.awt.Color( 39, 174,  96);
    private static final java.awt.Color C_RED        = new java.awt.Color(192,  57,  43);

    // ─────────────────────────────────────────────────────────────────────────
    public GUI() {
        // Load custom fonts
        String[] fontFiles = {
            "/fonts/Syne-Bold.ttf", "/fonts/Syne-Regular.ttf",
            "/fonts/Syne-Medium.ttf", "/fonts/Syne-ExtraBold.ttf",
            "/fonts/Syne-SemiBold.ttf"
        };
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (String path : fontFiles) {
            try {
                java.io.InputStream is = getClass().getResourceAsStream(path);
                if (is != null) ge.registerFont(java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is));
            } catch (Exception e) { e.printStackTrace(); }
        }

        try { SqliteDb.initialize(); }
        catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "Failed to initialize database: " + e.getMessage());
        }

        initComponents();
        initFilters();
        reloadAllTables();
        setupActionsColumn();
        setupHoverListeners();          // NEW
        initResponsiveLayouts();

        // Column widths – students
        tblStudents.getColumnModel().getColumn(0).setPreferredWidth(15);
        tblStudents.getColumnModel().getColumn(1).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(2).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(3).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(4).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(5).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(6).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(7).setPreferredWidth(50);

        tblStudents.setDefaultEditor(Object.class, null);
        tblPrograms.setDefaultEditor(Object.class, null);
        tblColleges.setDefaultEditor(Object.class, null);

        tblStudents.setRowSorter(null);
        tblPrograms.setRowSorter(null);
        tblColleges.setRowSorter(null);

        setupHeaderSorting();

        applyTransparentRowRenderer(tblStudents);
        applyTransparentRowRenderer(tblPrograms);
        applyTransparentRowRenderer(tblColleges);

        makeHeaderTransparent(tblStudents);
        makeHeaderTransparent(tblPrograms);
        makeHeaderTransparent(tblColleges);

        makeTableTransparent(tblStudents, scrStudents);
        makeTableTransparent(tblPrograms, scrPrograms);
        makeTableTransparent(tblColleges, scrColleges);

        scrStudents.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrPrograms .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrColleges .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        tblStudents.setRowHeight(38);
        tblPrograms.setRowHeight(38);
        tblColleges.setRowHeight(38);

        btnStudentsNext.setMargin(new java.awt.Insets(0,0,0,0));
        btnStudentsPrev.setMargin(new java.awt.Insets(0,0,0,0));
        btnProgramsPrev.setMargin(new java.awt.Insets(0,0,0,0));
        btnProgramsNext.setMargin(new java.awt.Insets(0,0,0,0));
        btnCollegesPrev.setMargin(new java.awt.Insets(0,0,0,0));
        btnCollegesNext.setMargin(new java.awt.Insets(0,0,0,0));

        jPanel6.setVisible(false);
        jPanel8.setVisible(false);

        setResizable(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) { resizePanels(); }
        });
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

        // Navigation buttons
        javax.swing.JButton[] navBtns = {jButton1, jButton2, jButton3};
        for (javax.swing.JButton b : navBtns) styleNavButton(b, navBtns);
        setActiveTab(jButton1);     // Students is default

        // Search field placeholders
        setupPlaceholder(jTextField1, "Search students...");
        setupPlaceholder(jTextField2, "Search programs...");
        setupPlaceholder(jTextField3, "Search colleges...");

        fixComboBox(jComboBox2);
        fixComboBox(jComboBox3);
    }

    // ── NEW: Active-tab indicator ─────────────────────────────────────────────
    private void setActiveTab(javax.swing.JButton activeBtn) {
        javax.swing.JButton[] tabs = {jButton1, jButton2, jButton3};
        for (javax.swing.JButton btn : tabs) {
            boolean active = (btn == activeBtn);
            btn.setBackground(active ? C_NAV_ACT : C_NAV);
            btn.setForeground(active
                ? new java.awt.Color(232, 234, 246)
                : new java.awt.Color(150, 150, 170));
            btn.setBorder(active
                ? javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, C_ACCENT)
                : javax.swing.BorderFactory.createEmptyBorder());
        }
    }

    // ── NEW: Hover listeners ──────────────────────────────────────────────────
    private void setupHoverListeners() {
        addHoverListener(tblStudents, r -> { hoveredStudentRow = r; tblStudents.repaint(); });
        addHoverListener(tblPrograms, r -> { hoveredProgramRow = r; tblPrograms.repaint(); });
        addHoverListener(tblColleges, r -> { hoveredCollegeRow = r; tblColleges.repaint(); });
    }

    private void addHoverListener(javax.swing.JTable table,
                                   java.util.function.IntConsumer setter) {
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseMoved(java.awt.event.MouseEvent e) {
                setter.accept(table.rowAtPoint(e.getPoint()));
            }
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                setter.accept(-1);
            }
        });
    }

    // ── NEW: Placeholder helper ───────────────────────────────────────────────
    private void setupPlaceholder(javax.swing.JTextField field, String placeholder) {
        field.setForeground(C_TEXT_DIM);
        field.setText(placeholder);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(C_TEXT);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(C_TEXT_DIM);
                }
            }
        });
    }

    // ── Header sorting (unchanged logic, wired to installSortHeaderRenderer) ──
    private void setupHeaderSorting() {
        tblStudents.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = tblStudents.columnAtPoint(e.getPoint());
                String dbCol = switch (col) {
                    case 0 -> "id";          case 1 -> "firstname";
                    case 2 -> "lastname";    case 3 -> "program_code";
                    case 4 -> "college_code";case 5 -> "year";
                    case 6 -> "gender";      default -> null;
                };
                if (dbCol == null) return;
                if (studentSortBy.equals(dbCol)) studentSortDesc = !studentSortDesc;
                else { studentSortBy = dbCol; studentSortDesc = false; }
                currentStudentPage = 1;
                renderStudentPage();
                tblStudents.getTableHeader().repaint();
            }
        });

        tblPrograms.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = tblPrograms.columnAtPoint(e.getPoint());
                String dbCol = switch (col) {
                    case 0 -> "code"; case 1 -> "name"; case 2 -> "college"; default -> null;
                };
                if (dbCol == null) return;
                if (programSortBy.equals(dbCol)) programSortDesc = !programSortDesc;
                else { programSortBy = dbCol; programSortDesc = false; }
                currentProgramPage = 1;
                renderProgramPage();
                tblPrograms.getTableHeader().repaint();
            }
        });

        tblColleges.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = tblColleges.columnAtPoint(e.getPoint());
                String dbCol = switch (col) {
                    case 0 -> "code"; case 1 -> "name"; default -> null;
                };
                if (dbCol == null) return;
                if (collegeSortBy.equals(dbCol)) collegeSortDesc = !collegeSortDesc;
                else { collegeSortBy = dbCol; collegeSortDesc = false; }
                currentCollegePage = 1;
                renderCollegePage();
                tblColleges.getTableHeader().repaint();
            }
        });

        installSortHeaderRenderer(tblStudents,
            new String[]{"id","firstname","lastname","program_code","college_code","year","gender",null},
            () -> studentSortBy, () -> studentSortDesc);
        installSortHeaderRenderer(tblPrograms,
            new String[]{"code","name","college",null},
            () -> programSortBy, () -> programSortDesc);
        installSortHeaderRenderer(tblColleges,
            new String[]{"code","name",null},
            () -> collegeSortBy, () -> collegeSortDesc);
    }

    // ── Delete / Edit callbacks (unchanged) ───────────────────────────────────
    private void onDeleteCollege(int row) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tblColleges.getModel();
        String code = String.valueOf(m.getValueAt(row, 0));
        int ok = javax.swing.JOptionPane.showConfirmDialog(this,
            "Delete college " + code + "?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
        if (ok == javax.swing.JOptionPane.YES_OPTION) {
            try { SqliteDb.collegeDelete(code); initFilters(); reloadAllTables(); }
            catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEditCollege(int row) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tblColleges.getModel();
        String code = String.valueOf(m.getValueAt(row, 0));
        String name = String.valueOf(m.getValueAt(row, 1));
        UpdateCollegeDialog d = new UpdateCollegeDialog(this, true, code, name);
        d.setLocationRelativeTo(this); d.setVisible(true);
        initFilters(); reloadAllTables();
    }

    private void onDeleteProgram(int row) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tblPrograms.getModel();
        String code = String.valueOf(m.getValueAt(row, 0));
        int ok = javax.swing.JOptionPane.showConfirmDialog(this,
            "Delete program " + code + "?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
        if (ok == javax.swing.JOptionPane.YES_OPTION) {
            try { SqliteDb.programDelete(code); initFilters(); reloadAllTables(); }
            catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEditProgram(int row) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tblPrograms.getModel();
        String code    = String.valueOf(m.getValueAt(row, 0));
        String name    = String.valueOf(m.getValueAt(row, 1));
        String displayedCollege = String.valueOf(m.getValueAt(row, 2));
        String collegeCode = displayedCollege;
        try {
            for (java.util.Map<String,String> c : SqliteDb.collegeList("code", false, 0, 0)) {
                if (displayedCollege.equals(c.get("name"))) { collegeCode = c.get("code"); break; }
            }
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
        UpdateProgramDialog d = new UpdateProgramDialog(this, true, code, name, collegeCode);
        d.setLocationRelativeTo(this); d.setVisible(true);
        initFilters(); reloadAllTables();
    }

    private void onDeleteStudent(int row) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tblStudents.getModel();
        String id = String.valueOf(m.getValueAt(row, 0));
        int ok = javax.swing.JOptionPane.showConfirmDialog(this,
            "Delete student " + id + "?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
        if (ok == javax.swing.JOptionPane.YES_OPTION) {
            try { SqliteDb.studentDelete(id); initFilters(); reloadAllTables(); }
            catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEditStudent(int row) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tblStudents.getModel();
        String id     = String.valueOf(m.getValueAt(row, 0));
        String fn     = String.valueOf(m.getValueAt(row, 1));
        String ln     = String.valueOf(m.getValueAt(row, 2));
        String prog   = String.valueOf(m.getValueAt(row, 3));
        String year   = String.valueOf(m.getValueAt(row, 5));
        String gender = String.valueOf(m.getValueAt(row, 6));
        UpdateStudentDialog d = new UpdateStudentDialog(this, true, id, fn, ln, prog, year, gender);
        d.setLocationRelativeTo(this); d.setVisible(true);
        initFilters(); reloadAllTables();
    }

    // ── Filters (unchanged) ───────────────────────────────────────────────────
    private void initFilters() {
        try {
            jComboBox2.removeAllItems();
            jComboBox2.addItem("All Programs");
            for (java.util.Map<String,String> p : SqliteDb.programList("code", false, 0, 0))
                jComboBox2.addItem(p.get("code"));

            jComboBox6.removeAllItems();
            jComboBox6.addItem("All Colleges");
            for (java.util.Map<String,String> c : SqliteDb.collegeList("code", false, 0, 0))
                jComboBox6.addItem(c.get("code"));

            jComboBox5.removeAllItems();
            jComboBox5.addItem("All Year Levels");
            for (String y : new String[]{"1","2","3","4","5"}) jComboBox5.addItem(y);

            jComboBox3.removeAllItems();
            jComboBox3.addItem("All Colleges");
            for (java.util.Map<String,String> c : SqliteDb.collegeList("code", false, 0, 0))
                jComboBox3.addItem(c.get("code"));

            jComboBox2.setSelectedItem("All Programs");
            jComboBox6.setSelectedItem("All Colleges");
            jComboBox5.setSelectedItem("All Year Levels");
            jComboBox3.setSelectedItem("All Colleges");

            fixComboBox(jComboBox2); fixComboBox(jComboBox3);
            fixComboBox(jComboBox5); fixComboBox(jComboBox6);
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
    }

    private void applyCollegeFiltersAndRefresh() {
        collegeNameQuery = jTextField3.getText().trim();
        if (collegeNameQuery.equals("Search colleges...")) collegeNameQuery = "";
        currentCollegePage = 1; renderCollegePage();
    }

    private void applyProgramFiltersAndRefresh() {
        programNameQuery = jTextField2.getText().trim();
        if (programNameQuery.equals("Search programs...")) programNameQuery = "";
        programCollegeFilter = jComboBox3.getSelectedItem() != null
            ? jComboBox3.getSelectedItem().toString() : "All Colleges";
        currentProgramPage = 1; renderProgramPage();
    }

    private void applyStudentFiltersAndRefresh() {
        studentNameQuery = jTextField1.getText().trim();
        if (studentNameQuery.equals("Search students...")) studentNameQuery = "";
        studentProgramFilter = jComboBox2.getSelectedItem() != null
            ? jComboBox2.getSelectedItem().toString() : "All Programs";
        studentCollegeFilter = jComboBox6.getSelectedItem() != null
            ? jComboBox6.getSelectedItem().toString() : "All Colleges";
        studentYearFilter = jComboBox5.getSelectedItem() != null
            ? jComboBox5.getSelectedItem().toString() : "All Year Levels";
        currentStudentPage = 1; renderStudentPage();
    }

    // ── Render pages (unchanged) ──────────────────────────────────────────────
    private void renderCollegePage() {
        javax.swing.table.DefaultTableModel m =
            (javax.swing.table.DefaultTableModel) tblColleges.getModel();
        m.setRowCount(0);
        try {
            int total = SqliteDb.collegeCountFiltered(collegeNameQuery);
            int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
            currentCollegePage = Math.max(1, Math.min(currentCollegePage, totalPages));
            java.util.List<java.util.Map<String,String>> rows = SqliteDb.collegeListFiltered(
                collegeSortBy, collegeSortDesc, currentCollegePage, PAGE_SIZE, collegeNameQuery);
            for (java.util.Map<String,String> c : rows)
                m.addRow(new Object[]{ c.get("code"), c.get("name"), "" });
            int showing = rows.size();
            lblCollegesShowing.setText("Showing " + showing + " of " + total + " entries");
            lblCollegesPage.setText("Page " + currentCollegePage + " of " + totalPages);
            btnCollegesPrev.setEnabled(currentCollegePage > 1);
            btnCollegesNext.setEnabled(currentCollegePage < totalPages);
            updatePaginationPosition(scrColleges, lblCollegesShowing,
                btnCollegesPrev, lblCollegesPage, btnCollegesNext, showing, tblColleges.getRowHeight());
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
    }

    private void renderProgramPage() {
        javax.swing.table.DefaultTableModel m =
            (javax.swing.table.DefaultTableModel) tblPrograms.getModel();
        m.setRowCount(0);
        try {
            int total = SqliteDb.programCountFiltered(programNameQuery, programCollegeFilter);
            int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
            currentProgramPage = Math.max(1, Math.min(currentProgramPage, totalPages));
            java.util.List<java.util.Map<String,String>> rows = SqliteDb.programListFiltered(
                programSortBy, programSortDesc, currentProgramPage, PAGE_SIZE,
                programNameQuery, programCollegeFilter);
            java.util.Map<String,String> collegeNames = new java.util.HashMap<>();
            for (java.util.Map<String,String> c : SqliteDb.collegeList("code", false, 0, 0))
                collegeNames.put(c.get("code"), c.get("name"));
            for (java.util.Map<String,String> p : rows) {
                String cc = p.get("college");
                m.addRow(new Object[]{ p.get("code"), p.get("name"),
                    collegeNames.getOrDefault(cc, cc), "" });
            }
            int showing = rows.size();
            lblProgramsShowing.setText("Showing " + showing + " of " + total + " entries");
            lblProgramsPage.setText("Page " + currentProgramPage + " of " + totalPages);
            btnProgramsPrev.setEnabled(currentProgramPage > 1);
            btnProgramsNext.setEnabled(currentProgramPage < totalPages);
            updatePaginationPosition(scrPrograms, lblProgramsShowing,
                btnProgramsPrev, lblProgramsPage, btnProgramsNext, showing, tblPrograms.getRowHeight());
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
    }

    private void renderStudentPage() {
        javax.swing.table.DefaultTableModel m =
            (javax.swing.table.DefaultTableModel) tblStudents.getModel();
        m.setRowCount(0);
        try {
            int total = SqliteDb.studentCountFiltered(
                studentNameQuery, studentProgramFilter, studentCollegeFilter, studentYearFilter);
            int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
            currentStudentPage = Math.max(1, Math.min(currentStudentPage, totalPages));
            java.util.List<java.util.Map<String,String>> rows = SqliteDb.studentListFiltered(
                studentSortBy, studentSortDesc, currentStudentPage, PAGE_SIZE,
                studentNameQuery, studentProgramFilter, studentCollegeFilter, studentYearFilter);
            for (java.util.Map<String,String> s : rows) {
                m.addRow(new Object[]{
                    s.get("id"), s.get("firstname"), s.get("lastname"),
                    s.get("program_code"), s.get("college_code"),
                    s.get("year"), s.get("gender"), "" });
            }
            int showing = rows.size();
            lblStudentsShowing.setText("Showing " + showing + " of " + total + " entries");
            lblStudentsPage.setText("Page " + currentStudentPage + " of " + totalPages);
            btnStudentsPrev.setEnabled(currentStudentPage > 1);
            btnStudentsNext.setEnabled(currentStudentPage < totalPages);
            updatePaginationPosition(scrStudents, lblStudentsShowing,
                btnStudentsPrev, lblStudentsPage, btnStudentsNext, showing, tblStudents.getRowHeight());
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
    }

    // ── MODIFIED: Row renderer with hover highlight ───────────────────────────
    private void applyTransparentRowRenderer(javax.swing.JTable table) {
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(t, value, false, false, row, col);
                setOpaque(true);
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                setFont(new java.awt.Font("Syne", java.awt.Font.PLAIN, 13));

                // Determine which hover variable to use by table identity
                int hovered = (t == tblStudents) ? hoveredStudentRow
                            : (t == tblPrograms)  ? hoveredProgramRow
                            : hoveredCollegeRow;

                if (row == hovered) {
                    setBackground(C_ROW_HOVER);
                    setForeground(C_TEXT);
                } else {
                    setBackground(row % 2 == 0 ? C_ROW_EVEN : C_ROW_ODD);
                    setForeground(C_TEXT);
                }

                // Subtle horizontal rule only — no heavy box borders
                setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
                    javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
                return this;
            }
        });
    }

    // ── MODIFIED: Header — styled via installSortHeaderRenderer ──────────────
    private void makeHeaderTransparent(javax.swing.JTable table) {
        // Actual rendering is fully handled by installSortHeaderRenderer.
        // Just disable reordering here.
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setCursor(
            new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    private void makeTableTransparent(javax.swing.JTable table,
                                       javax.swing.JScrollPane scroll) {
        table.setOpaque(true);
        table.setBackground(C_CREAM);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));

        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(C_CREAM);
        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder());
    }

    // ── MODIFIED: Actions column renderer — hover-aware ──────────────────────
    private javax.swing.table.TableCellRenderer makeActionsRenderer() {
        return (table, value, isSelected, hasFocus, row, col) -> {

            int hovered = (table == tblStudents) ? hoveredStudentRow
                        : (table == tblPrograms)  ? hoveredProgramRow
                        : hoveredCollegeRow;
            boolean isHovered = (row == hovered);

            java.awt.Color bg = isHovered ? C_ROW_HOVER
                : (row % 2 == 0 ? C_ROW_EVEN : C_ROW_ODD);

            javax.swing.JPanel panel = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));
            panel.setOpaque(true);
            panel.setBackground(bg);

            javax.swing.JButton editBtn = makeActionBtn("✏ Edit",   C_GREEN, isHovered);
            javax.swing.JButton delBtn  = makeActionBtn("✕ Delete", C_RED,   isHovered);

            // Thin vertical divider
            javax.swing.JSeparator sep = new javax.swing.JSeparator(javax.swing.JSeparator.VERTICAL);
            sep.setPreferredSize(new java.awt.Dimension(1, 14));
            sep.setForeground(C_BORDER);

            panel.add(editBtn);
            panel.add(sep);
            panel.add(delBtn);
            return panel;
        };
    }

    // ── NEW: Action button factory ────────────────────────────────────────────
    private javax.swing.JButton makeActionBtn(String label, java.awt.Color color,
                                               boolean hovered) {
        javax.swing.JButton btn = new javax.swing.JButton(label);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(hovered);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setForeground(hovered ? color : color.darker());
        btn.setFont(new java.awt.Font("Syne", java.awt.Font.BOLD, 11));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        if (hovered) {
            btn.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, color));
        }
        return btn;
    }

    // ── MODIFIED: Header renderer — polished with accent sort arrow ───────────
    private void installSortHeaderRenderer(
            javax.swing.JTable table,
            String[] dbCols,
            java.util.function.Supplier<String> getSortBy,
            java.util.function.Supplier<Boolean> getSortDesc) {

        table.getTableHeader().setDefaultRenderer(
            (t, value, isSelected, hasFocus, row, col) -> {
                javax.swing.JLabel lbl = new javax.swing.JLabel();
                lbl.setOpaque(true);
                lbl.setBackground(C_HDR_BG);
                lbl.setForeground(C_TEXT);
                lbl.setFont(new java.awt.Font("Syne", java.awt.Font.BOLD, 12));
                lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                // Two-pixel accent bottom border
                lbl.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, C_ACCENT),
                    javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));

                String dbCol = (col >= 0 && col < dbCols.length) ? dbCols[col] : null;
                String text  = value != null ? value.toString() : "";
                if (dbCol != null && dbCol.equals(getSortBy.get())) {
                    lbl.setText(text + (getSortDesc.get() ? "  ▼" : "  ▲"));
                    lbl.setForeground(C_ACCENT);
                } else {
                    lbl.setText(text);
                }
                return lbl;
            }
        );
    }

    // ── Actions column wiring (unchanged) ─────────────────────────────────────
    private void setupActionsColumn() {
        tblStudents.getColumnModel().getColumn(7).setCellRenderer(makeActionsRenderer());
        tblPrograms.getColumnModel().getColumn(3).setCellRenderer(makeActionsRenderer());
        tblColleges.getColumnModel().getColumn(2).setCellRenderer(makeActionsRenderer());

        tblStudents.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = tblStudents.rowAtPoint(e.getPoint());
                int col     = tblStudents.columnAtPoint(e.getPoint());
                if (viewRow < 0 || col != 7) return;
                int row = tblStudents.convertRowIndexToModel(viewRow);
                java.awt.Rectangle r = tblStudents.getCellRect(viewRow, col, false);
                if (e.getX() - r.x < r.width / 2) onEditStudent(row); else onDeleteStudent(row);
            }
        });

        tblPrograms.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = tblPrograms.rowAtPoint(e.getPoint());
                int col     = tblPrograms.columnAtPoint(e.getPoint());
                if (viewRow < 0 || col != 3) return;
                int row = tblPrograms.convertRowIndexToModel(viewRow);
                java.awt.Rectangle r = tblPrograms.getCellRect(viewRow, col, false);
                if (e.getX() - r.x < r.width / 2) onEditProgram(row); else onDeleteProgram(row);
            }
        });

        tblColleges.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = tblColleges.rowAtPoint(e.getPoint());
                int col     = tblColleges.columnAtPoint(e.getPoint());
                if (viewRow < 0 || col != 2) return;
                int row = tblColleges.convertRowIndexToModel(viewRow);
                java.awt.Rectangle r = tblColleges.getCellRect(viewRow, col, false);
                if (e.getX() - r.x < r.width / 2) onEditCollege(row); else onDeleteCollege(row);
            }
        });
    }

    // ── Resize (unchanged, removed stray invokeLater self-call) ──────────────
    private void resizePanels() {
        int w = getContentPane().getWidth();
        int h = getContentPane().getHeight();
        if (w == 0 || h == 0) return;

        int headerH  = 160;
        int contentH = h - headerH;
        int innerW   = w - 40;

        jPanel3.setBounds(0, 0,       w, headerH);
        jPanel2.setBounds(0, headerH, w, contentH);
        jPanel6.setBounds(0, headerH, w, contentH);
        jPanel8.setBounds(0, headerH, w, contentH);

        roundedPanel2.setBounds(20, 10, innerW, 100);
        roundedPanel3.setBounds(20, 10, innerW, 100);
        roundedPanel4.setBounds(20, 10, innerW, 100);
        layoutRoundedPanels(innerW);
        roundedPanel2.revalidate(); roundedPanel2.repaint();
        roundedPanel3.revalidate(); roundedPanel3.repaint();
        roundedPanel4.revalidate(); roundedPanel4.repaint();

        int defH = 520;
        scrStudents.setBounds(20, 130, innerW, scrStudents.getHeight() > 40 ? scrStudents.getHeight() : defH);
        scrPrograms.setBounds(20, 130, innerW, scrPrograms.getHeight() > 40 ? scrPrograms.getHeight() : defH);
        scrColleges.setBounds(20, 130, innerW, scrColleges.getHeight() > 40 ? scrColleges.getHeight() : defH);

        renderStudentPage();
        renderProgramPage();
        renderCollegePage();

        int showingX = 40;
        int prevX    = w - 210;
        int pageX    = w - 170;
        int nextX    = w - 70;

        lblStudentsShowing.setLocation(showingX, lblStudentsShowing.getY());
        btnStudentsPrev   .setLocation(prevX,    btnStudentsPrev.getY());
        lblStudentsPage   .setLocation(pageX,    lblStudentsPage.getY());
        btnStudentsNext   .setLocation(nextX,    btnStudentsNext.getY());

        lblProgramsShowing.setLocation(showingX, lblProgramsShowing.getY());
        btnProgramsPrev   .setLocation(prevX,    btnProgramsPrev.getY());
        lblProgramsPage   .setLocation(pageX,    lblProgramsPage.getY());
        btnProgramsNext   .setLocation(nextX,    btnProgramsNext.getY());

        lblCollegesShowing.setLocation(showingX, lblCollegesShowing.getY());
        btnCollegesPrev   .setLocation(prevX,    btnCollegesPrev.getY());
        lblCollegesPage   .setLocation(pageX,    lblCollegesPage.getY());
        btnCollegesNext   .setLocation(nextX,    btnCollegesNext.getY());
    }

    private void updatePaginationPosition(
            javax.swing.JScrollPane scrollPane,
            javax.swing.JLabel showingLabel,
            javax.swing.JButton prevBtn,
            javax.swing.JLabel pageLabel,
            javax.swing.JButton nextBtn,
            int showing,
            int rowHeight) {

        int headerHeight = 40;
        int tableHeight  = (showing * rowHeight) + headerHeight;
        int tableY       = scrollPane.getY();
        int paginationY  = tableY + tableHeight + 10;

        scrollPane.setSize(scrollPane.getWidth(), tableHeight);

        showingLabel.setLocation(showingLabel.getX(), paginationY);
        prevBtn.setLocation(prevBtn.getX(), paginationY);
        pageLabel.setLocation(pageLabel.getX(), paginationY);
        nextBtn.setLocation(nextBtn.getX(), paginationY);

        scrollPane.getParent().revalidate();
        scrollPane.getParent().repaint();
    }

    public void reloadStudentTable() { currentStudentPage = 1; renderStudentPage(); }
    public void reloadProgramTable() { currentProgramPage = 1; renderProgramPage(); }
    public void reloadCollegeTable() { currentCollegePage = 1; renderCollegePage(); }
    public void reloadAllTables()    { reloadStudentTable(); reloadProgramTable(); reloadCollegeTable(); updateCounts(); }

    // ── MODIFIED: ComboBox styling ────────────────────────────────────────────
    private void fixComboBox(javax.swing.JComboBox combo) {
        combo.setBackground(java.awt.Color.WHITE);
        combo.setForeground(C_TEXT);
        combo.setFocusable(false);
        combo.setFont(new java.awt.Font("Syne", java.awt.Font.PLAIN, 13));
        combo.setBorder(javax.swing.BorderFactory.createLineBorder(C_BORDER, 1));

        combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(new java.awt.Font("Syne", java.awt.Font.PLAIN, 13));
                if (isSelected) {
                    setBackground(C_ACCENT);
                    setForeground(java.awt.Color.WHITE);
                } else {
                    setBackground(java.awt.Color.WHITE);
                    setForeground(C_TEXT);
                }
                setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }

    // ── MODIFIED: Nav button styling ──────────────────────────────────────────
    private void styleNavButton(javax.swing.JButton btn, javax.swing.JButton[] allBtns) {
        btn.setBackground(C_NAV);
        btn.setForeground(C_TEXT_DIM);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        btn.setFont(new java.awt.Font("Inter", java.awt.Font.PLAIN, 13));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(C_NAV_ACT)) {
                    btn.setBackground(C_NAV_HOVER);
                    btn.setForeground(java.awt.Color.WHITE);
                }
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(C_NAV_ACT)) {
                    btn.setBackground(C_NAV);
                    btn.setForeground(C_TEXT_DIM);
                }
            }
        });
    }

    private void updateCounts() {
        try {
            jLabel2.setText(SqliteDb.studentCount() + " total");
            jLabel3.setText(SqliteDb.programCount() + " total");
            jLabel8.setText(SqliteDb.collegeCount() + " total");
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
    }

    private void initResponsiveLayouts() {
        jPanel2.setLayout(null);
        jPanel6.setLayout(null);
        jPanel8.setLayout(null);
        roundedPanel2.setLayout(null);
        roundedPanel3.setLayout(null);
        roundedPanel4.setLayout(null);
        tblStudents.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblPrograms.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblColleges.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private void layoutRoundedPanels(int innerW) {
        int pw    = innerW - 16;
        int topY  = 12, filterY = 58, rowH = 30;

        // Students panel
        jLabel4.setBounds(6,  topY + 4, 90,  22);
        jLabel2.setBounds(100, topY + 4, 200, 22);
        jButton7.setBounds(pw - 133, topY, 140, rowH);
        int fw4 = Math.max(60, (pw - 3 * 8) / 4);
        jTextField1.setBounds(6,               filterY, fw4, rowH);
        jComboBox2 .setBounds(6 + fw4 + 8,     filterY, fw4, rowH);
        jComboBox6 .setBounds(6 + 2*(fw4+8),   filterY, fw4, rowH);
        jComboBox5 .setBounds(6 + 3*(fw4+8),   filterY, fw4, rowH);

        // Programs panel
        jLabel5.setBounds(6,  topY + 4, 100, 22);
        jLabel3.setBounds(110, topY + 4, 80,  22);
        jButton5.setBounds(pw - 133, topY, 140, rowH);
        int fw2 = Math.max(80, (pw - 8) / 2);
        jTextField2.setBounds(6,         filterY, fw2, rowH);
        jComboBox3 .setBounds(6+fw2+8,   filterY, fw2, rowH);

        // Colleges panel
        jLabel7.setBounds(6,  topY + 4, 100, 22);
        jLabel8.setBounds(110, topY + 4, 80,  22);
        jButton6.setBounds(pw - 133, topY, 140, rowH);
        jTextField3.setBounds(6, filterY, pw - 6, rowH);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  AUTO-GENERATED — do not modify
    // ═════════════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        roundedPanel1 = new RoundedPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        btnCollegesPrev = new javax.swing.JButton();
        lblCollegesPage = new javax.swing.JLabel();
        btnCollegesNext = new javax.swing.JButton();
        lblCollegesShowing = new javax.swing.JLabel();
        scrColleges = new javax.swing.JScrollPane();
        tblColleges = new javax.swing.JTable();
        roundedPanel4 = new RoundedPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        btnProgramsPrev = new javax.swing.JButton();
        lblProgramsPage = new javax.swing.JLabel();
        btnProgramsNext = new javax.swing.JButton();
        lblProgramsShowing = new javax.swing.JLabel();
        roundedPanel3 = new RoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jComboBox3 = new javax.swing.JComboBox<>();
        jButton5 = new javax.swing.JButton();
        scrPrograms = new javax.swing.JScrollPane();
        tblPrograms = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnStudentsNext = new javax.swing.JButton();
        lblStudentsShowing = new javax.swing.JLabel();
        lblStudentsPage = new javax.swing.JLabel();
        btnStudentsPrev = new javax.swing.JButton();
        roundedPanel2 = new RoundedPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox6 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jButton7 = new javax.swing.JButton();
        scrStudents = new javax.swing.JScrollPane();
        tblStudents = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(1280, 720));
        getContentPane().setLayout(null);

        jPanel3.setBackground(new java.awt.Color(240, 238, 233));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(242, 242, 242)));
        jPanel3.setFocusCycleRoot(true);
        jPanel3.setPreferredSize(new java.awt.Dimension(880, 700));

        jLabel1.setBackground(new java.awt.Color(240, 238, 233));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24));
        jLabel1.setForeground(new java.awt.Color(26, 26, 46));
        jLabel1.setText("Student Information System Version 2");

        jLabel14.setBackground(new java.awt.Color(240, 238, 233));
        jLabel14.setFont(new java.awt.Font("Inter", 0, 14));
        jLabel14.setForeground(new java.awt.Color(113, 113, 130));
        jLabel14.setText("Manage students, programs, and colleges");

        jSeparator1.setEnabled(false);
        jSeparator1.setRequestFocusEnabled(false);
        jSeparator1.setVerifyInputWhenFocusTarget(false);

        roundedPanel1.setBackground(new java.awt.Color(240, 238, 233));
        roundedPanel1.setOpaque(true);
        roundedPanel1.setRoundBottomLeft(30);
        roundedPanel1.setRoundBottomRight(30);
        roundedPanel1.setRoundTopLeft(30);
        roundedPanel1.setRoundTopRight(30);

        jButton2.setBackground(new java.awt.Color(243, 243, 245));
        jButton2.setFont(new java.awt.Font("Inter", 0, 13));
        jButton2.setText("Colleges");
        jButton2.setBorderPainted(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusPainted(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton2.setOpaque(true);
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jButton3.setBackground(new java.awt.Color(243, 243, 245));
        jButton3.setFont(new java.awt.Font("Inter", 0, 13));
        jButton3.setText("Programs");
        jButton3.setBorderPainted(false);
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setFocusPainted(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton3.setOpaque(true);
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton1.setBackground(new java.awt.Color(243, 243, 245));
        jButton1.setFont(new java.awt.Font("Inter", 0, 13));
        jButton1.setForeground(new java.awt.Color(102, 102, 102));
        jButton1.setText("Students");
        jButton1.setBorderPainted(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusPainted(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton1.setOpaque(true);
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel1)
                    .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3);
        jPanel3.setBounds(0, 0, 1230, 160);

        jPanel8.setBackground(new java.awt.Color(240, 238, 233));
        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 18, 8, 18));
        jPanel8.setForeground(new java.awt.Color(255, 255, 255));
        jPanel8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel8.setMinimumSize(new java.awt.Dimension(1760, 1080));
        jPanel8.setName("");
        jPanel8.setPreferredSize(new java.awt.Dimension(1760, 1080));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCollegesPrev.setBackground(new java.awt.Color(26, 26, 46));
        btnCollegesPrev.setFont(new java.awt.Font("Syne SemiBold", 0, 13));
        btnCollegesPrev.setForeground(new java.awt.Color(255, 255, 255));
        btnCollegesPrev.setText("<");
        btnCollegesPrev.setBorderPainted(false);
        btnCollegesPrev.setFocusPainted(false);
        btnCollegesPrev.setOpaque(true);
        btnCollegesPrev.addActionListener(this::btnCollegesPrevActionPerformed);
        jPanel8.add(btnCollegesPrev, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 670, 30, 20));

        lblCollegesPage.setFont(new java.awt.Font("Syne", 0, 14));
        lblCollegesPage.setForeground(new java.awt.Color(26, 26, 46));
        lblCollegesPage.setText("Page 1");
        jPanel8.add(lblCollegesPage, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 670, 80, 20));

        btnCollegesNext.setBackground(new java.awt.Color(26, 26, 46));
        btnCollegesNext.setFont(new java.awt.Font("Syne SemiBold", 0, 13));
        btnCollegesNext.setForeground(new java.awt.Color(255, 255, 255));
        btnCollegesNext.setText(">");
        btnCollegesNext.setBorderPainted(false);
        btnCollegesNext.setFocusPainted(false);
        btnCollegesNext.setOpaque(true);
        btnCollegesNext.addActionListener(this::btnCollegesNextActionPerformed);
        jPanel8.add(btnCollegesNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 670, 30, 20));

        lblCollegesShowing.setFont(new java.awt.Font("Syne", 0, 14));
        lblCollegesShowing.setForeground(new java.awt.Color(26, 26, 46));
        lblCollegesShowing.setText("Showing X out of Y entries");
        jPanel8.add(lblCollegesShowing, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 670, 200, 20));

        tblColleges.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] { {null,null,null},{null,null,null},{null,null,null},{null,null,null} },
            new String [] { "College Code", "College Name", "Actions" }
        ));
        tblColleges.setOpaque(false);
        scrColleges.setViewportView(tblColleges);
        jPanel8.add(scrColleges, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 1180, 540));

        roundedPanel4.setBackground(new java.awt.Color(240, 238, 233));
        roundedPanel4.setRoundBottomLeft(30);
        roundedPanel4.setRoundBottomRight(30);
        roundedPanel4.setRoundTopLeft(30);
        roundedPanel4.setRoundTopRight(30);

        jLabel7.setFont(new java.awt.Font("Inter", 1, 18));
        jLabel7.setForeground(new java.awt.Color(26, 26, 46));
        jLabel7.setText("Colleges -");
        jLabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jTextField3.setColumns(15);
        jTextField3.setFont(new java.awt.Font("Syne", 0, 13));
        jTextField3.setForeground(new java.awt.Color(150, 150, 170));
        jTextField3.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 221, 216), 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        jTextField3.setMinimumSize(new java.awt.Dimension(15, 30));
        jTextField3.setPreferredSize(new java.awt.Dimension(179, 30));
        jTextField3.addActionListener(this::jTextField3ActionPerformed);

        jButton6.setBackground(new java.awt.Color(26, 26, 46));
        jButton6.setFont(new java.awt.Font("Syne SemiBold", 0, 13));
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("+ Add College");
        jButton6.setBorderPainted(false);
        jButton6.setFocusPainted(false);
        jButton6.setOpaque(true);
        jButton6.addActionListener(this::jButton6ActionPerformed);

        jLabel8.setFont(new java.awt.Font("Syne", 1, 18));
        jLabel8.setForeground(new java.awt.Color(26, 26, 46));
        jLabel8.setText("(0)");

        javax.swing.GroupLayout roundedPanel4Layout = new javax.swing.GroupLayout(roundedPanel4);
        roundedPanel4.setLayout(roundedPanel4Layout);
        roundedPanel4Layout.setHorizontalGroup(
            roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundedPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 844, Short.MAX_VALUE)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );
        roundedPanel4Layout.setVerticalGroup(
            roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel4Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7).addComponent(jLabel8))
                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        jPanel8.add(roundedPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 1180, 100));

        getContentPane().add(jPanel8);
        jPanel8.setBounds(0, 160, 1230, 820);

        jPanel6.setBackground(new java.awt.Color(240, 238, 233));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 18, 8, 18));
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));
        jPanel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel6.setMinimumSize(new java.awt.Dimension(1760, 1080));
        jPanel6.setName("");
        jPanel6.setPreferredSize(new java.awt.Dimension(1760, 1080));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnProgramsPrev.setBackground(new java.awt.Color(26, 26, 46));
        btnProgramsPrev.setFont(new java.awt.Font("Syne SemiBold", 0, 13));
        btnProgramsPrev.setForeground(new java.awt.Color(255, 255, 255));
        btnProgramsPrev.setText("<");
        btnProgramsPrev.setBorderPainted(false);
        btnProgramsPrev.setFocusPainted(false);
        btnProgramsPrev.setOpaque(true);
        btnProgramsPrev.addActionListener(this::btnProgramsPrevActionPerformed);
        jPanel6.add(btnProgramsPrev, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 670, 30, 20));

        lblProgramsPage.setFont(new java.awt.Font("Syne", 0, 14));
        lblProgramsPage.setForeground(new java.awt.Color(26, 26, 46));
        lblProgramsPage.setText("Page 1");
        jPanel6.add(lblProgramsPage, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 670, 80, 20));

        btnProgramsNext.setBackground(new java.awt.Color(26, 26, 46));
        btnProgramsNext.setFont(new java.awt.Font("Syne SemiBold", 0, 13));
        btnProgramsNext.setForeground(new java.awt.Color(255, 255, 255));
        btnProgramsNext.setText(">");
        btnProgramsNext.setBorderPainted(false);
        btnProgramsNext.setFocusPainted(false);
        btnProgramsNext.setOpaque(true);
        btnProgramsNext.addActionListener(this::btnProgramsNextActionPerformed);
        jPanel6.add(btnProgramsNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 670, 30, 20));

        lblProgramsShowing.setFont(new java.awt.Font("Syne", 0, 14));
        lblProgramsShowing.setForeground(new java.awt.Color(26, 26, 46));
        lblProgramsShowing.setText("Showing X out of Y entries");
        jPanel6.add(lblProgramsShowing, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 670, 200, 20));

        roundedPanel3.setBackground(new java.awt.Color(240, 238, 233));
        roundedPanel3.setRoundBottomLeft(30);
        roundedPanel3.setRoundBottomRight(30);
        roundedPanel3.setRoundTopLeft(30);
        roundedPanel3.setRoundTopRight(30);

        jLabel3.setFont(new java.awt.Font("Syne", 1, 18));
        jLabel3.setForeground(new java.awt.Color(26, 26, 46));
        jLabel3.setText("(0)");

        jLabel5.setFont(new java.awt.Font("Inter", 1, 18));
        jLabel5.setForeground(new java.awt.Color(26, 26, 46));
        jLabel5.setText("Programs -");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jTextField2.setColumns(15);
        jTextField2.setFont(new java.awt.Font("Syne", 0, 13));
        jTextField2.setForeground(new java.awt.Color(150, 150, 170));
        jTextField2.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 221, 216), 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        jTextField2.addActionListener(this::jTextField2ActionPerformed);

        jComboBox3.setFont(new java.awt.Font("Syne", 0, 13));
        jComboBox3.setForeground(new java.awt.Color(26, 26, 46));
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Colleges" }));
        jComboBox3.addActionListener(this::jComboBox3ActionPerformed);

        jButton5.setBackground(new java.awt.Color(26, 26, 46));
        jButton5.setFont(new java.awt.Font("Syne SemiBold", 0, 13));
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("+ Add Program");
        jButton5.setBorderPainted(false);
        jButton5.setFocusPainted(false);
        jButton5.setOpaque(true);
        jButton5.addActionListener(this::jButton5ActionPerformed);

        javax.swing.GroupLayout roundedPanel3Layout = new javax.swing.GroupLayout(roundedPanel3);
        roundedPanel3.setLayout(roundedPanel3Layout);
        roundedPanel3Layout.setHorizontalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, 0, 581, Short.MAX_VALUE))
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        roundedPanel3Layout.setVerticalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel3Layout.createSequentialGroup()
                        .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5).addComponent(jLabel3))
                        .addGap(18, 18, 18)))
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(jTextField2))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel6.add(roundedPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 1180, 100));

        tblPrograms.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] { {null,null,null,null},{null,null,null,null},{null,null,null,null},{null,null,null,null} },
            new String [] { "Program Code", "Program Name", "College", "Actions" }
        ) {
            boolean[] canEdit = new boolean [] { false, false, false, false };
            public boolean isCellEditable(int rowIndex, int columnIndex) { return canEdit[columnIndex]; }
        });
        scrPrograms.setViewportView(tblPrograms);
        jPanel6.add(scrPrograms, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 1180, 520));

        getContentPane().add(jPanel6);
        jPanel6.setBounds(0, 160, 1230, 820);

        jPanel2.setBackground(new java.awt.Color(240, 238, 233));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 18, 8, 18));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel2.setMinimumSize(new java.awt.Dimension(1760, 1080));
        jPanel2.setName("");
        jPanel2.setPreferredSize(new java.awt.Dimension(1760, 1080));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnStudentsNext.setBackground(new java.awt.Color(26, 26, 46));
        btnStudentsNext.setFont(new java.awt.Font("Syne SemiBold", 0, 10));
        btnStudentsNext.setForeground(new java.awt.Color(255, 255, 255));
        btnStudentsNext.setText(">");
        btnStudentsNext.setBorderPainted(false);
        btnStudentsNext.setFocusPainted(false);
        btnStudentsNext.setOpaque(true);
        btnStudentsNext.addActionListener(this::btnStudentsNextActionPerformed);
        jPanel2.add(btnStudentsNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 670, 30, 20));

        lblStudentsShowing.setFont(new java.awt.Font("Syne", 0, 14));
        lblStudentsShowing.setForeground(new java.awt.Color(26, 26, 46));
        lblStudentsShowing.setText("Showing X out of Y entries");
        jPanel2.add(lblStudentsShowing, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 670, 270, 20));

        lblStudentsPage.setFont(new java.awt.Font("Syne", 0, 14));
        lblStudentsPage.setForeground(new java.awt.Color(26, 26, 46));
        lblStudentsPage.setText("Page 1");
        jPanel2.add(lblStudentsPage, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 670, 90, 20));

        btnStudentsPrev.setBackground(new java.awt.Color(26, 26, 46));
        btnStudentsPrev.setFont(new java.awt.Font("Syne SemiBold", 0, 10));
        btnStudentsPrev.setForeground(new java.awt.Color(255, 255, 255));
        btnStudentsPrev.setText("<");
        btnStudentsPrev.setBorderPainted(false);
        btnStudentsPrev.setFocusPainted(false);
        btnStudentsPrev.addActionListener(this::btnStudentsPrevActionPerformed);
        jPanel2.add(btnStudentsPrev, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 670, 30, 20));

        roundedPanel2.setBackground(new java.awt.Color(240, 238, 233));
        roundedPanel2.setRoundBottomLeft(30);
        roundedPanel2.setRoundBottomRight(30);
        roundedPanel2.setRoundTopLeft(30);
        roundedPanel2.setRoundTopRight(30);

        jTextField1.setColumns(15);
        jTextField1.setFont(new java.awt.Font("Syne", 0, 13));
        jTextField1.setForeground(new java.awt.Color(150, 150, 170));
        jTextField1.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 221, 216), 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        jLabel4.setFont(new java.awt.Font("Inter", 1, 18));
        jLabel4.setForeground(new java.awt.Color(26, 26, 46));
        jLabel4.setText("Students -");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel2.setFont(new java.awt.Font("Syne", 1, 18));
        jLabel2.setForeground(new java.awt.Color(26, 26, 46));
        jLabel2.setText("(0)");

        jComboBox2.setFont(new java.awt.Font("Syne", 0, 13));
        jComboBox2.setForeground(new java.awt.Color(26, 26, 46));
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Programs" }));
        jComboBox2.addActionListener(this::jComboBox2ActionPerformed);

        jComboBox6.setFont(new java.awt.Font("Syne", 0, 13));
        jComboBox6.setForeground(new java.awt.Color(26, 26, 46));
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Colleges" }));
        jComboBox6.addActionListener(this::jComboBox6ActionPerformed);

        jComboBox5.setFont(new java.awt.Font("Syne", 0, 13));
        jComboBox5.setForeground(new java.awt.Color(26, 26, 46));
        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Year Levels" }));
        jComboBox5.addActionListener(this::jComboBox5ActionPerformed);

        jButton7.setBackground(new java.awt.Color(26, 26, 46));
        jButton7.setFont(new java.awt.Font("Inter", 0, 13));
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("+ Add Student");
        jButton7.setBorderPainted(false);
        jButton7.setFocusPainted(false);
        jButton7.setOpaque(true);
        jButton7.addActionListener(this::jButton7ActionPerformed);

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4).addComponent(jLabel2)))
                .addGap(18, 18, 18)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2,  javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox6,  javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox5,  javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );
        jPanel2.add(roundedPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 1180, 100));

        tblStudents.setAutoCreateRowSorter(true);
        tblStudents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null,null,null,null,null,null,null,null},
                {null,null,null,null,null,null,null,null},
                {null,null,null,null,null,null,null,null},
                {null,null,null,null,null,null,null,null}
            },
            new String [] { "ID","First Name","Last Name","Program","College","Year","Gender","Actions" }
        ) {
            boolean[] canEdit = new boolean [] { false,false,false,false,false,false,false,false };
            public boolean isCellEditable(int rowIndex, int columnIndex) { return canEdit[columnIndex]; }
        });
        scrStudents.setViewportView(tblStudents);
        jPanel2.add(scrStudents, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 1180, 520));

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 160, 1230, 820);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // ── Event handlers ────────────────────────────────────────────────────────
    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) { applyStudentFiltersAndRefresh(); }
    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) { applyProgramFiltersAndRefresh(); }
    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) { applyCollegeFiltersAndRefresh(); }
    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt)  { applyStudentFiltersAndRefresh(); }
    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt)  { applyProgramFiltersAndRefresh(); }
    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt)  { applyStudentFiltersAndRefresh(); }
    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt)  { applyStudentFiltersAndRefresh(); }

    private void btnStudentsNextActionPerformed(java.awt.event.ActionEvent evt) { currentStudentPage++; renderStudentPage(); }
    private void btnStudentsPrevActionPerformed(java.awt.event.ActionEvent evt) { currentStudentPage--; renderStudentPage(); }
    private void btnProgramsNextActionPerformed(java.awt.event.ActionEvent evt) { currentProgramPage++; renderProgramPage(); }
    private void btnProgramsPrevActionPerformed(java.awt.event.ActionEvent evt) { currentProgramPage--; renderProgramPage(); }
    private void btnCollegesNextActionPerformed(java.awt.event.ActionEvent evt) { currentCollegePage++; renderCollegePage(); }
    private void btnCollegesPrevActionPerformed(java.awt.event.ActionEvent evt) { currentCollegePage--; renderCollegePage(); }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        jPanel2.setVisible(true); jPanel6.setVisible(false); jPanel8.setVisible(false);
        setActiveTab(jButton1);
    }
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        jPanel2.setVisible(false); jPanel6.setVisible(true); jPanel8.setVisible(false);
        setActiveTab(jButton3);
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        jPanel2.setVisible(false); jPanel6.setVisible(false); jPanel8.setVisible(true);
        setActiveTab(jButton2);
    }

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
        AddCollegeDialog dialog = new AddCollegeDialog(this, true);
        dialog.setLocationRelativeTo(this); dialog.setVisible(true);
        initFilters(); reloadAllTables();
    }
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        AddProgramDialog dialog = new AddProgramDialog(this, true);
        dialog.setLocationRelativeTo(this); dialog.setVisible(true);
        initFilters(); reloadAllTables();
    }
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {
        AddStudentDialog dialog = new AddStudentDialog(this, true);
        dialog.setLocationRelativeTo(this); dialog.setVisible(true);
        initFilters(); reloadAllTables();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new GUI().setVisible(true));
    }

    // ── Variable declarations — do not modify ─────────────────────────────────
    //GEN-BEGIN:variables
    private javax.swing.JButton btnCollegesNext;
    private javax.swing.JButton btnCollegesPrev;
    private javax.swing.JButton btnProgramsNext;
    private javax.swing.JButton btnProgramsPrev;
    private javax.swing.JButton btnStudentsNext;
    private javax.swing.JButton btnStudentsPrev;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JLabel lblCollegesPage;
    private javax.swing.JLabel lblCollegesShowing;
    private javax.swing.JLabel lblProgramsPage;
    private javax.swing.JLabel lblProgramsShowing;
    private javax.swing.JLabel lblStudentsPage;
    private javax.swing.JLabel lblStudentsShowing;
    private RoundedPanel roundedPanel1;
    private RoundedPanel roundedPanel2;
    private RoundedPanel roundedPanel3;
    private RoundedPanel roundedPanel4;
    private javax.swing.JScrollPane scrColleges;
    private javax.swing.JScrollPane scrPrograms;
    private javax.swing.JScrollPane scrStudents;
    private javax.swing.JTable tblColleges;
    private javax.swing.JTable tblPrograms;
    private javax.swing.JTable tblStudents;
    //GEN-END:variables
}