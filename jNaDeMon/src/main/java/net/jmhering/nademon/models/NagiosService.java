package net.jmhering.nademon.models;

import java.util.HashMap;

/**
 * Created by clupeidae on 09.06.15.
 */
public class NagiosService {
    private HashMap<String, String> details;
    private String name;

    public NagiosService(String name, HashMap<String, String> details) {
        this.details = details;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, String> getDetails() {
        return details;
    }

    public String getDetail(String name) {
        return details.get(name);
    }
}
