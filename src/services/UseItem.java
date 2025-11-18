package services;

/*
 * @Author: NgojcDev
 */

import boss.miniboss.Xinbato;
import boss.miniboss.SoiHecQuyn;
import database.PlayerDAO;
import services.SummonDragon;
import services.Input;
import combine.CombineService;
import consts.BossID;
import services.ShenronEventService;
import radar.Card;
import services.RadarService;
import radar.RadarCard;
import consts.ConstMap;
import item.Item;
import consts.ConstNpc;
import consts.ConstItem;
import consts.ConstPlayer;
import control.boss.Boss;
import item.Item.ItemOption;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import map.ItemMap;
import map.Zone;
import player.Inventory;
import player.Player;
import skill.Skill;
import network.Message;
import utils.Rationalities;
import utils.SkillUtil;
import services.Service;
import utils.Util;
import network.MySession;
import npc.MabuEgg;
import services.ItemService;
import services.ItemTimeService;
import services.PetService;
import services.TaskService;
import services.NgocRongNamecService;
import utils.Logger;

public class UseItem {

    private static final int ITEM_BOX_TO_BODY_OR_BAG = 0;
    private static final int ITEM_BAG_TO_BOX = 1;
    private static final int ITEM_BODY_TO_BOX = 3;
    private static final int ITEM_BAG_TO_BODY = 4;
    private static final int ITEM_BODY_TO_BAG = 5;
    private static final int ITEM_BAG_TO_PET_BODY = 6;
    private static final int ITEM_BODY_PET_TO_BAG = 7;

    private static final byte DO_USE_ITEM = 0;
    private static final byte DO_THROW_ITEM = 1;
    private static final byte ACCEPT_THROW_ITEM = 2;
    private static final byte ACCEPT_USE_ITEM = 3;

    private static UseItem instance;

