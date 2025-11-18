package boss.event;

/*
 * @Author: NgojcDev
 */

import control.boss.BossesData;
import control.boss.Boss;
import consts.BossID;

import static consts.BossType.HALLOWEEN_EVENT;

import consts.ConstPlayer;
import map.ItemMap;
import player.Player;
import services.EffectSkillService;
import services.InventoryService;
import services.Service;
import services.SkillService;
import utils.SkillUtil;
import utils.Util;

public class Doi extends Boss {

    public Doi() throws Exception {
        super(HALLOWEEN_EVENT, BossID.DOI, true, true, BossesData.DOI);
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

    public void halloween(Player player) {
        if (player.effectSkill != null && !player.effectSkill.isHalloween) {
            EffectSkillService.gI().setIsHalloween(player, 3, 1800000);
        }
    }

    @Override
    public synchronized int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        // check xem phai mac cai trang su kien thi moi danh ra dame
        boolean canDame = InventoryService.gI().canDameBossHalloween(plAtt);
        if (canDame) {
            damage = 100;
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
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
        this.name = "Dơi " + Util.nextInt(10, 100);
        super.joinMap(); // To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

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
