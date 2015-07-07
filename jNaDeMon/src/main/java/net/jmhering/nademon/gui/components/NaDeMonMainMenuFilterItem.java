package net.jmhering.nademon.gui.components;

import net.jmhering.nademon.gui.MainGUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by clupeidae on 17.06.15.
 */
public class NaDeMonMainMenuFilterItem extends AbstractNaDeMonMainMenuItem {
    static final Logger l = LogManager.getLogger("NaDeMon");
    DefaultRowSorter sorter;
    String filter;
    String label;
    MainGUI gui;

    public NaDeMonMainMenuFilterItem(final String label, final DefaultRowSorter sorter, final String filter, MainGUI gui) {
        super(label);
        this.sorter = sorter;
        this.filter = filter;
        this.label = label;
        this.gui = gui;
    }

    @Override
    protected ActionListener performAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                l.trace(label + " button pressed.");
                RowFilter rf = RowFilter.regexFilter(filter, 3);
                l.trace(rf);
                sorter.setRowFilter(rf);
                gui.updateTableDescriptionLabel();
            }
        };
    }
}
