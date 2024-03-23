package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import datta.core.commands.CallCMD;
import datta.core.content.builders.ItemBuilder;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventPlayer;
import datta.core.content.utils.EventUtils;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.games.Game;
import datta.core.services.list.TimerService;
import datta.core.services.list.ToggleService;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.utils.build.BuildUtils.replace;
import static datta.core.content.utils.build.BuildUtils.set;


@CommandAlias("games")
public class PuertasGame extends Game {

    @Override
    public String name() {
        return "Puertas";
    }

    @Override
    public Location spawn() {
        return stringToLocation("1 101 291 -180 0");
    }


    @Override
    public void start() {
        game(() -> {
            CallCMD.callToggleable(ToggleService.Toggleable.PVP, false);
            CallCMD.callToggleable(ToggleService.Toggleable.FALL_DAMAGE, false);
            CallCMD.callToggleable(ToggleService.Toggleable.FOOD, false);
            CallCMD.callToggleable(ToggleService.Toggleable.PLACE, false);
            CallCMD.callToggleable(ToggleService.Toggleable.BREAK, false);
            CallCMD.callToggleable(ToggleService.Toggleable.SPAWNING_MOBS, false);

            CallCMD.callToggleable(ToggleService.Toggleable.DAMAGE, true);
            CallCMD.callToggleable(ToggleService.Toggleable.KICK_ON_DEATH, true);
            CallCMD.callToggleable(ToggleService.Toggleable.INTERACTIONS, true);
            CallCMD.callToggleable(ToggleService.Toggleable.TELEPORT_SPAWN_ON_JOIN, true);

            openStairs();

            TimerService.actionbarTimer(tiempoParaElegirPortal, () -> {
                closeStairs();
            });
        });
    }


    @Override
    public void end() {
        end(() -> {
            setDoors(Material.BARRIER);
            closeStairs();
            setParkourGlass(Material.RED_STAINED_GLASS);
        });
    }

