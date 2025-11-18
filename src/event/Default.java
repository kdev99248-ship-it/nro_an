package event;

/*
 * @Author: NgojcDev
 */

import consts.BossID;
import interfaces.Event;

public class Default extends Event {

    @Override
    public void boss() {
        createBoss(BossID.BROLY, 50);
    }

}
