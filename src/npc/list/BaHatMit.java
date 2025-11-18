package npc.list;

/*
 * @Author: NgojcDev
 */

import consts.ConstNpc;
import managers.DeathOrAliveArena;
import managers.DeathOrAliveArenaManager;
import services.DeathOrAliveArenaService;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.Service;
import services.ChangeMapService;
import combine.CombineService;
import services.ShopService;
import utils.Util;

public class BaHatMit extends Npc {

    public BaHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5 -> this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Ngươi tìm ta có việc gì?",
                        "Chức năng\npha lê", "Nâng cấp\nĐệ tử", "Phân rã\nĐồ TL", "Ghép Rương\nSKH VIP");

                case 112 -> {
                    if (Util.isAfterMidnight(player.lastTimePKVoDaiSinhTu)) {
                        player.haveRewardVDST = false;
                        player.thoiVangVoDaiSinhTu = 0;
                    }
                    if (player.haveRewardVDST) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Đây là phần thưởng cho con.",
                                "update");
                        return;
                    }
                    if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().equals(player)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Ngươi muốn hủy đăng ký thi đấu võ đài?",
                                    "Top 100", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " Ngọc", "Từ chối",
                                    "Về\nđảo rùa");
                            return;
                        }
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi muốn đăng ký thi đấu võ đài?\nnhiều phần thưởng giá trị đang đợi ngươi đó",
                                "Top 100", "Bình chọn", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " Ngọc", "Từ chối",
                                "Về\nđảo rùa");
                        return;
                    }
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi muốn đăng ký thi đấu võ đài?\nnhiều phần thưởng giá trị đang đợi ngươi đó",
                            "Top 100", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " Ngọc", "Từ chối", "Về\nđảo rùa");
                }
                case 174 -> this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Ngươi tìm ta có việc gì?",
                        "Quay về", "Từ chối");
                case 181 -> this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Ngươi tìm ta có việc gì?",
                        "Quay về", "Từ chối");
                case 184 -> this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Ta giúp ngươi nâng cấp trang sức thiên tử, ngươi đã chuẩn bị đủ nguyên liệu chưa?",
                        "Nâng cấp\nvòng chân\nthiên tử", "Đóng");
                default -> {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi tìm ta có việc gì?",
                            "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                            "Nâng Cấp\n Bông Tai", "Mở Chỉ Số\nBông Tai", "Làm phép\nNhập đá",
                            "Nhập\nNgọc Rồng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5 -> {
                    if (player.idMark.isBaseMenu()) {
                        switch (select) {
                            case 0 -> createOtherMenu(player, 3,
                                    "Ta có thể giúp gì cho ngươi ?",
                                    "Ép sao\ntrang bị", "Pha lê\nhóa\ntrang bị", "Nâng cấp\nSao pha lê",
                                    "Đánh bóng\nSao pha lê", "Cường hóa\nlỗ sao\npha lê", "Tạo đá\nHematite");
//                            case 1 ->
//                                createOtherMenu(player, 6,
//                                        "Ta sẽ biến trang bị mới cao cấp hơn của ngươi \n"
//                                        + "Thành trang bị có cấp độ và sao pha lê của trang bị cũ",
//                                        "Chuyển hóa\nDùng vàng", "Chuyển hóa\nDùng ngọc");
                            case 1 -> createOtherMenu(player, 12,
                                    "Ta sẽ giúp người nâng cấp đệ tử",
                                    "Nâng đệ\nFide", "Nâng đệ\nXên", "Nâng đệ\nKid Bư", "Nâng đệ\nKid beerus");
                            case 2 -> CombineService.gI().openTabCombine(player, CombineService.PHAN_RA_DO_THAN_LINH);
                            case 3 -> CombineService.gI().openTabCombine(player, CombineService.GHEP_RUONG_SKH_VIP);
                        }
                    } else if (player.idMark.getIndexMenu() == 3) {
                        switch (select) {
                            case 0:
                                CombineService.gI().openTabCombine(player, CombineService.EP_SAO_TRANG_BI);
                                break;
                            case 1:
                                CombineService.gI().openTabCombine(player, CombineService.PHA_LE_HOA_TRANG_BI);
                                break;
                            case 2:
                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_SAO_PHA_LE);
                                break;
                            case 3:
                                CombineService.gI().openTabCombine(player, CombineService.DANH_BONG_SAO_PHA_LE);
                                break;
                            case 4:
                                CombineService.gI().openTabCombine(player, CombineService.CUONG_HOA_LO_SAO);
                                break;
                            case 5:
                                CombineService.gI().openTabCombine(player, CombineService.TAO_DA_HEMATITE);
                                break;
                        }
                    } else if (player.idMark.getIndexMenu() == 6) {
                        switch (select) {
                            case 0:
                                CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_BANG_VANG);
                                break;
                            case 1:
                                CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_BANG_NGOC);
                                break;
                        }
                    } else if (player.idMark.getIndexMenu() == 12) {
                        switch (select) {
                            case 0:
                                CombineService.gI().openTabCombine(player, CombineService.NANG_DE_FIDE);
                                break;
                            case 1:
                                CombineService.gI().openTabCombine(player, CombineService.NANG_DE_XEN);
                                break;
                            case 2:
                                CombineService.gI().openTabCombine(player, CombineService.NANG_DE_MABU);
                                break;
                            case 3:
                                CombineService.gI().openTabCombine(player, CombineService.NANG_DE_KID_BEERUS);
                                break;
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                        switch (player.combineNew.typeCombine) {
                            case CombineService.EP_SAO_TRANG_BI, CombineService.CHUYEN_HOA_BANG_NGOC,
                                 CombineService.CHUYEN_HOA_BANG_VANG, CombineService.PHA_LE_HOA_TRANG_BI,
                                 CombineService.NANG_CAP_SAO_PHA_LE, CombineService.DANH_BONG_SAO_PHA_LE,
                                 CombineService.CUONG_HOA_LO_SAO, CombineService.TAO_DA_HEMATITE,
                                 CombineService.NANG_DE_FIDE, CombineService.NANG_DE_XEN, CombineService.NANG_DE_MABU,
                                 CombineService.NANG_DE_KID_BEERUS, CombineService.PHAN_RA_DO_THAN_LINH,
                                 CombineService.GHEP_RUONG_SKH_VIP -> {
                                switch (select) {
                                    case 0 -> CombineService.gI().startCombine(player);
                                    case 1 -> CombineService.gI().startCombineVip(player, 10);
                                    case 2 -> CombineService.gI().startCombineVip(player, 100);
                                    case 3 -> CombineService.gI().startCombineVip(player, 1000);
                                    default -> {
                                    }
                                }
                            }
                        }
                    }
                }
                case 112 -> {
                    if (player.idMark.isBaseMenu()) {
                        if (player.haveRewardVDST) {
                            switch (select) {
                                case 0 -> {
                                    Service.gI().sendThongBao(player, "update.");
                                }
                                case 1 -> {
                                    Service.gI().sendThongBao(player, "update.");

                                }
                            }
                            return;
                        }
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                            if (DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().equals(player)) {
                                switch (select) {
                                    case 0 -> {
                                    }
                                    case 1 -> this.npcChat("Không thể thực hiện");
                                    case 2 -> {
                                    }
                                    case 3 -> ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                }
                                return;
                            }
                            switch (select) {
                                case 0 -> {
                                }
                                case 1 -> this.createOtherMenu(player, ConstNpc.DAT_CUOC_HAT_MIT,
                                        "Phí bình chọn là 1 triệu vàng\nkhi trận đấu kết thúc\n90% tổng tiền bình chọn sẽ chia đều cho phe bình chọn chính xác",
                                        "Bình chọn cho "
                                                + DeathOrAliveArenaManager.gI().getVDST(player.zone)
                                                .getPlayer().name
                                                + " ("
                                                + DeathOrAliveArenaManager.gI().getVDST(player.zone).getCuocPlayer()
                                                + ")",
                                        "Bình chọn cho hạt mít (" + DeathOrAliveArenaManager.gI()
                                                .getVDST(player.zone).getCuocBaHatMit() + ")");
                                case 2 -> DeathOrAliveArenaService.gI().startChallenge(player);
                                case 3 -> {
                                }
                                case 4 -> ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                            }
                            return;
                        }
                        switch (select) {
                            case 0 -> {
                            }
                            case 1 -> DeathOrAliveArenaService.gI().startChallenge(player);
                            case 2 -> {
                            }
                            case 3 -> ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.DAT_CUOC_HAT_MIT) {
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                            switch (select) {
                                case 0 -> {
                                    if (player.inventory.gold >= 1_000_000) {
                                        DeathOrAliveArena vdst = DeathOrAliveArenaManager.gI().getVDST(player.zone);
                                        vdst.setCuocPlayer(vdst.getCuocPlayer() + 1);
                                        vdst.addBinhChon(player);
                                        player.binhChonPlayer++;
                                        player.zoneBinhChon = player.zone;
                                        player.inventory.gold -= 1_000_000;
                                        Service.gI().sendMoney(player);
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu "
                                                + Util.numberToMoney(1_000_000 - player.inventory.gold) + " vàng nữa");
                                    }
                                }
                                case 1 -> {
                                    if (player.inventory.gold >= 1_000_000) {
                                        DeathOrAliveArena vdst = DeathOrAliveArenaManager.gI().getVDST(player.zone);
                                        vdst.setCuocBaHatMit(vdst.getCuocBaHatMit() + 1);
                                        vdst.addBinhChon(player);
                                        player.binhChonHatMit++;
                                        player.zoneBinhChon = player.zone;
                                        player.inventory.gold -= 1_000_000;
                                        Service.gI().sendMoney(player);
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu "
                                                + Util.numberToMoney(1_000_000 - player.inventory.gold) + " vàng nữa");
                                    }
                                }
                            }
                        }
                    }
                }
                case 174 -> {
                    if (player.idMark.isBaseMenu()) {
                        switch (select) {
                            case 0 -> ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                        }
                    }
                }
                case 181 -> {
                    if (player.idMark.isBaseMenu()) {
                        switch (select) {
                            case 0 -> ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                        }
                    }
                }
                case 184 -> {
                    if (player.idMark.isBaseMenu()) {
                        switch (select) {
                            case 0 -> CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_CHAN_THIEN_TU);
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                        switch (player.combineNew.typeCombine) {
                            case CombineService.NANG_CAP_CHAN_THIEN_TU -> {
                                if (select == 0) {
                                    CombineService.gI().startCombine(player);
                                }
                            }
                        }
                    }
                }
                case 42, 43, 44, 84 -> {
                    if (player.idMark.isBaseMenu()) {
                        switch (select) {
                            case 0 -> // shop bùa
                                    createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                            "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                                                    + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                            "Bùa\n1 giờ", "Bùa\n8 giờ", "Bùa\n1 tháng", "Đóng");
                            case 1 -> CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_VAT_PHAM);
                            case 2 -> {
                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_BONG_TAI);
                            }
                            case 3 -> {
                                // nâng cấp bông tai
                                CombineService.gI().openTabCombine(player, CombineService.NANG_CHI_SO_BONG_TAI);
                            }
                            case 4 -> CombineService.gI().openTabCombine(player, CombineService.LAM_PHEP_NHAP_DA);
                            case 5 -> CombineService.gI().openTabCombine(player, CombineService.NHAP_NGOC_RONG);
                            case 6 ->
                                    createOtherMenu(player, ConstNpc.MENU_SACH_TUYET_KY, "Ta có thể giúp gì cho ngươi ?",
                                            "Đóng thành\nSách cũ",
                                            "Đổi Sách\nTuyệt kỹ",
                                            "Giám định\nSách",
                                            "Tẩy\nSách",
                                            "Nâng cấp\nSách\nTuyệt kỹ",
                                            "Hồi phục\nSách",
                                            "Phân rã\nSách");
                        }

                        // } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_SACH_TUYET_KY) {
                        // switch (select) {
                        // case 0:
                        // CheTaoCuonSachCu.showCombine(player);
                        // break;
                        // case 1:
                        // DoiSachTuyetKy.showCombine(player);
                        // break;
                        // case 2:
                        // CombineService.gI().openTabCombine(player, CombineService.GIAM_DINH_SACH);
                        // break;
                        // case 3:
                        // CombineService.gI().openTabCombine(player, CombineService.TAY_SACH);
                        // break;
                        // case 4:
                        // CombineService.gI().openTabCombine(player,
                        // CombineService.NANG_CAP_SACH_TUYET_KY);
                        // break;
                        // case 5:
                        // CombineService.gI().openTabCombine(player, CombineService.HOI_PHUC_SACH);
                        // break;
                        // case 6:
                        // CombineService.gI().openTabCombine(player, CombineService.PHAN_RA_SACH);
                        // break;
                        // }
                        // } else if (player.idMark.getIndexMenu() == ConstNpc.DONG_THANH_SACH_CU) {
                        // CheTaoCuonSachCu.cheTaoCuonSachCu(player);
                        // } else if (player.idMark.getIndexMenu() == ConstNpc.DOI_SACH_TUYET_KY) {
                        // DoiSachTuyetKy.doiSachTuyetKy(player);
                    } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                        switch (select) {
                            case 0 -> ShopService.gI().opendShop(player, "BUA_1H", true);
                            case 1 -> ShopService.gI().opendShop(player, "BUA_8H", true);
                            case 2 -> ShopService.gI().opendShop(player, "BUA_1M", true);
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                        switch (player.combineNew.typeCombine) {
                            case CombineService.NANG_CAP_VAT_PHAM, CombineService.NANG_CAP_BONG_TAI,
                                 CombineService.NANG_CHI_SO_BONG_TAI, CombineService.LAM_PHEP_NHAP_DA,
                                 CombineService.NHAP_NGOC_RONG, CombineService.PHAN_RA_DO_THAN_LINH,
                                 CombineService.NANG_DE_FIDE, CombineService.NANG_DE_XEN, CombineService.NANG_DE_MABU,
                                 CombineService.NANG_DE_KID_BEERUS -> {
                                if (select == 0) {
                                    CombineService.gI().startCombine(player);
                                }
                            }
                        }
                    }
                }
                default -> {
                }
            }
        }
    }
}
