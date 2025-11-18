package combine.list;

/*
 * @Author: NgocRongWhis (rewritten by ChatGPT)
 * @Description: Nâng cấp Bông Tai Porata +1 -> +4
 *   - 454 (+1) -> 921 (+2)
 *   - 921 (+2) -> 1165 (+3)
 *   - 1165 (+3) -> 1121 (+4)
 *
 * Ghi chú:
 * - Không còn dùng getParam(player, 31, itemId) để tính số lượng mảnh.
 *   Mọi kiểm tra / trừ mảnh dựa trên số lượng vật phẩm thực tế (manhVo.quantity) đang đặt vào khay ghép.
 */

import consts.ConstNpc;
import item.Item;
import combine.CombineService;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

public class NangCapBongTai {

    // ====== CẤU HÌNH CƠ BẢN ======
    private static final int GOLD_BONG_TAI_BASE = 200_000_000; // base cho +1 -> +2
    private static final int GEM_BONG_TAI_BASE  = 1_000;       // base cho +1 -> +2
    private static final int RATIO_BASE         = 50;          // tỉ lệ base cho +1 -> +2

    // ID vật phẩm
    private static final int ID_BT_1 = 454;   // +1
    private static final int ID_BT_2 = 921;   // +2
    private static final int ID_BT_3 = 1165;  // +3
    private static final int ID_BT_4 = 1121;  // +4 (tối đa)
    private static final int ID_MANH = 933;   // Mảnh vỡ bông tai

    // ====== MÔ TẢ TỪNG BẬC ======
    private static class Stage {
        final int currentId;          // id bông tai hiện tại
        final int targetId;           // id bông tai mục tiêu
        final int targetLevel;        // level option 72
        final int reqShards;          // mảnh cần khi thành công
        final int failShardPenalty;   // trừ mảnh khi thất bại
        final long goldCost;          // vàng cần
        final int gemCost;            // ngọc cần
        final int successRatio;       // tỉ lệ %

        Stage(int currentId, int targetId, int targetLevel,
              int reqShards, int failShardPenalty,
              long goldCost, int gemCost, int successRatio) {
            this.currentId = currentId;
            this.targetId = targetId;
            this.targetLevel = targetLevel;
            this.reqShards = reqShards;
            this.failShardPenalty = failShardPenalty;
            this.goldCost = goldCost;
            this.gemCost = gemCost;
            this.successRatio = successRatio;
        }
    }

    private static Stage getStageByCurrentId(int itemId) {
        // +1 -> +2
        if (itemId == ID_BT_1) {
            return new Stage(
                    ID_BT_1, ID_BT_2, 2,
                    9_999, 999,
                    GOLD_BONG_TAI_BASE,
                    GEM_BONG_TAI_BASE,
                    RATIO_BASE
            );
        }
        // +2 -> +3 (cost x2, ratio -10)
        if (itemId == ID_BT_2) {
            return new Stage(
                    ID_BT_2, ID_BT_3, 3,
                    29_999, 2_999,
                    GOLD_BONG_TAI_BASE * 2L,
                    GEM_BONG_TAI_BASE * 2,
                    RATIO_BASE - 10
            );
        }
        // +3 -> +4 (cost x4, ratio -20)
        if (itemId == ID_BT_3) {
            return new Stage(
                    ID_BT_3, ID_BT_4, 4,
                    99_999, 9_999,
                    GOLD_BONG_TAI_BASE * 4L,
                    GEM_BONG_TAI_BASE * 4,
                    RATIO_BASE - 20
            );
        }
        return null;
    }

    private static Item findBongTaiInCombine(Player player) {
        Item bt = null;
        for (Item it : player.combineNew.itemsCombine) {
            int id = it.template.id;
            if (id == ID_BT_1 || id == ID_BT_2 || id == ID_BT_3 || id == ID_BT_4) {
                bt = it;
            }
        }
        return bt;
    }

    private static Item findManhVoInCombine(Player player) {
        for (Item it : player.combineNew.itemsCombine) {
            if (it.template.id == ID_MANH) return it;
        }
        return null;
    }

    private static String buildNpcInfo(Stage s, Item manhVo, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("|2|").append(title).append("\n\n");
        sb.append("|2|Tỉ lệ thành công: ").append(s.successRatio).append("%\n");

        int have = (manhVo != null ? manhVo.quantity : 0);
        sb.append("|2|Cần ").append(Util.numberToMoney(s.reqShards)).append(" ")
          .append(ItemService.gI().getTemplate(ID_MANH).name).append("\n");
        sb.append("|2|Cần: ").append(Util.numberToMoney(s.gemCost)).append(" ngọc\n");
        sb.append("|2|Cần: ").append(Util.numberToMoney(s.goldCost)).append(" vàng\n");
        sb.append("|7|Thất bại -").append(Util.numberToMoney(s.failShardPenalty)).append(" ")
          .append(ItemService.gI().getTemplate(ID_MANH).name).append("\n");

        if (have < s.reqShards) {
            sb.append("Còn thiếu ").append(Util.numberToMoney(s.reqShards - have)).append(" ")
              .append(ItemService.gI().getTemplate(ID_MANH).name);
        }
        return sb.toString();
    }

    // ================== HIỂN THỊ MENU ==================
    public static void showInfoCombine(Player player) {
        if (player == null || player.combineNew.itemsCombine.size() != 2) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Cần 1 Bông tai Porata và Mảnh vỡ bông tai", "Đóng");
            return;
        }

        Item bongTai = findBongTaiInCombine(player);
        Item manhVo  = findManhVoInCombine(player);

