package net.jmhering.nademon.gui.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
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

    protected abstract ActionListener performAction();
}
