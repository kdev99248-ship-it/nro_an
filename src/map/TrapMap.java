package map;

/*
 * @Author: NgojcDev
 */

import player.Player;
import services.PlayerService;
import services.EffectMapService;
import utils.Util;

public class TrapMap {

    public int x;
    public int y;
    public int w;
    public int h;
    public int effectId;
    public int dame;

    public void doPlayer(Player player) {
        if (this.effectId == 49) {
            if (!player.isDie() && Util.canDoWithTime(player.idMark.getLastTimeAnXienTrapBDKB(), 1000)) {
                player.injured(null, dame + (Util.nextInt(-10, 10) * dame / 100), false, false);
                PlayerService.gI().sendInfoHp(player);
                EffectMapService.gI().sendEffectMapToAllInMap(player.zone, effectId, 2, 1, player.location.x - 32, 1040,
                        1);
                player.idMark.setLastTimeAnXienTrapBDKB(System.currentTimeMillis());
            }
        }
    }

}
