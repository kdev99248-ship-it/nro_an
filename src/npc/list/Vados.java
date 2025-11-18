package npc.list;

/*
 * @Author: NgojcDev
 */
import consts.ConstNpc;
import item.Item;
import item.Item.ItemOption;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import services.ChangeMapService;
import services.ShopService;
import utils.Util;

public class Vados extends Npc {

    public Vados(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (this.mapId == 184) {
                createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Đây là khu vực thiên tử, nơi chứa trang sức của thiên tức đã thất lạc hàng nghìn năm trước, ngươi định làm gì?",
                        "Cửa\nhàng", "Đổi Chân Thiên tử", "Về\nnhà");
            } else {
                createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Ta có thể giúp gì cho ngươi?", "Đến\n Thiên Tử", "Đóng");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 184) {
                handleMap184Menu(player, select);
            } else {
                switch (select) {
                    case 0 ->
                        ChangeMapService.gI().changeMapBySpaceShip(player, 184, -1, 250);
                }
            }
        }
    }

    private void handleMap184Menu(Player player, int select) {
        if (player.idMark.isBaseMenu()) {
            switch (select) {
                case 0 -> {
                     ShopService.gI().opendShop(player, "VADOS", false);
                }
                case 1 ->
                    this.createOtherMenu(player, 6666,
                            "Ngươi sưu tầm được đủ nguyên liệu nào rồi?\n",
                            "Ma quái", "Tinh thể");
                case 2 ->
                    ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
            }
        } else if (player.idMark.getIndexMenu() == 6666) {
            handleMaterialMenu(player, select);
        } else if (player.idMark.getIndexMenu() == 7777) {
            handleMaQuaiExchange(player, select);
        } else if (player.idMark.getIndexMenu() == 8888) {
            handleTinhTheExchange(player, select);
        }
    }

    private void handleMaterialMenu(Player player, int select) {
        switch (select) {
            case 0 ->
                this.createOtherMenu(player, 7777,
                        "Ngươi cần tìm đủ số lượng Ma quái để có thể đổi lấy Trang sức Chân thiên tử với công thức tương ứng sau: \n"
                        + "Cần số lượng 99 Ma quái để đổi Trang sức 30 ngày\n"
                        + "Cần số lượng 999 Ma quái để lấy Trang sức vĩnh viễn\n"
                        + "Ngươi muốn đổi loại trang sức nào?\n",
                        "30 ngày\n(-5 Tỷ vàng)", "Vĩnh viễn\n(-9 Tỷ vàng)", "Đóng");
            case 1 ->
                this.createOtherMenu(player, 8888,
                        "Ngươi cần tìm đủ số lượng Tinh thể để có thể đổi lấy Trang sức Chân thiên tử với công thức tương ứng sau: \n"
                        + "Cần số lượng 9 Tinh thể để đổi Trang sức 30 ngày\n"
                        + "Cần số lượng 99 Tinh thể để lấy Trang sức vĩnh viễn\n"
                        + "Ngươi muốn đổi loại trang sức nào?\n",
                        "30 ngày\n(-5 Tỷ vàng)", "Vĩnh viễn\n(-9 Tỷ vàng)", "Đóng");
        }
    }

    private void handleMaQuaiExchange(Player player, int select) {
        switch (select) {
            case 0 ->
                exchangeWithMaQuai(player, 99, 5000000000L, 30);
            case 1 ->
                exchangeWithMaQuai(player, 999, 9000000000L, 0);
        }
    }

    private void handleTinhTheExchange(Player player, int select) {
        switch (select) {
            case 0 ->
                exchangeWithTinhThe(player, 9, 5000000000L, 30);
            case 1 ->
                exchangeWithTinhThe(player, 99, 9000000000L, 0);
        }
    }

    private void exchangeWithMaQuai(Player player, int requiredQuantity, long goldCost, int days) {
        Item maquai = InventoryService.gI().findItemBag(player, 1788);
        if (maquai == null || maquai.quantity < requiredQuantity) {
            Service.gI().sendThongBao(player, "Cần " + requiredQuantity + " Ma quái để đổi");
            return;
        }
        if (player.inventory.gold < goldCost) {
            Service.gI().sendThongBao(player, "Không đủ vàng");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Vui lòng chừa 1 ô trống trong hành trang.");
            return;
        }

        Item cmtanthu = createRandomAccessory(days);
        player.inventory.gold -= goldCost;
        InventoryService.gI().subQuantityItemsBag(player, maquai, requiredQuantity);
        Service.gI().sendMoney(player);
        InventoryService.gI().addItemBag(player, cmtanthu);
        InventoryService.gI().sendItemBags(player);
        Service.gI().sendThongBao(player, "Bạn nhận được " + cmtanthu.template.name);
    }

    private void exchangeWithTinhThe(Player player, int requiredQuantity, long goldCost, int days) {
        Item tinhthe = InventoryService.gI().findItemBag(player, 1787);
        if (tinhthe == null || tinhthe.quantity < requiredQuantity) {
            Service.gI().sendThongBao(player, "Cần " + requiredQuantity + " Tinh thể để đổi");
            return;
        }
        if (player.inventory.gold < goldCost) {
            Service.gI().sendThongBao(player, "Không đủ vàng");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Vui lòng chừa 1 ô trống trong hành trang.");
            return;
        }

        Item cmtanthu = createRandomAccessory(days);
        player.inventory.gold -= goldCost;
        InventoryService.gI().subQuantityItemsBag(player, tinhthe, requiredQuantity);
        Service.gI().sendMoney(player);
        InventoryService.gI().addItemBag(player, cmtanthu);
        InventoryService.gI().sendItemBags(player);
        Service.gI().sendThongBao(player, "Bạn nhận được " + cmtanthu.template.name);
    }

    private Item createRandomAccessory(int days) {
        Item cmtanthu = ItemService.gI().createNewItem((short) 1780, 1);

        // Random option 1 (HP/Ki/SD)
        int randomOption = Util.nextInt(0, 2);
        switch (randomOption) {
            case 0 ->
                cmtanthu.itemOptions.add(new ItemOption(0, Util.nextInt(100, 2500)));
            case 1 ->
                cmtanthu.itemOptions.add(new ItemOption(6, Util.nextInt(10000, 50000)));
            case 2 ->
                cmtanthu.itemOptions.add(new ItemOption(7, Util.nextInt(10000, 50000)));
        }

        // Random option 2 (Kỹ năng)
        int randomOption1 = Util.nextInt(0, 2);
        switch (randomOption1) {
            case 0 ->
                cmtanthu.itemOptions.add(new ItemOption(50, Util.nextInt(1, 14)));
            case 1 ->
                cmtanthu.itemOptions.add(new ItemOption(77, Util.nextInt(1, 14)));
            case 2 ->
                cmtanthu.itemOptions.add(new ItemOption(103, Util.nextInt(1, 14)));
        }

        cmtanthu.itemOptions.add(new ItemOption(72, 1)); // Đặc biệt
        if (days > 0) {
            cmtanthu.itemOptions.add(new ItemOption(93, days)); // Thời hạn
        }

        return cmtanthu;
    }
}
