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
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.io.IOException;
import java.net.UnknownHostException;
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


    public static String matchStateToWord(int state) {
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

    public static int matchWordToState(String state) {
        switch (state) {
            case "OK":
                return 0;
            case "WARN":
                return 1;
            case "CRIT":
                return 2;
            case "UNK":
                return 3;
            default:
                return 3;
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
        NagiosHostCollection nagios_hosts = new NagiosHostCollection();
        try {
            JsonReader j = new JsonReader("http://kjc-sv007:8181/ninfo.php");
            JSONObject json = j.getResult();
            nagios_hosts = NagiosJsonConverter.convertJsonToNagiosHosts(json);
            l.debug("Found " + nagios_hosts.size() + " Hosts.");
        }
        catch (IOException e) {
            l.error("Unable to connect to server. Please check network connectivity");
            HashMap<String, String> emptyService = new HashMap<String, String>();
            emptyService.put("plugin_output", "Connection failed!");
            emptyService.put("current_state", "2");
            NagiosService s = new NagiosService("Server connectivity", emptyService);
            HashMap<String, NagiosService> emptyHost = new HashMap<String, NagiosService>();
            emptyHost.put("Server connectivity", s);
            NagiosHost h = new NagiosHost("Nagios Server", emptyHost);
            nagios_hosts.add(h);
        }
        catch (JSONException e) {
            l.error("Unable to parse JSON output of script. Please check if script runs without errors");
            HashMap<String, String> emptyService = new HashMap<String, String>();
            emptyService.put("plugin_output", "Connection failed!");
            emptyService.put("current_state", "2");
            NagiosService s = new NagiosService("Server connectivity", emptyService);
            HashMap<String, NagiosService> emptyHost = new HashMap<String, NagiosService>();
            emptyHost.put("Server connectivity", s);
            NagiosHost h = new NagiosHost("Nagios Server", emptyHost);
            nagios_hosts.add(h);
        }

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
