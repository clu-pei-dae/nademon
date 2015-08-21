package net.jmhering.nademon;

import net.jmhering.nademon.gui.MainGUI;
import net.jmhering.nademon.json.JsonReader;
import net.jmhering.nademon.json.NagiosJsonConverter;
import net.jmhering.nademon.models.NagiosConnection;
import net.jmhering.nademon.models.NagiosHostCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by clupeidae on 09.06.15.
 */
public class NaDeMon {
    static final Logger l = LogManager.getLogger("NaDeMon");

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println(new File(NaDeMon.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).toString());

        l.trace("Started.");
        l.info("Reading config file...");
        final Wini config;
        try {
            config = new Wini(new File("nademon.ini"));

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    l.trace("Initializing MainGUI");
                    try {
                        NagiosConnection nagcon = new NagiosConnection(new URL(config.get("NaDeMon", "URL", String.class)));
                        MainGUI window = new MainGUI(config, nagcon);
                    } catch (MalformedURLException e) {
                        l.error("Error parsing the URL: " + config.get("NaDeMon", "URL", String.class));
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (FileNotFoundException e) {
            l.error("Unable to parse config file 'nademon.ini' in " + System.getProperty("user.dir") + ": File Not Found.");
        }
    }
}
