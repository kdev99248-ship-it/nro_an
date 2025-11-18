package author_ngojc;

/*
 * @Author: NgojcDev
 */
//import bot.BotManager;

import bot.BotManager;
import services.SummonDragonXuong;
import utils.FileRunner;
import control.boss.BrolyManager;
import managers.ConsignShopManager;
import database.HistoryTransactionDAO;
import control.boss.BossManager;
import control.boss.OtherBossManager;
import control.boss.TreasureUnderSeaManager;
import control.boss.SnakeWayManager;
import control.boss.RedRibbonHQManager;
import control.boss.GasDestroyManager;
import control.boss.YardartManager;
import control.boss.ChristmasEventManager;
import control.boss.FinalBossManager;
import control.boss.HalloweenEventManager;
import control.boss.HungVuongEventManager;
import control.boss.LunarNewYearEventManager;
import control.boss.SkillSummonedManager;
import control.boss.TrungThuEventManager;

import java.io.IOException;

import interfaces.ISession;
import network.Network;
import network.MyKeyHandler;
import network.MySession;
import services.ClanService;
import services.NgocRongNamecService;
import services.HirudegarService;
import utils.Logger;

import java.util.*;

import managers.The23rdMartialArtCongressManager;
import managers.DeathOrAliveArenaManager;
import event.EventManager;
import database.EventDAO;
import managers.WorldMartialArtsTournamentManager;
import network.MessageSendCollect;
import managers.ShenronEventManager;
import managers.SuperRankManager;
import interfaces.ISessionAcceptHandler;
import server.Client;
import server.Controller;
import server.Manager;

public class DragonRun {

    public static String timeStart;

    public static final Map<Object, Object> CLIENTS = new HashMap<>();
    // Config
    public static String LINK = "HANHTRINHNRO.PRO";
    public static String ZALO = "https://zalo.me/g/qabzvn331";
    public static String NAME = "Hành Trình Nro";
    public static String IP = "127.0.0.1";
    public static int PORT = 14445;
    // Text Vip
    public static String TIME_VIP_START = "15/02/2025";
    public static String TIME_VIP_END = "15/03/2025";
    // Text Notify -
    public static String NOTIFY_1 = "Hành Trình Nro";
    public static String NOTIFY_2 = "Hành Trình Nro";
    public static String NOTIFY_3 = "Hành Trình Nro";
    // Mode Event
    public static boolean LUNNAR_NEW_YEAR = false;

    public static boolean INTERNATIONAL_WOMANS_DAY = false;

    public static boolean CHRISTMAS = false;

    public static boolean HALLOWEEN = true;

    public static boolean HUNG_VUONG = false;

    public static boolean TRUNG_THU = false;

    public static boolean TOP_UP = false;
    // Close Panel
    private static DragonRun instance;

    public static boolean isRunning;

    public void init() {
        Manager.gI();
        HistoryTransactionDAO.deleteHistory();
    }

    public static DragonRun gI() {
        if (instance == null) {
            instance = new DragonRun();
            instance.init();
        }
        return instance;
    }

