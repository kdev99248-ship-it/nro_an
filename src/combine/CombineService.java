package combine;

/*
 * @Author: NgojcDev
 */

import combine.list.*;
import consts.ConstNpc;
import item.Item;
import combine.list.nangcapde.NangDeFide;
import combine.list.nangcapde.NangDeXen;
import combine.list.nangcapde.NangDeKidbu;
import combine.list.nangcapde.NangDeKidBeerus;
import item.Item.ItemOption;
import player.Player;
import network.Message;
import npc.Npc;
import services.NpcManager;

public class CombineService {

    public static final byte MAX_STAR_ITEM = 9;
    public static final byte MAX_LEVEL_ITEM = 8;

    private static final byte OPEN_TAB_COMBINE = 0;
    private static final byte REOPEN_TAB_COMBINE = 1;
    private static final byte COMBINE_SUCCESS = 2;
    private static final byte COMBINE_FAIL = 3;
    private static final byte COMBINE_DRAGON_BALL = 5;
    public static final byte OPEN_ITEM = 6;
    public static final int GHEP_RUONG_SKH_VIP = -2132;
    public static final int EP_SAO_TRANG_BI = 500;
    public static final int PHA_LE_HOA_TRANG_BI = 501;
    public static final int CHUYEN_HOA_TRANG_BI = 502;
    public static final int NANG_CAP_VAT_PHAM = 510;
    public static final int NANG_CAP_BONG_TAI = 511;
    public static final int LAM_PHEP_NHAP_DA = 512;
    public static final int NHAP_NGOC_RONG = 513;
    public static final int PHAN_RA_DO_THAN_LINH = 514;
    public static final int NANG_CAP_DO_TS = 515;
    public static final int NANG_CHI_SO_BONG_TAI = 517;
    public static final int NANG_CAP_SAO_PHA_LE = 5130;
    public static final int DANH_BONG_SAO_PHA_LE = 518;
    public static final int CUONG_HOA_LO_SAO = 519;
    public static final int TAO_DA_HEMATITE = 520;
    // Chuyển hóa trang bị
    public static final int CHUYEN_HOA_BANG_VANG = 46346;
    public static final int CHUYEN_HOA_BANG_NGOC = 58745;

    // sách tuyệt kĩ
    public static final int GIAM_DINH_SACH = 57457;
    public static final int TAY_SACH = 555;
    public static final int NANG_CAP_SACH_TUYET_KY = 556;
    public static final int PHUC_HOI_SACH = 557;
    public static final int PHAN_RA_SACH = 558;
    //Nâng cấp đệ tử
    public static final int NANG_DE_FIDE = 559;
    public static final int NANG_DE_XEN = 560;
    public static final int NANG_DE_MABU = 561;
    public static final int NANG_DE_KID_BEERUS = 562;

    //Nâng cấp chân thiên tử
    public static final int NANG_CAP_CHAN_THIEN_TU = 563;

    private static CombineService instance;

    public final Npc baHatMit;
    public final Npc whis;

    private CombineService() {
        this.baHatMit = NpcManager.getNpc(ConstNpc.BA_HAT_MIT);
        this.whis = NpcManager.getNpc(ConstNpc.WHIS);
    }

    public static CombineService gI() {
        if (instance == null) {
            instance = new CombineService();
        }
        return instance;
    }

