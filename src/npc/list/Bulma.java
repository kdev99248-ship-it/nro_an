package npc.list;

/*
 * @Author: NgojcDev
 */

import author_ngojc.DragonRun;
import author_ngojc.GameServer;
import author_ngojc.TextServer;
import consts.ConstNpc;
import consts.ConstPlayer;
import npc.Npc;
import player.Player;
import rankings.TopService;
import services.*;

public class Bulma extends Npc {

    public Bulma(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                if (mapId == 5 && DragonRun.HALLOWEEN) {
                    createOtherMenu(player, ConstNpc.MENU_HALLOWEEN, TextServer.HALLOWEEN, "Đổi kẹo", "Cửa hàng", "Đổi hòm\nHalloween", "Đổi túi\nmù", "Bảng xếp\nhạng");
                } else if (player.gender != 0) {
                    NpcService.gI().createTutorial(player, tempId, this.avartar,
                            "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất");
                } else if (!player.inventory.itemsDaBan.isEmpty()) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng", "Mua lại vật phẩm đã bán");
                } else {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.idMark.isBaseMenu()) {
                switch (select) {
                    case 0 -> {
                        // Shop
                        if (player.gender == ConstPlayer.TRAI_DAT) {
                            ShopService.gI().opendShop(player, "BUNMA", true);
                        } else {
                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất", "Đóng");
                        }
                    }
                    case 1 -> {
                        if (!player.inventory.itemsDaBan.isEmpty()) {
                            ShopService.gI().opendShop(player, "ITEMS_DABAN", true);
                        }
                    }
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_HALLOWEEN) {
                switch (select) {
                    case 0:
                        createOtherMenu(player, ConstNpc.MENU_CHON_SL_KEO, "Bạn muốn đổi bao nhiêu kẹo?", "1 cái", "10 cái", "Tất cả");
                        break;
                    case 1:
                        ShopService.gI().opendShop(player, "BULMA_HALLOWEEN", true);
                        break;
                    case 2:
                        createOtherMenu(player, ConstNpc.MENU_DOI_THIEP, "Bạn muốn đổi bao nhiêu hòm?", "1 hòm", "10 hòm", "Tất cả");
                        break;
                    case 3:
                        createOtherMenu(player, ConstNpc.MENU_DOI_TUI_MU, "Bạn muốn đổi bao nhiêu túi?", "1 cái", "10 túi", "Tất cả");
                        break;
                    case 4:
                        TopService.showListTop(player, 4);
                        break;
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_CHON_SL_KEO) {
                switch (select) {
                    case 0:
                        InventoryService.gI().tradeKeo(player, 1, false);
                        break;
                    case 1:
                        InventoryService.gI().tradeKeo(player, 10, false);
                        break;
                    case 2:
                        InventoryService.gI().tradeKeo(player, -1, true);
                        break;
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_DOI_THIEP) {
                switch (select) {
                    case 0:
                        InventoryService.gI().tradeThiep(player, 1, false);
                        break;
                    case 1:
                        InventoryService.gI().tradeThiep(player, 10, false);
                        break;
                    case 2:
                        InventoryService.gI().tradeThiep(player, -1, true);
                        break;
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_DOI_TUI_MU) {
                switch (select) {
                    case 0:
                        InventoryService.gI().tradeTuiMu(player, 1, false);
                        break;
                    case 1:
                        InventoryService.gI().tradeTuiMu(player, 10, false);
                        break;
                    case 2:
                        InventoryService.gI().tradeTuiMu(player, -1, true);
                        break;
                }
            }
        }
    }
}
