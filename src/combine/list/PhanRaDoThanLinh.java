package combine.list;

/*
 * @Author: NgojcDev
 */
import consts.ConstNpc;
import item.Item;
import combine.CombineService;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

public class PhanRaDoThanLinh {

    public static void showInfoCombine(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (player.combineNew.itemsCombine.size() == 1) {
                Item item = player.combineNew.itemsCombine.get(0);
                if (item != null && item.isNotNullItem() && isGodItem(item)) {
                    String itemName = getItemTypeName(item.template.id);
                    int soLuongDa = getActivationStoneAmount(item.template.id);
                    String npcSay = "|2|Con có muốn phân rã " + itemName + "\n"
                            + "Nhận được random đá kích hoạt\n"
                            + "|0|Tốn: " + Util.numberToMoney(500_000_000) + " vàng";
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                            npcSay, "Phân rã\n" + Util.numberToMoney(500_000_000) + " vàng", "Từ chối");
                } else {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Chỉ có thể phân rã đồ thần linh (ID: 555-567)", "Đóng");
                }
            } else {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Cần đúng 1 món đồ thần linh để phân rã", "Đóng");
            }
        } else {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
        }
    }

    public static void phanRaDoThanLinh(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Hành trang đã đầy, cần ít nhất 1 chỗ trống");
            return;
        }

        if (player.combineNew.itemsCombine.isEmpty()) {
            Service.gI().sendThongBao(player, "Không tìm thấy vật phẩm để phân rã");
            return;
        }

        Item item = player.combineNew.itemsCombine.get(0);
        if (item == null || !item.isNotNullItem() || item.template == null) {
            Service.gI().sendThongBao(player, "Vật phẩm không hợp lệ");
            return;
        }

        if (!isGodItem(item)) {
            Service.gI().sendThongBao(player, "Chỉ có thể phân rã đồ thần linh.");
            return;
        }

        int goldRequired = 500_000_000;
        if (player.inventory.gold < goldRequired) {
            Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện (Cần: " + Util.numberToMoney(goldRequired) + ")");
            return;
        }

        int itemId = item.template.id;
        String itemName = getItemTypeName(itemId);
        int soLuongDa = getActivationStoneAmount(itemId);

        player.inventory.gold -= goldRequired;

        Item daKichHoat = ItemService.gI().createNewItem((short) 1770, soLuongDa);
        InventoryService.gI().addItemBag(player, daKichHoat);

        InventoryService.gI().subQuantityItemsBag(player, item, 1);

        CombineService.gI().sendEffectSuccessCombine(player);
        InventoryService.gI().sendItemBags(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
        player.combineNew.itemsCombine.clear();

        Service.gI().sendThongBao(player, "Phân rã " + itemName + " thành công! Nhận được " + soLuongDa + " đá kích hoạt");
    }

    private static boolean isGodItem(Item item) {
        if (item == null || item.template == null) {
            return false;
        }
        int id = item.template.id;

        return id >= 555 && id <= 567;
    }

    private static int getActivationStoneAmount(int itemId) {
        return switch (itemId) {
            case 561 ->
                Util.nextInt(100, 200);
            case 562, 564, 566 ->
                Util.nextInt(50, 100);
            case 556, 558, 560 ->
                Util.nextInt(30, 50);
            case 555, 557, 559 ->
                Util.nextInt(20, 30);
            case 563, 565, 567 ->
                Util.nextInt(10, 20);
            default ->
                1;
        }; // Nhẫn thần linh
        // Găng thần linh (562, 564, 566)
        // Quần thần linh (556, 558, 560)
        // Áo thần linh (555, 557, 559)
        // Giày thần linh (563, 565, 567)
    }

    private static String getItemTypeName(int itemId) {
        return switch (itemId) {
            case 561 ->
                "Nhẫn thần linh";
            case 562, 564, 566 ->
                "Găng thần linh";
            case 556, 558, 560 ->
                "Quần thần linh";
            case 555, 557, 559 ->
                "Áo thần linh";
            case 563, 565, 567 ->
                "Giày thần linh";
            default ->
                "Đồ thần linh";
        };
    }
}
