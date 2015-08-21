package net.jmhering.nademon.models.update.handlers;

import net.jmhering.nademon.gui.HostsView;
import net.jmhering.nademon.models.NagiosHostCollection;
import net.jmhering.nademon.models.update.NagiosConnectionUpdateHandlerInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by clupeidae on 21.08.15.
 */
public class HostViewUpdateHandler implements NagiosConnectionUpdateHandlerInterface {
    static final Logger l = LogManager.getLogger("NaDeMon");
    HostsView view;

    public HostViewUpdateHandler(HostsView view) {
        this.view = view;
    }
    @Override
    public void onUpdate(NagiosHostCollection hosts) {
        l.trace("Updating HostView");
        view.updateHosts();
    }
}
