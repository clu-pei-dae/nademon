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
    private static final int prevHostCount = 20;
    private NagiosHostCollection[] previousHosts = new NagiosHostCollection[prevHostCount];
    private final static String defaultErrorMsg = "Everything ok.";
    private String errorMsg = defaultErrorMsg;
    private HashSet<NagiosConnectionUpdateHandlerInterface> updateHandlers = new HashSet<>();
    private boolean updateInProgess = false;
    private boolean threwError = false;
    private boolean hasData = false;
    private NagiosConnectionUpdater updater;

    public NagiosConnection (URL url) {
        this.url = url;

        // Add dummy data.
        NagiosHost dummyHost = NagiosFactory.getNagiosHost("Null", "Service Connectivity", "3", "Didn't download any data yet. Please wait.");
        NagiosHostCollection dummyNHC = new NagiosHostCollection();
        dummyNHC.add(dummyHost);
        hosts = dummyNHC;
        hasData = true;
    }

    /**
     * Reload the JSON file and save the new results to the object.
     * @return true if everything was ok, false else.
     */
    public void updateData() {
        if (!updateInProgess) {
            l.trace("Starting new update thread.");
            updater =  new NagiosConnectionUpdater();
            updater.start();
        }
        else {
            l.trace("Won't start new update thread as the old one is still running.");
        }
    }

    private void addPreviousHost(NagiosHostCollection hostCol) {
        l.trace("Saving NagiosHostCollection");
        int max = prevHostCount - 1;
        for (int i=max; i < 0; i--) {
            if (i == max) {
                previousHosts[max] = null;
            }
            else {
                previousHosts[i+1] = previousHosts[i];
            }
        }
        previousHosts[0] = hostCol;
    }

    public NagiosHostCollection getPreviousHostCollection(int idx) {
        if (idx > previousHosts.length) {
            throw new IndexOutOfBoundsException();
        }
        return previousHosts[idx];
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean isRunning() {
        return updateInProgess;
    }

    public boolean hasData() {
        return hasData;
    }

    public NagiosHostCollection getHosts() {
        return hosts;
    }

    public void addOnUpdateHandler(NagiosConnectionUpdateHandlerInterface handler) {
        updateHandlers.add(handler);
        handler.onUpdate(hosts);
    }

    public void removeOnUpdateHandler(NagiosConnectionUpdateHandlerInterface handler) {
        updateHandlers.remove(handler);
    }

    private void callUpdateHandlers() {
        for (NagiosConnectionUpdateHandlerInterface handler : updateHandlers) {
            l.trace("Calling update handler: " + handler.getClass());
            handler.onUpdate(getHosts());
        }
    }

    public boolean threwError() {
        return threwError;
    }

    private class NagiosConnectionUpdater extends Thread {
        public void run() {
            l.trace("Hi, I'm the update thread and I'm running.");
            // Read the Nagios URL, parse the JSON data and convert it to NagiosHostCollection
            try {
                // Lock the getter.
                updateInProgess = true;
                threwError = false;
                JsonReader j = new JsonReader(url.toString());
                JSONObject json = j.getResult();
                NagiosHostCollection newHosts = NagiosJsonConverter.convertJsonToNagiosHosts(json);
                l.debug("Found " + newHosts.size() + " Hosts.");
                addPreviousHost(hosts);
                hosts = newHosts;
                errorMsg = defaultErrorMsg;
                updateInProgess = false;
                hasData = true;
                callUpdateHandlers();
            }
            catch (IOException e) {
                String msg = "Unable to connect to server. Please check network connectivity";
                l.error(msg);
                errorMsg = msg;
                updateInProgess = false;
                threwError = true;
            }
            catch (JSONException e) {
                String msg = "Unable to parse JSON output of script. Please check if script runs without errors";
                l.error(msg);
                errorMsg = msg;
                updateInProgess = false;
                threwError = true;
            }
        }
    }
}
