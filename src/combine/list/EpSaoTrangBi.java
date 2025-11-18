package combine.list;

/*
 * @Author: NgojcDev
 */

import combine.CombineService;
import combine.CombineSystem;
import consts.ConstNpc;
import item.Item;
import item.Item.ItemOption;
import player.Player;
import services.InventoryService;
import services.Service;
import services.ItemService;

public class EpSaoTrangBi {

    // ---------------- HELPER CHECK ----------------
    private static boolean isSaoPhaLeCap2(Item item) {
        return item != null && item.template.id >= 1416 && item.template.id <= 1422;
    }

    private static boolean isNgocRong(Item item) {
        return item != null && item.template.id >= 14 && item.template.id <= 20;
    }

    private static int getStar(Item trangBi) {
        for (ItemOption io : trangBi.itemOptions) {
            if (io.optionTemplate.id == 102) {
                return io.param;
            }
        }
        return 0;
    }

    private static int getStarEmpty(Item trangBi) {
        for (ItemOption io : trangBi.itemOptions) {
            if (io.optionTemplate.id == 107) {
                return io.param;
            }
        }
        return 0;
    }

    private static ItemOption getStarOption(Item trangBi) {
        for (ItemOption io : trangBi.itemOptions) {
            if (io.optionTemplate.id == 102) {
                return io;
            }
        }
        return null;
    }

    private static boolean isValidItemForStar(int saoSeEp, Item daPhaLe) {
        if (saoSeEp < 8) {
            return (CombineSystem.isDaPhaLe(daPhaLe) && !isNgocRong(daPhaLe) && !isSaoPhaLeCap2(daPhaLe)) || isNgocRong(daPhaLe);
        }
        return isSaoPhaLeCap2(daPhaLe) || isNgocRong(daPhaLe);
    }

    private static boolean hasRequiredEnhancement(Item trangBi, int saoSeEp) {
        for (ItemOption io : trangBi.itemOptions) {
            if (io.optionTemplate.id == 228 && io.param >= saoSeEp) {
                return true;
            }
        }
        return false;
    }

    private static Item[] getItems(Player player) {
        Item trangBi = null, daPhaLe = null;
        for (Item item : player.combineNew.itemsCombine) {
            if (CombineSystem.isTrangBiPhaLeHoa(item)) {
                trangBi = item;
            } else if (CombineSystem.isDaPhaLe(item) || isSaoPhaLeCap2(item) || isNgocRong(item)) {
                daPhaLe = item;
            }
        }
        return new Item[]{trangBi, daPhaLe};
    }

