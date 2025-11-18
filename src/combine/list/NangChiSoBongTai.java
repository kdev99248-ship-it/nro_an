package combine.list;

/*
 * @Author: NgocRongWhis (rewritten)
 * @Description: Mở chỉ số Bông Tai Porata cấp 2/3/4
 *  - ID: 921 (+2), 1165 (+3), 1121 (+4)
 *  - Nguyên liệu: x99 Hồn bông tai (934) + x1 Đá xanh lam (935) + 1000 ngọc
 *  - Tỉ lệ: +2=70%, +3=60%, +4=50%
 *  - Giới hạn %: +2 ≤ 15, +3 ≤ 25, +4 ≤ 35
 */

import consts.ConstNpc;
import item.Item;
import combine.CombineService;
import player.Player;
import services.InventoryService;
import services.Service;
import utils.Util;

public class NangChiSoBongTai {

    // IDs
    private static final int ID_BT_2 = 921;
    private static final int ID_BT_3 = 1165;
    private static final int ID_BT_4 = 1121;
    private static final int ID_HON   = 934;  // Mảnh hồn bông tai
    private static final int ID_DAXL  = 935;  // Đá xanh lam

    // Chi phí & yêu cầu cố định
    private static final int GEM_COST = 1_000;
    private static final int HON_NEED = 99;
    private static final int DAXL_NEED = 1;

    // Tỉ lệ theo cấp
    private static int ratioFor(int itemId) {
        if (itemId == ID_BT_2) return 50;
        if (itemId == ID_BT_3) return 40;
        if (itemId == ID_BT_4) return 30;
        return 0;
    }

    // Khung % ngẫu nhiên theo cấp
    private static int[] percentRangeFor(int itemId) {
        if (itemId == ID_BT_2) return new int[]{5, 15};   // max 15%
        if (itemId == ID_BT_3) return new int[]{10, 25};  // max 25%
        if (itemId == ID_BT_4) return new int[]{20, 35};  // max 35%
        return new int[]{0, 0};
    }

    private static String levelTitle(int itemId) {
        if (itemId == ID_BT_2) return "Bông tai Porata [+2]";
        if (itemId == ID_BT_3) return "Bông tai Porata [+3]";
        if (itemId == ID_BT_4) return "Bông tai Porata [+4]";
        return "Bông tai Porata";
    }

    public static void showInfoCombine(Player player) {
        if (player == null || player.combineNew.itemsCombine.size() != 3) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Cần 1 Bông tai Porata cấp 2/3/4, x99 Mảnh hồn bông tai và x1 Đá xanh lam", "Đóng");
            return;
        }

        Item bongTai = null, hon = null, da = null;
        for (Item it : player.combineNew.itemsCombine) {
            if (!it.isNotNullItem()) continue;
            int id = it.template.id;
            if (id == ID_BT_2 || id == ID_BT_3 || id == ID_BT_4) bongTai = it;
            else if (id == ID_HON) hon = it;
            else if (id == ID_DAXL) da = it;
        }

