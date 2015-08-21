package net.jmhering.nademon.models;

import java.awt.*;

/**
 * Created by clupeidae on 21.08.15.
 */
public class NagiosConstants {
    public static final int STATE_OK = 0;
    public static final int STATE_WARN = 1;
    public static final int STATE_CRIT = 2;
    public static final int STATE_UNK = 3;

    public static final Color COLOR_OK = new Color(169, 255, 163);
    public static final Color COLOR_WARN = new Color(254, 255, 193);
    public static final Color COLOR_CRIT = new Color(255, 221, 221);
    public static final Color COLOR_UNK = new Color(255, 221, 170);

    public static String matchStateToWord(int state) {
        switch (state) {
            case NagiosConstants.STATE_OK:
                return "OK";
            case NagiosConstants.STATE_WARN:
                return "WARN";
            case NagiosConstants.STATE_CRIT:
                return "CRIT";
            case NagiosConstants.STATE_UNK:
                return "UNK";
            default:
                return new Integer(state).toString();
        }
    }

    public static int matchWordToState(String state) {
        switch (state) {
            case "OK":
                return NagiosConstants.STATE_OK;
            case "WARN":
                return NagiosConstants.STATE_WARN;
            case "CRIT":
                return NagiosConstants.STATE_CRIT;
            case "UNK":
                return NagiosConstants.STATE_UNK;
            default:
                return NagiosConstants.STATE_UNK;
        }
    }

    public static Color matchStateToColor(int state) {
        switch (state) {
            case NagiosConstants.STATE_OK:
                return COLOR_OK;
            case NagiosConstants.STATE_WARN:
                return COLOR_WARN;
            case NagiosConstants.STATE_CRIT:
                return COLOR_CRIT;
            case NagiosConstants.STATE_UNK:
                return COLOR_UNK;
            default:
                return COLOR_UNK;
        }
    }
}
