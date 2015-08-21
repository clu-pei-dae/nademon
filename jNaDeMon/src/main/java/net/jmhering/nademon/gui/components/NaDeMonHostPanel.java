package net.jmhering.nademon.gui.components;

import net.jmhering.nademon.gui.MainGUI;
import net.jmhering.nademon.models.NagiosConstants;
import net.jmhering.nademon.models.NagiosHost;
import net.jmhering.nademon.models.NagiosService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by clupeidae on 21.08.15.
 */
public class NaDeMonHostPanel extends JPanel {
    static final Logger l = LogManager.getLogger("NaDeMon");
    NagiosHost host;
    JPanel innerPanel;
    MainGUI maingui;

    public NaDeMonHostPanel(NagiosHost host, MainGUI maingui) {
        this.maingui = maingui;
        l.trace("Addding HostPanel for host " + host.getHostname());
        innerPanel = new JPanel();
        innerPanel.setLayout(new BorderLayout());
        innerPanel.setPreferredSize(new Dimension(145, 120));
        this.add(innerPanel);
        this.host = host;
        addComponents();
    }

    private void addComponents() {
        int servicesOK = getNumberOfState(NagiosConstants.STATE_OK);
        int servicesWARN = getNumberOfState(NagiosConstants.STATE_WARN);
        int servicesCRIT = getNumberOfState(NagiosConstants.STATE_CRIT);
        int servicesUNK = getNumberOfState(NagiosConstants.STATE_UNK);

        JLabel lblName = new JLabel(host.getHostname());
        lblName.setHorizontalAlignment(JLabel.CENTER);
        lblName.setPreferredSize(new Dimension(100, 50));
        lblName.addMouseListener(performFilter(host.getHostname()));
        innerPanel.add(lblName, BorderLayout.PAGE_START);

        NaDeMonHostStatusCount lblOK = new NaDeMonHostStatusCount(NagiosConstants.STATE_OK, servicesOK);
        lblOK.addMouseListener(performFilter(host.getHostname(), NagiosConstants.matchStateToWord(NagiosConstants.STATE_OK)));
        innerPanel.add(lblOK, BorderLayout.LINE_START);

        NaDeMonHostStatusCount lblWARN = new NaDeMonHostStatusCount(NagiosConstants.STATE_WARN, servicesWARN);
        lblWARN.addMouseListener(performFilter(host.getHostname(), NagiosConstants.matchStateToWord(NagiosConstants.STATE_WARN)));
        innerPanel.add(lblWARN, BorderLayout.CENTER);

        NaDeMonHostStatusCount lblCRIT = new NaDeMonHostStatusCount(NagiosConstants.STATE_CRIT, servicesCRIT);
        lblCRIT.addMouseListener(performFilter(host.getHostname(), NagiosConstants.matchStateToWord(NagiosConstants.STATE_CRIT)));
        innerPanel.add(lblCRIT, BorderLayout.LINE_END);

        NaDeMonHostStatusCount lblUNK = new NaDeMonHostStatusCount(NagiosConstants.STATE_UNK, servicesUNK);
        lblUNK.addMouseListener(performFilter(host.getHostname(), NagiosConstants.matchStateToWord(NagiosConstants.STATE_UNK)));
        innerPanel.add(lblUNK, BorderLayout.PAGE_END);

        int mostWeightState = 4;
        int mostWeightStateCount = 0;
        if (servicesOK >= mostWeightStateCount) {
            mostWeightState = NagiosConstants.STATE_OK;
            mostWeightStateCount = servicesOK;
        }
        else if (servicesWARN >= mostWeightStateCount) {
            mostWeightState = NagiosConstants.STATE_WARN;
            mostWeightStateCount = servicesWARN;
        }
        else if (servicesCRIT >= mostWeightStateCount) {
            mostWeightState = NagiosConstants.STATE_CRIT;
            mostWeightStateCount = servicesCRIT;
        }
        else if (servicesUNK >= mostWeightStateCount) {
            mostWeightState = NagiosConstants.STATE_UNK;
            mostWeightStateCount = servicesUNK;
        }

        setBackground(NagiosConstants.matchStateToColor(mostWeightState));
    }

    private MouseAdapter performFilter(final String hostname, final String state) {
        return new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                l.trace("Filtering for hostname: " + hostname + " Status: " + state);
                ArrayList<RowFilter<Object, Object>> rfs = new ArrayList<RowFilter<Object, Object>>(2);
                rfs.add(RowFilter.regexFilter(hostname, 1));
                rfs.add(RowFilter.regexFilter(state, 3));
                RowFilter rf = RowFilter.andFilter(rfs);
                maingui.getSorter().setRowFilter(null);
                maingui.getSorter().setRowFilter(rf);
                maingui.updateTableDescriptionLabel();
            }
        };
    }

    private MouseAdapter performFilter(final String hostname) {
        return new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                l.trace("Filtering for hostname: " + hostname);
                ArrayList<RowFilter<Object, Object>> rfs = new ArrayList<RowFilter<Object, Object>>(2);
                rfs.add(RowFilter.regexFilter(hostname, 1));
                RowFilter rf = RowFilter.andFilter(rfs);
                maingui.getSorter().setRowFilter(null);
                maingui.getSorter().setRowFilter(rf);
                maingui.updateTableDescriptionLabel();
            }
        };
    }

    private int getNumberOfState(int state) {
        String stateAsString = new String("" + state);
        int counter = 0;
        for (NagiosService service : host.getServices().values()) {
            if (service.getDetail("current_state").equals(stateAsString)) {
                counter++;
            }
        }
        return counter;
    }
}
