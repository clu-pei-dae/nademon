package net.jmhering.nademon.gui.components;

import net.jmhering.nademon.gui.InfoTableModel;
import net.jmhering.nademon.models.NagiosConstants;

import javax.swing.*;

/**
 * Created by clupeidae on 21.08.15.
 */
public class NaDeMonHostStatusCount extends JLabel {
    public NaDeMonHostStatusCount(int state, int count) {
        StringBuilder lbl = new StringBuilder("<html><body style='text-align: center;'>");
        lbl.append(NagiosConstants.matchStateToWord(state));
        lbl.append(":<br>");
        lbl.append(count);
        lbl.append("</body></html>");

        setBackground(NagiosConstants.matchStateToColor(state));
        setOpaque(true);
        setText(lbl.toString());
        setHorizontalAlignment(CENTER);
    }
}
