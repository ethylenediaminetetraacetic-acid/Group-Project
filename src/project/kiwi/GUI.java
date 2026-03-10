import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

/**
 * The front-end code of the application, uses Swing.
 * * @author Yanis Makhoukhi
 *
 */

// ─────────────────────────────────────────────────────────────
//  MAIN GUI CLASS
// ─────────────────────────────────────────────────────────────
public class GUI extends JFrame {

    // ── palette ──────────────────────────────────────────────
    static final Color CLR_BG         = new Color(0xF4F6FB);
    static final Color CLR_PANEL      = Color.WHITE;
    static final Color CLR_ACCENT     = new Color(0x1565C0);
    static final Color CLR_ACCENT2    = new Color(0x1976D2);
    static final Color CLR_DANGER     = new Color(0xC62828);
    static final Color CLR_SUCCESS    = new Color(0x2E7D32);
    static final Color CLR_WARN       = new Color(0xF57F17);
    static final Color CLR_TABLE_HDR  = new Color(0xE3EAF7);
    static final Color CLR_TABLE_ALT  = new Color(0xF9FBFF);
    static final Color CLR_TEXT       = new Color(0x212121);
    static final Font  FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 22);
    static final Font  FONT_HEADER    = new Font("Segoe UI", Font.BOLD, 14);
    static final Font  FONT_BODY      = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font  FONT_MONO      = new Font("Consolas", Font.PLAIN, 12);

    private final AppState state = new AppState();
    private final UndoManager undoManager = new UndoManager();

    // ── shared widgets ────────────────────────────────────────
    private JTabbedPane tabs;
    private JLabel statusBar;

    // ── professionals tab ─────────────────────────────────────
    private DefaultTableModel profsTableModel;
    private JTable profsTable;

    // ── diary tab ────────────────────────────────────────────
    private JComboBox<HealthProfessional> diaryProfCombo;
    private DefaultTableModel diaryTableModel;
    private JTable diaryTable;
    private JLabel diarySearchTimeLabel;

    // ── search tab ───────────────────────────────────────────
    private JList<HealthProfessional> searchProfList;
    private JSpinner searchDateFrom, searchDateTo;
    private JSpinner searchDuration;
    private JTextArea searchResultArea;
    private JLabel searchTimeLabel;

    // ── tasks tab ────────────────────────────────────────────
    private JComboBox<HealthProfessional> taskProfCombo;
    private DefaultTableModel tasksTableModel;
    private JTable tasksTable;

    // ─────────────────────────────────────────────────────────
    

    public GUI() {
        super("🏥  Hospital Operation Scheduler");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 740));
        setLocationRelativeTo(null);
        getContentPane().setBackground(CLR_BG);
        buildUI();
        seedDemoData();
        refreshAllTabs();
    }

    // ─────────────────────────────────────────────────────────
    //  SEED DEMO DATA
    // ─────────────────────────────────────────────────────────
    private void seedDemoData() {
        HealthProfessional p1 = new HealthProfessional("Dr. Sarah Mitchell", "Surgeon", "Theatre 1");
        HealthProfessional p2 = new HealthProfessional("Dr. James Hartley", "Anaesthetist", "Theatre 1");
        HealthProfessional p3 = new HealthProfessional("Nurse Emily Clarke", "Nurse", "Ward 4");
        HealthProfessional p4 = new HealthProfessional("Dr. Alex Reid", "Physiotherapist", "Rehab Unit");

        state.professionals.add(p1);
        state.professionals.add(p2);
        state.professionals.add(p3);
        state.professionals.add(p4);

        for (HealthProfessional p : state.professionals) state.ensureStructures(p.getId());

        // Appointments for Dr. Mitchell
        Diary d1 = state.diaries.get(p1.getId());
        LocalDate today = LocalDate.now();
        Appointment a1 = new Appointment(today, LocalTime.of(9,0), LocalTime.of(11,0),
                "Operation", "John Smith");
        a1.getCoWorkerIds().add(p2.getId());
        Appointment a2 = new Appointment(today, LocalTime.of(14,0), LocalTime.of(15,0),
                "Consultation", "Alice Brown");
        Appointment a3 = new Appointment(today.plusDays(1), LocalTime.of(10,0), LocalTime.of(12,0),
                "Operation", "Bob Jones");
        d1.addAppointment(a1); d1.addAppointment(a2); d1.addAppointment(a3);

        // Appointments for Dr. Hartley
        Diary d2 = state.diaries.get(p2.getId());
        Appointment b1 = new Appointment(today, LocalTime.of(9,0), LocalTime.of(11,0),
                "Operation", "John Smith");
        Appointment b2 = new Appointment(today, LocalTime.of(13,0), LocalTime.of(14,0),
                "Blood Test", "Carol White");
        d2.addAppointment(b1); d2.addAppointment(b2);

        // Tasks
        LinkedList<Task> tasks1 = state.taskLists.get(p1.getId());
        tasks1.add(new Task("Review post-op notes for John Smith", Task.Priority.HIGH));
        tasks1.add(new Task("Order surgical supplies", Task.Priority.MEDIUM));
        tasks1.add(new Task("Update patient records", Task.Priority.LOW));
    }

    // ─────────────────────────────────────────────────────────
    //  BUILD UI
    // ─────────────────────────────────────────────────────────
    private void buildUI() {
        setJMenuBar(buildMenuBar());

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(CLR_BG);

        // ── title banner ──────────────────────────────────────
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(CLR_ACCENT);
        banner.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        JLabel titleLbl = new JLabel(" Hospital Operation Scheduler");
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setForeground(Color.WHITE);
        banner.add(titleLbl, BorderLayout.WEST);
        JLabel subLbl = new JLabel("IC10039 Team Project");
        subLbl.setFont(FONT_BODY);
        subLbl.setForeground(new Color(0xBBDEFB));
        banner.add(subLbl, BorderLayout.EAST);
        root.add(banner, BorderLayout.NORTH);

        // ── tabs ─────────────────────────────────────────────
        tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(FONT_HEADER);
        tabs.setBackground(CLR_BG);
        tabs.addTab(" Professionals", buildProfessionalsTab());
        tabs.addTab(" Diary",          buildDiaryTab());
        tabs.addTab(" Search",         buildSearchTab());
        tabs.addTab(" Task List",       buildTasksTab());
        root.add(tabs, BorderLayout.CENTER);

        // ── status bar ────────────────────────────────────────
        statusBar = new JLabel("  Ready");
        statusBar.setFont(FONT_BODY);
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(0xE8EAF6));
        statusBar.setForeground(CLR_TEXT);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0, new Color(0xC5CAE9)),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        root.add(statusBar, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
    }

    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.setBackground(CLR_ACCENT);

        // File
        JMenu fileMenu = styledMenu("File");
        JMenuItem saveItem = styledMenuItem(" Save to File");
        JMenuItem loadItem = styledMenuItem(" Load from File");
        JMenuItem exitItem = styledMenuItem(" Exit");
        saveItem.addActionListener(e -> saveToFile());
        loadItem.addActionListener(e -> loadFromFile());
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(saveItem); fileMenu.add(loadItem); fileMenu.addSeparator(); fileMenu.add(exitItem);

        // Edit
        JMenu editMenu = styledMenu("Edit");
        JMenuItem undoItem = styledMenuItem("↩  Undo Last Action");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> performUndo());
        editMenu.add(undoItem);

        // Help
        JMenu helpMenu = styledMenu("Help");
        JMenuItem aboutItem = styledMenuItem(" About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Hospital Operation Scheduler\nIC10039 Team Project\n\nFeatures:\n• Health professional management\n• Electronic diaries\n• Multi-professional appointment search\n• Undo / Redo\n• Task lists\n• File persistence",
            "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        mb.add(fileMenu); mb.add(editMenu); mb.add(helpMenu);
        return mb;
    }

    private JMenu styledMenu(String text) {
        JMenu m = new JMenu(text);
        m.setFont(FONT_BODY);
        m.setForeground(Color.WHITE);
        return m;
    }
    private JMenuItem styledMenuItem(String text) {
        JMenuItem i = new JMenuItem(text);
        i.setFont(FONT_BODY);
        return i;
    }

    // ─────────────────────────────────────────────────────────
    //  TAB 1 — PROFESSIONALS
    // ─────────────────────────────────────────────────────────
    private JPanel buildProfessionalsTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(CLR_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // table
        String[] cols = {"ID", "Name", "Profession", "Location"};
        profsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        profsTable = styledTable(profsTableModel);
        profsTable.getColumnModel().getColumn(0).setMaxWidth(80);

        JScrollPane scroll = new JScrollPane(profsTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xC5CAE9)));
        panel.add(styledCard(scroll, "Health Professionals"), BorderLayout.CENTER);

        // buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setBackground(CLR_BG);
        JButton addBtn  = accentBtn(" Add",   CLR_SUCCESS);
        JButton editBtn = accentBtn(" Edit",  CLR_ACCENT2);
        JButton delBtn  = accentBtn(" Delete", CLR_DANGER);

        addBtn.addActionListener(e -> showAddProfessionalDialog());
        editBtn.addActionListener(e -> showEditProfessionalDialog());
        delBtn.addActionListener(e -> deleteSelectedProfessional());

        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void showAddProfessionalDialog() {
        JTextField nameF = new JTextField(20);
        String[] professions = {"Surgeon", "Anaesthetist", "Nurse", "Physiotherapist",
                                "Radiologist", "Cardiologist", "Oncologist", "Paediatrician", "Other"};
        JComboBox<String> profCombo = new JComboBox<>(professions);
        profCombo.setEditable(true);
        JTextField locationF = new JTextField(20);

        JPanel form = formPanel(
            "Name:", nameF,
            "Profession:", profCombo,
            "Location:", locationF
        );
        int res = JOptionPane.showConfirmDialog(this, form, "Add Health Professional",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        if (nameF.getText().trim().isEmpty()) { showError("Name cannot be empty."); return; }

        HealthProfessional hp = new HealthProfessional(
                nameF.getText().trim(),
                profCombo.getSelectedItem().toString().trim(),
                locationF.getText().trim());
        state.professionals.add(hp);
        state.ensureStructures(hp.getId());
        undoManager.push(new UndoManager.UndoAction(
                UndoManager.ActionType.ADD_PROFESSIONAL, hp.getId(), null, null, hp));
        refreshAllTabs();
        status("Added professional: " + hp.getName());
    }

    private void showEditProfessionalDialog() {
        HealthProfessional hp = getSelectedProfessional();
        if (hp == null) { showError("Please select a professional to edit."); return; }

        // snapshot for undo
        HealthProfessional snapshot = new HealthProfessional(hp.getName(), hp.getProfession(), hp.getLocation());

        JTextField nameF = new JTextField(hp.getName(), 20);
        JTextField profF = new JTextField(hp.getProfession(), 20);
        JTextField locF  = new JTextField(hp.getLocation(), 20);
        JPanel form = formPanel("Name:", nameF, "Profession:", profF, "Location:", locF);
        int res = JOptionPane.showConfirmDialog(this, form, "Edit Professional",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        undoManager.push(new UndoManager.UndoAction(
                UndoManager.ActionType.EDIT_PROFESSIONAL, hp.getId(), null, snapshot, null));
        hp.setName(nameF.getText().trim());
        hp.setProfession(profF.getText().trim());
        hp.setLocation(locF.getText().trim());
        refreshAllTabs();
        status("Edited: " + hp.getName());
    }

    private void deleteSelectedProfessional() {
        HealthProfessional hp = getSelectedProfessional();
        if (hp == null) { showError("Please select a professional to delete."); return; }
        int conf = JOptionPane.showConfirmDialog(this,
                "Delete " + hp.getName() + "?\nAll their diary entries will also be removed.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf != JOptionPane.YES_OPTION) return;

        undoManager.push(new UndoManager.UndoAction(
                UndoManager.ActionType.DELETE_PROFESSIONAL, hp.getId(), null, hp, null));
        state.professionals.remove(hp);
        refreshAllTabs();
        status("Deleted: " + hp.getName());
    }

    private HealthProfessional getSelectedProfessional() {
        int row = profsTable.getSelectedRow();
        if (row < 0) return null;
        String id = (String) profsTableModel.getValueAt(row, 0);
        return state.professionals.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    // ─────────────────────────────────────────────────────────
    //  TAB 2 — DIARY
    // ─────────────────────────────────────────────────────────
    private JPanel buildDiaryTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(CLR_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topBar.setBackground(CLR_BG);
        topBar.add(styledLabel("Professional:"));
        diaryProfCombo = new JComboBox<>();
        diaryProfCombo.setFont(FONT_BODY);
        diaryProfCombo.setPreferredSize(new Dimension(260, 30));
        topBar.add(diaryProfCombo);
        diarySearchTimeLabel = new JLabel();
        diarySearchTimeLabel.setFont(FONT_MONO);
        diarySearchTimeLabel.setForeground(new Color(0x555555));
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(diarySearchTimeLabel);
        panel.add(topBar, BorderLayout.NORTH);

        // table
        String[] cols = {"ID", "Date", "Start", "End", "Treatment", "Patient", "Co-workers"};
        diaryTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        diaryTable = styledTable(diaryTableModel);
        diaryTable.getColumnModel().getColumn(0).setMaxWidth(70);
        diaryTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        diaryTable.getColumnModel().getColumn(2).setMaxWidth(70);
        diaryTable.getColumnModel().getColumn(3).setMaxWidth(70);
        diaryTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        diaryTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        diaryTable.getColumnModel().getColumn(6).setPreferredWidth(200);

        JScrollPane scroll = new JScrollPane(diaryTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xC5CAE9)));
        panel.add(styledCard(scroll, "Appointments"), BorderLayout.CENTER);

        // buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setBackground(CLR_BG);
        JButton addBtn  = accentBtn(" Add Appointment", CLR_SUCCESS);
        JButton editBtn = accentBtn(" Edit",            CLR_ACCENT2);
        JButton delBtn  = accentBtn(" Delete",           CLR_DANGER);
        JButton refreshBtn = accentBtn(" Refresh",       new Color(0x546E7A));

        addBtn.addActionListener(e -> showAddAppointmentDialog());
        editBtn.addActionListener(e -> showEditAppointmentDialog());
        delBtn.addActionListener(e -> deleteSelectedAppointment());
        refreshBtn.addActionListener(e -> refreshDiaryTable());

        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(refreshBtn);
        panel.add(btns, BorderLayout.SOUTH);

        diaryProfCombo.addActionListener(e -> refreshDiaryTable());
        return panel;
    }

    private void showAddAppointmentDialog() {
        HealthProfessional hp = (HealthProfessional) diaryProfCombo.getSelectedItem();
        if (hp == null) { showError("No professional selected."); return; }

        JSpinner dateSpinner   = dateSpinner(LocalDate.now());
        JSpinner startSpinner  = timeSpinner(LocalTime.of(9, 0));
        JSpinner endSpinner    = timeSpinner(LocalTime.of(10, 0));
        String[] types = {"Operation", "Consultation", "Blood Test", "MRI Scan",
                          "X-Ray", "Physiotherapy", "Follow-up", "Other"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setEditable(true);
        JTextField patientF = new JTextField(20);

        // co-workers list
        DefaultListModel<HealthProfessional> coModel = new DefaultListModel<>();
        state.professionals.stream()
             .filter(p -> !p.getId().equals(hp.getId()))
             .forEach(coModel::addElement);
        JList<HealthProfessional> coList = new JList<>(coModel);
        coList.setFont(FONT_BODY);
        JScrollPane coScroll = new JScrollPane(coList);
        coScroll.setPreferredSize(new Dimension(240, 80));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,6,4,6); g.anchor = GridBagConstraints.WEST;

        Object[][] rows = {
            {"Date:", dateSpinner}, {"Start Time:", startSpinner},
            {"End Time:", endSpinner}, {"Treatment Type:", typeCombo},
            {"Patient Name:", patientF}, {"Co-workers (optional):", coScroll}
        };
        for (int i = 0; i < rows.length; i++) {
            g.gridx=0; g.gridy=i; form.add(styledLabel((String)rows[i][0]), g);
            g.gridx=1; form.add((Component)rows[i][1], g);
        }

        int res = JOptionPane.showConfirmDialog(this, form,
                "Add Appointment for " + hp.getName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        if (patientF.getText().trim().isEmpty()) { showError("Patient name cannot be empty."); return; }

        LocalDate date = getDateFromSpinner(dateSpinner);
        LocalTime start = getTimeFromSpinner(startSpinner);
        LocalTime end   = getTimeFromSpinner(endSpinner);
        if (!end.isAfter(start)) { showError("End time must be after start time."); return; }

        Appointment appt = new Appointment(date, start, end,
                typeCombo.getSelectedItem().toString(), patientF.getText().trim());
        for (HealthProfessional cw : coList.getSelectedValuesList())
            appt.getCoWorkerIds().add(cw.getId());

        state.diaries.get(hp.getId()).addAppointment(appt);
        undoManager.push(new UndoManager.UndoAction(
                UndoManager.ActionType.ADD_APPOINTMENT, appt.getId(), hp.getId(), null, appt));
        refreshDiaryTable();
        status("Appointment added for " + hp.getName() + " on " + date);
    }

    private void showEditAppointmentDialog() {
        HealthProfessional hp = (HealthProfessional) diaryProfCombo.getSelectedItem();
        if (hp == null) return;
        int row = diaryTable.getSelectedRow();
        if (row < 0) { showError("Please select an appointment to edit."); return; }
        String id = (String) diaryTableModel.getValueAt(row, 0);
        Diary diary = state.diaries.get(hp.getId());
        Optional<Appointment> opt = diary.findById(id);
        if (!opt.isPresent()) return;
        Appointment appt = opt.get();

        // snapshot
        Appointment snap = new Appointment(appt.getDate(), appt.getStartTime(),
                appt.getEndTime(), appt.getTreatmentType(), appt.getPatientName());
        snap.setCoWorkerIds(new ArrayList<>(appt.getCoWorkerIds()));

        JSpinner dateSpinner  = dateSpinner(appt.getDate());
        JSpinner startSpinner = timeSpinner(appt.getStartTime());
        JSpinner endSpinner   = timeSpinner(appt.getEndTime());
        JTextField typeF    = new JTextField(appt.getTreatmentType(), 20);
        JTextField patientF = new JTextField(appt.getPatientName(), 20);

        JPanel form = formPanel(
            "Date:", dateSpinner,
            "Start Time:", startSpinner,
            "End Time:", endSpinner,
            "Treatment:", typeF,
            "Patient:", patientF
        );
        int res = JOptionPane.showConfirmDialog(this, form, "Edit Appointment",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        LocalDate date  = getDateFromSpinner(dateSpinner);
        LocalTime start = getTimeFromSpinner(startSpinner);
        LocalTime end   = getTimeFromSpinner(endSpinner);
        if (!end.isAfter(start)) { showError("End time must be after start time."); return; }

        undoManager.push(new UndoManager.UndoAction(
                UndoManager.ActionType.EDIT_APPOINTMENT, appt.getId(), hp.getId(), snap, null));
        diary.removeAppointment(appt.getId());
        appt.setDate(date); appt.setStartTime(start); appt.setEndTime(end);
        appt.setTreatmentType(typeF.getText().trim());
        appt.setPatientName(patientF.getText().trim());
        diary.addAppointment(appt);
        refreshDiaryTable();
        status("Appointment edited.");
    }

    private void deleteSelectedAppointment() {
        HealthProfessional hp = (HealthProfessional) diaryProfCombo.getSelectedItem();
        if (hp == null) return;
        int row = diaryTable.getSelectedRow();
        if (row < 0) { showError("Please select an appointment to delete."); return; }
        String id = (String) diaryTableModel.getValueAt(row, 0);

        Diary diary = state.diaries.get(hp.getId());
        Optional<Appointment> opt = diary.findById(id);
        if (!opt.isPresent()) return;
        Appointment appt = opt.get();

        int conf = JOptionPane.showConfirmDialog(this,
                "Delete appointment on " + appt.getDate() + " (" + appt.getTreatmentType() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf != JOptionPane.YES_OPTION) return;

        undoManager.push(new UndoManager.UndoAction(
                UndoManager.ActionType.DELETE_APPOINTMENT, id, hp.getId(), appt, null));
        diary.removeAppointment(id);
        refreshDiaryTable();
        status("Appointment deleted.");
    }

    private void refreshDiaryTable() {
        HealthProfessional hp = (HealthProfessional) diaryProfCombo.getSelectedItem();
        diaryTableModel.setRowCount(0);
        if (hp == null) return;

        long startNs = System.nanoTime();
        Diary diary = state.diaries.get(hp.getId());
        List<Appointment> appts = (diary != null) ? diary.getAllAppointments() : new ArrayList<>();
        long elapsed = System.nanoTime() - startNs;

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

        for (Appointment a : appts) {
            String coNames = a.getCoWorkerIds().stream()
                .map(cid -> state.professionals.stream()
                    .filter(p -> p.getId().equals(cid))
                    .findFirst().map(HealthProfessional::getName).orElse("?"))
                .collect(Collectors.joining(", "));
            diaryTableModel.addRow(new Object[]{
                a.getId(), a.getDate().format(df),
                a.getStartTime().format(tf), a.getEndTime().format(tf),
                a.getTreatmentType(), a.getPatientName(),
                coNames.isEmpty() ? "—" : coNames
            });
        }
        diarySearchTimeLabel.setText(String.format(" Search time: %.3f ms  (%d entries)",
                elapsed / 1_000_000.0, appts.size()));
    }

    // ─────────────────────────────────────────────────────────
    //  TAB 3 — SEARCH
    // ─────────────────────────────────────────────────────────
    private JPanel buildSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(CLR_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // ── left: criteria ───────────────────────────────────
        JPanel criteria = new JPanel();
        criteria.setLayout(new BoxLayout(criteria, BoxLayout.Y_AXIS));
        criteria.setBackground(CLR_PANEL);
        criteria.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xC5CAE9)),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        criteria.setPreferredSize(new Dimension(280, 0));

        criteria.add(styledLabel("Select Professionals:"));
        criteria.add(Box.createVerticalStrut(6));

        DefaultListModel<HealthProfessional> spModel = new DefaultListModel<>();
        searchProfList = new JList<>(spModel);
        searchProfList.setFont(FONT_BODY);
        searchProfList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane spScroll = new JScrollPane(searchProfList);
        spScroll.setPreferredSize(new Dimension(250, 140));
        spScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        criteria.add(spScroll);
        criteria.add(Box.createVerticalStrut(12));

        criteria.add(styledLabel("Date From:"));
        criteria.add(Box.createVerticalStrut(4));
        searchDateFrom = dateSpinner(LocalDate.now());
        searchDateFrom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        criteria.add(searchDateFrom);
        criteria.add(Box.createVerticalStrut(8));

        criteria.add(styledLabel("Date To:"));
        criteria.add(Box.createVerticalStrut(4));
        searchDateTo = dateSpinner(LocalDate.now().plusDays(7));
        searchDateTo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        criteria.add(searchDateTo);
        criteria.add(Box.createVerticalStrut(8));

        criteria.add(styledLabel("Min. Duration (minutes):"));
        criteria.add(Box.createVerticalStrut(4));
        searchDuration = new JSpinner(new SpinnerNumberModel(60, 15, 480, 15));
        searchDuration.setFont(FONT_BODY);
        searchDuration.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        criteria.add(searchDuration);
        criteria.add(Box.createVerticalStrut(16));

        JButton searchBtn = accentBtn(" Find Available Slots", CLR_ACCENT);
        searchBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchBtn.addActionListener(e -> runSearch());
        criteria.add(searchBtn);
        criteria.add(Box.createVerticalStrut(8));

        searchTimeLabel = new JLabel(" ");
        searchTimeLabel.setFont(FONT_MONO);
        searchTimeLabel.setForeground(new Color(0x555555));
        criteria.add(searchTimeLabel);

        // ── right: results ───────────────────────────────────
        searchResultArea = new JTextArea();
        searchResultArea.setEditable(false);
        searchResultArea.setFont(FONT_MONO);
        searchResultArea.setBackground(new Color(0xFAFAFA));
        JScrollPane resultScroll = new JScrollPane(searchResultArea);
        resultScroll.setBorder(BorderFactory.createLineBorder(new Color(0xC5CAE9)));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, criteria, styledCard(resultScroll, "Search Results"));
        split.setDividerLocation(300);
        split.setBackground(CLR_BG);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private void runSearch() {
        List<HealthProfessional> selected = searchProfList.getSelectedValuesList();
        if (selected.isEmpty()) { showError("Please select at least one professional."); return; }

        LocalDate from = getDateFromSpinner(searchDateFrom);
        LocalDate to   = getDateFromSpinner(searchDateTo);
        if (!to.isAfter(from.minusDays(1))) { showError("Date To must be >= Date From."); return; }
        int minMins = (Integer) searchDuration.getValue();

        long startNs = System.nanoTime();

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  AVAILABLE SLOTS  %-35s║\n", ""));
        sb.append(String.format("║  %s – %s  (min. %d min)%s║\n",
                from, to, minMins,
                " ".repeat(Math.max(0, 33 - (from.toString() + to.toString()).length()))));
        sb.append("╚══════════════════════════════════════════════════════╝\n\n");

        // Working hours: 08:00–18:00 in 30-min slots
        LocalTime workStart = LocalTime.of(8, 0);
        LocalTime workEnd   = LocalTime.of(18, 0);
        int foundSlots = 0;

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy (EEE)");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

        for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
            List<Appointment> allBusy = new ArrayList<>();
            for (HealthProfessional hp : selected) {
                Diary d = state.diaries.get(hp.getId());
                if (d != null) allBusy.addAll(d.getAppointmentsOnDate(day));
            }

            // collect free intervals on this day
            List<LocalTime[]> busyIntervals = allBusy.stream()
                .map(a -> new LocalTime[]{a.getStartTime(), a.getEndTime()})
                .sorted(Comparator.comparing(i -> i[0]))
                .collect(Collectors.toList());

            List<LocalTime[]> freeSlots = new ArrayList<>();
            LocalTime cursor = workStart;
            for (LocalTime[] busy : busyIntervals) {
                if (cursor.isBefore(busy[0])) {
                    long freeMins = java.time.Duration.between(cursor, busy[0]).toMinutes();
                    if (freeMins >= minMins) freeSlots.add(new LocalTime[]{cursor, busy[0]});
                }
                if (cursor.isBefore(busy[1])) cursor = busy[1];
            }
            if (cursor.isBefore(workEnd)) {
                long freeMins = java.time.Duration.between(cursor, workEnd).toMinutes();
                if (freeMins >= minMins) freeSlots.add(new LocalTime[]{cursor, workEnd});
            }

            if (!freeSlots.isEmpty()) {
                sb.append(String.format(" %s\n", day.format(df)));
                for (LocalTime[] slot : freeSlots) {
                    sb.append(String.format("     %s – %s  (%d min available)\n",
                            slot[0].format(tf), slot[1].format(tf),
                            java.time.Duration.between(slot[0], slot[1]).toMinutes()));
                    foundSlots++;
                }
                sb.append("\n");
            }
        }
        if (foundSlots == 0) sb.append("  ! No available slots found for the selected criteria.\n");

        long elapsed = System.nanoTime() - startNs;
        sb.append(String.format("\n──────────────────────────────────────────────────────\n"));
        sb.append(String.format("Professionals: %s\n",
                selected.stream().map(HealthProfessional::getName).collect(Collectors.joining(", "))));
        sb.append(String.format("Total slots found: %d\n", foundSlots));
        sb.append(String.format("Search completed in: %.4f ms\n", elapsed / 1_000_000.0));

        searchResultArea.setText(sb.toString());
        searchResultArea.setCaretPosition(0);
        searchTimeLabel.setText(String.format(" %.4f ms", elapsed / 1_000_000.0));
        status("Search complete – " + foundSlots + " slot(s) found.");
    }

    // ─────────────────────────────────────────────────────────
    //  TAB 4 — TASKS
    // ─────────────────────────────────────────────────────────
    private JPanel buildTasksTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(CLR_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topBar.setBackground(CLR_BG);
        topBar.add(styledLabel("Professional:"));
        taskProfCombo = new JComboBox<>();
        taskProfCombo.setFont(FONT_BODY);
        taskProfCombo.setPreferredSize(new Dimension(260, 30));
        topBar.add(taskProfCombo);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Priority", "Description", "Done"};
        tasksTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tasksTable = styledTable(tasksTableModel);
        tasksTable.getColumnModel().getColumn(0).setMaxWidth(70);
        tasksTable.getColumnModel().getColumn(1).setMaxWidth(90);
        tasksTable.getColumnModel().getColumn(3).setMaxWidth(60);

        // colour rows by priority
        tasksTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                    boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String prio = (String) tasksTableModel.getValueAt(row, 1);
                    if ("HIGH".equals(prio))        setBackground(new Color(0xFFEBEE));
                    else if ("MEDIUM".equals(prio)) setBackground(new Color(0xFFFDE7));
                    else                             setBackground(new Color(0xE8F5E9));
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tasksTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xC5CAE9)));
        panel.add(styledCard(scroll, "Task List"), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setBackground(CLR_BG);
        JButton addBtn    = accentBtn(" Add Task",       CLR_SUCCESS);
        JButton editBtn   = accentBtn(" Edit",           CLR_ACCENT2);
        JButton delBtn    = accentBtn(" Delete",          CLR_DANGER);
        JButton toggleBtn = accentBtn(" Toggle Done",     new Color(0x546E7A));

        addBtn.addActionListener(e -> showAddTaskDialog());
        editBtn.addActionListener(e -> showEditTaskDialog());
        delBtn.addActionListener(e -> deleteSelectedTask());
        toggleBtn.addActionListener(e -> toggleTaskDone());

        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(toggleBtn);
        panel.add(btns, BorderLayout.SOUTH);

        taskProfCombo.addActionListener(e -> refreshTasksTable());
        return panel;
    }

    private void showAddTaskDialog() {
        HealthProfessional hp = (HealthProfessional) taskProfCombo.getSelectedItem();
        if (hp == null) { showError("No professional selected."); return; }

        JTextField descF = new JTextField(30);
        JComboBox<Task.Priority> prioCombo = new JComboBox<>(Task.Priority.values());

        JPanel form = formPanel("Description:", descF, "Priority:", prioCombo);
        int res = JOptionPane.showConfirmDialog(this, form, "Add Task",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        if (descF.getText().trim().isEmpty()) { showError("Description cannot be empty."); return; }

        Task task = new Task(descF.getText().trim(), (Task.Priority) prioCombo.getSelectedItem());
        state.taskLists.get(hp.getId()).add(task);
        undoManager.push(new UndoManager.UndoAction(
                UndoManager.ActionType.ADD_TASK, task.getId(), hp.getId(), null, task));
        refreshTasksTable();
        status("Task added.");
    }

    private void showEditTaskDialog() {
        HealthProfessional hp = (HealthProfessional) taskProfCombo.getSelectedItem();
        if (hp == null) return;
        int row = tasksTable.getSelectedRow();
        if (row < 0) { showError("Select a task to edit."); return; }
        String id = (String) tasksTableModel.getValueAt(row, 0);
        LinkedList<Task> tasks = state.taskLists.get(hp.getId());
        Task task = tasks.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
        if (task == null) return;

        JTextField descF = new JTextField(task.getDescription(), 30);
        JComboBox<Task.Priority> prioCombo = new JComboBox<>(Task.Priority.values());
        prioCombo.setSelectedItem(task.getPriority());

        JPanel form = formPanel("Description:", descF, "Priority:", prioCombo);
        int res = JOptionPane.showConfirmDialog(this, form, "Edit Task",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        task.setDescription(descF.getText().trim());
        task.setPriority((Task.Priority) prioCombo.getSelectedItem());
        refreshTasksTable();
        status("Task updated.");
    }

    private void deleteSelectedTask() {
        HealthProfessional hp = (HealthProfessional) taskProfCombo.getSelectedItem();
        if (hp == null) return;
        int row = tasksTable.getSelectedRow();
        if (row < 0) { showError("Select a task to delete."); return; }
        String id = (String) tasksTableModel.getValueAt(row, 0);
        LinkedList<Task> tasks = state.taskLists.get(hp.getId());
        tasks.removeIf(t -> t.getId().equals(id));
        refreshTasksTable();
        status("Task deleted.");
    }

    private void toggleTaskDone() {
        HealthProfessional hp = (HealthProfessional) taskProfCombo.getSelectedItem();
        if (hp == null) return;
        int row = tasksTable.getSelectedRow();
        if (row < 0) return;
        String id = (String) tasksTableModel.getValueAt(row, 0);
        state.taskLists.get(hp.getId()).stream()
             .filter(t -> t.getId().equals(id))
             .findFirst().ifPresent(t -> t.setDone(!t.isDone()));
        refreshTasksTable();
    }

    private void refreshTasksTable() {
        HealthProfessional hp = (HealthProfessional) taskProfCombo.getSelectedItem();
        tasksTableModel.setRowCount(0);
        if (hp == null) return;
        LinkedList<Task> tasks = state.taskLists.get(hp.getId());
        if (tasks == null) return;
        // sort: HIGH first, then MEDIUM, LOW
        tasks.stream()
             .sorted(Comparator.comparingInt(t -> t.getPriority().ordinal()))
             .forEach(t -> tasksTableModel.addRow(new Object[]{
                 t.getId(), t.getPriority().toString(),
                 t.isDone() ? "✓ " + t.getDescription() : t.getDescription(),
                 t.isDone() ? "✓" : ""
             }));
    }

    // ─────────────────────────────────────────────────────────
    //  UNDO
    // ─────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private void performUndo() {
        if (!undoManager.canUndo()) { showError("Nothing to undo."); return; }
        UndoManager.UndoAction action = undoManager.pop();

        switch (action.type) {
            case ADD_PROFESSIONAL: {
                state.professionals.removeIf(p -> p.getId().equals(action.targetId));
                break;
            }
            case DELETE_PROFESSIONAL: {
                HealthProfessional hp = (HealthProfessional) action.before;
                state.professionals.add(hp);
                state.ensureStructures(hp.getId());
                break;
            }
            case EDIT_PROFESSIONAL: {
                HealthProfessional snap = (HealthProfessional) action.before;
                state.professionals.stream()
                     .filter(p -> p.getId().equals(action.targetId)).findFirst()
                     .ifPresent(p -> {
                         p.setName(snap.getName());
                         p.setProfession(snap.getProfession());
                         p.setLocation(snap.getLocation());
                     });
                break;
            }
            case ADD_APPOINTMENT: {
                Diary d = state.diaries.get(action.professionalId);
                if (d != null) d.removeAppointment(action.targetId);
                break;
            }
            case DELETE_APPOINTMENT: {
                Diary d = state.diaries.get(action.professionalId);
                if (d != null) d.addAppointment((Appointment) action.before);
                break;
            }
            case EDIT_APPOINTMENT: {
                Diary d = state.diaries.get(action.professionalId);
                if (d != null) {
                    d.removeAppointment(action.targetId);
                    d.addAppointment((Appointment) action.before);
                }
                break;
            }
            case ADD_TASK: {
                LinkedList<Task> tasks = state.taskLists.get(action.professionalId);
                if (tasks != null) tasks.removeIf(t -> t.getId().equals(action.targetId));
                break;
            }
            case DELETE_TASK: {
                LinkedList<Task> tasks = state.taskLists.get(action.professionalId);
                if (tasks != null) tasks.add((Task) action.before);
                break;
            }
        }
        refreshAllTabs();
        status("Undo: " + action.type);
    }

    // ─────────────────────────────────────────────────────────
    //  FILE I/O
    // ─────────────────────────────────────────────────────────
    private void saveToFile() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("hospital_scheduler.dat"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(fc.getSelectedFile()))) {
            oos.writeObject(state);
            status("Saved to " + fc.getSelectedFile().getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Data saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            showError("Save failed: " + ex.getMessage());
        }
    }

    private void loadFromFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(fc.getSelectedFile()))) {
            AppState loaded = (AppState) ois.readObject();
            state.professionals.clear();
            state.professionals.addAll(loaded.professionals);
            state.diaries.clear();
            state.diaries.putAll(loaded.diaries);
            state.taskLists.clear();
            state.taskLists.putAll(loaded.taskLists);
            refreshAllTabs();
            status("Loaded from " + fc.getSelectedFile().getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Data loaded successfully!", "Loaded", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError("Load failed: " + ex.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  REFRESH HELPERS
    // ─────────────────────────────────────────────────────────
    private void refreshAllTabs() {
        refreshProfsTable();
        refreshProfCombos();
        refreshDiaryTable();
        refreshTasksTable();
        refreshSearchList();
    }

    private void refreshProfsTable() {
        profsTableModel.setRowCount(0);
        for (HealthProfessional hp : state.professionals)
            profsTableModel.addRow(new Object[]{hp.getId(), hp.getName(), hp.getProfession(), hp.getLocation()});
    }

    private void refreshProfCombos() {
        HealthProfessional selDiary = (HealthProfessional) diaryProfCombo.getSelectedItem();
        HealthProfessional selTask  = (HealthProfessional) taskProfCombo.getSelectedItem();

        diaryProfCombo.removeAllItems();
        taskProfCombo.removeAllItems();
        for (HealthProfessional hp : state.professionals) {
            diaryProfCombo.addItem(hp);
            taskProfCombo.addItem(hp);
        }
        if (selDiary != null) diaryProfCombo.setSelectedItem(selDiary);
        if (selTask  != null) taskProfCombo.setSelectedItem(selTask);
    }

    private void refreshSearchList() {
        DefaultListModel<HealthProfessional> model =
                (DefaultListModel<HealthProfessional>) searchProfList.getModel();
        List<HealthProfessional> prev = searchProfList.getSelectedValuesList();
        model.clear();
        for (HealthProfessional hp : state.professionals) model.addElement(hp);

        // reselect previously selected
        for (int i = 0; i < model.size(); i++) {
            HealthProfessional modelHp = model.get(i);
            for (HealthProfessional p : prev) {
                if (p.getId().equals(modelHp.getId())) {
                    searchProfList.addSelectionInterval(i, i);
                    break;
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    //  UI HELPERS
    // ─────────────────────────────────────────────────────────
    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(FONT_BODY);
        t.setRowHeight(26);
        t.setGridColor(new Color(0xE0E0E0));
        t.setShowGrid(true);
        t.setSelectionBackground(new Color(0xBBDEFB));
        t.setSelectionForeground(CLR_TEXT);
        t.getTableHeader().setFont(FONT_HEADER);
        t.getTableHeader().setBackground(CLR_TABLE_HDR);
        t.getTableHeader().setForeground(CLR_ACCENT);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) setBackground(row % 2 == 0 ? CLR_PANEL : CLR_TABLE_ALT);
                setFont(FONT_BODY);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return this;
            }
        });
        return t;
    }

    private JButton accentBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY);
        b.setBackground(bg);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker()),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(CLR_TEXT);
        return l;
    }

    private JPanel styledCard(JComponent inner, String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CLR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xC5CAE9)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        if (title != null && !title.isEmpty()) {
            JLabel titleLbl = new JLabel("  " + title);
            titleLbl.setFont(FONT_HEADER);
            titleLbl.setForeground(CLR_ACCENT);
            titleLbl.setOpaque(true);
            titleLbl.setBackground(CLR_TABLE_HDR);
            titleLbl.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            card.add(titleLbl, BorderLayout.NORTH);
        }
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    @SuppressWarnings("unchecked")
    private JPanel formPanel(Object... pairs) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < pairs.length; i += 2) {
            g.gridx = 0; g.gridy = i / 2; g.fill = GridBagConstraints.NONE;
            p.add(styledLabel((String) pairs[i]), g);
            g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL;
            p.add((Component) pairs[i + 1], g);
        }
        return p;
    }

    private JSpinner dateSpinner(LocalDate initial) {
        SpinnerDateModel model = new SpinnerDateModel(
            java.sql.Date.valueOf(initial), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner s = new JSpinner(model);
        s.setFont(FONT_BODY);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(s, "dd/MM/yyyy");
        editor.getTextField().setFont(FONT_BODY);
        s.setEditor(editor);
        return s;
    }

    private JSpinner timeSpinner(LocalTime initial) {
        SpinnerDateModel model = new SpinnerDateModel(
            java.sql.Time.valueOf(initial), null, null, java.util.Calendar.MINUTE);
        JSpinner s = new JSpinner(model);
        s.setFont(FONT_BODY);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(s, "HH:mm");
        editor.getTextField().setFont(FONT_BODY);
        s.setEditor(editor);
        return s;
    }

    private LocalDate getDateFromSpinner(JSpinner s) {
        java.util.Date d = (java.util.Date) s.getValue();
        return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    private LocalTime getTimeFromSpinner(JSpinner s) {
        java.util.Date d = (java.util.Date) s.getValue();
        return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime()
               .withSecond(0).withNano(0);
    }

    private void status(String msg) {
        statusBar.setText("  " + msg);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}