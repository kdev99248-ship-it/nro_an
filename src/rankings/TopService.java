package rankings;

import author_ngojc.TextServer;
import database.AlyraManager;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import network.Message;
import player.Player;
import server.Manager;
import services.TaskService;
import utils.Logger;
import utils.Util;

public class TopService {

    private static TopService instance;

    public static TopService gI() {
        if (instance == null) {
            instance = new TopService();
        }
        return instance;
    }

    public void updateTop() {
        if (Manager.timeRealTop + (10 * 60 * 1000) < System.currentTimeMillis()) {
            Manager.timeRealTop = System.currentTimeMillis();
            try (Connection conn = AlyraManager.getConnection_Data()) {
                Manager.topNV = Manager.realTop(TextServer.TOP_NV, conn);
                Manager.topWHIS = Manager.realTop(TextServer.TOP_WHIS, conn);
                Manager.topSM = Manager.realTop(TextServer.TOP_SM, conn);
                Manager.topNap = Manager.realTop(TextServer.TOP_NAP, conn);
            } catch (Exception ignored) {
                Logger.error("Lỗi đọc top");
            }
        }
    }


//    public static void showListTopTask(Player player) {
//        TopTaskManager.getInstance().load();
//        List<Player> list = TopTaskManager.getInstance().getList();
//        Message msg = null;
//        try {
//            msg = new Message(-96);
//            msg.writer().writeByte(0);
//            msg.writer().writeUTF("Top 100");
//            msg.writer().writeByte(list.size());
//            for (int i = 0; i < list.size(); i++) {
//                Player top = list.get(i);
//                msg.writer().writeInt(i + 1);
//                msg.writer().writeInt(i + 1);
//                msg.writer().writeShort(top.getHead());
//
//                if (player.getSession().version >= 214) {
//                    msg.writer().writeShort(-1);
//                }
//                msg.writer().writeShort(top.getBody());
//                msg.writer().writeShort(top.getLeg());
//                msg.writer().writeUTF(top.name);
//                msg.writer().writeUTF(NDVSqlFetcher.loadById(top.id).playerTask.taskMain.name);
//                msg.writer().writeUTF("...");
//            }
//            player.sendMessage(msg);
//            msg.cleanup();
//        } catch (IOException e) {
//        } finally {
//            if (msg != null) {
//                msg.cleanup();
//            }
//        }
//    }

    public static String getTopNap() {
        StringBuffer sb = new StringBuffer("");
        PreparedStatement ps;
        ResultSet rs;
        try {
            Connection conn = AlyraManager.getConnection_Data();
            ps = conn.prepareStatement(TextServer.TOP_NAP);
            conn.setAutoCommit(false);

            rs = ps.executeQuery();
            byte i = 1;
            while (rs.next()) {
                sb.append(i).append(".").append(rs.getString("name")).append(": ").append(rs.getString("danap")).append(" Đã Nạp\b");
                i++;
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static String getTopSM() {
        StringBuffer sb = new StringBuffer("");
        PreparedStatement ps;
        ResultSet rs;
        try {
            Connection conn = AlyraManager.getConnection();
            ps = conn.prepareStatement(TextServer.TOP_SM);
            conn.setAutoCommit(false);

            rs = ps.executeQuery();
            byte i = 1;
            while (rs.next()) {
                sb.append(i).append(".").append(rs.getString("name")).append(": ").append(rs.getString("sm")).append(" Sức Mạnh\b");
                i++;
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static void showListTop(Player player, int select) {
        List<TOP> tops = Manager.topNV;
        switch (select) {
            case 0 -> tops = Manager.topNV;
            case 1 -> tops = Manager.topSM;
            case 2 -> tops = Manager.topWHIS;
            case 3 -> tops = Manager.topNap;
            case 4 -> {
                try {
                    Manager.topHalloween = Manager.realTop(TextServer.HALLOWEEN, AlyraManager.getConnection());
                    tops = Manager.topHalloween;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                switch (select) {
                    case 0 -> {
                        msg.writer().writeUTF(TaskService.gI().getTaskMainById(player, top.getNv()).name.substring(0, TaskService.gI().getTaskMainById(player, top.getNv()).name.length() > 20 ? 20 : TaskService.gI().getTaskMainById(player, top.getNv()).name.length()) + "...");
                        msg.writer().writeUTF(TaskService.gI().getTaskMainById(player, top.getNv()).subTasks.get(top.getSubnv()).name + " - " + getTimeLeft(top.getLasttime()));
                    }
                    case 1 -> {
                        msg.writer().writeUTF("" + Util.numberToMoney(top.getPower()) + " Sức mạnh");
                        msg.writer().writeUTF("" + top.getPower() + " Sức mạnh");
                    }
                    case 2 -> {
                        msg.writer().writeUTF("LV:" + top.getLevel() + " với " + Util.roundToTwoDecimals(top.getTime() / 1000d) + " giây");
                        msg.writer().writeUTF(getTimeLeft(top.getLasttime()));
                    }
                    case 3 -> {
                        msg.writer().writeUTF("" + Util.numberToMoney(top.getCash()) + " VNĐ");
                        msg.writer().writeUTF("" + top.getCash() + " VNĐ");
                    }
                    case 4 -> {
                        msg.writer().writeUTF("" + Util.numberToMoney(top.getHlw()) + " Điểm");
                        msg.writer().writeUTF("" + top.getHlw() + " Điểm");
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static String getTimeLeft(long lastTime) {
        int secondsPassed = (int) ((System.currentTimeMillis() - lastTime) / 1000);

        if (secondsPassed > 86400) {
            return (secondsPassed / 86400) + " ngày trước";
        } else if (secondsPassed > 3600) {
            return (secondsPassed / 3600) + " giờ trước";
        } else if (secondsPassed > 60) {
            return (secondsPassed / 60) + " phút trước";
        } else {
            return secondsPassed + " giây trước";
        }
    }

}
