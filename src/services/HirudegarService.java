package services;

/*
 * @Author: NgojcDev
 */

import java.util.List;
import map.Map;
import map.Zone;
import player.Player;
import server.Manager;
import services.ChangeMapService;
import services.MapService;
import services.Service;
import utils.TimeUtil;

public class HirudegarService implements Runnable {

    private static HirudegarService instance;

    public static HirudegarService gI() {
        if (instance == null) {
            instance = new HirudegarService();
        }
        return instance;
    }

    private HirudegarService() {
    }

    @Override
    public void run() {
        while (true) {
            try {
                long startTime = System.currentTimeMillis();
                update();
                Thread.sleep(Math.max(1000 - (System.currentTimeMillis() - startTime), 100));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        // Nếu KHÔNG phải giờ mở Hirudegarn (22h-23h), đưa tất cả người chơi về nhà
        if (!TimeUtil.isHirudegarOpen()) {
            sendAllPlayersInHirudegarMapHome();
        }
    }

    /**
     * Đưa tất cả người chơi trong map Hirudegarn (mapId = 126) về nhà
     */
    public void sendAllPlayersInHirudegarMapHome() {
        try {
            for (Map map : Manager.MAPS) {
                if (map.mapId == 126) { // Map Hirudegarn
                    for (Zone zone : map.zones) {
                        // Tạo copy của list để tránh ConcurrentModificationException
                        List<Player> players = new java.util.ArrayList<>(zone.getPlayers());
                        for (Player player : players) {
                            if (player != null && player.isPl() && player.zone != null && player.zone.map.mapId == 126) {
                                // Thông báo cho người chơi
                                Service.gI().sendThongBao(player, "Hirudegarn chỉ xuất hiện từ 22h-23h! Bạn được đưa về nhà");
                                
                                // Đưa về nhà (map tương ứng với gender của player)
                                int homeMapId = player.gender + 21; // 21, 22, 23 là map nhà của 3 hành tinh
                                ChangeMapService.gI().changeMapInYard(player, homeMapId, -1, -1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
