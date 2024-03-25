package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.commands.CallCMD;
import datta.core.content.WorldEditService;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventUtils;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.games.Game;
import datta.core.services.individual.FreezeList;
import datta.core.services.individual.Glow;
import datta.core.services.list.TimerService;
import datta.core.services.list.ToggleService;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static datta.core.content.builders.ColorBuilder.color;
import static datta.core.content.builders.ColorBuilder.stringToLocation;

@CommandPermission("spreenstudios.games")
@CommandAlias("games")
public class ReyDeLaColina extends Game {
    ItemStack itemStack = new ItemBuilder(Material.STICK, "&ePalo")
            .addEnchant(Enchantment.KNOCKBACK, 2)
            .build();

    @Override
    public int endAt() {
        return 0;
    }


    @Override
    public String name() {
        return "Rey de la colina";
    }

    @Override
    public Location spawn() {
        return stringToLocation("310 3 492 180 0");
    }

    int startAt = 5;
    BukkitTask task;

    @Subcommand("reydelacolina start")
    @Override
    public void start() {
        game(() -> {


            CallCMD.callToggleable(ToggleService.Toggleable.PVP, false);
            CallCMD.callToggleable(ToggleService.Toggleable.FALL_DAMAGE, false);
            CallCMD.callToggleable(ToggleService.Toggleable.FOOD, false);
            CallCMD.callToggleable(ToggleService.Toggleable.PLACE, false);
            CallCMD.callToggleable(ToggleService.Toggleable.BREAK, false);
            CallCMD.callToggleable(ToggleService.Toggleable.INTERACTIONS, false);
            CallCMD.callToggleable(ToggleService.Toggleable.SPAWNING_MOBS, false);
            CallCMD.callToggleable(ToggleService.Toggleable.KICK_ON_DEATH, true);
            CallCMD.callToggleable(ToggleService.Toggleable.TELEPORT_SPAWN_ON_JOIN, true);

            placeOrBreakWalls(true);

            for (Player t : Bukkit.getOnlinePlayers()) {
                t.getInventory().addItem(itemStack);
                SenderUtil.sendActionbar(t, "&aRecibiste un palo de empuje en tu inventario.", Sound.ENTITY_ITEM_PICKUP);
            }

            CallCMD.callToggleable(ToggleService.Toggleable.PVP, true, true);
            task();

            TimerService.bossBarTimer("{time}", BarColor.PURPLE, BarStyle.SOLID, 600, this::stop);
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
    @Subcommand("reydelacolina stop")
    public void stop() {
        if (task != null) task.cancel();

        TimerService.removeActionbar();
        TimerService.removeBossBar();

        for (Player t : Bukkit.getOnlinePlayers()) {
            t.getInventory().clear();
            SenderUtil.sendActionbar(t, "&c¡RING RING!", Sound.ENTITY_ITEM_PICKUP);
        }

    }


    @Override
    public List<String> scoreboard() {
        return new ArrayList<>(List.of(
                "",
                "&e Tabla:",
                "&7 &a• %core_top_1%",
                "&7 &e• %core_top_2%",
                "&7 &c• %core_top_3%",
                "&7 &c• %core_top_4%",
                "&7 &c• %core_top_5%",
                "",
                "&f Puntos: &e%core_points%",
                "&f Tiempo: &e%core_bossbartime%",
                ""
        ));
    }

    public static Map<Player, Integer> pointsMap = new HashMap<>();

    public static String getPlayerTop(Player player) {
        Integer i = pointsMap.getOrDefault(player, 0);
        return String.valueOf(i);
    }

    public static Player getTopPlayer(int top) {
        List<Map.Entry<Player, Integer>> entryList = new ArrayList<>(pointsMap.entrySet());
        entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        if (top >= 1 && top <= entryList.size()) {
            Map.Entry<Player, Integer> topEntry = entryList.get(top - 1);
            return topEntry.getKey();
        } else {
            return null;
        }
    }

    public static String getTop(int i) {
        List<Map.Entry<Player, Integer>> list = new ArrayList<>(pointsMap.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        if (i <= list.size() && i > 0) {
            Map.Entry<Player, Integer> entry = list.get(i - 1);
            Player player = entry.getKey();
            int points = entry.getValue();
            return color("&7" + player.getName() + ": &a" + points);
        } else {
            return "...";
        }
    }


    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return null;
    }

    @Override
    public Material menuItem() {
        return Material.STICK;
    }


    public void task() {
        Cuboid cuboid = new Cuboid("314 24 452 306 25 460");
        Location point1 = cuboid.getPoint1();
        Location point2 = cuboid.getPoint2();

        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player t : Bukkit.getOnlinePlayers()) {
                    t.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 5, false, false, false));

                    if (cuboid.isIn(t)) {
                        addPoints(t, 1);
                    }
                }
            }
        }.runTaskTimer(Core.getInstance(), 0, 8L);
    }

    public void addPoints(Player player, int points) {
        Integer i = pointsMap.getOrDefault(player, 0);
        int newPoints = i + points;

        for (Player t : Bukkit.getOnlinePlayers())
            SenderUtil.sendActionbar(t, "&a(+) "+player.getName()+" &festa sumando puntos: &e" + newPoints);


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
                WorldEditService.fill(cuboid.getPoint1(), cuboid.getPoint2(), Material.BARRIER);
            }
        }
    }

    @CommandCompletion(" Cantidad-de-Sobrevivientes")
    @Subcommand("reydelacolina removeplayers")
    public void removePlayers(int survivorsTOP) {

        List<Map.Entry<Player, Integer>> sortedEntries = new ArrayList<>(pointsMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        List<Player> survivors = new ArrayList<>();
        for (int i = 0; i < survivorsTOP && i < sortedEntries.size(); i++) {
            survivors.add(sortedEntries.get(i).getKey());
        }


        for (Player t : Bukkit.getOnlinePlayers()) {
            if (!t.isOp()) {

                if (!survivors.contains(t)) {

                    EventUtils.addPlayerColor(t, "&c");
                    FreezeList.freezePlayer(t, true);
                    Glow.glowPlayer(t, true);
                }
            }
        }
    }
}