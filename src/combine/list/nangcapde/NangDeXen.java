package combine.list.nangcapde;

/*
 * @Author: NgojcDev
 * @Description: Nâng cấp đệ tử Xên
 */
import consts.ConstNpc;
import item.Item;
import combine.CombineService;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

public class NangDeXen {

    private static final int MANH_XEN_ID = 1759;     // Mảnh Xên
    private static final int MANH_FIDE_ID = 1758;    // Mảnh Fide
    private static final int TRUNG_MABU_ID = 568;    // Trứng Mabư
    private static final int DA_NGU_SAC_ID = 674;    // Đá ngũ sắc
    private static final int TRUNG_DE_XEN_ID = 1762; // Trứng đệ Xên (kết quả)

    // Số lượng cần thiết
    private static final int REQUIRED_MANH_XEN = 500;
    private static final int REQUIRED_MANH_FIDE = 500;
    private static final int REQUIRED_TRUNG_MABU = 10;
    private static final int REQUIRED_DA_NGU_SAC = 20;

    // Tỉ lệ thành công
    private static final float SUCCESS_RATE = 70.0f;

    public static void showInfoCombine(Player player) {
        if (player.combineNew.itemsCombine.size() != 4) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Cần đúng 4 loại vật phẩm:\n- 500 Mảnh Xên\n- 500 Mảnh Fide\n- 10 Trứng Mabư\n- 20 Đá ngũ sắc", "Đóng");
            return;
        }

        Item manhXen = null;
        Item manhFide = null;
        Item trungMabu = null;
        Item daNguSac = null;

        for (Item item : player.combineNew.itemsCombine) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case MANH_XEN_ID ->
                        manhXen = item;
                    case MANH_FIDE_ID ->
                        manhFide = item;
                    case TRUNG_MABU_ID ->
                        trungMabu = item;
                    case DA_NGU_SAC_ID ->
                        daNguSac = item;
                    default -> {
                    }
                }
            }
        }

        if (manhXen == null || manhFide == null || trungMabu == null || daNguSac == null) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Thiếu vật phẩm cần thiết:\n- 500 Mảnh Xên\n- 500 Mảnh Fide\n- 10 Trứng Mabư\n- 20 Đá ngũ sắc", "Đóng");
            return;
        }

        String errorMsg = "";
        if (manhXen.quantity < REQUIRED_MANH_XEN) {
            errorMsg += "Còn thiếu " + (REQUIRED_MANH_XEN - manhXen.quantity) + " Mảnh Xên\n";
        }
        if (manhFide.quantity < REQUIRED_MANH_FIDE) {
            errorMsg += "Còn thiếu " + (REQUIRED_MANH_FIDE - manhFide.quantity) + " Mảnh Fide\n";
        }
        if (trungMabu.quantity < REQUIRED_TRUNG_MABU) {
            errorMsg += "Còn thiếu " + (REQUIRED_TRUNG_MABU - trungMabu.quantity) + " Trứng Mabư\n";
        }
        if (daNguSac.quantity < REQUIRED_DA_NGU_SAC) {
            errorMsg += "Còn thiếu " + (REQUIRED_DA_NGU_SAC - daNguSac.quantity) + " Đá ngũ sắc\n";
        }

        if (!errorMsg.isEmpty()) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Không đủ vật phẩm:\n" + errorMsg, "Đóng");
            return;
        }

        String infoMsg = "|2|Nâng cấp đệ tử Xên\n|0|"
                + "Cần:\n"
                + "|1|~ " + REQUIRED_MANH_XEN + " Mảnh Xên\n"
                + "|1|~ " + REQUIRED_MANH_FIDE + " Mảnh Fide\n"
                + "|1|~ " + REQUIRED_TRUNG_MABU + " Trứng Mabư\n"
                + "|1|~ " + REQUIRED_DA_NGU_SAC + " Đá ngũ sắc\n"
                + "|7|Tỉ lệ thành công: " + SUCCESS_RATE + "%\n"
                + "|2|Kết quả: Trứng đệ Xên";

        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                infoMsg, "Nâng cấp", "Từ chối");
    }

    public static void nangDeXen(Player player) {
        if (player.combineNew.itemsCombine.size() != 4) {
            return;
        }

        Item manhXen = null;
        Item manhFide = null;
        Item trungMabu = null;
        Item daNguSac = null;

        for (Item item : player.combineNew.itemsCombine) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case MANH_XEN_ID ->
                        manhXen = item;
                    case MANH_FIDE_ID ->
                        manhFide = item;
                    case TRUNG_MABU_ID ->
                        trungMabu = item;
                    case DA_NGU_SAC_ID ->
                        daNguSac = item;
                    default -> {
                    }
                }
            }
        }

        if (manhXen == null || manhFide == null || trungMabu == null || daNguSac == null
                || manhXen.quantity < REQUIRED_MANH_XEN
                || manhFide.quantity < REQUIRED_MANH_FIDE
                || trungMabu.quantity < REQUIRED_TRUNG_MABU
                || daNguSac.quantity < REQUIRED_DA_NGU_SAC) {
            Service.gI().sendThongBao(player, "Không đủ vật phẩm để thực hiện nâng cấp");
            return;
        }

        if (Util.isTrue(SUCCESS_RATE, 100)) {

            InventoryService.gI().subQuantityItemsBag(player, manhXen, REQUIRED_MANH_XEN);
            InventoryService.gI().subQuantityItemsBag(player, manhFide, REQUIRED_MANH_FIDE);
            InventoryService.gI().subQuantityItemsBag(player, trungMabu, REQUIRED_TRUNG_MABU);
            InventoryService.gI().subQuantityItemsBag(player, daNguSac, REQUIRED_DA_NGU_SAC);

            Item trungDeXen = ItemService.gI().createNewItem((short) TRUNG_DE_XEN_ID, 1);
            InventoryService.gI().addItemBag(player, trungDeXen);

            CombineService.gI().sendEffectSuccessCombine(player);
            Service.gI().sendThongBao(player, "Chúc mừng! Bạn đã nâng cấp thành công và nhận được Trứng đệ Xên");
        } else {

            InventoryService.gI().subQuantityItemsBag(player, manhXen, REQUIRED_MANH_XEN);
            InventoryService.gI().subQuantityItemsBag(player, manhFide, REQUIRED_MANH_FIDE);
            InventoryService.gI().subQuantityItemsBag(player, trungMabu, REQUIRED_TRUNG_MABU);
            InventoryService.gI().subQuantityItemsBag(player, daNguSac, REQUIRED_DA_NGU_SAC);

            CombineService.gI().sendEffectFailCombine(player);
            Service.gI().sendThongBao(player, "Rất tiếc! Nâng cấp thất bại");
        }

        InventoryService.gI().sendItemBags(player);
        CombineService.gI().reOpenItemCombine(player);
        player.combineNew.itemsCombine.clear();
    }
}
