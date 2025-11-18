package server;

/*
 * @Author: NgojcDev
 */

import author_ngojc.DragonRun;
import player.Player;
import network.Message;
import services.Service;
import utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServerNotify extends Thread {

    private long lastNotifyTime;
    private final List<String> notifies;
    // private int indexNotify; // không cần dùng nữa nếu random toàn bộ
    private final String notify[] = {
            "" + DragonRun.NOTIFY_1 + "",
            "" + DragonRun.NOTIFY_2 + "",
            "" + DragonRun.NOTIFY_3 + ""
    };
    private static ServerNotify instance;
    private final Random random = new Random();

    private ServerNotify() {
        this.notifies = new ArrayList<>();
        this.start();
    }

    public static ServerNotify gI() {
        if (instance == null) {
            instance = new ServerNotify();
        }
        return instance;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                if (Util.canDoWithTime(this.lastNotifyTime, 1000)) {
                    // Chọn thông báo ngẫu nhiên từ mảng notify
                    int randomIndex = random.nextInt(notify.length);
                    sendChatVip(notify[randomIndex]);
                    this.lastNotifyTime = System.currentTimeMillis();
                    // Nếu vẫn muốn duy trì cơ chế vòng lặp cho notifies thì giữ nguyên phần này
                }
                if (!notifies.isEmpty()) {
                    sendChatVip(notifies.remove(0));
                }
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void sendChatVip(String text) {
        Message msg;
        try {
            msg = new Message(93);
            msg.writer().writeUTF(text);
            Service.gI().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
            // Xử lý ngoại lệ nếu cần
        }
    }

    public void notify(String text) {
        this.notifies.add(text);
    }

    public void sendNotifyTab(Player player) {
        Message msg;
        try {
            msg = new Message(50);
            msg.writer().writeByte(10);
            for (int i = 0; i < Manager.NOTIFY.size(); i++) {
                String[] arr = Manager.NOTIFY.get(i).split("<>");
                msg.writer().writeShort(i);
                msg.writer().writeUTF(arr[0]);
                msg.writer().writeUTF(arr[1]);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception ignored) {
        }
    }
}
