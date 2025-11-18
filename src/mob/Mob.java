package mob;

/*
 * @Author: NgojcDev
 */

import author_ngojc.DragonRun;
import services.InventoryService;
import services.Service;
import services.TaskService;
import services.ItemMapService;
import consts.ConstMap;
import consts.ConstMob;
import consts.ConstTask;
import event.EventManager;
import item.Item;
import map.ItemMap;

import java.util.List;

import map.Zone;
import player.Location;
import player.Pet;
import player.Player;
import network.Message;

import java.io.IOException;

import server.Maintenance;
import server.Manager;
import utils.Rationalities;
import utils.Util;

import java.util.ArrayList;

import services.AchievementService;
import services.TrainingService;
import server.ServerNotify;
import services.ItemService;
import services.MapService;
import skill.Skill;
import utils.TimeUtil;

public class Mob {

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public List<Player> temporaryEnemies = new ArrayList<>();

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int lvMob = 0;
    public int status = 5;
    public int type = 1;

    private long lastTimeAttackPlayer;
    private long timeAttack = 2000;
    public long lastTimePhucHoi = System.currentTimeMillis();
    public long lastTimeSendEffect = System.currentTimeMillis();

    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.sethp(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.type = mob.type;
        this.setTiemNang();
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }

    public void setTiemNang() {
        this.maxTiemNang = (long) this.point.getHpFull() * (long) (this.pTiemNang + Util.nextInt(-2, 2)) / 100L;
    }

    public boolean isDie() {
        return this.point.gethp() <= 0;
    }

    public void setDie() {
        this.lastTimePhucHoi = System.currentTimeMillis();
        this.lastTimeDie = System.currentTimeMillis();
    }

    public void addTemporaryEnemies(Player pl) {
        if (pl != null && !temporaryEnemies.contains(pl)) {
            temporaryEnemies.add(pl);
        }
    }

    public void injured(Player plAtt, long damage, boolean dieWhenHpFull) {
        damage = Math.abs(damage);
        if (!this.isDie()) {
            if (damage >= this.point.hp) {
                damage = this.point.hp;
            }
            if (!dieWhenHpFull) {
                if (this.point.hp == this.point.maxHp && damage >= this.point.hp) {
                    damage = this.point.hp - 1;
                }
                if ((this.tempId == ConstMob.MOC_NHAN || this.tempId == ConstMob.BU_NHIN_MA_QUAI)
                        && damage > this.point.maxHp / 10) {
                    damage = this.point.maxHp / 10;
                }
            }
            if (MapService.gI().isMapKhiGasHuyDiet(this.zone.map.mapId)) {
                boolean mob76Die = true;
                for (Mob mob : this.zone.mobs) {
                    if (!mob.isDie() && mob.tempId == ConstMob.CO_MAY_HUY_DIET) {
                        mob76Die = false;
                        break;
                    }
                }
                if (!mob76Die && plAtt != null && plAtt.playerSkill != null && plAtt.playerSkill.skillSelect != null) {
                    switch (plAtt.playerSkill.skillSelect.template.id) {
                        case Skill.LIEN_HOAN, Skill.ANTOMIC, Skill.MASENKO, Skill.KAMEJOKO -> damage = 1;
                    }
                }
            }
            if (!dieWhenHpFull && !isBigBoss() && !MapService.gI().isMapPhoBan(this.zone.map.mapId) && this.lvMob > 0
                    && plAtt != null && plAtt.charms.tdOaiHung < System.currentTimeMillis()) {
                damage = (int) ((this.point.maxHp <= 20_000_000 ? this.point.maxHp * 1 : 2) * (10.0 / 100));
                this.mobAttackPlayer(plAtt);
            }
            if (plAtt != null && plAtt.isBoss && this.tempId > 0 && Util.isTrue(1, 2)
                    && Util.canDoWithTime(lastTimeAttackPlayer, 2500)) {
                this.mobAttackPlayer(plAtt);
                lastTimeAttackPlayer = System.currentTimeMillis();
            }

            if (damage > 2_000_000_000) {
                damage = 2_000_000_000;
            }
            this.point.hp -= damage;
            addTemporaryEnemies(plAtt);
            if (this.isDie()) {
                this.status = 0;
                this.setDie();
                this.temporaryEnemies.clear();
                if (plAtt != null) {
                    this.sendMobDieAffterAttacked(plAtt, (int) damage);
                    TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                    TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                    TaskService.gI().checkDoneClanTaskKillMob(plAtt, this);
                    AchievementService.gI().checkDoneTaskKillMob(plAtt, this);
                }
                if (this.id == 13) {
                    this.zone.isbulon1Alive = false;
                }
                if (this.id == 14) {
                    this.zone.isbulon2Alive = false;
                }
            } else {
                this.sendMobStillAliveAffterAttacked((int) damage,
                        plAtt != null ? (plAtt.nPoint != null && plAtt.nPoint.isCrit) : false);
            }
            if (plAtt != null) {
                if (plAtt.isPl() && plAtt.satellite != null && plAtt.satellite.isDefend) {
                    plAtt.satellite.isDefend = false;
                }
                long tiemNang = getTiemNangForPlayer(plAtt, damage);

                // +1 tiềm năng sức mạnh mặc định khi đánh quái trong map khí gas
                if (MapService.gI().isMapKhiGasHuyDiet(this.zone.map.mapId)) {
                    tiemNang = 1;
                }

                // +23,500 tiềm năng sức mạnh khi đánh quái trong map Up Porata
                if (MapService.gI().isMapUpPorata(this.zone.map.mapId)) {
                    tiemNang = 23500;
                }

                Service.gI().addSMTN(plAtt, (byte) 2, tiemNang, true);
                TrainingService.gI().tangTnsmLuyenTap(plAtt, tiemNang);
            }
        }
    }

