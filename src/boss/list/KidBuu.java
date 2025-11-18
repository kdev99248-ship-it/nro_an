package boss.list;

/*
 * @Author: NgojcDev
 */
import control.boss.Boss;
import consts.BossID;
import control.boss.BossesData;
import map.ItemMap;
import player.Player;
import services.Service;
import services.TaskService;
import utils.Util;
import java.util.Random;

public class KidBuu extends Boss {

    public KidBuu() throws Exception {
        super(BossID.KID_BUU, BossesData.KID_BUU);

    }

    @Override
    public void reward(Player plKill) {
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);

        // Luôn rơi bình hút năng lượng
        ItemMap binhHutNangLuong = new ItemMap(this.zone, 1765, 1,
                this.location.x + Util.nextInt(-30, 30),
                this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
        Service.gI().dropItemMap(this.zone, binhHutNangLuong);

        int[] itemsCap2 = {1150, 1151, 1152, 1153, 1154}; // Cuồng nộ 2, Bổ khí 2, Bổ huyết 2, Giáp Xên 2, Ẩn danh 2
       int soLuongItem = Util.nextInt(2, 6); // Random từ 2 đến 5 item
        for (int i = 0; i < soLuongItem; i++) {
            int randomItemCap2 = itemsCap2[new Random().nextInt(itemsCap2.length)];
            ItemMap itemCap2 = new ItemMap(this.zone, randomItemCap2, 1,
                    this.location.x + Util.nextInt(-50, 50),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, itemCap2);
        }

    }

    @Override
    public synchronized int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Buu né được!");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage / 3);

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

}
