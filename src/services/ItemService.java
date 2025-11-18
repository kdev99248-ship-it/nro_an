package services;

/*
 * @Author: NgojcDev
 */

import combine.CombineService;
import utils.Template;
import utils.Template.ItemOptionTemplate;
import item.Item;
import map.ItemMap;
import player.Player;
import shop.ItemShop;
import server.Manager;
import utils.TimeUtil;
import utils.Util;
import item.Item.ItemOption;

import java.util.*;
import java.util.stream.Collectors;

import map.Zone;

public class ItemService {

    private static ItemService i;

    public static ItemService gI() {
        if (i == null) {
            i = new ItemService();
        }
        return i;
    }

    public short getItemIdByIcon(short IconID) {
        for (int i = 0; i < Manager.ITEM_TEMPLATES.size(); i++) {
            if (Manager.ITEM_TEMPLATES.get(i).iconID == IconID) {
                return Manager.ITEM_TEMPLATES.get(i).id;
            }
        }
        return -1;
    }

    public Item createItemNull() {
        Item item = new Item();
        return item;
    }

    public Item createItemFromItemShop(ItemShop itemShop) {
        Item item = new Item();
        item.template = itemShop.temp;
        item.quantity = 1;
        item.content = item.getContent();
        item.info = item.getInfo();
        for (Item.ItemOption io : itemShop.options) {
            item.itemOptions.add(new Item.ItemOption(io));
        }
        return item;
    }

    public Item copyItem(Item item) {
        Item it = new Item();
        it.itemOptions = new ArrayList<>();
        it.template = item.template;
        it.info = item.info;
        it.content = item.content;
        it.quantity = item.quantity;
        it.createTime = item.createTime;
        for (Item.ItemOption io : item.itemOptions) {
            it.itemOptions.add(new Item.ItemOption(io));
        }
        return it;
    }

    public Item createNewItem(short tempId) {
        return createNewItem(tempId, 1);
    }

