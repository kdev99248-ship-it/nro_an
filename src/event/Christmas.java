package event;

/*
 * @Author: NgojcDev
 */

import consts.BossID;
import interfaces.Event;

public class Christmas extends Event {

    @Override
    public void boss() {
        createBoss(BossID.ONG_GIA_NOEL, 30);
    }
}
