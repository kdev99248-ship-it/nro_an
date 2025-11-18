package boss.miniboss;

import consts.BossStatus;
import control.boss.Boss;
import control.boss.BossesData;
import map.ItemMap;
import player.Player;
import services.PlayerService;
import services.Service;
import utils.Util;
import item.Item;

public class Odo extends Boss {

    public Odo() throws Exception {
        super(-Util.nextInt(1000, 1000000), true, true, BossesData.O_DO_NEW);
    }

    private static final String[] textOdo = new String[]{
        "Hôi quá, tránh xa ta ra", "Biến đi", "Trời ơi đồ ở dơ",
        "Thúi quá", "Mùi gì hôi quá"
    };
    private long lastTimeOdo;

    public void subHpWithOdo() {
        try {
            if (this.nPoint != null) {
                if (Util.canDoWithTime(lastTimeOdo, 15000)) {
                    for (int i = this.zone.getNotBosses().size() - 1; i >= 0; i--) {
                        Player pl = this.zone.getNotBosses().get(i);
                        if (pl != null && pl.nPoint != null && !pl.isDie()) {
                            int subHp = (int) ( pl.nPoint.hpMax * 2 / 100);
                            if (subHp >= pl.nPoint.hp) {
                                subHp = pl.nPoint.hp - 1;
                            }
                            Service.gI().chat(pl, textOdo[Util.nextInt(0, textOdo.length - 1)]);
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.gI().Send_Info_NV(pl);
                            pl.injured(null, subHp, true, false);
                        }
                    }
                    this.lastTimeOdo = System.currentTimeMillis();
                     this.changeStatus(BossStatus.CHAT_S);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void chatM() {
        if (this.data[this.currentLevel].getTextM().length == 0) {
            return;
        }
        if (!Util.canDoWithTime(this.lastTimeChatM, this.timeChatM)) {
            return;
        }
        String textChat = this.data[this.currentLevel].getTextM()[Util.nextInt(0, this.data[this.currentLevel].getTextM().length - 1)];
        int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
        textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
        this.chat(prefix, textChat);
        this.lastTimeChatM = System.currentTimeMillis();
        this.timeChatM = Util.nextInt(3000, 20000);
    }

    @Override
    public void active() {
        this.attack();
        subHpWithOdo();
    }
    
   @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.location == null) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20) && Util.getDistance(this, pl) > 50) {
                        if (Util.isTrue(5, 20)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)), pl.location.y);
                        }
                    } else if (Util.getDistance(this, pl) <= 50) {

                    }
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
    public void reward(Player plKill) {
        int[] itemne = {441, 442, 443, 444, 445, 446, 447, 459};
        Service.gI().dropItemMap(this.zone, Util.saoPhaLe(zone, Util.isTrue(95, 100) ? itemne[Util.nextInt(itemne.length - 1)] : itemne[itemne.length - 1], 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y), -1));
        if (Util.isTrue(1, 5)) {
            ItemMap item = new ItemMap(zone, 927, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), -1);
            item.options.add(new Item.ItemOption(87, 0));
        }
        if (Util.isTrue(1, 10)) {
            ItemMap item2 = new ItemMap(zone, 926, 1, this.location.x + Util.nextInt(50), this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), -1);
            item2.options.add(new Item.ItemOption(87, 0));
        }
    }
  @Override
    public synchronized int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            damage = Util.nextInt(300, 500);
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
