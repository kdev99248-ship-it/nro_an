package utils;

/*
 * @Author: NgojcDev
 */

import control.boss.BossManager;
import item.Item;

import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.*;

import mob.Mob;
import npc.Npc;
import player.Player;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;

import java.time.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import map.ItemMap;
import map.Zone;
import services.ItemService;
import services.MapService;
import services.Service;

public class Util {

    private static final Random rand;

    static {
        rand = new Random();
    }

    public static int createIdBossClone(int idPlayer) {
        return -idPlayer - 1_000_000_000;
    }

    public static boolean contains(String[] arr, String key) {
        return Arrays.toString(arr).contains(key);
    }

    public static String numberToMoney(long power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        num.setMaximumFractionDigits(1);
        if (power >= 1000000000) {
            return num.format((double) power / 1000000000) + " Tỷ";
        } else if (power >= 1000000) {
            return num.format((double) power / 1000000) + " Tr";
        } else if (power >= 1000) {
            return num.format((double) power / 1000) + " k";
        } else {
            return num.format(power);
        }
    }

    public static String powerToString(long power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        num.setMaximumFractionDigits(1);
        if (power >= 1000000000) {
            return num.format((double) power / 1000000000) + " Tỷ";
        } else if (power >= 1000000) {
            return num.format((double) power / 1000000) + " Tr";
        } else if (power >= 1000) {
            return num.format((double) power / 1000) + " k";
        } else {
            return num.format(power);
        }
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static int getDistance(Player pl1, Player pl2) {
        return getDistance(pl1.location.x, pl1.location.y, pl2.location.x, pl2.location.y);
    }

    public static int getDistance(Player pl, Npc npc) {
        return getDistance(pl.location.x, pl.location.y, npc.cx, npc.cy);
    }

    public static int getDistance(Player pl, Mob mob) {
        return getDistance(pl.location.x, pl.location.y, mob.location.x, mob.location.y);
    }

    public static int getDistance(Mob mob1, Mob mob2) {
        return getDistance(mob1.location.x, mob1.location.y, mob2.location.x, mob2.location.y);
    }

    public static int maxIntValue(int a) {
        if (a > Integer.MAX_VALUE) {
            a = Integer.MAX_VALUE;
        }
        return a;
    }

    public static int nextInt(int from, int to) {
        return from + rand.nextInt(to - from + 1);
    }

    public static int nextInt(int max) {
        return rand.nextInt(max);
    }

    public static long nextLong(long from, long to) {
        return from + rand.nextLong(to - from + 1);
    }

    public static long nextLong(long max) {
        return rand.nextLong(max);
    }

    public static Zone NgojcMap() {
        int mapId = Util.nextInt(0, 20);
        Zone zoneJoin = MapService.gI().getMapWithRandZone(mapId);
        while (zoneJoin == null || zoneJoin.zoneId == 0) {
            try {
                zoneJoin = MapService.gI().getMapWithRandZone(mapId);
            } catch (Exception e) {
            }
        }
        return zoneJoin;
    }

    public static int nextInt(int[] percen) {
        int next = nextInt(1000), i;
        for (i = 0; i < percen.length; i++) {
            if (next < percen[i]) {
                return i;
            }
            next -= percen[i];
        }
        return i;
    }

    public static int getOne(int n1, int n2) {
        return rand.nextInt() % 2 == 0 ? n1 : n2;
    }

    public static boolean percent(double value) {
        // value = % cơ hội rơi (ví dụ: 0.1 = 0.1%, 5 = 5%, 100 = chắc chắn)
        if (value <= 0) {
            return false;
        }
        if (value >= 100) {
            return true;
        }
        long total = 1_000_000; // độ chính xác 0.0001%
        long success = (long) (value * 10_000); // 1% = 10_000 / 1,000,000
        long num = Util.nextLong(total);
        return num < success;
    }

    public static String replace(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }

    public static boolean isTrue(long ratioPercentage, long totalPercentage) {
        long num = Util.nextLong(totalPercentage);
        return num < ratioPercentage;
    }

    public static boolean isTrue(float ratioPercentage, long totalPercentage) {
        if (ratioPercentage < 1) {
            ratioPercentage *= 100;
            totalPercentage *= 100;
        }
        return isTrue((long) ratioPercentage, totalPercentage);
    }

    public static boolean isTrue(long ratioPercentage, long totalPercentage, int accuracy) {
        return Util.nextLong(totalPercentage * accuracy) < ratioPercentage && Util.nextInt(accuracy) == 0;
    }

    public static boolean isTrue(float ratioPercentage, long totalPercentage, int accuracy) {
        if (ratioPercentage < 1) {
            ratioPercentage *= 100;
            totalPercentage *= 100;
        }
        return isTrue((long) ratioPercentage, totalPercentage, accuracy);
    }

    public static boolean haveSpecialCharacter(String text) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        boolean b = m.find();
        return b || text.contains(" ");
    }

