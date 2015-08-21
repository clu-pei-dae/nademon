package net.jmhering.nademon.gui;

import net.jmhering.nademon.models.NagiosConnection;
import net.jmhering.nademon.models.NagiosConstants;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Created by clupeidae on 10.06.15.
 */
public class InfoTable extends JTable{
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
            c.setBackground(NagiosConstants.matchStateToColor(NagiosConstants.matchWordToState(type)));
        }

        return c;
    }
}
