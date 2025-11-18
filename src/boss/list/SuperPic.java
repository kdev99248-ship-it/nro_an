/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boss.list;

/**
 *
 * @author Administrator
 */
import consts.BossID;
import consts.ConstPlayer;
import control.boss.Boss;
import control.boss.BossesData;
import item.Item;
import java.util.ArrayList;
import java.util.List;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.SkillService;
import services.TaskService;
import utils.Util;

public class SuperPic extends Boss{
long st;
    public SuperPic() throws Exception {
        super(BossID.SUPER_PIC, BossesData.SUPER_PIC);
    }
    
    @Override
    public void reward(Player plKill) {
        short[][] ratioItem = new short[][]{{16,20,1},{674,10,1},{457,100,2}};
        for (short[] ratioI : ratioItem) {
            if(Util.isTrue(ratioI[1],100)){
               InventoryService.gI().addItemBag(plKill, ItemService.gI().createNewItem(ratioI[0],ratioI[2]));
            }
        }
        InventoryService.gI().sendItemBags(plKill);
    }
    
    @Override
    public void joinMap() {
        this.name = this.name +" "+ Util.nextInt(1,100);
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
    
    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills
                        .get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                int dis = Util.getDistance(this, pl);
                if (dis > 450) {
                    move(pl.location.x - 24, pl.location.y);
                } else if (dis > 100) {
                    int dir = (this.location.x - pl.location.x < 0 ? 1 : -1);
                    int move = Util.nextInt(50, 100);
                    move(this.location.x + (dir == 1 ? move : -move), pl.location.y);
                } else {
                    if (Util.isTrue(30, 100)) {
                        int move = Util.nextInt(50);
                        move(pl.location.x + (Util.nextInt(0, 1) == 1 ? move : -move), this.location.y);
                    }
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    @Override
    public synchronized int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            damage = 10_000;

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
