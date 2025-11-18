package npc.list;

/*
 * @Author: NgojcDev
 */
import consts.ConstNpc;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.NpcService;
import services.TaskService;
import services.ChangeMapService;
import services.ShopService;
import services.ItemService;
import services.Service;
import item.Item;
import item.Item.ItemOption;
import utils.Util;

public class Bill extends Npc {

    public Bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            TaskService.gI().checkDoneTaskTalkNpc(player, this);
            if (mapId == 154) {
                createOtherMenu(player, ConstNpc.BASE_MENU,
                        "...",
                        "Về\nthánh địa\nKaio", "Từ chối");
            } else {
                createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Chưa tới giờ thi đấu, xem hướng dẫn để biết thêm chi tiết",
                        "Đổi đồ\nHủy diệt", "Hướng\ndẫn\nthêm", "Từ chối");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 48 -> {
                    switch (player.idMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU -> {
                            switch (select) {
                                case 0 -> {
                                    if (InventoryService.gI().fullSetThan(player)) {
                                        createOtherMenu(player, 3,
                                                "Chọn hành tinh để đổi trang bị hủy diệt\n"
                                                + "Mỗi món đổi cần:\n"
                                                + "- 1 món thần linh tương ứng đang đeo\n"
                                                + "- 99 món ăn tương ứng",
                                                "Trái Đất", "Xayda", "Namek", "Từ chối");
                                    } else {
                                        Service.gI().sendThongBaoFromBill(player, "Ngươi trang bị đủ bộ 5 món trang bị Thần\nvà mang 99 phần đồ ăn tới đây...\nrồi ta nói chuyện tiếp.");
                                    }
                                }
                                case 1 ->
                                    NpcService.gI().createTutorial(player, tempId, this.avartar,
                                            ConstNpc.HUONG_DAN_BILL);
                            }
                        }
                        case 2 -> {
                            if (select == 0 && InventoryService.gI().canOpenBillShop(player)) {
                                ShopService.gI().opendShop(player, "BILL", true);
                                break;
                            }
                        }
                        case 3 -> {
                            if (select < 3) {
                                createEquipmentMenu(player, select);
                            }
                        }
                        case 4 -> {
                            if (select < 5) {
                                handleEquipmentExchange(player, select);
                            }
                        }

                    }
                }
                case 154 -> {
                    if (select == 0) {
                        ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                        break;
                    }
                }
            }
        }
    }

    private void createEquipmentMenu(Player player, int planet) {
        String[] planetNames = {"Trái Đất", "Xayda", "Namek"};
        String[] equipTypes = {"Áo", "Quần", "Găng", "Giày", "Nhẫn"};
        String[] foodNames = {"Bánh Pudding", "Xúc xích", "Kem dâu", "Mì ly", "Sushi"};

        createOtherMenu(player, 4,
                "Chọn trang bị để đổi (" + planetNames[planet] + "):\n"
                + "Áo: 99 " + foodNames[0] + "\n"
                + "Quần: 99 " + foodNames[1] + "\n"
                + "Găng: 99 " + foodNames[2] + "\n"
                + "Giày: 99 " + foodNames[3] + "\n"
                + "Nhẫn: 99 " + foodNames[4],
                "Áo", "Quần", "Găng", "Giày", "Nhẫn", "Từ chối");

        player.idMark.setMenuType(planet); // Lưu hành tinh được chọn
    }

    private void handleEquipmentExchange(Player player, int equipType) {
        if (equipType >= 5) {
            return; // Từ chối
        }
        int planet = player.idMark.getMenuType();

        // ID thần linh theo hành tinh [planet][equipType]
        int[][] godEquipIds = {
            {555, 556, 562, 563, 561}, // Trái Đất
            {557, 558, 564, 565, 561}, // Xayda  
            {559, 560, 566, 567, 561} // Namek
        };

        // ID hủy diệt theo hành tinh [planet][equipType]
        int[][] destroyEquipIds = {
            {650, 651, 657, 658, 656}, // Trái Đất
            {654, 655, 661, 662, 656}, // Xayda
            {652, 653, 659, 660, 656} // Namek
        };

        // ID đồ ăn theo loại trang bị
        int[] foodIds = {663, 664, 665, 666, 667}; // Bánh Pudding, Xúc xích, Kem dâu, Mì ly, Sushi

        String[] equipNames = {"Áo", "Quần", "Găng", "Giày", "Nhẫn"};
        String[] foodNames = {"Bánh Pudding", "Xúc xích", "Kem dâu", "Mì ly", "Sushi"};
        String[] planetNames = {"Trái Đất", "Xayda", "Namek"};

        // Kiểm tra có đủ set thần linh
        if (!InventoryService.gI().fullSetThan(player)) {
            Service.gI().sendThongBaoFromBill(player, "Ngươi phải đeo đủ bộ trang bị Thần Linh!");
            return;
        }

        // Kiểm tra có đang đeo trang bị thần linh (bất kỳ hành tinh nào)
        Item currentEquip = player.inventory.itemsBody.get(equipType);
        if (currentEquip == null || currentEquip.template.level != 13) {
            Service.gI().sendThongBaoFromBill(player, "Ngươi phải đang đeo " + equipNames[equipType] + " Thần Linh!");
            return;
        }

        // Kiểm tra có đủ 99 món ăn
        Item food = InventoryService.gI().findItemBag(player, foodIds[equipType]);
        if (food == null || food.quantity < 99) {
            Service.gI().sendThongBaoFromBill(player, "Ngươi cần có ít nhất 99 " + foodNames[equipType] + "!");
            return;
        }

        // Kiểm tra có chỗ trống trong hành trang
        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
            Service.gI().sendThongBaoFromBill(player, "Hành trang không đủ chỗ trống!");
            return;
        }

        // Thực hiện đổi
        // Chuyển trang bị về bag rồi xóa
        InventoryService.gI().itemBodyToBag(player, equipType);
        // Tìm trang bị thần linh vừa chuyển về bag (bất kỳ hành tinh nào)
        Item removedEquip = null;
        for (Item item : player.inventory.itemsBag) {
            if (item != null && item.template != null && item.template.level == 13 && item.template.type == equipType) {
                removedEquip = item;
                break;
            }
        }
        if (removedEquip != null) {
            InventoryService.gI().subQuantityItemsBag(player, removedEquip, 1);
        }

        // Xóa 99 món ăn
        InventoryService.gI().subQuantityItemsBag(player, food, 99);

        // Tạo trang bị hủy diệt với full option
        Item destroyEquip = createDestroyEquipment((short) destroyEquipIds[planet][equipType]);

        // Thêm vào hành trang
        InventoryService.gI().addItemBag(player, destroyEquip);

        // Cập nhật giao diện
        InventoryService.gI().sendItemBody(player);
        InventoryService.gI().sendItemBags(player);

        Service.gI().sendThongBaoFromBill(player, "Đổi thành công " + equipNames[equipType] + " Hủy Diệt " + planetNames[planet] + "!");
    }

    private Item createDestroyEquipment(short templateId) {
        Item item = ItemService.gI().createNewItem(templateId);
        item.itemOptions.clear();

        // Thêm các option full cho trang bị hủy diệt
        switch (item.template.type) {
            case 0 -> {
                // Áo
                item.itemOptions.add(new ItemOption(47, Util.nextInt(2000, 2500)));
                item.itemOptions.add(new ItemOption(21, 44));
            }
            case 1 -> {
                // Quần
                item.itemOptions.add(new ItemOption(22, Util.nextInt(150, 200)));
                item.itemOptions.add(new ItemOption(21, 46));
            }
            case 2 -> {
                // Găng
                item.itemOptions.add(new ItemOption(0, Util.nextInt(8600, 10350)));
                item.itemOptions.add(new ItemOption(21, 50));
            }
            case 3 -> {
                // Giày
                item.itemOptions.add(new ItemOption(23, Util.nextInt(96, 119)));
                item.itemOptions.add(new ItemOption(27, Util.nextInt(11000, 12000)));
                item.itemOptions.add(new ItemOption(21, 42));
            }
            case 4 -> {
                // Nhẫn
                item.itemOptions.add(new ItemOption(14, Util.nextInt(20, 25)));
                item.itemOptions.add(new ItemOption(21, 48));
            }
        }
        item.itemOptions.add(new ItemOption(30, 0)); // Có thể nâng cấp

        item.content = item.getContent();
        item.info = item.getInfo();

        return item;
    }
}
