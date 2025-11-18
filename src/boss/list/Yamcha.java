package boss.list;

/*
 * @Author: NgojcDev
 */

import consts.BossID;
import control.boss.BossesData;
import interfaces.The23rdMartialArtCongress;
import static consts.BossType.PHOBAN;
import player.Player;

public class Yamcha extends The23rdMartialArtCongress {

    public Yamcha(Player player) throws Exception {
        super(PHOBAN, BossID.YAMCHA, BossesData.YAMCHA);
        this.playerAtt = player;
    }
}
