package control.boss;

/*
 * @Author: NgojcDev
 */

import boss.event.*;
import consts.BossID;
import boss.list.Rambo;
import boss.list.MapDauDinh;
import boss.list.Kuku;
import boss.list.Android19;
import boss.list.Pic;
import boss.list.Android14;
import boss.list.Poc;
import boss.list.Android13;
import boss.list.KingKong;
import boss.list.DrKore;
import boss.list.Android15;
import boss.list.DeathBeam1;
import boss.list.DeathBeam2;
import boss.list.DeathBeam3;
import boss.list.DeathBeam4;
import boss.list.DeathBeam5;
import boss.list.GoldenFrieza;
import boss.list.Cooler;
import boss.list.SieuBoHung;
import boss.list.XenBoHung;
import boss.list.Broly;
import boss.list.TaoPaiPai;
import boss.list.Fide;
import boss.list.Mabu;
import boss.list.BuiBui;
import boss.list.BuiBui2;
import boss.list.Cadic;
import boss.list.Drabura;
import boss.list.Drabura2;
import boss.list.Drabura3;
import boss.list.Goku;
import boss.list.Yacon;
import boss.list.Mabu2H;
import boss.list.SuperBu;

import boss.list.BIDO;
import boss.list.BOJACK;
import boss.list.BUJIN;
import boss.list.KOGU;
import boss.list.SUPER_BOJACK;
import boss.list.ZANGYA;
import boss.list.CHIENBINH0;
import boss.list.CHIENBINH1;
import boss.list.CHIENBINH2;
import boss.list.CHIENBINH3;
import boss.list.CHIENBINH4;
import boss.list.CHIENBINH5;
import boss.list.DOITRUONG5;
import boss.list.TANBINH0;
import boss.list.TANBINH1;
import boss.list.TANBINH2;
import boss.list.TANBINH3;
import boss.list.TANBINH4;
import boss.list.TANBINH5;
import boss.list.TAPSU0;
import boss.list.TAPSU1;
import boss.list.TAPSU2;
import boss.list.TAPSU3;
import boss.list.TAPSU4;
import boss.list.XENCON1;
import boss.list.XENCON2;
import boss.list.XENCON3;
import boss.list.XENCON4;
import boss.list.XENCON5;
import boss.list.XENCON6;
import boss.list.XENCON7;
import boss.miniboss.Xinbato;
import boss.miniboss.SoiHecQuyn;
import boss.list.Mai;
import boss.list.Pilap;
import boss.list.Su;
import boss.list.Cumber;
import boss.miniboss.AnTrom;
import boss.miniboss.Odo;
import boss.list.KidBuu;
import boss.list.BlackGoku;
import boss.list.BlackGokuRose;
import boss.list.Chiller;
import boss.list.KidXen;
import boss.list.SO1;
import boss.list.SO2;
import boss.list.SO3;
import boss.list.SO4;
import boss.list.SuperPic;
import boss.list.TDT;
import player.Player;
import network.Message;
import services.MapService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import map.Zone;
import server.Maintenance;
import utils.Functions;
import utils.Logger;
import utils.Util;

import javax.sound.midi.Soundbank;

public class BossManager implements Runnable {

    private static BossManager instance;
    public static byte ratioReward = 10;

    private static final Set<Integer> HIDDEN_SHOW_BOSS = Set.of(
            BossID.XINBATO_1,
            BossID.SOI_HEC_QUYN_1,
            BossID.AN_TROM,
            BossID.MAP_DAU_DINH,
            BossID.RAMBO,
            BossID.KUKU
    );

    private boolean isHiddenInShow(Boss b) {
        if (b == null) {
            return true; // null => ẩn khỏi UI
        }
        try {
            // Ẩn theo ID
            if (HIDDEN_SHOW_BOSS.contains(b.id)) {
                return true;
            }
            // Có thể bổ sung điều kiện tùy map/status ở đây nếu muốn
            return false;
        } catch (Throwable t) {
            return true; // có lỗi thì cứ ẩn để an toàn UI
        }
    }

    public static BossManager gI() {
        if (instance == null) {
            instance = new BossManager();
        }
        return instance;
    }

    public BossManager() {
        this.bosses = new ArrayList<>();
    }

