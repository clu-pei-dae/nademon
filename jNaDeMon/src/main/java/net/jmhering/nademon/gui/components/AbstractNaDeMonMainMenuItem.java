package net.jmhering.nademon.gui.components;

import net.jmhering.nademon.gui.MainGUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by clupeidae on 17.06.15.
 */
public abstract class AbstractNaDeMonMainMenuItem extends JMenuItem {
    static final Logger l = LogManager.getLogger("NaDeMon");

    public AbstractNaDeMonMainMenuItem(String label) {
        this.setLayout(new FlowLayout());
        JLabel lblCaption = new JLabel(label);
        this.add(lblCaption);
        this.setPreferredSize(new Dimension((int) (Math.log(lblCaption.getText().length()) * 60), 30));
        this.addActionListener(this.performAction());
    }

    public AbstractNaDeMonMainMenuItem(String label, String shortcut) {
        this(label);
        KeyStroke stroke = KeyStroke.getKeyStroke(shortcut);
        if (stroke == null) {
            l.error("The selected keyboard shortcut is formatted wrong: " + shortcut);
        }
        this.getActionMap().put("keyPress", keypressAction());
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "keyPress");
    }

    protected Action keypressAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                l.trace("Key pressed");
                performAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            }
        };
    }

    protected abstract ActionListener performAction();
}
