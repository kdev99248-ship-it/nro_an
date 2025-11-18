package control.boss;

import control.boss.BossManager;

/*
 * @Author: NgojcDev
 */

public class LunarNewYearEventManager extends BossManager {

    private static LunarNewYearEventManager instance;

    public static LunarNewYearEventManager gI() {
        if (instance == null) {
            instance = new LunarNewYearEventManager();
        }
        return instance;
    }

}