    @Override
    public List<String> scoreboard() {
        return new ArrayList<>();
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return new ArrayList<>(List.of(

                new MenuBuilder.MenuItem(new ItemBuilder(Material.PAPER, "&aAbrir portal cercano").build(), () -> {
                    setNearestCuboid(player, Material.AIR);
                }),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.PAPER, "&cCerrar portal cercano").build(), () -> {
                    setNearestCuboid(player, Material.BARRIER);
                }),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.PAPER, "&aAbrir cristales de parkour").build(), () -> {
                    setParkourGlass(Material.AIR);
                }),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.PAPER, "&cCerrar cristales de parkour").build(), () -> {
                    setParkourGlass(Material.RED_STAINED_GLASS_PANE);
                }),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.PAPER, "&aIniciar parkour").build(), this::startParkour),

                new MenuBuilder.MenuItem(new ItemBuilder(Material.PAPER, "&cTerminar parkour").build(), this::stopParkour)
        ));
    }

    @Override
    public Material menuItem() {
        return Material.IRON_DOOR;
    }

    // # Constantes
    private static final Cuboid PARKOUR_1 = new Cuboid("11 109 266 20 116 275");
    private static final Cuboid PARKOUR_2 = new Cuboid("20 116 287 11 109 281");
    private static final Cuboid PARKOUR_3 = new Cuboid("-9 109 274 -18 115 266");
    private static final Cuboid PARKOUR_4 = new Cuboid("-18 109 280 -10 115 287");
    private final Map<Player, String> saved = new HashMap<>();
    private final Cuboid[] parkours = {PARKOUR_1, PARKOUR_2, PARKOUR_3, PARKOUR_4};
    private final String[] parkourNames = {"parkour-1", "parkour-2", "parkour-3", "parkour-4"};

    int tiempoParaElegirPortal = 120; // 2 minutos
    int tiempoParaIniciarParkour = 5;
    int tiempoDeParkour = 300;

    boolean freeze = false;

    // # Metodos

    public void openStairs() {
        Cuboid izq = new Cuboid("-15 108 288 -10 108 267");
        Cuboid der = new Cuboid("19 108 265 11 108 290");

        replace(izq, Material.BARRIER, Material.AIR);
        replace(der, Material.BARRIER, Material.AIR);

        for (Player t : Bukkit.getOnlinePlayers())
            SenderUtil.sendActionbar(t, "&a¡Ya puedes elegir un portal!", Sound.ENTITY_ITEM_PICKUP);
    }

    public void closeStairs() {
        Cuboid izq = new Cuboid("-15 108 288 -10 108 267");
        Cuboid der = new Cuboid("19 108 265 11 108 290");

        replace(izq, Material.AIR, Material.BARRIER);
        replace(der, Material.AIR, Material.BARRIER);

        for (Player t : Bukkit.getOnlinePlayers())
            SenderUtil.sendActionbar(t, "&c¡Ya no puedes elegir un portal!", Sound.ENTITY_ITEM_PICKUP);
    }

    private void enter(Player player, String parkourName) {
        parkourName = parkourName.replace("parkour-", "");

        if (saved.get(player) != null && saved.get(player).equalsIgnoreCase(parkourName)) {
            return;
        }

        saved.put(player, parkourName);
        SenderUtil.sendActionbar(player, "&a(!) Has elegido un portal con éxito.", Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
    }

    private void setDoors(Material material) {
        Cuboid portal1 = new Cuboid("19 114 269 19 109 271");
        Cuboid portal2 = new Cuboid("19 114 283 19 109 285");
        Cuboid portal3 = new Cuboid("-17 114 285 -17 109 283");
        Cuboid portal4 = new Cuboid("-17 114 271 -17 109 269");

        set(portal1, material);
        set(portal2, material);
        set(portal3, material);
        set(portal4, material);
    }


    public Cuboid nearestCuboid(Location location) {
        Cuboid[] portals = {
                new Cuboid("19 114 269 19 109 271"),
                new Cuboid("19 114 283 19 109 285"),
                new Cuboid("-17 114 285 -17 109 283"),
                new Cuboid("-17 114 271 -17 109 269")
        };

        Location centerLocation = location.toCenterLocation();
        Cuboid nearestCuboid = null;
        double minDistance = Double.MAX_VALUE;
        double maxDistance = 10;

        for (Cuboid portal : portals) {
            Location portalCenter = portal.getCenter();
            double distance = centerLocation.distance(portalCenter);
            if (distance <= maxDistance && distance < minDistance) {
                minDistance = distance;
                nearestCuboid = portal;
            }
        }

        return nearestCuboid;
    }


    public void setNearestCuboid(Player player, Material material) {
        Location location = player.getLocation();
        Cuboid cuboid = nearestCuboid(location);

        set(cuboid, material);

        if (material == Material.AIR) {
            SenderUtil.sendMessage(player, "%core_prefix% &aHas abierto el portal mas cercano a ti.");
        } else {
            SenderUtil.sendMessage(player, "%core_prefix% &cHas cerrado el portal mas cercano a ti.");
        }
    }

    public void setParkourGlass(Material material) {
        Cuboid[] cuboids = {
                new Cuboid("370 20 495 374 24 495"),
                new Cuboid("417 10 488 400 8 488"),
                new Cuboid("442 8 494 431 6 494"),
                new Cuboid("477 7 494 482 4 494")
        };

        for (Cuboid cuboid : cuboids) {
            set(cuboid, material);
        }
    }

    public void startParkour() {
        TimerService.actionbarTimer(tiempoParaIniciarParkour, () -> {
            setParkourGlass(Material.AIR);
            for (Player t : Bukkit.getOnlinePlayers()) {
                SenderUtil.sendActionbar(t, "&a(!) Tienes 2 minutos parra llegar ala meta!", Sound.ENTITY_ITEM_PICKUP);
            }

            TimerService.bossBarTimer("{time}", BarColor.PURPLE, BarStyle.SOLID, tiempoDeParkour, () -> {
                freeze = true;

                for (Player t : Bukkit.getOnlinePlayers()) {
                    SenderUtil.sendActionbar(t, "&c¡RING RING!", Sound.ITEM_LODESTONE_COMPASS_LOCK);
                }
            });
        });
    }

    public void stopParkour() {
        freeze = false;

        Cuboid cuboid = new Cuboid("500 65 499 355 -1 350");

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            EventPlayer eventPlayer = new EventPlayer(onlinePlayer);

            if (eventPlayer.isStaff()) return;

            if (cuboid.isIn(onlinePlayer)) {
                dead(onlinePlayer);
            }
        }
    }


    public void dead(Player player) {
        EventUtils.eliminate(player,true);
    }

    public void win(Player player) {
        player.teleport(stringToLocation("1 100 134"));

        for (Player t : Bukkit.getOnlinePlayers()) {
            SenderUtil.sendActionbar(t, "&a(✔) " + player.getName() + " llego ala meta!", Sound.BLOCK_NOTE_BLOCK_BANJO);
        }
    }

    // # Eventos

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        for (int i = 0; i < parkours.length; i++) {
            if (parkours[i].isIn(player)) {
                enter(player, parkourNames[i]);
                break;
            }
        }
    }


    @EventHandler
    public void moveOnWinParkour(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Cuboid cuboid = new Cuboid("500 65 350 355 -1 368");

        if (cuboid.isIn(player)) {
            win(player);
        }
    }

    @EventHandler
    public void moveInFreeze(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        EventPlayer eventPlayer = new EventPlayer(player);

        if (freeze) {
            if (eventPlayer.isStaff()) return;
            SenderUtil.sendActionbar(player, "&c¡Estas congelado!", Sound.BLOCK_ANVIL_BREAK);
            event.setCancelled(true);
        }
    }
}