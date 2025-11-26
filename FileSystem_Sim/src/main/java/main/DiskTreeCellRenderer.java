/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import OS_Structures.File;
import OS_Structures.Folder;
import javax.swing.ImageIcon;
import java.net.URL;
/**
 *
 * @author Miguel
 */
public class DiskTreeCellRenderer extends DefaultTreeCellRenderer {
    private Icon folderIcon;
    private Icon fileIcon;

    public DiskTreeCellRenderer() {
        System.out.println(getClass().getResource("src/resources/folderIcon.png"));
        folderIcon = new ImageIcon("folderIcon.png");
        System.out.println(folderIcon);
        System.out.println(folderIcon.getIconHeight());
        System.out.println(folderIcon.getIconWidth());
        fileIcon = new ImageIcon(getClass().getResource("fileIcon.png"));
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = 
                node.getUserObject();
        
        if (userObject instanceof File) {
            setIcon(fileIcon);
            System.out.println("Works");
        }
        else if (userObject instanceof Folder) {
            setIcon(folderIcon);
            System.out.println("Works");
        }
        else {
            System.out.println("Not WOrks");
            if (leaf) {
                setIcon(getDefaultLeafIcon());
            } else if (expanded) {
                setIcon(getDefaultOpenIcon());
            } else {
                setIcon(getDefaultClosedIcon());
            }
        }
        return this;
    }
}