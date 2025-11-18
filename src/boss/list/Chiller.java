/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boss.list;

import control.boss.Boss;
import consts.BossID;
import consts.ConstPlayer;
import control.boss.BossesData;
import item.Item;
import java.util.Random;
import map.ItemMap;
import player.Player;
import server.Manager;
import services.InventoryService;
import services.ItemService;
import services.Service;
import services.SkillService;
import services.TaskService;
import utils.Util;
import services.UseItem;
/**
 *
 * @author Administrator
 */
public class Chiller extends Boss{
        public long st;
      public Chiller() throws Exception {
        super(BossID.CHILLER_1, BossesData.CHILLER_1,BossesData.CHILLER_2);
        st = System.currentTimeMillis();
    }
    @Override
    public void reward(Player plKill) {
        boolean hasRatioItem = Util.isTrue(20,100);
        short[][] idItemHanhTinh = {
                    {555, 556, 562, 563, 561},
                    {559, 560, 566, 567, 561},
                    {557, 558, 564, 565, 561}
            };
        if(hasRatioItem){
            short randomId =(short) Util.nextInt(idItemHanhTinh.length);
            Item itemThanLinh = UseItem.gI().taoItemThanLinh(idItemHanhTinh[randomId][(short)Util.nextInt(4)]);
            InventoryService.gI().addItemBag(plKill, itemThanLinh);
            InventoryService.gI().sendItemBags(plKill);
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
