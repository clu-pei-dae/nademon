package net.jmhering.nademon.json;

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
        NagiosHostCollection hosts = new NagiosHostCollection();
        JSONObject services = json.getJSONObject("services");
        for (String host : services.keySet()) {
            l.debug("Processing host: " + host);
            JSONObject host_data = services.getJSONObject(host);
            HashMap<String, NagiosService> host_services = new HashMap<String, NagiosService>();
            for (String service : host_data.keySet()) {
                l.trace("Processing service: " + service);
                JSONObject service_data = host_data.getJSONObject(service);
                HashMap<String, String> service_details = new HashMap<String, String>();
                for (String service_info : service_data.keySet()) {
                    service_details.put(service_info, service_data.getString(service_info));
                }
                NagiosService nagiosService = new NagiosService(service, service_details);
                host_services.put(service, nagiosService);
            }
            NagiosHost nagiosHost = new NagiosHost(host, host_services);
            hosts.add(nagiosHost);
        }
        return hosts;
    }
}
