package author_ngojc;

import database.PlayerDAO;
import consts.ConstItem;
import item.Item;
import player.Player;
import player.Inventory;
import services.Service;
import services.InventoryService;
import services.ItemService;
import java.util.concurrent.ConcurrentHashMap;

public class GoldBarService {
    
    private static GoldBarService instance;
    private final ConcurrentHashMap<Integer, Long> lastUpdateCache = new ConcurrentHashMap<>();
    private static final long CACHE_TIMEOUT = 30000;
    private final ConcurrentHashMap<Integer, Long> lastActionCache = new ConcurrentHashMap<>();
    private static final long ACTION_COOLDOWN = 1000;
    private final ConcurrentHashMap<Integer, Boolean> transactionLocks = new ConcurrentHashMap<>();
    
    public static GoldBarService gI() {
        if (instance == null) {
            instance = new GoldBarService();
        }
        return instance;
    }
    
    private static final int[][] TY_LE_QUY_DOI = {
        {10000, 25}, {20000, 50}, {50000, 125}, 
        {100000, 250}, {200000, 500}, {500000, 1300}, {1000000, 2750}
    };
    
    public int[] getTyLeQuiDoi(int index) {
        if (index < 0 || index >= TY_LE_QUY_DOI.length) {
            return null;
        }
        return TY_LE_QUY_DOI[index];
    }
    
    public boolean quiDoiVNDThanhThoiVang(Player player, int indexTyLe) {
        if (player == null || player.getSession() == null) {
            return false;
        }
        
        int userId = player.getSession().userId;
        long thoiGianHienTai = System.currentTimeMillis();
        
        Long lanCuoi = lastActionCache.get(userId);
        if (lanCuoi != null && (thoiGianHienTai - lanCuoi) < ACTION_COOLDOWN) {
            Service.gI().sendThongBao(player, "Vui lòng đợi " + ((ACTION_COOLDOWN - (thoiGianHienTai - lanCuoi)) / 1000 + 1) + " giây!");
            return false;
        }
        
        if (transactionLocks.putIfAbsent(userId, true) != null) {
            Service.gI().sendThongBao(player, "Bạn đang có giao dịch khác! Vui lòng đợi.");
            return false;
        }
        
        try {
            int[] tyLe = getTyLeQuiDoi(indexTyLe);
            if (tyLe == null) {
                Service.gI().sendThongBao(player, "Gói quy đổi không hợp lệ!");
                return false;
            }
            
            int vndCanTru = tyLe[0];
            int thoiVangNhan = tyLe[1];
            int vndHienTai = player.getSession().cash;
            int thoiVangHienTai = player.getSession().goldBar;
            
            if (vndHienTai < vndCanTru) {
                Service.gI().sendThongBao(player, "Bạn không đủ " + dinhDangSo(vndCanTru) + " VND!\nVND hiện có: " + dinhDangSo(vndHienTai));
                return false;
            }
            
            if (thoiVangHienTai > Integer.MAX_VALUE - thoiVangNhan - 1000000) {
                Service.gI().sendThongBao(player, "Số thỏi vàng sau khi quy đổi vượt quá giới hạn!");
                return false;
            }
            
            if (!PlayerDAO.subcash(player, vndCanTru)) {
                Service.gI().sendThongBao(player, "Lỗi khi trừ VND! Vui lòng thử lại.");
                return false;
            }
            
            if (!PlayerDAO.addGoldBar(player, thoiVangNhan)) {
                Service.gI().sendThongBao(player, "Lỗi khi cộng thỏi vàng! Đang hoàn lại VND...");
                PlayerDAO.addcash(player, vndCanTru);
                return false;
            }
            
            lastActionCache.put(userId, thoiGianHienTai);
            capNhatCache(player);
            
            Service.gI().sendThongBao(player, 
                "Quy đổi thành công!\n" +
                "Trừ: " + dinhDangSo(vndCanTru) + " VND\n" +
                "Nhận: " + thoiVangNhan + " thỏi vàng\n" +
                "VND còn lại: " + dinhDangSo(player.getSession().cash));
            
            return true;
            
        } catch (Exception e) {
            Service.gI().sendThongBao(player, "Có lỗi xảy ra! Vui lòng thử lại.");
            return false;
        } finally {
            transactionLocks.remove(userId);
        }
    }
    
