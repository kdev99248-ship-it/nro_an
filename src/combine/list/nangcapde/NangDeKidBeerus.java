package combine.list.nangcapde;

/*
 * @Author: NgojcDev
 * @Description: Nâng cấp đệ tử Kid Beerus
 */
import consts.ConstNpc;
import item.Item;
import combine.CombineService;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

public class NangDeKidBeerus {

    private static final int BINH_HUT_NANG_LUONG_ID = 1765; // Bình hút năng lượng
    private static final int TRUNG_MABU_ID = 568;           // Trứng Mabư
    private static final int DA_NGU_SAC_ID = 674;           // Đá ngũ sắc
    private static final int TRUNG_DE_KID_BEERUS_ID = 1764; // Trứng đệ Kid Beerus (kết quả)

    // Số lượng cần thiết
    private static final int REQUIRED_BINH_HUT_NANG_LUONG = 3000;
    private static final int REQUIRED_TRUNG_MABU = 30;
    private static final int REQUIRED_DA_NGU_SAC = 50;

    // Tỉ lệ thành công
    private static final float SUCCESS_RATE = 50.0f;

    public static void showInfoCombine(Player player) {
        if (player.combineNew.itemsCombine.size() != 3) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Cần đúng 3 loại vật phẩm:\n- 3000 Bình hút năng lượng\n- 30 Trứng Mabư\n- 50 Đá ngũ sắc", "Đóng");
            return;
        }

        Item binhHutNangLuong = null;
        Item trungMabu = null;
        Item daNguSac = null;

        for (Item item : player.combineNew.itemsCombine) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case BINH_HUT_NANG_LUONG_ID ->
                        binhHutNangLuong = item;
                    case TRUNG_MABU_ID ->
                        trungMabu = item;
                    case DA_NGU_SAC_ID ->
                        daNguSac = item;
                    default -> {
                    }
                }
            }
        }

        if (binhHutNangLuong == null || trungMabu == null || daNguSac == null) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Thiếu vật phẩm cần thiết:\n- 3000 Bình hút năng lượng\n- 30 Trứng Mabư\n- 50 Đá ngũ sắc", "Đóng");
            return;
        }

        String errorMsg = "";
        if (binhHutNangLuong.quantity < REQUIRED_BINH_HUT_NANG_LUONG) {
            errorMsg += "Còn thiếu " + (REQUIRED_BINH_HUT_NANG_LUONG - binhHutNangLuong.quantity) + " Bình hút năng lượng\n";
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

        // Hiển thị thông tin nâng cấp
        String infoMsg = "|2|Nâng cấp đệ tử Kid Beerus\n|0|"
                + "Cần:\n"
                + "|1|~ " + REQUIRED_BINH_HUT_NANG_LUONG + " Bình hút năng lượng\n"
                + "|1|~ " + REQUIRED_TRUNG_MABU + " Trứng Mabư\n"
                + "|1|~ " + REQUIRED_DA_NGU_SAC + " Đá ngũ sắc\n"
                + "|7|Tỉ lệ thành công: " + SUCCESS_RATE + "%\n"
                + "|2|Kết quả: Trứng đệ Kid Beerus";

        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                infoMsg, "Nâng cấp", "Từ chối");
    }

    public static void nangDeKidBeerus(Player player) {
        if (player.combineNew.itemsCombine.size() != 3) {
            return;
        }

        Item binhHutNangLuong = null;
        Item trungMabu = null;
        Item daNguSac = null;

        for (Item item : player.combineNew.itemsCombine) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case BINH_HUT_NANG_LUONG_ID ->
                        binhHutNangLuong = item;
                    case TRUNG_MABU_ID ->
                        trungMabu = item;
                    case DA_NGU_SAC_ID ->
                        daNguSac = item;
                    default -> {
                    }
                }
            }
        }

        if (binhHutNangLuong == null || trungMabu == null || daNguSac == null
                || binhHutNangLuong.quantity < REQUIRED_BINH_HUT_NANG_LUONG
                || trungMabu.quantity < REQUIRED_TRUNG_MABU
                || daNguSac.quantity < REQUIRED_DA_NGU_SAC) {
            Service.gI().sendThongBao(player, "Không đủ vật phẩm để thực hiện nâng cấp");
            return;
        }

        if (Util.isTrue(SUCCESS_RATE, 100)) {

            InventoryService.gI().subQuantityItemsBag(player, binhHutNangLuong, REQUIRED_BINH_HUT_NANG_LUONG);
            InventoryService.gI().subQuantityItemsBag(player, trungMabu, REQUIRED_TRUNG_MABU);
            InventoryService.gI().subQuantityItemsBag(player, daNguSac, REQUIRED_DA_NGU_SAC);

            Item trungDeKidBeerus = ItemService.gI().createNewItem((short) TRUNG_DE_KID_BEERUS_ID, 1);
            InventoryService.gI().addItemBag(player, trungDeKidBeerus);

            CombineService.gI().sendEffectSuccessCombine(player);
            Service.gI().sendThongBao(player, "Chúc mừng! Bạn đã nâng cấp thành công và nhận được Trứng đệ Kid Beerus");
        } else {
            InventoryService.gI().subQuantityItemsBag(player, binhHutNangLuong, REQUIRED_BINH_HUT_NANG_LUONG);
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
