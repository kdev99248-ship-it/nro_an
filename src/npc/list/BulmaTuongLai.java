package npc.list;

/*
 * @Author: NgojcDev
 */

import consts.ConstNpc;
import item.Item;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import services.TaskService;
import services.ShopService;
import utils.Util;

public class BulmaTuongLai extends Npc {

    public BulmaTuongLai(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (this.mapId == 104 || this.mapId == 5) {
                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Hế lô bạn nhỏ", "Cửa hàng", "Đóng");
                }
            } else if (this.mapId == 102) {
                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Muốn làm tí inovar không?", "Cửa hàng",
                            "Đổi X99 Phiếu Bé Ngu Lấy Hào Quang", "Đổi X99 Phiếu Bé Ngu + 20Tr Lấy Cái Nịt", "Đóng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 104 || this.mapId == 5) {
                if (player.idMark.isBaseMenu()) {
                    if (select == 0) {
                        ShopService.gI().opendShop(player, "KARIN", true);
                    }
                }
            } else if (this.mapId == 102) {
                if (player.idMark.isBaseMenu()) {
                    switch (select) {
                        case 0 ->
                            ShopService.gI().opendShop(player, "BULMA_TL", true);
                        case 1 -> {
                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                if (InventoryService.gI().findItemBag(player, 2048) != null
                                        && InventoryService.gI().findItemBag(player, 2048).quantity >= 99) {
                                    InventoryService.gI().subQuantityItemsBag(player,
                                            InventoryService.gI().findItemBag(player, 2048), 99);
                                    InventoryService.gI().sendItemBags(player);
                                    Item item = ItemService.gI().createNewItem(((short) 2046));
                                    item.itemOptions.add(new Item.ItemOption(147, Util.nextInt(10, 30)));
                                    item.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 30)));
                                    InventoryService.gI().addItemBag(player, item);
                                    InventoryService.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được Hào Quang Rực Rỡ!");
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không đủ Phiếu Bé Ngu!");
                                    this.npcChat(player, "THAM LAM!");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Hành Trang không còn chỗ trống!");
                            }
                        }
                        case 2 -> {
                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                if (InventoryService.gI().findItemBag(player, 2048) != null
                                        && InventoryService.gI().findItemBag(player, 2048).quantity >= 99
                                        && player.inventory.gold >= 20000000) {
                                    InventoryService.gI().subQuantityItemsBag(player,
                                            InventoryService.gI().findItemBag(player, 2048), 99);
                                    InventoryService.gI().sendItemBags(player);
                                    player.inventory.gold -= 20000000;
                                    Item item = ItemService.gI().createNewItem(((short) 673));
                                    Service.gI().sendMoney(player);
                                    InventoryService.gI().addItemBag(player, item);
                                    InventoryService.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "BẠN VỪA NHẬN ĐƯỢC CÁI NỊT TỪ TIẾN BRY!");
                                    this.npcChat(player, "NGU DỐT!");
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không đủ Phiếu Bé Ngu hoặc Vàng!");
                                    this.npcChat(player, "THAM LAM!");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Hành Trang không còn chỗ trống!");
                            }
                        }
                        default -> {
                        }
                    }
                }
            }
        }
    }
}