    public boolean rutThoiVang(Player player, int soLuong) {
        if (player == null || player.getSession() == null || player.inventory == null) {
            return false;
        }
        
        int userId = player.getSession().userId;
        long thoiGianHienTai = System.currentTimeMillis();
        
        Long lanCuoi = lastActionCache.get(userId);
        if (lanCuoi != null && (thoiGianHienTai - lanCuoi) < ACTION_COOLDOWN) {
            Service.gI().sendThongBao(player, "Vui lòng đợi " + ((ACTION_COOLDOWN - (thoiGianHienTai - lanCuoi)) / 1000 + 1) + " giây!");
            return false;
        }
        
        if (transactionLocks.putIfAbsent(userId, true) != null) {
            Service.gI().sendThongBao(player, "Bạn đang có giao dịch khác! Vui lòng đợi.");
            return false;
        }
        
        try {
            if (soLuong <= 0) {
                Service.gI().sendThongBao(player, "Số lượng phải lớn hơn 0!");
                return false;
            }
            
            if (soLuong > 50000000) {
                Service.gI().sendThongBao(player, "Số lượng quá lớn! Tối đa 50 triệu thỏi vàng.");
                return false;
            }
            
            int thoiVangHienTai = player.getSession().goldBar;
            if (thoiVangHienTai < soLuong) {
                Service.gI().sendThongBao(player, "Bạn không đủ " + soLuong + " thỏi vàng!\nThỏi vàng hiện có: " + thoiVangHienTai);
                return false;
            }
            
            Item thoiVangCoSan = InventoryService.gI().findItemBag(player, ConstItem.THOI_VANG);
            
            // Chỉ kiểm tra overflow nếu đã có thỏi vàng trong hành trang
            if (thoiVangCoSan != null) {
                if (thoiVangCoSan.quantity > Integer.MAX_VALUE - soLuong) {
                    Service.gI().sendThongBao(player, "Số thỏi vàng vượt quá giới hạn!");
                    return false;
                }
            }
            // Nếu null (chưa có) thì vẫn cho phép rút, sẽ tạo item mới
            
            if (!PlayerDAO.subGoldBar(player, soLuong)) {
                Service.gI().sendThongBao(player, "Lỗi khi trừ thỏi vàng! Vui lòng thử lại.");
                return false;
            }
            
            Item thoiVangMoi = ItemService.gI().createNewItem((short) ConstItem.THOI_VANG, soLuong);
            if (thoiVangMoi == null) {
                Service.gI().sendThongBao(player, "Lỗi tạo item! Hoàn lại thỏi vàng...");
                PlayerDAO.addGoldBar(player, soLuong);
                return false;
            }
            
            if (!InventoryService.gI().addItemBag(player, thoiVangMoi)) {
                Service.gI().sendThongBao(player, "Lỗi thêm vào hành trang! Hoàn lại thỏi vàng...");
                PlayerDAO.addGoldBar(player, soLuong);
                return false;
            }
            
            InventoryService.gI().sendItemBags(player);
            lastActionCache.put(userId, thoiGianHienTai);
            capNhatCache(player);
            
            Service.gI().sendThongBao(player, 
                "Rút thành công " + soLuong + " thỏi vàng!\n" +
                "Thỏi vàng còn lại: " + player.getSession().goldBar);
            
            return true;
            
        } catch (Exception e) {
            Service.gI().sendThongBao(player, "Có lỗi xảy ra! Vui lòng thử lại.");
            return false;
        } finally {
            transactionLocks.remove(userId);
        }
    }
    
    public int laySoThoiVang(Player player) {
        if (player == null || player.getSession() == null) {
            return 0;
        }
        return Math.max(0, player.getSession().goldBar);
    }
    
    public String dinhDangSo(int soTien) {
        if (soTien >= 1000000) {
            return (soTien / 1000000) + "M";
        } else if (soTien >= 1000) {
            return (soTien / 1000) + "K";
        }
        return String.valueOf(soTien);
    }
    
    private void capNhatCache(Player player) {
        lastUpdateCache.put(player.getSession().userId, System.currentTimeMillis());
    }
    
    public void dontDepCache() {
        long thoiGianHienTai = System.currentTimeMillis();
        lastUpdateCache.entrySet().removeIf(entry -> 
            (thoiGianHienTai - entry.getValue()) > CACHE_TIMEOUT);
        lastActionCache.entrySet().removeIf(entry -> 
            (thoiGianHienTai - entry.getValue()) > (ACTION_COOLDOWN * 10));
    }
}
