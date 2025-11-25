/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.Enumeration;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import OS_Structures.*;

/**
 *
 * @author vince
 */
public class GUI extends javax.swing.JPanel {
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private DefaultTableModel tableModelFiles;
    private DefaultTableModel tableModelDiskView;
    private DefaultTableModel tableModelBuffer;
    private Disk disk;
    public GUI() {
        initComponents();
        setupTree();
        setupNewComponents();
    }

    private void setupTree() {
        rootNode = new DefaultMutableTreeNode(new Folder("Root", new User("Nop", true)));
        treeModel = new DefaultTreeModel(rootNode);
        jTree1.setModel(treeModel);
        updateLocationComboBox();
        updateTargetFileComboBox();
    }

    private void setupNewComponents() {
        jComboBoxCurrentUser.addItem("Admin");
        jComboBoxCurrentUser.addItem("Miguel");
        jComboBoxCurrentUser.addItem("Vincenzo");
        jComboBoxCurrentUser.addActionListener(e -> checkAdminPermissions());
        String[] fileColumns = {"Nombre", "Bloques", "Dir. Bloque", "Proceso Creador", "Usuario Creador"};
        tableModelFiles = new DefaultTableModel(fileColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableFiles.setModel(tableModelFiles);
        int diskCols = 10;
        int diskRows = 10;
        tableModelDiskView = new DefaultTableModel(diskRows, diskCols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableDiskView.setModel(tableModelDiskView);
        jTableDiskView.setTableHeader(null);
        String[] bufferColumns = {"Archivo", "Dirección Bloque", "Info Adicional"};
        tableModelBuffer = new DefaultTableModel(bufferColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableBuffer.setModel(tableModelBuffer); 
        ActionListener createPanelListener = e -> updateCreatePanelState();
        jRadioButtonArchivo.addActionListener(createPanelListener);
        jRadioButtonCarpeta.addActionListener(createPanelListener);
        updateOperatePanelState();
        updateCreatePanelState();
        checkAdminPermissions();
    }

    private void updateLocationComboBox() {
        jComboBoxLocation.removeAllItems();
        addNodesToComboBox(rootNode, true);
    }

    private void updateTargetFileComboBox() {
        jComboBoxTargetFile.removeAllItems();
        addNodesToComboBox(rootNode, false);
    }

    /**
     * Método recursivo para poblar los ComboBox
     * @param node El nodo desde donde empezar
     * @param foldersOnly True si solo queremos carpetas, False si solo queremos archivos
     */
    private void addNodesToComboBox(DefaultMutableTreeNode node, boolean foldersOnly) {
        if (foldersOnly && node.getAllowsChildren()) {
            if (jComboBoxLocation != null) {
                jComboBoxLocation.addItem(node);
            }
        } else if (!foldersOnly && !node.getAllowsChildren() && node != rootNode) {
            if (jComboBoxTargetFile != null) {
                jComboBoxTargetFile.addItem(node);
            }
        }
        Enumeration<?> children = node.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
            addNodesToComboBox(childNode, foldersOnly);
        }
    }

    private void checkAdminPermissions() {
        boolean isAdmin = "Admin".equals(jComboBoxCurrentUser.getSelectedItem());   
        jRadioButtonModify.setEnabled(isAdmin);
        jRadioButtonDelete.setEnabled(isAdmin);
        if (!isAdmin && (jRadioButtonModify.isSelected() || jRadioButtonDelete.isSelected())) {
            jRadioButtonRead.setSelected(true);
            updateOperatePanelState();
        }
        jRadioButtonCarpeta.setEnabled(isAdmin);
        if (!isAdmin && jRadioButtonCarpeta.isSelected()) {
            jRadioButtonArchivo.setSelected(true);
            updateCreatePanelState();
        }
    }
 
    private void updateCreatePanelState() {
        boolean isFile = jRadioButtonArchivo.isSelected();
        jLabelBlockSize.setEnabled(isFile);
        jTextFieldBlockSize.setEnabled(isFile);
    }

    private void updateOperatePanelState() {
        if (jRadioButtonModify.isSelected()) {
            jLabelTargetFile.setEnabled(true);
            jComboBoxTargetFile.setEnabled(true);
            jLabelFileName_Modify.setEnabled(true);
            jTextFieldFileName_Modify.setEnabled(true);
        } else {
            jLabelTargetFile.setEnabled(true);
            jComboBoxTargetFile.setEnabled(true);
            jLabelFileName_Modify.setEnabled(false);
            jTextFieldFileName_Modify.setEnabled(false);
        }
    }
    
    private void updateFileNameInTable(String oldName, String newName) {
        for (int i = 0; i < tableModelFiles.getRowCount(); i++) {
            if (tableModelFiles.getValueAt(i, 0).equals(oldName)) {
                tableModelFiles.setValueAt(newName, i, 0);
                break;
            }
        }
    }

    private void deleteFileNameFromTable(String name) {
        for (int i = 0; i < tableModelFiles.getRowCount(); i++) {
            if (tableModelFiles.getValueAt(i, 0).equals(name)) {
                tableModelFiles.removeRow(i);
                break;
            }
        }
    }

    public void updateProcessQueue(String queueName, String content) {
        switch (queueName.toLowerCase()) {
            case "nuevo":
                jTextAreaNew.setText(content);
                break;
            case "listo":
                jTextAreaReady.setText(content);
                break;
            case "ejecutando":
                jTextAreaRunning.setText(content);
                break;
            case "bloqueado":
                jTextAreaBlocked.setText(content);
                break;
            case "terminado":
                jTextAreaFinished.setText(content);
                break;
        }
    }
    public void updateCurrentIOProcess(String processInfo) {
        jTextFieldCurrentIO.setText(processInfo);
    } 
    public void addFileToTable(Object[] rowData) {
        tableModelFiles.addRow(rowData);
    } 
    public void refreshFileTable(/* Lista de archivos */) {
        tableModelFiles.setRowCount(0);
    }
    public void updateDiskBlock(int row, int col, Object value) {
        tableModelDiskView.setValueAt(value, row, col);
    }
    public void refreshBufferTable(/* Lista de bloques en buffer */) {
        tableModelBuffer.setRowCount(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        buttonGroupTipoProceso = new javax.swing.ButtonGroup();
        buttonGroupCreateType = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jScrollPaneTree = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jTabbedPaneMiddle = new javax.swing.JTabbedPane();
        jPanelQueues = new javax.swing.JPanel();
        jLabelNew = new javax.swing.JLabel();
        jScrollPaneNew = new javax.swing.JScrollPane();
        jTextAreaNew = new javax.swing.JTextArea();
        jLabelReady = new javax.swing.JLabel();
        jScrollPaneReady = new javax.swing.JScrollPane();
        jTextAreaReady = new javax.swing.JTextArea();
        jLabelRunning = new javax.swing.JLabel();
        jScrollPaneRunning = new javax.swing.JScrollPane();
        jTextAreaRunning = new javax.swing.JTextArea();
        jLabelBlocked = new javax.swing.JLabel();
        jScrollPaneBlocked = new javax.swing.JScrollPane();
        jTextAreaBlocked = new javax.swing.JTextArea();
        jLabelFinished = new javax.swing.JLabel();
        jScrollPaneFinished = new javax.swing.JScrollPane();
        jTextAreaFinished = new javax.swing.JTextArea();
        jLabelCurrentIO = new javax.swing.JLabel();
        jTextFieldCurrentIO = new javax.swing.JTextField();
        jPanelFileTable = new javax.swing.JPanel();
        jScrollPaneFiles = new javax.swing.JScrollPane();
        jTableFiles = new javax.swing.JTable();
        jPanelDiskView = new javax.swing.JPanel();
        jScrollPaneDisk = new javax.swing.JScrollPane();
        jTableDiskView = new javax.swing.JTable();
        jPanelBuffer = new javax.swing.JPanel();
        jScrollPaneBuffer = new javax.swing.JScrollPane();
        jTableBuffer = new javax.swing.JTable();
        jPanelRight = new javax.swing.JPanel();
        jPanelUser = new javax.swing.JPanel();
        jLabelCurrentUser = new javax.swing.JLabel();
        jComboBoxCurrentUser = new javax.swing.JComboBox<>();
        jLabelNewUser = new javax.swing.JLabel();
        jTextFieldNewUser = new javax.swing.JTextField();
        jButtonAddUser = new javax.swing.JButton();
        jPanelCreateOperation = new javax.swing.JPanel();
        jLabelLocation = new javax.swing.JLabel();
        jComboBoxLocation = new javax.swing.JComboBox<>();
        jLabelFileName_Create = new javax.swing.JLabel();
        jTextFieldFileName_Create = new javax.swing.JTextField();
        jLabelBlockSize = new javax.swing.JLabel();
        jTextFieldBlockSize = new javax.swing.JTextField();
        jButtonCreateFileProcess = new javax.swing.JButton();
        jRadioButtonArchivo = new javax.swing.JRadioButton();
        jRadioButtonCarpeta = new javax.swing.JRadioButton();
        jPanelOperateOperation = new javax.swing.JPanel();
        jLabelProcessType = new javax.swing.JLabel();
        jRadioButtonRead = new javax.swing.JRadioButton();
        jRadioButtonModify = new javax.swing.JRadioButton();
        jRadioButtonDelete = new javax.swing.JRadioButton();
        jLabelTargetFile = new javax.swing.JLabel();
        jComboBoxTargetFile = new javax.swing.JComboBox<>();
        jLabelFileName_Modify = new javax.swing.JLabel();
        jTextFieldFileName_Modify = new javax.swing.JTextField();
        jButtonOperateFileProcess = new javax.swing.JButton();
        jLabel1.setText("JTree");
        jScrollPaneTree.setViewportView(jTree1);
        jLabelNew.setText("Nuevo:");
        jTextAreaNew.setEditable(false);
        jTextAreaNew.setColumns(20);
        jTextAreaNew.setRows(3);
        jScrollPaneNew.setViewportView(jTextAreaNew);
        jLabelReady.setText("Listo:");
        jTextAreaReady.setEditable(false);
        jTextAreaReady.setColumns(20);
        jTextAreaReady.setRows(3);
        jScrollPaneReady.setViewportView(jTextAreaReady);
        jLabelRunning.setText("Ejecutando:");
        jTextAreaRunning.setEditable(false);
        jTextAreaRunning.setColumns(20);
        jTextAreaRunning.setRows(3);
        jScrollPaneRunning.setViewportView(jTextAreaRunning);
        jLabelBlocked.setText("Bloqueado:");
        jTextAreaBlocked.setEditable(false);
        jTextAreaBlocked.setColumns(20);
        jTextAreaBlocked.setRows(3);
        jScrollPaneBlocked.setViewportView(jTextAreaBlocked);
        jLabelFinished.setText("Terminado:");
        jTextAreaFinished.setEditable(false);
        jTextAreaFinished.setColumns(20);
        jTextAreaFinished.setRows(3);
        jScrollPaneFinished.setViewportView(jTextAreaFinished);
        jLabelCurrentIO.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelCurrentIO.setText("Operación E/S Actual:");
        jTextFieldCurrentIO.setEditable(false);
        javax.swing.GroupLayout jPanelQueuesLayout = new javax.swing.GroupLayout(jPanelQueues);
        jPanelQueues.setLayout(jPanelQueuesLayout);
        jPanelQueuesLayout.setHorizontalGroup(
            jPanelQueuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelQueuesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelQueuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneNew, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                    .addComponent(jScrollPaneReady)
                    .addComponent(jScrollPaneRunning)
                    .addComponent(jScrollPaneBlocked)
                    .addComponent(jScrollPaneFinished)
                    .addGroup(jPanelQueuesLayout.createSequentialGroup()
                        .addGroup(jPanelQueuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelNew)
                            .addComponent(jLabelReady)
                            .addComponent(jLabelRunning)
                            .addComponent(jLabelBlocked)
                            .addComponent(jLabelFinished)
                            .addGroup(jPanelQueuesLayout.createSequentialGroup()
                                .addComponent(jLabelCurrentIO)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldCurrentIO, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelQueuesLayout.setVerticalGroup(
            jPanelQueuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelQueuesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneNew, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelReady)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneReady, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelRunning)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneRunning, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelBlocked)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneBlocked, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelFinished)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneFinished, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelQueuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCurrentIO)
                    .addComponent(jTextFieldCurrentIO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(112, Short.MAX_VALUE))
        );
        jTabbedPaneMiddle.addTab("Colas de Procesos", jPanelQueues);
        jPanelFileTable.setLayout(new java.awt.BorderLayout());
        jTableFiles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPaneFiles.setViewportView(jTableFiles);
        jPanelFileTable.add(jScrollPaneFiles, java.awt.BorderLayout.CENTER);
        jTabbedPaneMiddle.addTab("Archivos", jPanelFileTable);
        jPanelDiskView.setLayout(new java.awt.BorderLayout());
        jTableDiskView.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
            }
        ));
        jScrollPaneDisk.setViewportView(jTableDiskView);
        jPanelDiskView.add(jScrollPaneDisk, java.awt.BorderLayout.CENTER);
        jTabbedPaneMiddle.addTab("Disco", jPanelDiskView);
        jPanelBuffer.setLayout(new java.awt.BorderLayout());
        jTableBuffer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
            }
        ));
        jScrollPaneBuffer.setViewportView(jTableBuffer);
        jPanelBuffer.add(jScrollPaneBuffer, java.awt.BorderLayout.CENTER);
        jTabbedPaneMiddle.addTab("Buffer", jPanelBuffer);
        jPanelUser.setBorder(javax.swing.BorderFactory.createTitledBorder("Gestión de Usuarios"));
        jLabelCurrentUser.setText("Usuario Actual:");
        jLabelNewUser.setText("Agregar Nuevo Usuario:");
        jButtonAddUser.setText("Agregar");
        jButtonAddUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddUserActionPerformed(evt);
            }
        });
        javax.swing.GroupLayout jPanelUserLayout = new javax.swing.GroupLayout(jPanelUser);
        jPanelUser.setLayout(jPanelUserLayout);
        jPanelUserLayout.setHorizontalGroup(
            jPanelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxCurrentUser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelUserLayout.createSequentialGroup()
                        .addComponent(jTextFieldNewUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAddUser))
                    .addGroup(jPanelUserLayout.createSequentialGroup()
                        .addGroup(jPanelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelCurrentUser)
                            .addComponent(jLabelNewUser))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelUserLayout.setVerticalGroup(
            jPanelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUserLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelCurrentUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxCurrentUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelNewUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldNewUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAddUser))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelCreateOperation.setBorder(javax.swing.BorderFactory.createTitledBorder("Crear (Archivo/Carpeta)"));
        jLabelLocation.setText("Ubicación:");
        jLabelFileName_Create.setText("Nombre Archivo/Carpeta:");
        jLabelBlockSize.setText("Tamaño en Bloques:");
        jButtonCreateFileProcess.setText("Crear Proceso (Crear)");
        jButtonCreateFileProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateFileProcessActionPerformed(evt);
            }
        });
        buttonGroupCreateType.add(jRadioButtonArchivo);
        jRadioButtonArchivo.setSelected(true);
        jRadioButtonArchivo.setText("Archivo");
        buttonGroupCreateType.add(jRadioButtonCarpeta);
        jRadioButtonCarpeta.setText("Carpeta");
        javax.swing.GroupLayout jPanelCreateOperationLayout = new javax.swing.GroupLayout(jPanelCreateOperation);
        jPanelCreateOperation.setLayout(jPanelCreateOperationLayout);
        jPanelCreateOperationLayout.setHorizontalGroup(
            jPanelCreateOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCreateOperationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCreateOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxLocation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldFileName_Create)
                    .addComponent(jTextFieldBlockSize)
                    .addComponent(jButtonCreateFileProcess, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                    .addGroup(jPanelCreateOperationLayout.createSequentialGroup()
                        .addGroup(jPanelCreateOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelFileName_Create)
                            .addComponent(jLabelBlockSize)
                            .addComponent(jLabelLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelCreateOperationLayout.createSequentialGroup()
                                .addComponent(jRadioButtonArchivo)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButtonCarpeta)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelCreateOperationLayout.setVerticalGroup(
            jPanelCreateOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCreateOperationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelLocation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelCreateOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonArchivo)
                    .addComponent(jRadioButtonCarpeta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelFileName_Create)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldFileName_Create, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelBlockSize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldBlockSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonCreateFileProcess)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelOperateOperation.setBorder(javax.swing.BorderFactory.createTitledBorder("Operar Archivo (Leer/Mod/Del)"));
        jLabelProcessType.setText("Tipo de Proceso:");
        buttonGroupTipoProceso.add(jRadioButtonRead);
        jRadioButtonRead.setSelected(true);
        jRadioButtonRead.setText("Leer");
        jRadioButtonRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonReadActionPerformed(evt);
            }
        });
        buttonGroupTipoProceso.add(jRadioButtonModify);
        jRadioButtonModify.setText("Modificar");
        jRadioButtonModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonModifyActionPerformed(evt);
            }
        });
        buttonGroupTipoProceso.add(jRadioButtonDelete);
        jRadioButtonDelete.setText("Eliminar");
        jRadioButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonDeleteActionPerformed(evt);
            }
        });
        jLabelTargetFile.setText("Archivo Objetivo:");
        jLabelFileName_Modify.setText("Nuevo Nombre (para Modificar):");
        jButtonOperateFileProcess.setText("Crear Proceso (Operar)");
        jButtonOperateFileProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOperateFileProcessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelOperateOperationLayout = new javax.swing.GroupLayout(jPanelOperateOperation);
        jPanelOperateOperation.setLayout(jPanelOperateOperationLayout);
        jPanelOperateOperationLayout.setHorizontalGroup(
            jPanelOperateOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOperateOperationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOperateOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxTargetFile, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldFileName_Modify)
                    .addComponent(jButtonOperateFileProcess, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                    .addGroup(jPanelOperateOperationLayout.createSequentialGroup()
                        .addGroup(jPanelOperateOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelProcessType)
                            .addGroup(jPanelOperateOperationLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanelOperateOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioButtonModify)
                                    .addComponent(jRadioButtonDelete)
                                    .addComponent(jRadioButtonRead)))
                            .addComponent(jLabelTargetFile)
                            .addComponent(jLabelFileName_Modify))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelOperateOperationLayout.setVerticalGroup(
            jPanelOperateOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOperateOperationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelProcessType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonRead)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonModify)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelTargetFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxTargetFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelFileName_Modify)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldFileName_Modify, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                .addComponent(jButtonOperateFileProcess)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelRightLayout = new javax.swing.GroupLayout(jPanelRight);
        jPanelRight.setLayout(jPanelRightLayout);
        jPanelRightLayout.setHorizontalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelCreateOperation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelOperateOperation, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelRightLayout.setVerticalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelCreateOperation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelOperateOperation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jScrollPaneTree, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(134, 134, 134)
                        .addComponent(jLabel1)))
                .addGap(18, 18, 18)
                .addComponent(jTabbedPaneMiddle, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanelRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneTree)
                .addGap(18, 18, 18))
            .addComponent(jTabbedPaneMiddle)
            .addComponent(jPanelRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddUserActionPerformed
        String newUser = jTextFieldNewUser.getText().trim();
        if (!newUser.isEmpty()) {
            jComboBoxCurrentUser.addItem(newUser);
            jComboBoxCurrentUser.setSelectedItem(newUser);
            jTextFieldNewUser.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "El nombre de usuario no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonAddUserActionPerformed
    private void jRadioButtonReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonReadActionPerformed
        updateOperatePanelState();
    }//GEN-LAST:event_jRadioButtonReadActionPerformed
    private void jRadioButtonModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonModifyActionPerformed
        updateOperatePanelState();
    }//GEN-LAST:event_jRadioButtonModifyActionPerformed
    private void jRadioButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonDeleteActionPerformed
        updateOperatePanelState();
    }//GEN-LAST:event_jRadioButtonDeleteActionPerformed

    private void jButtonCreateFileProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateFileProcessActionPerformed
        String processName = "P-" + new SimpleDateFormat("HHmmssSSS").format(new Date());    
        String currentUser = (String) jComboBoxCurrentUser.getSelectedItem();
        String fileName = jTextFieldFileName_Create.getText().trim();
        DefaultMutableTreeNode location = (DefaultMutableTreeNode) jComboBoxLocation.getSelectedItem(); 
        if (fileName.isEmpty() || location == null) {
            JOptionPane.showMessageDialog(this, "Para 'Crear', complete Nombre y Ubicación.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(fileName);
        if (jRadioButtonArchivo.isSelected()) {
            String blockSizeStr = jTextFieldBlockSize.getText().trim();
            if (blockSizeStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Para 'Archivo', especifique Tamaño en Bloques.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int blocks;
            try {
                blocks = Integer.parseInt(blockSizeStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El tamaño en bloques debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println("--- Proceso CREAR ARCHIVO ---");
            System.out.println("Proceso: " + processName + ", Usuario: " + currentUser);
            System.out.println("Archivo: " + fileName + ", Tamaño: " + blocks + " bloques");
            newNode.setAllowsChildren(false); 
            treeModel.insertNodeInto(newNode, location, location.getChildCount());     
            Object[] rowData = {fileName, blocks, "N/A", processName, currentUser};
            addFileToTable(rowData);
            updateTargetFileComboBox();    
        } else if (jRadioButtonCarpeta.isSelected()) {
            System.out.println("--- Proceso CREAR CARPETA ---");
            System.out.println("Proceso: " + processName + ", Usuario: " + currentUser);
            System.out.println("Carpeta: " + fileName);    
            newNode.setAllowsChildren(true);
            treeModel.insertNodeInto(newNode, location, location.getChildCount());
            updateLocationComboBox();
        }     
        jTree1.expandPath(new javax.swing.tree.TreePath(location.getPath()));
        jTextFieldFileName_Create.setText("");
        jTextFieldBlockSize.setText("");
    }//GEN-LAST:event_jButtonCreateFileProcessActionPerformed

    private void jButtonOperateFileProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOperateFileProcessActionPerformed
        String processName = "P-" + new SimpleDateFormat("HHmmssSSS").format(new Date());        
        String currentUser = (String) jComboBoxCurrentUser.getSelectedItem();
        DefaultMutableTreeNode targetFileNode = (DefaultMutableTreeNode) jComboBoxTargetFile.getSelectedItem();
        if (targetFileNode == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un archivo objetivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }    
        String targetFileName = targetFileNode.getUserObject().toString();   
        if (jRadioButtonRead.isSelected()) {
            System.out.println("--- Proceso LEER ---");
            System.out.println("Usuario: " + currentUser);
            System.out.println("Proceso: " + processName);
            System.out.println("Archivo: " + targetFileName);
        } else if (jRadioButtonModify.isSelected()) {
            String newName = jTextFieldFileName_Modify.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Para 'Modificar', escriba un nuevo nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println("--- Proceso MODIFICAR ---");
            System.out.println("Usuario: " + currentUser);
            System.out.println("Proceso: " + processName);
            System.out.println("Archivo: " + targetFileName + " -> " + newName);
            targetFileNode.setUserObject(newName);
            treeModel.nodeChanged(targetFileNode);
            updateFileNameInTable(targetFileName, newName);       
            updateTargetFileComboBox();
        } else if (jRadioButtonDelete.isSelected()) {
            System.out.println("--- Proceso ELIMINAR ---");
            System.out.println("Usuario: " + currentUser);
            System.out.println("Proceso: " + processName);
            System.out.println("Archivo: " + targetFileName);
            treeModel.removeNodeFromParent(targetFileNode);       
            deleteFileNameFromTable(targetFileName);         
            updateTargetFileComboBox();
        }
        jTextFieldFileName_Modify.setText("");
    }//GEN-LAST:event_jButtonOperateFileProcessActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupCreateType;
    private javax.swing.ButtonGroup buttonGroupTipoProceso;
    private javax.swing.JButton jButtonAddUser;
    private javax.swing.JButton jButtonCreateFileProcess;
    private javax.swing.JButton jButtonOperateFileProcess;
    private javax.swing.JComboBox<String> jComboBoxCurrentUser;
    private javax.swing.JComboBox<DefaultMutableTreeNode> jComboBoxLocation;
    private javax.swing.JComboBox<DefaultMutableTreeNode> jComboBoxTargetFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelBlockSize;
    private javax.swing.JLabel jLabelBlocked;
    private javax.swing.JLabel jLabelCurrentIO;
    private javax.swing.JLabel jLabelCurrentUser;
    private javax.swing.JLabel jLabelFileName_Create;
    private javax.swing.JLabel jLabelFileName_Modify;
    private javax.swing.JLabel jLabelFinished;
    private javax.swing.JLabel jLabelLocation;
    private javax.swing.JLabel jLabelNew;
    private javax.swing.JLabel jLabelNewUser;
    private javax.swing.JLabel jLabelProcessType;
    private javax.swing.JLabel jLabelReady;
    private javax.swing.JLabel jLabelRunning;
    private javax.swing.JLabel jLabelTargetFile;
    private javax.swing.JPanel jPanelBuffer;
    private javax.swing.JPanel jPanelCreateOperation;
    private javax.swing.JPanel jPanelDiskView;
    private javax.swing.JPanel jPanelFileTable;
    private javax.swing.JPanel jPanelOperateOperation;
    private javax.swing.JPanel jPanelQueues;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelUser;
    private javax.swing.JRadioButton jRadioButtonArchivo;
    private javax.swing.JRadioButton jRadioButtonCarpeta;
    private javax.swing.JRadioButton jRadioButtonDelete;
    private javax.swing.JRadioButton jRadioButtonModify;
    private javax.swing.JRadioButton jRadioButtonRead;
    private javax.swing.JScrollPane jScrollPaneBuffer;
    private javax.swing.JScrollPane jScrollPaneBlocked;
    private javax.swing.JScrollPane jScrollPaneDisk;
    private javax.swing.JScrollPane jScrollPaneFiles;
    private javax.swing.JScrollPane jScrollPaneFinished;
    private javax.swing.JScrollPane jScrollPaneNew;
    private javax.swing.JScrollPane jScrollPaneReady;
    private javax.swing.JScrollPane jScrollPaneRunning;
    private javax.swing.JScrollPane jScrollPaneTree;
    private javax.swing.JTabbedPane jTabbedPaneMiddle;
    private javax.swing.JTable jTableBuffer;
    private javax.swing.JTable jTableDiskView;
    private javax.swing.JTable jTableFiles;
    private javax.swing.JTextArea jTextAreaBlocked;
    private javax.swing.JTextArea jTextAreaFinished;
    private javax.swing.JTextArea jTextAreaNew;
    private javax.swing.JTextArea jTextAreaReady;
    private javax.swing.JTextArea jTextAreaRunning;
    private javax.swing.JTextField jTextFieldBlockSize;
    private javax.swing.JTextField jTextFieldCurrentIO;
    private javax.swing.JTextField jTextFieldFileName_Create;
    private javax.swing.JTextField jTextFieldFileName_Modify;
    private javax.swing.JTextField jTextFieldNewUser;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}