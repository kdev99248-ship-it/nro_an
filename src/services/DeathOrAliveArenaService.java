package services;

/*
 * @Author: NgojcDev
 */

import managers.DeathOrAliveArena;
import managers.DeathOrAliveArenaManager;
import map.Zone;
import player.Player;
import network.Message;
import consts.ConstNpc;
import java.io.IOException;
import map.Map;
import npc.Npc;
import services.NpcManager;
import services.InventoryService;
import services.MapService;
import services.Service;
import services.ChangeMapService;
import static utils.Util.setTimeout;

public class DeathOrAliveArenaService {

    private static DeathOrAliveArenaService i;

    public static DeathOrAliveArenaService gI() {
        if (i == null) {
            i = new DeathOrAliveArenaService();
        }
        return i;
    }

    public void startChallenge(Player player) {
        Zone zone = getMapChallenge(112);
        if (zone != null) {
            if (InventoryService.gI().findItemBag(player, 77) != null
                    && InventoryService.gI().findItemBag(player, 77).quantity >= player.thoiVangVoDaiSinhTu) {
                InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 77),
                        player.thoiVangVoDaiSinhTu);
                InventoryService.gI().sendItemBags(player);
                player.thoiVangVoDaiSinhTu += 1;
                player.lastTimePKVoDaiSinhTu = System.currentTimeMillis();
            } else {
                Service.gI().sendThongBao(player, "Bạn không có đủ ngọc!");
                return;
            }
            if (!zone.equals(player.zone)) {
                ChangeMapService.gI().changeMap(player, zone, player.location.x, 408);
            }
            setTimeout(() -> {
                Npc baHatMit = NpcManager.getNpc(ConstNpc.BA_HAT_MIT);
                DeathOrAliveArena vdst = new DeathOrAliveArena();
                vdst.setPlayer(player);
                vdst.setNpc(baHatMit);
                vdst.setRound(0);
                vdst.toTheNextRound();
                vdst.setZone(zone);
                vdst.setTimeTotal(0);
                DeathOrAliveArenaManager.gI().add(vdst);
                baHatMit.npcChat(player, "Số thứ tự của ngươi là 1 chuẩn bị thi đấu nhé.");
                Service.gI().releaseCooldownSkill(player);
                player.isPKDHVT = true;
                player.lastTimePKDHVT23 = System.currentTimeMillis();
                vdst.endChallenge = false;
            }, 500);
        } else {
        }
    }

    public void sendTypePK(Player player, Player boss) {
        Message msg;
        try {
            msg = Service.gI().messageSubCommand((byte) 35);
            msg.writer().writeInt((int) boss.id);
            msg.writer().writeByte(3);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public Zone getMapChallenge(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        Zone zone = null;
        try {
            if (map != null) {
                int zoneId = 0;
                while (zoneId < map.zones.size()) {
                    Zone zonez = map.zones.get(zoneId);
                    if (DeathOrAliveArenaManager.gI().getVDST(zonez) == null) {
                        zone = zonez;
                        break;
                    }
                    zoneId++;
                }
            }
        } catch (Exception e) {
        }
        return zone;
    }
}