    public long getTiemNangForPlayer(Player pl, long dame) {
        int levelPlayer = Service.gI().getCurrLevel(pl);
        int levelMob = this.level;
        int checkLevel = Math.abs(levelPlayer - this.level);
        long tiemNang = (long) (dame + (point.getHpFull() * 0.0005));
        switch (this.tempId) {
            case 0:
                tiemNang = 1;
                break;
        }
        if (checkLevel > 5 && levelPlayer > levelMob) {
            tiemNang = 1;
        } else {
            if (checkLevel < 0) {
                checkLevel = Math.abs(levelMob - levelPlayer);
            } else {
                tiemNang /= (int) (checkLevel * 0.5) + 1.25;
            }
        }
        if (tiemNang < 1) {
            tiemNang = 1;
        }
        if (pl.nPoint != null) {
            tiemNang = (int) pl.nPoint.calSucManhTiemNang(tiemNang);
        } else {
            return 0;
        }
        if (pl.zone.map.mapId == 122 || pl.zone.map.mapId == 123 || pl.zone.map.mapId == 124) {
            tiemNang *= 2;
        }
        return tiemNang;
    }

    public void update() {
        if (zone.isGoldenFriezaAlive && TimeUtil.is21H()) {
            if (!isDie()) {
                startDie();
                return;
            }
        }
        if (!this.isDie() && this.tempId == ConstMob.CO_MAY_HUY_DIET && Util.canDoWithTime(lastTimeSendEffect, 1000)) {
            sendEffect(55);
            lastTimeSendEffect = System.currentTimeMillis();
        }

        if (this.isDie() && !Maintenance.isRunning && !isBigBoss()) {
            switch (zone.map.type) {
                case ConstMap.MAP_DOANH_TRAI:
                    if (this.tempId == ConstMob.BULON && this.zone.isTUTAlive
                            && Util.canDoWithTime(lastTimeDie, 10000)) {
                        this.hoiSinh();
                        this.hoiSinhMobPhoBan();
                        if (this.id == 13) {
                            this.zone.isbulon1Alive = true;
                        }
                        if (this.id == 14) {
                            this.zone.isbulon2Alive = true;
                        }
                    }
                    break;
                case ConstMap.MAP_BAN_DO_KHO_BAU:
                    break;
                case ConstMap.MAP_CON_DUONG_RAN_DOC:
                    break;
                case ConstMap.MAP_KHI_GAS_HUY_DIET:
                    break;
                case ConstMap.MAP_TAY_KARIN:
                    break;
                default:
                    if (this.zone.isGoldenFriezaAlive && TimeUtil.is21H()) {
                        return;
                    }
                    if (Util.canDoWithTime(lastTimeDie, 5000)) {
                        this.hoiSinh();
                        this.sendMobHoiSinh();
                    }
                    if (Util.canDoWithTime(lastTimePhucHoi, 30000) && !isDie()) {
                        lastTimePhucHoi = System.currentTimeMillis();
                        int hpMax = this.point.maxHp;
                        if (this.point.hp < hpMax) {
                            hoi_hp(hpMax / 10);
                        } else {
                            this.sendMobHoiSinh();
                        }
                    }
            }
        }

        effectSkill.update();
        attack();
    }

    public boolean isBigBoss() {
        return (this.tempId == ConstMob.HIRUDEGARN || this.tempId == ConstMob.VUA_BACH_TUOC
                || this.tempId == ConstMob.ROBOT_BAO_VE || this.tempId == ConstMob.GAU_TUONG_CUOP
                || this.tempId == ConstMob.VOI_CHIN_NGA || this.tempId == ConstMob.GA_CHIN_CUA
                || this.tempId == ConstMob.NGUA_CHIN_LMAO || this.tempId == ConstMob.PIANO);
    }

