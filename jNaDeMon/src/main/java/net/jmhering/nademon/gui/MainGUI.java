package net.jmhering.nademon.gui;

import net.jmhering.nademon.gui.components.NaDeMonMainMenuFilterItem;
import net.jmhering.nademon.gui.components.NaDeMonMainMenuSimpleItem;
import net.jmhering.nademon.gui.components.NaDeMonTablePopupMenu;
import net.jmhering.nademon.models.NagiosHostCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;


/**
 * Created by clupeidae on 09.06.15.
 */
public class MainGUI {
    static final Logger l = LogManager.getLogger("NaDeMon");

    private static final int D_WIDTH = 1200;
    private static final int D_HEIGHT = 800;
    private static final int UPDATE_INTERVAL = 5000;
    private JLabel lblLastUpdate;
    private JPanel mainPanel;
    private InfoTable tblInfo;
    private JScrollPane scpScrollPane;
    private JMenuBar menuMain;
    private JFrame frame;
    private NagiosHostCollection hosts;
    protected final InfoTableModel tblModel = InfoTableModel.getInstance();
    protected TableRowSorter sorter;
    private SystemTray tray = SystemTray.getSystemTray();

    public MainGUI() {
        l.trace("Creating MainGUI");
        createUIComponents();
        frame.pack();
        frame.setVisible(true);
        l.trace("Window showed.");
        startUpdateTimer();
    }

    private void startUpdateTimer() {
        Timer timer = new Timer(UPDATE_INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                updateTableData();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void updateTableData() {
        tblModel.updateData();
        updateTableDescriptionLabel();
    }

    public void updateTableDescriptionLabel() {
        lblLastUpdate.setText("#" + tblInfo.getRowCount() + " - Last Update: " + tblModel.getLastUpdated());
    }

    private void createUIComponents() {
        // The window.
        frame = new JFrame("MainGUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Nagios Desktop Monitor");

        // The window panel.
        mainPanel = new JPanel();
        mainPanel.setMinimumSize(new Dimension(D_WIDTH, D_HEIGHT));
        mainPanel.setPreferredSize(new Dimension(D_WIDTH, D_HEIGHT));
        mainPanel.setLayout(new BorderLayout());
        frame.setContentPane(mainPanel);

        // The table.
        tblInfo = new InfoTable();
        tblInfo.setFillsViewportHeight(true);
        tblInfo.setAutoCreateRowSorter(true);
        tblInfo.setAutoscrolls(true);
        tblInfo.setRowHeight(30);
        tblInfo.setColumnSelectionAllowed(false);
        tblInfo.setShowHorizontalLines(false);
        tblInfo.setShowVerticalLines(false);
        tblInfo.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
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
        sorter = (TableRowSorter) tblInfo.getRowSorter();

        ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        // Set comparator for the status column so that we sort by status id and not the string representation.
        sorter.setComparator(3, new Comparator<Object>() {

            public int compare(Object t, Object t1) {
                int t_int = InfoTableModel.matchWordToState((String)t);
                int t1_int = InfoTableModel.matchWordToState((String)t1);

                return new Integer(t_int).compareTo(t1_int);
            }
        });
        sorter.sort();

        // Popup menu
        tblInfo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.isPopupTrigger()) {
                    doPop(mouseEvent);
                }
            }

            public void mouseReleased(MouseEvent mouseEvent) {
                if (mouseEvent.isPopupTrigger()) {
                    doPop(mouseEvent);
                }
            }

            private void doPop(MouseEvent mouseEvent) {
                l.trace("Showing pop up menu.");
                NaDeMonTablePopupMenu mnuPopUp = new NaDeMonTablePopupMenu(sorter, tblInfo);
                mnuPopUp.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }
        });

        // Scrollpane that contains the Table.
        scpScrollPane = new JScrollPane(tblInfo);
        mainPanel.add(scpScrollPane, BorderLayout.CENTER);

        // The menu bar.
        menuMain = new JMenuBar();
        FlowLayout menuLayout = new FlowLayout();
        menuLayout.setAlignment(FlowLayout.LEFT);
        menuMain.setLayout(menuLayout);
        mainPanel.add(menuMain, BorderLayout.NORTH);

        JLabel lblFilter = new JLabel("Filter by Status: ");
        menuMain.add(lblFilter);

        // The OK Filter button.
        NaDeMonMainMenuFilterItem miOK = new NaDeMonMainMenuFilterItem("OK", sorter, ".*OK.*", this);
        menuMain.add(miOK);

        // The WARN Filter button.
        NaDeMonMainMenuFilterItem miWARN = new NaDeMonMainMenuFilterItem("WARN", sorter, ".*WARN.*", this);
        menuMain.add(miWARN);

        // The CRIT Filter button.
        NaDeMonMainMenuFilterItem miCRIT = new NaDeMonMainMenuFilterItem("CRIT", sorter, ".*CRIT.*", this);
        menuMain.add(miCRIT);

        // The ALL Filter button.
        NaDeMonMainMenuFilterItem miALL = new NaDeMonMainMenuFilterItem("ALL", sorter, ".?", this);
        menuMain.add(miALL);

        menuMain.add(new JSeparator());


        // Refresh button.
        ActionListener refreshAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                tblModel.updateData();
            }
        };
        NaDeMonMainMenuSimpleItem miREFRESH = new NaDeMonMainMenuSimpleItem("Refresh", refreshAction);
        menuMain.add(miREFRESH);

        lblLastUpdate = new JLabel("Last Update: #####");
        menuMain.add(lblLastUpdate);

        updateTableData();

        // Tray icon
        if (SystemTray.isSupported()) {
            l.trace("System tray supported");
            BufferedImage trayIconImage = null;
            try {
                trayIconImage = ImageIO.read(ClassLoader.getSystemClassLoader().getResource("icon.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;

            TrayIcon trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "NaDeMon");

            trayIcon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (frame.isVisible()) {
                        frame.setVisible(false);
                    }
                    else {
                        frame.setVisible(true);
                        frame.setExtendedState(JFrame.NORMAL);
                    }
                }
            });

            frame.addWindowStateListener(new WindowStateListener() {
                @Override
                public void windowStateChanged(WindowEvent e) {
                    if(e.getNewState() == JFrame.ICONIFIED){
                        frame.setVisible(false);
                    }
                }
            });
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                l.error(e);
            }
        }
        else {
            l.error("System tray not supported!");
        }
    }
}
