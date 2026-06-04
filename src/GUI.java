/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author reyvi
 */
public class GUI extends javax.swing.JFrame {
    
    private String studentSortBy = "id"; private boolean studentSortDesc = false;
    private String programSortBy = "code"; private boolean programSortDesc = false;
    private String collegeSortBy = "code"; private boolean collegeSortDesc = false;

    private String studentNameQuery = "";
    private String studentProgramFilter = "All Programs";
    private String studentCollegeFilter = "All Colleges";
    private String studentYearFilter = "All Year Levels";

    private String programNameQuery = "";
    private String programCollegeFilter = "All Colleges";

    private String collegeNameQuery = "";
    
    private int currentStudentPage = 1;
    private int currentProgramPage = 1;
    private int currentCollegePage = 1;
    private static final int PAGE_SIZE = 15;
    
    public GUI() {

    String[] fontFiles = {
        "/fonts/Syne-Bold.ttf",
        "/fonts/Syne-Regular.ttf",
        "/fonts/Syne-Medium.ttf",
        "/fonts/Syne-ExtraBold.ttf",
        "/fonts/Syne-SemiBold.ttf"
    };
    
    java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
    for (String path : fontFiles) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                ge.registerFont(java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
            
        try {
            SqliteDb.initialize();
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "Failed to initialize database: " + e.getMessage());
        }
        
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/fonts/Syne-Bold.ttf");
            if (is != null) {
                java.awt.Font syneFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is);
                ge.registerFont(syneFont);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initComponents();        
        initFilters();
        reloadAllTables();
        setupActionsColumn();
        initResponsiveLayouts();
        
        tblStudents.getColumnModel().getColumn(0).setPreferredWidth(15);
        tblStudents.getColumnModel().getColumn(1).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(2).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(3).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(4).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(5).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(6).setPreferredWidth(10);
        tblStudents.getColumnModel().getColumn(7).setPreferredWidth(50);
        
        tblPrograms.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblPrograms.getColumnModel().getColumn(0).setMaxWidth(130);
        tblPrograms.getColumnModel().getColumn(1).setPreferredWidth(320);
        tblPrograms.getColumnModel().getColumn(2).setPreferredWidth(320);
        tblPrograms.getColumnModel().getColumn(3).setPreferredWidth(290);
        tblPrograms.getColumnModel().getColumn(3).setMaxWidth(290);
        
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
        scrPrograms.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrColleges.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        tblStudents.setRowHeight(36);
        tblPrograms.setRowHeight(36);
        tblColleges.setRowHeight(36);

      
        btnStudentsNext.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnStudentsPrev.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnProgramsPrev.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnProgramsNext.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnCollegesPrev.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnCollegesNext.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jPanel6.setVisible(false);
        jPanel8.setVisible(false);
              

        jPanel6.setVisible(false);
        jPanel8.setVisible(false);

        
        setResizable(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizePanels();
            }
        });
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);


        // hover and click effects
        javax.swing.JButton[] all = {jButton1, jButton2, jButton3};
        styleNavButton(jButton1, all);
        styleNavButton(jButton2, all);
        styleNavButton(jButton3, all);
        
        jButton1.setBackground(new java.awt.Color(18, 18, 32));
        jButton1.setForeground(new java.awt.Color(199, 202, 219));
             
        jTextField1.setForeground(new java.awt.Color(180, 180, 180)); 
        jTextField1.setText("Search students...");
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (jTextField1.getText().equals("Search students...")) {
                    jTextField1.setText("");
                    jTextField1.setForeground(new java.awt.Color(26, 26, 46));
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (jTextField1.getText().isEmpty()) {
                    jTextField1.setText("Search students...");
                    jTextField1.setForeground(new java.awt.Color(180, 180, 180)); 
                }
            }
        });
        
        jTextField2.setForeground(new java.awt.Color(180, 180, 180));
        jTextField2.setText("Search programs...");
        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (jTextField2.getText().equals("Search programs...")) {
                    jTextField2.setText("");
                    jTextField2.setForeground(new java.awt.Color(26, 26, 46));
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (jTextField2.getText().isEmpty()) {
                    jTextField2.setText("Search programs...");
                    jTextField2.setForeground(new java.awt.Color(180, 180, 180));
                }
            }
        });

        jTextField3.setForeground(new java.awt.Color(180, 180, 180));
        jTextField3.setText("Search colleges...");
        jTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (jTextField3.getText().equals("Search colleges...")) {
                    jTextField3.setText("");
                    jTextField3.setForeground(new java.awt.Color(26, 26, 46));
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (jTextField3.getText().isEmpty()) {
                    jTextField3.setText("Search colleges...");
                    jTextField3.setForeground(new java.awt.Color(180, 180, 180));
                }
            }
        });

        fixComboBox(jComboBox2);
        fixComboBox(jComboBox3);

    }
    
    private void setupHeaderSorting() {
        // STUDENTS
        tblStudents.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = tblStudents.columnAtPoint(e.getPoint());
                String dbCol = switch (col) {
                    case 0 -> "id";
                    case 1 -> "firstname";
                    case 2 -> "lastname";
                    case 3 -> "program_code";
                    case 4 -> "college_code";
                    case 5 -> "year";
                    case 6 -> "gender";
                    default -> null;
                };
                if (dbCol == null) return;

                if (studentSortBy.equals(dbCol)) studentSortDesc = !studentSortDesc;
                else { studentSortBy = dbCol; studentSortDesc = false; }

                currentStudentPage = 1;
                renderStudentPage();
                tblStudents.getTableHeader().repaint();
            }
        });

        // PROGRAMS
        tblPrograms.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = tblPrograms.columnAtPoint(e.getPoint());
                // 0 Code, 1 Name, 2 College, 3 Actions
                String dbCol = switch (col) {
                    case 0 -> "code";
                    case 1 -> "name";
                    case 2 -> "college";
                    default -> null;
                };
                if (dbCol == null) return;

                if (programSortBy.equals(dbCol)) programSortDesc = !programSortDesc;
                else { programSortBy = dbCol; programSortDesc = false; }

                currentProgramPage = 1;
                renderProgramPage();
                tblPrograms.getTableHeader().repaint();
            }
        });

        // COLLEGES
        tblColleges.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = tblColleges.columnAtPoint(e.getPoint());
                // 0 Code, 1 Name, 2 Actions
                String dbCol = switch (col) {
                    case 0 -> "code";
                    case 1 -> "name";
                    default -> null;
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
            new String[]{"id","firstname","lastname","program_code","college_code","year","gender", null},
            () -> studentSortBy, () -> studentSortDesc);

        installSortHeaderRenderer(tblPrograms,
            new String[]{"code","name","college", null},
            () -> programSortBy, () -> programSortDesc);

        installSortHeaderRenderer(tblColleges,
            new String[]{"code","name", null},
            () -> collegeSortBy, () -> collegeSortDesc);
    }
    
    private void onDeleteCollege(int row) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tblColleges.getModel();
        String code = String.valueOf(m.getValueAt(row, 0));

        int ok = javax.swing.JOptionPane.showConfirmDialog(
                this, "Delete college " + code + "?", "Confirm",
                javax.swing.JOptionPane.YES_NO_OPTION);

        if (ok == javax.swing.JOptionPane.YES_OPTION) {
            try {
                SqliteDb.collegeDelete(code);
                initFilters();
                reloadAllTables();
            } catch (Exception e) {
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
        d.setLocationRelativeTo(this);
        d.setVisible(true);

        initFilters();
        reloadAllTables();
    }
    
    private void onDeleteProgram(int row) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tblPrograms.getModel();
        String code = String.valueOf(m.getValueAt(row, 0));

        int ok = javax.swing.JOptionPane.showConfirmDialog(
                this, "Delete program " + code + "?", "Confirm",
                javax.swing.JOptionPane.YES_NO_OPTION);

        if (ok == javax.swing.JOptionPane.YES_OPTION) {
            try {
                SqliteDb.programDelete(code);
                initFilters();
                reloadAllTables();
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onEditProgram(int row) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tblPrograms.getModel();

        String code = String.valueOf(m.getValueAt(row, 0));
        String name = String.valueOf(m.getValueAt(row, 1));

        // table shows college NAME, but update dialog expects college CODE
        String displayedCollege = String.valueOf(m.getValueAt(row, 2));
        String collegeCode = displayedCollege;
        try {
            for (java.util.Map<String,String> c : SqliteDb.collegeList("code", false, 0, 0)) {
                if (displayedCollege.equals(c.get("name"))) {
                    collegeCode = c.get("code");
                    break;
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        UpdateProgramDialog d = new UpdateProgramDialog(this, true, code, name, collegeCode);
        d.setLocationRelativeTo(this);
        d.setVisible(true);

        initFilters();
        reloadAllTables();
    }
    
    private void onDeleteStudent(int row) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tblStudents.getModel();
        String id = String.valueOf(m.getValueAt(row, 0));

        int ok = javax.swing.JOptionPane.showConfirmDialog(
                this, "Delete student " + id + "?", "Confirm",
                javax.swing.JOptionPane.YES_NO_OPTION);

        if (ok == javax.swing.JOptionPane.YES_OPTION) {
            try {
                SqliteDb.studentDelete(id);
                initFilters();
                reloadAllTables();
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }    
    
    private void onEditStudent(int row) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tblStudents.getModel();

        String id = String.valueOf(m.getValueAt(row, 0));
        String fn = String.valueOf(m.getValueAt(row, 1));
        String ln = String.valueOf(m.getValueAt(row, 2));
        String prog = String.valueOf(m.getValueAt(row, 3));
        String year = String.valueOf(m.getValueAt(row, 5));
        String gender = String.valueOf(m.getValueAt(row, 6));

        UpdateStudentDialog d = new UpdateStudentDialog(this, true, id, fn, ln, prog, year, gender);
        d.setLocationRelativeTo(this);
        d.setVisible(true);

        initFilters();
        reloadAllTables();
    }   
    
    private void initFilters() {
        try {
            // ---------- Students filters ----------
            // jComboBox2 = Program filter
            jComboBox2.removeAllItems();
            jComboBox2.addItem("All Programs");
            for (java.util.Map<String,String> p : SqliteDb.programList("code", false, 0, 0)) {
                jComboBox2.addItem(p.get("code"));
            }

            // jComboBox6 = College filter
            jComboBox6.removeAllItems();
            jComboBox6.addItem("All Colleges");
            for (java.util.Map<String,String> c : SqliteDb.collegeList("code", false, 0, 0)) {
                jComboBox6.addItem(c.get("code"));
            }

            // jComboBox5 = Year filter
            jComboBox5.removeAllItems();
            jComboBox5.addItem("All Year Levels");
            jComboBox5.addItem("1");
            jComboBox5.addItem("2");
            jComboBox5.addItem("3");
            jComboBox5.addItem("4");
            jComboBox5.addItem("5");

            // ---------- Programs filter ----------
            // jComboBox3 = Program's college filter (replace old Search in model)
            jComboBox3.removeAllItems();
            jComboBox3.addItem("All Colleges");
            for (java.util.Map<String,String> c : SqliteDb.collegeList("code", false, 0, 0)) {
                jComboBox3.addItem(c.get("code"));
            }

            // ---------- Default selected ----------
            jComboBox2.setSelectedItem("All Programs");
            jComboBox6.setSelectedItem("All Colleges");
            jComboBox5.setSelectedItem("All Year Levels");
            jComboBox3.setSelectedItem("All Colleges");

            // keep existing style renderer
            fixComboBox(jComboBox2);
            fixComboBox(jComboBox3);
            fixComboBox(jComboBox5);
            fixComboBox(jComboBox6);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }    
    
    private void applyCollegeFiltersAndRefresh() {
        collegeNameQuery = jTextField3.getText().trim();
        if (collegeNameQuery.equals("Search colleges...")) collegeNameQuery = "";

        currentCollegePage = 1;
        renderCollegePage();
    }    
    
    private void applyProgramFiltersAndRefresh() {
        programNameQuery = jTextField2.getText().trim();
        if (programNameQuery.equals("Search programs...")) programNameQuery = "";

        programCollegeFilter = jComboBox3.getSelectedItem() != null
                ? jComboBox3.getSelectedItem().toString() : "All Colleges";

        currentProgramPage = 1;
        renderProgramPage();
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

        currentStudentPage = 1;
        renderStudentPage();
    }    
    
    private void renderCollegePage() {
        javax.swing.table.DefaultTableModel m =
            (javax.swing.table.DefaultTableModel) tblColleges.getModel();
        m.setRowCount(0);

        try {
            int total = SqliteDb.collegeCountFiltered(collegeNameQuery);

            int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
            currentCollegePage = Math.max(1, Math.min(currentCollegePage, totalPages));

            java.util.List<java.util.Map<String,String>> rows = SqliteDb.collegeListFiltered(
                collegeSortBy, collegeSortDesc, currentCollegePage, PAGE_SIZE, collegeNameQuery
            );

            for (java.util.Map<String,String> c : rows) {
                m.addRow(new Object[]{
                    c.get("code"),
                    c.get("name"),
                    ""
                });
            }

            int showing = rows.size();
            lblCollegesShowing.setText("Showing " + showing + " out of " + total + " entries");
            lblCollegesPage.setText("Page " + currentCollegePage + " of " + totalPages);
            btnCollegesPrev.setEnabled(currentCollegePage > 1);
            btnCollegesNext.setEnabled(currentCollegePage < totalPages);

            updatePaginationPosition(
                scrColleges, lblCollegesShowing, btnCollegesPrev, lblCollegesPage, btnCollegesNext,
                showing, tblColleges.getRowHeight()
            );
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
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
                programNameQuery, programCollegeFilter
            );

            // optional: map college code -> college name for display
            java.util.Map<String,String> collegeNames = new java.util.HashMap<>();
            for (java.util.Map<String,String> c : SqliteDb.collegeList("code", false, 0, 0)) {
                collegeNames.put(c.get("code"), c.get("name"));
            }

            for (java.util.Map<String,String> p : rows) {
                String cc = p.get("college");
                String displayCollege = collegeNames.getOrDefault(cc, cc);

                m.addRow(new Object[]{
                    p.get("code"),
                    p.get("name"),
                    displayCollege,
                    ""
                });
            }

            int showing = rows.size();
            lblProgramsShowing.setText("Showing " + showing + " out of " + total + " entries");
            lblProgramsPage.setText("Page " + currentProgramPage + " of " + totalPages);
            btnProgramsPrev.setEnabled(currentProgramPage > 1);
            btnProgramsNext.setEnabled(currentProgramPage < totalPages);

            updatePaginationPosition(
                scrPrograms, lblProgramsShowing, btnProgramsPrev, lblProgramsPage, btnProgramsNext,
                showing, tblPrograms.getRowHeight()
            );
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }    
    
    private void renderStudentPage() {
        javax.swing.table.DefaultTableModel m =
            (javax.swing.table.DefaultTableModel) tblStudents.getModel();
        m.setRowCount(0);

        try {
            int total = SqliteDb.studentCountFiltered(
                studentNameQuery, studentProgramFilter, studentCollegeFilter, studentYearFilter
            );

            int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
            currentStudentPage = Math.max(1, Math.min(currentStudentPage, totalPages));

            java.util.List<java.util.Map<String,String>> rows = SqliteDb.studentListFiltered(
                studentSortBy, studentSortDesc, currentStudentPage, PAGE_SIZE,
                studentNameQuery, studentProgramFilter, studentCollegeFilter, studentYearFilter
            );

            for (java.util.Map<String,String> s : rows) {
                m.addRow(new Object[]{
                    s.get("id"),
                    s.get("firstname"),
                    s.get("lastname"),
                    s.get("program_code"),
                    s.get("college_code"),
                    s.get("year"),
                    s.get("gender"),
                    ""
                });
            }

            int showing = rows.size();
            lblStudentsShowing.setText("Showing " + showing + " out of " + total + " entries");
            lblStudentsPage.setText("Page " + currentStudentPage + " of " + totalPages);
            btnStudentsPrev.setEnabled(currentStudentPage > 1);
            btnStudentsNext.setEnabled(currentStudentPage < totalPages);

            updatePaginationPosition(
                scrStudents, lblStudentsShowing, btnStudentsPrev, lblStudentsPage, btnStudentsNext,
                showing, tblStudents.getRowHeight()
            );
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }    
    
    private void applyTransparentRowRenderer(javax.swing.JTable table) {
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(t, value, false, false, row, col);

                setOpaque(true);
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

                setBackground(row % 2 == 0
                    ? java.awt.Color.WHITE
                    : new java.awt.Color(240, 238, 233));
                setForeground(new java.awt.Color(26, 26, 46));

                setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(220, 220, 220)),
                    javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));

                return this;
            }
        });
    }
    
    private void makeHeaderTransparent(javax.swing.JTable table) {
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setOpaque(false);
        header.setBackground(new java.awt.Color(0, 0, 0, 0));
        header.setForeground(new java.awt.Color(26, 26, 46));
        header.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);
    }
    
    
    private void makeTableTransparent(javax.swing.JTable table, javax.swing.JScrollPane scroll) {
        table.setOpaque(true);
        table.setBackground(new java.awt.Color(240, 238, 233));
        table.setFillsViewportHeight(true);

        javax.swing.table.JTableHeader h = table.getTableHeader();
        h.setOpaque(false);
        h.setBackground(new java.awt.Color(0, 0, 0, 0));

        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(new java.awt.Color(240, 238, 233));
        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder());
    }
    
    private javax.swing.table.TableCellRenderer makeActionsRenderer() {
        return (table, value, isSelected, hasFocus, row, col) -> {
            javax.swing.JPanel panel = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 6));
            panel.setOpaque(true);
            panel.setBackground(row % 2 == 0 ? java.awt.Color.WHITE : new java.awt.Color(240, 238, 233));

            javax.swing.JButton editBtn = new javax.swing.JButton("Edit");
            editBtn.setContentAreaFilled(false);
            editBtn.setBorderPainted(false);
            editBtn.setFocusPainted(false);
            editBtn.setOpaque(false);
            editBtn.setForeground(new java.awt.Color(39, 174, 96));
            editBtn.setFont(new java.awt.Font("Syne", java.awt.Font.BOLD, 12));
            editBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

            javax.swing.JButton delBtn = new javax.swing.JButton("Delete");
            delBtn.setContentAreaFilled(false);
            delBtn.setBorderPainted(false);
            delBtn.setFocusPainted(false);
            delBtn.setOpaque(false);
            delBtn.setForeground(new java.awt.Color(192, 57, 43));
            delBtn.setFont(new java.awt.Font("Syne", java.awt.Font.BOLD, 12));
            delBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

            panel.add(editBtn);
            panel.add(delBtn);
            return panel;
        };
    }
    
    private void installSortHeaderRenderer(
            javax.swing.JTable table,
            String[] dbCols,
            java.util.function.Supplier<String> getSortBy,
            java.util.function.Supplier<Boolean> getSortDesc) {

        javax.swing.table.TableCellRenderer original = table.getTableHeader().getDefaultRenderer();

        table.getTableHeader().setDefaultRenderer((t, value, isSelected, hasFocus, row, col) -> {
            java.awt.Component c = original.getTableCellRendererComponent(
                    t, value, isSelected, hasFocus, row, col);

            if (c instanceof javax.swing.JLabel label) {
                String dbCol = (col >= 0 && col < dbCols.length) ? dbCols[col] : null;
                String text  = value != null ? value.toString() : "";

                if (dbCol != null && dbCol.equals(getSortBy.get())) {
                    label.setText(text + (getSortDesc.get() ? "  ↓" : "  ↑"));
                } else {
                    label.setText(text);
                }
            }
            return c;
        });
    }

    private void setupActionsColumn() {
        tblStudents.getColumnModel().getColumn(7).setCellRenderer(makeActionsRenderer());
        tblPrograms.getColumnModel().getColumn(3).setCellRenderer(makeActionsRenderer());
        tblColleges.getColumnModel().getColumn(2).setCellRenderer(makeActionsRenderer());

        tblStudents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = tblStudents.rowAtPoint(e.getPoint());
                int col = tblStudents.columnAtPoint(e.getPoint());
                if (viewRow < 0 || col != 7) return;
                int row = tblStudents.convertRowIndexToModel(viewRow);

                java.awt.Rectangle r = tblStudents.getCellRect(viewRow, col, false);
                int x = e.getX() - r.x;
                if (x < r.width / 2) onEditStudent(row); else onDeleteStudent(row);
            }
        });

        tblPrograms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = tblPrograms.rowAtPoint(e.getPoint());
                int col = tblPrograms.columnAtPoint(e.getPoint());
                if (viewRow < 0 || col != 3) return;
                int row = tblPrograms.convertRowIndexToModel(viewRow);

                java.awt.Rectangle r = tblPrograms.getCellRect(viewRow, col, false);
                int x = e.getX() - r.x;
                if (x < r.width / 2) onEditProgram(row); else onDeleteProgram(row);
            }
        });

        tblColleges.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = tblColleges.rowAtPoint(e.getPoint());
                int col = tblColleges.columnAtPoint(e.getPoint());
                if (viewRow < 0 || col != 2) return;
                int row = tblColleges.convertRowIndexToModel(viewRow);

                java.awt.Rectangle r = tblColleges.getCellRect(viewRow, col, false);
                int x = e.getX() - r.x;
                if (x < r.width / 2) onEditCollege(row); else onDeleteCollege(row);
            }
        });
    }
    
    private void resizePanels() {
        int w = getContentPane().getWidth();
        int h = getContentPane().getHeight();
        if (w == 0 || h == 0) return;

        int headerH  = 160;
        int contentH = h - headerH;
        int innerW   = w - 40;

        // Main panels
        jPanel3.setBounds(0, 0,       w, headerH);
        jPanel2.setBounds(0, headerH, w, contentH);
        jPanel6.setBounds(0, headerH, w, contentH);
        jPanel8.setBounds(0, headerH, w, contentH);

        // Rounded filter panels
        roundedPanel2.setBounds(20, 10, innerW, 100);
        roundedPanel3.setBounds(20, 10, innerW, 100);
        roundedPanel4.setBounds(20, 10, innerW, 100);
        layoutRoundedPanels(innerW);
        roundedPanel2.revalidate(); roundedPanel2.repaint();
        roundedPanel3.revalidate(); roundedPanel3.repaint();
        roundedPanel4.revalidate(); roundedPanel4.repaint();

        // Scroll panes — set correct width now; height set by updatePaginationPosition
        int defH = 520;
        scrStudents.setBounds(20, 130, innerW, scrStudents.getHeight() > 40 ? scrStudents.getHeight() : defH);
        scrPrograms.setBounds(20, 130, innerW, scrPrograms.getHeight() > 40 ? scrPrograms.getHeight() : defH);
        scrColleges.setBounds(20, 130, innerW, scrColleges.getHeight() > 40 ? scrColleges.getHeight() : defH);

        // Render pages — this sets scroll pane HEIGHT and pagination Y positions
        renderStudentPage();
        renderProgramPage();
        renderCollegePage();

        // Fix pagination X positions AFTER render (render uses getX() to preserve X,
        // but initial X values are wrong until we explicitly correct them here)
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
        javax.swing.SwingUtilities.invokeLater(this::resizePanels);
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
        int tableHeight = (showing * rowHeight) + headerHeight;
        int tableY = scrollPane.getY();
        int paginationY = tableY + tableHeight + 10;

        // shrink/grow viewport to visible rows only
        scrollPane.setSize(scrollPane.getWidth(), tableHeight);

        // move footer controls right under rows
        showingLabel.setLocation(showingLabel.getX(), paginationY);
        prevBtn.setLocation(prevBtn.getX(), paginationY);
        pageLabel.setLocation(pageLabel.getX(), paginationY);
        nextBtn.setLocation(nextBtn.getX(), paginationY);

        scrollPane.getParent().revalidate();
        scrollPane.getParent().repaint();
    }

    public void reloadStudentTable() {
        currentStudentPage = 1;
        renderStudentPage();
    }

    public void reloadProgramTable() {
        currentProgramPage = 1;
        renderProgramPage();
    }

    public void reloadCollegeTable() {
        currentCollegePage = 1;
        renderCollegePage();
    }

    public void reloadAllTables() {
        reloadStudentTable();
        reloadProgramTable();
        reloadCollegeTable();
        updateCounts();
    }
    
    
    private void fixComboBox(javax.swing.JComboBox combo) {
        combo.setBackground(java.awt.Color.WHITE);
        combo.setForeground(new java.awt.Color(26, 26, 46));
        combo.setFocusable(false);
        combo.setFont(new java.awt.Font("Syne", java.awt.Font.PLAIN, 13));
        

        combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(new java.awt.Color(232, 79, 39));
                    setForeground(java.awt.Color.WHITE);
                } else {
                    setBackground(java.awt.Color.WHITE);
                    setForeground(new java.awt.Color(26, 26, 46));
                }
                setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
    }



    private void styleNavButton(javax.swing.JButton btn, javax.swing.JButton[] allBtns) {
        java.awt.Color normal     = new java.awt.Color(26,26,46);
        java.awt.Color hover      = new java.awt.Color(44, 44, 60);
        java.awt.Color active     = new java.awt.Color(18, 18, 32);
        java.awt.Color textNormal = new java.awt.Color(232, 234, 246);
        java.awt.Color textActive = new java.awt.Color(199, 202, 219);

        btn.setBackground(normal);
        btn.setForeground(textNormal);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(active)) {
                    btn.setBackground(hover);
                    btn.setForeground(textActive);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(active)) {
                    btn.setBackground(normal);
                    btn.setForeground(textNormal);
                }
            }
            public void mousePressed(java.awt.event.MouseEvent e) {
                
                for (javax.swing.JButton b : allBtns) {
                    b.setBackground(normal);
                    b.setForeground(textNormal);
                }
                
                btn.setBackground(active);
                btn.setForeground(textActive);
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
        int pw   = innerW - 16; // usable width inside each panel
        int topY = 12, filterY = 58, rowH = 30;

        // ── Students ─────────────────────────────────────────────────────────
        jLabel4.setBounds(6, topY + 4, 90, 22);
        jLabel2.setBounds(100, topY + 4, 200, 22);
        jButton7.setBounds(pw - 133, topY, 140, rowH);

        int fw4 = Math.max(60, (pw - 3 * 8) / 4); // 4 equal fields, 8px gaps
        jTextField1.setBounds(6,                 filterY, fw4, rowH);
        jComboBox2 .setBounds(6 + fw4 + 8,       filterY, fw4, rowH);
        jComboBox6 .setBounds(6 + 2*(fw4 + 8),   filterY, fw4, rowH);
        jComboBox5 .setBounds(6 + 3*(fw4 + 8),   filterY, fw4, rowH);

        // ── Programs ──────────────────────────────────────────────────────────
        jLabel5.setBounds(6, topY + 4, 100, 22);
        jLabel3.setBounds(110, topY + 4, 80, 22);
        jButton5.setBounds(pw - 133, topY, 140, rowH);

        int fw2 = Math.max(80, (pw - 8) / 2);     // 2 equal fields, 8px gap
        jTextField2.setBounds(6,          filterY, fw2, rowH);
        jComboBox3 .setBounds(6 + fw2 + 8, filterY, fw2, rowH);

        // ── Colleges ──────────────────────────────────────────────────────────
        jLabel7.setBounds(6, topY + 4, 100, 22);
        jLabel8.setBounds(110, topY + 4, 80, 22);
        jButton6.setBounds(pw - 133, topY, 140, rowH);
        jTextField3.setBounds(6, filterY, pw - 6, rowH);
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(26, 26, 46));
        jLabel1.setText("Student Information System Version 2");

        jLabel14.setBackground(new java.awt.Color(240, 238, 233));
        jLabel14.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
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
        jButton2.setFont(new java.awt.Font("Inter", 0, 13)); // NOI18N
        jButton2.setText("Colleges");
        jButton2.setBorderPainted(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusPainted(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton2.setOpaque(true);
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jButton3.setBackground(new java.awt.Color(243, 243, 245));
        jButton3.setFont(new java.awt.Font("Inter", 0, 13)); // NOI18N
        jButton3.setText("Programs");
        jButton3.setBorderPainted(false);
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setFocusPainted(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton3.setOpaque(true);
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton1.setBackground(new java.awt.Color(243, 243, 245));
        jButton1.setFont(new java.awt.Font("Inter", 0, 13)); // NOI18N
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
        jPanel8.setName(""); // NOI18N
        jPanel8.setPreferredSize(new java.awt.Dimension(1760, 1080));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCollegesPrev.setBackground(new java.awt.Color(26, 26, 46));
        btnCollegesPrev.setFont(new java.awt.Font("Syne SemiBold", 0, 13)); // NOI18N
        btnCollegesPrev.setForeground(new java.awt.Color(255, 255, 255));
        btnCollegesPrev.setText("<");
        btnCollegesPrev.setBorderPainted(false);
        btnCollegesPrev.setFocusPainted(false);
        btnCollegesPrev.setOpaque(true);
        btnCollegesPrev.addActionListener(this::btnCollegesPrevActionPerformed);
        jPanel8.add(btnCollegesPrev, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 670, 30, 20));

        lblCollegesPage.setBackground(new java.awt.Color(232, 79, 39));
        lblCollegesPage.setFont(new java.awt.Font("Syne", 0, 14)); // NOI18N
        lblCollegesPage.setForeground(new java.awt.Color(26, 26, 46));
        lblCollegesPage.setText("Page 1");
        jPanel8.add(lblCollegesPage, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 670, 80, 20));

        btnCollegesNext.setBackground(new java.awt.Color(26, 26, 46));
        btnCollegesNext.setFont(new java.awt.Font("Syne SemiBold", 0, 13)); // NOI18N
        btnCollegesNext.setForeground(new java.awt.Color(255, 255, 255));
        btnCollegesNext.setText(">");
        btnCollegesNext.setToolTipText("");
        btnCollegesNext.setBorderPainted(false);
        btnCollegesNext.setFocusPainted(false);
        btnCollegesNext.setOpaque(true);
        btnCollegesNext.addActionListener(this::btnCollegesNextActionPerformed);
        jPanel8.add(btnCollegesNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 670, 30, 20));

        lblCollegesShowing.setBackground(new java.awt.Color(232, 79, 39));
        lblCollegesShowing.setFont(new java.awt.Font("Syne", 0, 14)); // NOI18N
        lblCollegesShowing.setForeground(new java.awt.Color(26, 26, 46));
        lblCollegesShowing.setText("Showing X out of Y entries");
        jPanel8.add(lblCollegesShowing, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 670, 170, 20));

        tblColleges.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "College Code", "College Name", "Actions"
            }
        ));
        tblColleges.setOpaque(false);
        scrColleges.setViewportView(tblColleges);

        jPanel8.add(scrColleges, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 1180, 540));

        roundedPanel4.setBackground(new java.awt.Color(240, 238, 233));
        roundedPanel4.setRoundBottomLeft(30);
        roundedPanel4.setRoundBottomRight(30);
        roundedPanel4.setRoundTopLeft(30);
        roundedPanel4.setRoundTopRight(30);

        jLabel7.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(26, 26, 46));
        jLabel7.setText("Colleges -");
        jLabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jTextField3.setColumns(15);
        jTextField3.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jTextField3.setForeground(new java.awt.Color(180, 180, 180));
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField3.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 221, 216), 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        jTextField3.setMinimumSize(new java.awt.Dimension(15, 30));
        jTextField3.setPreferredSize(new java.awt.Dimension(179, 30));
        jTextField3.addActionListener(this::jTextField3ActionPerformed);

        jButton6.setBackground(new java.awt.Color(26, 26, 46));
        jButton6.setFont(new java.awt.Font("Syne SemiBold", 0, 13)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("+ Add College");
        jButton6.setBorderPainted(false);
        jButton6.setFocusPainted(false);
        jButton6.setOpaque(true);
        jButton6.addActionListener(this::jButton6ActionPerformed);

        jLabel8.setBackground(new java.awt.Color(232, 79, 39));
        jLabel8.setFont(new java.awt.Font("Syne", 1, 18)); // NOI18N
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
                        .addComponent(jLabel7)
                        .addComponent(jLabel8))
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
        jPanel6.setName(""); // NOI18N
        jPanel6.setPreferredSize(new java.awt.Dimension(1760, 1080));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnProgramsPrev.setBackground(new java.awt.Color(26, 26, 46));
        btnProgramsPrev.setFont(new java.awt.Font("Syne SemiBold", 0, 13)); // NOI18N
        btnProgramsPrev.setForeground(new java.awt.Color(255, 255, 255));
        btnProgramsPrev.setText("<");
        btnProgramsPrev.setBorderPainted(false);
        btnProgramsPrev.setFocusPainted(false);
        btnProgramsPrev.setOpaque(true);
        btnProgramsPrev.addActionListener(this::btnProgramsPrevActionPerformed);
        jPanel6.add(btnProgramsPrev, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 670, 30, 20));

        lblProgramsPage.setBackground(new java.awt.Color(232, 79, 39));
        lblProgramsPage.setFont(new java.awt.Font("Syne", 0, 14)); // NOI18N
        lblProgramsPage.setForeground(new java.awt.Color(26, 26, 46));
        lblProgramsPage.setText("Page 1");
        jPanel6.add(lblProgramsPage, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 670, 80, 20));

        btnProgramsNext.setBackground(new java.awt.Color(26, 26, 46));
        btnProgramsNext.setFont(new java.awt.Font("Syne SemiBold", 0, 13)); // NOI18N
        btnProgramsNext.setForeground(new java.awt.Color(255, 255, 255));
        btnProgramsNext.setText(">");
        btnProgramsNext.setToolTipText("");
        btnProgramsNext.setBorderPainted(false);
        btnProgramsNext.setFocusPainted(false);
        btnProgramsNext.setOpaque(true);
        btnProgramsNext.addActionListener(this::btnProgramsNextActionPerformed);
        jPanel6.add(btnProgramsNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 670, 30, 20));

        lblProgramsShowing.setBackground(new java.awt.Color(232, 79, 39));
        lblProgramsShowing.setFont(new java.awt.Font("Syne", 0, 14)); // NOI18N
        lblProgramsShowing.setForeground(new java.awt.Color(26, 26, 46));
        lblProgramsShowing.setText("Showing X out of Y entries");
        jPanel6.add(lblProgramsShowing, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 670, 170, 20));

        roundedPanel3.setBackground(new java.awt.Color(240, 238, 233));
        roundedPanel3.setRoundBottomLeft(30);
        roundedPanel3.setRoundBottomRight(30);
        roundedPanel3.setRoundTopLeft(30);
        roundedPanel3.setRoundTopRight(30);

        jLabel3.setBackground(new java.awt.Color(232, 79, 39));
        jLabel3.setFont(new java.awt.Font("Syne", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(26, 26, 46));
        jLabel3.setText("(0)");

        jLabel5.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(26, 26, 46));
        jLabel5.setText("Programs -");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jTextField2.setColumns(15);
        jTextField2.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(180, 180, 180));
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField2.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 221, 216), 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        jTextField2.addActionListener(this::jTextField2ActionPerformed);

        jComboBox3.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jComboBox3.setForeground(new java.awt.Color(26, 26, 46));
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Search in: Code", "Search in: Name", "Search in: College" }));
        jComboBox3.addActionListener(this::jComboBox3ActionPerformed);

        jButton5.setBackground(new java.awt.Color(26, 26, 46));
        jButton5.setFont(new java.awt.Font("Syne SemiBold", 0, 13)); // NOI18N
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
                            .addComponent(jLabel5)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)))
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(jTextField2))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel6.add(roundedPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 1180, 100));

        tblPrograms.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Program Code", "Program Name", "College", "Actions"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
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
        jPanel2.setName(""); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(1760, 1080));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnStudentsNext.setBackground(new java.awt.Color(26, 26, 46));
        btnStudentsNext.setFont(new java.awt.Font("Syne SemiBold", 0, 10)); // NOI18N
        btnStudentsNext.setForeground(new java.awt.Color(255, 255, 255));
        btnStudentsNext.setText(">");
        btnStudentsNext.setToolTipText("");
        btnStudentsNext.setBorderPainted(false);
        btnStudentsNext.setFocusPainted(false);
        btnStudentsNext.setOpaque(true);
        btnStudentsNext.addActionListener(this::btnStudentsNextActionPerformed);
        jPanel2.add(btnStudentsNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 670, 30, 20));

        lblStudentsShowing.setBackground(new java.awt.Color(232, 79, 39));
        lblStudentsShowing.setFont(new java.awt.Font("Syne", 0, 14)); // NOI18N
        lblStudentsShowing.setForeground(new java.awt.Color(26, 26, 46));
        lblStudentsShowing.setText("Showing X out of Y entries");
        jPanel2.add(lblStudentsShowing, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 670, 270, 20));

        lblStudentsPage.setBackground(new java.awt.Color(232, 79, 39));
        lblStudentsPage.setFont(new java.awt.Font("Syne", 0, 14)); // NOI18N
        lblStudentsPage.setForeground(new java.awt.Color(26, 26, 46));
        lblStudentsPage.setText("Page 1");
        jPanel2.add(lblStudentsPage, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 670, 90, 20));

        btnStudentsPrev.setBackground(new java.awt.Color(26, 26, 46));
        btnStudentsPrev.setFont(new java.awt.Font("Syne SemiBold", 0, 10)); // NOI18N
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
        jTextField1.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(180, 180, 180));
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField1.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 221, 216), 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        jLabel4.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(26, 26, 46));
        jLabel4.setText("Students -");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel2.setBackground(new java.awt.Color(232, 79, 39));
        jLabel2.setFont(new java.awt.Font("Syne", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(26, 26, 46));
        jLabel2.setText("(0)");

        jComboBox2.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jComboBox2.setForeground(new java.awt.Color(26, 26, 46));
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Search in: ID", "Search in: First Name", "Search in: Last Name", "Search in: Program", "Search in: Year", "Search in: Gender" }));
        jComboBox2.addActionListener(this::jComboBox2ActionPerformed);

        jComboBox6.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jComboBox6.setForeground(new java.awt.Color(26, 26, 46));
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Search in: ID", "Search in: First Name", "Search in: Last Name", "Search in: Program", "Search in: Year", "Search in: Gender" }));
        jComboBox6.addActionListener(this::jComboBox6ActionPerformed);

        jComboBox5.setFont(new java.awt.Font("Syne", 0, 13)); // NOI18N
        jComboBox5.setForeground(new java.awt.Color(26, 26, 46));
        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Search in: ID", "Search in: First Name", "Search in: Last Name", "Search in: Program", "Search in: Year", "Search in: Gender" }));
        jComboBox5.addActionListener(this::jComboBox5ActionPerformed);

        jButton7.setBackground(new java.awt.Color(26, 26, 46));
        jButton7.setFont(new java.awt.Font("Inter", 0, 13)); // NOI18N
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
                        .addComponent(jLabel4)
                        .addComponent(jLabel2)))
                .addGap(18, 18, 18)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );

        jPanel2.add(roundedPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 1180, 100));

        tblStudents.setAutoCreateRowSorter(true);
        tblStudents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "First Name", "Last Name", "Program", "College", "Year", "Gender", "Actions"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrStudents.setViewportView(tblStudents);

        jPanel2.add(scrStudents, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 1180, 520));

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 160, 1230, 820);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        applyStudentFiltersAndRefresh();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        applyProgramFiltersAndRefresh();
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        applyCollegeFiltersAndRefresh();
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        applyStudentFiltersAndRefresh();
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void btnStudentsNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStudentsNextActionPerformed
        currentStudentPage++; renderStudentPage();
    }//GEN-LAST:event_btnStudentsNextActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        AddCollegeDialog dialog = new AddCollegeDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        initFilters();
        reloadAllTables();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        AddProgramDialog dialog = new AddProgramDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        initFilters();
        reloadAllTables();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        applyProgramFiltersAndRefresh();
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jPanel2.setVisible(false);
        jPanel6.setVisible(true);
        jPanel8.setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jPanel2.setVisible(false);
        jPanel6.setVisible(false);
        jPanel8.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        AddStudentDialog dialog = new AddStudentDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        initFilters();
        reloadAllTables();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void btnStudentsPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStudentsPrevActionPerformed
        currentStudentPage--; renderStudentPage();
    }//GEN-LAST:event_btnStudentsPrevActionPerformed

    private void btnProgramsPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProgramsPrevActionPerformed
        currentProgramPage--; renderProgramPage();
    }//GEN-LAST:event_btnProgramsPrevActionPerformed

    private void btnProgramsNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProgramsNextActionPerformed
        currentProgramPage++; renderProgramPage();
    }//GEN-LAST:event_btnProgramsNextActionPerformed

    private void btnCollegesPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCollegesPrevActionPerformed
        currentCollegePage--; renderCollegePage();  
    }//GEN-LAST:event_btnCollegesPrevActionPerformed

    private void btnCollegesNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCollegesNextActionPerformed
        currentCollegePage++; renderCollegePage();
    }//GEN-LAST:event_btnCollegesNextActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jPanel2.setVisible(true);
        jPanel6.setVisible(false);
        jPanel8.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
        applyStudentFiltersAndRefresh();
    }//GEN-LAST:event_jComboBox5ActionPerformed

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        applyStudentFiltersAndRefresh();
    }//GEN-LAST:event_jComboBox6ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        //</editor-fold>

        java.awt.EventQueue.invokeLater(() -> new GUI().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables
}