    public void attack() {
        Player player = getPlayerCanAttack();
        if (!isDie() && !effectSkill.isHaveEffectSkill() && tempId != ConstMob.MOC_NHAN
                && tempId != ConstMob.BU_NHIN_MA_QUAI && tempId != ConstMob.CO_MAY_HUY_DIET && !this.isBigBoss()
                && (this.lvMob < 1 || MapService.gI().isMapPhoBan(this.zone.map.mapId))
                && Util.canDoWithTime(lastTimeAttackPlayer, timeAttack)) {
            if (player != null) {
                this.mobAttackPlayer(player);
            }
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

    public Player getPlayerCanAttack() {
        Player plAttack = getFirstPlayerCanAttack();
        if (plAttack != null) {
            return plAttack;
        }
        int distance = 100;
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.isNewPet && (pl.satellite == null || !pl.satellite.isDefend)
                        && (pl.effectSkin == null || !pl.effectSkin.isVoHinh)
                        && (this.tempId > 18 || (this.tempId > 9 && this.type == 4)) || isBigBoss()) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance || isBigBoss()) {
                        plAttack = pl;
                        distance = dis;
                    }
                }
            }
            this.timeAttack = 2000;
        } catch (Exception e) {

        }
        return plAttack;
    }

    private Player getFirstPlayerCanAttack() {
        Player plAtt = null;
        try {
            List<Player> playersMap = zone.getHumanoids();
            int dis = 300;
            if (playersMap != null) {
                for (Player plAttt : playersMap) {
                    if (plAttt.isDie() || plAttt.isBoss || (plAttt.satellite != null && plAttt.satellite.isDefend)
                            || (plAttt.effectSkin != null && plAttt.effectSkin.isVoHinh)
                            || !this.temporaryEnemies.contains(plAttt)) {
                        continue;
                    }
                    int d = Util.getDistance(plAttt, this);
                    if (d <= dis) {
                        dis = d;
                        plAtt = plAttt;
                    }
                }
            }
            this.timeAttack = 1000;
        } catch (Exception e) {

        }
        return plAtt;
    }

    private void mobAttackPlayer(Player player) {
        int dameMob = this.point.getDameAttack();
        if (player.charms != null && player.charms.tdDaTrau > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        if (player.isPet && ((Pet) player).master.charms != null
                && ((Pet) player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        if (this.lvMob > 0 && !MapService.gI().isMapPhoBan(this.zone.map.mapId)) {
            dameMob = (int) (player.nPoint.hpMax * (10.0 / 100));
        }
        if (player.satellite != null && player.satellite.isDefend) {
            dameMob -= dameMob / 5;
        }
        if (player.itemTime != null && player.itemTime.isUseCMS) {
            dameMob = (int) Math.round(dameMob * 0.1);
        }
        if (this.lvMob > 0 && player.charms.tdOaiHung > System.currentTimeMillis()) {
            dameMob = 0;
        }
        int dame = player.injured(null, dameMob, false, true);

        this.sendMobAttackMe(player, dame);
        this.sendMobAttackPlayer(player);
        this.phanSatThuong(player, dame);
    }

    private void sendMobAttackMe(Player player, int dame) {
        if (!player.isPet && !player.isNewPet && !player.isBot) {
            Message msg;
            try {
                msg = new Message(-11);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(dame); // dame
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    private void sendMobAttackPlayer(Player player) {
        Message msg;
        try {
            msg = new Message(-10);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeInt(player.nPoint.hp);
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hoiSinh() {
        this.status = 5;
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
    }

    public int lvMob() {
        for (Mob mobMap : this.zone.mobs) {
            if (mobMap.lvMob > 0) {
                return 0;
            }
        }
        this.lvMob = this.tempId > 18 && !isBigBoss() ? Util.isTrue(10, 100) ? 1 : 0 : 0;
        this.point.hp = this.lvMob > 0 ? this.point.maxHp <= 20000000 ? this.point.maxHp * 10 : 2000000000
                : this.point.maxHp;
        return this.lvMob;
    }

    public void sendMobHoiSinh() {
        Message msg = null;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(lvMob());
            msg.writer().writeInt(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            this.sendMobMaxHp(this.point.hp);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void hoi_hp(int hp) {
        Message msg = null;
        try {
            this.point.sethp(this.point.gethp() + hp);
            int HP = hp > 0 ? 1 : Math.abs(hp);
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(HP);
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendEffect(int Effect) {
        Message msg = null;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(Effect);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void sendMobDieAffterAttacked(Player plKill, int dameHit) {
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(plKill.nPoint.isCrit); // crit
            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (Exception e) {
        }
    }

    private void hutItem(Player player, List<ItemMap> items) {
        if (!player.isPet && !player.isNewPet) {
            if (player.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(player, item.itemMapId, true);
                }
            }
        } else {
            if (((Pet) player).master.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(((Pet) player).master, item.itemMapId, true);
                }
            }
        }
    }

    private List<ItemMap> mobReward(Player player, ItemMap itemTask, Message msg) {
        List<ItemMap> itemReward = new ArrayList<>();
        try {
            itemReward = this.getItemMobReward(player, this.location.x + Util.nextInt(-10, 10),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y));
            if (itemTask != null) {
                itemReward.add(itemTask);
            }
            msg.writer().writeByte(itemReward.size()); // sl item roi
            for (ItemMap itemMap : itemReward) {
                msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                msg.writer().writeShort(itemMap.x); // xend item
                msg.writer().writeShort(itemMap.y); // yend item
                msg.writer().writeInt((int) itemMap.playerId); // id nhan vat
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemReward;
    }

    public List<ItemMap> getItemMobReward(Player player, int x, int yEnd) {
        List<ItemMap> list = new ArrayList<>();
        if (player.isBoss || player.isBot) {
            return list;
        }
        int mapid = player.zone.map.mapId;
//        if (player.isPl() && Util.isTrue(1, 10000) && this.tempId == 0) {
//            short itTemp = (short) ItemService.gI().randTempItemKichHoat(player.gender);
//            ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
//            List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
//            if (!ops.isEmpty()) {
//                it.options = ops;
//            }
//            it.options.add(new Item.ItemOption(210, 0));
//            it.options.add(new Item.ItemOption(216, 0));
//            it.options.add(new Item.ItemOption(30, 0));
//            list.add(it);
//        }
        if ((MapService.gI().isMapTuongLai(mapid) || MapService.gI().isMapCold(mapid)) && player.itemTime.isUseCoBonLa) {
            if (Util.isTrue(Rationalities.RATIO_DROP_GOLD_KEY[0], Rationalities.RATIO_DROP_GOLD_KEY[1])) {
                list.add(new ItemMap(zone, 1561, 1, x, yEnd, player.id));
            }
        }

        if (this.tempId == 0) {
            return list;
        }
        // =============================== EVENT NOEL
        if (DragonRun.CHRISTMAS) {
            Player pl = player;
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (Util.isTrue(1, 50)) {
                if (pl.itemEvent != null && pl.itemEvent.canDropTatVoGiangSinh(100)) {
                    list.add(new ItemMap(zone, 649, 1, x, yEnd, player.id));
                }
            }
        }
        if (mapid == 5 || mapid == 13) {
            Player pl = player;
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (Util.isTrue(1, 500)) {
                if (pl.itemEvent != null && pl.itemEvent.canDropBinhNuoc(100)) {
                    list.add(new ItemMap(zone, 456, 1, x, yEnd, pl.id));
                }
            }
        }
        // CLOSE

        // =============================== EVENT 20/10
        if (DragonRun.INTERNATIONAL_WOMANS_DAY) {
            Player pl = player;
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (Util.isTrue(1, 50)) {
                if (pl.itemEvent != null && pl.itemEvent.canDropHoaHong(100)) {
                    list.add(new ItemMap(zone, 610, 1, x, yEnd, player.id));
                }
            }
        }
        // CLOSE

        // =============================== EVENT HALLOWEN
        if (DragonRun.HALLOWEEN) {
            if (InventoryService.gI().canDameBossHalloween(player)) {
                // roi bi ngo
                if (Util.isTrue(20, 100)) {
                    list.add(new ItemMap(zone, 585, Util.nextInt(1, 2), this.location.x, yEnd, player.id));
                }
            }
        }
        // CLOSE

        // =============================== MOB REWARD
        // ========================Capsul Kì Bí========================
        if (player.itemTime.isUseMayDo
                && (Util.isTrue(10, 100)
                || (player.isActive() && Util.isTrue(10, 100)))
                && this.tempId > 57 && this.tempId < 66) {
            list.add(new ItemMap(zone, 380, 1, x, yEnd, player.id));
        }

        // ========================TASK========================
        if (player.isPl() && TaskService.gI().getIdTask(player) == ConstTask.TASK_8_1) {
            if (player.gender == 0 && this.tempId == 11 || player.gender == 1 && this.tempId == 12
                    || player.gender == 2 && this.tempId == 10) {
                list.add(new ItemMap(zone, 20, 1, x, yEnd, player.id));
                TaskService.gI().checkDoneTaskFind7Stars(player);
            }
        }

        if (player.isPl() && TaskService.gI().getIdTask(player) == ConstTask.TASK_14_1) {
            if (player.gender == 0 || player.gender == 1 || player.gender == 2) {
                list.add(new ItemMap(zone, 85, 1, x, yEnd, player.id));
                TaskService.gI().checkDoneTaskFindDoremon(player);
            }
        }

        // ========================Map Bang Hội========================
        if (MapService.gI().isMapUpPorata(mapid)) {
            if (Util.isTrue(0, 100)) {
                ItemMap it = new ItemMap(zone, 934, Util.nextInt(1, 10), x, yEnd, player.id);
                it.options.add(new Item.ItemOption(30, 0));
                list.add(it);
            } else if (Util.isTrue(10, 100)) {
                ItemMap it = new ItemMap(zone, 933, 1, x, yEnd, player.id);
                it.options.add(new Item.ItemOption(30, 0));
                list.add(it);
            } else if (Util.isTrue(0, 50)) {
                ItemMap it = new ItemMap(zone, 935, 1, x, yEnd, player.id);
                it.options.add(new Item.ItemOption(31, Util.nextInt(1, 100)));
                it.options.add(new Item.ItemOption(30, 0));
                list.add(it);
            }
        }

        // ======================== Vàng Ngọc ========================
        // Kiểm tra xem bản đồ có phải là bản đồ 3 hành tinh hay không
        if (MapService.gI().isMap3Planets(mapid)) {

            // Phân chia tỷ lệ rơi vàng dựa trên các điều kiện
            if (Util.isTrue(1, 20) // Xác suất chung là 1/100
                    || (Manager.TEST && Util.isTrue(1, 5))
                    || (player.isActive() && Util.isTrue(1, 20))
                    || (player.isAdmin() && Util.isTrue(1, 20))) {

                // Tính số lượng vàng rơi ra ngẫu nhiên trong khoảng từ 500 đến 3000
                int vang = Util.nextInt(500, 3000);

                // Phân chia các mức vàng rơi dựa trên giá trị vàng đã tính
                if (vang < 1000) {
                    // Nếu vàng dưới 1000, rơi item vàng cấp 76 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 76, vang, x, yEnd, player.id));
                } else if (vang < 2000) {
                    // Nếu vàng từ 1000 đến 2000, rơi item vàng cấp 188 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 188, vang, x, yEnd, player.id));
                } else {
                    // Nếu vàng trên 2000, rơi item vàng cấp 189 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 189, vang, x, yEnd, player.id));
                }
            }
        }

        // Kiểm tra nếu bản đồ hiện tại là bản đồ Nappa
        if (MapService.gI().isMapNappa(mapid)) {

            // Kiểm tra điều kiện rơi vàng (bỏ qua tỷ lệ rơi vàng, chỉ xét điều kiện khác)
            if (Util.isTrue(1, 100) // Xác suất chung là 1/100
                    || (Manager.TEST && Util.isTrue(1, 5)) // Trong chế độ test, tỷ lệ rơi vàng là 1/5
                    || (player.isActive() && Util.isTrue(1, 20)) // Nếu người chơi đang hoạt động, tỷ lệ rơi vàng là
                    // 1/20
                    || (player.isAdmin() && Util.isTrue(1, 10)) // Nếu người chơi là admin, tỷ lệ rơi vàng là 10%
            ) {

                // Tính số lượng vàng rơi ra ngẫu nhiên trong khoảng từ 2000 đến 6000
                int vang = Util.nextInt(2000, 6000);

                // Phân chia các mức vàng rơi dựa trên giá trị vàng đã tính
                if (vang < 3000) {
                    // Nếu vàng dưới 3000, rơi item vàng cấp 188 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 188, vang, x, yEnd, player.id));
                } else if (vang < 5000) {
                    // Nếu vàng từ 3000 đến 5000, rơi item vàng cấp 189 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 189, vang, x, yEnd, player.id));
                } else {
                    // Nếu vàng trên 5000, rơi item vàng cấp 190 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 190, vang, x, yEnd, player.id));
                }
            }
        }

        // Vàng cold
        if (MapService.gI().isMapCold(mapid)) {

            // Kiểm tra điều kiện rơi vàng với 4 trường hợp tài khoản khác nhau
            if (Util.isTrue(1, 100) // Xác suất chung là 1/100
                    || (Manager.TEST && Util.isTrue(1, 5)) // Trong chế độ test, tỷ lệ rơi vàng là 1/5
                    || (player.isActive() && Util.isTrue(1, 20)) // Nếu người chơi đang hoạt động, tỷ lệ rơi vàng là
                    // 1/20
                    || (player.isAdmin() && Util.isTrue(1, 10)) // Nếu người chơi là admin, tỷ lệ rơi vàng là 10%
            ) {

                // Tính số lượng vàng rơi ra ngẫu nhiên trong khoảng từ 7000 đến 15000
                int vang = Util.nextInt(8000, 18000);

                // Phân chia các mức vàng rơi dựa trên giá trị vàng đã tính
                if (vang < 10000) {
                    list.add(new ItemMap(zone, 189, vang, x, yEnd, player.id)); // Rơi vàng cấp 189
                } else if (vang < 14000) {
                    list.add(new ItemMap(zone, 190, vang, x, yEnd, player.id)); // Rơi vàng cấp 190
                } else {
                    list.add(new ItemMap(zone, 190, vang, x, yEnd, player.id)); // Rơi vàng cấp 190
                }
            }
        }

        // Vàng tương lai
        if (MapService.gI().isMapTuongLai(mapid)) {

            // Kiểm tra điều kiện rơi vàng với 4 trường hợp tài khoản khác nhau
            if (Util.isTrue(1, 100) // Xác suất chung là 1/100
                    || (Manager.TEST && Util.isTrue(1, 5)) // Trong chế độ test, tỷ lệ rơi vàng là 1/5
                    || (player.isActive() && Util.isTrue(1, 20)) // Nếu người chơi đang hoạt động, tỷ lệ rơi vàng là
                    // 1/20
                    || (player.isAdmin() && Util.isTrue(1, 10)) // Nếu người chơi là admin, tỷ lệ rơi vàng là 10%
            ) {

                // Tính số lượng vàng rơi ra ngẫu nhiên trong khoảng từ 5000 đến 10000
                int vang = Util.nextInt(5000, 12000);

                // Phân chia các mức vàng rơi dựa trên giá trị vàng đã tính
                if (vang < 6000) {
                    list.add(new ItemMap(zone, 188, vang, x, yEnd, player.id)); // Rơi vàng cấp 188
                } else if (vang < 10000) {
                    list.add(new ItemMap(zone, 189, vang, x, yEnd, player.id)); // Rơi vàng cấp 189
                } else {
                    list.add(new ItemMap(zone, 190, vang, x, yEnd, player.id)); // Rơi vàng cấp 190
                }
            }
        }

        // Vàng phó bản
        if (MapService.gI().isMapPhoBan(mapid)) {

            // Kiểm tra điều kiện rơi vàng với 4 trường hợp tài khoản khác nhau
            if (Util.isTrue(1, 100) // Xác suất chung là 1/100
                    || (Manager.TEST && Util.isTrue(1, 5)) // Trong chế độ test, tỷ lệ rơi vàng là 1/5
                    || (player.isActive() && Util.isTrue(1, 10)) // Nếu người chơi đang hoạt động, tỷ lệ rơi vàng là
                    // 1/10
                    || (player.isAdmin() && Util.isTrue(1, 10)) // Nếu người chơi là admin, tỷ lệ rơi vàng là 10%
            ) {

                // Tính số lượng vàng rơi ra ngẫu nhiên trong khoảng từ 8000 đến 20000
                int vang = Util.nextInt(8000, 20000);

                // Phân chia các mức vàng rơi dựa trên giá trị vàng đã tính
                if (vang < 6000) {
                    list.add(new ItemMap(zone, 188, vang, x, yEnd, player.id)); // Rơi vàng cấp 188
                } else if (vang < 10000) {
                    list.add(new ItemMap(zone, 189, vang, x, yEnd, player.id)); // Rơi vàng cấp 189
                } else {
                    list.add(new ItemMap(zone, 190, vang, x, yEnd, player.id)); // Rơi vàng cấp 190
                }
            }
        }

        // Ngọc
        if (Util.isTrue(1, 1000000) // Xác suất chung là 1/100000
                || (Manager.TEST && Util.isTrue(1, 10)) // Trong chế độ test, tỷ lệ rơi ngọc là 1/10
                || (player.isActive() && Util.isTrue(5, 10000)) // Nếu người chơi đang hoạt động, tỷ lệ rơi ngọc là
                // 1/200
                || (player.isAdmin() && Util.isTrue(1, 10)) // Nếu người chơi là admin, tỷ lệ rơi ngọc là 10%
        ) {
            // Thay đổi ngọc muốn rơi ở đây
            int ngoc = Util.nextInt(1, 1);
            list.add(new ItemMap(zone, 77, ngoc, x, yEnd, player.id)); // Thêm ngọc vào danh sách item
        }

        // ========================SKH========================
        // Set kich hoat
//        if (((player.isActive() && Util.isTrue(1, 1)) // neu mtv thi 1/50k%
//                || (player.isNewMember && Util.isTrue(1, 1))) // neu la newbie ma chua mtv thì 1/100k
//                && MapService.gI().isMapUpSKH(mapid) // check map up
//        ) {
//            short itTemp = (short) ItemService.gI().randTempItemKichHoat(player.gender);
//            ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
//            List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
//            if (!ops.isEmpty()) {
//                it.options = ops;
//            }
//
//            int[] opsrand = ItemService.gI().randOptionItemKichHoat(player.gender);
//            it.options.add(new Item.ItemOption(opsrand[0], 0));
//            it.options.add(new Item.ItemOption(opsrand[1], 0));
//            it.options.add(new Item.ItemOption(30, 0));
//            list.add(it);
//        }
//        if (((player.isActive() && Util.isTrue(1, 50000)) // neu mtv thi 1/50k%
//                || (player.isNewMember && Util.isTrue(1, 100000))) // neu la newbie ma chua mtv thì 1/100k
//                && MapService.gI().isMapUpSKH(mapid) // check map up
//        ) {
//            short itTemp = (short) ItemService.gI().randTempItemKichHoat(player.gender);
//            ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
//            List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
//            if (!ops.isEmpty()) {
//                it.options = ops;
//            }
//
//            int[] opsrand = ItemService.gI().randOptionItemKichHoatNew(player.gender);
//            it.options.add(new Item.ItemOption(opsrand[0], 0));
//            it.options.add(new Item.ItemOption(opsrand[1], 0));
//            it.options.add(new Item.ItemOption(opsrand[2], 0));
//            it.options.add(new Item.ItemOption(opsrand[3], 0)); // SKH mới thêm 4 option
//            it.options.add(new Item.ItemOption(30, 0));
//            list.add(it);
//        }
        // ========================Đồ Sao Khác Vải Thô========================
        if (((player.isActive() && Util.isTrue(0, 5000)) // Nếu người chơi đang hoạt động và có xác suất 1/10000
                || (Manager.TEST && Util.isTrue(1, 1000))) // Nếu trong chế độ TEST và có xác suất 1/10000
                && MapService.gI().isMapNappa(mapid) // Kiểm tra nếu là bản đồ Nappa
        ) {
            short itTemp = (short) ItemService.gI().randTempItemDoSao(player.gender); // Lấy item sao ngẫu nhiên cho
            // người chơi
            ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id); // Tạo đối tượng item
            List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp); // Lấy danh sách option cho item

            if (!ops.isEmpty()) {
                it.options = ops; // Nếu có options thì gán vào item
            }

            // Thêm option dựa trên xác suất
            int randOption = Util.nextInt(100); // Lấy số ngẫu nhiên từ 0 đến 99
            boolean hasOption = false; // Biến kiểm tra có thêm option hay không

            // 50% xác suất thêm option
            if (randOption < 50) {
                int randAddOption = Util.nextInt(100); // Lấy số ngẫu nhiên để quyết định sao nào
                if (randAddOption < 50) { // 45% cho option 1 (sao 1)
                    it.options.add(new Item.ItemOption(107, 1)); // Thêm option sao 1
                    hasOption = true;
                } else if (randAddOption < 90) { // 30% cho option 2 (sao 2)
                    it.options.add(new Item.ItemOption(107, 2)); // Thêm option sao 2
                    hasOption = true;
                } else { // 25% cho option 3 (sao 3)
                    it.options.add(new Item.ItemOption(107, 3)); // Thêm option sao 3
                    hasOption = true;
                }
            }

            // Nếu có option (sao) thì mới thêm vào list
            if (hasOption) {
                list.add(it); // Thêm item vào danh sách item rơi
            }
        }

        // END
        // ========================Đồ Sao 3 Map Đầu========================
        if (((Util.isTrue(1, 5000))
                || (Manager.TEST && Util.isTrue(1, 300))
                || (player.isAdmin() && Util.isTrue(15, 150)))
                && MapService.gI().isMapUpSKH(mapid)) {

            // Tính toán tỷ lệ dựa trên sức mạnh
            int baseRate = 50; // Tỷ lệ cơ bản 50%
            int powerReduction = (int) Math.min(player.nPoint.power / 100000, 5) * 20; // Giảm 20% mỗi 100k sức mạnh,
            // tối đa 5 lần
            int finalRate = Math.max(baseRate - powerReduction, 0); // Tỷ lệ không âm

            // Nếu tỷ lệ > 0, kiểm tra xác suất
            if (finalRate > 0 && Util.nextInt(100) < finalRate) {
                short itTemp = (short) ItemService.gI().randDoSao(player.gender);
                ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
                List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);

                if (!ops.isEmpty()) {
                    it.options = ops;
                }

                // Thêm option dựa trên xác suất
                int randOption = Util.nextInt(100); // Lấy số ngẫu nhiên từ 0 đến 99
                boolean hasOption = false; // Biến để kiểm tra có thêm option hay không

                // 50% xác suất thêm option
                if (randOption < 50) {
                    int randAddOption = Util.nextInt(100); // Lấy số ngẫu nhiên để quyết định thêm sao nào
                    if (randAddOption < 60) { // 60% cho option 1 (sao 1)
                        it.options.add(new Item.ItemOption(107, 1)); // Option 1 (sao 1)
                        hasOption = true;
                    } else if (randAddOption < 90) { // 30% cho option 2 (sao 2)
                        it.options.add(new Item.ItemOption(107, 2)); // Option 2 (sao 2)
                        hasOption = true;
                    } else { // 10% cho option 3 (sao 3)
                        it.options.add(new Item.ItemOption(107, 3)); // Option 3 (sao 3)
                        hasOption = true;
                    }
                }

                // Nếu có option (sao) thì mới thêm vào list
                if (hasOption) {
                    list.add(it);
                }
            }
        }

        // ========================Đồ Thần + Thức Ăn========================
        if (MapService.gI().isMapCold(mapid)) {
            if (player.isPet) {
                player = ((Pet) player).master;
            }
            if (player.isPet) {
                player = ((Pet) player).master;
            }
//            if (Util.percent(0.0001)) {
//                ItemMap it = ItemService.gI().randDoTL(this.zone, 1, x, yEnd, player.id);
//                list.add(it);
//                ServerNotify.gI().notify(player.name + " vừa nhặt được " + it.itemTemplate.name + " tại "
//                        + this.zone.map.mapName + " khu " + this.zone.zoneId);
//            }
            if (Util.isTrue(1, 20000) // 1/20000 xác suất
                    || (player.isActive() && Util.isTrue(1, 700)) // 1/700 xác suất khi người chơi hoạt động
                    || (Manager.TEST && Util.isTrue(1, 100)) // 1/500 xác suất khi là test
                    || (player.isAdmin() && Util.isTrue(10, 100))) { // 10/100 xác suất khi là admin
                if (InventoryService.gI().fullSetThan(player)) {
                    // Tạo item thức ăn
                    ItemMap it = new ItemMap(zone, Util.nextInt(663, 667), 1, x, yEnd, player.id);
                    it.options.add(new Item.ItemOption(30, 0)); // Option thức ăn
                    list.add(it);
                }
            }
        }

        // Thức ăn tương lai
        if (MapService.gI().isMapTuongLai(mapid)
                && ((Util.isTrue(1, 20000) // 1/20000 xác suất
                || (player.isActive() && Util.isTrue(1, 1000)) // 1/1000 xác suất khi người chơi kích hoạt
                || (Manager.TEST && Util.isTrue(1, 200)) // 1/500 xác suất khi là test
                || (player.isAdmin() && Util.isTrue(10, 100)))) // 10/100 xác suất khi là admin
                && InventoryService.gI().fullSetThan(player)) {

            ItemMap it = new ItemMap(zone, Util.nextInt(663, 667), 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(30, 0)); // Option thức ăn
            list.add(it);
        }

        // ========================Sao Pha Lê Mảnh đá Vụn Đá Nâng
        // Cấp========================
        if (player.nPoint.isDoSPL
                && (Util.isTrue(1, 100) // 1/20000 xác suất
                || (player.isActive() && Util.isTrue(1, 100)) // 1/1000 xác suất khi người chơi hoạt động
                || (Manager.TEST && Util.isTrue(1, 50)) // 1/500 xác suất khi là test
                || (player.isAdmin() && Util.isTrue(10, 100)))) { // 10/100 xác suất khi là admin

            int rand = Util.nextInt(0, 6); // Lấy giá trị ngẫu nhiên từ 0 đến 5
            ItemMap it = new ItemMap(zone, 441 + rand, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));
            list.add(it);
        }

        // Đá nâng cấp
        if (MapService.gI().isMapCold(mapid)) {
            if (Util.isTrue(1, 10000)
                    || (Manager.TEST && Util.isTrue(1, 5))
                    || (player.isActive() && Util.isTrue(1, 500))) {
                int rand = Util.nextInt(0, 4);
                ItemMap it = new ItemMap(zone, 220 + rand, 1, x, yEnd, player.id);
                it.options.add(new Item.ItemOption(71 - rand, 0));
                list.add(it);
            }
        }

        // Mảnh đá vụn cho bản đồ Doanh Trại
        if (MapService.gI().isMapDoanhTrai(mapid)
                && (Util.isTrue(1, 10000)
                || (Manager.TEST && Util.isTrue(1, 5))
                || (player.isActive() && Util.isTrue(1, 10)))) {
            ItemMap it = new ItemMap(zone, 225, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(74, 0));
            list.add(it);
        }

        // Mảnh đá vụn cho bản đồ 3 Planets (tỷ lệ khác)
        if (MapService.gI().isMap3Planets(mapid)
                && (Util.isTrue(1, 500)
                || (Manager.TEST && Util.isTrue(1, 10))
                || (player.isActive() && Util.isTrue(1, 250)))) {
            ItemMap it = new ItemMap(zone, 225, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(74, 0));
            list.add(it);
        }

        // Kiểm tra nếu map nằm trong danh sách các map cần áp dụng xác suất
        if (MapService.gI().isMap3Planets(mapid)
                || MapService.gI().isMapNappa(mapid)
                || MapService.gI().isMapTuongLai(mapid)
                || MapService.gI().isMapCold(mapid)) {
            if (Util.isTrue(1, 200)
                    || (player.isActive() && Util.isTrue(1, 100))) {
                int rand = Util.nextInt(0, 1);
                ItemMap it = new ItemMap(zone, 19 + rand, 1, x, yEnd, player.id);
                list.add(it);
            }
        }
        // ========================Mảnh Thiên Sứ========================
        if ((Util.isTrue(1, 10000) || (player.isActive() && Util.isTrue(20, 10000)))
                && MapService.gI().isMapHanhTinhThucVat(mapid) && InventoryService.gI().findItemNTK(player)) {
            list.add(new ItemMap(zone, Util.nextInt(1066, 1070), 1, x, yEnd, player.id));
        }
        // if (Util.isTrue(1, 5000) || (player.isActive() && Util.isTrue(1, 1000))) {
        // list.add(new ItemMap(zone, 77, 1, x, yEnd, player.id));
        // }

        // ========================Ma Quái Drop cho Map 184 và 185========================
        if (MapService.gI().isMapMaQuai(mapid)) {
            // Tỷ lệ rơi 30% (30/100)
            if (Util.percent(10)) {
                list.add(new ItemMap(zone, 1788, 1, x, yEnd, player.id));
            }
        }

        return list;
    }

    private ItemMap dropItemTask(Player player) {
        ItemMap itemMap = null;
        switch (this.tempId) {
            case ConstMob.KHUNG_LONG:
            case ConstMob.LON_LOI:
            case ConstMob.QUY_DAT:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_2_0) {
                    itemMap = new ItemMap(this.zone, 73, 1, this.location.x, this.location.y, player.id);
                }
                break;
        }
        if (itemMap != null) {
            return itemMap;
        }
        return null;
    }

    private void sendMobStillAliveAffterAttacked(int dameHit, boolean crit) {
        Message msg;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(crit); // chí mạng
            msg.writer().writeInt(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hoiSinhMobPhoBan() {
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(this.lvMob); // level mob
            msg.writer().writeInt(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hoiSinhMobTayKarin() {
        this.point.hp = this.point.maxHp;
        this.maxTiemNang = 1;
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(this.lvMob); // level mob
            msg.writer().writeInt(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendSieuQuai(int type) {
        Message msg;
        try {
            msg = new Message(-75);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(type);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendDisable(boolean bool) {
        Message msg;
        try {
            msg = new Message(81);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendDoneMove(boolean bool) {
        Message msg;
        try {
            msg = new Message(82);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendFire(boolean bool) {
        Message msg;
        try {
            msg = new Message(85);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendIce(boolean bool) {
        Message msg;
        try {
            msg = new Message(86);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendWind(boolean bool) {
        Message msg;
        try {
            msg = new Message(87);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendMobMaxHp(int maxHp) {
        Message msg;
        try {
            msg = new Message(87);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(maxHp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    private void phanSatThuong(Player plTarget, long dame) {
        if (plTarget.nPoint == null) {
            return;
        }
        int percentPST = plTarget.nPoint.tlPST;
        if (percentPST != 0) {
            int damePST = (int) (long) (dame * percentPST / 100L);
            Message msg;
            try {
                msg = new Message(-9);
                msg.writer().writeByte(this.id);
                if (damePST >= this.point.hp) {
                    damePST = this.point.hp - 1;
                }
                int hpMob = this.point.hp;
                injured(null, damePST, true);
                damePST = hpMob - this.point.hp;
                msg.writer().writeInt(this.point.hp);
                msg.writer().writeInt(damePST);
                msg.writer().writeBoolean(false);
                msg.writer().writeByte(36);
                Service.gI().sendMessAllPlayerInMap(this.zone, msg);
                msg.cleanup();
            } catch (IOException e) {
            }
        }
    }

    public void startDie() {
        Message msg;
        try {
            setDie();
            this.point.hp = -1;
            this.status = 0;
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }
}
