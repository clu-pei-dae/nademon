package net.jmhering.nademon.json;

import net.jmhering.nademon.models.NagiosFactory;
import net.jmhering.nademon.models.NagiosHost;
import net.jmhering.nademon.models.NagiosHostCollection;
import net.jmhering.nademon.models.NagiosService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by clupeidae on 09.06.15.
 */
public class NagiosJsonConverter {
    static final Logger l = LogManager.getLogger("NaDeMon");

    public static NagiosHostCollection convertJsonToNagiosHosts(JSONObject json) {
        // The host collection that stores the hosts.
        NagiosHostCollection hosts = new NagiosHostCollection();

        // The JSONObject that stores the services.
        JSONObject services = json.getJSONObject("services");

        // Iterate over all hosts.
        for (String hostname : services.keySet()) {
            l.debug("Processing host: " + hostname);

            // The service data of the current host.
            JSONObject host_data = services.getJSONObject(hostname);

            // The host object of the current host.
            NagiosHost host = NagiosFactory.getNagiosHost(hostname);

            // Iterate over all services.
            for (String servicename : host_data.keySet()) {
                l.trace("Processing service: " + servicename);
                JSONObject service_data = host_data.getJSONObject(servicename);

                // Create the service object.
                NagiosService service = NagiosFactory.getNagiosService(servicename);

                // Iterate over all service information and add them to the service object.
                for (String service_info : service_data.keySet()) {
                    service.addDetail(service_info, service_data.getString(service_info));
                }

                // Add the service to the host.
                host.addService(service);
            }

            // Add the host to the NagiosHostCollection.
            hosts.add(host);
        }
        return hosts;
    }
}
