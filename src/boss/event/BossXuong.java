package boss.event;

import consts.BossID;

import consts.BossType;
import consts.ConstPlayer;
import control.boss.Boss;
import control.boss.BossesData;
import map.ItemMap;
import player.Inventory;
import player.Location;
import player.Player;
import services.InventoryService;
import services.Service;
import services.SkillService;
import utils.SkillUtil;
import utils.Util;

public class BossXuong extends Boss {
    long st;

    public BossXuong() throws Exception {
        super(BossID.BOSS_XUONG, BossesData.BOSS_XUONG);
    }

    @Override
    public synchronized int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (InventoryService.gI().canDameBossHalloween(plAtt)) {
            damage = 500;
        } else {
            damage = 0;
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(10, 100)) {
            ItemMap it = new ItemMap(this.zone, 457, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }
        Util.ratioNroBiNgo(plKill);
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, Util.nextInt(500, 1000)) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.nPoint.dame = pl.nPoint.hpMax / Util.nextInt(30, 50);
                this.playerSkill.skillSelect = this.playerSkill.skills
                        .get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                        }
                    }
                    // halloween(pl); // Tắt hiệu ứng Halloween
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void joinMap() {
        this.name = "Boss Xương " + Util.nextInt(10, 1000);
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void active() {
        super.active();
        if (Util.canDoWithTime(st, 15 * 60 * 1000)) {
            leaveMap();
        }
    }
}
