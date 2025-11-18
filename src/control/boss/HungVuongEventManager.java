package control.boss;

import control.boss.BossManager;

/*
 * @Author: NgojcDev
 */

public class HungVuongEventManager extends BossManager {

    private static HungVuongEventManager instance;

    public static HungVuongEventManager gI() {
        if (instance == null) {
            instance = new HungVuongEventManager();
        }
        return instance;
    }

}
