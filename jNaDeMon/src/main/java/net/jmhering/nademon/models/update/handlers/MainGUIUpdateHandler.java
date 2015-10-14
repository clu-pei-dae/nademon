package net.jmhering.nademon.models.update.handlers;

import net.jmhering.nademon.gui.MainGUI;
import net.jmhering.nademon.models.NagiosHostCollection;
import net.jmhering.nademon.models.update.NagiosConnectionUpdateHandlerInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by clupeidae on 25.08.15.
 */
public class MainGUIUpdateHandler implements NagiosConnectionUpdateHandlerInterface {
    static final Logger l = LogManager.getLogger("NaDeMon");
    MainGUI gui;

    public MainGUIUpdateHandler(MainGUI gui) {
        this.gui = gui;
    }
    @Override
    public void onUpdate(NagiosHostCollection hosts) {
        l.trace("Updating MainGUI");
        gui.updateTableDescriptionLabel();
    }
}
