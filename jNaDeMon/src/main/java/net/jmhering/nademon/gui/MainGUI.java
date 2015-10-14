package net.jmhering.nademon.gui;

import net.jmhering.nademon.gui.components.NaDeMonMainMenuFilterItem;
import net.jmhering.nademon.gui.components.NaDeMonMainMenuSimpleItem;
import net.jmhering.nademon.gui.components.NaDeMonSearchTextField;
import net.jmhering.nademon.gui.components.NaDeMonTablePopupMenu;
import net.jmhering.nademon.models.*;
import net.jmhering.nademon.models.update.handlers.HostViewUpdateHandler;
import net.jmhering.nademon.models.update.handlers.MainGUIUpdateHandler;
import net.jmhering.nademon.models.update.handlers.NotificationUpdateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 * Created by clupeidae on 09.06.15.
 */
public class MainGUI {
    static final Logger l = LogManager.getLogger("NaDeMon");

    private static final int D_WIDTH = 1200;
    private static final int D_HEIGHT = 800;
    private static int UPDATE_INTERVAL = 5000;
    private JTextField searchText;
    private JLabel lblLastUpdate;
    private JPanel mainPanel;
    private InfoTable tblInfo;
    private JScrollPane scpScrollPane;
    private JPanel menuPanel;
    private JMenuBar menuMain;
    private JMenuBar menuSearch;
    private JFrame frame;
    private Wini config;
    private NagiosConnection nagcon;
    public ActionListener toggleSearchBar;
    private NagiosHostCollection hosts;
    protected InfoTableModel tblModel;
    protected TableRowSorter sorter;
    private SystemTray tray;
    private HostsView hostview;

    public MainGUI(Wini config, NagiosConnection nagcon) throws MalformedURLException {
        this.config = config;
        this.nagcon = nagcon;


        tblModel = InfoTableModel.getInstance(nagcon);
        hostview = new HostsView(config, nagcon, this);

        nagcon.updateData();

        UPDATE_INTERVAL = this.config.get("NaDeMon", "updateInterval", int.class) * 1000;
        l.info("Setting update interval to " + UPDATE_INTERVAL);
        l.trace("Creating MainGUI");
        try {
            tray = SystemTray.getSystemTray();
        }
        catch (UnsupportedOperationException e) {
            l.debug("System tray not available.");
        }


        createUIComponents();
        frame.pack();
        frame.setVisible(true);
        l.trace("Window showed.");
        l.trace("Creating HostView");

        l.trace("HostView created.");
        nagcon.addOnUpdateHandler(new MainGUIUpdateHandler(this));
        nagcon.addOnUpdateHandler(new NotificationUpdateHandler(nagcon));
        startUpdateTimer();
    }

