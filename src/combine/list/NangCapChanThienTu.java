package combine.list;

/*
 * @Author: NgojcDev
 */
import item.Item;
import item.Item.ItemOption;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import combine.CombineService;
import consts.ConstNpc;
import utils.Util;

public class NangCapChanThienTu {

    public static void showInfoCombine(Player player) {
        Item chanThienTu = null;
        Item tinhThe = null;
        Item maQuai = null;

        for (Item item : player.combineNew.itemsCombine) {
            if (item.isNotNullItem()) {
                if (isChanThienTu(item.template.id)) {
                    chanThienTu = item;
                } else if (item.template.id == 1787) { // Tinh thể
                    tinhThe = item;
                } else if (item.template.id == 1788) { // Ma quái
                    maQuai = item;
                }
            }
        }

        if (chanThienTu != null && tinhThe != null && maQuai != null) {
            int level = getChanThienTuLevel(chanThienTu.template.id);
            int successRate = getSuccessRate(level);
            int requiredTinhThe = getRequiredTinhThe(level);
            int requiredMaQuai = getRequiredMaQuai(level);
            if (chanThienTu.template.id == 1786) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Vòng chân thiên tử đã đạt cấp độ tối đa!", "Đóng");
                return;
            }

            String errorMsg = "";
            if (tinhThe.quantity < requiredTinhThe) {
                errorMsg += "Thiếu " + (requiredTinhThe - tinhThe.quantity) + " Tinh thể\n";
            }
            if (maQuai.quantity < requiredMaQuai) {
                errorMsg += "Thiếu " + (requiredMaQuai - maQuai.quantity) + " Ma quái\n";
            }

            if (!errorMsg.isEmpty()) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Không đủ nguyên liệu!\n" + errorMsg, "Đóng");
                return;
            }