        if (bongTai == null || hon == null || da == null) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Cần 1 Bông tai Porata cấp 2/3/4, x99 Mảnh hồn bông tai và x1 Đá xanh lam", "Đóng");
            return;
        }

        final int id = bongTai.template.id;
        final int ratio = ratioFor(id);
        if (ratio <= 0) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Bông tai không hợp lệ để mở chỉ số", "Đóng");
            return;
        }

        // set cost/ratio vào combineNew cho client hiển thị
        player.combineNew.gemCombine = GEM_COST;
        player.combineNew.goldCombine = 0; // không dùng vàng
        player.combineNew.ratioCombine = ratio;

        String title = levelTitle(id);
        int[] range = percentRangeFor(id);

        StringBuilder npcSay = new StringBuilder();
        npcSay.append("|2|").append(title).append("\n\n");
        npcSay.append("|2|Tỉ lệ thành công: ").append(ratio).append("%\n");
        npcSay.append("|2|Giới hạn chỉ số: ").append(range[1]).append("%\n");
        npcSay.append("|2|Cần ").append(HON_NEED).append(" ").append(hon.template.name).append("\n");
        npcSay.append("|2|Cần ").append(DAXL_NEED).append(" ").append(da.template.name).append("\n");
        npcSay.append("|2|Cần: ").append(player.combineNew.gemCombine).append(" ngọc\n");
        npcSay.append("|1|+1 Chỉ số ngẫu nhiên");

        // Kiểm tra nguyên liệu/ngọc
        if (hon.quantity < HON_NEED) {
            npcSay.append("\n|7|Còn thiếu ").append(HON_NEED - hon.quantity).append(" ").append(hon.template.name);
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay.toString(), "Đóng");
            return;
        }
        if (da.quantity < DAXL_NEED) {
            npcSay.append("\n|7|Còn thiếu ").append(DAXL_NEED - da.quantity).append(" ").append(da.template.name);
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay.toString(), "Đóng");
            return;
        }

        if (player.inventory.gem >= player.combineNew.gemCombine) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay.toString(),
                    "Nâng cấp\n" + player.combineNew.gemCombine + " ngọc", "Từ chối");
        } else {
            npcSay.append("\n|7|Thiếu ").append(player.combineNew.gemCombine - player.inventory.gem).append(" ngọc xanh");
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay.toString(), "Đóng");
        }
    }

    public static void nangChiSoBongTai(Player player) {
        try {
            if (player == null) return;

            // Kiểm tra ngọc
            if (player.inventory.gem < player.combineNew.gemCombine) {
                Service.gI().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu "
                        + Util.powerToString(player.combineNew.gemCombine - player.inventory.gem) + " ngọc nữa!");
                return;
            }

            Item bongTai = null, hon = null, da = null;
            for (Item it : player.combineNew.itemsCombine) {
                if (!it.isNotNullItem()) continue;
                int id = it.template.id;
                if (id == ID_BT_2 || id == ID_BT_3 || id == ID_BT_4) bongTai = it;
                else if (id == ID_HON) hon = it;
                else if (id == ID_DAXL) da = it;
            }
            if (bongTai == null || hon == null || da == null) {
                Service.gI().sendThongBao(player, "Thiếu vật phẩm để mở chỉ số");
                return;
            }
            if (hon.quantity < HON_NEED || da.quantity < DAXL_NEED) {
                Service.gI().sendThongBao(player, "Thiếu nguyên liệu");
                return;
            }

            final int id = bongTai.template.id;
            final int ratio = ratioFor(id);
            final int[] range = percentRangeFor(id);
            if (ratio <= 0 || range[0] == 0 && range[1] == 0) {
                Service.gI().sendThongBao(player, "Bông tai không hợp lệ để mở chỉ số");
                return;
            }

            // Trừ ngọc trước
            player.inventory.gem -= player.combineNew.gemCombine;
            Service.gI().sendMoney(player);

            boolean success = Util.isTrue(ratio, 100);
            if (success) {
                // Danh sách option được random
                byte[] optPool = {77, 80, 81, 103, 50, 94, 14};
                byte optId = optPool[Util.nextInt(0, optPool.length - 1)];

                // Random % theo cấp (đảm bảo không vượt max)
                int param = Util.nextInt(range[0], range[1]);

                // Xoá option cũ & set lại
                bongTai.itemOptions.clear();

                // Gắn mốc cấp (72)
                int level = (id == ID_BT_2 ? 2 : id == ID_BT_3 ? 3 : 4);
                bongTai.itemOptions.add(new Item.ItemOption(72, level));

                // Gắn chỉ số ngẫu nhiên
                bongTai.itemOptions.add(new Item.ItemOption(optId, (short) param));

                // Khoá (nếu đang dùng 38 làm nhãn/khóa hiển thị)
                bongTai.itemOptions.add(new Item.ItemOption(38, 0));

                CombineService.gI().sendEffectSuccessCombine(player);
            } else {
                CombineService.gI().sendEffectFailCombine(player);
            }

            // Trừ nguyên liệu
            InventoryService.gI().subQuantityItemsBag(player, hon, HON_NEED);
            InventoryService.gI().subQuantityItemsBag(player, da, DAXL_NEED);

            // Cập nhật túi + mở lại giao diện
            InventoryService.gI().sendItemBags(player);
            CombineService.gI().reOpenItemCombine(player);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