    /**
     * Hiển thị thông tin đập đồ
     *
     * @param player
     * @param index
     */
    public void showInfoCombine(Player player, int[] index) {
        if (player.combineNew == null) {
            return;
        }
        player.combineNew.clearItemCombine();
        if (index.length > 0) {
            for (int i = 0; i < index.length; i++) {
                player.combineNew.itemsCombine.add(player.inventory.itemsBag.get(index[i]));
            }
        }
        switch (player.combineNew.typeCombine) {
            case EP_SAO_TRANG_BI:
                EpSaoTrangBi.showInfoCombine(player);
                break;
            case GHEP_RUONG_SKH_VIP:
                GhepRuongSKHVip.showInfoCombine(player);
                break;
            case PHA_LE_HOA_TRANG_BI:
                PhaLeHoaTrangBi.showInfoCombine(player);
                break;
            case NHAP_NGOC_RONG:
                NhapNgocRong.showInfoCombine(player);
                break;
            case NANG_CAP_VAT_PHAM:
                NangCapVatPham.showInfoCombine(player);
                break;
            case NANG_CAP_BONG_TAI:
                NangCapBongTai.showInfoCombine(player);
                break;
            case NANG_CHI_SO_BONG_TAI:
                NangChiSoBongTai.showInfoCombine(player);
                break;
            case NANG_CAP_DO_TS:
                // CheTaoTrangBiThienSu.showInfoCombine(player);
                break;
            case NANG_CAP_SAO_PHA_LE:
                NangCapSaoPhaLe.showInfoCombine(player);
                break;
            case DANH_BONG_SAO_PHA_LE:
                DanhBongSaoPhaLe.showInfoCombine(player);
                break;
            case CUONG_HOA_LO_SAO:
                CuongHoaLoSao.showInfoCombine(player);
                break;
            case TAO_DA_HEMATITE:
                TaoDaHematite.showInfoCombine(player);
                break;
            case LAM_PHEP_NHAP_DA:
                LamPhepNhapDa.showInfoCombine(player);
                break;
            case CHUYEN_HOA_BANG_NGOC:
                ChuyenHoaBangNgoc.showInfoCombine(player);
                break;
            case CHUYEN_HOA_BANG_VANG:
                ChuyenHoaBangVang.showInfoCombine(player);
                break;
            case NANG_DE_FIDE:
                NangDeFide.showInfoCombine(player);
                break;
            case NANG_DE_XEN:
                NangDeXen.showInfoCombine(player);
                break;
            case NANG_DE_MABU:
                NangDeKidbu.showInfoCombine(player);
                break;
            case NANG_DE_KID_BEERUS:
                NangDeKidBeerus.showInfoCombine(player);
                break;
            case PHAN_RA_DO_THAN_LINH:
                PhanRaDoThanLinh.showInfoCombine(player);
                break;
            case NANG_CAP_CHAN_THIEN_TU:
                NangCapChanThienTu.showInfoCombine(player);
                break;
        }
    }

    /**
     * Bắt đầu đập đồ - điều hướng từng loại đập đồ
     *
     * @param player
     */
    public void startCombine(Player player) {
        switch (player.combineNew.typeCombine) {
            case EP_SAO_TRANG_BI:
                EpSaoTrangBi.epSaoTrangBi(player);
                break;
            case GHEP_RUONG_SKH_VIP:
                GhepRuongSKHVip.ghepRuongSKhVIP(player);
                break;
            case PHA_LE_HOA_TRANG_BI:
                PhaLeHoaTrangBi.phaLeHoa(player);
                break;
            case NHAP_NGOC_RONG:
                NhapNgocRong.nhapNgocRong(player);
                break;
            case NANG_CAP_VAT_PHAM:
                NangCapVatPham.nangCapVatPham(player);
                break;
            case NANG_CAP_BONG_TAI:
                NangCapBongTai.nangCapBongTai(player);
                break;
            case NANG_CHI_SO_BONG_TAI:
                NangChiSoBongTai.nangChiSoBongTai(player);
                break;
            case NANG_CAP_DO_TS:
                // CheTaoTrangBiThienSu.debug(player);
                break;
            case NANG_CAP_SAO_PHA_LE:
                NangCapSaoPhaLe.nangCapSaoPhaLe(player);
                break;
            case DANH_BONG_SAO_PHA_LE:
                DanhBongSaoPhaLe.danhBongSaoPhaLe(player);
                break;
            case CUONG_HOA_LO_SAO:
                CuongHoaLoSao.cuongHoaLoSao(player);
                break;
            case TAO_DA_HEMATITE:
                TaoDaHematite.taoDaHematite(player);
                break;
            case LAM_PHEP_NHAP_DA:
                LamPhepNhapDa.lamphepnhapda(player);
                break;
            case CHUYEN_HOA_BANG_NGOC:
                ChuyenHoaBangNgoc.ChuyenHoaBangngoc(player);
                break;
            case CHUYEN_HOA_BANG_VANG:
                ChuyenHoaBangVang.ChuyenHoaBangvang(player);
                break;
            case NANG_DE_FIDE:
                NangDeFide.nangDeFide(player);
                break;
            case NANG_DE_XEN:
                NangDeXen.nangDeXen(player);
                break;
            case NANG_DE_MABU:
                NangDeKidbu.nangDeKidbu(player);
                break;
            case NANG_DE_KID_BEERUS:
                NangDeKidBeerus.nangDeKidBeerus(player);
                break;
            case PHAN_RA_DO_THAN_LINH:
                PhanRaDoThanLinh.phanRaDoThanLinh(player);
                break;
            case NANG_CAP_CHAN_THIEN_TU:
                NangCapChanThienTu.nangCapChanThienTu(player);
                break;
        }

        player.idMark.setIndexMenu(ConstNpc.IGNORE_MENU);
        player.combineNew.clearParamCombine();
        player.combineNew.lastTimeCombine = System.currentTimeMillis();

    }

