package npc.list;

/*
 * @Author: NgojcDev
 */

import consts.BossID;
import consts.ConstNpc;
import item.Item;
import java.io.IOException;
import network.Message;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.Service;
import services.SkillService;
import services.ChangeMapService;
import combine.CombineService;
import services.TrainingService;
import services.NpcService;
import services.ShopService;
import skill.Skill;
import utils.SkillUtil;
import utils.Util;

public class Whis extends Npc {

    private static final int COST_HD = 50000000;

    public Whis(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 154 -> {
                    Item BiKiepTuyetKy = InventoryService.gI().findItem(player.inventory.itemsBag, 2055);
                    if (BiKiepTuyetKy != null) {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Thử đánh với ta xem nào.\nNgươi còn 1 lượt nữa cơ mà.",
                                "Nói chuyện",
                                "Học\ntuyệt kỹ",
                                "Top 100",
                                "[LV:" + (player.traning.getTop() + 1) + "]",
                                "Đến vùng đất hủy diệt");
                    } else {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Thử đánh với ta xem nào.\nNgươi còn 1 lượt nữa cơ mà.",
                                "Nói chuyện",
                                "Top 100",
                                "[LV:" + (player.traning.getTop() + 1) + "]",
                                "Đến vùng đất hủy diệt");
                    }
                }
                case 164 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                            "Quay về", "Từ chối");
                case 169 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                case 48 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Đã tìm đủ nguyên liệu cho tôi chưa?\n Tôi sẽ giúp cậu mạnh lên kha khá đấy!", "Hướng Dẫn",
                            "Đổi SKH VIP", "Chế tạo\nChân mệnh", "Nâng cấp\nChân mệnh", "Từ Chối");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.idMark.isBaseMenu() && (this.mapId == 154 || this.mapId == 169)) {
                Item BiKiepTuyetKy = InventoryService.gI().findItem(player.inventory.itemsBag, 2055);
                switch (select) {
                    case 0 -> {
                        if (this.mapId == 154) {
                            if (!player.setClothes.checkSetDes()) {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Ngươi hãy trang bị đủ 5 món trang bị Hủy Diệt rồi ta nói chuyện tiếp.",
                                        "OK");
                                return;
                            }
                            this.createOtherMenu(player, 5,
                                    "Ta sẽ giúp ngươi chế tạo trang bị thiên sứ",
                                    "Shop thiên sứ",
                                    "Chế tạo",
                                    "Từ chối");
                        } else if (this.mapId == 164) {
                            ChangeMapService.gI().changeMapInYard(player, 154, -1, 758);
                        }
                    }
                    case 1 -> {
                        if (BiKiepTuyetKy != null) {
                            int idskill = Skill.MA_PHONG_BA;
                            if (player.gender == 0) {
                                idskill = Skill.SUPER_KAME;
                            } else if (player.gender == 2) {
                                idskill = Skill.LIEN_HOAN_CHUONG;
                            }
                            Skill curSkill = SkillUtil.getSkillbyId(player, idskill);
                            boolean checkskill = false;
                            if (curSkill == null) {
                                checkskill = true;
                            } else if (curSkill.point == 0) {
                                checkskill = true;
                            }
                            if (checkskill) {
                                if (player.gender == 0) {
                                    this.createOtherMenu(player, 6,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ Super kamejoko 1\n|7|Bí kiếp tuyệt kỹ: "
                                                    + BiKiepTuyetKy.quantity + "/9999\n" + "|2|Giá vàng: 10.000.000\n"
                                                    + "|2|Giá ngọc: 99",
                                            "Đồng ý", "Từ chối");
                                }
                                if (player.gender == 1) {
                                    this.createOtherMenu(player, 6,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ Ma phong ba 1\n|7|Bí kiếp tuyệt kỹ: "
                                                    + BiKiepTuyetKy.quantity + "/9999\n" + "|2|Giá vàng: 10.000.000\n"
                                                    + "|2|Giá ngọc: 99",
                                            "Đồng ý", "Từ chối");
                                }
                                if (player.gender == 2) {
                                    this.createOtherMenu(player, 6,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ Ca đíc liên hoàn chưởng 1\n|7|Bí kiếp tuyệt kỹ: "
                                                    + BiKiepTuyetKy.quantity + "/9999\n" + "|2|Giá vàng: 10.000.000\n"
                                                    + "|2|Giá ngọc: 99",
                                            "Đồng ý", "Từ chối");
                                }
                            } else {
                                if (player.gender == 0) {
                                    this.createOtherMenu(player, 6,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ Super kamejoko " + (curSkill.point + 1) + "\n"
                                                    + "|7|Bí kiếp tuyệt kỹ: " + BiKiepTuyetKy.quantity + "/999\n"
                                                    + "|2|Giá vàng: 10.000.000\n" + "|2|Giá ngọc: 99",
                                            "Đồng ý", "Từ chối");
                                }
                                if (player.gender == 1) {
                                    this.createOtherMenu(player, 6,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ Ma phong ba " + (curSkill.point + 1) + "\n"
                                                    + "|7|Bí kiếp tuyệt kỹ: " + BiKiepTuyetKy.quantity + "/999\n"
                                                    + "|2|Giá vàng: 10.000.000\n" + "|2|Giá ngọc: 99",
                                            "Đồng ý", "Từ chối");
                                }
                                if (player.gender == 2) {
                                    this.createOtherMenu(player, 6, "|1|Ta sẽ dạy ngươi tuyệt kỹ "
                                            + "Ca đíc liên hoàn chưởng " + (curSkill.point + 1) + "\n"
                                            + "|7|Bí kiếp tuyệt kỹ: " + BiKiepTuyetKy.quantity + "/999\n"
                                            + "|2|Giá vàng: 10.000.000\n" + "|2|Giá ngọc: 99",
                                            "Đồng ý", "Từ chối");
                                }
                            }
                        } else {
                        }
                    }
                    case 2 -> {
                        if (BiKiepTuyetKy == null) {
                            TrainingService.gI().callBoss(player, BossID.WHIS, false);
                        } else {
                        }
                    }
                    case 3 -> {
                        if (BiKiepTuyetKy == null) {
                            vaoMapHD(player);
                        } else {
                            TrainingService.gI().callBoss(player, BossID.WHIS, false);
                        }
                    }
                    case 4 -> {
                        if (BiKiepTuyetKy != null) {
                            vaoMapHD(player);
                        }
                    }
                }
            } else if (player.idMark.getIndexMenu() == 5) {
                switch (select) {
                    case 0 ->
                        ShopService.gI().opendShop(player, "THIEN_SU", true);
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_DAP_DO) {
                if (select == 0) {
                    CombineService.gI().startCombine(player);
                }
            } else if (player.idMark.getIndexMenu() == 6) {
                switch (select) {
                    case 0 -> {
                        Item sach = InventoryService.gI().findItemBag(player, 2055);
                        if (sach != null && player.inventory.gold >= 10000000 && player.inventory.gem > 99
                                && player.nPoint.power >= 60000000000L) {
                            int idskill = Skill.MA_PHONG_BA;
                            int iconSkill = 11194;
                            if (player.gender == 0) {
                                idskill = Skill.SUPER_KAME;
                                iconSkill = 11162;
                            } else if (player.gender == 2) {
                                idskill = Skill.LIEN_HOAN_CHUONG;
                                iconSkill = 11193;
                            }
                            Skill curSkill = SkillUtil.getSkillbyId(player, idskill);
                            boolean checkskill = false;
                            if (curSkill == null) {
                                checkskill = true;
                            } else if (curSkill.point == 0) {
                                checkskill = true;
                            }
                            if (checkskill) {
                                if (sach.quantity >= 9999) {
                                    try {
                                        int trubk = 99;
                                        String msg = "Tư chất kém!";
                                        String msg2 = "Ngu dốt!";
                                        if (Util.isTrue(1, 15)) {
                                            trubk = 9999;
                                            msg = "Học skill thành công!";
                                            msg2 = "Chúc mừng con nhé!";
                                            switch (player.gender) {
                                                case 0 ->
                                                    SkillService.gI().learSkillSpecial(player, Skill.SUPER_KAME);
                                                case 2 ->
                                                    SkillService.gI().learSkillSpecial(player, Skill.LIEN_HOAN_CHUONG);
                                                default ->
                                                    SkillService.gI().learSkillSpecial(player, Skill.MA_PHONG_BA);
                                            }
                                        } else {
                                            iconSkill = 15313;
                                        }
                                        Message msgg;
                                        msgg = new Message(-81);
                                        msgg.writer().writeByte(0);
                                        msgg.writer().writeUTF("Skill 9");
                                        msgg.writer().writeUTF("NgocRongMeow");
                                        msgg.writer().writeShort(tempId);
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        msgg = new Message(-81);
                                        msgg.writer().writeByte(1);
                                        msgg.writer().writeByte(1);
                                        msgg.writer().writeByte(InventoryService.gI().getIndexItemBag(player, sach));
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        msgg = new Message(-81);
                                        msgg.writer().writeByte(trubk == 99 ? 8 : 7);
                                        msgg.writer().writeShort(iconSkill);
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        this.npcChat(player, msg2);
                                        Service.gI().sendThongBao(player, msg);
                                        InventoryService.gI().subQuantityItemsBag(player, sach, trubk);
                                        player.inventory.gold -= 10000000;
                                        player.inventory.gem -= 99;
                                        InventoryService.gI().sendItemBags(player);
                                        Service.gI().sendThongBao(player, msg);
                                    } catch (IOException e) {
                                    }
                                } else {
                                    int sosach = 9999 - sach.quantity;
                                    Service.gI().sendThongBao(player,
                                            "Ngươi còn thiếu " + sosach + " bí kíp nữa.\nHãy tìm đủ rồi đến gặp ta.");
                                }
                            } else {
                                if (sach.quantity >= 999) {
                                    if (curSkill.currLevel < 1000) {
                                        npcChat(player, "Ngươi chưa luyện skill đến mức thành thạo. Luyện thêm đi.");
                                    } else if (curSkill.point >= 9) {
                                        npcChat(player, "Skill của ngươi đã đến cấp độ tối đa");
                                    } else {
                                        try {
                                            int trubk = 99;
                                            String msg = "Tư chất kém!";
                                            String msg2 = "Ngu dốt!";
                                            if (Util.isTrue(1, 30)) {
                                                trubk = 999;
                                                msg = "Nâng skill thành công!";
                                                msg2 = "Chúc mừng con nhé!";
                                                curSkill.point++;
                                                curSkill.currLevel = 0;
                                                SkillService.gI().sendCurrLevelSpecial(player, curSkill);
                                            } else {
                                                iconSkill = 15313;
                                            }
                                            Message msgg;
                                            msgg = new Message(-81);
                                            msgg.writer().writeByte(0);
                                            msgg.writer().writeUTF("Skill 9");
                                            msgg.writer().writeUTF("NgocRongMeow");
                                            msgg.writer().writeShort(tempId);
                                            player.sendMessage(msgg);
                                            msgg.cleanup();
                                            msgg = new Message(-81);
                                            msgg.writer().writeByte(1);
                                            msgg.writer().writeByte(1);
                                            msgg.writer()
                                                    .writeByte(InventoryService.gI().getIndexItemBag(player, sach));
                                            player.sendMessage(msgg);
                                            msgg.cleanup();
                                            msgg = new Message(-81);
                                            msgg.writer().writeByte(trubk == 99 ? 8 : 7);
                                            msgg.writer().writeShort(iconSkill);
                                            player.sendMessage(msgg);
                                            msgg.cleanup();
                                            this.npcChat(player, msg2);
                                            Service.gI().sendThongBao(player, msg);
                                            InventoryService.gI().subQuantityItemsBag(player, sach, trubk);
                                            player.inventory.gold -= 10000000;
                                            player.inventory.gem -= 99;
                                            InventoryService.gI().sendItemBags(player);
                                            Service.gI().sendThongBao(player, msg);
                                        } catch (IOException e) {
                                        }
                                    }
                                } else {
                                    int sosach = 999 - sach.quantity;
                                    Service.gI().sendThongBao(player,
                                            "Ngươi còn thiếu " + sosach + " bí kíp nữa.\nHãy tìm đủ rồi đến gặp ta.");
                                }
                            }
                        } else if (player.nPoint.power < 60000000000L) {
                            Service.gI().sendThongBao(player, "Ngươi không đủ sức mạnh để học tuyệt kỹ");
                        } else if (player.inventory.gold <= 10000000) {
                            Service.gI().sendThongBao(player, "Hãy có đủ vàng thì quay lại gặp ta.");
                        } else if (player.inventory.gem <= 99) {
                            Service.gI().sendThongBao(player, "Hãy có đủ ngọc xanh thì quay lại gặp ta.");
                        }
                    }
                }

            } else if (player.idMark.isBaseMenu() && this.mapId == 169) {
                if (select == 0) {
                    ChangeMapService.gI().changeMapBySpaceShip(player, 154, -1, 450);
                }
            } else if (player.idMark.isBaseMenu() && this.mapId == 48) {
                switch (select) {
                    case 0:
                        NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_DOI_SKH_VIP);
                        break;
                    default:
                        break;
                }

            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_NANG_DOI_SKH_VIP) {
                if (select == 0) {
                    CombineService.gI().startCombine(player);
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_CHE_TAO_CHAN_MENH) {
                if (select == 0) {
                    CombineService.gI().startCombine(player);
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_CHAN_MENH) {
                if (select == 0) {
                    CombineService.gI().startCombine(player);
                }
            }
        }
    }

    private void vaoMapHD(Player player) {
        if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
            player.inventory.gold -= COST_HD;
            Service.gI().sendMoney(player);
            ChangeMapService.gI().changeMapBySpaceShip(player, 169, -1, 168);
        } else {
            this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
            Service.gI().sendThongBao(player, "Yêu cầu sm lớn hơn 80 Tỷ và 50 Tr vàng.");
        }
    }

}
