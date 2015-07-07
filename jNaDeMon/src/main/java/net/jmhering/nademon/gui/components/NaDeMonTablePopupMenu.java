package net.jmhering.nademon.gui.components;

import net.jmhering.nademon.gui.InfoTable;
import net.jmhering.nademon.gui.InfoTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by clupeidae on 19.06.15.
 */
public class NaDeMonTablePopupMenu extends JPopupMenu {
    static final Logger l = LogManager.getLogger("NaDeMon");
    DefaultRowSorter sorter;

    public NaDeMonTablePopupMenu(final DefaultRowSorter sorter, InfoTable table) {
        if (table.getSelectedRow() == -1) {
            l.trace("No row was selected.");
            add(new JMenuItem("Please select a row first."));
        }
        else {
            this.sorter = sorter;

            JMenuItem lblCaption = new JMenuItem("Sort by");
            add(lblCaption);

            JMenuItem srtByHost = new JMenuItem("Host");
            add(srtByHost);
            srtByHost.addActionListener(getFilterListener((String) table.getValueAt(table.getSelectedRow(), 1), 1));

            JMenuItem srtByService = new JMenuItem("Service");
            add(srtByService);
            srtByService.addActionListener(getFilterListener((String) table.getValueAt(table.getSelectedRow(), 2), 2));}
    }
    private ActionListener getFilterListener(final String filter, final int col) {
        l.trace("Creating filter for " + filter);
        return new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                RowFilter rf = RowFilter.regexFilter(filter, col);
                l.trace(rf);
                sorter.setRowFilter(rf);
            }
        };
    }
}