    public void run() {
        isRunning = true;
        activeServerSocket();
        utils.MemoryOptimizer.gI().startMemoryMonitoring();

        Thread.ofVirtual().name("Update NRNM").start(NgocRongNamecService.gI());
        Thread.ofVirtual().name("Update Super Rank").start(SuperRankManager.gI());
        Thread.ofVirtual().name("Update DHVT23").start(The23rdMartialArtCongressManager.gI());
        Thread.ofVirtual().name("Update Võ Đài Sinh Tử").start(DeathOrAliveArenaManager.gI());
        Thread.ofVirtual().name("Update WMAT").start(WorldMartialArtsTournamentManager.gI());
        Thread.ofVirtual().name("Update Bảo Trì Tự Động").start(AutoMaintenance.gI());
        Thread.ofVirtual().name("Update Shenron").start(ShenronEventManager.gI());
        Thread.ofVirtual().name("Update Hirudegarn").start(HirudegarService.gI());
        Thread.ofVirtual().name("Update Dragon Xuong").start(SummonDragonXuong.gI());
        BossManager.gI().loadBoss();
        Manager.MAPS.forEach(map.Map::initBoss);
        EventManager.gI().init();

        new Thread(BossManager.gI(), "Update boss").start();
        Thread.ofVirtual().name("Update yardart boss").start(YardartManager.gI());
        Thread.ofVirtual().name("Update final boss").start(FinalBossManager.gI());
        Thread.ofVirtual().name("Update Skill-summoned boss").start(SkillSummonedManager.gI());
        Thread.ofVirtual().name("Update broly boss").start(BrolyManager.gI());
        Thread.ofVirtual().name("Update other boss").start(OtherBossManager.gI());
        Thread.ofVirtual().name("Update reb ribbon hq boss").start(RedRibbonHQManager.gI());
        Thread.ofVirtual().name("Update treasure under sea boss").start(TreasureUnderSeaManager.gI());
        Thread.ofVirtual().name("Update snake way boss").start(SnakeWayManager.gI());
        Thread.ofVirtual().name("Update gas destroy boss").start(GasDestroyManager.gI());
        Thread.ofVirtual().name("Update trung thu event boss").start(TrungThuEventManager.gI());
        Thread.ofVirtual().name("Update halloween event boss").start(HalloweenEventManager.gI());
        Thread.ofVirtual().name("Update christmas event boss").start(ChristmasEventManager.gI());
        Thread.ofVirtual().name("Update Hung Vuong event boss").start(HungVuongEventManager.gI());
        Thread.ofVirtual().name("Update lunar new year event boss").start(LunarNewYearEventManager.gI());
        Thread.ofVirtual().name("Thread Bot Game").start(BotManager.gI());
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame f = new javax.swing.JFrame("Hành Trình Ngọc Rồng • Server Dashboard");
            f.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
            // đóng cửa sổ => tắt server an toàn
            f.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    try {
                        DragonRun.gI().close();
                    } catch (Exception ignored) {
                    }
                }
            });
            f.setContentPane(new server.panel()); // <- class panel bạn đã có
            f.setSize(760, 560);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }

    private void activeServerSocket() {
        try {
            Network.gI().init().setAcceptHandler(new ISessionAcceptHandler() {
                        @Override
                        public void sessionInit(ISession is) {
                            if (!canConnectWithIp(is.getIP())) {
                                is.disconnect();
                                return;
                            }
                            is.setMessageHandler(Controller.gI())
                                    .setSendCollect(new MessageSendCollect())
                                    .setKeyHandler(new MyKeyHandler())
                                    .startCollect().startQueueHandler();
                        }

                        @Override
                        public void sessionDisconnect(ISession session) {
                            Logger.log("Disconnect from " + session.getIP());
                            Client.gI().kickSession((MySession) session);
                        }
                    }).setTypeSessionClone(MySession.class)
                    .setDoSomeThingWhenClose(() -> {
                        Logger.error("SERVER CLOSE\n");
                        System.exit(0);
                    })
                    .start(PORT);
        } catch (Exception e) {
        }
    }

    private boolean canConnectWithIp(String ipAddress) {
        Object o = CLIENTS.get(ipAddress);
        if (o == null) {
            CLIENTS.put(ipAddress, 1);
            return true;
        } else {
            int n = Integer.parseInt(String.valueOf(o));
            if (n < Manager.MAX_PER_IP) {
                n++;
                CLIENTS.put(ipAddress, n);
                return true;
            } else {
                return false;
            }
        }
    }

    public void disconnect(MySession session) {
        Object o = CLIENTS.get(session.getIP());
        if (o != null) {
            int n = Integer.parseInt(String.valueOf(o));
            n--;
            if (n < 0) {
                n = 0;
            }
            CLIENTS.put(session.getIP(), n);
        }
    }

    public void close() {
        isRunning = false;
        try {
            ClanService.gI().close();
        } catch (Exception e) {
            Logger.error("Lỗi save clan!\n");
        }
        try {
            ConsignShopManager.gI().save();
        } catch (Exception e) {
            Logger.error("Lỗi save shop ký gửi!\n");
        }
        Client.gI().close();
        EventDAO.save();

        utils.MemoryOptimizer.gI().stopMemoryMonitoring();
        utils.ThreadManager.gI().shutdown();

        if (AutoMaintenance.isRunning) {
            AutoMaintenance.isRunning = false;
            try {
                String batchFilePath = "run.bat";
                FileRunner.runBatchFile(batchFilePath);
            } catch (IOException e) {
            }
        }
        System.exit(0);
    }
}