        if (bongTai == null || manhVo == null) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Cần 1 Bông tai Porata và Mảnh vỡ bông tai", "Đóng");
            return;
        }

        // Bông tai đã tối đa
        if (bongTai.template.id == ID_BT_4) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "|2|Bông tai Porata [+4]\nĐã tối đa. Không thể nâng cấp thêm.", "Đóng");
            return;
        }

        Stage s = getStageByCurrentId(bongTai.template.id);
        if (s == null) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Bông tai không hợp lệ để nâng cấp", "Đóng");
            return;
        }

        // Thiết lập chi phí/tỉ lệ vào combineNew (để client show)
        player.combineNew.goldCombine = (int) Math.min(Integer.MAX_VALUE, s.goldCost);
        player.combineNew.gemCombine  = s.gemCost;
        player.combineNew.ratioCombine = s.successRatio;

        String title = switch (s.targetLevel) {
            case 2 -> "Bông tai Porata [+2]";
            case 3 -> "Bông tai Porata [+3]";
            case 4 -> "Bông tai Porata [+4]";
            default -> "Bông tai Porata";
        };

        String npcSay = buildNpcInfo(s, manhVo, title);
        int haveShards = (manhVo != null ? manhVo.quantity : 0);

        // Điều kiện mở nút "Nâng cấp"
        if (haveShards >= s.reqShards
                && player.inventory.gem >= player.combineNew.gemCombine
                && player.inventory.gold >= player.combineNew.goldCombine) {
            // Cho phép nâng cấp
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                    "Nâng cấp\n" + Util.numberToMoney(player.combineNew.goldCombine) + " vàng\n"
                            + Util.numberToMoney(player.combineNew.gemCombine) + " ngọc\n",
                    "Từ chối");
        } else {
            // Thông báo thiếu gì
            StringBuilder msg = new StringBuilder(npcSay);
            if (haveShards < s.reqShards) {
                // đã ghi "Còn thiếu ..." bên trên
            } else if (player.inventory.gem < player.combineNew.gemCombine) {
                msg.append("\n|7|Thiếu ").append(Util.numberToMoney(player.combineNew.gemCombine - player.inventory.gem))
                        .append(" ngọc xanh");
            } else if (player.inventory.gold < player.combineNew.goldCombine) {
                msg.append("\n|7|Thiếu ").append(Util.powerToString(player.combineNew.goldCombine - player.inventory.gold))
                        .append(" vàng");
            }
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, msg.toString(), "Đóng");
        }
    }

    // ================== THỰC THI NÂNG CẤP ==================
    public static void nangCapBongTai(Player player) {
        if (player == null || player.combineNew.itemsCombine.size() != 2) {
            return;
        }

        // Kiểm tra chi phí đã set ở showInfo
        final int gold = player.combineNew.goldCombine;
        final int gem  = player.combineNew.gemCombine;

        if (player.inventory.gold < gold) {
            Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
            return;
        }
        if (player.inventory.gem < gem) {
            Service.gI().sendThongBao(player, "Không đủ ngọc để thực hiện");
            return;
        }

        Item bongTai = findBongTaiInCombine(player);
        Item manhVo  = findManhVoInCombine(player);
        if (bongTai == null || manhVo == null) {
            Service.gI().sendThongBao(player, "Thiếu vật phẩm cần thiết");
            return;
        }

        // Đã tối đa thì thôi
        if (bongTai.template.id == ID_BT_4) {
            Service.gI().sendThongBao(player, "Bông tai đã đạt cấp tối đa");
            return;
        }

        Stage s = getStageByCurrentId(bongTai.template.id);
        if (s == null) {
            Service.gI().sendThongBao(player, "Bông tai không hợp lệ để nâng cấp");
            return;
        }

        // Kiểm tra đủ mảnh dựa trên số lượng thực tế đặt vào khay ghép
        int shardTotal = manhVo.quantity;
        if (shardTotal < s.reqShards) {
            Service.gI().sendThongBao(player, "Không đủ mảnh vỡ bông tai");
            return;
        }

        // Không cho nâng nếu trong túi đã có cấp mục tiêu
        Item existTarget = InventoryService.gI().findItemBag(player, s.targetId);
        if (existTarget != null) {
            Service.gI().sendThongBao(player, "Ngươi đã có bông tai Porata cấp " + s.targetLevel + " trong hành trang, không thể nâng cấp nữa.");
            return;
        }

        // Trừ chi phí
        player.inventory.gold -= gold;
        player.inventory.gem  -= gem;

        boolean success = Util.isTrue(player.combineNew.ratioCombine, 100);
        if (success) {
            // Thành công: đổi template + set option 72 đúng cấp, trừ đủ mảnh yêu cầu
            bongTai.template = ItemService.gI().getTemplate(s.targetId);
            bongTai.itemOptions.clear();
            bongTai.itemOptions.add(new Item.ItemOption(72, s.targetLevel));
            CombineService.gI().sendEffectSuccessCombine(player);

            // Trừ mảnh theo yêu cầu (từ chính stack đang đặt)
            InventoryService.gI().subQuantityItemsBag(player, manhVo, s.reqShards);
        } else {
            // Thất bại: trừ mảnh phạt từ stack đang đặt
            CombineService.gI().sendEffectFailCombine(player);
            int penalty = Math.min(s.failShardPenalty, shardTotal); // tránh âm
            InventoryService.gI().subQuantityItemsBag(player, manhVo, penalty);
        }

        // Đồng bộ túi và tiền
        InventoryService.gI().sendItemBags(player);
        Service.gI().sendMoney(player);

        // Mở lại giao diện ghép
        CombineService.gI().reOpenItemCombine(player);
    }
}
