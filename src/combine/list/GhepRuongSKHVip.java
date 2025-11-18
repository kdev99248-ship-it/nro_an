package combine.list;

import combine.CombineService;
import consts.ConstNpc;
import item.Item;
import lombok.extern.slf4j.Slf4j;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

import java.util.List;

@Slf4j
public class GhepRuongSKHVip {

    private static final long COST_GHEP_RUONG_SKH_VIP = 500_000_000;

    // ---------------- SHOW INFO ----------------
    public static void showInfoCombine(Player player) {
        if (checkValidCoundBag(player)) {
            return;
        }
        if (player.combineNew.itemsCombine.size() != 3) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Hãy đặt 3 hộp SKH khác nhau vào", "Đóng");
            return;
        }
        Item it1 = null, it2 = null, it3 = null;
        for (Item item : player.combineNew.itemsCombine) {
            if (item.template.id == 1771) {
                it1 = item;
            }
            if (item.template.id == 1772) {
                it2 = item;
            }
            if (item.template.id == 1773) {
                it3 = item;
            }
        }
        if (it1 == null || it2 == null || it3 == null) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Hãy đặt 3 hộp SKH khác nhau vào", "Đóng");
            return;
        }

        StringBuilder npcSay = new StringBuilder();
        npcSay.append("|7|Thông tin Nâng Cấp").append("\n");
        npcSay.append("|5|Sau khi ghép sẽ nhận được x1 Rương SKH VIP").append("\n");
        npcSay.append("|2|Cần x500 Tr Vàng").append("\n");
        npcSay.append("|7|Bạn có muốn tiếp tục?");
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay.toString(), "Ghép", "Từ Chối");
    }

    static boolean checkValidCoundBag(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
            return true;
        }
        return false;
    }

    public static void ghepRuongSKhVIP(Player player) {
        List<Item> itemList = player.combineNew.itemsCombine;
        Item it1 = null, it2 = null, it3 = null;
        for (Item item : itemList) {
            if (item.template.id == 1771) {
                it1 = item;
            }
            if (item.template.id == 1772) {
                it2 = item;
            }
            if (item.template.id == 1773) {
                it3 = item;
            }
        }
        if (it1 == null || it2 == null || it3 == null) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Hãy đặt 3 hộp SKH khác nhau vào", "Đóng");
            return;
        }

        // check money
        if (player.inventory.gold - COST_GHEP_RUONG_SKH_VIP < 0) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Không đủ vàng còn thiếu " + Util.powerToString(COST_GHEP_RUONG_SKH_VIP - player.inventory.gold) + " vàng", "Đóng");
            return;
        }

        player.inventory.gold -= COST_GHEP_RUONG_SKH_VIP;
        Item ruongSKhVip = ItemService.gI().createNewItem((short) 1538, 1);
        Service.gI().sendMoney(player);
        InventoryService.gI().addItemBag(player, ruongSKhVip);
        InventoryService.gI().subQuantityItemsBag(player, it1, 1);
        InventoryService.gI().subQuantityItemsBag(player, it2, 1);
        InventoryService.gI().subQuantityItemsBag(player, it3, 1);
        InventoryService.gI().sendItemBags(player);
        CombineService.gI().sendEffectSuccessCombine(player);
        Service.gI().sendThongBao(player, "Bạn nhận được x1 " + ruongSKhVip.template.name);
        CombineService.gI().reOpenItemCombine(player);
    }
}
