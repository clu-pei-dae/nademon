package net.jmhering.nademon.gui;

import net.jmhering.nademon.models.NagiosHostCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;


/**
 * Created by clupeidae on 09.06.15.
 */
public class MainGUI {
    static final Logger l = LogManager.getLogger("NaDeMon");

    private JPanel mainPanel;
    private JTable tblInfo;
    private JScrollPane scpScrollPane;
    private JMenuBar menuMain;
    private JFrame frame;
    private NagiosHostCollection hosts;
    protected final InfoTableModel tblModel = InfoTableModel.getInstance();
    protected DefaultRowSorter sorter;


    public MainGUI() {
        l.trace("Creating MainGUI");
        this.frame = new JFrame("MainGUI");
        this.frame.setContentPane(mainPanel);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        l.trace("Window initialized.");

        createUIComponents();

        this.frame.pack();
        this.frame.setVisible(true);
        l.trace("Window showed.");
    }

    private void createUIComponents() {
        l.trace("Creating UI Components");

        tblInfo = new InfoTable();
        tblInfo.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

        menuMain = new JMenuBar();

        tblInfo.setModel(tblModel);

        // Counter
        tblInfo.getColumnModel().getColumn(0).setMinWidth(1);
        tblInfo.getColumnModel().getColumn(0).setMaxWidth(50);
        tblInfo.getColumnModel().getColumn(0).setPreferredWidth(30);

        // Hostname
        tblInfo.getColumnModel().getColumn(1).setMinWidth(10);
        tblInfo.getColumnModel().getColumn(1).setMaxWidth(100);
        tblInfo.getColumnModel().getColumn(1).setPreferredWidth(100);

        // Service name
        tblInfo.getColumnModel().getColumn(2).setMinWidth(10);
        tblInfo.getColumnModel().getColumn(2).setMaxWidth(200);
        tblInfo.getColumnModel().getColumn(2).setPreferredWidth(200);

        // Status
        tblInfo.getColumnModel().getColumn(3).setMinWidth(10);
        tblInfo.getColumnModel().getColumn(3).setMaxWidth(100);
        tblInfo.getColumnModel().getColumn(3).setPreferredWidth(50);

        // Sorting
        tblInfo.setAutoCreateRowSorter(true);
        sorter = (DefaultRowSorter) tblInfo.getRowSorter();

        ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        JMenuItem miOK = new JMenuItem("OK");
        miOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                l.trace("OK button pressed.");
                RowFilter rf = RowFilter.regexFilter(".*OK.*", 3);
                l.trace(rf);
                sorter.setRowFilter(rf);
            }
        });
        JMenuItem miWARN = new JMenuItem("WARN");
        JMenuItem miCRIT = new JMenuItem("CRIT");
        JMenuItem miALL = new JMenuItem("ALL");

        JMenuItem miREFRESH = new JMenuItem("Refresh");
        miREFRESH.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                l.trace("REFRESH button pressed.");
                tblModel.updateData();
            }
        });

        menuMain.add(miOK);
        menuMain.add(miWARN);
        menuMain.add(miCRIT);
        menuMain.add(miALL);
        menuMain.add(miREFRESH);
    }
}
