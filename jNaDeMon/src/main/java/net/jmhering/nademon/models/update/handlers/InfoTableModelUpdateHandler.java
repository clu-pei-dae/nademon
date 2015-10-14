package net.jmhering.nademon.models.update.handlers;

import net.jmhering.nademon.gui.InfoTableModel;
import net.jmhering.nademon.gui.MainGUI;
import net.jmhering.nademon.models.NagiosHostCollection;
import net.jmhering.nademon.models.update.NagiosConnectionUpdateHandlerInterface;

/**
 * Created by clupeidae on 21.08.15.
 */
public class InfoTableModelUpdateHandler implements NagiosConnectionUpdateHandlerInterface {
    private InfoTableModel tblModel;

    public InfoTableModelUpdateHandler(InfoTableModel tblModel) {
        this.tblModel = tblModel;
    }

    @Override
    public void onUpdate(NagiosHostCollection hosts) {
        tblModel.updateData();
    }
}
