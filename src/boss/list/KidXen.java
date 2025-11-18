package boss.list;

/*
 * @Author: NgojcDev
 */
import control.boss.Boss;
import consts.BossID;
import consts.BossStatus;
import control.boss.BossesData;
import map.ItemMap;
import player.Player;
import services.ItemService;
import services.Service;
import utils.Util;

public class KidXen extends Boss {

    private long st;

    public KidXen() throws Exception {
        super(BossID.KID_XEN, true, true, BossesData.KID_XEN);
    }

    @Override
    public void reward(Player plKill) {
        // 15% rơi đồ DTL
        if (Util.isTrue(10, 100)) {
            ItemMap it = ItemService.gI().randDoTL(
                    this.zone,
                    1,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        }

        // 50% rơi 1 món trong {16, 17} (mỗi món 50/50)
        if (Util.isTrue(50, 100)) {
            int itemId = Util.isTrue(50, 100) ? 16 : 17; // chọn 16 hoặc 17
            int x = this.location.x + Util.nextInt(-30, 30);
            int y = this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24);

            Service.gI().dropItemMap(this.zone, new ItemMap(
                    this.zone, itemId, 1,
                    x, y,
                    plKill.id
            ));
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
            this.changeStatus(BossStatus.LEAVE_MAP);
        }

    }
}
