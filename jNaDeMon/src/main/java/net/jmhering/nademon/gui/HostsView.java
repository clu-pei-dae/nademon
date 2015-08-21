package net.jmhering.nademon.gui;

import net.jmhering.nademon.gui.components.NaDeMonHostPanel;
import net.jmhering.nademon.models.NagiosConnection;
import net.jmhering.nademon.models.NagiosHost;
import net.jmhering.nademon.models.NagiosHostCollection;
import net.jmhering.nademon.models.update.handlers.HostViewUpdateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

/**
 * Created by clupeidae on 19.08.15.
 */
public class HostsView {
    static final Logger l = LogManager.getLogger("NaDeMon");
    private static final int D_WIDTH = 800;
    private static final int D_HEIGHT = 600;
    private JPanel mainPanel;
    public JFrame frame;
    private MainGUI maingui;
    private NagiosConnection nagcon;
    private GridLayout mainLayout;

    public HostsView(Wini config, NagiosConnection nagcon, MainGUI maingui) {
        l.trace("HostsView created.");
        this.nagcon = nagcon;
        this.maingui = maingui;
        createUIComponents();
        nagcon.addOnUpdateHandler(new HostViewUpdateHandler(this));
        nagcon.updateData();
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        frame = new JFrame("MainGUI");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setTitle("Nagios Desktop Monitor");
        frame.setBackground(Color.WHITE);

        // The window panel.
        mainPanel = new JPanel();
        mainPanel.setMinimumSize(new Dimension(D_WIDTH, D_HEIGHT));
        mainPanel.setPreferredSize(new Dimension(D_WIDTH, D_HEIGHT));
        mainLayout = new GridLayout(0, 5, 5, 5);
        mainPanel.setLayout(mainLayout);
        mainPanel.setBackground(Color.WHITE);

        frame.setContentPane(mainPanel);

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                super.componentResized(componentEvent);
                setMainLayoutCols();
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });
    }

    protected void setMainLayoutCols() {
        if (frame.getWidth() > 150) {
            int newCols = frame.getWidth() / 150;
            mainLayout.setColumns(newCols);
        }
        else {
            mainLayout.setColumns(1);
        }
    }

    public void updateHosts() {
        mainPanel.removeAll();
        setMainLayoutCols();
        NagiosHostCollection hosts = nagcon.getHosts();
        for (NagiosHost host : hosts) {
            NaDeMonHostPanel hostpanel = new NaDeMonHostPanel(host, maingui);
            mainPanel.add(hostpanel);
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
