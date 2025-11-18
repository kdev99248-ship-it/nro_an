package boss.list;

/*
 * @Author: NgojcDev
 */

import control.boss.Boss;
import consts.BossID;
import consts.BossStatus;
import control.boss.BossesData;
import java.util.Random;
import map.ItemMap;
import player.Player;
import services.Service;
import services.TaskService;
import utils.Util;

public class Pic extends Boss {

    public Pic() throws Exception {
        super(BossID.PIC, BossesData.PIC);
    }

    @Override
    public void reward(Player plKill) {
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);

        // Rơi vàng (ID 190)
        Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, 190, Util.nextInt(20000, 30001),
                this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 900000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); // To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public void doneChatS() {
        this.changeStatus(BossStatus.AFK);
    }

    @Override
    public void doneChatE() {
        if (this.parentBoss == null) {
            return;
        }
        this.parentBoss.changeStatus(BossStatus.ACTIVE);
    }

}
