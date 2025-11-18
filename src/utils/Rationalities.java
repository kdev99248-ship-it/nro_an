package utils;

import combine.CombineService;
import consts.ConstNpc;
import consts.ConstPlayer;
import item.Item;
import npc.NpcFactory;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.Service;

public class Rationalities {
    private static final int[][] ITEM_OPTION_CS_SD = new int[][]{
            {1200, 1600}, {2400, 3200}, {5000, 9000}
    };
    public static int[] RATIO_DROP_GOLD_KEY = new int[]{1, 1_000_000};

    public static int[][] ITEM_ID_DROP_IN_VIP_CHEST_TD = new int[][]{
            {232, 244, 256, 268, 280}, {233, 245, 257, 269, 281}, {555, 556, 561, 562, 563}
    };
    public static int[][] ITEM_ID_DROP_IN_VIP_CHEST_NM = new int[][]{
            {260, 236, 248, 272, 280}, {237, 249, 261, 273, 281}, {555, 556, 561, 562, 563}
    };

    public static int[][] ITEM_ID_DROP_IN_VIP_CHEST_XD = new int[][]{
            {238, 250, 262, 274, 280}, {241, 253, 265, 277, 281}, {555, 556, 561, 562, 563}
    };
    public static int[][] RATIO_ITEM_DROP_IN_VIP_CHEST = new int[][]{
            {100, 100}, {20, 100}, {5, 100}
    };
    public static int[][] SKH_TD_ID = new int[][]{
            {127, 100, 100}, {128, 50, 100}, {129, 5, 100}
    };
    public static int[][] SKH_NM_ID = new int[][]{
            {130, 100, 100}, {233, 5, 100}, {132, 50, 100}
    };
    public static int[][] SKH_XD_ID = new int[][]{
            {133, 20, 100}, {134, 100, 100}, {135, 20, 100}
    };
    public static int[][] ITEM_OPTION_CS_GIAP = new int[][]{
            {3000, 6000}, {4800, 7200}, {5000, 9000}
    };
    public static int[][] ITEM_OPTION_CS_HP_MP = new int[][]{
            {50_000, 80_000}, {66_000, 88_000}, {70_000, 100_000}
    };
    public static int[][] ITEM_OPTION_CS_SDCM = new int[][]{
            {1, 2}, {2, 5}, {5, 8}
    };
    public static int[][] ITEM_OPTION_CS_HP_PC = new int[][]{
            {3, 7}, {8, 10}, {12, 15}
    };
    public static int[][] ITEM_OPTION_CS_SD_PC = new int[][]{
            {1, 2}, {2, 3}, {5, 8}
    };

    public static int[][] ITEM_OPTION_CS_CM = new int[][]{
            {8, 11}, {10, 12}, {12, 15}
    };

    public static int[][] RATIO_STAR = new int[][]{
            {100, 100}, {80, 100}, {70, 100}, {20, 100}, {10, 100}, {5, 100}, {3, 100}, {2, 100}, {1, 100}
    };

