package services;

import author_ngojc.DragonRun;
import consts.ConstNpc;
import item.Item;
import player.Player;
import utils.Util;

public class SummonDragonXuong implements Runnable {
    private static SummonDragonXuong I;

    public boolean hasPlayerSummonDragonXuong;

    public long playerIdSummonDragonXuong;

    public long TIME_WAIT_DRAGON_XUONG = 3 * 60 * 1000;

    public long lastTimeCallDragonXuong;

    public static SummonDragonXuong gI() {
        if (I == null) {
            I = new SummonDragonXuong();
        }
        return I;
    }

    @Override
    public void run() {
        while (DragonRun.isRunning) {
            try {
                update();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (Util.canDoWithTime(lastTimeCallDragonXuong, TIME_WAIT_DRAGON_XUONG) && hasPlayerSummonDragonXuong && playerIdSummonDragonXuong != -1) {
            hasPlayerSummonDragonXuong = false;
            playerIdSummonDragonXuong = -1;
        }
    }

    public void summonDragonXuong(Player pl, Item item) {
        if (hasPlayerSummonDragonXuong && playerIdSummonDragonXuong == pl.id) {
            createMenuRongXuong(pl);
            return;
        }
        // find ngoc rong
        if (item.template.id != 702) {
            Service.gI().sendThongBao(pl, "Cần ngọc rồng bí ngô 1 sao để ước");
            return;
        }
        // find 6 vien con lai
        Item star2 = InventoryService.gI().findItemBag(pl, 703);
        Item star3 = InventoryService.gI().findItemBag(pl, 704);
        Item star4 = InventoryService.gI().findItemBag(pl, 705);
        Item star5 = InventoryService.gI().findItemBag(pl, 706);
        Item star6 = InventoryService.gI().findItemBag(pl, 707);
        Item star7 = InventoryService.gI().findItemBag(pl, 708);
        if (star2 == null) {
            Service.gI().sendThongBao(pl, "Không tìm thấy ngọc rồng bí ngô 2 sao");
            return;
        }
        if (star3 == null) {
            Service.gI().sendThongBao(pl, "Không tìm thấy ngọc rồng bí ngô 3 sao");
            return;
        }
        if (star4 == null) {
            Service.gI().sendThongBao(pl, "Không tìm thấy ngọc rồng bí ngô 4 sao");
            return;
        }
        if (star5 == null) {
            Service.gI().sendThongBao(pl, "Không tìm thấy ngọc rồng bí ngô 5 sao");
            return;
        }
        if (star6 == null) {
            Service.gI().sendThongBao(pl, "Không tìm thấy ngọc rồng bí ngô 6 sao");
            return;
        }
        if (star7 == null) {
            Service.gI().sendThongBao(pl, "Không tìm thấy ngọc rồng bí ngô 7 sao");
            return;
        }
        if (hasPlayerSummonDragonXuong) {
            Service.gI().sendThongBao(pl, "Đang có người gọi rồng xương hãy thử lại sau");
            return;
        }
        InventoryService.gI().subQuantityItemsBag(pl, star2, 1);
        InventoryService.gI().subQuantityItemsBag(pl, star3, 1);
        InventoryService.gI().subQuantityItemsBag(pl, star4, 1);
        InventoryService.gI().subQuantityItemsBag(pl, star5, 1);
        InventoryService.gI().subQuantityItemsBag(pl, star6, 1);
        InventoryService.gI().subQuantityItemsBag(pl, star7, 1);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        lastTimeCallDragonXuong = System.currentTimeMillis();
        playerIdSummonDragonXuong = pl.id;
        hasPlayerSummonDragonXuong = true;
        createMenuRongXuong(pl);
    }

    private void createMenuRongXuong(Player pl) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Điều ước rồng Xương").append("\n");
        stringBuilder.append("|5|Hãy chọn một điều ước nếu quá 3 phút sẽ ko còn linh nghiệm").append("\n");
        String[] select = new String[]{"Nâng skill\n2 đệ tử", "Nâng skill\n3 đệ tử", "20\nthỏi vàng", "Skin\nHalloween"};
        NpcService.gI().createMenuConMeo(pl, ConstNpc.MENU_UOC_RONG_XUONG, -1, stringBuilder.toString(), select);
    }

    public boolean canHandleWish(Player player) {
        return !Util.canDoWithTime(lastTimeCallDragonXuong, TIME_WAIT_DRAGON_XUONG);
    }
}