    private UseItem() {

    }

    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    public void getItem(MySession session, Message msg) {
        Player player = session.player;
        if (player == null) {
            return;
        }
        TransactionService.gI().cancelTrade(player);
        try {
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();
            if (index == -1) {
                return;
            }
            switch (type) {
                case ITEM_BOX_TO_BODY_OR_BAG:
                    InventoryService.gI().itemBoxToBodyOrBag(player, index);
                    TaskService.gI().checkDoneTaskGetItemBox(player);
                    break;
                case ITEM_BAG_TO_BOX:
                    InventoryService.gI().itemBagToBox(player, index);
                    break;
                case ITEM_BODY_TO_BOX:
                    InventoryService.gI().itemBodyToBox(player, index);
                    break;
                case ITEM_BAG_TO_BODY:
                    InventoryService.gI().itemBagToBody(player, index);
                    break;
                case ITEM_BODY_TO_BAG:
                    InventoryService.gI().itemBodyToBag(player, index);
                    break;
                case ITEM_BAG_TO_PET_BODY:
                    InventoryService.gI().itemBagToPetBody(player, index);
                    break;
                case ITEM_BODY_PET_TO_BAG:
                    InventoryService.gI().itemPetBodyToBag(player, index);
                    break;
            }
            if (player.setClothes != null) {
                player.setClothes.setup();
            }
            if (player.pet != null) {
                player.pet.setClothes.setup();
            }
            player.setClanMember();
            Service.gI().sendFlagBag(player);
            Service.gI().point(player);
            Service.gI().sendSpeedPlayer(player, -1);
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);

        }
    }

    public Item finditem(Player player, int iditem) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == iditem) {
                return item;
            }
        }
        return null;
    }

    public void doItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg = null;
        byte type;
        try {
            type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            switch (type) {
                case DO_USE_ITEM:
                    if (player != null && player.inventory != null) {
                        if (index != -1) {
                            if (index < 0) {
                                return;
                            }
                            Item item = player.inventory.itemsBag.get(index);
                            if (item.isNotNullItem()) {
                                if (item.template.type == 7) {
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc chắn học "
                                            + player.inventory.itemsBag.get(index).template.name + "?");
                                    player.sendMessage(msg);
                                } else if (item.template.id == 570) {
                                    if (!Util.isAfterMidnight(player.lastTimeRewardWoodChest)) {
                                        Service.gI().sendThongBao(player, "Hãy chờ đến ngày mai");
                                        return;
                                    }
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc muốn mở\n"
                                            + player.inventory.itemsBag.get(index).template.name + " ?");
                                    player.sendMessage(msg);
                                } else if (item.template.type == 22) {
                                    if (player.zone.items.stream()
                                            .filter(it -> it != null && it.itemTemplate.type == 22).count() > 2) {
                                        Service.gI().sendThongBaoOK(player, "Mỗi map chỉ đặt được 3 Vệ Tinh");
                                        return;
                                    }
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc muốn dùng\n"
                                            + player.inventory.itemsBag.get(index).template.name + " ?");
                                    player.sendMessage(msg);
                                } else {
                                    UseItem.gI().useItem(player, item, index);
                                }
                            }
                        } else {
                            int iditem = _msg.reader().readShort();
                            Item item = finditem(player, iditem);
                            UseItem.gI().useItem(player, item, index);
                        }
                    }
                    break;
                case DO_THROW_ITEM:
                    if (!(player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23)) {
                        Item item = null;
                        if (index < 0) {
                            return;
                        }
                        if (where == 0) {
                            item = player.inventory.itemsBody.get(index);
                        } else {
                            item = player.inventory.itemsBag.get(index);
                        }

                        if (item.isNotNullItem() && item.template.id == 570) {
                            Service.gI().sendThongBao(player, "Không thể bỏ vật phẩm này.");
                            return;
                        }
                        if (!item.isNotNullItem()) {
                            return;
                        }
                        msg = new Message(-43);
                        msg.writer().writeByte(type);
                        msg.writer().writeByte(where);
                        msg.writer().writeByte(index);
                        msg.writer().writeUTF("Bạn chắc chắn muốn vứt " + item.template.name + "?");
                        player.sendMessage(msg);
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case ACCEPT_THROW_ITEM:
                    InventoryService.gI().throwItem(player, where, index);
                    Service.gI().point(player);
                    InventoryService.gI().sendItemBags(player);
                    break;
                case ACCEPT_USE_ITEM:
                    UseItem.gI().useItem(player, player.inventory.itemsBag.get(index), index);
                    break;
            }
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void useItem(Player pl, Item item, int indexBag) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 570) {
                if (!Util.isAfterMidnight(pl.lastTimeRewardWoodChest)) {
                    Service.gI().sendThongBao(pl, "Hãy chờ đến ngày mai");
                } else {
                    openRuongGo(pl);
                }
                return;
            }
            if (item.template.strRequire <= pl.nPoint.power) {
                switch (item.template.type) {
                    case 33: // card
                        UseCard(pl, item);
                        break;
                    case 7: // sách học, nâng skill
                        learnSkill(pl, item);
                        break;
                    case 6: // đậu thần
                        this.eatPea(pl);
                        break;
                    case 12: // ngọc rồng các loại
                        controllerCallRongThan(pl, item);
                        break;
                    case 23: // thú cưỡi mới
                    case 24: // thú cưỡi cũ
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        break;
                    case 11: // item bag
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendFlagBag(pl);
                        break;
                    case 72: {
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendPetFollow(pl, (short) (item.template.iconID - 1));
                        break;
                    }
                    case 99:
                    case 98: {
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendEffPlayer(pl);
                        break;
                    }
                    default:
                        switch (item.template.id) {
                            case 1538:
                                Rationalities.openHopQuaKichHoatVip(pl, item);
                                break;
                            case 1635:
                                ItemTimeService.gI().useCoBonLa(pl, item);
                                break;
                            case 702:
                                SummonDragonXuong.gI().summonDragonXuong(pl, item);
                                break;
                            case 1116:
                                // mo hom halloween
                                UseItem.gI().openHalloWeenChest(pl, item);
                                break;
                            case 1728:
                                UseItem.gI().openTuiMuHalloween(pl, item);
                                break;
                            case 457: // Thỏi vàng - hiện menu bán SLL
                                Input.gI().createFormBanSLL(pl);
                                return;
                            case 992: // Nhan thoi khong
                                pl.type = 2;
                                pl.maxTime = 5;
                                Service.gI().Transport(pl);
                                break;
                            case 361:
                                pl.idGo = (short) Util.nextInt(0, 6);
                                NgocRongNamecService.gI().menuCheckTeleNamekBall(pl);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                InventoryService.gI().sendItemBags(pl);
                                break;
                            case 1778:
                                openHopQuaThanLinh(pl, item, indexBag);
                                break;
                            case 1790:
                                openCaiTrangHit(pl, item);
                                break;
                            case 892:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 882, 883, 884);
                                Service.gI().point(pl);
                                break;
                            case 893:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 885, 886, 887);
                                Service.gI().point(pl);
                                break;
                            case 908:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 891, 892, 893);
                                Service.gI().point(pl);
                                break;
                            case 909:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 894, 895, 896);
                                Service.gI().point(pl);
                                break;
                            case 910:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 897, 898, 899);
                                Service.gI().point(pl);
                                break;
                            case 916:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 925, 926, 927);
                                Service.gI().point(pl);
                                break;
                            case 917:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 928, 929, 930);
                                Service.gI().point(pl);
                                break;
                            case 918:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 931, 932, 933);
                                Service.gI().point(pl);
                                break;
                            case 919:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 934, 935, 936);
                                Service.gI().point(pl);
                                break;
                            case 936:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 718, 719, 720);
                                Service.gI().point(pl);
                                break;
                            case 942:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 966, 967, 968);
                                Service.gI().point(pl);
                                break;
                            case 943:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 969, 970, 971);
                                Service.gI().point(pl);
                                break;
                            case 944:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 972, 973, 974);
                                Service.gI().point(pl);
                                break;
                            case 967:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1050, 1051, 1052);
                                Service.gI().point(pl);
                                break;
                            case 1008:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1074, 1075, 1076);
                                Service.gI().point(pl);
                                break;
                            case 1039:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1089, 1090, 1091);
                                Service.gI().point(pl);
                                break;
                            case 1040:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1092, 1093, 1094);
                                Service.gI().point(pl);
                                break;
                            case 1046:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, -1, -1, -1);
                                Service.gI().point(pl);
                                break;
                            case 1107:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1155, 1156, 1157);
                                Service.gI().point(pl);
                                break;
                            case 1114:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1158, 1159, 1160);
                                Service.gI().point(pl);
                                break;
                            case 1188:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1183, 1184, 1185);
                                Service.gI().point(pl);
                                break;
                            case 1202:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1201, 1202, 1203);
                                Service.gI().point(pl);
                                break;
                            case 1203:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1201, 1202, 1203);
                                Service.gI().point(pl);
                                break;
                            case 1207:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1077, 1078, 1079);
                                Service.gI().point(pl);
                                break;
                            case 1224:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1227, 1228, 1229);
                                Service.gI().point(pl);
                                break;
                            case 1225:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1233, 1234, 1235);
                                Service.gI().point(pl);
                                break;
                            case 1226:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1230, 1231, 1232);
                                Service.gI().point(pl);
                                break;
                            case 1243:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1245, 1246, 1247);
                                Service.gI().point(pl);
                                break;
                            case 1244:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1248, 1249, 1250);
                                Service.gI().point(pl);
                                break;
                            case 1256:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1267, 1268, 1269);
                                Service.gI().point(pl);
                                break;
                            case 1318:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1299, 1300, 1301);
                                Service.gI().point(pl);
                                break;
                            case 1347:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1302, 1303, 1304);
                                Service.gI().point(pl);
                                break;
                            case 1414:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1341, 1342, 1343);
                                Service.gI().point(pl);
                                break;
                            case 1435:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1347, 1348, 1349);
                                Service.gI().point(pl);
                                break;
                            case 1452:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1365, 1366, 1367);
                                Service.gI().point(pl);
                                break;
                            case 1458:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1368, 1369, 1370);
                                Service.gI().point(pl);
                                break;
                            case 1482:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1398, 1399, 1400);
                                Service.gI().point(pl);
                                break;
                            case 1497:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1401, 1402, 1403);
                                Service.gI().point(pl);
                                break;
                            case 1550:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1428, 1429, 1430);
                                Service.gI().point(pl);
                                break;
                            case 1551:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1425, 1426, 1427);
                                Service.gI().point(pl);
                                break;
                            case 1564:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1437, 1438, 1439);
                                Service.gI().point(pl);
                                break;
                            case 1568:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1443, 1444, 1445);
                                Service.gI().point(pl);
                                break;
                            case 1573:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1446, 1447, 1448);
                                Service.gI().point(pl);
                                break;
                            case 1596:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1473, 1474, 1475);
                                Service.gI().point(pl);
                                break;
                            case 1597:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1473, 1474, 1475);
                                Service.gI().point(pl);
                                break;
                            case 1611:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1488, 1494, 1495);
                                Service.gI().point(pl);
                                break;
                            case 1620:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1496, 1497, 1498);
                                Service.gI().point(pl);
                                break;
                            case 1621:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1496, 1497, 1498);
                                Service.gI().point(pl);
                                break;
                            case 1622:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1488, 1489, 1490);
                                Service.gI().point(pl);
                                break;
                            case 1629:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1505, 1506, 1507);
                                Service.gI().point(pl);
                                break;
                            case 1630:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1508, 1509, 1510);
                                Service.gI().point(pl);
                                break;
                            case 1631:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1513, 1516, 1517);
                                Service.gI().point(pl);
                                break;
                            case 1633:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1523, 1524, 1525);
                                Service.gI().point(pl);
                                break;
                            case 1654:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1526, 1529, 1530);
                                Service.gI().point(pl);
                                break;
                            case 1668:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1550, 1551, 1552);
                                Service.gI().point(pl);
                                break;
                            case 1682:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1558, 1559, 1560);
                                Service.gI().point(pl);
                                break;
                            case 1683:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1561, 1562, 1563);
                                Service.gI().point(pl);
                                break;
                            case 1686:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1572, 1573, 1574);
                                Service.gI().point(pl);
                                break;
                            case 1750:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1464, 1465, 1466);
                                Service.gI().point(pl);
                                break;
                            case 211: // nho tím
                            case 212: // nho xanh
                                eatGrapes(pl, item);
                                break;
                            case 460:
                                Ngojc_XuongCho(pl, item);
                                break;
                            case 456:
                                Ngojc_BinhNuoc(pl, item);
                                break;
                            case 342:
                            case 343:
                            case 344:
                            case 345:
                                if (pl.zone.items.stream().filter(it -> it != null && it.itemTemplate.type == 22)
                                        .count() < 3) {
                                    Service.gI().dropSatellite(pl, item, pl.zone, pl.location.x, pl.location.y);
                                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                } else {
                                    Service.gI().sendThongBaoOK(pl, "Mỗi map chỉ đặt được 3 Vệ Tinh");
                                }
                                break;
                            case 380: // cskb
                                openCSKB(pl, item);
                                break;
                            case 381: // cuồng nộ
                            case 382: // bổ huyết
                            case 383: // bổ khí
                            case 384: // giáp xên
                            case 385: // ẩn danh
                            case 379: // máy dò capsule
                            case 638: // commeson
                            case 2075: // rocket
                            case 2160: // Nồi cơm điện
                            case 579:
                            case 1045: // đuôi khỉ
                            case 663: // bánh pudding
                            case 664: // xúc xíc
                            case 665: // kem dâu
                            case 666: // mì ly
                            case 667: // sushi
                            case 880:
                            case 881:
                            case 882:
