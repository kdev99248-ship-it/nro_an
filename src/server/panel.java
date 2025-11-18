package server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

// Nếu có thể, dùng com.sun.management để lấy CPU chính xác hơn
// (không bắt buộc, có try-catch fallback bên dưới)
import com.sun.management.OperatingSystemMXBean;
import network.SessionManager;

public class panel extends JPanel {

    // ====== UI components ======
    private final JLabel lblTitle = new JLabel("BẢNG ĐIỀU KHIỂN MÁY CHỦ", SwingConstants.LEFT);
    private final JLabel lblSubTitle = new JLabel("Hành Trình Ngọc Rồng", SwingConstants.LEFT);

    private final JLabel lblTimeStart = new JLabel();
    private final JLabel lblClients = new JLabel();
    private final JLabel lblSessions = new JLabel();
    private final JLabel lblThreads = new JLabel();
    private final JLabel lblCpu = new JLabel();
    private final JLabel lblRam = new JLabel();
    private final JLabel lblUptime = new JLabel();
    private final JLabel lblMaint = new JLabel();

    private final JProgressBar cpuBar = new JProgressBar(0, 100);
    private final JProgressBar ramBar = new JProgressBar(0, 100);

    private final JTextArea txtLog = new JTextArea(6, 40);

    private final JButton btnMaint = new JButton("Bảo trì máy chủ");
    private final JButton btnKickAll = new JButton("Đá toàn bộ người chơi");
    private final JButton btnGC = new JButton("Dọn bộ nhớ (GC)");
    private final JButton btnRefresh = new JButton("Làm mới ngay");

    private final Timer uiTimer;

    private final DecimalFormat pctFmt = new DecimalFormat("0.0");

    private final long processStartMillis = System.currentTimeMillis(); // fallback nếu bạn không có DragonRun.timeStart

    public panel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // ==== Header ====
        JPanel header = new JPanel(new BorderLayout(8, 4));
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        lblSubTitle.setFont(lblSubTitle.getFont().deriveFont(Font.PLAIN, 12f));
        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSubTitle, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ==== Center: Stat + Actions + Log ====
        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;

        // ---- Stats panel ----
        JPanel stats = makeStatsPanel();
        gc.gridx = 0; gc.gridy = 0;
        center.add(stats, gc);

        // ---- Actions panel (buttons cùng kích thước) ----
        JPanel actions = makeActionsPanel();
        gc.gridx = 0; gc.gridy = 1;
        center.add(actions, gc);

        // ---- Log panel ----
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Thông báo / Nhật ký"));
        txtLog.setEditable(false);
        txtLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logPanel.add(new JScrollPane(txtLog,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        gc.gridx = 0; gc.gridy = 2; gc.weighty = 1;
        center.add(logPanel, gc);

        add(center, BorderLayout.CENTER);

        // ==== Footer (status line) ====
        JPanel footer = new JPanel(new BorderLayout());
        JLabel hint = new JLabel("Cập nhật mỗi giây • Nhấn “Làm mới ngay” để cập nhật tức thì");
        hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 11f));
        footer.add(hint, BorderLayout.LINE_START);
        add(footer, BorderLayout.SOUTH);

        // ==== Events ====
        btnMaint.addActionListener(this::onMaintenance);
        btnKickAll.addActionListener(this::onKickAll);
        btnGC.addActionListener(e -> {
            long before = usedMemory();
            System.gc();
            long after = usedMemory();
            post("Đã gọi GC: " + humanBytes(before) + " → " + humanBytes(after));
            refreshStats();
        });
        btnRefresh.addActionListener(e -> refreshStats());

        // ==== Auto update timer ====
        uiTimer = new Timer(1000, e -> refreshStats());
        uiTimer.start();

