package rtpqueue.utils;

import org.bukkit.ChatColor;

public class ChatUtil {

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String strip(String msg) {
        return ChatColor.stripColor(color(msg));
    }
}
