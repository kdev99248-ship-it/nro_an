package boss.list;

/*
 * @Author: NgojcDev
 */

import consts.BossID;
import control.boss.BossesData;
import interfaces.DeathOrAliveArena;
import static consts.BossType.PHOBAN;
import player.Player;

public class ThoDauBac extends DeathOrAliveArena {

    public ThoDauBac(Player player) throws Exception {
        super(PHOBAN, BossID.THO_DAU_BAC, BossesData.THO_DAU_BAC);
        this.playerAtt = player;
    }
}
