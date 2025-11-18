/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boss.list;

import consts.BossID;
import consts.ConstPlayer;
import control.boss.Boss;
import control.boss.BossesData;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.SkillService;
import utils.Util;

/**
 *
 * @author Administrator
 */
public class BlackGokuRose extends Boss{
    long st;
    long timeLeaveMap;
    public BlackGokuRose() throws Exception {
        super(BossID.BLACK_GOKU_ROSE, false, true, BossesData.BL_GOKU_ROSE);
    }
     
     @Override
    public void reward(Player plKill) {
        short[][] ratioItem = new short[][]{{1758,50,1},{1759,60,1},{992,40,1}};
        for (short[] ratioI : ratioItem) {
            if(Util.isTrue(ratioI[1],100)){
               InventoryService.gI().addItemBag(plKill, ItemService.gI().createNewItem(ratioI[0],ratioI[2]));
            }
        }
        InventoryService.gI().sendItemBags(plKill);
    }
    
     @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, timeLeaveMap)) {
            if (Util.isTrue(1, 2)) {
                this.leaveMap();
            } else {
                this.leaveMapNew();
            }
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
            timeLeaveMap = Util.nextInt(300000, 900000);
        }
    }

    @Override
    public void joinMap() {
        this.name = this.data[this.currentLevel].getName() + " " + Util.nextInt(1, 100);
        super.joinMap();
        st = System.currentTimeMillis();
        timeLeaveMap = Util.nextInt(600000, 900000);
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
}
