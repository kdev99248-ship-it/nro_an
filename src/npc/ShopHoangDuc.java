package npc;

import player.Player;

public class ShopHoangDuc extends Npc {
    public ShopHoangDuc(int mapId, int status, int cx, int cy, int tempId, int avatar) {
        super(mapId, status, cx, cy, tempId, avatar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {

    }
}
