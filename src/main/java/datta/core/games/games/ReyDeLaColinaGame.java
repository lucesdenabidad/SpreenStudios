package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.WorldEditService;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.games.Game;
import datta.core.services.list.TimerService;
import datta.core.services.list.ToggleService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static datta.core.Core.callSetting;
import static datta.core.content.builders.ColorBuilder.color;
import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.builders.MenuBuilder.slot;

@CommandAlias("games")
public class ReyDeLaColinaGame extends Game {
    ItemStack itemStack = new ItemBuilder(Material.STICK, "&ePalo")
            .addEnchant(Enchantment.KNOCKBACK,2)
            .build();
    @Override
    public String name() {
        return "Rey de la colina";
    }

    @Override
    public Location spawn() {
        return stringToLocation("310 3 492 180 0");
    }

    @Override
    public String[] gameinfo() {
        return new String[0];
    }

    int startAt = 5;
    BukkitTask task;

    @Subcommand("reydelacolina start")
    @Override
    public void start() {
        game(() -> {
            TimerService.bossBarTimer("&c(!) &fLas murallas seran destruidas en &e{time} ⌚", BarColor.PURPLE, BarStyle.SOLID, startAt, () -> {
                placeOrBreakWalls(true);
                for (Player t : Bukkit.getOnlinePlayers()) {
                    t.getInventory().addItem(itemStack);
                    callSetting(ToggleService.Toggleable.PVP, true);
                }
            });

            task();
        });
    }


    @Subcommand("reydelacolina end")
    @Override
    public void end() {
        end(() -> {
            placeOrBreakWalls(false);
            if (task != null) task.cancel();
        });
    }

    @Override
    public List<String> scoreboard() {
        return new ArrayList<>(List.of(
                "",
                "&e Tabla:",
                "&7 &a• %core_top_1%",
                "&7 &e• %core_top_2%",
                "&7 &c• %core_top_3%",
                "",
                "&f Puntos: &e%core_points%",
                ""
        ));
    }

    public static Map<Player, Integer> pointsMap = new HashMap<>();

    public static String getPlayerTop(Player player){
        Integer i = pointsMap.getOrDefault(player, 0);
        return String.valueOf(i);
    }

    public static String getTop(int i) {
        List<Map.Entry<Player, Integer>> list = new ArrayList<>(pointsMap.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        if (i <= list.size() && i > 0) {
            Map.Entry<Player, Integer> entry = list.get(i - 1);
            Player player = entry.getKey();
            int points = entry.getValue();
            return color("&7"+player.getName() + ": &a" + points);
        } else {
            return "...";
        }
    }


    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return null;
    }


    public void task(){
        Cuboid cuboid = new Cuboid("314 24 452 306 25 460");
        Location point1 = cuboid.getPoint1();
        Location point2 = cuboid.getPoint2();

        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player t : Bukkit.getOnlinePlayers()) {
                    if (cuboid.isIn(t)){
                        addPoints(t,1);
                    }
                }
            }
        }.runTaskTimer(Core.getInstance(),0,8L);
    }

    public void addPoints(Player player, int points){
        Integer i = pointsMap.getOrDefault(player, 0);
        int newPoints = i + points;

        pointsMap.put(player, newPoints);
    }

    @Subcommand("reydelacolina walls")
    public void placeOrBreakWalls(boolean value) {

        List<Cuboid> list = List.of(
                new Cuboid("333 3 479 333 5 433"),
                new Cuboid("333 5 433 287 3 433"),
                new Cuboid("287 3 433 287 5 479"),
                new Cuboid("287 5 479 333 3 479")
        );


        if (value) {
            for (Cuboid cuboid : list) {
                WorldEditService.fill(cuboid.getPoint1(), cuboid.getPoint2(), Material.AIR);
            }
        } else {
            for (Cuboid cuboid : list) {
                WorldEditService.fill(cuboid.getPoint1(), cuboid.getPoint2(), Material.RED_STAINED_GLASS);
            }
        }
    }

    @Override
    public ItemStack menuItem() {
        return new ItemBuilder(Material.GOLD_INGOT,"&eRey de la colina").build();
    }

    @Override
    public int menuSlot() {
        return slot(6,2);
    }
}
