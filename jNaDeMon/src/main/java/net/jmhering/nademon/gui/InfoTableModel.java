package net.jmhering.nademon.gui;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import net.jmhering.nademon.json.JsonReader;
import net.jmhering.nademon.json.NagiosJsonConverter;
import net.jmhering.nademon.models.NagiosHost;
import net.jmhering.nademon.models.NagiosHostCollection;
import net.jmhering.nademon.models.NagiosService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by clupeidae on 10.06.15.
 */
public class InfoTableModel extends AbstractTableModel {
    static final Logger l = LogManager.getLogger("NaDeMon");
    private static InfoTableModel instance;
    private String[] columnNames = new String[5];
    private Object[][] table_data;

    private String matchStateToWord(int state) {
        switch (state) {
            case 0:
                return "OK";
            case 1:
                return "WARN";
            case 2:
                return "CRIT";
            case 3:
                return "UNK";
            default:
                return new Integer(state).toString();
        }
    }

    public static InfoTableModel getInstance() {
        if (InfoTableModel.instance == null) {
            InfoTableModel.instance = new InfoTableModel();
        }
        l.trace("Returning instance");
        l.trace(InfoTableModel.instance);
        return InfoTableModel.instance;
    }

    private InfoTableModel() {
        columnNames[0] = "#";
        columnNames[1] = "Host";
        columnNames[2] = "Service";
        columnNames[3] = "Status";
        columnNames[4] = "Description";
        this.updateData();
    }

    public void updateData() {
        JsonReader j = new JsonReader("http://kjc-sv007/ninfo.php");
        JSONObject json = j.getResult();
        NagiosHostCollection nagios_hosts = NagiosJsonConverter.convertJsonToNagiosHosts(json);
        l.debug("Found " + nagios_hosts.size() + " Hosts.");

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
                table_data[c_row][3] = matchStateToWord(new Integer(host.getService(service).getDetail("current_state")));
                table_data[c_row][4] = host.getService(service).getDetail("plugin_output");
                c_row++;
            }
        }

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
            if (table_data[i][1].equals("kjc-ws009") && table_data[i][2].equals("Reboot")) {
                l.debug ("WS009 Status: " + table_data[i][3]);
            }
            return table_data[i][i1];
        }
    }
}