    public static boolean canDoWithTime(long lastTime, long miniTimeTarget) {
        return System.currentTimeMillis() - lastTime > miniTimeTarget;
    }

    private static final char[] SOURCE_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É',
            'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
            'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
            'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
            'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
            'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
            'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
            'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
            'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
            'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
            'ữ', 'Ự', 'ự',};

    private static final char[] DESTINATION_CHARACTERS = {'A', 'A', 'A', 'A', 'E',
            'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a',
            'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u',
            'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u',
            'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
            'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e',
            'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E',
            'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
            'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
            'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
            'U', 'u', 'U', 'u',};

    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
        if (index >= 0) {
            ch = DESTINATION_CHARACTERS[index];
        }
        return ch;
    }

    public static String removeAccent(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }

    public static Object[] addArray(Object[]... arrays) {
        if (arrays == null || arrays.length == 0) {
            return null;
        }
        if (arrays.length == 1) {
            return arrays[0];
        }
        Object[] arr0 = arrays[0];
        for (int i = 1; i < arrays.length; i++) {
            arr0 = ArrayUtils.addAll(arr0, arrays[i]);
        }
        return arr0;
    }

    public static String ngojcdevase(long number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(number).replace(',', '.');
    }

    public static int randomBossId() {
        int bossId = Util.nextInt(-1000000, -100000);
        while (BossManager.gI().getBossById(bossId) != null) {
            bossId = Util.nextInt(-1000000, 100000);
        }
        return bossId;
    }

    public static boolean isAfterMidnight(long currenttimemillis) {
        Instant instant = Instant.ofEpochMilli(currenttimemillis);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        LocalDate otherDate = zonedDateTime.toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return currentDate.isAfter(otherDate);
    }

    public static boolean isTimeDifferenceGreaterThanNDays(long setTime, int nDays) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - setTime;
        long daysDifference = timeDifference / 86400000;
        return daysDifference >= nDays;
    }

    public static String formatNumber(long j) {
        long j2 = (j / 1000) + 1;
        String str = "";
        int i = 0;
        while (((long) i) < j2) {
            if (j >= 1000) {
                long j3 = j % 1000;
                if (j3 == 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(".000");
                    stringBuilder.append(str);
                    str = stringBuilder.toString();
                } else {
                    StringBuilder stringBuilder2;
                    String str2;
                    if (j3 < 10) {
                        stringBuilder2 = new StringBuilder();
                        str2 = ".00";
                    } else if (j3 < 100) {
                        stringBuilder2 = new StringBuilder();
                        str2 = ".0";
                    } else {
                        stringBuilder2 = new StringBuilder();
                        str2 = ".";
                    }
                    stringBuilder2.append(str2);
                    stringBuilder2.append(j3);
                    stringBuilder2.append(str);
                    str = stringBuilder2.toString();
                }
                j /= 1000;
                i++;
            } else {
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(j);
                stringBuilder3.append(str);
                return stringBuilder3.toString();
            }
        }
        return str;
    }

    public static void threadPool(Runnable task) {
        ThreadManager.gI().submitIOTask(() -> {
            try {
                task.run();
            } catch (Exception e) {
                Logger.error(e + "\n");
            }
        });
    }

    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " bytes";
        } else if (bytes < 1024 * 1024) {
            return bytes / 1024 + " KB";
        } else if (bytes < 1024 * 1024 * 1024) {
            return bytes / (1024 * 1024) + " MB";
        } else {
            return bytes / (1024 * 1024 * 1024) + " GB";
        }
    }

    public static ItemMap saoPhaLe(Zone zone, int tempId, int quantity, int x, int y, int playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, y, playerId);
        switch (tempId) {
            case 441 -> it.options.add(new Item.ItemOption(95, 5));
            case 442 -> it.options.add(new Item.ItemOption(96, 5));
            case 443 -> it.options.add(new Item.ItemOption(97, 5));
            case 444 -> it.options.add(new Item.ItemOption(99, 3));
            case 445 -> it.options.add(new Item.ItemOption(98, 3));
            case 446 -> it.options.add(new Item.ItemOption(100, 5));
            case 447 -> it.options.add(new Item.ItemOption(101, 5));
            case 459 -> {
                it.options.add(new Item.ItemOption(112, 80));
                it.options.add(new Item.ItemOption(93, 90));
                it.options.add(new Item.ItemOption(30, 1));
            }
        }
        return it;
    }

    public static Item saoPhaLe(int tempId, int quantity) {
        Item it = ItemService.gI().createNewItem((short) tempId, quantity);
        switch (tempId) {
            case 441 -> it.itemOptions.add(new Item.ItemOption(95, 5));
            case 442 -> it.itemOptions.add(new Item.ItemOption(96, 5));
            case 443 -> it.itemOptions.add(new Item.ItemOption(97, 5));
            case 444 -> it.itemOptions.add(new Item.ItemOption(99, 3));
            case 445 -> it.itemOptions.add(new Item.ItemOption(98, 3));
            case 446 -> it.itemOptions.add(new Item.ItemOption(100, 5));
            case 447 -> it.itemOptions.add(new Item.ItemOption(101, 5));
            case 459 -> {
                it.itemOptions.add(new Item.ItemOption(112, 80));
                it.itemOptions.add(new Item.ItemOption(93, 90));
                it.itemOptions.add(new Item.ItemOption(30, 1));
            }
        }
        return it;
    }

    public static void setTimeout(Runnable runnable, int delay) {
        ThreadManager.gI().scheduleTask(runnable, delay, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public static byte getHead(byte gender) {
        return switch (gender) {
            case 2 -> 28;
            case 1 -> 32;
            default -> 64;
        };
    }

    public static byte getLeg(byte gender) {
        return switch (gender) {
            case 2 -> 17;
            case 1 -> 11;
            default -> 15;
        };
    }

    public static byte getBody(byte gender) {
        return switch (gender) {
            case 2 -> 16;
            case 1 -> 10;
            default -> 14;
        };
    }

    public static String addSlashes(String input) {
        input = input.replace("\\", "\\\\");
        input = input.replace("'", "\\'");
        input = input.replace("\"", "\\\"");
        input = input.replace("\b", "\\b");
        input = input.replace("\n", "\\n");
        input = input.replace("\r", "\\r");
        input = input.replace("\t", "\\t");

        return input;
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Long.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String roundToTwoDecimals(double num) {
        double roundedNumber = Math.round(num * 100.0) / 100.0;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(roundedNumber);
    }

    public static String dvp(long j) {
        long j2 = (j / 1000) + 1;
        String str = "";
        int i = 0;
        while (((long) i) < j2) {
            if (j >= 1000) {
                long j3 = j % 1000;
                if (j3 == 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(".000");
                    stringBuilder.append(str);
                    str = stringBuilder.toString();
                } else {
                    StringBuilder stringBuilder2;
                    String str2;
                    if (j3 < 10) {
                        stringBuilder2 = new StringBuilder();
                        str2 = ".00";
                    } else if (j3 < 100) {
                        stringBuilder2 = new StringBuilder();
                        str2 = ".0";
                    } else {
                        stringBuilder2 = new StringBuilder();
                        str2 = ".";
                    }
                    stringBuilder2.append(str2);
                    stringBuilder2.append(j3);
                    stringBuilder2.append(str);
                    str = stringBuilder2.toString();
                }
                j /= 1000;
                i++;
            } else {
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(j);
                stringBuilder3.append(str);
                return stringBuilder3.toString();
            }
        }
        return str;
    }

    public static String dvp2(double n) {
        if (n >= 1000) {
            String[] suffixes = {"", "k", "m", "b"};
            int index = 0;
            while (n >= 1000 && index < suffixes.length - 1) {
                index++;
                n /= 1000.0;
            }
            return String.format("%.2f%s", n, suffixes[index]);
        }
        return String.valueOf((int) n);
    }

    public static void ratioNroBiNgo(Player plKill) {
        int tempId = 0;
        if (Util.isTrue(5, 100)) {
            tempId = 702;
        } else if (Util.isTrue(10, 100)) {
            tempId = 703;
        } else if (Util.isTrue(15, 100)) {
            tempId = 704;
        } else if (Util.isTrue(25, 100)) {
            tempId = 705;
        } else if (Util.isTrue(35, 100)) {
            tempId = 706;
        } else if (Util.isTrue(45, 100)) {
            tempId = 707;
        } else {
            tempId = 708;
        }
        Service.gI().dropItemMap(plKill.zone, new ItemMap(plKill.zone, tempId, 1, plKill.location.x, plKill.location.y, plKill.id));
    }
}