    protected final List<Boss> bosses;

    public void addBoss(Boss boss) {
        this.bosses.add(boss);
    }

    public List<Boss> getBosses() {
        return this.bosses;
    }

    public void removeBoss(Boss boss) {
        this.bosses.remove(boss);
    }

    public void loadBoss() {
        this.createBoss(BossID.AN_TROM, 3);
        this.createBoss(BossID.TIEU_DOI_TRUONG);
        this.createBoss(BossID.SO_4);
        this.createBoss(BossID.SO_3);
        this.createBoss(BossID.SO_2);
        this.createBoss(BossID.SO_1);
        this.createBoss(BossID.TIEU_DOI_TRUONG_NM);
        this.createBoss(BossID.BOJACK);
        this.createBoss(BossID.SUPER_BOJACK);
        this.createBoss(BossID.KING_KONG);
        this.createBoss(BossID.XEN_BO_HUNG);
        this.createBoss(BossID.SIEU_BO_HUNG);
        this.createBoss(BossID.KUKU, 5);
        this.createBoss(BossID.MAP_DAU_DINH, 5);
        this.createBoss(BossID.RAMBO, 5);
        this.createBoss(BossID.FIDE);
        this.createBoss(BossID.ANDROID_14);
        this.createBoss(BossID.DR_KORE);
        this.createBoss(BossID.COOLER);
        this.createBoss(BossID.BLACK_GOKU, 3);
        this.createBoss(BossID.GOLDEN_FRIEZA, 5);
        this.createBoss(BossID.SOI_HEC_QUYN_1, 10);
        this.createBoss(BossID.SU, 1);
        this.createBoss(BossID.MAI, 1);
        this.createBoss(BossID.CUMBER, 1);
        this.createBoss(BossID.FILAP, 1);
        this.createBoss(BossID.XINBATO_1, 10);
        this.createBoss(BossID.KID_BUU, 1);
        this.createBoss(BossID.KID_XEN, 1);
        this.createBoss(BossID.BOSS_XUONG, 5);
        this.createBoss(BossID.CHILLER_1,1);
        this.createBoss(BossID.SUPER_PIC,2);
        this.createBoss(BossID.BLACK_GOKU_ROSE,2);
    }

    public void createBoss(int bossID, int total) {
        for (int i = 0; i < total; i++) {
            createBoss(bossID);
        }
    }

