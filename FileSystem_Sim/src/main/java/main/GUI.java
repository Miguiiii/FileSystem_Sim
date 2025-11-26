package main;

import OS_Structures.*;
import OS_Structures.Process; 
import Structures.List; 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout; 
import java.awt.GridBagConstraints; 
import java.awt.GridBagLayout;    
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader; 
import java.io.BufferedWriter; 
import java.io.FileReader; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.text.SimpleDateFormat; 
import java.util.ArrayList; 
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box; 
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser; 
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea; 
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities; 
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter; 
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class GUI extends javax.swing.JPanel {

    private FileSystem fileSystem;
    private User currentUser;
    private java.util.List<User> userList; 
    private Timer refreshTimer; 
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private DefaultTableModel diskTableModel;   
    private DefaultTableModel filesTableModel;  
    private JTable jTableDiskView;
    private JTable jTableFiles;
    private JTree jTree1;
    private JLabel lblFreeSpace;
    private JLabel lblHeadPosition;
    private JComboBox<DISK_SCHEDULE> comboPolicy;
    private JTextArea txtReadMonitor; 
    private JComboBox<String> comboUsers;
    private JTextField txtNewUser;
    private JButton btnCreateUser;
    private JTextField txtCreateName;
    private JComboBox<String> comboLocation; 
    private JSpinner spinnerSize;
    private JButton btnCreate;
    private JButton btnCreate10; 
    private JRadioButton radioFile, radioFolder;
    private javax.swing.ButtonGroup btnGroupCreate;
    private JRadioButton radioRead, radioModify, radioDelete;
    private javax.swing.ButtonGroup btnGroupOperate;
    private JRadioButton radioOpTargetFile, radioOpTargetFolder; 
    private javax.swing.ButtonGroup btnGroupOpTarget;
    private JComboBox<String> comboOpLocation; 
    private JComboBox<String> comboOpItem;     
    private JButton btnOperate;
    private JTextField txtNewName;
    private JLabel lblNewName; 
    private JButton btnSaveCSV; 
    
    public GUI() {
        userList = new ArrayList<>();
        User admin = new User("admin", true); 
        userList.add(admin);
        currentUser = admin;
        showStartupDialog();
        initComponentsCustom();
        setupListeners(); 
        setupModels();
        updateCombos();
        refreshUserCombo();
        updateOperationsUI();
        startRefresher();
    }
    
    private void refreshUserCombo() {
        if (comboUsers != null) {
            comboUsers.removeAllItems();
            for(User u : userList) comboUsers.addItem(u.getName());
            comboUsers.setSelectedItem(currentUser.getName());
        }
    }
    
    private void showStartupDialog() {
        JPanel panelStart = new JPanel(new GridLayout(0, 1, 10, 10));
        JLabel lblTitle = new JLabel("Configuración Inicial");
        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel panelNew = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSize = new JLabel("Tamaño Disco: ");
        JSpinner spinSize = new JSpinner(new SpinnerNumberModel(512, 10, 10000, 1));
        panelNew.add(lblSize);
        panelNew.add(spinSize);
        
        JButton btnLoad = new JButton("Cargar CSV...");
        btnLoad.addActionListener(e -> {
            java.awt.Window w = SwingUtilities.getWindowAncestor(btnLoad);
            if (w != null) w.dispose();
            loadFromCSV(); 
        });

        panelStart.add(lblTitle);
        panelStart.add(new JLabel("Opción A: Nuevo Sistema"));
        panelStart.add(panelNew);
        panelStart.add(new javax.swing.JSeparator());
        panelStart.add(new JLabel("Opción B: Cargar Respaldo"));
        panelStart.add(btnLoad);

        int result = JOptionPane.showConfirmDialog(this, panelStart, "Sistema de Archivos", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (fileSystem == null) {
            int size = 512;
            if (result == JOptionPane.OK_OPTION) {
                try {
                    spinSize.commitEdit();
                } catch (Exception e) {}
                size = (Integer) spinSize.getValue();
            }
            fileSystem = new FileSystem(size);
            fileSystem.boot();
        }
    }

    private void initComponentsCustom() {
        setLayout(new BorderLayout(5, 5)); 
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel panelDisk = new JPanel(new BorderLayout());
        panelDisk.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        jTableDiskView = new JTable();
        JScrollPane scrollDisk = new JScrollPane(jTableDiskView);
        scrollDisk.setBorder(BorderFactory.createTitledBorder("Mapa de Bloques (SD)"));
        panelDisk.add(scrollDisk, BorderLayout.CENTER);
        
        JPanel panelDiskInfo = new JPanel();
        panelDiskInfo.add(new JLabel("Estado: En Línea | Espacio Libre: "));
        lblFreeSpace = new JLabel("Calculando...");
        panelDiskInfo.add(lblFreeSpace);
        panelDisk.add(panelDiskInfo, BorderLayout.SOUTH);

        jTree1 = new JTree();
        JScrollPane scrollTree = new JScrollPane(jTree1);
        scrollTree.setBorder(BorderFactory.createTitledBorder("Árbol de Directorios"));
        scrollTree.setMinimumSize(new Dimension(150, 100));
        
        jTableFiles = new JTable();
        JScrollPane scrollFiles = new JScrollPane(jTableFiles);
        scrollFiles.setBorder(BorderFactory.createTitledBorder("Lista Global de Archivos"));
        
        JSplitPane splitFiles = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTree, scrollFiles);
        splitFiles.setDividerLocation(180); 
        splitFiles.setResizeWeight(0.3);
        splitFiles.setOneTouchExpandable(true);

        JPanel panelOptions = new JPanel(new BorderLayout());
        panelOptions.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextArea txtInfoOptions = new JTextArea();
        txtInfoOptions.setText("OPCIONES DEL SISTEMA\n\n"
                + "Utilice el botón inferior para guardar un reporte completo del estado actual.\n"
                + "El reporte incluirá:\n"
                + "1. Estado del Disco y Políticas.\n"
                + "2. Estructura completa de archivos y carpetas.\n"
                + "3. Bitácora (Log) de operaciones realizadas.");
        txtInfoOptions.setEditable(false);
        txtInfoOptions.setOpaque(false);
        txtInfoOptions.setFont(new java.awt.Font("Segoe UI", 0, 14));
        panelOptions.add(txtInfoOptions, BorderLayout.CENTER);
        
        JPanel panelBottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSaveCSV = new JButton("Guardar Reporte (CSV)");
        btnSaveCSV.addActionListener(e -> actionSave());
        btnSaveCSV.setBackground(new Color(220, 255, 220)); 
        
        panelBottomRight.add(btnSaveCSV);
        panelOptions.add(panelBottomRight, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Disco (SD)", panelDisk);
        tabbedPane.addTab("Explorador", splitFiles);
        tabbedPane.addTab("Opciones", panelOptions); 
        
        add(tabbedPane, BorderLayout.CENTER);
        JPanel panelLeftContainer = new JPanel(new GridBagLayout());
        panelLeftContainer.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panelLeftContainer.setPreferredSize(new Dimension(260, 0)); 
        
        GridBagConstraints gbcLSpacer = new GridBagConstraints();
        gbcLSpacer.gridx = 0; gbcLSpacer.gridy = 0;
        gbcLSpacer.weighty = 0.05; 
        gbcLSpacer.fill = GridBagConstraints.BOTH;
        panelLeftContainer.add(Box.createVerticalStrut(30), gbcLSpacer);
 
        JPanel panelPolicies = createCompactPanel("Políticas de Disco");
        comboPolicy = new JComboBox<>(DISK_SCHEDULE.values());
        comboPolicy.addActionListener(e -> actionChangePolicy());
        lblHeadPosition = new JLabel("Cabezal: Sector 0");
        lblHeadPosition.setFont(new java.awt.Font("Monospaced", 1, 12));
        lblHeadPosition.setForeground(new Color(0, 100, 200));

        addCompactRow(panelPolicies, new JLabel("Algoritmo:"));
        addCompactRow(panelPolicies, comboPolicy);
        addCompactRow(panelPolicies, new JLabel("Monitoreo:"));
        addCompactRow(panelPolicies, lblHeadPosition);
        
        GridBagConstraints gbcLeftPol = new GridBagConstraints();
        gbcLeftPol.gridx = 0; gbcLeftPol.gridy = 1;
        gbcLeftPol.fill = GridBagConstraints.HORIZONTAL;
        gbcLeftPol.weightx = 1.0;
        gbcLeftPol.insets = new Insets(0, 0, 10, 0);
        gbcLeftPol.anchor = GridBagConstraints.NORTH;
        panelLeftContainer.add(panelPolicies, gbcLeftPol);
 
        JPanel panelReadMonitor = new JPanel(new BorderLayout());
        panelReadMonitor.setBorder(BorderFactory.createTitledBorder("Log de Operaciones"));
        
        txtReadMonitor = new JTextArea();
        txtReadMonitor.setEditable(false);
        txtReadMonitor.setFont(new java.awt.Font("Monospaced", 0, 11));
        txtReadMonitor.setBackground(new Color(245, 245, 245));
        txtReadMonitor.setText("=== BITÁCORA DEL SISTEMA ===\n");
        txtReadMonitor.setLineWrap(true);
        txtReadMonitor.setWrapStyleWord(true);
        
        JScrollPane scrollMonitor = new JScrollPane(txtReadMonitor);
        scrollMonitor.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelReadMonitor.add(scrollMonitor, BorderLayout.CENTER);
        
        GridBagConstraints gbcLeftMon = new GridBagConstraints();
        gbcLeftMon.gridx = 0; gbcLeftMon.gridy = 2;
        gbcLeftMon.fill = GridBagConstraints.BOTH; 
        gbcLeftMon.weightx = 1.0;
        gbcLeftMon.weighty = 1.0; 
        panelLeftContainer.add(panelReadMonitor, gbcLeftMon);

        add(panelLeftContainer, BorderLayout.WEST);

        JPanel panelRightContainer = new JPanel(new GridBagLayout());
        panelRightContainer.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        GridBagConstraints gbcRSpacer = new GridBagConstraints();
        gbcRSpacer.gridx = 0; gbcRSpacer.gridy = 0;
        gbcRSpacer.weighty = 0.05; 
        gbcRSpacer.fill = GridBagConstraints.BOTH;
        panelRightContainer.add(Box.createVerticalStrut(30), gbcRSpacer);

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = GridBagConstraints.RELATIVE;
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        gbcMain.weightx = 1.0;
        gbcMain.insets = new Insets(0, 0, 20, 0); 
        gbcMain.anchor = GridBagConstraints.NORTH;

        JPanel panelUsers = createCompactPanel("Gestión de Usuarios");
        comboUsers = new JComboBox<>();
        comboUsers.addItem("admin");
        comboUsers.addActionListener(e -> actionSelectUser());
        txtNewUser = new JTextField("");
        btnCreateUser = new JButton("Crear Usuario");
        btnCreateUser.addActionListener(e -> actionCreateUser());
        
        addCompactRow(panelUsers, new JLabel("Usuario Activo:"));
        addCompactRow(panelUsers, comboUsers);
        addCompactRow(panelUsers, new JLabel("Nuevo Usuario:"));
        addCompactRow(panelUsers, txtNewUser);
        addCompactRow(panelUsers, btnCreateUser);

        JPanel panelCreate = createCompactPanel("Crear Archivo/Carpeta");
        btnGroupCreate = new javax.swing.ButtonGroup();
        radioFile = new JRadioButton("Archivo", true);
        radioFolder = new JRadioButton("Carpeta");
        btnGroupCreate.add(radioFile); btnGroupCreate.add(radioFolder);
        
        JPanel typePanel = new JPanel(new GridLayout(1, 2));
        typePanel.add(radioFile); typePanel.add(radioFolder);
        
        txtCreateName = new JTextField("NuevoDoc");
        comboLocation = new JComboBox<>(); 
        spinnerSize = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); 
        btnCreate = new JButton("Crear Elemento");
        btnCreate.addActionListener(e -> actionCreate());
        btnCreate10 = new JButton("Crear 10 (Auto)");
        btnCreate10.setBackground(new Color(230, 240, 255));
        btnCreate10.addActionListener(e -> actionCreateBatch10());

        addCompactRow(panelCreate, new JLabel("Tipo:"));
        addCompactRow(panelCreate, typePanel);
        addCompactRow(panelCreate, new JLabel("Nombre:"));
        addCompactRow(panelCreate, txtCreateName);
        addCompactRow(panelCreate, new JLabel("Tamaño (Bloques):"));
        addCompactRow(panelCreate, spinnerSize);
        addCompactRow(panelCreate, new JLabel("Carpeta Padre:"));
        addCompactRow(panelCreate, comboLocation); 
        addCompactRow(panelCreate, btnCreate);
        addCompactRow(panelCreate, btnCreate10); 

        JPanel panelOperate = createCompactPanel("Operaciones");
        btnGroupOperate = new javax.swing.ButtonGroup();
        radioRead = new JRadioButton("Leer", true);
        radioModify = new JRadioButton("Modif");
        radioDelete = new JRadioButton("Elim");
        btnGroupOperate.add(radioRead); btnGroupOperate.add(radioModify); btnGroupOperate.add(radioDelete);
        
        JPanel opModePanel = new JPanel(new GridLayout(1, 3));
        opModePanel.add(radioRead); opModePanel.add(radioModify); opModePanel.add(radioDelete);
        
        btnGroupOpTarget = new javax.swing.ButtonGroup();
        radioOpTargetFile = new JRadioButton("Archivo", true);
        radioOpTargetFolder = new JRadioButton("Carpeta");
        btnGroupOpTarget.add(radioOpTargetFile); btnGroupOpTarget.add(radioOpTargetFolder);
        
        JPanel opTargetTypePanel = new JPanel(new GridLayout(1, 2));
        opTargetTypePanel.add(radioOpTargetFile); opTargetTypePanel.add(radioOpTargetFolder);
        
        comboOpLocation = new JComboBox<>(); 
        comboOpItem = new JComboBox<>();     
        txtNewName = new JTextField("");
        lblNewName = new JLabel("Nuevo Nombre:");
        btnOperate = new JButton("Ejecutar Acción");
        btnOperate.addActionListener(e -> actionOperate());

        addCompactRow(panelOperate, new JLabel("Modo:"));
        addCompactRow(panelOperate, opModePanel);
        addCompactRow(panelOperate, new JLabel("Objetivo:"));
        addCompactRow(panelOperate, opTargetTypePanel);
        addCompactRow(panelOperate, new JLabel("Ubicación:"));
        addCompactRow(panelOperate, comboOpLocation);
        addCompactRow(panelOperate, new JLabel("Item:"));
        addCompactRow(panelOperate, comboOpItem);
        addCompactRow(panelOperate, lblNewName);
        addCompactRow(panelOperate, txtNewName); 
        addCompactRow(panelOperate, btnOperate); 

        panelRightContainer.add(panelUsers, gbcMain);
        panelRightContainer.add(panelCreate, gbcMain);
        panelRightContainer.add(panelOperate, gbcMain);
        
        GridBagConstraints gbcRFill = new GridBagConstraints();
        gbcRFill.gridx = 0; gbcRFill.weighty = 0.95; 
        gbcRFill.fill = GridBagConstraints.VERTICAL;
        panelRightContainer.add(new JPanel(), gbcRFill);
        
        JScrollPane scrollRight = new JScrollPane(panelRightContainer);
        scrollRight.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollRight.setBorder(null); 
        scrollRight.setPreferredSize(new Dimension(300, 0)); 
        
        add(scrollRight, BorderLayout.EAST);
    }
    
    private JPanel createCompactPanel(String title) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        return p;
    }
    
    private void addCompactRow(JPanel panel, JComponent comp) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(1, 2, 1, 2); 
        panel.add(comp, gbc);
    }

    private void loadFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Cargar Reporte CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                
                String line;
                int diskSize = 512; 
                String savedLog = "";
                boolean readingLog = false;
                ArrayList<String[]> fileLines = new ArrayList<>();
                
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    
                    if (line.startsWith("=== LOG")) {
                        readingLog = true;
                        continue;
                    }
                    if (readingLog) {
                        savedLog += line + "\n";
                        continue;
                    }
                    if (line.startsWith("Espacio Libre")) {
                        diskSize = 2048; 
                    }
                    
                    if (line.contains(",")) {
                        String[] parts = line.split(",");
                        if (parts.length >= 6 && isNumeric(parts[4])) {
                            fileLines.add(parts);
                        }
                    }
                }
                
                if(refreshTimer != null) refreshTimer.stop();
                
                this.fileSystem = new FileSystem(diskSize);
                this.fileSystem.boot();
                
                userList.clear();
                userList.add(new User("admin", true)); 
                currentUser = userList.get(0); 
                
                fileLines.sort((a, b) -> a[0].length() - b[0].length());
                
                for (String[] parts : fileLines) {
                    try {
                        String path = parts[0].trim();
                        String name = parts[1].trim();
                        String type = parts[2].trim();
                        String ownerStr = parts[3].trim();
                        int size = Integer.parseInt(parts[4].trim());
                        String dirStr = parts[5].trim();
                        int startDir = (dirStr.equals("-") || dirStr.isEmpty()) ? -1 : Integer.parseInt(dirStr);
                        
                        Folder parent = findFolderByPath(path);
                        if (parent == null) parent = fileSystem.getDisk().getRoot();
                        
                        User u = findUserByName(ownerStr);
                        if (u == null) {
                            u = new User(ownerStr, false);
                            userList.add(u);
                        }
                        
                        if (type.equalsIgnoreCase("Carpeta")) {
                            Folder f = new Folder(name, u, parent);
                            parent.saveElement(f);
                        } else {
                            restoreFileDirectly(name, u, size, parent, startDir);
                        }
                    } catch (Exception e) {
                        System.out.println("Skipping line: " + e.getMessage());
                    }
                }
                
                if (txtReadMonitor != null) {
                    txtReadMonitor.setText(savedLog);
                    refreshUserCombo();
                    setupModels(); 
                }
                
                if(refreshTimer != null) refreshTimer.start();
                
                JOptionPane.showMessageDialog(null, "Sistema restaurado correctamente.");
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error crítico cargando CSV: " + e.getMessage());
                fileSystem = new FileSystem(512);
                fileSystem.boot();
                if(refreshTimer != null) refreshTimer.start();
            }
        }
    }
    
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void restoreFileDirectly(String name, User owner, int size, Folder parent, int startDir) {
        OS_Structures.File f = new OS_Structures.File(name, owner, size, parent);
        parent.saveElement(f);
        
        if (startDir >= 0) {
            Block[] memory = fileSystem.getDisk().getMemory();
            if (startDir < memory.length) {
                f.setHeadDir(memory[startDir]);
            }
            Block prev = null;
            for (int i = 0; i < size; i++) {
                int targetIdx = startDir + i;
                if (targetIdx < memory.length) {
                    Block current = memory[targetIdx];
                    current.fillBlock(); 
                    if (prev != null) prev.setNext(current);
                    prev = current;
                }
            }
        }
    }
    
    private Folder findFolderByPath(String path) {
        if (path.equals("ROOT") || path.isEmpty()) return fileSystem.getDisk().getRoot();
        String[] parts = path.split("/");
        Folder current = fileSystem.getDisk().getRoot();
        for (int i = 1; i < parts.length; i++) {
            String folderName = parts[i];
            DiskElement de = current.getContents().getValueOfKey(folderName);
            if (de != null && !de.isFile()) {
                current = (Folder) de;
            } else {
                return current; 
            }
        }
        return current;
    }
    
    private User findUserByName(String name) {
        for(User u : userList) if(u.getName().equals(name)) return u;
        return null;
    }

    private void actionSave() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Sistema");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".csv");
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                
                writer.write("=== ESTADO DEL SISTEMA ==="); writer.newLine();
                writer.write("Fecha," + sdf.format(new Date())); writer.newLine();
                writer.write("Usuario Actual," + currentUser.getName()); writer.newLine();
                writer.write("Politica Disco," + comboPolicy.getSelectedItem()); writer.newLine();
                writer.write("Espacio Libre," + fileSystem.getDisk().getFreeSpace()); writer.newLine();
                writer.newLine();
                
                writer.write("=== SISTEMA DE ARCHIVOS ==="); writer.newLine();
                writer.write("RutaPadre,Nombre,Tipo,Dueño,Tamaño,DirInicio"); writer.newLine();
                writeFolderToCSV(writer, fileSystem.getDisk().getRoot(), "ROOT");
                writer.newLine();
                
                writer.write("=== LOG DE OPERACIONES ==="); writer.newLine();
                writer.write(txtReadMonitor.getText());
                
                JOptionPane.showMessageDialog(this, "Guardado en:\n" + fileToSave.getName());
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
            }
        }
    }
    
    private void writeFolderToCSV(BufferedWriter writer, Folder current, String currentPath) throws IOException {
        if (current == null) return;
        List<DiskElement> contents = current.getContents().getValues();
        if (contents == null) return;
        
        for (DiskElement de : contents) {
            String type = de.isFile() ? "Archivo" : "Carpeta";
            String owner = (de.getOwner() != null) ? de.getOwner().getName() : "Sys";
            String size = "0";
            String startDir = "-";
            
            if (de.isFile()) {
                OS_Structures.File f = (OS_Structures.File) de;
                size = String.valueOf(f.getSize());
                startDir = String.valueOf(f.getFileDir());
            }
            
            writer.write(currentPath + "," + de.getName() + "," + type + "," + owner + "," + size + "," + startDir);
            writer.newLine();
            
            if (!de.isFile()) {
                writeFolderToCSV(writer, (Folder) de, currentPath + "/" + de.getName());
            }
        }
    }

    private void logToMonitor(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());
        
        SwingUtilities.invokeLater(() -> {
            if (txtReadMonitor != null) {
                txtReadMonitor.append("[" + time + "] " + message + "\n");
                txtReadMonitor.append("----------------------------\n");
                txtReadMonitor.setCaretPosition(txtReadMonitor.getDocument().getLength());
            }
        });
    }

    private void actionChangePolicy() {
        if (!currentUser.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Solo el administrador puede cambiar las políticas.");
            return;
        }
        DISK_SCHEDULE selected = (DISK_SCHEDULE) comboPolicy.getSelectedItem();
        logToMonitor("Política cambiada a: " + selected);
    }

    private void setupListeners() {
        ActionListener typeListener = e -> {
            spinnerSize.setEnabled(!radioFolder.isSelected());
            btnCreate10.setEnabled(!radioFolder.isSelected());
        };
        radioFile.addActionListener(typeListener);
        radioFolder.addActionListener(typeListener);
        
        ActionListener opTypeListener = e -> updateOpTargetItems();
        radioOpTargetFile.addActionListener(opTypeListener);
        radioOpTargetFolder.addActionListener(opTypeListener);
        comboOpLocation.addActionListener(e -> updateOpTargetItems());
        
        ActionListener modeListener = e -> updateOperationsUI();
        radioRead.addActionListener(modeListener);
        radioModify.addActionListener(modeListener);
        radioDelete.addActionListener(modeListener);
    }
    
    private void updateOperationsUI() {
        boolean isAdmin = currentUser.isAdmin();
        boolean isModify = radioModify.isSelected();
        
        if (!isAdmin) {
            radioModify.setEnabled(false);
            radioDelete.setEnabled(false);
            comboPolicy.setEnabled(false);
            if (radioModify.isSelected() || radioDelete.isSelected()) {
                radioRead.setSelected(true);
                isModify = false; 
            }
        } else {
            radioModify.setEnabled(true);
            radioDelete.setEnabled(true);
            comboPolicy.setEnabled(true);
        }
        
        txtNewName.setEnabled(isModify);
        lblNewName.setEnabled(isModify);
        btnCreateUser.setEnabled(isAdmin);
    }

    private void setupModels() {
        rootNode = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(rootNode);
        jTree1.setModel(treeModel);

        String[] diskCols = {"ID Bloque", "Estado", "Contenido"};
        diskTableModel = new DefaultTableModel(diskCols, 0);
        jTableDiskView.setModel(diskTableModel);
        
        jTableDiskView.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) table.getModel().getValueAt(row, 1);
                c.setBackground("Ocupado".equals(status) ? new Color(255, 180, 180) : new Color(180, 255, 180));
                return c;
            }
        });

        String[] fileCols = {"Ubicación", "Nombre", "Tipo", "Dueño", "Tamaño", "Dir. Inicio"};
        filesTableModel = new DefaultTableModel(fileCols, 0);
        jTableFiles.setModel(filesTableModel);

        updateTreeStructure();
        updateCombos();
    }

    private void actionCreateUser() {
        if (!currentUser.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Solo el administrador puede crear usuarios.");
            return;
        }
        String name = txtNewUser.getText().trim();
        if (name.isEmpty()) return;
        
        for (User u : userList) {
            if (u.getName().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this, "Usuario ya existe.");
                return;
            }
        }
        User newUser = new User(name, false);
        userList.add(newUser);
        comboUsers.addItem(name);
        comboUsers.setSelectedItem(name);
        txtNewUser.setText("");
        logToMonitor("Usuario creado: " + name);
        JOptionPane.showMessageDialog(this, "Usuario creado.");
    }
    
    private void actionSelectUser() {
        String selectedName = (String) comboUsers.getSelectedItem();
        if (selectedName == null) return;
        for (User u : userList) {
            if (u.getName().equals(selectedName)) {
                this.currentUser = u;
                updateOperationsUI();
                logToMonitor("Sesión: " + selectedName);
                break;
            }
        }
    }

    private void actionCreate() {
        String name = txtCreateName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese nombre.");
            return;
        }
        
        Folder parentFolder = getSelectedParentFolder();
        
        if (parentFolder.getContents().getValueOfKey(name) != null) {
             JOptionPane.showMessageDialog(this, "Error: Nombre duplicado en " + parentFolder.getName());
             return; 
        }

        DiskElement newElement;
        if (radioFile.isSelected()) {
            int size = (Integer) spinnerSize.getValue();
            if (size > fileSystem.getDisk().getFreeSpace()) {
                JOptionPane.showMessageDialog(this, "Error: Espacio insuficiente.");
                return; 
            }
            newElement = new OS_Structures.File(name, currentUser, size, parentFolder);
        } else {
            newElement = new Folder(name, currentUser, parentFolder);
        }

        final DiskElement el = newElement;
        final String fName = name;
        final Folder pFolder = parentFolder;
        
        new Thread(() -> {
            fileSystem.createProcess(el, CRUD.CREATE, currentUser, fName, pFolder);
            logToMonitor("Solicitud CREAR enviada: " + fName);
        }).start();
    }
    
    private void actionCreateBatch10() {
        if (!radioFile.isSelected()) {
            JOptionPane.showMessageDialog(this, "Seleccione 'Archivo' para usar esta función.");
            return;
        }
        
        Folder parentFolder = getSelectedParentFolder();
        int sizePerFile = (Integer) spinnerSize.getValue();
        int totalNeeded = sizePerFile * 10;
        
        if (totalNeeded > fileSystem.getDisk().getFreeSpace()) {
            JOptionPane.showMessageDialog(this, "Error: Espacio insuficiente para 10 archivos.");
            return;
        }
        
        new Thread(() -> {
            long baseTime = System.currentTimeMillis() % 10000; 
            for (int i = 1; i <= 10; i++) {
                String batchName = "Batch_" + baseTime + "_" + i;
                OS_Structures.File f = new OS_Structures.File(batchName, currentUser, sizePerFile, parentFolder);
                fileSystem.createProcess(f, CRUD.CREATE, currentUser, batchName, parentFolder);
                try { Thread.sleep(50); } catch (Exception e){}
            }
            logToMonitor("Lote de 10 archivos enviado.");
        }).start();
    }
    
    private Folder getSelectedParentFolder() {
        String parentName = (String) comboLocation.getSelectedItem();
        Folder parentFolder = fileSystem.getDisk().getRoot();
        if (parentName != null && !parentName.equals("Root")) {
             DiskElement found = findElementRecursive(fileSystem.getDisk().getRoot(), parentName);
             if (found != null && !found.isFile()) parentFolder = (Folder) found;
        }
        return parentFolder;
    }

    private void actionOperate() {
        String parentName = (String) comboOpLocation.getSelectedItem();
        String targetName = (String) comboOpItem.getSelectedItem();
        
        if (parentName == null || targetName == null) {
            JOptionPane.showMessageDialog(this, "Seleccione ubicación y elemento.");
            return;
        }
        
        Folder parentFolder = fileSystem.getDisk().getRoot();
        if (!parentName.equals("Root")) {
            DiskElement p = findElementRecursive(fileSystem.getDisk().getRoot(), parentName);
            if (p instanceof Folder) parentFolder = (Folder) p;
        }
        
        DiskElement target = parentFolder.getContents().getValueOfKey(targetName);
        
        if (target == null) {
            JOptionPane.showMessageDialog(this, "Elemento no existe (refresque la vista).");
            updateCombos(); 
            return;
        }
        
        if ((radioModify.isSelected() || radioDelete.isSelected()) && !currentUser.isAdmin()) {
             JOptionPane.showMessageDialog(this, "Acceso Denegado: Solo administradores.");
             return;
        }

        CRUD type = CRUD.READ;
        if (radioModify.isSelected()) type = CRUD.UPDATE;
        if (radioDelete.isSelected()) type = CRUD.DELETE;
        
        if (type == CRUD.READ) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("LECTURA SOLICITADA:\n");
                sb.append("Nombre:   ").append(target.getName()).append("\n");
                sb.append("Tipo:     ").append(target.isFile() ? "Archivo" : "Carpeta").append("\n");
                
                String ownerName = (target.getOwner() != null) ? target.getOwner().getName() : "Sistema";
                sb.append("Dueño:    ").append(ownerName).append("\n");
                
                String parentFolderName = (target.getParent() != null) ? target.getParent().getName() : "Ninguno";
                sb.append("Padre:    ").append(parentFolderName).append("\n");
                
                if (target.isFile()) {
                    OS_Structures.File f = (OS_Structures.File) target;
                    sb.append("Tamaño:   ").append(f.getSize()).append(" bloques\n");
                    sb.append("Inicio:   Bloque ").append(f.getFileDir()).append("\n");
                } else {
                    Folder f = (Folder) target;
                    int count = (f.getContents() != null) ? f.getContents().getSize() : 0;
                    sb.append("Contiene: ").append(count).append(" elementos\n");
                }
                logToMonitor(sb.toString());
            } catch (Exception e) {
                logToMonitor("Error leyendo metadata.");
            }
        } 

        String tempNewName = null;
        if (type == CRUD.UPDATE) {
            tempNewName = txtNewName.getText().trim();
            if (tempNewName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese nuevo nombre.");
                return;
            }
            if (parentFolder.getContents().getValueOfKey(tempNewName) != null) {
                JOptionPane.showMessageDialog(this, "El nombre ya existe.");
                return;
            }
        }
        
        final String finalNewName = tempNewName;
        final CRUD finalType = type;
        final Folder finalParent = parentFolder;
        
        new Thread(() -> {
            fileSystem.createProcess(target, finalType, currentUser, finalNewName, finalParent);
            if (finalType != CRUD.READ) {
                logToMonitor("Operación " + finalType + " enviada sobre " + target.getName());
            }
        }).start();
    }

    private void startRefresher() {
        refreshTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDiskTable();
                lblFreeSpace.setText(fileSystem.getDisk().getFreeSpace() + " Bloques");
                updateTreeStructure(); 
                updateCombos(); 
            }
        });
        refreshTimer.start();
    }
    
    private void updateOpTargetItems() {
        comboOpItem.removeAllItems();
        String locationName = (String) comboOpLocation.getSelectedItem();
        if (locationName == null) return;
        
        Folder location = fileSystem.getDisk().getRoot();
        if (!locationName.equals("Root")) {
             DiskElement found = findElementRecursive(fileSystem.getDisk().getRoot(), locationName);
             if (found instanceof Folder) location = (Folder) found;
             else return;
        }
        
        boolean wantFiles = radioOpTargetFile.isSelected();
        List<DiskElement> contents = location.getContents().getValues();
        if (contents == null) return;
        
        for (DiskElement de : contents) {
            if (wantFiles && de.isFile()) {
                comboOpItem.addItem(de.getName());
            } else if (!wantFiles && !de.isFile()) {
                comboOpItem.addItem(de.getName());
            }
        }
    }

    private void updateTreeStructure() {
        Folder rootLogic = fileSystem.getDisk().getRoot();
        rootNode.removeAllChildren(); 
        buildTree(rootLogic, rootNode);
        treeModel.reload(); 
        for (int i = 0; i < jTree1.getRowCount(); i++) jTree1.expandRow(i);
        updateFileTable(rootLogic);
    }

    private void buildTree(Folder folder, DefaultMutableTreeNode visualNode) {
        if (folder == null) return;
        List<DiskElement> content = folder.getContents().getValues();
        if (content == null) return; 
        for (DiskElement de : content) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(de.getName());
            visualNode.add(childNode);
            if (!de.isFile()) buildTree((Folder) de, childNode);
        }
    }

    private void updateDiskTable() {
        Block[] memory = fileSystem.getDisk().getMemory();
        if (diskTableModel.getRowCount() != memory.length) {
            diskTableModel.setRowCount(0);
            for (Block b : memory) diskTableModel.addRow(new Object[]{b.getBlockDir(), b.isFree() ? "Libre" : "Ocupado", "-"});
        } else {
            for (int i=0; i<memory.length; i++) {
                String status = memory[i].isFree() ? "Libre" : "Ocupado";
                if (!status.equals(diskTableModel.getValueAt(i, 1))) diskTableModel.setValueAt(status, i, 1);
            }
        }
    }
    
    private void updateFileTable(Folder root) {
        filesTableModel.setRowCount(0);
        java.util.List<DiskElement> allFiles = new ArrayList<>();
        collectAllElements(root, allFiles);
        for (DiskElement de : allFiles) {
             String tipo = de.isFile() ? "Archivo" : "Carpeta";
             String size = de.isFile() ? String.valueOf(((OS_Structures.File)de).getSize()) : "-";
             String ownerName = (de.getOwner() != null) ? de.getOwner().getName() : "Sys";
             String parentName = (de.getParent() != null) ? de.getParent().getName() : "Root";
             String startDir = "-";
             if (de.isFile()) startDir = String.valueOf(((OS_Structures.File)de).getFileDir());
             
             filesTableModel.addRow(new Object[]{parentName, de.getName(), tipo, ownerName, size, startDir});
        }
    }
    
    private void collectAllElements(Folder current, java.util.List<DiskElement> accumulator) {
        if (current == null) return;
        List<DiskElement> contents = current.getContents().getValues();
        if (contents == null) return;
        for (DiskElement de : contents) {
            accumulator.add(de); 
            if (!de.isFile()) collectAllElements((Folder) de, accumulator); 
        }
    }
    
    private void updateCombos() {
        Object selLoc = comboLocation.getSelectedItem();
        Object selOpLoc = comboOpLocation.getSelectedItem();
        comboLocation.removeAllItems();
        comboOpLocation.removeAllItems();
        comboLocation.addItem("Root");
        comboOpLocation.addItem("Root");
        
        fillCombosRecursive(fileSystem.getDisk().getRoot());
        
        if (selLoc != null) try { comboLocation.setSelectedItem(selLoc); } catch(Exception e){}
        
        boolean restoredOpLoc = false;
        if (selOpLoc != null) {
            try { 
                comboOpLocation.setSelectedItem(selOpLoc); 
                restoredOpLoc = true;
            } catch(Exception e){}
        }
        
        if (restoredOpLoc || comboOpItem.getItemCount() == 0) {
            updateOpTargetItems();
        }
    }
    
    private void fillCombosRecursive(Folder current) {
        if (current == null) return;
        List<DiskElement> list = current.getContents().getValues();
        if (list == null) return;

        for (DiskElement de : list) {
            if (!de.isFile()) {
                comboLocation.addItem(de.getName());
                comboOpLocation.addItem(de.getName());
                fillCombosRecursive((Folder) de);
            }
        }
    }

    private DiskElement findElementRecursive(Folder current, String name) {
        if (current.getName().equals(name)) return current;
        List<DiskElement> list = current.getContents().getValues();
        if (list == null) return null;
        for (DiskElement de : list) {
            if (de.getName().equals(name)) return de;
            if (!de.isFile()) {
                DiskElement found = findElementRecursive((Folder)de, name);
                if (found != null) return found;
            }
        }
        return null;
    }
}