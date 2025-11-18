package npc.list;

/*
 * @Author: NgojcDev
 */
import author_ngojc.DragonRun;
import consts.ConstNpc;
import consts.ConstTask;
import npc.Npc;
import player.Player;
import services.Input;
import services.TaskService;
import services.Service;
import services.ShopService;
import services.NpcService;
import services.InventoryService;
import author_ngojc.GoldBarService;
import database.PlayerDAO;

public class OngGohan extends Npc {

    long st = System.currentTimeMillis();

    public OngGohan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "|0|Chào con , Đến với: " + DragonRun.NAME + "\n", "Nhận Quà\nTân Thủ", "Bỏ Qua\nNhiệm Vụ", "Giftcode", "Thỏi vàng\n", "Mở\nThành Viên", "Hòm thư");

            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.idMark.isBaseMenu()) {
                switch (select) {
                    case 0 -> {
                        this.createOtherMenu(player, ConstNpc.QUA_TAN_THU,
                                "Chọn phần quà mà con muốn nhận:", "Nhận 5m\nNgọc Xanh");
                    }
                    case 1 -> {
                        // Bỏ qua nhiệm vụ
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Con muốn bỏ qua nhiệm vụ nào?", "Đại Hội\nVõ Thuật", "Trung Úy\nTrắng", "Thách Đấu\n10 Người", "Đóng");
                    }
                    case 2 -> {
                        Input.gI().createFormGiftCode(player);
                    }
                    case 3 -> {
                        int soThoiVang = GoldBarService.gI().laySoThoiVang(player);
                        int soVND = player.getSession().cash;
                        this.createOtherMenu(player, ConstNpc.MENU_THOI_VANG,
                                " Con muốn làm gì?",
                                "Quy Đổi\nThỏi Vàng",
                                "Rút\n[" + GoldBarService.gI().laySoThoiVang(player) + "]",
                                "Quay lại");
                    }
                    case 4 -> {
                        // Mở thành viên
                        if (player.getSession().actived) {
                            Service.gI().sendThongBao(player, "Con đã là thành viên rồi!");
                        } else {
                            this.createOtherMenu(player, ConstNpc.MENU_MO_THANH_VIEN,
                                    "Con muốn mở thành viên bằng cách nào?",
                                    "Tốn phí\n(20.000 VND)");
                        }
                    }
                    case 5 -> {
                        int mailCount = player.inventory.itemsMail.size() 
                            - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsMail);
                        this.createOtherMenu(player, ConstNpc.MENU_HOM_THU,
                                "Con muốn làm gì nào?",
                                "Hòm thư\n(" + mailCount + " món)", "Xóa hết\nHòm thư");
                    }
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.QUA_TAN_THU) {
                switch (select) {
                    case 0 -> {
                        // Xử lý nhận 5m ngọc xanh
                        player.inventory.gem = Math.min(player.inventory.gem + 5000000, 200000000);
                        Service.gI().sendMoney(player);
                        Service.gI().sendThongBao(player, "Chúc mừng! Bạn đã nhận được 5 triệu ngọc xanh!");
                    }
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.IGNORE_MENU) {
                int currentTaskId = TaskService.gI().getIdTask(player);
                switch (select) {
                    case 0 -> {
                        // Bỏ qua nhiệm vụ Đại Hội Võ Thuật [18,1,0,0]
                        if (currentTaskId == ConstTask.TASK_18_1) {
                            TaskService.gI().doneTask(player, ConstTask.TASK_18_1);
                            Service.gI().sendThongBao(player, "Đã bỏ qua nhiệm vụ Đại Hội Võ Thuật!");
                        } else {
                            Service.gI().sendThongBao(player, "Con không đang làm nhiệm vụ Đại Hội Võ Thuật!");
                        }
                    }
                    case 1 -> {
                        // Bỏ qua nhiệm vụ Trung Úy Trắng [19,1,0,0]
                        if (currentTaskId == ConstTask.TASK_19_1) {
                            TaskService.gI().doneTask(player, ConstTask.TASK_19_1);
                            Service.gI().sendThongBao(player, "Đã bỏ qua nhiệm vụ Trung Úy Trắng!");
                        } else {
                            Service.gI().sendThongBao(player, "Con không đang làm nhiệm vụ Trung Úy Trắng!");
                        }
                    }
                    case 2 -> {
                        if (currentTaskId == ConstTask.TASK_16_0) {
                            TaskService.gI().doneTask(player, ConstTask.TASK_16_0);
                            Service.gI().sendThongBao(player, "Đã bỏ qua nhiệm vụ Thách Đấu 10 Người!");
                        } else {
                            Service.gI().sendThongBao(player, "Con không đang làm nhiệm vụ Thách Đấu 10 Người!");
                        }
                    }
                    case 3 -> {
                        // Đóng menu
                        return;
                    }
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_THOI_VANG) {
                // Xử lý menu thỏi vàng
                switch (select) {
                    case 0 -> {
                        int soVND = player.getSession().cash;
                        if (soVND < 10000) {
                            Service.gI().sendThongBao(player, "Bạn cần ít nhất 10.000 VND để quy đổi!");
                            return;
                        }
                        this.createOtherMenu(player, ConstNpc.MENU_QUY_DOI_THOI_VANG,
                                "Số tiền hiện có: " + GoldBarService.gI().dinhDangSo(soVND)
                                + "\nChọn gói quy đổi:",
                                "10K VND\n25 thỏi", "20K VND\n50 thỏi", "50K VND\n125 thỏi",
                                "100K VND\n250 thỏi", "200K VND\n500 thỏi", "500K VND\n1300 thỏi",
                                "1M VND\n2750 thỏi", "Quay lại");
                    }
                    case 1 -> {
                        int soThoiVang = GoldBarService.gI().laySoThoiVang(player);
                        if (soThoiVang <= 0) {
                            Service.gI().sendThongBao(player, "Bạn không có thỏi vàng nào!");
                            return;
                        }
                        Input.gI().createFormWithdrawGoldBar(player);
                    }
                    case 2 -> {
                        // Quay lại menu chính
                        openBaseMenu(player);
                    }
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_QUY_DOI_THOI_VANG) {
                switch (select) {
                    case 0 ->
                        GoldBarService.gI().quiDoiVNDThanhThoiVang(player, 0);
                    case 1 ->
                        GoldBarService.gI().quiDoiVNDThanhThoiVang(player, 1);
                    case 2 ->
                        GoldBarService.gI().quiDoiVNDThanhThoiVang(player, 2);
                    case 3 ->
                        GoldBarService.gI().quiDoiVNDThanhThoiVang(player, 3);
                    case 4 ->
                        GoldBarService.gI().quiDoiVNDThanhThoiVang(player, 4);
                    case 5 ->
                        GoldBarService.gI().quiDoiVNDThanhThoiVang(player, 5);
                    case 6 ->
                        GoldBarService.gI().quiDoiVNDThanhThoiVang(player, 6);
                    case 7 -> {
                        int soThoiVang = GoldBarService.gI().laySoThoiVang(player);
                        int soVND = player.getSession().cash;
                        this.createOtherMenu(player, ConstNpc.MENU_THOI_VANG,
                                "VND hiện có: " + GoldBarService.gI().dinhDangSo(soVND)
                                + "\nThỏi vàng: " + soThoiVang + "\nCon muốn làm gì?",
                                "Quy đổi\nVND -> Thỏi vàng",
                                "Rút thỏi vàng\nra hành trang",
                                "Quay lại");
                    }
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_MO_THANH_VIEN) {
                // Xử lý menu mở thành viên
                switch (select) {
                    case 0 -> {
                        // Mở thành viên tốn phí (20.000 VND)
                        if (player.getSession().cash < 20000) {
                            Service.gI().sendThongBao(player, "Con cần ít nhất 20.000 VND để mở thành viên!");
                            return;
                        }
                        if (PlayerDAO.subcash(player, 20000)) {
                            if (PlayerDAO.activateAccount(player)) {
                                Service.gI().sendThongBao(player, "Chúc mừng! Con đã trở thành thành viên!");
                                Service.gI().sendMoney(player);
                            } else {
                                // Hoàn lại tiền nếu lỗi
                                PlayerDAO.addcash(player, 20000);
                                Service.gI().sendThongBao(player, "Có lỗi xảy ra, vui lòng thử lại!");
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Không đủ tiền để mở thành viên!");
                        }
                    }
                    case 1 -> {
                        // Mở thành viên miễn phí (cần hoàn thành task 22)
                        int currentTaskId = TaskService.gI().getIdTask(player);
                        if (currentTaskId >= ConstTask.TASK_22_20) {
                            if (PlayerDAO.activateAccount(player)) {
                                Service.gI().sendThongBao(player, "Chúc mừng! Con đã trở thành thành viên miễn phí!");
                            } else {
                                Service.gI().sendThongBao(player, "Có lỗi xảy ra, vui lòng thử lại!");
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Con cần hoàn thành đến nhiệm vụ tiểu đội sát thủ để mở thành viên miễn phí!");
                        }
                    }
                    case 2 -> {
                        // Quay lại menu chính
                        openBaseMenu(player);
                    }
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_HOM_THU) {
                // Xử lý menu hòm thư
                switch (select) {
                    case 0 -> {
                        // Mở hòm thư
                        ShopService.gI().opendShop(player, "ITEMS_MAIL", true);
                    }
                    case 1 -> {
                        // Xóa hết hòm thư
                        NpcService.gI().createMenuConMeo(player,
                                ConstNpc.CONFIRM_REMOVE_ALL_ITEM_MAIL, this.avartar,
                                "Con có chắc muốn xóa hết thư trong hòm thư? Sau khi xóa sẽ không thể khôi phục!",
                                "Đồng ý", "Hủy bỏ");
                    }
                }
            }
        }
    }

}