        // ==== First fill ====
        refreshStats();
    }

    // ================= UI sub-panels =================

    private JPanel makeStatsPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Trạng thái hệ thống"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; c.weightx = 0;

        // Left labels
        p.add(new JLabel("Time start:"), c);
        c.gridy++; p.add(new JLabel("Clients:"), c);
        c.gridy++; p.add(new JLabel("Sessions:"), c);
        c.gridy++; p.add(new JLabel("Threads:"), c);
        c.gridy++; p.add(new JLabel("CPU:"), c);
        c.gridy++; p.add(new JLabel("RAM:"), c);
        c.gridy++; p.add(new JLabel("Uptime:"), c);
        c.gridy++; p.add(new JLabel("Bảo trì:"), c);

        // Right values
        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        p.add(lblTimeStart, c);
        c.gridy++; p.add(lblClients, c);
        c.gridy++; p.add(lblSessions, c);
        c.gridy++; p.add(lblThreads, c);

        // CPU row with progress bar
        c.gridy++;
        JPanel cpuRow = new JPanel(new BorderLayout(6, 0));
        cpuBar.setStringPainted(true);
        cpuRow.add(cpuBar, BorderLayout.CENTER);
        cpuRow.add(lblCpu, BorderLayout.EAST);
        p.add(cpuRow, c);

        // RAM row with progress bar
        c.gridy++;
        JPanel ramRow = new JPanel(new BorderLayout(6, 0));
        ramBar.setStringPainted(true);
        ramRow.add(ramBar, BorderLayout.CENTER);
        ramRow.add(lblRam, BorderLayout.EAST);
        p.add(ramRow, c);

        c.gridy++; p.add(lblUptime, c);
        c.gridy++; p.add(lblMaint, c);

        return p;
    }

    private JPanel makeActionsPanel() {
        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10)); // 2 hàng x 2 cột, các nút tự đều
        p.setBorder(BorderFactory.createTitledBorder("Thao tác nhanh"));

        stylePrimary(btnMaint);
        styleWarning(btnKickAll);
        styleNeutral(btnGC);
        styleNeutral(btnRefresh);

        p.add(btnMaint);
        p.add(btnKickAll);
        p.add(btnGC);
        p.add(btnRefresh);

        return p;
    }

    private void stylePrimary(JButton b) {
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
        b.setBackground(new Color(0x1976D2));
        b.setForeground(Color.WHITE);
    }

    private void styleWarning(JButton b) {
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
        b.setBackground(new Color(0xD32F2F));
        b.setForeground(Color.WHITE);
    }

    private void styleNeutral(JButton b) {
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
        b.setBackground(new Color(0x455A64));
        b.setForeground(Color.WHITE);
    }

    // ================= Actions =================

    private void onMaintenance(ActionEvent e) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bật chế độ bảo trì sau 10 giây?\nNgười chơi sẽ bị ngắt kết nối.",
                "Xác nhận bảo trì",
                JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Gọi thật trong server của bạn
                Maintenance.gI().start(10);
                post("Đã yêu cầu vào bảo trì sau 10 giây.");
            } catch (Exception ex) {
                post("Lỗi bật bảo trì: " + ex.getMessage());
            }
            refreshStats();
        }
    }

    private void onKickAll(ActionEvent e) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Đá toàn bộ người chơi ngay?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    // Gọi thật trong server của bạn
                     Client.gI().close();
                    post("Đã gửi lệnh đá toàn bộ người chơi.");
                } catch (Exception ex) {
                    post("Lỗi đá người chơi: " + ex.getMessage());
                }
                refreshStats();
            }).start();
        }
    }

    // ================= Metrics & Refresh =================

    private void refreshStats() {
        // Time start
        String timeStartText;
        try {
            // Nếu bạn có DragonRun.timeStart dạng long epoch millis:
            long ts = /* DragonRun.timeStart */ processStartMillis;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
            timeStartText = fmt.format(new Date(ts).toInstant());
        } catch (Throwable t) {
            timeStartText = "(không xác định)";
        }
        lblTimeStart.setText(timeStartText);

        // Clients / Sessions / Threads
        int playerCount = safeCallInt(() -> Client.gI().getPlayers().size());
        int sessionCount = safeCallInt(() -> SessionManager.gI().getNumSession());
        int threadCount = Thread.activeCount();

        lblClients.setText(playerCount + " người chơi");
        lblSessions.setText(sessionCount + " sessions");
        lblThreads.setText(threadCount + " luồng");

        // CPU
        double cpuPct = readCpuProcessPercent();
        cpuBar.setValue((int) Math.round(cpuPct));
        cpuBar.setString(pctFmt.format(cpuPct) + " %");
        lblCpu.setText(pctFmt.format(cpuPct) + " %");

        // RAM
        long used = usedMemory();
        long total = Runtime.getRuntime().totalMemory();
        long max = Runtime.getRuntime().maxMemory();
        int ramPct = (int) Math.round((used * 100.0) / Math.max(1, max));
        ramBar.setValue(ramPct);
        ramBar.setString(ramPct + " %");
        lblRam.setText(humanBytes(used) + " / " + humanBytes(max));

        // Uptime
        long start = /* DragonRun.timeStart */ processStartMillis;
        Duration up = Duration.ofMillis(System.currentTimeMillis() - start);
        lblUptime.setText(formatDuration(up));

        // Maintenance state (nếu có API)
        boolean maint = safeCallBool(() -> /* Maintenance.gI().isRunning() */ false);
        lblMaint.setText(maint ? "ĐANG BẢO TRÌ" : "BÌNH THƯỜNG");
        lblMaint.setForeground(maint ? new Color(0xD32F2F) : new Color(0x2E7D32));

        // Nếu bạn có SystemMetrics.ToString() muốn hiện thêm:
        // postOncePerMinute(SystemMetrics.ToString());
    }

    // Đọc CPU phần trăm tiến trình (ưu tiên com.sun.management)
    private double readCpuProcessPercent() {
        try {
            java.lang.management.OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
            if (os instanceof OperatingSystemMXBean) {
                OperatingSystemMXBean mx = (OperatingSystemMXBean) os;
                double p = mx.getProcessCpuLoad(); // 0..1
                if (p >= 0) return p * 100.0;
            }
        } catch (Throwable ignored) {}
        // Fallback thô (load average theo core)
        try {
            java.lang.management.OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
            double load = os.getSystemLoadAverage(); // có thể -1
            int cores = Runtime.getRuntime().availableProcessors();
            if (load >= 0 && cores > 0) {
                double pct = Math.min(100.0, (load / cores) * 100.0);
                return Math.max(0.0, pct);
            }
        } catch (Throwable ignored) {}
        return 0.0;
    }

    private long usedMemory() {
        Runtime r = Runtime.getRuntime();
        return r.totalMemory() - r.freeMemory();
    }

    private String humanBytes(long b) {
        final String[] u = {"B", "KB", "MB", "GB", "TB"};
        double v = b;
        int i = 0;
        while (v >= 1024 && i < u.length - 1) {
            v /= 1024;
            i++;
        }
        return new DecimalFormat("0.##").format(v) + " " + u[i];
    }

    private String formatDuration(Duration d) {
        long days = d.toDays();
        long hours = d.minusDays(days).toHours();
        long minutes = d.minusDays(days).minusHours(hours).toMinutes();
        long seconds = d.getSeconds() % 60;
        if (days > 0) return String.format("%dd %02dh %02dm %02ds", days, hours, minutes, seconds);
        if (hours > 0) return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
        return String.format("%02dm %02ds", minutes, seconds);
    }

    private void post(String line) {
        txtLog.append("• " + line + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    private int safeCallInt(SupplierInt s) {
        try { return s.get(); } catch (Throwable t) { return 0; }
    }
    private boolean safeCallBool(SupplierBool s) {
        try { return s.get(); } catch (Throwable t) { return false; }
    }
    @FunctionalInterface private interface SupplierInt { int get(); }
    @FunctionalInterface private interface SupplierBool { boolean get(); }

    // ================= Entry point demo =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Hành Trình Ngọc Rồng • Server Dashboard");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new panel());
            f.setSize(760, 560);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
