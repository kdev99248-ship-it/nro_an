package event;

/*
 * @Author: NgojcDev
 */

import consts.BossID;
import interfaces.Event;

public class HungVuong extends Event {

    @Override
    public void boss() {
        createBoss(BossID.THUY_TINH, 10);
    }
}
