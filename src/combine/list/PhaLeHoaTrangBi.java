package combine.list;

import consts.ConstNpc;
import item.Item;
import combine.CombineService;

import static combine.CombineService.MAX_STAR_ITEM;

import combine.CombineSystem;
import player.Player;
import server.ServerNotify;
import services.InventoryService;
import services.Service;
import utils.Util;

import java.util.Arrays;
import java.util.List;

public class PhaLeHoaTrangBi {

    private static final int MAX_STAR_SKH = 4;

    public static void showInfoCombine(Player player) {
        if (player.combineNew.itemsCombine.size() != 1) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 vật phẩm để pha lê hóa", "Đóng");
            return;
        }

        Item item = player.combineNew.itemsCombine.get(0);
        if (!CombineSystem.isTrangBiPhaLeHoa(item)) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể đục lỗ", "Đóng");
            return;
        }

        int star = 0;
        int epStar = -1; // Giá trị mặc định là chưa ép
        for (Item.ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 107) {
                star = io.param; // Lấy số sao hiện tại
            }
            if (io.optionTemplate.id == 102) {
                epStar = io.param; // Lấy số sao đã ép
            }
        }

        // Kiểm tra số sao hiện tại có vượt quá giới hạn tối đa
        if (star >= CombineService.MAX_STAR_ITEM) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm đã đạt tối đa sao pha lê", "Đóng");
            return;
        }

        // Không trừ ngọc ở đây, sẽ trừ khi thực sự đập
        processPhaLeHoa(player, item, star);
    }

    private static void processPhaLeHoa(Player player, Item item, int star) {
        player.combineNew.goldCombine = CombineSystem.getGoldPhaLeHoa(star);
        player.combineNew.gemCombine = CombineSystem.getGemPhaLeHoa(star);
        player.combineNew.ratioCombine = CombineSystem.getRatioPhaLeHoa(star);

        String npcSay = item.template.name + "\n|2|";
        for (Item.ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id != 102) {
                npcSay += io.getOptionString() + "\n";
            }
        }
        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%" + "\n";

        if (player.combineNew.goldCombine <= player.inventory.gold) {
            npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                    "Nâng cấp\ncần " + player.combineNew.gemCombine + " ngọc", "Nâng cấp\n10 lần", "Nâng cấp\n100 lần", "Nâng cấp 1000 lần");
        } else {
            npcSay += "Còn thiếu " + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold) + " vàng";
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
        }
    }

    public static void phaLeHoa(Player player, int... numm) {
        if (player.idMark != null && !Util.canDoWithTime(player.idMark.getLastTimeCombine(), 500)) {
            return;
        }
        player.idMark.setLastTimeCombine(System.currentTimeMillis());
        int n = 1;
        if (numm.length > 0) {
            n = numm[0];
        }
        if (!player.combineNew.itemsCombine.isEmpty()) {
            int gold = player.combineNew.goldCombine;
            int gem = player.combineNew.gemCombine;
            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            } else if (player.inventory.gem < gem) {
                Service.gI().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }
            int num = 0;
            int star = 0;
            boolean success = false;
            int fail = 0;
            Item item = null;
            Item.ItemOption optionStar = null;

            for (int i = 0; i < n; i++) {
                num = i;
                item = player.combineNew.itemsCombine.get(0);

                if (!CombineSystem.isTrangBiPhaLeHoa(item)) {
                    break;
                }

                star = 0;
                optionStar = null;
                for (Item.ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 107) {
                        star = io.param;
                        optionStar = io;
                        break;
                    }
                }

                if (isSHKItem(item) && star >= MAX_STAR_SKH) {
                    break;
                }

                if (star >= MAX_STAR_ITEM) {
                    break;
                }

                // Tính toán chi phí dựa trên số sao hiện tại
                player.combineNew.goldCombine = CombineSystem.getGoldPhaLeHoa(star);
                player.combineNew.gemCombine = CombineSystem.getGemPhaLeHoa(star);
                player.combineNew.ratioCombine = CombineSystem.getRatioPhaLeHoa(star);

                gold = player.combineNew.goldCombine;
                gem = player.combineNew.gemCombine;

                // Kiểm tra đủ vàng và ngọc trước khi trừ
                if (player.inventory.gem < gem || player.inventory.gold < gold) {
                    break;
                }

                // Trừ vàng và ngọc một cách an toàn
                player.inventory.gold -= gold;
                player.inventory.gem -= gem;

                int ratio = 1;
                boolean succ = true;
                if (optionStar != null) {
                    switch (optionStar.param) {
                        case 4 -> ratio *= 120 / 100;
                        case 5 -> ratio *= 120 / 100;
                        case 6 -> ratio *= 120 / 100;
                        case 7 -> ratio *= 120 / 100;
                        case 8 -> ratio *= 120 / 100;
                        case 9 -> ratio *= 120 / 100;
                    }
                }

                if (Util.isTrue(player.combineNew.ratioCombine, 100 * ratio) && succ) {
                    success = true;
                    break;
                } else {
                    fail++;
                }
            }

            if (success) {
                star++;
                if (item != null) {
                    if (optionStar == null) {
                        item.itemOptions.add(new Item.ItemOption(107, star));
                    } else {
                        optionStar.param = star;
                    }
                    if (optionStar != null && optionStar.param >= 7) {

                        ServerNotify.gI().notify("Chúc mừng " + player.name + " vừa pha lê hóa "
                                + "thành công " + item.template.name + " lên " + star + " sao pha lê");
                    }
                }
                if (n > 1 && num > 1) {
                    Service.gI().sendThongBao(player, "Pha lê hóa trang bị lên " + star + " sao thành công, sau " + num + " lần nâng cấp!");
                }
                CombineService.gI().sendEffectSuccessCombine(player);
            } else {
                CombineService.gI().sendEffectFailCombine(player);
            }
            InventoryService.gI().sendItemBags(player);
            Service.gI().sendMoney(player);
            CombineService.gI().reOpenItemCombine(player);
        }

    }

    public static List<Integer> ID_SKH = Arrays.asList(127, 128, 129, 130, 233, 131, 132, 133, 134, 135);

    private static boolean isSHKItem(Item item) {
        for (Item.ItemOption itemOption : item.itemOptions) {
            if (ID_SKH.contains(itemOption.optionTemplate.id)) {
                return true;
            }
        }
        return false;
    }
}
