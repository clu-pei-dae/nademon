package net.jmhering.nademon.gui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Created by clupeidae on 10.06.15.
 */
public class InfoTable extends JTable{
    public static final Color OK = new Color(169, 255, 163);
    public static final Color WARN = new Color(255, 255, 163);
    public static final Color CRIT = new Color(222, 27, 27);
    public static final Color UNK = new Color(248, 248, 255);

    public InfoTable() {
        super();
    }

    public InfoTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public InfoTable(Object[][] rowData, Object[] columnNames) {
        super(rowData,columnNames);
    }

    public InfoTable(TableModel dm) {
        super(dm);
    }

    public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
    {
        Component c = super.prepareRenderer(renderer, row, column);

        //  Color row based on a cell value

        if (!isRowSelected(row))
        {
            c.setBackground(getBackground());
            int modelRow = convertRowIndexToModel(row);
            String type = (String)getModel().getValueAt(modelRow, 3);
            if ("OK".equals(type)) c.setBackground(OK);
            if ("WARN".equals(type)) c.setBackground(WARN);
            if ("CRIT".equals(type)) c.setBackground(CRIT);
            if ("UNK".equals(type)) c.setBackground(UNK);
        }

        return c;
    }
}
