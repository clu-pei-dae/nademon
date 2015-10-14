package net.jmhering.nademon.models.update.handlers;

import net.jmhering.nademon.gui.notifications.NotificationPopUp;
import net.jmhering.nademon.models.NagiosConnection;
import net.jmhering.nademon.models.NagiosHost;
import net.jmhering.nademon.models.NagiosHostCollection;
import net.jmhering.nademon.models.NagiosService;
import net.jmhering.nademon.models.update.NagiosConnectionUpdateHandlerInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * Created by clupeidae on 26.08.15.
 */
public class NotificationUpdateHandler implements NagiosConnectionUpdateHandlerInterface {
    static final Logger l = LogManager.getLogger("NaDeMon");
    private NagiosConnection nagcon;
    static NotificationPopUp pu;

    public NotificationUpdateHandler(NagiosConnection nagcon) {
        this.nagcon = nagcon;
    }

    @Override
    public void onUpdate(NagiosHostCollection hosts) {
        l.trace("Starting to compare hosts");
        HashMap<String, NagiosHost> oldHosts = new HashMap<>();
        HashMap<String, HashMap<String, NagiosService>> changes = new HashMap<>();

        // Convert collection to HashMap.
        for (NagiosHost host : nagcon.getPreviousHostCollection(0)) {
            oldHosts.put(host.getHostname(), host);
        }

        for (NagiosHost host : hosts) {
            // We're gonna use this many times...
            String hostname = host.getHostname();

            // Skip if host not in old hosts: we have nothing to show.
            if (!oldHosts.containsKey(hostname)) {
                l.trace(hostname + " is not in old host list.");
                continue;
            }
            // Compare the hosts.
            HashMap<String, NagiosService> comparison = compareHosts(oldHosts.get(hostname), host);

            // If there are any changes, then we save them.
            if (comparison.size() > 0) {
                changes.put(hostname, comparison);
            }
            else {
                l.trace(hostname + " did not change.");
            }
        }

        // If there are any changes, we can show the notification pop up.
        if (changes.size() > 0) {
            pu = new NotificationPopUp(changes);
        }
        else {
            l.trace("No changes in host states.");
        }
    }

    /**
     * Compares two NagiosHosts.
     * @param hostOld
     * @param hostNew
     * @return
     */
    private HashMap<String, NagiosService> compareHosts(NagiosHost hostOld, NagiosHost hostNew) {
        HashMap<String, NagiosService> value = new HashMap<>();
        for (String serviceName : hostNew.getServices().keySet()) {
            // Skip, if service doesn't exist in old host.
            if (!hostOld.getServices().containsKey(serviceName)) {
                continue;
            }
            NagiosService newService = hostNew.getService(serviceName);
            NagiosService oldService = hostOld.getService(serviceName);

            int newState = new Integer(newService.getDetail("current_state"));
            int oldState = new Integer(oldService.getDetail("current_state"));

            // Skip, if service state is the same.
            if (newState == oldState) {
                continue;
            }
            value.put(serviceName, newService);
        }
        return value;
    }
}
