package net.jmhering.nademon;

import net.jmhering.nademon.gui.MainGUI;
import net.jmhering.nademon.json.JsonReader;
import net.jmhering.nademon.json.NagiosJsonConverter;
import net.jmhering.nademon.models.NagiosHostCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by clupeidae on 09.06.15.
 */
public class NaDeMon {
    static final Logger l = LogManager.getLogger("NaDeMon");

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println(new File(NaDeMon.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).toString());

        l.trace("Started.");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                l.trace("Initializing MainGUI");
                MainGUI window = new MainGUI();
            }
        });

    }
}
