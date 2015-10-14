package net.jmhering.nademon.gui.notifications;

import net.jmhering.nademon.models.NagiosConstants;
import net.jmhering.nademon.models.NagiosService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.HashMap;

/**
 * Created by clupeidae on 29.07.15.
 */
public class NotificationPopUp {
    static final Logger l = LogManager.getLogger("NaDeMon");

    public NotificationPopUp(HashMap<String, HashMap<String, NagiosService>> comparisonMap) {
        l.trace("Building notification message");
        StringBuilder msg = new StringBuilder();

        for (String hostname : comparisonMap.keySet()) {
            msg.append("<b>");
            msg.append(hostname);
            msg.append("</b><br>");
            HashMap<String, NagiosService> host = comparisonMap.get(hostname);
            for (String serviceName : host.keySet()) {
                NagiosService service = host.get(serviceName);
                msg.append(serviceName);
                msg.append(": ");
                msg.append(NagiosConstants.matchStateToWord(new Integer(service.getDetail("current_state"))));
                msg.append("<br>");
            }
        }
        l.trace("Showing notification popup");
        NotificationPopUpFrame p = new NotificationPopUpFrame(msg.toString());
    }

    public static void main(String[] args) {
        NotificationPopUpFrame p = new NotificationPopUpFrame("Hallo Welt");
    }
}
