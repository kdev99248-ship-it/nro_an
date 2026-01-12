package npc;

import player.Player;
import services.ShopService;

public class ShopHoangDuc extends Npc {
    public static String SHOP_TAG_NAME = "SHOP_HOANG_DUC";

    public ShopHoangDuc(int mapId, int status, int cx, int cy, int tempId, int avatar) {
        super(mapId, status, cx, cy, tempId, avatar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            ShopService.gI().opendShop(player, SHOP_TAG_NAME,true);
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {

    }
}
