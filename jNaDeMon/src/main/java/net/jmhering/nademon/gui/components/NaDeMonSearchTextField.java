package net.jmhering.nademon.gui.components;

import net.jmhering.nademon.gui.MainGUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

/**
 * Created by clupeidae on 29.07.15.
 */
public class NaDeMonSearchTextField extends JTextField {
    static final Logger l = LogManager.getLogger("NaDeMon");
    NaDeMonSearchTextField textfield;
    MainGUI gui;

    public NaDeMonSearchTextField(MainGUI ui) {
        super();
        this.textfield = this;
        this.gui = ui;

        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                search();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                search();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                search();
            }

            private void search() {
                textfield.setBackground(Color.WHITE);
                String searchFilter = textfield.getText();
                ArrayList<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>(4);
                try {
                    filters.add(RowFilter.regexFilter(searchFilter, 1));
                    filters.add(RowFilter.regexFilter(searchFilter, 2));
                    filters.add(RowFilter.regexFilter(searchFilter, 3));
                    filters.add(RowFilter.regexFilter(searchFilter, 4));
                    RowFilter filter = RowFilter.orFilter(filters);
                    gui.getSorter().setRowFilter(filter);
                }
                catch (PatternSyntaxException e) {
                    l.trace("Error in search syntax: " + searchFilter);
                    textfield.setBackground(Color.RED);
                }
            }
        });

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gui.toggleSearchBar.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                }
            }
        });
    }
}
