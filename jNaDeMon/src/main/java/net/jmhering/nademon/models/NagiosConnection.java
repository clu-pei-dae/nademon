package net.jmhering.nademon.models;

import net.jmhering.nademon.models.update.NagiosConnectionUpdateHandlerInterface;
import net.jmhering.nademon.json.JsonReader;
import net.jmhering.nademon.json.NagiosJsonConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;

/**
 * Created by clupeidae on 21.08.15.
 */
public class NagiosConnection {
    static final Logger l = LogManager.getLogger("NaDeMon");
    private URL url;
    private NagiosHostCollection hosts;
    private String errorMsg;
    private HashSet<NagiosConnectionUpdateHandlerInterface> updateHandlers = new HashSet<>();
    private boolean updateInProgess = false;

    public NagiosConnection (URL url) {
        this.url = url;
    }

    /**
     * Reload the JSON file and save the new results to the object.
     * @return true if everything was ok, false else.
     */
    public boolean updateData() {
        // Lock the getter.
        updateInProgess = true;

        // Create new object that will store the data.
        NagiosHostCollection nagiosHosts = new NagiosHostCollection();

        // Read the Nagios URL, parse the JSON data and convert it to NagiosHostCollection
        try {
            JsonReader j = new JsonReader(url.toString());
            JSONObject json = j.getResult();
            hosts = NagiosJsonConverter.convertJsonToNagiosHosts(json);
            l.debug("Found " + nagiosHosts.size() + " Hosts.");
            updateInProgess = false;
            callUpdateHandlers();
        }
        catch (IOException e) {
            String msg = "Unable to connect to server. Please check network connectivity";
            l.error(msg);
            errorMsg = msg;
            updateInProgess = false;
            return false;
        }
        catch (JSONException e) {
            String msg = "Unable to parse JSON output of script. Please check if script runs without errors";
            l.error(msg);
            errorMsg = msg;
            updateInProgess = false;
            return false;
        }
        return true;
    }

    public String getErrorMsg() {
        while (updateInProgess) {
            // wait.
        }
        return errorMsg;
    }

    public NagiosHostCollection getHosts() {
        if (updateInProgess) {
            l.debug("Update in progress. Waiting until finished.");
        }
        while (updateInProgess) {
            // wait.
        }
        return hosts;
    }

    public void addOnUpdateHandler(NagiosConnectionUpdateHandlerInterface handler) {
        updateHandlers.add(handler);
    }

    private void callUpdateHandlers() {
        for (NagiosConnectionUpdateHandlerInterface handler : updateHandlers) {
            l.trace("Calling update handler: " + handler.getClass());
            handler.onUpdate(getHosts());
        }
    }
}
