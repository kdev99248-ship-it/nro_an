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
import utils.Util;

public class CuongHoaLoSao {

    private static final int COST = 500_000_000;

    public static void showInfoCombine(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
            return;
        }

        if (player.combineNew.itemsCombine.size() != 3) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Cần bỏ đủ vật phẩm yêu cầu", "Đóng");
            return;
        }

        Item item = null, Hematite = null, DuiDuc = null;
        for (Item i : player.combineNew.itemsCombine) {
            if (CombineSystem.isTrangBiPhaLeHoa(i)) {
                item = i;
            } else if (i.template.id == 1423) {
                Hematite = i;
            } else if (i.template.id == 1438) {
                DuiDuc = i;
            }
        }

        if (item == null || Hematite == null || DuiDuc == null
                || Hematite.quantity < 1 || DuiDuc.quantity < 1) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Bạn chưa bỏ đủ vật phẩm !!!", "Đóng");
            return;
        }

        int starEmpty = 0;
        ItemOption optionStar = null;
        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 107) {
                starEmpty = io.param;
            } else if (io.optionTemplate.id == 228) {
                optionStar = io;
            }
        }

        String npcSay = item.template.name + "\n|2|";
        for (ItemOption io : Hematite.itemOptions) {
            npcSay += io.getOptionString() + "\n";
        }

        if ((starEmpty == 8 || starEmpty == 9) && (optionStar == null || optionStar.param < 8)) {
            npcSay += "Cường hóa\n Ô sao pha lê thứ 8\n" + item.template.name
                    + "\n|7| Cần 1 " + Hematite.template.name
                    + "\n|7| Cần 1 " + DuiDuc.template.name
                    + "\nCần " + Util.numberToMoney(COST) + " vàng";
        } else if (starEmpty == 9 && optionStar != null && optionStar.param == 8) {
            npcSay += "Cường hóa\n Ô sao pha lê thứ 9\n" + item.template.name
                    + "\n|7| Cần 1 " + Hematite.template.name
                    + "\n|7| Cần 1 " + DuiDuc.template.name
                    + "\nCần " + Util.numberToMoney(COST) + " vàng";
        } else if (optionStar != null && optionStar.param >= 9) {
            npcSay += "Trang bị đã đạt tối đa sao pha lê, không thể cường hóa thêm";
        } else {
            npcSay += "Cường hóa không hợp lệ. Trang bị cần có 8 hoặc 9 lỗ sao pha lê để cường hóa";
        }

        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                npcSay, "Cường Hóa", "Từ chối");
    }

    public static void cuongHoaLoSao(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Hành trang cần ít nhất 1 chỗ trống");
            return;
        }

        if (player.combineNew.itemsCombine.size() != 3) {
            Service.gI().sendThongBao(player, "Cần bỏ đủ vật phẩm yêu cầu");
            return;
        }

        Item item = null, Hematite = null, DuiDuc = null;
        for (Item i : player.combineNew.itemsCombine) {
            if (CombineSystem.isTrangBiPhaLeHoa(i)) {
                item = i;
            } else if (i.template.id == 1423) {
                Hematite = i;
            } else if (i.template.id == 1438) {
                DuiDuc = i;
            }
        }

        if (item == null || Hematite == null || DuiDuc == null
                || Hematite.quantity < 1 || DuiDuc.quantity < 1) {
            Service.gI().sendThongBao(player, "Vật phẩm hoặc nguyên liệu không hợp lệ!");
            return;
        }

        int starEmpty = 0;
        ItemOption optionStar = null;
        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 107) {
                starEmpty = io.param;
            } else if (io.optionTemplate.id == 228) {
                optionStar = io;
            }
        }
        
        // KIỂM TRA ĐỦ VÀNG TRƯỚC KHI THỰC HIỆN
        if (player.inventory.gold < COST) {
            Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện (Cần: " + Util.numberToMoney(COST) + ")");
            return;
        }

        if ((starEmpty == 8 || starEmpty == 9) && (optionStar == null || optionStar.param < 8)) {
            if (optionStar == null) {
                item.itemOptions.add(new ItemOption(218, 0));
                item.itemOptions.add(new ItemOption(228, 8));
            } else {
                optionStar.param = 8;
            }
            player.inventory.gold -= COST;
            CombineService.gI().sendEffectSuccessCombine(player);
            Service.gI().sendThongBao(player, "Cường hóa lỗ sao pha lê thứ 8 thành công!");

        } else if (starEmpty == 9 && optionStar != null && optionStar.param == 8) {
            optionStar.param = 9;
            player.inventory.gold -= COST;
            CombineService.gI().sendEffectSuccessCombine(player);
            Service.gI().sendThongBao(player, "Cường hóa lỗ sao pha lê thứ 9 thành công!");

        } else if (optionStar != null && optionStar.param >= 9) {
            Service.gI().sendThongBao(player, "Trang bị của bạn đã đạt tối đa sao pha lê, không thể cường hóa thêm.");
            return;
        } else {
            Service.gI().sendThongBao(player, "Cường hóa không hợp lệ. Trang bị cần có 8 hoặc 9 lỗ sao pha lê để cường hóa.");
            return;
        }

        // Trừ nguyên liệu
        InventoryService.gI().subQuantityItemsBag(player, Hematite, 1);
        InventoryService.gI().subQuantityItemsBag(player, DuiDuc, 1);

        // Cập nhật giao diện
        Service.gI().sendMoney(player);
        InventoryService.gI().sendItemBags(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}