    // ---------------- SHOW INFO ----------------
    public static void showInfoCombine(Player player) {
        if (GhepRuongSKHVip.checkValidCoundBag(player)) {
            return;
        }
        if (player.combineNew.itemsCombine.size() != 2) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Hãy đặt trang bị và nguyên liệu vào", "Đóng");
            return;
        }
        Item[] items = getItems(player);
        Item trangBi = items[0], daPhaLe = items[1];
        if (trangBi == null || daPhaLe == null) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Hãy đặt trang bị và nguyên liệu vào", "Đóng");
            return;
        }

        int star = getStar(trangBi);
        int starEmpty = getStarEmpty(trangBi);

        if (starEmpty > 9) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Trang bị đã đạt tối đa sao", "Đóng");
            return;
        }

        int saoSeEp = star + 1;
        if (!isValidItemForStar(saoSeEp, daPhaLe)) {
            String message = saoSeEp < 8
                    ? "Sao " + saoSeEp + " chỉ có thể dùng đá pha lê thông thường hoặc ngọc rồng!"
                    : "Sao " + saoSeEp + " cần dùng sao pha lê cấp 2 hoặc ngọc rồng!";
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, message, "Đóng");
            return;
        }

        if (saoSeEp >= 8 && !hasRequiredEnhancement(trangBi, saoSeEp)) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Cần cường hóa lỗ sao pha lê thứ " + saoSeEp + " trước khi ép vào", "Đóng");
            return;
        }

        player.combineNew.gemCombine = CombineSystem.getGemEpSao(star);

        StringBuilder npcSay = new StringBuilder(trangBi.template.name + "\n|2|");
        for (ItemOption io : trangBi.itemOptions) {
            if (io.optionTemplate.id != 102) {
                npcSay.append(io.getOptionString()).append("\n");
            }
        }

        if (isSaoPhaLeCap2(daPhaLe) || daPhaLe.template.type == 30) {
            for (ItemOption io : daPhaLe.itemOptions) {
                npcSay.append("|7|").append(io.getOptionString()).append("\n");
            }
        } else if (isNgocRong(daPhaLe)) {
            int optionId = CombineSystem.getOptionDaPhaLe(daPhaLe);
            int param = CombineSystem.getParamDaPhaLe(daPhaLe);
            npcSay.append("|7|").append(ItemService.gI().getItemOptionTemplate(optionId).name.replace("#", String.valueOf(param))).append("\n");
        }

        npcSay.append("|7|Cần ").append(player.combineNew.gemCombine).append(" ngọc");
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay.toString(),
                "Ép sao", "Từ chối");
    }

    // ---------------- DO COMBINE ----------------
    public static void epSaoTrangBi(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Hành trang cần ít nhất 1 chỗ trống");
            return;
        }

        if (player.combineNew.itemsCombine.size() != 2) {
            Service.gI().sendThongBao(player, "Cần bỏ đủ vật phẩm yêu cầu");
            return;
        }

        Item[] items = getItems(player);
        Item trangBi = items[0], daPhaLe = items[1];
        if (trangBi == null || daPhaLe == null) {
            Service.gI().sendThongBao(player, "Cần bỏ đủ vật phẩm yêu cầu");
            return;
        }

        int star = getStar(trangBi);
        int starEmpty = getStarEmpty(trangBi);
        ItemOption optionStar = getStarOption(trangBi);

        if (star >= starEmpty) {
            Service.gI().sendThongBao(player, "Trang bị đã đạt tối đa sao");
            return;
        }

        int saoSeEp = star + 1;
        if (!isValidItemForStar(saoSeEp, daPhaLe)) {
            String msg = saoSeEp < 8
                    ? "Sao " + saoSeEp + " chỉ có thể dùng đá pha lê thông thường hoặc ngọc rồng!"
                    : "Sao " + saoSeEp + " cần dùng sao pha lê cấp 2 hoặc ngọc rồng!";
            Service.gI().sendThongBao(player, msg);
            return;
        }

        if (saoSeEp >= 8 && !hasRequiredEnhancement(trangBi, saoSeEp)) {
            Service.gI().sendThongBao(player, "Cần cường hóa lỗ sao pha lê thứ " + saoSeEp + " trước khi ép vào");
            return;
        }

        int gem = CombineSystem.getGemEpSao(star);
        player.inventory.subGemAndRuby(gem);

        int optionId, param;
        if (isNgocRong(daPhaLe)) {
            optionId = CombineSystem.getOptionDaPhaLe(daPhaLe);
            param = CombineSystem.getParamDaPhaLe(daPhaLe);
        } else if (isSaoPhaLeCap2(daPhaLe) && !daPhaLe.itemOptions.isEmpty()) {
            optionId = daPhaLe.itemOptions.get(0).optionTemplate.id;
            param = daPhaLe.itemOptions.get(0).param;
        } else {
            optionId = CombineSystem.getOptionDaPhaLe(daPhaLe);
            param = CombineSystem.getParamDaPhaLe(daPhaLe);
        }

        // Thêm hoặc tăng option - Xử lý đặc biệt cho ô 8,9
        if (saoSeEp >= 8) {
            // Đối với ô 8,9: luôn tạo option mới, tách riêng khỏi các option khác
            trangBi.itemOptions.add(new ItemOption(optionId, param));
        } else {
            // Đối với ô 1-7: gộp chung option như cũ
            ItemOption option = null;
            for (ItemOption io : trangBi.itemOptions) {
                if (io.optionTemplate.id == optionId) {
                    option = io;
                    break;
                }
            }
            if (option != null) {
                option.param += param;
            } else {
                trangBi.itemOptions.add(new ItemOption(optionId, param));
            }
        }

        // Tăng số sao
        if (optionStar != null) {
            optionStar.param++;
            Service.gI().sendThongBao(player, "Đã ép sao lên " + optionStar.param + " thành công!");
        } else {
            trangBi.itemOptions.add(new ItemOption(102, 1));
            Service.gI().sendThongBao(player, "Đã ép sao lên 1 thành công!");
        }

        // Trừ nguyên liệu và cập nhật
        InventoryService.gI().subQuantityItemsBag(player, daPhaLe, 1);
        CombineService.gI().sendEffectSuccessCombine(player);
        InventoryService.gI().sendItemBags(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}
