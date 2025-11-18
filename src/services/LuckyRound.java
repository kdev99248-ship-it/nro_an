package services;

/*
 * @Author: NgojcDev
 */

import item.Item;
import java.io.IOException;
import player.Player;
import network.Message;
import java.util.List;

public class LuckyRound {

    private static final byte MAX_ITEM_IN_BOX = 100;

    public static final byte USING_GOLD_BAR = 0;

    private static final int PRICE_GOLD_BAR = 1;
    private static final int GOLD_BAR = 457;

    private static LuckyRound instance;

    public static LuckyRound gI() {
        if (instance == null) {
            instance = new LuckyRound();
        }
        return instance;
    }

    public void openCrackBallUI(Player pl, byte type) {
        pl.idMark.setTypeLuckyRound(type);
        Message msg = null;
        try {
            msg = new Message(-127);
            msg.writer().writeByte(0);
            msg.writer().writeByte(7);
            for (int i = 0; i < 7; i++) {
                msg.writer().writeShort(4028);
            }
            msg.writer().writeByte(type); // type price
            msg.writer().writeInt(PRICE_GOLD_BAR); // price
             msg.writer().writeShort(GOLD_BAR);
            pl.sendMessage(msg);
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }


    public void readOpenBall(Player player, Message msg) {
        try {
            msg.reader().readByte(); // type
            byte count = msg.reader().readByte();
            switch (player.idMark.getTypeLuckyRound()) {
                case USING_GOLD_BAR:
                    openBallByGoldBar(player, count);
                    break;
            }
        } catch (Exception e) {
            switch (player.idMark.getTypeLuckyRound()) {
                default:
                    openCrackBallUI(player, player.idMark.getTypeLuckyRound());
                    break;
            }
        }
    }


    private void openBallByGoldBar(Player player, byte count) {
        int goldBarNeed = (count * PRICE_GOLD_BAR);
        Item goldBar = InventoryService.gI().findItemBag(player, GOLD_BAR);
        if (goldBar == null || goldBar.quantity < goldBarNeed) {
            Service.gI().sendThongBao(player, "Bạn không đủ thỏi vàng để quay");
        } else {
            if (count + player.inventory.itemsBoxCrackBall.size() <= MAX_ITEM_IN_BOX) {
                InventoryService.gI().subQuantityItemsBag(player, goldBar, goldBarNeed);
                InventoryService.gI().sendItemBags(player);
                List<Item> list = RewardService.gI().getListItemLuckyRound(player, count, false);
                addItemToBox(player, list);
                sendReward(player, list);
                Service.gI().sendMoney(player);
            } else {
                Service.gI().sendThongBao(player, "Rương phụ đã đầy");
            }
        }
    }


    private void sendReward(Player player, List<Item> items) {
        Message msg = null;
        try {
            msg = new Message(-127);
            msg.writer().writeByte(1);
            msg.writer().writeByte(items.size());
            for (Item item : items) {
                msg.writer().writeShort(item.template.iconID);
            }
            player.sendMessage(msg);
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void addItemToBox(Player player, List<Item> items) {
        player.inventory.itemsBoxCrackBall.addAll(items);
    }
}