    public void startCombineVip(Player player, int n) {
        switch (player.combineNew.typeCombine) {
            case PHA_LE_HOA_TRANG_BI:
                PhaLeHoaTrangBi.phaLeHoa(player, n);
                break;
        }

        player.idMark.setIndexMenu(ConstNpc.IGNORE_MENU);
        player.combineNew.clearParamCombine();
        player.combineNew.lastTimeCombine = System.currentTimeMillis();

    }

    /**
     * Mở tab đập đồ
     *
     * @param player
     * @param type   kiểu đập đồ
     */
    public void openTabCombine(Player player, int type) {
        player.combineNew.setTypeCombine(type);
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_TAB_COMBINE);
            msg.writer().writeUTF(getTextInfoTabCombine(type));
            msg.writer().writeUTF(getTextTopTabCombine(type));
            if (player.idMark.getNpcChose() != null) {
                msg.writer().writeShort(player.idMark.getNpcChose().tempId);
            }
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    /**
     * Hiệu ứng mở item
     *
     * @param player
     * @param icon1
     * @param icon2
     */
    public void sendEffectOpenItem(Player player, short icon1, short icon2) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_ITEM);
            msg.writer().writeShort(icon1);
            msg.writer().writeShort(icon2);
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void sendEffectCombineItem(Player player, byte type, short icon1, short icon2) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(type);
            switch (type) {
                case 0:
                    msg.writer().writeUTF("");
                    msg.writer().writeUTF("");
                    break;
                case 1:
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(-1);
                    break;
                case 2: // success 0 eff 0
                case 3: // success 1 eff 0
                    break;
                case 4: // success 0 eff 1
                    msg.writer().writeShort(icon1);
                    break;
                case 5: // success 0 eff 2
                    msg.writer().writeShort(icon1);
                    break;
                case 6: // success 0 eff 3
                    msg.writer().writeShort(icon1);
                    msg.writer().writeShort(icon2);
                    break;
                case 7: // success 0 eff 4
                    msg.writer().writeShort(icon1);
                    break;
                case 8: // success 1 eff 4
                    // Lam do ts
                    break;
            }
            msg.writer().writeShort(-1); // id npc
            msg.writer().writeShort(-1); // x
            msg.writer().writeShort(-1); // y
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    /**
     * Hiệu ứng đập đồ thành công
     *
     * @param player
     */
    public void sendEffectSuccessCombine(Player player) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_SUCCESS);
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    /**
     * Hiệu ứng đập đồ thất bại
     *
     * @param player
     */
    public void sendEffectFailCombine(Player player) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_FAIL);
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public boolean isTrangBiGoc(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (isDoLuongLong(item) || isDoJean(item) || isDoZelot(item)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isTrangBiChuyenHoa(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (isDoThanXD(item) || isDoThanTD(item) || isDoThanNM(item)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isCheckTrungTypevsGender(Item item, Item item2) {
        if (item != null && item.isNotNullItem() && item2 != null && item2.isNotNullItem()) {
            if (item.template.type == item2.template.type && item.template.gender == item2.template.gender) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isDoLuongLong(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 241 || item.template.id == 253 || item.template.id == 265 || item.template.id == 277
                    || item.template.id == 281) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isDoThanNM(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 559 - 2 || item.template.id == 560 - 2 || item.template.id == 566 - 2
                    || item.template.id == 567 - 2 || item.template.id == 561) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isDoZelot(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 241 - 4 || item.template.id == 253 - 4 || item.template.id == 265 - 4
                    || item.template.id == 277 - 4 || item.template.id == 281) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isDoJean(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 241 - 8 || item.template.id == 253 - 8 || item.template.id == 265 - 8
                    || item.template.id == 277 - 8 || item.template.id == 281) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isDoThanXD(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 559 || item.template.id == 560 || item.template.id == 566 || item.template.id == 567
                    || item.template.id == 561) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isDoThanTD(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 559 - 4 || item.template.id == 560 - 4 || item.template.id == 566 - 4
                    || item.template.id == 567 - 4 || item.template.id == 561) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean CheckSlot(Item trangBi, int starEmpty) {
        if (starEmpty < 8) {
            // Nếu starEmpty nhỏ hơn 8, không cần kiểm tra cường hóa, trả về true ngay
            return true;
        }

        // Nếu starEmpty >= 8, kiểm tra cường hóa
        for (ItemOption io : trangBi.itemOptions) {
            if (starEmpty == 8 && io.optionTemplate.id == 228) {
                return io.param >= 8;
            } else if (starEmpty == 9 && io.optionTemplate.id == 228) {
                return io.param >= 9;
            }
        }

        return false;

    }

    /**
     * Gửi lại danh sách đồ trong tab combine
     *
     * @param player
     */
    public void reOpenItemCombine(Player player) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(REOPEN_TAB_COMBINE);
            msg.writer().writeByte(player.combineNew.itemsCombine.size());
            for (Item it : player.combineNew.itemsCombine) {
                for (int j = 0; j < player.inventory.itemsBag.size(); j++) {
                    if (it == player.inventory.itemsBag.get(j)) {
                        msg.writer().writeByte(j);
                    }
                }
            }
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    /**
     * Hiệu ứng ghép ngọc rồng
     *
     * @param player
     * @param icon
     */
    public void sendEffectCombineDB(Player player, short icon) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_DRAGON_BALL);
            msg.writer().writeShort(icon);
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private String getTextTopTabCombine(int type) {
        switch (type) {
            case GHEP_RUONG_SKH_VIP:
                return "Ta sẽ ghép rương SKH thường thành SKH vip";
            case EP_SAO_TRANG_BI:
                return "Ta sẽ phù phép\ncho trang bị của ngươi\ntrở lên mạnh mẽ";
            case PHA_LE_HOA_TRANG_BI:
                return "Ta sẽ phù phép\ncho trang bị của ngươi\ntrở thành trang bị pha lê";
            case NHAP_NGOC_RONG:
                return "Ta sẽ phù phép\ncho 7 viên Ngọc Rồng\nthành 1 viên Ngọc Rồng cấp cao";
            case NANG_CAP_VAT_PHAM:
                return "Ta sẽ phù phép cho trang bị của ngươi trở lên mạnh mẽ";
            case PHAN_RA_DO_THAN_LINH:
                return "Ta sẽ phân rã \n  trang bị của người thành điểm!";
            case NANG_CAP_DO_TS:
                return "Ta sẽ nâng cấp \n  trang bị của người thành\n đồ thiên sứ!";
            case NANG_CAP_BONG_TAI:
                return "NÂNG CẤP BÔNG TAI\n"
                        + "Porata->Porata cấp 2,3,4";
            case NANG_CHI_SO_BONG_TAI:
                return "MỞ CHỈ SỐ BÔNG TAI\n"
                        + "Cho Porata 2,3,4";
            // START _ NEW PHA LÊ HÓA //
            case NANG_CAP_SAO_PHA_LE:
                return "Ta sẽ phù phép\nnâng cấp Sao Pha Lê\nthành cấp 2";
            case DANH_BONG_SAO_PHA_LE:
                return "Đánh bóng\nSao pha lê cấp 2";
            case CUONG_HOA_LO_SAO:
                return "Cường hóa\nÔ Sao Pha lê";
            case TAO_DA_HEMATITE:
                return "Ta sẽ phù phép\n"
                        + "tạo đá Hematite";
            case LAM_PHEP_NHAP_DA:
                return "Ta sẽ phù phép\n"
                        + "tạo đá nâng cấp";
            case CHUYEN_HOA_BANG_NGOC:
            case CHUYEN_HOA_BANG_VANG:
                return "Ta sẽ phù phép\ncho trang bị của ngươi\n chuyển hóa thành trang bị khác";
            case NANG_DE_FIDE:
            case NANG_DE_XEN:
            case NANG_DE_MABU:
            case NANG_DE_KID_BEERUS:
                return "Ta sẽ phù phép\ncho đệ tử của ngươi!";
            case NANG_CAP_CHAN_THIEN_TU:
                return "Ta sẽ phù phép\ncho vòng chân thiên tử của ngươi\ntrở nên mạnh mẽ hơn!";
            default:
                return "";
        }
    }

    private String getTextInfoTabCombine(int type) {
        switch (type) {
            case GHEP_RUONG_SKH_VIP:
                return "Cho 3 Rương SKh Thường vào\n sau đó chọn Nâng Cấp";
            case EP_SAO_TRANG_BI:
                return "Chọn trang bị\n(Áo, quần, găng, giày hoặc rađa) có ô đặt sao pha lê\nChọn loại sao pha lê\n"
                        + "Sau đó chọn 'Nâng cấp'";
            case PHA_LE_HOA_TRANG_BI:
                return "Chọn trang bị\n(Áo, quần, găng, giày hoặc rađa)\nSau đó chọn 'Nâng cấp'";
            case NHAP_NGOC_RONG:
                return "Vào hành trang\nChọn 7 viên ngọc cùng sao\nSau đó chọn 'Làm phép'";
            case NANG_CAP_VAT_PHAM:
                return "vào hành trang\nChọn trang bị\n(Áo, quần, găng, giày hoặc rađa)\nChọn loại đá để nâng cấp\n"
                        + "Sau đó chọn 'Nâng cấp'";
            case PHAN_RA_DO_THAN_LINH:
                return "vào hành trang\nChọn trang bị\n(Áo, quần, găng, giày hoặc rađa)\nChọn loại đá để phân rã\n"
                        + "Sau đó chọn 'Phân Rã'";
            case NANG_CAP_DO_TS:
                return "vào hành trang\nChọn 1 công thức or công thức Vip\nkèm 1 đá nâng, 1 đá may mắn\n và 999 mảnh thiên sứ\n "
                        + "Ta sẽ cho ra đồ thiên sứ từ 0-15% chỉ số\n"
                        + "Sau đó chọn 'Nâng Cấp'";
            case NANG_CAP_BONG_TAI:
                return "Số lượng mảnh vỡ cần:\n"
                        + "Bông tai 1 -> 2: 9999 mảnh\n"
                        + "50% Thành công, thất bại mất 999 Mảnh\n"
                        + "Bông tai 2 -> 3: 29999 mảnh\n"
                        + "40% Thành công, thất bại mất 2999 Mảnh\n"
                        + "Bông tai 3 -> 4: 99999 mảnh\n"
                        + "30% Thành công, thất bại mất 9999 Mảnh\n"
                        + "Chúc bạn may mắn";
            case NANG_CHI_SO_BONG_TAI:
                return "Nguyên liệu:\n"
                        + "1 Đá xanh lam - mua shop\n"
                        + "99 Mảnh hồn - map bang hội\n"
                        + "Chỉ số ngẫu nhiên\n"
                        + "Bt 2 max 15%\n"
                        + "Bt 3 max 25%\n"
                        + "Bt 4 max 35%\n"
                        + "Chúc bạn may mắn";
            // START _ NEW PHA LÊ HÓA //
            case NANG_CAP_SAO_PHA_LE:
                return "Vào hành trang\nChọn đá Hematite\n Chọn loại sao pha lê (cấp 1)\nSau đó chọn 'Nâng cấp'";
            case DANH_BONG_SAO_PHA_LE:
                return "Vào hành trang\nChọn loại sao pha lê cấp 2 có từ 2 viên trở\nlên\nChọn 1 loại đá mài\nSau đó chọn 'Đánh bóng'";
            case CUONG_HOA_LO_SAO:
                return "Vào hành trang\n"
                        + "Chọn trang bị có Ô sao thứ 8 trở lên chưa\n"
                        + "cường hóa\n"
                        + "Chọn đá Hematite\n"
                        + "Chọn dùi đục\n"
                        + "Sau đó chọn 'Cường hóa'";
            case TAO_DA_HEMATITE:
                return "Vào hành trang\n"
                        + "Chọn 5 sao pha lê cấp 2 cùng màu\n"
                        + "Chọn 'Tạo đá Hematite'";
            // END _ NEW PHA LÊ HÓA //
            case LAM_PHEP_NHAP_DA:
                return "Vào hành trang\n"
                        + "Chọn 10 mảnh đá vụn và 1 bình nước phép\n"
                        + "Chọn Nâng Cấp";
            case CHUYEN_HOA_BANG_NGOC:
            case CHUYEN_HOA_BANG_VANG:
                return "Vào hành trang\nChọn trang bị gốc ô 1\n(Áo,quần,găng,giày hoặc rada)\ntừ cấp[+4] trở lên\nChọn tiếp trang bị cần chuyển hóa ô 2\nvà chưa nâng cấp\nsau đó chọn 'Nâng cấp'";
            case NANG_DE_FIDE:
                return "Vào hành trang\nChọn 200 Mảnh Fide\n5 Trứng Mabư\n 10 Đá ngũ sắc\nsau đó chọn 'Nâng cấp'";

            case NANG_DE_XEN:
                return "Vào hành trang\nChọn 500 Mảnh Xên\n500 Mảnh Fide\n 10 Trứng Mabư\n 20 Đá ngũ sắc\nsau đó chọn 'Nâng cấp'";

            case NANG_DE_MABU:
                return "Vào hành trang\nChọn 500 Mảnh Kidbu\n500 Mảnh Xên\n500 Mảnh Fide\n 20 Trứng Mabư\n 30 Đá ngũ sắc\nsau đó chọn 'Nâng cấp'";

            case NANG_DE_KID_BEERUS:
                return "Vào hành trang\nChọn 3000 Bình hút năng lượng\n30 Trứng Mabư\n50 Đá ngũ sắc\nsau đó chọn 'Nâng cấp'";

            case NANG_CAP_CHAN_THIEN_TU:
                return "Vào hành trang\nChọn 1 vòng chân thiên tử\nChọn Tinh thể và Ma quái\ntương ứng theo cấp độ\nSau đó chọn 'Nâng cấp'";

            default:
        }
        return "";
    }

}
