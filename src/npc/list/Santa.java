package npc.list;

/*
 * @Author: NgojcDev
 */
import consts.ConstNpc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import npc.Npc;
import player.Player;
import services.Input;
import services.ShopService;

public class Santa extends Npc {

    public Santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {

            List<String> menu = new ArrayList<>(Arrays.asList(
                    "Cửa hàng",
                    "Mở rộng\nHành trang\nRương đồ",
                    "Nhập mã\nquà tặng",
                    "Cửa hàng\nHạn sử dụng",
                    "Tiệm\nHớt tóc"));

            String[] menus = menu.toArray(new String[0]);

            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Xin chào, ta có một số vật phẩm đặc biệt cậu có muốn xem không?", menus);
        }

    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {

            if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                if (player.idMark.isBaseMenu()) {
                    switch (select) {
                        case 0 ->
                            ShopService.gI().opendShop(player, "SANTA", false);
                        case 1 ->
                            ShopService.gI().opendShop(player, "SANTA_MO_RONG_HANH_TRANG", false);
                        case 2 ->
                            Input.gI().createFormGiftCode(player);
                        case 3 ->
                            ShopService.gI().opendShop(player, "SANTA_HAN_SU_DUNG", false);
                        case 4 ->
                            ShopService.gI().opendShop(player, "SANTA_HEAD", false);
                        case 5 ->
                            ShopService.gI().opendShop(player, "SANTA_DANH_HIEU", false);
                        case 6 ->
                            ShopService.gI().opendShop(player, "SANTA_DANH_HIEU", false);
                    }
                }
            }
        }
    }
}
