package boss.list;

import consts.BossID;
import control.boss.Boss;
import control.boss.BossesData;
import java.util.Random;
import map.ItemMap;
import player.Player;
import services.EffectSkillService;
import services.ItemService;
import services.Service;
import services.TaskService;
import utils.Util;

public class Cumber extends Boss {

    private long st;

    public Cumber() throws Exception {
        super(BossID.CUMBER, BossesData.CUMBER, BossesData.CUMBER_2);
    }

     @Override
    public void reward(Player plKill) {
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);

        // Rơi vàng (ID 190)
        Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, 190, Util.nextInt(20000, 30001),
                this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));

        // 50% xác suất rơi thêm vật phẩm
        if (Util.isTrue(80, 100)) {
            int[] items = {16, 17, 18};
            int randomItem = items[new Random().nextInt(items.length)];
            Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, randomItem, 1,
                    this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }

        if (Util.isTrue(20, 100)) {
            ItemMap it = ItemService.gI().randDoTL(this.zone, 1, this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x,
                            this.location.y - 24),
                    plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }

        int pointToAdd = Util.nextInt(3, 6);
        plKill.pointBoss += pointToAdd;
        Service.gI().sendThongBao(plKill, "Bạn nhận được " + pointToAdd + " điểm săn boss");
    }

    @Override
    public synchronized int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (piercing) {
                damage /= 100;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return (int) damage;
        } else {
            return 0;
        }
    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
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

}
