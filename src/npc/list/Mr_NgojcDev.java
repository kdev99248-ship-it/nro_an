package npc.list;

/*
 * @Author: NgojcDev
 */
import author_ngojc.DragonRun;
import consts.ConstNpc;
import database.PlayerDAO;
import item.Item;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import services.TaskService;
import rankings.TopService;
import utils.Util;

public class Mr_NgojcDev extends Npc {

    public Mr_NgojcDev(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "|0| Xin Chào. Tôi Là NgojcDev Người Build Source Này.\n Cư Dân Muốn Hỗ Trợ Gì Không ?",
                        "Website\nGame", "Group\nZalo", "Trade",
                        "Top Power\n",
                        "Top Task\n",
                        "Top Money");

            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.idMark.isBaseMenu()) {
                switch (select) {
                    case 0 ->
                        Service.gI().LinkService(player, 14333, "|0|" + DragonRun.NAME + "\n\n",
                                "" + DragonRun.LINK + "", "Open");
                    case 1 ->
                        Service.gI().LinkService(player, 14333, "|0|" + DragonRun.NAME + "\n\n",
                                "" + DragonRun.ZALO + "", "Open");
                    case 2 ->
                        openDoiNgocMenu(player); // Mở menu đổi vàng
                    case 3 ->
                        TopService.showListTop(player, 1);
                    case 4 ->
                        TopService.showListTop(player, 0);
                    case 5 ->
                        TopService.showListTop(player, 3);
                }
            } else if (player.idMark.getIndexMenu() == 3) {
                DoiNgoc(player, select);
            }
        }
    }

    void openDoiNgocMenu(Player player) {
        this.createOtherMenu(player, 3, "\n|0|VND: " + player.getSession().vnd + "",
                "10K VNĐ\n50\nNgọc Xanh",
                "20K VNĐ\n100\nNgọc Xanh",
                "50K VNĐ\n300\nNgọc Xanh",
                "100K VNĐ\n700\nNgọc Xanh",
                "200K VNĐ\n1800\nNgọc Xanh",
                "500K VNĐ\n4500\nNgọc Xanh",
                "1TR VNĐ\n9500\nNgọc Xanh",
                "5TR VNĐ\n53000\nNgọc Xanh",
                "10TR VNĐ\n110000\nNgọc Xanh",
                "Đóng");
    }
    // Đổi vàng

    void DoiNgoc(Player player, int select) {
        int[] vietnamdong = { 10_000, 20_000, 50_000, 100_000, 200_000, 500_000, 1_000_000, 5_000_000, 10_000_000 };
        int[] gemnhanduoc = { 50, 100, 300, 700, 1800, 4500, 9500, 53000, 110000 };

        // Kiểm tra chỉ mục hợp lệ
        if (select < 0 || select >= vietnamdong.length) {
            Service.gI().sendThongBao(player, "Lựa chọn không hợp lệ!");
            return;
        }

        synchronized (player) { // Đảm bảo an toàn khi giao dịch
            if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
                Service.gI().sendThongBao(player, "Hành trang của bạn đã đầy!");
                return;
            }

            int mney = vietnamdong[select];
            if (player.getSession().vnd < mney) {
                Service.gI().sendThongBao(player, "Bạn còn thiếu " + Util.formatNumber(mney - player.getSession().vnd)
                        + " VNĐ để thực hiện giao dịch!");
                return;
            }

            // Tiến hành trừ tiền
            int gem = gemnhanduoc[select];
            int coinAmount = (mney / 1_000) * 2; // Mỗi 1k VNĐ đổi thành 2 item Coins

            PlayerDAO.subvnd(player, mney);
            player.getSession().vnd -= mney;

            // Tạo vật phẩm Ngọc Xanh và Item Coins
            Item ngoc = ItemService.gI().createNewItem((short) 77, gem);
            Item coins = ItemService.gI().createNewItem((short) 1999, coinAmount);

            // Thêm vào túi đồ
            InventoryService.gI().addItemBag(player, ngoc);
            InventoryService.gI().addItemBag(player, coins);
            InventoryService.gI().sendItemBags(player);

            // Thông báo thành công
            Service.gI().sendThongBao(player, "Bạn đã đổi thành công " + Util.formatNumber(mney) + " VNĐ thành "
                    + Util.formatNumber(gem) + " Ngọc Xanh và " + coinAmount + " Coins!");
            if (mney >= 1_000_000) {
            }
        }
    }
}
