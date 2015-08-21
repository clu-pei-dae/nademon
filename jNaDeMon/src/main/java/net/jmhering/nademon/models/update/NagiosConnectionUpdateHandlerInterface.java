package net.jmhering.nademon.models.update;

import net.jmhering.nademon.models.NagiosHostCollection;

/**
 * Created by clupeidae on 21.08.15.
 */
public interface NagiosConnectionUpdateHandlerInterface {
    public void onUpdate(NagiosHostCollection hosts);
}