    public Item createNewItem(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();

        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item otpts(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(47, Util.nextInt(2000, 2500)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(22, Util.nextInt(150, 200)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(0, Util.nextInt(18000, 20000)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(23, Util.nextInt(150, 200)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(14, Util.nextInt(20, 25)));
        }
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemSetKichHoat(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.itemOptions = createItemNull().itemOptions;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemFromItemMap(ItemMap itemMap) {
        Item item = createNewItem(itemMap.itemTemplate.id, itemMap.quantity);
        item.itemOptions = itemMap.options;
        return item;
    }

    public ItemOptionTemplate getItemOptionTemplate(int id) {
        return Manager.ITEM_OPTION_TEMPLATES.get(id);
    }

    public Template.ItemTemplate getTemplate(int id) {
        return Manager.ITEM_TEMPLATES.get(id);
    }

    public int getPercentTrainArmor(Item item) {
        if (item != null) {
            switch (item.template.id) {
                case 529:
                case 534:
                    return 10;
                case 530:
                case 535:
                    return 20;
                case 531:
                case 536:
                    return 30;
                case 1716: // Giáp tập luyện cấp 4
                    return 40;
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    public boolean isTrainArmor(Item item) {
        if (item != null) {
            switch (item.template.id) {
                case 529:
                case 534:
                case 530:
                case 535:
                case 531:
                case 536:
                case 1716: // Giáp tập luyện cấp 4
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public boolean isOutOfDateTime(Item item) {
        if (item != null) {
            for (Item.ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 93) {
                    int dayPass = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.DAY);
                    if (dayPass != 0) {
                        io.param -= dayPass;
                        if (io.param <= 0) {
                            return true;
                        } else {
                            item.createTime = System.currentTimeMillis();
                        }
                    }
                }
            }
        }
        return false;
    }

    public void OpenItem736(Player player, Item itemUse) {
        try {
            if (InventoryService.gI().getCountEmptyBag(player) <= 1) {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 2 ô trống hành trang");
                return;
            }
            short[] icon = new short[2];
            int rd = Util.nextInt(1, 100);
            int rac = 50;
            int ruby = 20;
            int dbv = 10;
            int vb = 10;
            int bh = 5;
            int ct = 5;
            Item item = randomRac();
            if (rd <= rac) {
                item = randomRac();
            } else if (rd <= rac + ruby) {
                item = createItemSetKichHoat(861, 1);
            } else if (rd <= rac + ruby + dbv) {
                item = daBaoVe();
            } else if (rd <= rac + ruby + dbv + vb) {
                item = vanBay2011(true);
            } else if (rd <= rac + ruby + dbv + vb + bh) {
                item = phuKien2011(true);
            } else if (rd <= rac + ruby + dbv + vb + bh + ct) {
                item = caitrang2011(true);
            }
            if (item.template.id == 861) {
                item.quantity = Util.nextInt(10, 30);
            }
            icon[0] = itemUse.template.iconID;
            icon[1] = item.template.iconID;
            InventoryService.gI().subQuantityItemsBag(player, itemUse, 1);
            InventoryService.gI().addItemBag(player, item);
            InventoryService.gI().sendItemBags(player);
            player.inventory.event++;
            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
            CombineService.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int randTempItemDoSao(int gender) {
        // Mảng chứa các item theo từng loại (type)
        int[][] ao = {{3, 34, 136, 137, 138, 139}, {4, 42, 152, 153, 154, 155}, {5, 50, 168, 169, 170, 171}};
        int[][] quan = {{9, 36, 140, 141, 142, 143}, {10, 44, 156, 157, 158, 159},
                {11, 52, 172, 173, 174, 175}};
        int[][] gang = {{37, 38, 144, 145, 146, 147}, {25, 45, 160, 161, 162, 163},
                {26, 54, 176, 177, 178, 179}};
        int[][] giay = {{39, 40, 148, 149, 150, 151}, {31, 48, 164, 165, 166, 167},
                {32, 56, 180, 181, 182, 183}};
        int[][] rada = {{58, 59, 184, 185, 186, 187}, {58, 59, 184, 185, 186, 187},
                {58, 59, 184, 185, 186, 187}};
        int[][][] item = {ao, gang, quan, giay, rada};

        // Khởi tạo đối tượng Random
        Random random = new Random();

        // Xác định type
        int type;
        if (Util.isTrue(10, 100)) {
            type = 4; // rada
        } else if (Util.isTrue(23, 100)) {
            type = 3; // giay
        } else if (Util.isTrue(23, 100)) {
            type = 1; // ao
        } else if (Util.isTrue(23, 100)) {
            type = 0; // gang
        } else {
            type = 2; // quan
        }

        // Lấy chỉ số ngẫu nhiên từ 0 đến 5 bằng Random
        int index = random.nextInt(6); // Lấy giá trị ngẫu nhiên từ 0 đến 5

        // Trả về phần tử tương ứng
        return item[type][gender][index];
    }

    public int randDoSao(int gender) {
        int[][][] items = {
                {{0, 33}, {1, 41}, {2, 49}},
                {{6, 35}, {7, 43}, {8, 51}},
                {{27, 30}, {28, 47}, {29, 55}},
                {{21, 24}, {22, 46}, {23, 53}},
                {{12, 57}, {12, 57}, {12, 57}}
        };

        // Random số trong khoảng 0 - 99
        int rand = Util.nextInt(100);

        int type;
        if (rand < 10) {
            type = 4; // rada (10%)
        } else if (rand < 32) {
            type = 0; // ao (22.5%)
        } else if (rand < 55) {
            type = 1; // quan (22.5%)
        } else if (rand < 77) {
            type = 2; // giày (22.5%)
        } else {
            type = 3; // găng (22.5%)
        }

        // Chọn item dựa trên type và gender
        return items[type][gender][Util.nextInt(2)];
    }

    public void OpenItem648(Player player, Item itemUse) {
        try {
            if (InventoryService.gI().getCountEmptyBag(player) <= 1) {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 2 ô trống hành trang");
                return;
            }
            short[] icon = new short[2];
            int rd = Util.nextInt(1, 100);
            int rac = 50;
            int ruby = 20;
            int dbv = 10;
            int vb = 10;
            int bh = 5;
            int ct = 5;
            Item item = randomRac();
            if (rd <= rac) {
                item = randomRac2();
            } else if (rd <= rac + ruby) {
                item = createItemSetKichHoat(861, 1);
            } else if (rd <= rac + ruby + dbv) {
                item = vatphamsk(true);
            } else if (rd <= rac + ruby + dbv + vb) {
                item = vanBayChrimas(true);
            } else if (rd <= rac + ruby + dbv + vb + bh) {
                item = phuKienChristmas(true);
            } else if (rd <= rac + ruby + dbv + vb + bh + ct) {
                item = caitrangChristmas(true);
            }
            if (item.template.id == 861) {
                item.quantity = Util.nextInt(10, 30);
            }
            icon[0] = itemUse.template.iconID;
            icon[1] = item.template.iconID;
            InventoryService.gI().subQuantityItemsBag(player, itemUse, 1);
            InventoryService.gI().addItemBag(player, item);
            InventoryService.gI().sendItemBags(player);
            player.inventory.event++;
            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
            CombineService.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cải trang sự kiện 20/11
    public Item caitrang2011(boolean rating) {
        Item item = createItemSetKichHoat(680, 1);
        item.itemOptions.add(new Item.ItemOption(76, 1));// VIP
        item.itemOptions.add(new Item.ItemOption(77, 28));// hp 28%
        item.itemOptions.add(new Item.ItemOption(103, 25));// ki 25%
        item.itemOptions.add(new Item.ItemOption(147, 24));// sd 26%
        if (Util.isTrue(995, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(3) + 1));// hsd
        }
        return item;
    }

    // Cải trang sự kiện giáng sinh
    public Item caitrangChristmas(boolean rating) {
        Item item = createItemSetKichHoat(Util.nextInt(386, 394), 1);
        item.itemOptions.add(new Item.ItemOption(77, Util.nextInt(15, 51)));
        item.itemOptions.add(new Item.ItemOption(103, Util.nextInt(15, 51)));
        item.itemOptions.add(new Item.ItemOption(147, Util.nextInt(15, 20)));
        item.itemOptions.add(new Item.ItemOption(95, Util.nextInt(15, 51)));
        item.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 30)));
        item.itemOptions.add(new Item.ItemOption(106, 0));// sd 26%
        if (Util.isTrue(995, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(3) + 1));// hsd
        }
        return item;
    }

    // 610 - bong hoa
    // Phụ kiện bó hoa 20/11
    public Item phuKien2011(boolean rating) {
        Item item = createItemSetKichHoat(954, 1);
        item.itemOptions.add(new Item.ItemOption(77, new Random().nextInt(5) + 5));
        item.itemOptions.add(new Item.ItemOption(103, new Random().nextInt(5) + 5));
        item.itemOptions.add(new Item.ItemOption(147, new Random().nextInt(5) + 5));
        if (Util.isTrue(1, 100)) {
            item.itemOptions.get(Util.nextInt(item.itemOptions.size() - 1)).param = 10;
        }
        item.itemOptions.add(new Item.ItemOption(30, 1));// ko the gd
        if (Util.isTrue(995, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(3) + 1));// hsd
        }
        return item;
    }

    public Item phuKienChristmas(boolean rating) {
        Item item = createItemSetKichHoat(745, 1);
        item.itemOptions.add(new Item.ItemOption(77, new Random().nextInt(25) + 5));
        item.itemOptions.add(new Item.ItemOption(103, new Random().nextInt(25) + 5));
        item.itemOptions.add(new Item.ItemOption(147, new Random().nextInt(25) + 5));
        if (Util.isTrue(1, 100)) {
            item.itemOptions.get(Util.nextInt(item.itemOptions.size() - 1)).param = 10;
        }
        item.itemOptions.add(new Item.ItemOption(30, 1));// ko the gd
        if (Util.isTrue(995, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(3) + 1));// hsd
        }
        return item;
    }

    public Item vanBay2011(boolean rating) {
        Item item = createItemSetKichHoat(795, 1);
        item.itemOptions.add(new Item.ItemOption(89, 1));
        item.itemOptions.add(new Item.ItemOption(30, 1));// ko the gd
        if (Util.isTrue(950, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(3) + 1));// hsd
        }
        return item;
    }

    public int[] randOptionItemKichHoatNew(byte gender) {
        int op1, op2, op3, op4; // Khởi tạo op3, op4 với giá trị mặc định
        switch (gender) {
            case 0 -> { // Giới tính Nam
                {
                    op1 = 245;
                    op2 = 246;
                    op3 = 247;
                    op4 = 248;
                }
            }
            case 1 -> {
                {
                    op1 = 237;
                    op2 = 238;
                    op3 = 239;
                    op4 = 240;
                }
            }
            default -> {
                {
                    op1 = 241;
                    op2 = 242;
                    op3 = 243;
                    op4 = 244;
                }
            }
        }
        return new int[]{op1, op2, op3, op4}; // Trả về mảng chứa 4 giá trị
    }

    public Item daBaoVe() {
        Item item = createItemSetKichHoat(987, 1);
        item.itemOptions.add(new Item.ItemOption(30, 1));// ko the gd
        return item;
    }

    public Item randomRac() {
        short[] racs = {20, 19, 18, 17};
        Item item = createItemSetKichHoat(racs[Util.nextInt(racs.length - 1)], 1);
        if (optionRac(item.template.id) != 0) {
            item.itemOptions.add(new Item.ItemOption(optionRac(item.template.id), 1));
        }
        return item;
    }

    public Item randomRac2() {
        short[] racs = {585, 704, 2048, 379, 384, 385, 381, 828, 829, 830, 831, 832, 833, 834, 835, 836, 837, 838, 839,
                840, 841, 842, 934, 935};
        int idItem = racs[Util.nextInt(racs.length - 1)];
        if (Util.isTrue(1, 100)) {
            idItem = 956;
        }
        Item item = createItemSetKichHoat(idItem, 1);
        if (optionRac(item.template.id) != 0) {
            item.itemOptions.add(new Item.ItemOption(optionRac(item.template.id), 1));
        }
        return item;
    }

    public Item vanBayChrimas(boolean rating) {
        Item item = createItemSetKichHoat(746, 1);
        item.itemOptions.add(new Item.ItemOption(89, 1));
        item.itemOptions.add(new Item.ItemOption(30, 1));// ko the gd
        if (Util.isTrue(950, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(3) + 1));// hsd
        }
        return item;
    }

    public byte optionRac(short itemId) {
        switch (itemId) {
            case 220:
                return 71;
            case 221:
                return 70;
            case 222:
                return 69;
            case 224:
                return 67;
            case 223:
                return 68;
            default:
                return 0;
        }
    }

    public Item vatphamsk(boolean hsd) {
        int[] itemId = {2025, 2026, 2036, 2037, 2038, 2039, 2040, 2019, 2020, 2021, 2022, 2023, 2024, 954, 955, 952,
                953, 924, 860, 742};
        byte[] option = {77, 80, 81, 103, 50, 94, 5};
        byte[] option_v2 = {14, 16, 17, 19, 27, 28, 47, 87}; // 77 %hp // 80 //81 //103 //50 //94 //5 % sdcm
        byte optionid = 0;
        byte optionid_v2 = 0;
        byte param = 0;
        Item lt = ItemService.gI().createNewItem((short) itemId[Util.nextInt(itemId.length)]);
        lt.itemOptions.clear();
        optionid = option[Util.nextInt(0, 6)];
        param = (byte) Util.nextInt(5, 15);
        lt.itemOptions.add(new Item.ItemOption(optionid, param));
        if (Util.isTrue(1, 100)) {
            optionid_v2 = option_v2[Util.nextInt(option_v2.length)];
            lt.itemOptions.add(new Item.ItemOption(optionid_v2, param));
        }
        if (Util.isTrue(999, 1000) && hsd) {
            lt.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 7)));
        }
        lt.itemOptions.add(new Item.ItemOption(30, 0));
        return lt;
    }

    public List<Item.ItemOption> getListOptionItemShop(short id) {
        List<Item.ItemOption> list = new ArrayList<>();
        Manager.SHOPS.forEach(shop -> shop.tabShops.forEach(tabShop -> tabShop.itemShops.forEach(itemShop -> {
            if (itemShop.temp.id == id && list.isEmpty()) {
                list.addAll(itemShop.options);
            }
        })));
        return list;
    }

    public int randTempItemKichHoat(int gender) {
        int[][][] items = {{{0, 33}, {1, 41}, {2, 49}}, {{6, 35}, {7, 43}, {8, 51}},
                {{27, 30}, {28, 47}, {29, 55}}, {{21, 24}, {22, 46}, {23, 53}},
                {{12, 57}, {12, 57}, {12, 57}}};
        // a w j g rd
        int type;
        if (Util.isTrue(10, 100)) {
            type = 4; // rada
        } else if (Util.isTrue(30, 100)) {
            type = 3; // gang
        } else if (Util.isTrue(50, 100)) {
            type = 1; // quan
        } else if (Util.isTrue(70, 100)) {
            type = 0; // ao
        } else {
            type = 2; // giay
        }

        return items[type][gender][Util.nextInt(1)];
    }

    public int[] randOptionItemKichHoat(int gender) {
        int op1;
        int op2;
        switch (gender) {
            case 0 -> {
                if (Util.isTrue(70, 100)) {
                    op1 = 128;
                    op2 = 140;
                } else if (Util.isTrue(50, 100)) {
                    op1 = 127;
                    op2 = 139;
                } else if (Util.isTrue(40, 100)) {
                    op1 = 129;
                    op2 = 141;
                } else {
                    op1 = 214;
                    op2 = 215;
                }
            }
            case 1 -> {
                if (Util.isTrue(70, 100)) {
                    op1 = 130;
                    op2 = 142;
                } else if (Util.isTrue(50, 100)) {
                    op1 = 131;
                    op2 = 143;
                } else {
                    op1 = 132;
                    op2 = 144;
                }
            }
            default -> {
                if (Util.isTrue(70, 100)) {
                    op1 = 134;
                    op2 = 137;
                } else if (Util.isTrue(50, 100)) {
                    op1 = 135;
                    op2 = 138;
                } else {
                    op1 = 133;
                    op2 = 136;
                }
            }
        }
        return new int[]{op1, op2};
    }

    public ItemMap randDoTL(Zone zone, int quantity, int x, int y, long id) {
        short idTempTL, type;
        short[] ao = {555, 557, 559};
        short[] quan = {556, 558, 560};
        short[] gang = {562, 564, 566};
        short[] giay = {563, 565, 567};
        short[] nhan = {561};
        short[] options = {30, 34, 35, 36, 86, 87, 208};
        if (Util.isTrue(10, 100)) {
            idTempTL = nhan[0];
            type = 4; // rada
        } else if (Util.isTrue(30, 100)) {
            idTempTL = gang[Util.nextInt(3)];
            type = 2; // gang
        } else if (Util.isTrue(50, 100)) {
            idTempTL = quan[Util.nextInt(3)];
            type = 1; // quan
        } else if (Util.isTrue(70, 100)) {
            idTempTL = ao[Util.nextInt(3)];
            type = 0; // ao
        } else {
            idTempTL = giay[Util.nextInt(3)];
            type = 3; // giay
        }
        int tiLe = Util.nextInt(100, 115);
        List<ItemOption> itemoptions = new ArrayList<>();
        switch (type) {
            case 0 -> itemoptions.add(new ItemOption(47, Util.nextInt(800, 900) * tiLe / 100));
            case 1 -> {
                int chiso = Util.nextInt(46000, 49000) * tiLe / 100;
                itemoptions.add(new ItemOption(22, chiso / 1000));
                itemoptions.add(new ItemOption(27, chiso * 125 / 1000));
            }
            case 2 -> itemoptions.add(new ItemOption(0, Util.nextInt(4300, 4500) * tiLe / 100));
            case 3 -> {
                int chiso = Util.nextInt(46000, 49000) * tiLe / 100;
                itemoptions.add(new ItemOption(23, chiso / 1000));
                itemoptions.add(new ItemOption(28, chiso * 125 / 1000));
            }
            case 4 -> itemoptions.add(new ItemOption(14, Util.nextInt(14, 17) * tiLe / 100));
        }
        if (Util.isTrue(90, 100)) {
            itemoptions.add(new ItemOption(options[Util.nextInt(options.length)], 0));
        }
        itemoptions.add(new ItemOption(21, Util.nextInt(15, 40)));
        ItemMap it = new ItemMap(zone, idTempTL, quantity, x, y, id);
        it.options.clear();
        it.options.addAll(itemoptions);
        return it;
    }

    public Item createLockItem(int tempId, int i) {
        Item item = ItemService.gI().createNewItem((short) tempId, 1);
        item.itemOptions.add(new ItemOption(30, 0));
        return item;
    }
}
