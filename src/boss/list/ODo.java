package boss.list;

/*
 * @Author: NgojcDev
 */

import consts.BossID;
import control.boss.BossesData;
import interfaces.The23rdMartialArtCongress;
import static consts.BossType.PHOBAN;
import player.Player;

public class ODo extends The23rdMartialArtCongress {

    public ODo(Player player) throws Exception {
        super(PHOBAN, BossID.O_DO, BossesData.O_DO);
        this.playerAtt = player;
    }
}
