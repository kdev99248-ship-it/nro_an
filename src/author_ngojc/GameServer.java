package author_ngojc;

import static author_ngojc.DragonRun.timeStart;
import utils.Logger;
import utils.TimeUtil;

public class GameServer {

    public static void main(String[] args) {
        Logger.ngojc("   _  ___________     _______\n");
        Logger.ngojc("  / |/ / ___/ __ \\__ / / ___/\n");
        Logger.ngojc(" /    / (_ / /_/ / // / /__  \n");
        Logger.ngojc("/_/|_/\\___/\\____/\\___/\\___/  \n");
        timeStart = TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss");
        DragonRun.gI().run();
    }
}
