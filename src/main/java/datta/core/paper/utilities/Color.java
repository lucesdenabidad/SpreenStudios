package datta.core.paper.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color {


    public static boolean checkoutPAPI(String s, JavaPlugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin(s) == null) {
            plugin.getLogger().warning("No tienes el plugin de '" + s + "' en el servidor. (Dependencia obligatoria)");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return false;
        }


        return true;
    }

    private static String replacePlaceholders(String content, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            content = content.replace("{" + i + "}", String.valueOf(objects[i]));
        }
        return content;
    }

    private static String translateColors(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', content);
    }

    private static String setPlayerPlaceholders(Player player, String content) {
        return PlaceholderAPI.setPlaceholders(player, content);
    }

    private static String hex(String content) {

        if (content == null) {
            return "&#FFFFF";
        }

        final char colorChar = ChatColor.COLOR_CHAR;
        Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
        final Matcher matcher = HEX_PATTERN.matcher(content);
        final StringBuffer buffer = new StringBuffer(content.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group(1);

            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }


    public static String format(String content, Object... objects) {

        if (content == null) {
            return "";
        }
        return format(null, content, objects);
    }

    public static String format(Player player, String content, Object... objects) {
        if (content == null) {
            return "";
        }

        for (int i = 0; i < objects.length; i++) {
            content = content.replace("{" + i + "}", String.valueOf(objects[i]));
        }
        if (player != null) content = PlaceholderAPI.setPlaceholders(player, content);
        content = format(content);
        return content;
    }

    private static String format(String content) {
        if (content == null) {
            return "";
        }

        return format(null, content);
    }

    private static String format(Player player, String content) {
        if (player != null) content = PlaceholderAPI.setPlaceholders(player, content);
        content = translateColors(content);

        final char colorChar = ChatColor.COLOR_CHAR;
        Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
        final Matcher matcher = HEX_PATTERN.matcher(content);
        final StringBuffer buffer = new StringBuffer(content.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group(1);

            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();

    }

    public static String locationToString(Location location) {

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        World world = location.getWorld();


        String returned = format("{0}, {1}, {2} - {3}", x, y, z, world.getName());

        return returned;
    }

    public static Location centerLocations(Location pos1, Location pos2) {
        double centerX = (pos1.getX() + pos2.getX()) / 2.0;
        double centerY = (pos1.getY() + pos2.getY()) / 2.0;
        double centerZ = (pos1.getZ() + pos2.getZ()) / 2.0;
        World world = pos1.getWorld();
        return new Location(world, centerX, centerY, centerZ);
    }

    public static Location centerSafeLocations(Location pos1, Location pos2) {
        double centerX = (pos1.getX() + pos2.getX()) / 2.0;
        double centerY = (pos1.getY() + pos2.getY()) / 2.0;
        double centerZ = (pos1.getZ() + pos2.getZ()) / 2.0;
        World world = pos1.getWorld();
        while (world.getBlockAt((int) centerX, (int) centerY, (int) centerZ).getType() != Material.AIR) {
            centerY++;
        }

        return new Location(world, centerX, centerY, centerZ);
    }

    public static Location stringToLocation(String locationString) {

        World world = Bukkit.getWorlds().get(0);
        String[] parts = locationString.split(" ");

        if (parts.length < 3) {
            throw new IllegalArgumentException("Incorrect location format. Must be 'X,Y,Z'.");
        }

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);

        if (world == null) {
            throw new IllegalArgumentException("World '" + world + "' not found.");
        }

        if (parts.length > 3) {
            float yaw = Float.parseFloat(parts[3]);
            float pitch = parts.length > 4 ? Float.parseFloat(parts[4]) : 0.0f;
            return new Location(world, x, y, z, yaw, pitch);
        } else {
            return new Location(world, x, y, z);
        }
    }

    public static String formatTime(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    public static List<String> formatList(List<String> list) {
        List<String> formatted = new ArrayList<>();
        for (String s : list)
            formatted.add(format(s));

        return formatted;
    }

    public static List<String> formatList(Player player,String ... list) {
        List<String> formatted = new ArrayList<>();
        for (String s : list) formatted.add(format(player, s));

        return formatted;
    }

    public static List<String> formatList(Player player, List<String> list) {
        List<String> formatted = new ArrayList<>();
        for (String s : list) formatted.add(format(player, s));

        return formatted;
    }

    public static String formatBoolean(boolean value, String trueResult, String falseResult) {
        if (value) {
            return trueResult;
        } else {
            return falseResult;
        }
    }

    public static String snakeCaseToCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (char c : input.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }


    static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static String formatDoubleToTwoDecimals(double number) {
        return decimalFormat.format(number).replace(",", ".");
    }
}