            String infoMsg = "|7|Nâng cấp " + chanThienTu.template.name + "\n|5|";
            for (ItemOption io : chanThienTu.itemOptions) {
                infoMsg += io.getOptionString() + "\n";
            }
            infoMsg += "|2|Tỉ lệ thành công: " + successRate + "%\n";
            infoMsg += "|5|Cần: " + requiredTinhThe + " Tinh thể (" + tinhThe.quantity + ")\n";
            infoMsg += "|5|Cần: " + requiredMaQuai + " Ma quái (" + maQuai.quantity + ")";

            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                    infoMsg, "Nâng cấp", "Từ chối");
        } else {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Cần có: 1 vòng chân thiên tử, Tinh thể và Ma quái", "Đóng");
        }
    }

    public static void nangCapChanThienTu(Player player) {
        Item chanThienTu = null;
        Item tinhThe = null;
        Item maQuai = null;

        for (Item item : player.combineNew.itemsCombine) {
            if (item.isNotNullItem()) {
                if (isChanThienTu(item.template.id)) {
                    chanThienTu = item;
                } else if (item.template.id == 1787) { // Tinh thể
                    tinhThe = item;
                } else if (item.template.id == 1788) { // Ma quái
                    maQuai = item;
                }
            }
        }

        if (chanThienTu == null || tinhThe == null || maQuai == null) {
            Service.gI().sendThongBao(player, "Cần có: 1 vòng chân thiên tử, Tinh thể và Ma quái");
            return;
        }

        int level = getChanThienTuLevel(chanThienTu.template.id);
        int successRate = getSuccessRate(level);
        int requiredTinhThe = getRequiredTinhThe(level);
        int requiredMaQuai = getRequiredMaQuai(level);

        // Fix: Chỉ item có ID = 1786 mới là max level
        if (chanThienTu.template.id == 1786) {
            Service.gI().sendThongBao(player, "Vòng chân thiên tử đã đạt cấp độ tối đa!");
            return;
        }

        if (tinhThe.quantity < requiredTinhThe) {
            Service.gI().sendThongBao(player, "Không đủ Tinh thể!");
            return;
        }

        if (maQuai.quantity < requiredMaQuai) {
            Service.gI().sendThongBao(player, "Không đủ Ma quái!");
            return;
        }

        // Trừ nguyên liệu
        InventoryService.gI().subQuantityItemsBag(player, tinhThe, requiredTinhThe);
        InventoryService.gI().subQuantityItemsBag(player, maQuai, requiredMaQuai);

        // Kiểm tra thành công
        boolean success = Util.isTrue(successRate, 100);

        if (success) {
            // Nâng cấp thành công
            int newId = chanThienTu.template.id + 1;
            chanThienTu.template = ItemService.gI().getTemplate((short) newId);

            // Cập nhật level option
            boolean hasLevelOption = false;
            for (ItemOption io : chanThienTu.itemOptions) {
                if (io.optionTemplate.id == 72) { // Level option
                    io.param = getChanThienTuLevel(newId);
                    hasLevelOption = true;
                    break;
                }
            }
            if (!hasLevelOption) {
                chanThienTu.itemOptions.add(new ItemOption(72, getChanThienTuLevel(newId)));
            }

            // Thêm random option
            int[] randomOptions = {67, 68, 69}; // Chỉ số tăng thêm
            int randomOption = randomOptions[Util.nextInt(0, randomOptions.length - 1)];

            boolean hasRandomOption = false;
            for (ItemOption io : chanThienTu.itemOptions) {
                if (io.optionTemplate.id == randomOption) {
                    io.param += Util.nextInt(1, 5);
                    hasRandomOption = true;
                    break;
                }
            }
            if (!hasRandomOption) {
                chanThienTu.itemOptions.add(new ItemOption(randomOption, Util.nextInt(3, 8)));
            }

            CombineService.gI().sendEffectSuccessCombine(player);
        } else {
            CombineService.gI().sendEffectFailCombine(player);
        }

        InventoryService.gI().sendItemBags(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }

    private static boolean isChanThienTu(int id) {
        return id >= 1780 && id <= 1786;
    }

    private static int getChanThienTuLevel(int id) {
        return switch (id) {
            case 1780 -> 1; // Chân thiên tử tân thủ
            case 1781 -> 2; // Chân thiên tử tập sự  
            case 1782 -> 3; // Chân thiên tử tân binh
            case 1783 -> 4; // Chân thiên tử chiến binh
            case 1784 -> 5; // Chân thiên tử vệ binh
            case 1785 -> 6; // Chân thiên tử siêu cấp
            case 1786 -> 7; // Chân thiên tử siêu vip
            default -> 0;
        };
    }

    private static int getSuccessRate(int level) {
        return switch (level) {
            case 1 -> 80; // Tân thủ -> Tập sự
            case 2 -> 70; // Tập sự -> Tân binh
            case 3 -> 60; // Tân binh -> Chiến binh
            case 4 -> 50; // Chiến binh -> Vệ binh
            case 5 -> 40; // Vệ binh -> Siêu cấp
            case 6 -> 30; // Siêu cấp -> Siêu vip
            default -> 0;
        };
    }

    private static int getRequiredTinhThe(int level) {
        return switch (level) {
            case 1 -> 3;  // Tân thủ -> Tập sự
            case 2 -> 5;  // Tập sự -> Tân binh  
            case 3 -> 7;  // Tân binh -> Chiến binh
            case 4 -> 10; // Chiến binh -> Vệ binh
            case 5 -> 15; // Vệ binh -> Siêu cấp
            case 6 -> 20; // Siêu cấp -> Siêu vip
            default -> 0;
        };
    }

    private static int getRequiredMaQuai(int level) {
        return switch (level) {
            case 1 -> 3;  // Tân thủ -> Tập sự
            case 2 -> 5;  // Tập sự -> Tân binh
            case 3 -> 7;  // Tân binh -> Chiến binh  
            case 4 -> 10; // Chiến binh -> Vệ binh
            case 5 -> 15; // Vệ binh -> Siêu cấp
            case 6 -> 20; // Siêu cấp -> Siêu vip
            default -> 0;
        };
    }
}