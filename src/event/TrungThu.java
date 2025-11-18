package event;

/*
 * @Author: NgojcDev
 */

import consts.BossID;
import interfaces.Event;

public class TrungThu extends Event {

    @Override
    public void boss() {
        createBoss(BossID.KHIDOT, 10);
        createBoss(BossID.NGUYETTHAN, 10);
    }
}
