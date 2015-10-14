package net.jmhering.nademon.gui;

import net.jmhering.nademon.models.*;
import net.jmhering.nademon.models.update.handlers.InfoTableModelUpdateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.util.*;
/**
 * Created by clupeidae on 10.06.15.
 */

public class InfoTableModel extends AbstractTableModel {
    static final Logger l = LogManager.getLogger("NaDeMon");
    private static InfoTableModel instance;
    private String[] columnNames = new String[5];
    private Object[][] table_data;
    private String lastUpdated = "";
    private NagiosConnection nagcon;


    public static InfoTableModel getInstance(NagiosConnection nagcon) {
        if (InfoTableModel.instance == null) {
            InfoTableModel.instance = new InfoTableModel(nagcon);
        }
        l.trace("Returning instance");
        l.trace(InfoTableModel.instance);
        return InfoTableModel.instance;
    }

    private InfoTableModel(NagiosConnection nagcon) {
        this.nagcon = nagcon;

        columnNames[0] = "#";
        columnNames[1] = "Host";
        columnNames[2] = "Service";
        columnNames[3] = "Status";
        columnNames[4] = "Description";

        // Insert dummy data.
        table_data = new Object[1][columnNames.length];
        table_data[0][0] = "0";
        table_data[0][1] = "None";
        table_data[0][2] = "Service Connection";
        table_data[0][3] = "3";
        table_data[0][4] = "No data loaded yet.";

        // Add update handler.
        nagcon.addOnUpdateHandler(new InfoTableModelUpdateHandler(this));
    }

    public void updateData() {
        NagiosHostCollection nagios_hosts = nagcon.getHosts();

        // Get number of services in total.
        int c_services = 0;
        for (NagiosHost h : nagios_hosts) {
            c_services += h.getServices().size();
        }

        // Instantiate the table data.
        table_data = new Object[c_services][columnNames.length];

        // Row counter
        int c_row = 0;

        // Insert data into table_data array.
        for (NagiosHost host : nagios_hosts) {
            String hostname = host.getHostname();
            for (String service : host.getServices().keySet()) {
                table_data[c_row][0] = new String(new Integer(c_row).toString());
                table_data[c_row][1] = hostname;
                table_data[c_row][2] = service;
                table_data[c_row][3] = NagiosConstants.matchStateToWord(new Integer(host.getService(service).getDetail("current_state")));
                table_data[c_row][4] = host.getService(service).getDetail("plugin_output");
                c_row++;
            }
        }

        this.lastUpdated = new Date().toString();



        // fireTableRowsInserted(0, c_row - 1);
        fireTableDataChanged();
    }

    public int getRowCount() {
        return table_data.length;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int i) {
        return columnNames[i];
    }

    public Class<?> getColumnClass(int i) {
        if (i == 0) {
            return int.class;
        }
        else {
            return String.class;
        }
    }

    public boolean isCellEditable(int i, int i1) {
        return false;
    }

    public Object getValueAt(int i, int i1) {
        if (i1 == 0) {
            return i;
        }
        else {
            return table_data[i][i1];
        }
    }

    public String getLastUpdated() {
        return lastUpdated;
    }
}