    public Boss createBoss(int bossID) {
        try {
            Boss boss = switch (bossID) {
                case BossID.BOSS_XUONG -> new BossXuong();
                case BossID.TAP_SU_0 -> new TAPSU0();
                case BossID.TAP_SU_1 -> new TAPSU1();
                case BossID.TAP_SU_2 -> new TAPSU2();
                case BossID.TAP_SU_3 -> new TAPSU3();
                case BossID.TAP_SU_4 -> new TAPSU4();
                case BossID.TAN_BINH_5 -> new TANBINH5();
                case BossID.TAN_BINH_0 -> new TANBINH0();
                case BossID.TAN_BINH_1 -> new TANBINH1();
                case BossID.TAN_BINH_2 -> new TANBINH2();
                case BossID.TAN_BINH_3 -> new TANBINH3();
                case BossID.TAN_BINH_4 -> new TANBINH4();
                case BossID.CHIEN_BINH_5 -> new CHIENBINH5();
                case BossID.CHIEN_BINH_0 -> new CHIENBINH0();
                case BossID.CHIEN_BINH_1 -> new CHIENBINH1();
                case BossID.CHIEN_BINH_2 -> new CHIENBINH2();
                case BossID.CHIEN_BINH_3 -> new CHIENBINH3();
                case BossID.CHIEN_BINH_4 -> new CHIENBINH4();
                case BossID.DOI_TRUONG_5 -> new DOITRUONG5();
                case BossID.SO_4 -> new SO4();
                case BossID.SO_3 -> new SO3();
                case BossID.SO_2 -> new SO2();
                case BossID.SO_1 -> new SO1();
                case BossID.TIEU_DOI_TRUONG -> new TDT();
                case BossID.CUMBER -> new Cumber();
                case BossID.SU -> new Su();
                case BossID.MAI -> new Mai();
                case BossID.FILAP -> new Pilap();
                case BossID.AN_TROM -> new AnTrom();
                case BossID.BUJIN -> new BUJIN();
                case BossID.KOGU -> new KOGU();
                case BossID.ZANGYA -> new ZANGYA();
                case BossID.BIDO -> new BIDO();
                case BossID.BOJACK -> new BOJACK();
                case BossID.SUPER_BOJACK -> new SUPER_BOJACK();
                case BossID.KUKU -> new Kuku();
                case BossID.MAP_DAU_DINH -> new MapDauDinh();
                case BossID.RAMBO -> new Rambo();
                case BossID.TAU_PAY_PAY_DONG_NAM_KARIN -> new TaoPaiPai();
                case BossID.DRABURA -> new Drabura();
                case BossID.BUI_BUI -> new BuiBui();
                case BossID.BUI_BUI_2 -> new BuiBui2();
                case BossID.YA_CON -> new Yacon();
                case BossID.DRABURA_2 -> new Drabura2();
                case BossID.GOKU -> new Goku();
                case BossID.CADIC -> new Cadic();
                case BossID.MABU_12H -> new Mabu();
                case BossID.DRABURA_3 -> new Drabura3();
                case BossID.MABU -> new Mabu2H();
                case BossID.SUPERBU -> new SuperBu();
                case BossID.FIDE -> new Fide();
                case BossID.DR_KORE -> new DrKore();
                case BossID.ANDROID_19 -> new Android19();
                case BossID.ANDROID_13 -> new Android13();
                case BossID.ANDROID_14 -> new Android14();
                case BossID.ANDROID_15 -> new Android15();
                case BossID.PIC -> new Pic();
                case BossID.POC -> new Poc();
                case BossID.KING_KONG -> new KingKong();
                case BossID.XEN_BO_HUNG -> new XenBoHung();
                case BossID.SIEU_BO_HUNG -> new SieuBoHung();
                case BossID.XEN_CON_1 -> new XENCON1();
                case BossID.XEN_CON_2 -> new XENCON2();
                case BossID.XEN_CON_3 -> new XENCON3();
                case BossID.XEN_CON_4 -> new XENCON4();
                case BossID.XEN_CON_5 -> new XENCON5();
                case BossID.XEN_CON_6 -> new XENCON6();
                case BossID.XEN_CON_7 -> new XENCON7();
                case BossID.COOLER -> new Cooler();
                case BossID.BROLY -> new Broly();
                case BossID.KHIDOT -> new KhiDot();
                case BossID.NGUYETTHAN -> new NguyetThan();
                case BossID.NHATTHAN -> new NhatThan();
                case BossID.GOLDEN_FRIEZA -> new GoldenFrieza();
                case BossID.DEATH_BEAM_1 -> new DeathBeam1();
                case BossID.DEATH_BEAM_2 -> new DeathBeam2();
                case BossID.DEATH_BEAM_3 -> new DeathBeam3();
                case BossID.DEATH_BEAM_4 -> new DeathBeam4();
                case BossID.DEATH_BEAM_5 -> new DeathBeam5();
                case BossID.BIMA -> new BiMa();
                case BossID.MATROI -> new MaTroi();
                case BossID.DOI -> new Doi();
                case BossID.ONG_GIA_NOEL -> new OngGiaNoel();
                case BossID.SON_TINH -> new SonTinh();
                case BossID.THUY_TINH -> new ThuyTinh();
                case BossID.LAN_CON -> new LanCon();
                case BossID.SOI_HEC_QUYN_1 -> new SoiHecQuyn();
                case BossID.XINBATO_1 -> new Xinbato();
                case BossID.KID_BUU -> new KidBuu();
                case BossID.KID_XEN -> new KidXen();
                case BossID.BLACK_GOKU -> new BlackGoku();
                case BossID.BLACK_GOKU_ROSE -> new BlackGokuRose();
                case BossID.CHILLER_1 -> new Chiller();
                case BossID.SUPER_PIC -> new SuperPic();
                default -> null;
            };
            if (boss != null) {
                this.addBoss(boss);
            }
            return boss;
        } catch (Exception e) {
            Logger.error(e + "\n");
            return null;
        }
    }