    private void startUpdateTimer() {
        l.trace("Starting update time");
        Timer timer = new Timer(UPDATE_INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                nagcon.updateData();            }
        });
        timer.setRepeats(true);
        timer.start();
    }
    public boolean searchHasFocus() {
        return searchText.hasFocus();
    }

    public TableRowSorter getSorter() {
        return sorter;
    }

    public void updateTableDescriptionLabel() {
        lblLastUpdate.setText("#" + tblInfo.getRowCount() + " - Last Update: " + tblModel.getLastUpdated());
    }

    private void createUIComponents() {
        // The window.
        frame = new JFrame("MainGUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Nagios Desktop Monitor");
        frame.setBackground(Color.WHITE);

        // The window panel.
        mainPanel = new JPanel();
        mainPanel.setMinimumSize(new Dimension(D_WIDTH, D_HEIGHT));
        mainPanel.setPreferredSize(new Dimension(D_WIDTH, D_HEIGHT));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
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
                int t_int = NagiosConstants.matchWordToState((String)t);
                int t1_int = NagiosConstants.matchWordToState((String)t1);

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
        scpScrollPane.setBackground(Color.WHITE);
        mainPanel.add(scpScrollPane, BorderLayout.CENTER);

        // Pane that contains the bars and menus.
        menuPanel = new JPanel();
        menuPanel.setLayout(new GridBagLayout());
        menuPanel.setBackground(Color.WHITE);
        mainPanel.add(menuPanel, BorderLayout.NORTH);

        // The search bar.
        menuSearch = new JMenuBar();
        GroupLayout searchLayout = new GroupLayout(menuSearch);
        searchLayout.setAutoCreateGaps(true);
        searchLayout.setAutoCreateContainerGaps(true);
        menuSearch.setLayout(searchLayout);
        menuSearch.setBorderPainted(false);
        menuSearch.setBackground(Color.WHITE);
        JLabel lblSearch = new JLabel("Search for");
        lblSearch.setHorizontalAlignment(SwingConstants.LEFT);
        //searchText = new JTextField();
        searchText = new NaDeMonSearchTextField(this);
        searchLayout.setHorizontalGroup(searchLayout.createSequentialGroup()
                        .addComponent(lblSearch)
                        .addComponent(searchText)
        );
        searchLayout.setVerticalGroup(searchLayout.createSequentialGroup()
                        .addGroup(searchLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSearch)
                                .addComponent(searchText))
        );


        // The menu bar.
        menuMain = new JMenuBar();
        FlowLayout menuLayout = new FlowLayout();
        menuLayout.setAlignment(FlowLayout.LEFT);
        menuMain.setLayout(menuLayout);
        menuMain.setBackground(Color.WHITE);
        menuMain.setBorderPainted(false);
        GridBagConstraints menuMainConstraints = new GridBagConstraints();
        menuMainConstraints.gridx = 0;
        menuMainConstraints.gridy = 0;
        menuMainConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuPanel.add(menuMain, menuMainConstraints);

        JLabel lblFilter = new JLabel("Filter by Status: ");
        menuMain.add(lblFilter);

        // The OK Filter button.
        NaDeMonMainMenuFilterItem miOK = new NaDeMonMainMenuFilterItem("OK", sorter, ".*OK.*", this, "O");
        menuMain.add(miOK);

        // The WARN Filter button.
        NaDeMonMainMenuFilterItem miWARN = new NaDeMonMainMenuFilterItem("WARN", sorter, ".*WARN.*", this, "W");
        menuMain.add(miWARN);

        // The CRIT Filter button.
        NaDeMonMainMenuFilterItem miCRIT = new NaDeMonMainMenuFilterItem("CRIT", sorter, ".*CRIT.*", this, "C");
        menuMain.add(miCRIT);

        // The ALL Filter button.
        final NaDeMonMainMenuFilterItem miALL = new NaDeMonMainMenuFilterItem("ALL", sorter, ".?", this, "A");
        menuMain.add(miALL);

        menuMain.add(new JSeparator());

        // Refresh button.
        ActionListener refreshAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                nagcon.updateData();
            }
        };
        NaDeMonMainMenuSimpleItem miREFRESH = new NaDeMonMainMenuSimpleItem("Refresh", refreshAction, "R");
        menuMain.add(miREFRESH);

        menuMain.add(new JSeparator());

        final GridBagConstraints menuSearchConstraints = new GridBagConstraints();
        menuSearchConstraints.gridx = 0;
        menuSearchConstraints.gridy = 1;
        menuSearchConstraints.fill = GridBagConstraints.HORIZONTAL;

        toggleSearchBar = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (Arrays.asList(menuPanel.getComponents()).contains(menuSearch)) {
                    tblInfo.grabFocus();
                    l.trace("Removing search bar.");
                    menuPanel.remove(menuSearch);
                    miALL.getActionListeners()[0].actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                }
                else {
                    l.trace("Adding search bar.");
                    menuPanel.add(menuSearch, menuSearchConstraints);
                    searchText.grabFocus();
                }
                l.trace("Updating UI.");
                menuPanel.validate();
                menuPanel.repaint();
                mainPanel.validate();
                mainPanel.repaint();
            }
        };

        NaDeMonMainMenuSimpleItem miSEARCH = new NaDeMonMainMenuSimpleItem("Search", toggleSearchBar, "F3");
        menuMain.add(miSEARCH);

        ActionListener showHosts = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!hostview.frame.isVisible()) {
                    hostview.frame.setVisible(true);
                }
            }
        };

        NaDeMonMainMenuSimpleItem miHOSTS = new NaDeMonMainMenuSimpleItem("Hosts", showHosts, "F12");
        menuMain.add(miHOSTS);

        lblLastUpdate = new JLabel("Last Update: #####");
        menuMain.add(lblLastUpdate);

        // Tray icon.
        if (SystemTray.isSupported()) {
            l.trace("System tray supported");
            BufferedImage trayIconImage = null;
            try {
                // Load tray icon.
                trayIconImage = ImageIO.read(ClassLoader.getSystemClassLoader().getResource("icon.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;

            // Create the actual tray icon.
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
