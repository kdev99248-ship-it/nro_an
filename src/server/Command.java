package server;

import author_ngojc.DragonRun;
import bot.BotManager;
import control.boss.TrungThuEventManager;
import control.boss.TreasureUnderSeaManager;
import control.boss.SnakeWayManager;
import control.boss.RedRibbonHQManager;
import control.boss.OtherBossManager;
import control.boss.GasDestroyManager;
import control.boss.BrolyManager;
import control.boss.BossManager;
import consts.ConstNpc;
import managers.GiftCodeManager;
import item.Item;
import player.Pet;
import player.Player;
import network.SessionManager;
import services.ItemService;
import services.PetService;
import services.Service;
import services.Input;
import services.ChangeMapService;
import services.NpcService;
import services.InventoryService;
import utils.SystemMetrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import server.Client;
import services.TaskService;

public class Command {

    private static Command instance;

    private final Map<String, Consumer<Player>> adminCommands = new HashMap<>();
    private final Map<String, BiConsumer<Player, String>> parameterizedCommands = new HashMap<>();

    public static Command gI() {
        if (instance == null) {
            instance = new Command();
        }
        return instance;
    }

    private Command() {
        initAdminCommands();
        initParameterizedCommands();
    }

    private void initAdminCommands() {

        adminCommands.put("giftcode", player -> GiftCodeManager.gI().checkInfomationGiftCode(player));
        adminCommands.put("listboss", player -> BossManager.gI().showListBoss(player));
        adminCommands.put("lbb", player -> BrolyManager.gI().showListBoss(player));
        adminCommands.put("lbpb", player -> OtherBossManager.gI().showListBoss(player));
        adminCommands.put("lbdt", player -> RedRibbonHQManager.gI().showListBoss(player));
        adminCommands.put("lbbdkb", player -> TreasureUnderSeaManager.gI().showListBoss(player));
        adminCommands.put("lbcdrd", player -> SnakeWayManager.gI().showListBoss(player));
        adminCommands.put("lbkghd", player -> GasDestroyManager.gI().showListBoss(player));
        adminCommands.put("lbtt", player -> TrungThuEventManager.gI().showListBoss(player));
        adminCommands.put("vmn", player -> NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_ADMIN, -1,
                "|0|Time start: " + DragonRun.timeStart + "\nClients: " + Client.gI().getPlayers().size()
                + " người chơi\n Sessions: " + SessionManager.gI().getNumSession() + "\nThreads: " + Thread.activeCount()
                + " luồng" + "\n" + SystemMetrics.ToString(),
                "Ngọc rồng", "Đệ tử", "Bảo trì", "Tìm kiếm\nngười chơi", "Boss", "Gửi thư", "Đóng"));
        adminCommands.put("item", player -> Input.gI().createFormGiveItem(player));
        adminCommands.put("mail", player -> Input.gI().createFormSendMailToPlayer(player));
        adminCommands.put("getitem", player -> Input.gI().createFormGetItem(player));
        adminCommands.put("d", player -> Service.gI().setPos(player, player.location.x, player.location.y + 10));
    }

    private void initParameterizedCommands() {
        parameterizedCommands.put("us", (player, text) -> {
            Manager.gI().updateShop();
        });
        parameterizedCommands.put("m ", (player, text) -> {
            int mapId = Integer.parseInt(text.replace("m ", ""));
            ChangeMapService.gI().changeMapInYard(player, mapId, -1, -1);
        });
        parameterizedCommands.put("dev", (player, text) -> {
            NpcService.gI().createMenuConMeo(player, 206783, 206783, "|7| Menu bot\n"
                    + "Player online : " + Client.gI().getPlayers().size() + "\n"
                    + "Bot online : " + BotManager.gI().bot.size(),
                    "Bot\nPem Quái", "Bot\nBán Item", "Bot\nSăn Boss", "Đóng");
        });
        parameterizedCommands.put("vt", (player, text) -> {
            Service.gI().sendThongBaoOK(player, "x: " + player.location.x + " - y: " + player.location.y);
        });
        parameterizedCommands.put("n", (player, text) -> {
            int idTask = Integer.parseInt(text.replaceAll("n", ""));
            player.playerTask.taskMain.id = idTask - 1;
            player.playerTask.taskMain.index = 0;
            TaskService.gI().sendNextTaskMain(player);

        });
        parameterizedCommands.put("i ", (player, text) -> {
            try {
                // Tách tham số sau "i "
                String[] parts = text.trim().split("\\s+");
                if (parts.length < 2) {
                    Service.gI().sendThongBao(player, "Cú pháp: i <itemId> <số lượng>");
                    return;
                }

                int itemId = Integer.parseInt(parts[1]);
                int quantity = 1; // mặc định
                if (parts.length >= 3) {
                    quantity = Integer.parseInt(parts[2]);
                }

                // Tạo item
                Item item = ItemService.gI().createNewItem((short) itemId);
                item.quantity = quantity; // gán số lượng

                // Gắn option mặc định
                List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop((short) itemId);
                if (!ops.isEmpty()) {
                    item.itemOptions = ops;
                }

                // Thêm vào túi
                InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBags(player);

                // Thông báo
                Service.gI().sendThongBao(player, "GET " + item.template.name + " [" + item.template.id + "] x" + quantity + " SUCCESS !");
            } catch (Exception e) {
                Service.gI().sendThongBao(player, "Lỗi: " + e.getMessage());
            }
        });

    }

    public void chat(Player player, String text) {
        if (!check(player, text)) {
            Service.gI().chat(player, text);
        }
    }

    public boolean check(Player player, String text) {
        if (player.isAdmin()) {
            if (adminCommands.containsKey(text)) {
                adminCommands.get(text).accept(player);
                return true;
            }

            for (Map.Entry<String, BiConsumer<Player, String>> entry : parameterizedCommands.entrySet()) {
                if (text.startsWith(entry.getKey())) {
                    entry.getValue().accept(player, text);
                    return true;
                }
            }
        }

        if (text.startsWith("ten con la ")) {
            PetService.gI().changeNamePet(player, text.replaceAll("ten con la ", ""));
        }

        if (player.pet != null) {
            switch (text) {
                case "di theo", "follow" ->
                    player.pet.changeStatus(Pet.FOLLOW);
                case "bao ve", "protect" ->
                    player.pet.changeStatus(Pet.PROTECT);
                case "tan cong", "attack" ->
                    player.pet.changeStatus(Pet.ATTACK);
                case "ve nha", "go home" ->
                    player.pet.changeStatus(Pet.GOHOME);
                case "bien hinh" ->
                    player.pet.transform();
            }
        }
        return false;
    }
}
