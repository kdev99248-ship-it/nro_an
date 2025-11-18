package boss.list;

/*
 * @Author: NgojcDev
 */

import consts.BossID;
import control.boss.BossesData;
import interfaces.The23rdMartialArtCongress;
import static consts.BossType.PHOBAN;
import player.Player;

public class Pocolo_dhvt extends The23rdMartialArtCongress {

    public Pocolo_dhvt(Player player) throws Exception {
        super(PHOBAN, BossID.JACKY_CHUN, BossesData.JACKY_CHUN);
        this.playerAtt = player;
    }
}
