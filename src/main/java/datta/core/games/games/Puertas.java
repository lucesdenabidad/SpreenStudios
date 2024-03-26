package datta.core.games.games;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import datta.core.commands.CallCMD;
import datta.core.content.builders.MenuBuilder;
import datta.core.content.utils.EventUtils;
import datta.core.content.utils.build.BuildUtils;
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
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static datta.core.content.builders.ColorBuilder.stringToLocation;
import static datta.core.content.utils.EventUtils.fix;
import static datta.core.content.utils.build.BuildUtils.replace;
import static datta.core.content.utils.build.BuildUtils.set;
import static org.bukkit.Material.*;


@CommandPermission("spreenstudios.games")
@CommandAlias("games")
public class Puertas extends Game {

    @Override
    public int endAt() {
        return 0;
    }


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
            CallCMD.callToggleable(ToggleService.Toggleable.FOOD, false);
            CallCMD.callToggleable(ToggleService.Toggleable.PLACE, false);
            CallCMD.callToggleable(ToggleService.Toggleable.BREAK, false);
            CallCMD.callToggleable(ToggleService.Toggleable.SPAWNING_MOBS, false);

            CallCMD.callToggleable(ToggleService.Toggleable.FALL_DAMAGE, false);
            CallCMD.callToggleable(ToggleService.Toggleable.VOID_DAMAGE, true);
            CallCMD.callToggleable(ToggleService.Toggleable.DAMAGE, true);
            CallCMD.callToggleable(ToggleService.Toggleable.KICK_ON_DEATH, true);
            CallCMD.callToggleable(ToggleService.Toggleable.INTERACTIONS, true);
            CallCMD.callToggleable(ToggleService.Toggleable.TELEPORT_SPAWN_ON_JOIN, true);

            setPortals(BARRIER);
            openStairs();

