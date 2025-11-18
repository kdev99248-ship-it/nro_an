package event;

import author_ngojc.DragonRun;

/*
 * @Author: NgojcDev
 */

public class EventManager {

    private static EventManager instance;

    public static EventManager gI() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void init() {
        new Default().init();
        if (DragonRun.LUNNAR_NEW_YEAR) {
            new LunarNewYear().init();
        }
        if (DragonRun.INTERNATIONAL_WOMANS_DAY) {
            new InternationalWomensDay().init();
        }
        if (DragonRun.HALLOWEEN) {
            new Halloween().init();
        }
        if (DragonRun.CHRISTMAS) {
            new Christmas().init();
        }
        if (DragonRun.HUNG_VUONG) {
            new HungVuong().init();
        }
        if (DragonRun.TRUNG_THU) {
            new TrungThu().init();
        }
        if (DragonRun.TOP_UP) {
            new TopUp().init();
        }
    }
}
