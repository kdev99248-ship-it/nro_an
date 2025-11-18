package services;

/*
 * @Author: NgojcDev
 */

import item.Item;
import player.Player;
import utils.Util;

import java.util.ArrayList;
import java.util.List;

public class RewardService {

    private static RewardService I;

    private RewardService() {

    }

    public static RewardService gI() {
        if (RewardService.I == null) {
            RewardService.I = new RewardService();
        }
        return RewardService.I;
    }

    // ========================LUCKY ROUND========================
    public List<Item> getListItemLuckyRound(Player player, int num, boolean vip) {
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Item it = ItemService.gI().createNewItem((short) 189); // vang
            it.quantity = Util.nextInt(5, 50) * 1000;
            boolean success = Util.isTrue(1, 2);
            if (Util.isTrue(10, 100)) {
                // Đá nâng cấp (ID: 1074-1078) và Đá may mắn (ID: 1079-1083)
                int[] upgradeStones = {1074, 1075, 1076, 1077, 1078, 1079, 1080, 1081, 1082, 1083};
                it = ItemService.gI().createNewItem((short) upgradeStones[Util.nextInt(upgradeStones.length)]);
                it.quantity = 1;
            } else if (Util.isTrue(10, 100)) {
                // 10% tỷ lệ ra cải trang Mị Nương
                it = ItemService.gI().createNewItem((short) 860);
                it.itemOptions.clear();
                // Random chỉ số SD: 1-25%
                int sdParam = Util.nextInt(1, 25);
                it.itemOptions.add(new Item.ItemOption(50, sdParam));

                // Random chỉ số HP: 1-25%
                int hpParam = Util.nextInt(1, 25);
                it.itemOptions.add(new Item.ItemOption(77, hpParam));

                // Random chỉ số KI: 1-25%
                int kiParam = Util.nextInt(1, 25);
                it.itemOptions.add(new Item.ItemOption(103, kiParam));

                // Random chỉ số SDCM: 1-20%
                int sdcmParam = Util.nextInt(1, 40);
                it.itemOptions.add(new Item.ItemOption(5, sdcmParam));

                // 90% có option hạn sử dụng, 10% không có
                if (Util.isTrue(90, 100)) {
                    int expireParam = Util.nextInt(1, 3);
                    it.itemOptions.add(new Item.ItemOption(93, expireParam));
                }

                it.quantity = 1;
            } else if (Util.isTrue(10, 100)) {
                // 10% tỷ lệ ra cải trang Thỏ
                it = ItemService.gI().createNewItem((short) 464);
                it.itemOptions.clear();

                // Random chỉ số SD: 1-30%
                int sdParam = Util.nextInt(1, 30);
                it.itemOptions.add(new Item.ItemOption(50, sdParam));

                // Random chỉ số HP: 1-40%
                int hpParam = Util.nextInt(1, 40);
                it.itemOptions.add(new Item.ItemOption(77, hpParam));

                // Random chỉ số KI: 1-40%
                int kiParam = Util.nextInt(1, 40);
                it.itemOptions.add(new Item.ItemOption(103, kiParam));

                // Random chỉ số Đẹp: 1-15%
                int sdcmParam = Util.nextInt(1, 15);
                it.itemOptions.add(new Item.ItemOption(117, sdcmParam));

                // 90% có option hạn sử dụng, 10% không có
                if (Util.isTrue(90, 100)) {
                    int expireParam = Util.nextInt(1, 3);
                    it.itemOptions.add(new Item.ItemOption(93, expireParam));
                }
                it.quantity = 1;
            } else if (Util.isTrue(1, 2)) {
                int[] itemId = {467, 468, 469, 470, 471, 741, 745, 800, 801, 803, 804, 1000};
                int itemid = itemId[Util.nextInt(itemId.length)];
                if (Util.isTrue(20, 100)) {
                    int[] itemId2 = {467, 468, 469, 470, 471, 741, 745, 800, 801, 803, 804, 999, 1000, 1001};
                    itemid = itemId2[Util.nextInt(itemId2.length)];
                }
                byte[] option = {77, 80, 81, 103, 50, 94, 5};
                byte optionid;
                byte param;
                Item vpdl = ItemService.gI().createNewItem((short) itemid);
                vpdl.itemOptions.clear();
                optionid = option[Util.nextInt(0, 6)];
                param = (byte) Util.nextInt(5, 10);
                vpdl.itemOptions.add(new Item.ItemOption(optionid, param));
                vpdl.itemOptions.add(new Item.ItemOption(30, 0));
                vpdl.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 30)));
                it = vpdl;
                it.quantity = 1;
                //Mảnh rồng
            } else if (Util.isTrue(1, 30)) {
                it = ItemService.gI().createNewItem((short) 1204);
                it.quantity = 1;
                //Mảnh đội trưởng vàng
            } else if (Util.isTrue(1, 30)) {
                it = ItemService.gI().createNewItem((short) 956);
                it.quantity = 1;
            } else if (Util.isTrue(1, 20)) {
                it = ItemService.gI().createNewItem((short) 585);
                it.quantity = Util.nextInt(1, 5);
            } else if (Util.isTrue(1, 100)) {
                it = ItemService.gI().createNewItem((short) Util.nextInt(18, 20));
                it.quantity = Util.nextInt(1, 5);
            } else if (Util.isTrue(1, 30)) {
                it = ItemService.gI().createNewItem((short) Util.nextInt(220, 224));
                it.quantity = Util.nextInt(1, 5);
            } else if (Util.isTrue(1, 100)) {
                it = ItemService.gI().createNewItem((short) Util.nextInt(828, 842));
                it.quantity = Util.nextInt(1, 5);
            } else if (Util.isTrue(20, 100)) {
                it = ItemService.gI().createNewItem((short) Util.nextInt(1150, 1154));
                it.quantity = 1;
            }
            it = itemRand(it, success);
            list.add(it);
        }
        return list;
    }

    public Item itemRand(Item item, boolean success) {
        if (!success) {
            item = ItemService.gI().createNewItem((short) 189, Util.nextInt(5, 50) * 1000);
        }
        return item;
    }

    public void rewardLancon(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            player.canReward = false;
            player.haveReward = true;
            int[] items = {734, 920, 849, 743, 733};
            Item item = ItemService.gI().createNewItem((short) items[Util.nextInt(items.length)]);
            byte[] option = {77, 80, 81, 103, 50, 94, 5};
            byte[] option_v2 = {14, 16, 17, 19, 27, 28, 47, 87};
            if (Util.isTrue(5, 100)) {
                item.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 5)));
                item.itemOptions
                        .add(new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 2)));
            } else {
                item.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 10)));
                if (Util.isTrue(1, 10)) {
                    item.itemOptions
                            .add(new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 10)));
                }
                item.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 30)));
            }
            item.itemOptions.add(new Item.ItemOption(89, 0));
            item.itemOptions.add(new Item.ItemOption(30, 0));
            InventoryService.gI().addItemBag(player, item);
            InventoryService.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn vừa nhận được " + item.template.name);
        } else {
            Service.gI().sendThongBao(player, "Cần 1 ô hành trang trống");
        }
    }

    public Item rewardCapsuleTet(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (Util.isTrue(40, 100)) {
                int[] items = {734, 920, 849, 743, 733};
                Item item = ItemService.gI().createNewItem((short) items[Util.nextInt(items.length)]);
                byte[] option = {77, 80, 81, 103, 50, 94, 5};
                byte[] option_v2 = {14, 16, 17, 19, 27, 28, 47, 87};
                if (Util.isTrue(5, 100)) {
                    item.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 5)));
                    item.itemOptions
                            .add(new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 2)));
                } else {
                    item.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 10)));
                    if (Util.isTrue(1, 10)) {
                        item.itemOptions.add(
                                new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 10)));
                    }
                    item.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 30)));
                }
                item.itemOptions.add(new Item.ItemOption(89, 0));
                item.itemOptions.add(new Item.ItemOption(30, 0));
                return item;
            } else if (Util.isTrue(50, 100)) {
                int[] items = {942, 943, 944};
                Item item = ItemService.gI().createNewItem((short) items[Util.nextInt(items.length)]);
                byte[] option = {77, 80, 81, 103, 50, 94, 5};
                byte[] option_v2 = {14, 16, 17, 19, 27, 28, 47, 87};
                if (Util.isTrue(5, 100)) {
                    item.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 5)));
                    item.itemOptions
                            .add(new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 2)));
                } else {
                    item.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 10)));
                    if (Util.isTrue(1, 10)) {
                        item.itemOptions.add(
                                new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 10)));
                    }
                    item.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 30)));
                }
                item.itemOptions.add(new Item.ItemOption(30, 0));
                return item;
            } else {
                Item it = ItemService.gI().createNewItem((short) Util.nextInt(2148, 2152));
                it.quantity = 1;
                if (Util.isTrue(5, 100)) {
                    it.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 30)));
                    it.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 30)));
                    it.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 30)));
                    it.itemOptions.add(new Item.ItemOption(94, Util.nextInt(20, 30)));
                    it.itemOptions.add(new Item.ItemOption(14, Util.nextInt(2, 35)));
                    it.itemOptions.add(new Item.ItemOption(108, Util.nextInt(2, 40)));
                    if (Util.isTrue(5, 30)) {
                        it.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 14)));
                    }
                    it.itemOptions.add(new Item.ItemOption(154, 0));
                } else {
                    it.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 50)));
                    it.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 50)));
                    if (Util.isTrue(5, 30)) {
                        it.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 35)));
                    }
                    it.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 50)));
                    it.itemOptions.add(new Item.ItemOption(94, Util.nextInt(20, 50)));
                    it.itemOptions.add(new Item.ItemOption(14, Util.nextInt(2, 50)));
                    it.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 15)));
                }
                byte[] option = {80, 81, 103, 50, 94, 5};
                byte[] option_v2 = {14, 16, 17, 19, 27, 28, 47, 87};
                if (Util.isTrue(20, 100)) {
                    it.itemOptions.add(new Item.ItemOption(option[Util.nextInt(option.length)], Util.nextInt(1, 5)));
                }
                if (Util.isTrue(20, 100)) {
                    it.itemOptions.add(new Item.ItemOption(108, Util.nextInt(1, 40)));
                }
                if (Util.isTrue(20, 100)) {
                    it.itemOptions
                            .add(new Item.ItemOption(option_v2[Util.nextInt(option_v2.length)], Util.nextInt(1, 2)));
                }
                return it;
            }
        } else {
            Service.gI().sendThongBao(player, "Cần 1 ô hành trang trống");
            return null;
        }
    }
}