            TimerService.bossBarTimer("{time}", BarColor.PURPLE, BarStyle.SOLID, tiempoParaElegirPortal, () -> {
                closeStairs(true);
            });
        });
    }

    @Override
    public void end() {
        end(() -> {
            setParkourGlass(Material.RED_STAINED_GLASS);
            setPortals(Material.BARRIER);
            closeStairs(false);
        });
    }

    @Override
    public List<String> scoreboard() {
        return new ArrayList<>();
    }

    @Override
    public List<MenuBuilder.MenuItem> menuItems(Player player) {
        return new ArrayList<>(List.of(
        ));
    }


    @Override
    public Material menuItem() {
        return Material.IRON_DOOR;
    }

    // # Constantes

    private static final Parkour PARKOUR_1 = new Parkour(
            "1",
            "puertabuena",
            "&aPuerta buena",
            new Cuboid("-253 11 -306 -248 14 -306"),
            new Cuboid("18 108 267 12 108 273"),
            new Cuboid("-254 56 -441 -249 51 -449"),
            new Cuboid("11 116 266 20 108 274"),

            new ArrayList<>());

    private static final Parkour PARKOUR_2 = new Parkour(
            "2",
            "puertamala",
            "&cPuerta mala",
            new Cuboid("-285 30 -305 -281 27 -305"),
            new Cuboid("-11 108 268 -15 108 272"),
            new Cuboid("-286 31 -436 -277 26 -449"),
            new Cuboid("-9 119 275 -18 108 266"),
            new ArrayList<>());


    int tiempoParaElegirPortal = 120; // 2 minutos - Eligen portal
    int tiempoParaIniciarParkour = 1; // 5 segundos - antes de elegir portal
    int tiempoDeParkour = 300; // 5 Minutos - Duracion antes de morir

    Map<Player, Location> CHECKPOINTS = new HashMap<>();

    // # Metodos
    public void openStairs() {
        replace(PARKOUR_1.stairsSector, Material.BARRIER, AIR);
        replace(PARKOUR_2.stairsSector, Material.BARRIER, AIR);

        for (Player t : Bukkit.getOnlinePlayers())
            SenderUtil.sendActionbar(t, "&a¡Ya puedes elegir un portal!", Sound.ENTITY_ITEM_PICKUP);

        setParkourGlass(Material.RED_STAINED_GLASS);
    }
    public void closeStairs(boolean msg) {
        replace(PARKOUR_1.stairsSector, AIR, Material.BARRIER);
        replace(PARKOUR_2.stairsSector, AIR, Material.BARRIER);

        if (msg) {
            for (Player t : Bukkit.getOnlinePlayers())
                SenderUtil.sendActionbar(t, "&c¡Ya no puedes elegir un portal!", Sound.ENTITY_ITEM_PICKUP);
        }

        setParkourGlass(Material.RED_STAINED_GLASS);
    }

    private void enter(Player player, Parkour parkour) {
        if (parkour.members.contains(player)) return;

        PARKOUR_1.removeMember(player);
        PARKOUR_2.removeMember(player);

        parkour.addMember(player);

        if (!player.isOp()) {
            SenderUtil.sendActionbar(player, "&a(!) Has elegido un portal con éxito.", Sound.BLOCK_NOTE_BLOCK_BIT);
        }
    }
    private void setPortals(Material material) {
        Cuboid portal1 = new Cuboid("-17 114 271 -17 109 269");
        Cuboid portal2 = new Cuboid("19 109 269 19 114 271");

        set(portal1, material);
        set(portal2, material);
    }


    public Cuboid nearestCuboid(Location location) {
        Cuboid[] portals = {
                new Cuboid("19 114 269 19 109 271"),
                new Cuboid("19 114 283 19 109 285"),
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

        if (material == AIR) {
            SenderUtil.sendMessage(player, "%core_prefix% &aHas abierto el portal mas cercano a ti.");
        } else {
            SenderUtil.sendMessage(player, "%core_prefix% &cHas cerrado el portal mas cercano a ti.");
        }
    }

    public void setParkourGlass(Material material) {
        Cuboid glass_1 = PARKOUR_1.glassSector;
        Cuboid glass_2 = PARKOUR_2.glassSector;

        BuildUtils.set(glass_1, material);
        BuildUtils.set(glass_2, material);
    }


    public void startParkour() {
        TimerService.bossBarTimer("{time}", BarColor.PURPLE, BarStyle.SOLID, tiempoParaIniciarParkour, () -> {
            setParkourGlass(AIR);
            for (Player t : Bukkit.getOnlinePlayers()) {
                SenderUtil.sendActionbar(t, "&a(!) Tienes 5 minutos parra llegar a la meta!", Sound.ENTITY_ITEM_PICKUP);
            }

            TimerService.bossBarTimer("{time}", BarColor.PURPLE, BarStyle.SOLID, tiempoDeParkour, () -> {
                for (Player t : Bukkit.getOnlinePlayers()) {
                    SenderUtil.sendActionbar(t, "&c¡RING RING!", Sound.ITEM_LODESTONE_COMPASS_LOCK);
                }

                stopParkour();
            });
        });
    }

    public void stopParkour() {
        Cuboid cuboid = new Cuboid("-230 72 -301 -300 6 -450");

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.isOp()) {
                if (cuboid.isIn(onlinePlayer)) {
                    EventUtils.addPlayerColor(onlinePlayer, "&c");
                    Glow.glowPlayer(onlinePlayer,true);
                    FreezeList.freezePlayer(onlinePlayer, true);
                }
            }
        }

        TimerService.removeBossBar();
        TimerService.removeActionbar();

        setParkourGlass(Material.RED_STAINED_GLASS);
    }

    public void win(Player player) {
        player.teleport(stringToLocation("1 101 291"));

        for (Player t : Bukkit.getOnlinePlayers()) {
            SenderUtil.sendActionbar(t, "&a(✔) " + player.getName() + " llego a la meta!", Sound.BLOCK_NOTE_BLOCK_BANJO);
        }

        CHECKPOINTS.remove(player);
    }

    // # Eventos

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Cuboid PARKOUR_2_SECTOR = PARKOUR_2.stairsSector;
        Cuboid PARKOUR_1_SECTOR = PARKOUR_1.stairsSector;

        if (PARKOUR_2_SECTOR.isIn(player)) {
            enter(player, PARKOUR_2);
        }

         if (PARKOUR_1_SECTOR.isIn(player)) {
            enter(player, PARKOUR_1);
        }
    }


    @EventHandler
    public void moveOnWinParkour(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Cuboid winSector = PARKOUR_1.winSector;
        Cuboid winSector1 = PARKOUR_2.winSector;

        if (winSector1.isIn(player) || winSector.isIn(player)) {
            win(player);
        }
    }


    @EventHandler
    public void moveInParkours(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation().toCenterLocation();
        Block block = location.getBlock();

        if (!player.isOp()) {


            if (block.getType() == Material.STRUCTURE_VOID) {
                CHECKPOINTS.put(player, location);
            }

            if (block.getType() == Material.WATER) {
                Location location1 = CHECKPOINTS.get(player);
                if (location1 != null) {
                    player.teleport(location1);
                }
            }

            if (block.getType() == Material.LAVA) {
                EventUtils.eliminate(player, true);
            }
        }
    }

    // # Commands

    @Subcommand("puertas setportals")
    public void openPortals(CommandSender sender, Material m){
        setPortals(m);
        SenderUtil.sendMessage(sender, "%core_prefix% &aLos portales fueron modificados a "+ fix(m.name())+".");
    }

    @Subcommand("puertas startparkour")
    public void starparkour(CommandSender sender){
        startParkour();
        SenderUtil.sendMessage(sender, "%core_prefix% &aIniciando parkours.");
    }
    @Subcommand("puertas stopparkour")
    public void stopparkour(CommandSender sender){
        stopParkour();
        SenderUtil.sendMessage(sender, "%core_prefix% &cFrenando juegos.");
    }


    public static class Parkour {

        String id;
        String name;
        String displayName;

        Cuboid glassSector;
        Cuboid stairsSector;
        Cuboid winSector;
        Cuboid joinSector;

        List<Player> members;

        public Parkour(String id, String name, String displayName, Cuboid glass, Cuboid sector, Cuboid winSector, Cuboid joinSector, List<Player> members) {
            this.id = id;
            this.name = name;
            this.displayName = displayName;
            this.glassSector = glass;
            this.stairsSector = sector;
            this.winSector = winSector;
            this.joinSector = joinSector;
            this.members = members;
        }

        public void addMember(Player player){
            members.add(player);
        }

        public void removeMember(Player player){
            members.remove(player);
        }

    }
}