//                                if (pl.itemTime.isEatMeal2) {
//                                    Service.gI().sendThongBao(pl, "Chỉ được sử dụng 1 cái");
//                                    break;
//                                }
                                useItemTime(pl, item);
                                break;
                            case 1150: // Cuồng nộ 2
                                if (pl.itemTime.isUseCuongNo) {
                                    Service.gI().sendThongBao(pl, "Không thể sử dụng cuồng nộ cấp 2 khi đang có cuồng nộ cấp 1");
                                    break;
                                }
                                useItemTime(pl, item);
                                break;
                            case 1151: // Bổ khí 2
                                if (pl.itemTime.isUseBoKhi) {
                                    Service.gI().sendThongBao(pl, "Không thể sử dụng bổ khí cấp 2 khi đang có bổ khí cấp 1");
                                    break;
                                }
                                useItemTime(pl, item);
                                break;
                            case 1152: // Bổ huyết 2
                                if (pl.itemTime.isUseBoHuyet) {
                                    Service.gI().sendThongBao(pl, "Không thể sử dụng bổ huyết cấp 2 khi đang có bổ huyết cấp 1");
                                    break;
                                }
                                useItemTime(pl, item);
                                break;
                            case 1153: // Giáp Xên bọ hung 2
                                if (pl.itemTime.isUseGiapXen) {
                                    Service.gI().sendThongBao(pl, "Không thể sử dụng giáp xên cấp 2 khi đang có giáp xên cấp 1");
                                    break;
                                }
                                useItemTime(pl, item);
                                break;
                            case 1154: // Ẩn danh 2
                                if (pl.itemTime.isUseAnDanh) {
                                    Service.gI().sendThongBao(pl, "Không thể sử dụng ẩn danh cấp 2 khi đang có ẩn danh cấp 1");
                                    break;
                                }
                                useItemTime(pl, item);
                                break;
                            case 521: // tdlt
                                useTDLT(pl, item);
                                break;
                            case 454: // bông tai
                                UseItem.gI().usePorata(pl);
                                break;
                            case 921: // bông tai
                                UseItem.gI().usePorata2(pl);
                                break;
                            case 1165: // bông tai cấp 3
                                UseItem.gI().usePorata3(pl);
                                break;
                            case 1121: // bông tai cấp 4
                                UseItem.gI().usePorata4(pl);
                                break;
                            case 193: // gói 10 viên capsule
                                openCapsuleUI(pl);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            case 194: // capsule đặc biệt
                                openCapsuleUI(pl);
                                break;
                            case 401: // đổi đệ tử
                                changePet(pl, item);
                                break;
                            case 402: // sách nâng chiêu 1 đệ tử
                            case 403: // sách nâng chiêu 2 đệ tử
                            case 404: // sách nâng chiêu 3 đệ tử
                            case 759: // sách nâng chiêu 4 đệ tử
                                upSkillPet(pl, item);
                                break;
                            case 726:
                                UseItem.gI().ItemManhGiay(pl, item);
                                break;
                            case 727:
                            case 728:
                                UseItem.gI().ItemSieuThanThuy(pl, item);
                                break;
                            case 648:
                                ItemService.gI().OpenItem648(pl, item);
                                break;
                            case 736:
                                ItemService.gI().OpenItem736(pl, item);
                                break;
                            case 987:
                                Service.gI().sendThongBao(pl, "Bảo vệ trang bị không bị rớt cấp"); // đá bảo vệ
                                break;
                            case 2006:
                                Input.gI().createFormChangeNameByItem(pl);
                                break;
                            case 568:
                                if (pl.mabuEgg == null) {
                                    MabuEgg.createMabuEgg(pl);
                                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                } else {
                                    Service.gI().sendThongBao(pl, "Hừm… trứng Mabu đã có trong nhà rồi.");
                                }
                                break;
                            case 1761: // Trứng đệ Fide
                                useEggPet(pl, item, 5, "Fide");
                                break;
                            case 1762: // Trứng đệ Xên
                                useEggPet(pl, item, 7, "Xên");
                                break;
                            case 1763: // Trứng đệ Kidbu
                                useEggPet(pl, item, 6, "Kid Bư");
                                break;
                            case 1764: // Trứng đệ Kid Beerus (thêm ID mới)
                                useEggPet(pl, item, 8, "Kid Beerus");
                                break;
                            case 1628:
                                useItemTime(pl, item);
                                break;
                            case 1771:
                                openHopQuaSetKichHoat(pl, item, 0);
                                break;
                            case 1772:
                                openHopQuaSetKichHoat(pl, item, 1);
                                break;
                            case 1773:
                                openHopQuaSetKichHoat(pl, item, 2);
                                break;
                            case 1774: // Thay skill 1 đệ tử
                                changeSkillPet(pl, item, 0);
                                break;
                            case 1775: // Thay skill 2 đệ tử
                                changeSkillPet(pl, item, 1);
                                break;
                            case 1776: // Thay skill 3 đệ tử
                                changeSkillPet(pl, item, 2);
                                break;
                            case 1777: // Thay skill 4 đệ tử
                                changeSkillPet(pl, item, 3);
                                break;
                        }
                        break;
                }
                TaskService.gI().checkDoneTaskUseItem(pl, item);
                InventoryService.gI().sendItemBags(pl);
            } else {
                Service.gI().sendThongBaoOK(pl, "Sức mạnh không đủ yêu cầu");
            }
        }
    }

    public static int[] HALLOWEEN_TUI_MU_REWARD_LIST = {1106, 742, 1105};

    private void openTuiMuHalloween(Player pl, Item item) {
        pl.diemhalloween += 30;
        PlayerDAO.updateHlw(pl);
        final int MAX_REWARD = 1;
        int emptySlots = InventoryService.gI().getCountEmptyBag(pl);
        if (emptySlots < MAX_REWARD) {
            Service.gI().sendThongBao(pl, "Cần ít nhất " + MAX_REWARD + " ô trống để mở Hòm Halloween!");
            return;
        }
        List<Item> rewards = new ArrayList<>();
        rewards.add(ItemService.gI().createNewItem((short) HALLOWEEN_TUI_MU_REWARD_LIST[Util.nextInt(HALLOWEEN_TUI_MU_REWARD_LIST.length)], 1));
        // Add phần thưởng vào túi
        for (Item reward : rewards) {
            boolean isVinhVien = Util.isTrue(5, 100);
            if (isVinhVien) {
                reward.itemOptions.add(new ItemOption(77, Util.nextInt(10, 40)));
                reward.itemOptions.add(new ItemOption(103, Util.nextInt(10, 40)));
                reward.itemOptions.add(new ItemOption(50, Util.nextInt(10, 40)));
                reward.itemOptions.add(new ItemOption(5, Util.nextInt(10, 50)));
            } else {
                reward.itemOptions.add(new ItemOption(77, Util.nextInt(5, 35)));
                reward.itemOptions.add(new ItemOption(103, Util.nextInt(5, 35)));
                reward.itemOptions.add(new ItemOption(50, Util.nextInt(5, 35)));
                reward.itemOptions.add(new ItemOption(5, Util.nextInt(10, 30)));
                reward.itemOptions.add(new ItemOption(30, Util.nextInt(5, 30)));
                reward.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
            }
            InventoryService.gI().addItemBag(pl, reward);
        }

        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);

        Service.gI().sendThongBao(pl, "Bạn nhận được " + rewards.size() + " vật phẩm từ Túi Mù Halloween!");
    }

    public static int[][] HALLOWEEN_CHEST_REWARD_LIST = {
            {1345, 10, 1},    // ID 20, 1%, số lượng 1
            {1346, 10, 1},   // ID 457, 5%
            {741, 10, 1}, // ID 1048, 10%, có thể nhận tối đa 2
            {740, 20, 1},   // ID 17, 20%
            {1107, 30, 1},   // ID 77, 30%
            {1114, 15, 1}   // ...
    };

    public static int[][] NGOAI_LE = {{457, 1}, {402, 1}, {403, 1}, {404, 1}, {759, 1}};

    private void openHalloWeenChest(Player pl, Item item) {
        pl.diemhalloween += 20;
        PlayerDAO.updateHlw(pl);
        final int MAX_REWARD = 1;
        int emptySlots = InventoryService.gI().getCountEmptyBag(pl);
        if (emptySlots < MAX_REWARD) {
            Service.gI().sendThongBao(pl, "Cần ít nhất " + MAX_REWARD + " ô trống để mở Hòm Halloween!");
            return;
        }
        List<Item> rewards = new ArrayList<>();
        int totalAdded = 0;

        for (int[] data : HALLOWEEN_CHEST_REWARD_LIST) {
            if (totalAdded >= MAX_REWARD) break; // đạt giới hạn
            int itemId = data[0];
            int rate = data[1];
            int maxQuantity = data[2];

            // random theo tỉ lệ %
            int roll = Util.nextInt(1, 100); // random 1-100
            if (roll <= rate) {
                Item reward = ItemService.gI().createNewItem((short) itemId);
                reward.quantity = Util.nextInt(1, maxQuantity);
                boolean isvinhVien = Util.isTrue(10, 100);
                if (isvinhVien) {
                    reward.itemOptions.add(new ItemOption(50, Util.nextInt(15, 25)));
                    reward.itemOptions.add(new ItemOption(77, Util.nextInt(15, 25)));
                    reward.itemOptions.add(new ItemOption(103, Util.nextInt(15, 25)));
                } else {
                    reward.itemOptions.add(new ItemOption(50, Util.nextInt(15, 25)));
                    reward.itemOptions.add(new ItemOption(77, Util.nextInt(15, 25)));
                    reward.itemOptions.add(new ItemOption(103, Util.nextInt(15, 25)));
                    reward.itemOptions.add(new ItemOption(30, 0));
                    reward.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
                }
                rewards.add(reward);
                totalAdded++;
            }
        }

        if (rewards.isEmpty()) {
            int[] tem = NGOAI_LE[Util.nextInt(NGOAI_LE.length)];
            rewards.add(ItemService.gI().createNewItem((short) tem[0], tem[1]));
        }

        // Add phần thưởng vào túi
        for (Item reward : rewards) {
            InventoryService.gI().addItemBag(pl, reward);
        }

        // Xóa 1 rương sau khi mở
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);

        Service.gI().sendThongBao(pl, "Bạn nhận được " + rewards.size() + " vật phẩm từ Hòm Halloween!");
    }

    public void openRuongGo(Player player) {
        Item ruongGo = InventoryService.gI().findItemBag(player, 570);
        if (ruongGo != null) {
            int level = InventoryService.gI().getParam(player, 72, 570);
            int requiredSlots = calculateRequiredEmptySlots(level);
            if (InventoryService.gI().getCountEmptyBag(player) < requiredSlots) {
                Service.gI().sendThongBao(player,
                        "Cần ít nhất " + (requiredSlots - InventoryService.gI().getCountEmptyBag(player))
                                + " ô trống trong hành trang");
            } else {
                player.itemsWoodChest.clear();
                if (level == 0) {
                    InventoryService.gI().subQuantityItemsBag(player, ruongGo, 1);
                    InventoryService.gI().sendItemBags(player);

                    Item item = ItemService.gI().createNewItem((short) 190);
                    item.quantity = 1;
                    InventoryService.gI().addItemBag(player, item);
                    InventoryService.gI().sendItemBags(player);

                    Service.gI().sendThongBao(player, "reward");
                    return;
                }
                int baseGoldAmount = 100 * level;
                int randomFactor = Util.nextInt(-15, 15);
                int goldAmount = baseGoldAmount + (baseGoldAmount * randomFactor / 100);

                Item itemGold = ItemService.gI().createNewItem((short) 190);
                itemGold.quantity = goldAmount * 1000;
                player.itemsWoodChest.add(itemGold);
                if (level >= 9) {
                    int quantity = 100 + (level - 9) * 20;

                    Item item77 = ItemService.gI().createNewItem((short) 77);
                    item77.quantity = quantity;

                    player.itemsWoodChest.add(item77);
                }
                int clothesCount = 1;
                if (level >= 5 && level <= 8) {
                    clothesCount = 2;
                } else if (level >= 10 && level <= 12) {
                    clothesCount = 3;
                }
                for (int i = 0; i < clothesCount; i++) {
                    int randItemId = randClothes(level);
                    Item rewardItem = ItemService.gI().createNewItem((short) randItemId);
                    List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop((short) randItemId);
                    if (ops != null && !ops.isEmpty()) {
                        rewardItem.itemOptions.addAll(ops);
                    }
                    rewardItem.quantity = 1;
                    player.itemsWoodChest.add(rewardItem);
                }

                int[] rewardItems = {17, 18, 19, 20, 380, 381, 382, 383, 384, 385, 1229};
                int rewardCount = 2;
                if (level >= 5 && level <= 8) {
                    rewardCount = 3;
                } else if (level >= 10 && level <= 12) {
                    rewardCount = 4;
                }

                Set<Integer> selectedItems = new HashSet<>();
                while (selectedItems.size() < rewardCount) {
                    int randItemId = rewardItems[Util.nextInt(0, rewardItems.length - 1)];
                    if (!selectedItems.contains(randItemId)) {
                        selectedItems.add(randItemId);
                        Item rewardItem = ItemService.gI().createNewItem((short) randItemId);
                        rewardItem.quantity = Util.nextInt(1, level);
                        player.itemsWoodChest.add(rewardItem);
                    }
                }

                int saoPhaLeCount = (level > 9) ? 2 : 1;
                for (int i = 0; i < saoPhaLeCount; i++) {
                    int rand = Util.nextInt(0, 6);
                    Item level1 = ItemService.gI().createNewItem((short) (441 + rand));
                    level1.itemOptions.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));
                    level1.quantity = Util.nextInt(1, 3);
                    player.itemsWoodChest.add(level1);
                }

                int dncCount = (level > 9) ? 2 : 1;
                for (int i = 0; i < dncCount; i++) {
                    int rand = Util.nextInt(0, 4);
                    Item dnc = ItemService.gI().createNewItem((short) (220 + rand));
                    dnc.itemOptions.add(new Item.ItemOption(71 - rand, 0));
                    dnc.quantity = Util.nextInt(1, level * 2);
                    player.itemsWoodChest.add(dnc);
                }
                InventoryService.gI().subQuantityItemsBag(player, ruongGo, 1);
                InventoryService.gI().sendItemBags(player);

                for (Item it : player.itemsWoodChest) {
                    InventoryService.gI().addItemBag(player, it);
                }
                InventoryService.gI().sendItemBags(player);

                player.indexWoodChest = player.itemsWoodChest.size() - 1;
                int i = player.indexWoodChest;
                if (i < 0) {
                    return;
                }
                Item itemWoodChest = player.itemsWoodChest.get(i);
                player.indexWoodChest--;
                String info = "|1|" + itemWoodChest.template.name;
                if (itemWoodChest.quantity > 1) {
                    info += " (x" + itemWoodChest.quantity + ")";
                }

                String info2 = "\n|2|";
                if (!itemWoodChest.itemOptions.isEmpty()) {
                    for (Item.ItemOption io : itemWoodChest.itemOptions) {
                        if (io.optionTemplate.id != 102 && io.optionTemplate.id != 73) {
                            info2 += io.getOptionString() + "\n";
                        }
                    }
                }
                info = (info2.length() > "\n|2|".length() ? (info + info2).trim() : info.trim()) + "\n|0|"
                        + itemWoodChest.template.description;
                NpcService.gI().createMenuConMeo(player, ConstNpc.RUONG_GO, -1, "Bạn nhận được\n"
                        + info.trim(), "OK" + (i > 0 ? " [" + i + "]" : ""));
            }
        }
    }

    public int calculateRequiredEmptySlots(int level) {
        int requiredSlots = 0;

        int baseGoldAmount = 100 * level;
        int randomFactor = Util.nextInt(-15, 15);
        int goldAmount = baseGoldAmount + (baseGoldAmount * randomFactor / 100);

        if (goldAmount > 0) {
            requiredSlots++;
        }
        int clothesCount = 1;
        if (level >= 5 && level <= 8) {
            clothesCount = 2;
        } else if (level >= 10 && level <= 12) {
            clothesCount = 3;
        }
        requiredSlots += clothesCount;
        int[] rewardItems = {17, 18, 19, 20, 380, 381, 382, 383, 384, 385, 1229};
        int rewardCount = 2;

        if (level >= 5 && level <= 8) {
            rewardCount = 3;
        } else if (level >= 10 && level <= 12) {
            rewardCount = 4;
        }
        requiredSlots += rewardCount;

        int saoPhaLeCount = (level > 9) ? 2 : 1;
        requiredSlots += saoPhaLeCount;
        int dncCount = (level > 9) ? 2 : 1;
        requiredSlots += dncCount;

        return requiredSlots;
    }

    private void Ngojc_XuongCho(Player pl, Item item) {
        List<Player> bosses = pl.zone.getBosses();
        boolean checkSoi = false;

        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.SOI_HEC_QUYN_1 && !pl.isDie()) {
                    checkSoi = true;
                }
            }
        }

        if (!checkSoi) {
            Service.gI().sendThongBao(pl, "Không tìm thấy Sói hẹc quyn");
            return;
        }

        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.SOI_HEC_QUYN_1) {
                    Boss soihecQuyn = (Boss) bossPlayer;
                    if (soihecQuyn != null) {
                        if (((SoiHecQuyn) soihecQuyn).Ngojc_KiemTraNhatXuong()) {
                            Service.gI().sendThongBao(pl, "Sói đã no rồi");
                            continue;
                        } else {
                            ((SoiHecQuyn) soihecQuyn).NhatXuong();
                            Service.gI().chat(soihecQuyn, "Ê, Cục xương ngon quá");
                        }

                        ItemMap itemMap = null;
                        int x = pl.location.x;
                        if (x < 0 || x >= pl.zone.map.mapWidth) {
                            return;
                        }
                        int y = pl.zone.map.yPhysicInTop(x, pl.location.y - 24);
                        itemMap = new ItemMap(pl.zone, 460, 1, x, y, pl.id);
                        itemMap.isPickedUp = true;
                        itemMap.createTime -= 23000;
                        if (itemMap != null) {
                            Service.gI().dropItemMap(pl.zone, itemMap);
                        }

                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        InventoryService.gI().sendItemBags(pl);

                        if (Util.nextInt(4) < 3) { // 75% cơ hội
                            int rand = Util.nextInt(0, 6); // Random từ 0 đến 6
                            short idItem = (short) (rand + 441); // Item 441 + rand
                            Item it = ItemService.gI().createNewItem(idItem);
                            it.itemOptions.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));

                            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                                InventoryService.gI().addItemBag(pl, it);
                                Service.gI().sendThongBao(pl, "Bạn vừa nhận được " + it.template.name);
                            } else {
                                Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ trống.");
                            }
                        } else {
                            short idItem = 459; // Item 459
                            Item it = ItemService.gI().createNewItem(idItem);
                            it.itemOptions.add(new Item.ItemOption(112, 80));
                            it.itemOptions.add(new Item.ItemOption(93, 90));
                            it.itemOptions.add(new Item.ItemOption(20, Util.nextInt(10000)));
                            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                                InventoryService.gI().addItemBag(pl, it);
                                Service.gI().sendThongBao(pl, "Bạn vừa nhận được " + it.template.name);
                            } else {
                                Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ trống.");
                            }
                        }

                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            // Handle exception
                        }

                        ItemMapService.gI().removeItemMapAndSendClient(itemMap);
                        ((SoiHecQuyn) soihecQuyn).leaveMapNew();
                    }

                }
            }

            InventoryService.gI().sendItemBags(pl);
        }

    }

    private void Ngojc_BinhNuoc(Player pl, Item item) {
        List<Player> bosses = pl.zone.getBosses();
        boolean checkSoi = false;

        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.XINBATO_1 && !pl.isDie()) {
                    checkSoi = true;
                }
            }
        }

        if (!checkSoi) {
            Service.gI().sendThongBao(pl, "");
            return;
        }

        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.XINBATO_1) {
                    Boss xinbato = (Boss) bossPlayer;
                    if (xinbato != null) {
                        if (((Xinbato) xinbato).Ngojc_Check()) {
                            continue;
                        } else {
                            ((Xinbato) xinbato).NhatXuong1();
                            Service.gI().chat(xinbato, "Cảm ơn" + pl.name + " đã cho ta bình nước");
                        }

                        ItemMap itemMap = null;
                        int x = pl.location.x;
                        if (x < 0 || x >= pl.zone.map.mapWidth) {
                            return;
                        }
                        int y = pl.zone.map.yPhysicInTop(x, pl.location.y - 24);
                        itemMap = new ItemMap(pl.zone, 456, 99, x, y, pl.id);
                        itemMap.isPickedUp = true;
                        itemMap.createTime -= 23000;
                        if (itemMap != null) {
                            Service.gI().dropItemMap(pl.zone, itemMap);
                        }

                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        InventoryService.gI().sendItemBags(pl);

                        if (Util.nextInt(4) < 3) { // 75% cơ hội
                            int rand = Util.nextInt(0, 6); // Random từ 0 đến 6
                            short idItem = (short) (rand + 441); // Item 441 + rand
                            Item it = ItemService.gI().createNewItem(idItem);
                            it.itemOptions.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));

                            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                                InventoryService.gI().addItemBag(pl, it);
                                Service.gI().sendThongBao(pl, "Bạn vừa nhận được " + it.template.name);
                            } else {
                                Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ trống.");
                            }
                        } else {
                            short idItem = 459; // Item 459
                            Item it = ItemService.gI().createNewItem(idItem);
                            it.itemOptions.add(new Item.ItemOption(112, 80));
                            it.itemOptions.add(new Item.ItemOption(93, 90));
                            it.itemOptions.add(new Item.ItemOption(20, Util.nextInt(10000)));
                            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                                InventoryService.gI().addItemBag(pl, it);
                                Service.gI().sendThongBao(pl, "Bạn vừa nhận được " + it.template.name);
                            } else {
                                Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ trống.");
                            }
                        }

                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            // Handle exception
                        }

                        ItemMapService.gI().removeItemMapAndSendClient(itemMap);
                        ((Xinbato) xinbato).leaveMapNew();
                    }

                }
            }

            InventoryService.gI().sendItemBags(pl);
        }

    }

    private int randClothes(int level) {
        int result = level - Util.nextInt(2, 4);
        if (result < 1) {
            result = 1;
        }
        return ConstItem.LIST_ITEM_CLOTHES[Util.nextInt(0, 2)][Util.nextInt(0, 4)][result];
    }

    private void changePet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender + 1;
            if (gender > 2) {
                gender = 0;
            }
            PetService.gI().changeNormalPet(player, gender);
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }

    private void eatGrapes(Player pl, Item item) {
        int percentCurrentStatima = pl.nPoint.stamina * 100 / pl.nPoint.maxStamina;
        if (percentCurrentStatima > 50) {
            Service.gI().sendThongBao(pl, "Thể lực vẫn còn trên 50%");
            return;
        } else if (item.template.id == 211) {
            pl.nPoint.stamina = pl.nPoint.maxStamina;
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 100%");
        } else if (item.template.id == 212) {
            pl.nPoint.stamina += (pl.nPoint.maxStamina * 20 / 100);
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 20%");
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
        PlayerService.gI().sendCurrentStamina(pl);
    }

    private void openCSKB(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = {76, 188, 189, 190, 381, 382, 383, 384, 385};
            int[][] gold = {{5000, 20000}};
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryService.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);

            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void useItemTime(Player pl, Item item) {
        switch (item.template.id) {
            case 381: // cuồng nộ
                if (pl.itemTime.isUseCuongNo2) {
                    Service.gI().sendThongBao(pl, "Không thể sử dụng cuồng nộ cấp 1 khi đang có cuồng nộ cấp 2");
                    return;
                }
                pl.itemTime.lastTimeCuongNo = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo = true;
                Service.gI().point(pl);
                break;
            case 382: // bổ huyết
                if (pl.itemTime.isUseBoHuyet2) {
                    Service.gI().sendThongBao(pl, "Không thể sử dụng bổ huyết cấp 1 khi đang có bổ huyết cấp 2");
                    return;
                }
                pl.itemTime.lastTimeBoHuyet = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet = true;
                break;
            case 383: // bổ khí
                if (pl.itemTime.isUseBoKhi2) {
                    Service.gI().sendThongBao(pl, "Không thể sử dụng bổ khí cấp 1 khi đang có bổ khí cấp 2");
                    return;
                }
                pl.itemTime.lastTimeBoKhi = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi = true;
                break;
            case 384: // giáp xên
                if (pl.itemTime.isUseGiapXen2) {
                    Service.gI().sendThongBao(pl, "Không thể sử dụng giáp xên cấp 1 khi đang có giáp xên cấp 2");
                    return;
                }
                pl.itemTime.lastTimeGiapXen = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen = true;
                break;
            case 385: // ẩn danh
                if (pl.itemTime.isUseAnDanh2) {
                    Service.gI().sendThongBao(pl, "Không thể sử dụng ẩn danh cấp 1 khi đang có ẩn danh cấp 2");
                    return;
                }
                pl.itemTime.lastTimeAnDanh = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh = true;
                break;
            case 379: // máy dò capsule
                pl.itemTime.lastTimeUseMayDo = System.currentTimeMillis();
                pl.itemTime.isUseMayDo = true;
                break;
            case 1150: // Cuồng nộ 2 - +120% sức đánh gốc
                pl.itemTime.lastTimeCuongNo2 = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo2 = true;
                Service.gI().point(pl);
                ItemTimeService.gI().sendAllItemTime(pl);
                break;
            case 1151: // Bổ khí 2 - +120% KI
                pl.itemTime.lastTimeBoKhi2 = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi2 = true;
                Service.gI().point(pl);
                ItemTimeService.gI().sendAllItemTime(pl);
                break;
            case 1152: // Bổ huyết 2 - +120% HP
                pl.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet2 = true;
                Service.gI().point(pl);
                ItemTimeService.gI().sendAllItemTime(pl);
                break;
            case 1153: // Giáp Xên bọ hung 2 - giảm 60% sát thương
                pl.itemTime.lastTimeGiapXen2 = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen2 = true;
                ItemTimeService.gI().sendAllItemTime(pl);
                break;
            case 1154: // Ẩn danh 2 - +10 phút (tối đa 40 phút)
                pl.itemTime.lastTimeAnDanh2 = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh2 = true;
                ItemTimeService.gI().sendAllItemTime(pl);
                break;
            case 1628: // Bùa x2 tn,sm đệ tử
                if (pl.pet == null) {
                    Service.gI().sendThongBao(pl, "Bạn cần có đệ tử để sử dụng vật phẩm này");
                    return;
                }
                if (pl.itemTime.isUsePetBuff) {
                    Service.gI().sendThongBao(pl, "Đệ tử đang trong trạng thái được tăng cường");
                    return;
                }
                pl.itemTime.lastTimeUsePetBuff = System.currentTimeMillis();
                pl.itemTime.isUsePetBuff = true;
                Service.gI().point(pl.pet);
                ItemTimeService.gI().sendAllItemTime(pl);
                break;
            case 638: // Commeson
                pl.itemTime.lastTimeUseCMS = System.currentTimeMillis();
                pl.itemTime.isUseCMS = true;
                break;
            case 2160: // Nồi cơm điện
                pl.itemTime.lastTimeUseNCD = System.currentTimeMillis();
                pl.itemTime.isUseNCD = true;
                break;
            case 579:
            case 1045: // Đuôi khỉ
                pl.itemTime.lastTimeUseDK = System.currentTimeMillis();
                pl.itemTime.isUseDK = true;
                break;
            case 663: // bánh pudding
            case 664: // xúc xíc
            case 665: // kem dâu
            case 666: // mì ly
            case 667: // sushi
                pl.itemTime.lastTimeEatMeal = System.currentTimeMillis();
                pl.itemTime.isEatMeal = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal);
                pl.itemTime.iconMeal = item.template.iconID;
                break;
            case 880:
            case 881:
            case 882:
                pl.itemTime.lastTimeEatMeal2 = System.currentTimeMillis();
                pl.itemTime.isEatMeal2 = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal2);
                pl.itemTime.iconMeal2 = item.template.iconID;
                break;
            case 1109: // máy dò đồ
                pl.itemTime.lastTimeUseMayDo2 = System.currentTimeMillis();
                pl.itemTime.isUseMayDo2 = true;
                break;
        }
        Service.gI().point(pl);
        ItemTimeService.gI().sendAllItemTime(pl);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
    }

    private void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                case SummonDragon.NGOC_RONG_2_SAO:
                case SummonDragon.NGOC_RONG_3_SAO:
                    SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGON,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        } else if (tempId >= ShenronEventService.NGOC_RONG_1_SAO && tempId <= ShenronEventService.NGOC_RONG_7_SAO) {
            ShenronEventService.gI().openMenuSummonShenron(pl, 0);
        }
    }

    private void learnSkill(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill.point == 7) {
                    Service.gI().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
                } else {
                    if (curSkill.point == 0) {
                        if (level == 1) {// Hoc skill moi
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 23);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else { // neu chua hoc ma hoc lv cao
                            Skill skillNeed = SkillUtil
                                    .createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            Service.gI().sendThongBao(pl,
                                    "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                        }
                    } else {
                        if (curSkill.point + 1 == level) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            pl.BoughtSkill.add((int) item.template.id);
                            // System.out.println(curSkill.template.name + " - " + curSkill.point);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 62);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Service.gI().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp "
                                    + (curSkill.point + 1) + " trước!");
                        }
                    }
                    InventoryService.gI().sendItemBags(pl);
                }
            } else {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
            }
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        }
    }

    private void useTDLT(Player pl, Item item) {
        if (pl.itemTime.isUseTDLT) {
            ItemTimeService.gI().turnOffTDLT(pl, item);
        } else {
            ItemTimeService.gI().turnOnTDLT(pl, item);
        }
    }

    private void usePorata2(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion2(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void usePorata3(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion3(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void usePorata4(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion4(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void usePorata(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void openCapsuleUI(Player pl) {
        pl.idMark.setTypeChangeMap(ConstMap.CHANGE_CAPSULE);
        ChangeMapService.gI().openChangeMapTab(pl);
    }

    public void choseMapCapsule(Player pl, int index) {

        if (pl.idNRNM != -1) {
            Service.gI().sendThongBao(pl, "Không thể mang ngọc rồng này lên Phi thuyền");
            Service.gI().hideWaitDialog(pl);
            return;
        }

        int zoneId = -1;
        if (index > pl.mapCapsule.size() - 1 || index < 0) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            Service.gI().hideWaitDialog(pl);
            return;
        }
        Zone zoneChose = pl.mapCapsule.get(index);
        // Kiểm tra số lượng người trong khu

        if (zoneChose.getNumOfPlayers() > 25
                || MapService.gI().isMapDoanhTrai(zoneChose.map.mapId)
                || MapService.gI().isMapMaBu(zoneChose.map.mapId)
                || MapService.gI().isMapHuyDiet(zoneChose.map.mapId)) {
            Service.gI().sendThongBao(pl, "Hiện tại không thể vào được khu!");
            return;
        }
        if (index != 0 || zoneChose.map.mapId == 21
                || zoneChose.map.mapId == 22
                || zoneChose.map.mapId == 23) {
            pl.mapBeforeCapsule = pl.zone;
        } else {
            zoneId = pl.mapBeforeCapsule != null ? pl.mapBeforeCapsule.zoneId : -1;
            pl.mapBeforeCapsule = null;
        }
        pl.changeMapVIP = true;
        ChangeMapService.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).map.mapId, zoneId, -1);
    }

    public void eatPea(Player player) {
        if (!Util.canDoWithTime(player.lastTimeEatPea, 1000)) {
            return;
        }
        player.lastTimeEatPea = System.currentTimeMillis();
        Item pea = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.type == 6) {
                pea = item;
                break;
            }
        }
        if (pea != null) {
            int hpKiHoiPhuc = 0;
            int lvPea = Integer.parseInt(pea.template.name.substring(13));
            for (Item.ItemOption io : pea.itemOptions) {
                if (io.optionTemplate.id == 2) {
                    hpKiHoiPhuc = io.param * 1000;
                    break;
                }
                if (io.optionTemplate.id == 48) {
                    hpKiHoiPhuc = io.param;
                    break;
                }
            }
            player.nPoint.setHp(player.nPoint.hp + hpKiHoiPhuc);
            player.nPoint.setMp(player.nPoint.mp + hpKiHoiPhuc);
            PlayerService.gI().sendInfoHpMp(player);
            Service.gI().sendInfoPlayerEatPea(player);
            if (player.pet != null && player.zone.equals(player.pet.zone) && !player.pet.isDie()) {
                int statima = 100 * lvPea;
                player.pet.nPoint.stamina += statima;
                if (player.pet.nPoint.stamina > player.pet.nPoint.maxStamina) {
                    player.pet.nPoint.stamina = player.pet.nPoint.maxStamina;
                }
                player.pet.nPoint.setHp(player.pet.nPoint.hp + hpKiHoiPhuc);
                player.pet.nPoint.setMp(player.pet.nPoint.mp + hpKiHoiPhuc);
                Service.gI().sendInfoPlayerEatPea(player.pet);
                Service.gI().chatJustForMe(player, player.pet, "Cám ơn sư phụ");
            }

            InventoryService.gI().subQuantityItemsBag(player, pea, 1);
            InventoryService.gI().sendItemBags(player);
        }
    }

    private void upSkillPet(Player pl, Item item) {
        if (pl.pet == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        try {
            switch (item.template.id) {
                case 402: // skill 1
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 0)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 403: // skill 2
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 1)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 404: // skill 3
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 2)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 759: // skill 4
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 3)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;

            }

        } catch (Exception e) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    private void ItemManhGiay(Player pl, Item item) {
        if (pl.winSTT && !Util.isAfterMidnight(pl.lastTimeWinSTT)) {
            Service.gI().sendThongBao(pl, "Hãy gặp thần mèo Karin để sử dụng");
            return;
        } else if (pl.winSTT && Util.isAfterMidnight(pl.lastTimeWinSTT)) {
            pl.winSTT = false;
            pl.callBossPocolo = false;
            pl.zoneSieuThanhThuy = null;
        }
        NpcService.gI().createMenuConMeo(pl, item.template.id, 564,
                "Đây chính là dấu hiệu riêng của...\nĐại Ma Vương Pôcôlô\nĐó là một tên quỷ dữ đội lốt người, một kẻ đại gian ác\ncó sức mạnh vô địch và lòng tham không đáy...\nĐối phó với hắn không phải dễ\nCon có chắc chắn muốn tìm hắn không?",
                "Đồng ý", "Từ chối");
    }

    private void ItemSieuThanThuy(Player pl, Item item) {
        long tnsm = 5_000_000;
        int n = 0;
        switch (item.template.id) {
            case 727:
                n = 2;
                break;
            case 728:
                n = 10;
                break;
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
        if (Util.isTrue(50, 100)) {
            Service.gI().sendThongBao(pl, "Bạn đã bị chết vì độc của thuốc tăng lực siêu thần thủy.");
            pl.setDie();
        } else {
            for (int i = 0; i < n; i++) {
                Service.gI().addSMTN(pl, (byte) 2, tnsm, true);
            }
        }
    }

    private void useEggPet(Player pl, Item item, int petType, String petName) {
        try {
//            // Kiểm tra yêu cầu đệ tử tiền đề
//            String requirementError = checkPetRequirement(pl, petType);
//            if (requirementError != null) {
//                Service.gI().sendThongBao(pl, requirementError);
//                return;
//            }

            // Xóa đệ tử hiện tại nếu có
            if (pl.pet != null) {
                if (pl.fusion.typeFusion != ConstPlayer.NON_FUSION) {
                    pl.pet.unFusion();
                }
                ChangeMapService.gI().exitMap(pl.pet);
                pl.pet.dispose();
                pl.pet = null;
            }

            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);

            switch (petType) {
                case 5: // Fide
                    PetService.gI().createFidePet(pl);
                    break;
                case 6: // Kid Bư
                    PetService.gI().createKidBuPet(pl);
                    break;
                case 7: // Xên
                    PetService.gI().createXenPet(pl);
                    break;
                case 8: // Kid Beerus
                    PetService.gI().createKidBeerusPet(pl);
                    break;
            }

            Service.gI().sendThongBao(pl, "Chúc mừng! Bạn đã nhận được đệ tử " + petName + "!");

        } catch (Exception e) {
            Service.gI().sendThongBao(pl, "Không thể sử dụng trứng đệ tử!");
        }
    }

    private String checkPetRequirement(Player pl, int petType) {
        switch (petType) {
            case 5: // Fide - yêu cầu đệ Mabư (type 1)
                if (pl.pet == null || pl.pet.typePet != 1) {
                    return "Bạn cần có đệ tử Mabư để mở trứng đệ Fide!";
                }
                break;
            case 7: // Xên - yêu cầu đệ Fide (type 5)
                if (pl.pet == null || pl.pet.typePet != 5) {
                    return "Bạn cần có đệ tử Fide để mở trứng đệ Xên!";
                }
                break;
            case 6: // Kid Bư - yêu cầu đệ Xên (type 7)
                if (pl.pet == null || pl.pet.typePet != 7) {
                    return "Bạn cần có đệ tử Xên để mở trứng đệ Kid Bư!";
                }
                break;
            case 8: // Kid Beerus - yêu cầu đệ Kid Bư (type 6)
                if (pl.pet == null || pl.pet.typePet != 6) {
                    return "Bạn cần có đệ tử Kid Bư để mở trứng đệ Kid Beerus!";
                }
                break;
        }
        return null; // Không có lỗi
    }

    public void UseCard(Player pl, Item item) {
        RadarCard radarTemplate = RadarService.gI().RADAR_TEMPLATE.stream().filter(c -> c.Id == item.template.id)
                .findFirst().orElse(null);
        if (radarTemplate == null) {
            return;
        }
        if (radarTemplate.Require != -1) {
            RadarCard radarRequireTemplate = RadarService.gI().RADAR_TEMPLATE.stream()
                    .filter(r -> r.Id == radarTemplate.Require).findFirst().orElse(null);
            if (radarRequireTemplate == null) {
                return;
            }
            Card cardRequire = pl.Cards.stream().filter(r -> r.Id == radarRequireTemplate.Id).findFirst().orElse(null);
            if (cardRequire == null || cardRequire.Level < radarTemplate.RequireLevel) {
                Service.gI().sendThongBao(pl, "Bạn cần sưu tầm " + radarRequireTemplate.Name + " ở cấp độ "
                        + radarTemplate.RequireLevel + " mới có thể sử dụng thẻ này");
                return;
            }
        }
        Card card = pl.Cards.stream().filter(r -> r.Id == item.template.id).findFirst().orElse(null);
        if (card == null) {
            Card newCard = new Card(item.template.id, (byte) 1, radarTemplate.Max, (byte) -1, radarTemplate.Options);
            pl.Cards.add(newCard);
            RadarService.gI().RadarSetAmount(pl, newCard.Id, newCard.Amount, newCard.MaxAmount);
            RadarService.gI().RadarSetLevel(pl, newCard.Id, newCard.Level);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);
        } else {
            if (card.Level >= 2) {
                Service.gI().sendThongBao(pl, "Thẻ này đã đạt cấp tối đa");
                return;
            }
            card.Amount++;
            if (card.Amount >= card.MaxAmount) {
                card.Amount = 0;
                if (card.Level == -1) {
                    card.Level = 1;
                } else {
                    card.Level++;
                }
                Service.gI().point(pl);
            }
            RadarService.gI().RadarSetAmount(pl, card.Id, card.Amount, card.MaxAmount);
            RadarService.gI().RadarSetLevel(pl, card.Id, card.Level);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);
        }
    }

    private void openHopQuaSetKichHoat(Player pl, Item item, int planetType) {
        if (InventoryService.gI().getCountEmptyBag(pl) < 1) {
            Service.gI().sendThongBao(pl, "Hành trang đã đầy");
            return;
        }

        int[] selectedOptions = new int[]{-1, -1};

        switch (planetType) {
            case 0 -> {
                int earthSetIndex = Util.nextInt(0, 3);
                switch (earthSetIndex) {
                    case 0 -> selectedOptions = new int[]{127, 139};
                    // Set 1: Options 127 + 139
                    case 1 -> selectedOptions = new int[]{128, 140};
                    // Set 2: Options 128 + 140
                    case 2 -> selectedOptions = new int[]{129, 141};
                }
            }
            case 1 -> {
                int xaydaSetIndex = Util.nextInt(0, 2);
                switch (xaydaSetIndex) {
                    case 0 -> selectedOptions = new int[]{133, 136};
                    // Set 1: Options 133 + 136
                    case 1 -> selectedOptions = new int[]{134, 137};
                    // Set 2: Options 134 + 137
                    case 2 -> selectedOptions = new int[]{135, 138};
                    // Set 3: Options 135 + 138
                }
            }
            case 2 -> {
                int namekSetIndex = Util.nextInt(0, 3);
                switch (namekSetIndex) {
                    case 0 -> selectedOptions = new int[]{130, 142};
                    // Set 1: Options 130 + 142
                    case 1 -> selectedOptions = new int[]{233, 234};
                    // Set 2: Options 131 + 143
                    case 2 -> selectedOptions = new int[]{132, 144};
                }
            }
        }

        if (selectedOptions[0] == -1) {
            Service.gI().sendThongBao(pl, "Có lỗi xảy ra khi mở hộp quà");
            return;
        }

        int randomItemType = Util.nextInt(0, 4);
        short itemId = 12;

        switch (planetType) {
            case 0 -> {
                // Trái Đất
                switch (randomItemType) {
                    case 0 -> {
                        short[] earthShirts = {0, 33, 3, 34};
                        itemId = earthShirts[Util.nextInt(0, earthShirts.length - 1)];
                    }
                    case 1 -> {
                        short[] earthPants = {6, 35, 9, 36};
                        itemId = earthPants[Util.nextInt(0, earthPants.length - 1)];
                    }
                    case 2 -> {
                        short[] earthShoes = {27, 30, 39, 40};
                        itemId = earthShoes[Util.nextInt(0, earthShoes.length - 1)];
                    }
                    case 3 -> {
                        short[] radars = {12, 57, 58, 59};
                        itemId = radars[Util.nextInt(0, radars.length - 1)];
                    }
                    case 4 -> {
                        short[] earthGloves = {21, 37, 24, 38};
                        itemId = earthGloves[Util.nextInt(0, earthGloves.length - 1)];
                    }
                }
            }

            case 1 -> {
                // Xayda
                switch (randomItemType) {
                    case 0 -> {
                        short[] xaydaShirts = {2, 49, 5, 50};
                        itemId = xaydaShirts[Util.nextInt(0, xaydaShirts.length - 1)];
                    }
                    case 1 -> {
                        short[] xaydaPants = {8, 51, 11, 52};
                        itemId = xaydaPants[Util.nextInt(0, xaydaPants.length - 1)];
                    }
                    case 2 -> {
                        short[] xaydaShoes = {29, 55, 32, 56};
                        itemId = xaydaShoes[Util.nextInt(0, xaydaShoes.length - 1)];
                    }
                    case 3 -> {
                        short[] radars = {12, 57, 58, 59};
                        itemId = radars[Util.nextInt(0, radars.length - 1)];
                    }
                    case 4 -> {
                        short[] xaydaGloves = {23, 53, 26, 54};
                        itemId = xaydaGloves[Util.nextInt(0, xaydaGloves.length - 1)];
                    }
                }
            }

            case 2 -> {
                // Namek
                switch (randomItemType) {
                    case 0 -> {
                        short[] namekShirts = {1, 41, 4, 42};
                        itemId = namekShirts[Util.nextInt(0, namekShirts.length - 1)];
                    }
                    case 1 -> {
                        short[] namekPants = {7, 43, 10, 44};
                        itemId = namekPants[Util.nextInt(0, namekPants.length - 1)];
                    }
                    case 2 -> {
                        short[] namekShoes = {28, 47, 31, 48};
                        itemId = namekShoes[Util.nextInt(0, namekShoes.length - 1)];
                    }
                    case 3 -> {
                        short[] radars = {12, 57, 58, 59};
                        itemId = radars[Util.nextInt(0, radars.length - 1)];
                    }
                    case 4 -> {
                        short[] namekGloves = {22, 45, 25, 46};
                        itemId = namekGloves[Util.nextInt(0, namekGloves.length - 1)];
                    }
                }
            }

        }

        Item activationItem = ItemService.gI().createItemSetKichHoat(itemId, 1);

        if (activationItem.itemOptions == null) {
            activationItem.itemOptions = new ArrayList<>();
        }

        List<Item.ItemOption> shopOptions = ItemService.gI().getListOptionItemShop(itemId);
        if (shopOptions != null && !shopOptions.isEmpty()) {
            for (Item.ItemOption shopOption : shopOptions) {
                activationItem.itemOptions.add(new Item.ItemOption(shopOption));
            }
        }

        for (int optionId : selectedOptions) {
            int param = 0;

            ItemOption newOption = new ItemOption(optionId, param);
            activationItem.itemOptions.add(newOption);
        }

        activationItem.itemOptions.add(new Item.ItemOption(30, 0));

        InventoryService.gI().addItemBag(pl, activationItem);

        CombineService.gI().sendEffectOpenItem(pl, item.template.iconID, activationItem.template.iconID);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
    }

    private void changeSkillPet(Player pl, Item item, int skillIndex) {
        if (pl.pet == null) {
            Service.gI().sendThongBao(pl, "Bạn cần có đệ tử để sử dụng vật phẩm này!");
            return;
        }

        try {
            switch (skillIndex) {
                case 0:
                    // Skill 1 đệ tử luôn tồn tại, chỉ cần thay đổi
                    Skill skill1 = SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1);
                    pl.pet.playerSkill.skills.set(0, skill1);
                    Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ đã cho con skill mới!");
                    break;

                case 1: // Skill 2
                    if (pl.pet.playerSkill.skills.get(1).skillId == -1) {
                        Service.gI().sendThongBao(pl, "Đệ tử cần đạt sức mạnh 150tr để có thể thay skill 2!");
                        return;
                    }
                    pl.pet.openSkill2();
                    Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ đã cho con skill 2 mới!");
                    break;

                case 2: // Skill 3
                    if (pl.pet.playerSkill.skills.get(2).skillId == -1) {
                        Service.gI().sendThongBao(pl, "Đệ tử cần đạt sức mạnh 1tỷ5 để có thể thay skill 3!");
                        return;
                    }
                    pl.pet.openSkill3();
                    Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ đã cho con skill 3 mới!");
                    break;

                case 3: // Skill 4
                    if (pl.pet.playerSkill.skills.get(3).skillId == -1) {
                        Service.gI().sendThongBao(pl, "Đệ tử cần đạt sức mạnh 20tỷ để có thể thay skill 4!");
                        return;
                    }
                    pl.pet.openSkill4();
                    Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ đã cho con skill 4 mới!");
                    break;
            }


            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);
            Service.gI().sendThongBao(pl, "Đã thay đổi skill " + (skillIndex + 1) + " cho đệ tử thành công!");

        } catch (Exception e) {
            Service.gI().sendThongBao(pl, "Có lỗi xảy ra khi thay skill đệ tử!");
            Logger.logException(UseItem.class, e);
        }
    }

    private void openCaiTrangHit(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) < 1) {
            Service.gI().sendThongBao(pl, "Hành trang đã đầy");
            return;
        }
        Item caiTrang = ItemService.gI().createNewItem((short) 884);
        int sdValue = Util.nextInt(1, 18);
        caiTrang.itemOptions.add(new Item.ItemOption(50, sdValue));
        int sdcmValue = Util.nextInt(1, 110);
        caiTrang.itemOptions.add(new Item.ItemOption(5, sdcmValue));
        if (Util.isTrue(90, 100)) {
            int hsdValue = Util.nextInt(1, 3);
            caiTrang.itemOptions.add(new Item.ItemOption(93, hsdValue));
        }
        caiTrang.itemOptions.add(new Item.ItemOption(30, 0));
        InventoryService.gI().addItemBag(pl, caiTrang);
        CombineService.gI().sendEffectOpenItem(pl, item.template.iconID, caiTrang.template.iconID);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
    }

    private void openHopQuaThanLinh(Player pl, Item item, int indexBag) {
        try {
            NpcService.gI().createMenuConMeo(pl, ConstNpc.MENU_HOP_QUA_THAN_LINH,
                    -1, "Bạn muốn nhận set thần linh của hành tinh nào?",
                    "Trái Đất", "Xayda", "Namek", "Đóng");
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        }
    }


    public void nhanSetThanLinh(Player player, int hanhTinh, Item hopQua, int indexBag) {
        try {
            if (player == null || hopQua == null) {
                return;
            }

            if (hanhTinh < 0 || hanhTinh > 2) {
                Service.gI().sendThongBao(player, "Lựa chọn không hợp lệ!");
                return;
            }

            if (InventoryService.gI().getCountEmptyBag(player) < 5) {
                Service.gI().sendThongBao(player, "Cần ít nhất 5 ô trống trong hành trang để nhận set thần linh!");
                return;
            }

            int[][] idItemHanhTinh = {
                    {555, 556, 562, 563, 561},
                    {559, 560, 566, 567, 561},
                    {557, 558, 564, 565, 561}
            };

            String[] tenHanhTinh = {"Trái Đất", "Xayda", "Namek"};

            for (int i = 0; i < 5; i++) {
                Item itemThanLinh = taoItemThanLinh((short) idItemHanhTinh[hanhTinh][i]);
                InventoryService.gI().addItemBag(player, itemThanLinh);
            }

            InventoryService.gI().subQuantityItemsBag(player, hopQua, 1);
            InventoryService.gI().sendItemBags(player);

            Service.gI().sendThongBao(player, "Bạn đã nhận được set thần linh " + tenHanhTinh[hanhTinh] + "!");

        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
            Service.gI().sendThongBao(player, "Có lỗi xảy ra khi mở hộp quà!");
        }
    }

    public Item taoItemThanLinh(short idItem) {
        Item item = ItemService.gI().createNewItem(idItem, 1);
        byte loaiTrangBi = item.template.type;
        int tiLeNgauNhien = Util.nextInt(100, 115);
        List<Item.ItemOption> danhSachOption = new ArrayList<>();
        switch (loaiTrangBi) {
            case 0 -> danhSachOption.add(new Item.ItemOption(47, Util.nextInt(800, 900) * tiLeNgauNhien / 100));
            case 1 -> {
                int chiSoQuan = Util.nextInt(46000, 49000) * tiLeNgauNhien / 100;
                danhSachOption.add(new Item.ItemOption(22, chiSoQuan / 1000));
                danhSachOption.add(new Item.ItemOption(27, chiSoQuan * 125 / 1000));
            }
            case 2 -> danhSachOption.add(new Item.ItemOption(0, Util.nextInt(4300, 4500) * tiLeNgauNhien / 100));
            case 3 -> {
                int chiSoGiay = Util.nextInt(46000, 49000) * tiLeNgauNhien / 100;
                danhSachOption.add(new Item.ItemOption(23, chiSoGiay / 1000));
                danhSachOption.add(new Item.ItemOption(28, chiSoGiay * 125 / 1000));
            }
            case 4 -> danhSachOption.add(new Item.ItemOption(14, Util.nextInt(14, 17) * tiLeNgauNhien / 100));
        }

        danhSachOption.add(new Item.ItemOption(21, Util.nextInt(15, 40)));
        item.itemOptions.clear();
        item.itemOptions.addAll(danhSachOption);
        item.info = item.getInfo();
        item.content = item.getContent();

        return item;
    }

}
