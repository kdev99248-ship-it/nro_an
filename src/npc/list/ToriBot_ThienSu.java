package npc.list;

/*
 * @Author: NgojcDev
 */
import consts.ConstNpc;
import item.Item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import npc.Npc;
import player.Player;
import services.ConsignShopService;
import services.Input;
import services.InventoryService;
import services.NpcService;
import services.ShopService;

public class ToriBot_ThienSu extends Npc {

    public ToriBot_ThienSu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            createOtherMenu(player, 0,
                    "Xin chào, ta có một số vật phẩm đặc biệt cậu có muốn xem không?",
                    "Shop", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player pl, int select) {
        if (canOpenNpc(pl)) {
            switch (select) {
                case 0:
                    ShopService.gI().opendShop(pl, "TORI_THIENSU", false);
                    break;
            }
        }
    }
}