    public Boss getBoss(int id) {
        try {
            Boss boss = this.bosses.get(id);
            if (boss != null) {
                return boss;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public void showListBoss(Player player) {
        if (player == null || !player.isAdmin()) {
            return;
        }
        player.idMark.setMenuType(3);
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Boss");

            // Đếm số boss hiển thị (đã lọc map & HIDDEN_SHOW_BOSS)
            long count = bosses.stream()
                    .filter(Objects::nonNull)
                    .filter(boss -> !isHiddenInShow(boss))
                    .filter(boss -> {
                        try {
                            int mapJoin = boss.data[0].getMapJoin()[0];
                            return !MapService.gI().isMapBossFinal(mapJoin)
                                    && !MapService.gI().isMapHuyDiet(mapJoin)
                                    && !MapService.gI().isMapYardart(mapJoin)
                                    && !MapService.gI().isMapMaBu(mapJoin)
                                    && !MapService.gI().isMapBlackBallWar(mapJoin);
                        } catch (Throwable t) {
                            return false;
                        }
                    })
                    .count();
            msg.writer().writeByte((int) count);

            // Ghi danh sách
            for (int i = 0; i < bosses.size(); i++) {
                Boss boss = this.bosses.get(i);
                if (boss == null || isHiddenInShow(boss)) {
                    continue;
                }
                // Lọc theo map như cũ
                try {
                    int mapJoin = boss.data[0].getMapJoin()[0];
                    if (MapService.gI().isMapBossFinal(mapJoin)
                            || MapService.gI().isMapYardart(mapJoin)
                            || MapService.gI().isMapHuyDiet(mapJoin)
                            || MapService.gI().isMapMaBu(mapJoin)
                            || MapService.gI().isMapBlackBallWar(mapJoin)) {
                        continue;
                    }
                } catch (Throwable t) {
                    continue;
                }

                // Ghi item
                msg.writer().writeInt(i);
                msg.writer().writeInt(i);
                short[] outfit;
                String name;
                try {
                    outfit = boss.data[0].getOutfit();
                    name = boss.data[0].getName();
                } catch (Throwable t) {
                    // fallback an toàn
                    outfit = new short[]{-1, -1, -1};
                    name = "Unknown";
                }
                msg.writer().writeShort(outfit[0]);
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(outfit[1]);
                msg.writer().writeShort(outfit[2]);
                msg.writer().writeUTF(name);

                if (boss.zone != null && boss.zone.map != null) {
                    msg.writer().writeUTF(String.valueOf(boss.bossStatus));
                    msg.writer().writeUTF(boss.zone.map.mapName + "(" + boss.zone.map.mapId + ") khu " + boss.zone.zoneId);
                } else {
                    msg.writer().writeUTF(String.valueOf(boss.bossStatus));
                    msg.writer().writeUTF("=))");
                }
            }

            player.sendMessage(msg);
        } catch (Exception ignored) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public Boss getBossById(int bossId) {
        return this.bosses.stream().filter(boss -> boss.id == bossId && !boss.isDie()).findFirst().orElse(null);
    }

    public boolean checkBosses(Zone zone, int BossID) {
        return this.bosses.stream()
                .filter(boss -> boss.id == BossID && boss.zone != null && boss.zone.equals(zone) && !boss.isDie())
                .findFirst().orElse(null) != null;
    }

    public Player findBossClone(Player player) {
        return player.zone.getBosses().stream().filter(boss -> boss.id < -100_000_000 && !boss.isDie()).findFirst()
                .orElse(null);
    }

    public Boss getBossById(int bossId, int mapId, int zoneId) {
        return this.bosses.stream().filter(boss -> boss.id == bossId && boss.zone != null
                        && boss.zone.map.mapId == mapId && boss.zone.zoneId == zoneId && !boss.isDie()).findFirst()
                .orElse(null);
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                long st = System.currentTimeMillis();
                for (int i = this.bosses.size() - 1; i >= 0; i--) {
                    try {
                        this.bosses.get(i).update();
                    } catch (Exception e) {

                    }
                }
                Functions.sleep(Math.max(150 - (System.currentTimeMillis() - st), 10));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
