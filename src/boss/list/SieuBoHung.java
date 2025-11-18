package boss.list;

/*
 * @Author: NgojcDev
 */

import consts.ConstPlayer;
import control.boss.Boss;
import consts.BossID;
import consts.BossStatus;
import control.boss.BossesData;
import player.Player;
import services.EffectSkillService;
import services.Service;
import services.TaskService;
import utils.Util;
import services.PlayerService;

public class SieuBoHung extends Boss {

    private long st;
    public boolean callCellCon;
    private long lastTimeHapThu;
    private int timeHapThu;

    private final String text[] = { "Thưa quý vị và các bạn, đây đúng là trận đấu trời long đất lở",
            "Vượt xa mọi dự đoán của chúng tôi", "Eo ơi toàn thân lão Xên bốc cháy kìa",
            "Ngọc Rồng Hip Mãi Đỉnh" };
    private long lastTimeChat;
    private long lastTimeMove;
    private int indexChat = 0;

    public SieuBoHung() throws Exception {
        super(BossID.SIEU_BO_HUNG, BossesData.SIEU_BO_HUNG_1, BossesData.SIEU_BO_HUNG_2);
    }

    @Override
    protected void resetBase() {
        super.resetBase();
        this.callCellCon = false;
    }

    public void callCellCon() {
        new Thread(() -> {
            try {
                this.changeStatus(BossStatus.AFK);
                this.changeToTypeNonPK();
                this.recoverHP();
                this.callCellCon = true;
                this.chat("Hãy đấu với 7 đứa con của ta, chúng đều là siêu cao thủ");
                Thread.sleep(2000);
                this.chat("Cứ chưởng tiếp đi haha");
                Thread.sleep(2000);
                this.chat("Liệu mà giữ mạng đấy");
                Thread.sleep(2000);
                for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
                    switch ((int) boss.id) {
                        case BossID.XEN_CON_1 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_2 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_3 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_4 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_5 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_6 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_7 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                    }
                }
            } catch (Exception e) {
            }
        }).start();
    }

    public void recoverHP() {
        PlayerService.gI().hoiPhuc(this, this.nPoint.hpMax, 0);
    }

    @Override
    public void reward(Player plKill) {
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.attack();
    }

    @Override
    public synchronized int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (prepareBom) {
            return 0;
        }
        if (!this.callCellCon && damage >= this.nPoint.hp) {
            if (Util.isTrue(1, 3)) {
                this.callCellCon();
                return 0;
            } else {
                this.callCellCon = true;
            }
        }
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage / 3);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 4;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                setBom(plAtt);
                return 0;
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
        this.mc();
        if (this.currentLevel > 0) {
            if (this.bossStatus == BossStatus.AFK) {
                this.changeStatus(BossStatus.ACTIVE);
            }
        }
        if (Util.canDoWithTime(st, 900000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    public void mc() {
        Player mc = zone.getNpc();
        if (mc != null) {
            if (Util.canDoWithTime(lastTimeChat, 3000)) {
                String textchat = text[indexChat];
                Service.gI().chat(mc, textchat);
                indexChat++;
                if (indexChat == text.length) {
                    indexChat = 0;
                    lastTimeChat = System.currentTimeMillis() + 7000;
                } else {
                    lastTimeChat = System.currentTimeMillis();
                }
            }

            if (Util.canDoWithTime(lastTimeMove, 15000)) {
                if (Util.isTrue(2, 3)) {
                    int x = this.location.x + Util.nextInt(-100, 100);
                    int y = x > 156 && x < 611 ? 288 : 312;
                    PlayerService.gI().playerMove(mc, x, y);
                }
                lastTimeMove = System.currentTimeMillis();
            }
        }
    }

}
