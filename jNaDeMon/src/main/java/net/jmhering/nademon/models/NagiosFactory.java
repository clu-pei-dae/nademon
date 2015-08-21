package net.jmhering.nademon.models;

import java.util.HashMap;

/**
 * Created by clupeidae on 21.08.15.
 */
public class NagiosFactory {
    /**
     * Create a host with a service with status and message.
     * @param hostname The hostname.
     * @param service The service name.
     * @param state The service's state.
     * @param message The message.
     * @return
     */
    public static NagiosHost getNagiosHost(String hostname, String service, String state, String message) {
        // Create the host.
        NagiosHost host = getNagiosHost(hostname);

        // Add the service.
        host.addService(getNagiosService(service, state, message));

        // return the host.
        return host;
    }

    /**
     * Creates an empty host object.
     * @param hostname The host's name.
     * @return
     */
    public static NagiosHost getNagiosHost(String hostname) {
        HashMap<String, NagiosService> emptyHost = new HashMap<String, NagiosService>();
        return new NagiosHost(hostname, emptyHost);
    }

    /**
     * Create a service with status and message
     * @param servicename The service name.
     * @param state The service's state.
     * @param message The service's message
     * @return
     */
    public static NagiosService getNagiosService(String servicename, String state, String message) {
        // Create the actual service.
        NagiosService service = getNagiosService(servicename);

        // Put the information to the service.
        service.addDetail("plugin_output", message);
        service.addDetail("current_state", state);

        // Create the actual service.
        return service;
    }

    /**
     * Create an empty service.
     * @param service The service's name.
     * @return An empty service.
     */
    public static NagiosService getNagiosService (String service) {
        // This HashMap stores the service's information.
        HashMap<String, String> emptyService = new HashMap<String, String>();

        // Create the actual service.
        return new NagiosService(service, emptyService);
    }
}
