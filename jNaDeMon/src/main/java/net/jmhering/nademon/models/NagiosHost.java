package net.jmhering.nademon.models;

import java.util.HashMap;

/**
 * Created by clupeidae on 09.06.15.
 */
public class NagiosHost {
    /**
     *
     */
    private String hostname;
    private HashMap<String, NagiosService> services;

    public NagiosHost(String host, HashMap<String, NagiosService> serv) {
        hostname = host;
        services = serv;
    }

    public String getHostname() {
        return hostname;
    }

    public HashMap<String, NagiosService> getServices() {
        return services;
    }

    public NagiosService getService(String service) {
        return services.get(service);
    }
}
