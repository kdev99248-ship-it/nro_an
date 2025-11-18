package boss.list;

/*
 * @Author: NgojcDev
 */

import consts.BossID;
import control.boss.BossesData;
import interfaces.The23rdMartialArtCongress;
import static consts.BossType.PHOBAN;
import player.Player;

public class SoiHecQuyn_DHVT extends The23rdMartialArtCongress {

    public SoiHecQuyn_DHVT(Player player) throws Exception {
        super(PHOBAN, BossID.SOI_HEC_QUYN, BossesData.SOI_HEC_QUYN);
        this.playerAtt = player;
    }
}
