package net.jmhering.nademon.gui.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by clupeidae on 17.06.15.
 */
public class NaDeMonMainMenuSimpleItem extends AbstractNaDeMonMainMenuItem {
    static final Logger l = LogManager.getLogger("NaDeMon");
    ActionListener action;
    String label;

    public NaDeMonMainMenuSimpleItem(final String label, ActionListener action) {
        super(label);
        this.action = action;
        this.label = label;
    }

    public NaDeMonMainMenuSimpleItem(final String label, ActionListener action, String shortcut) {
        super(label, shortcut);
        this.action = action;
        this.label = label;
    }

    @Override
    protected ActionListener performAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                l.trace(label + " button pressed.");
                action.actionPerformed(actionEvent);
            }
        };
    }
}