    public static void ratioHopQuaVip(Player player, int gender) {
        int[] result = new int[]{-1, -1};
        switch (gender) {
            case ConstPlayer.TRAI_DAT -> {
                result = getTempDrop(ITEM_ID_DROP_IN_VIP_CHEST_TD);
            }
            case ConstPlayer.NAMEC -> {
                result = getTempDrop(ITEM_ID_DROP_IN_VIP_CHEST_NM);
            }
            case ConstPlayer.XAYDA -> {
                result = getTempDrop(ITEM_ID_DROP_IN_VIP_CHEST_XD);
            }
        }
        if (result[0] == -1) {
            Service.gI().sendThongBao(player, "Có lỗi khi mở hộp quà hãy liên hệ dev để xử lý");
            return;
        }
        // create tem
        Item item = ItemService.gI().createLockItem(result[0], 1);
        if (item == null) {
            Service.gI().sendThongBao(player, "Có lỗi khi mở hộp quà hãy liên hệ dev để xử lý");
            return;
        }
        // create option for item
        switch (item.template.type) {
            case 0 -> {
                int optionParam = Util.nextInt(ITEM_OPTION_CS_GIAP[result[1]][0], ITEM_OPTION_CS_GIAP[result[1]][1]);
                item.itemOptions.add(new Item.ItemOption(47, optionParam));
            }
            case 1 -> {
                int optionParam = Util.nextInt(ITEM_OPTION_CS_HP_MP[result[1]][0], ITEM_OPTION_CS_HP_MP[result[1]][1]);
                item.itemOptions.add(new Item.ItemOption(6, optionParam));
            }
            case 2 -> {
                int optionParam = Util.nextInt(ITEM_OPTION_CS_SD[result[1]][0], ITEM_OPTION_CS_SD[result[1]][1]);
                item.itemOptions.add(new Item.ItemOption(0, optionParam));
            }
            case 3 -> {
                int optionParam = Util.nextInt(ITEM_OPTION_CS_HP_MP[result[1]][0], ITEM_OPTION_CS_HP_MP[result[1]][1]);
                item.itemOptions.add(new Item.ItemOption(7, optionParam));
            }
            case 4 -> {
                int optionParam = Util.nextInt(ITEM_OPTION_CS_CM[result[1]][0], ITEM_OPTION_CS_CM[result[1]][1]);
                item.itemOptions.add(new Item.ItemOption(14, optionParam));
            }
        }
        if (Util.isTrue(10, 100)) {
            // them sd chi mang
            int optionParam = Util.nextInt(ITEM_OPTION_CS_SDCM[result[1]][0], ITEM_OPTION_CS_SDCM[result[1]][1]);
            item.itemOptions.add(new Item.ItemOption(5, optionParam));
        }
        if (Util.isTrue(5, 100)) {
            int optionParam = Util.nextInt(ITEM_OPTION_CS_SD_PC[result[1]][0], ITEM_OPTION_CS_SD_PC[result[1]][1]);
            item.itemOptions.add(new Item.ItemOption(50, optionParam));
        }
        if (Util.isTrue(20, 100)) {
            int optionParam = Util.nextInt(ITEM_OPTION_CS_HP_PC[result[1]][0], ITEM_OPTION_CS_HP_PC[result[1]][1]);
            item.itemOptions.add(new Item.ItemOption(77, optionParam));
        }
        if (Util.isTrue(20, 100)) {
            int optionParam = Util.nextInt(ITEM_OPTION_CS_HP_PC[result[1]][0], ITEM_OPTION_CS_HP_PC[result[1]][1]);
            item.itemOptions.add(new Item.ItemOption(103, optionParam));
        }
        // ratio star
        int star = 0;
        for (int i = RATIO_STAR.length - 1; i >= 0; i--) {
            if (Util.isTrue(RATIO_STAR[i][0], RATIO_STAR[i][1])) {
                star = i;
                break;
            }
        }
        if (star > 0) {
            item.itemOptions.add(new Item.ItemOption(107, star));
        }
        // ratio set kich hoat ne
        int skhId = -1;
        switch (gender) {
            case ConstPlayer.TRAI_DAT -> {
                for (int i = SKH_TD_ID.length - 1; i >= 0; i--) {
                    if (Util.isTrue(SKH_TD_ID[i][1], SKH_TD_ID[i][2])) {
                        skhId = SKH_TD_ID[i][0];
                    }
                }
            }
            case ConstPlayer.NAMEC -> {
                for (int i = SKH_NM_ID.length - 1; i >= 0; i--) {
                    if (Util.isTrue(SKH_NM_ID[i][1], SKH_NM_ID[i][2])) {
                        skhId = SKH_NM_ID[i][0];
                    }
                }
            }
            case ConstPlayer.XAYDA -> {
                for (int i = SKH_XD_ID.length - 1; i >= 0; i--) {
                    if (Util.isTrue(SKH_XD_ID[i][1], SKH_XD_ID[i][2])) {
                        skhId = SKH_XD_ID[i][0];
                    }
                }
            }
        }
        if (skhId != -1) {
            item.itemOptions.add(new Item.ItemOption(skhId, 0));
            item.itemOptions.add(new Item.ItemOption(getOptionSKH(skhId), 0));
        }
        // done item
        CombineService.gI().sendEffectOpenItem(player, player.idMark.iconId, item.template.iconID);
        InventoryService.gI().addItemBag(player, item);
        InventoryService.gI().sendItemBags(player);
    }

    private static int[] getTempDrop(int[][] itemIdDropInVipChestXd) {
        int tempId = -1;
        int index = -1;
        if (Util.isTrue(RATIO_ITEM_DROP_IN_VIP_CHEST[2][0], RATIO_ITEM_DROP_IN_VIP_CHEST[2][1])) {
            tempId = itemIdDropInVipChestXd[2][Util.nextInt(itemIdDropInVipChestXd[2].length)];
            index = 2;
        } else if (Util.isTrue(RATIO_ITEM_DROP_IN_VIP_CHEST[1][0], RATIO_ITEM_DROP_IN_VIP_CHEST[1][1])) {
            tempId = itemIdDropInVipChestXd[1][Util.nextInt(itemIdDropInVipChestXd[1].length)];
            index = 1;
        } else if (Util.isTrue(RATIO_ITEM_DROP_IN_VIP_CHEST[0][0], RATIO_ITEM_DROP_IN_VIP_CHEST[0][1])) {
            tempId = itemIdDropInVipChestXd[0][Util.nextInt(itemIdDropInVipChestXd[0].length)];
            index = 0;
        }
        return new int[]{tempId, index};
    }

    public static int getOptionSKH(int skh) {
        return switch (skh) {
            case 127 -> 139;
            case 128 -> 140;
            case 129 -> 141;
            case 133 -> 136;
            case 134 -> 137;
            case 135 -> 138;
            case 130 -> 142;
            case 233 -> 234;
            case 131 -> 144;
            default -> -1;
        };
    }

    public static void openHopQuaKichHoatVip(Player player, Item item) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Cần ít nhất một ô hành trang trống");
            return;
        }
        // tim chia khoa vang torng tui do
        Item goldKey = InventoryService.gI().findItemBag(player, 1561);
        if (goldKey == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy chìa khóa vàng trong túi");
            return;
        }
        player.idMark.iconId = item.template.iconID;
        // get gender
        InventoryService.gI().subQuantityItemsBag(player, goldKey, 1);
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().sendItemBags(player);
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_OPEN_HOP_QUA_VIP, -1, "Bạn hãy chọn hành tinh muốn mở", "Trái Đất", "Namec", "XayDa");
    }
}
