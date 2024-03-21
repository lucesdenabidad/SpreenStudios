package datta.core.services.list;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.CoreTask;
import datta.core.content.configuration.Configuration;
import datta.core.services.Service;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandPermission("spreenstudios.cinema")
@CommandAlias("cinema|cine|cinematicas|cinematics")
public class CinemaService extends Service {
    public Configuration configuration = instance().configurationManager.getConfig("cinema.yml");

    @Override
    public Core instance() {
        return Core.getInstance();
    }

    @Override
    public String name() {
        return "Cinema";
    }

    @Override
    public String[] info() {
        return new String[0];
    }

    @Override
    public void onLoad() {
        register(true, true);
    }

    @Override
    public void onUnload() {
        register(true, false);
    }


    // Commands

    @Subcommand("record")
    public void recordCMD(Player player, String name) {
        record(player, name);
    }

    @Subcommand("stoprecord")
    public void stoprecordCMD(Player player) {
        stopRecord(player);
    }

    @Subcommand("play")
    public void play(Player player, String name) {
        Cinema cinema = getCinema(name);
        if (cinema == null) {
            player.sendMessage("null");
            return;
        }

        playCinema(player, cinema);
    }

    @Subcommand("playall")
    public void playAll(Player player, String name) {
        Cinema cinema = getCinema(name);
        if (cinema == null) {
            player.sendMessage("null");
            return;
        }

        playAllCinema(cinema);
    }


    Map<Player, String> map = new HashMap<>();
    Map<Player, BukkitTask> recordTask = new HashMap<>();
    Map<Player, List<Location>> frames = new HashMap<>();
    Map<Player, Location> locationMap = new HashMap<>();
    Map<Player, GameMode> gamemodeMap = new HashMap<>();

    public void record(Player player, String param) {
        List<Location> locations = new ArrayList<>();
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                Location location = player.getLocation().clone();
                locations.add(location);
                frames.put(player, locations);

                SenderUtil.sendActionbar(player, "&c● " + locations.size());
            }
        }.runTaskTimer(instance(), 0, 1L);

        recordTask.put(player, task);
        map.put(player, param);
    }

    public void stopRecord(Player player) {
        BukkitTask task = recordTask.remove(player);
        String s = map.remove(player);
        List<Location> locations = frames.remove(player);
        if (task != null && s != null && locations != null) {
            task.cancel();
            Cinema cinema = new Cinema(s, locations);
            saveCinema(cinema);
        }
    }

    @EventHandler
    public void toggle(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (recordTask.containsKey(player)) {
            stopRecord(player);
            event.setCancelled(true);
        }
    }

    public void playCinema(Player player, Cinema cinema) {
        World world = cinema.frames.get(0).getWorld();
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(cinema.frames.get(0), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setInvisible(true);
        armorStand.setSmall(false);
        armorStand.setMarker(false);
        armorStand.setGravity(false);

        locationMap.put(player, player.getLocation());
        gamemodeMap.put(player, player.getGameMode());

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(armorStand);
        CoreTask.runTask(() -> {

            player.setSpectatorTarget(armorStand);

            long delayPerTeleport = 1L;

            for (int i = 0; i < cinema.frames.size(); i++) {
                Location frame = cinema.frames.get(i);
                int finalI = i;
                Bukkit.getScheduler().runTaskLater(instance(), () -> {
                    armorStand.teleport(frame);
                    if (finalI == cinema.frames.size() - 1) {
                        armorStand.remove();
                        player.setGameMode(gamemodeMap.getOrDefault(player, GameMode.SURVIVAL));
                        player.teleport(locationMap.get(player));

                        gamemodeMap.remove(player);
                        locationMap.remove(player);
                    }
                }, delayPerTeleport * i);
            }
        }, 6L);
    }

    private void playAllCinema(Cinema cinema) {
        World world = cinema.frames.get(0).getWorld();
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(cinema.frames.get(0), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setInvisible(true);
        armorStand.setSmall(false);
        armorStand.setMarker(false);
        armorStand.setGravity(false);

        for (Player player : Bukkit.getOnlinePlayers()) {

            locationMap.put(player, player.getLocation());
            gamemodeMap.put(player, player.getGameMode());

            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(armorStand);
        }

        CoreTask.runTask(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setSpectatorTarget(armorStand);
            }

            long delayPerTeleport = 1L;

            for (int i = 0; i < cinema.frames.size(); i++) {
                Location frame = cinema.frames.get(i);
                int finalI = i;
                Bukkit.getScheduler().runTaskLater(instance(), () -> {
                    armorStand.teleport(frame);
                    if (finalI == cinema.frames.size() - 1) {
                        armorStand.remove();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.setGameMode(gamemodeMap.getOrDefault(player, GameMode.SURVIVAL));
                            player.teleport(locationMap.get(player));

                            gamemodeMap.remove(player);
                            locationMap.remove(player);
                        }
                    }
                }, delayPerTeleport * i);
            }
        }, 6L);
    }


    public Cinema getCinema(String name) {
        List<String> cinemaFrames = getCinemaFrames(name);
        List<Location> frames = new ArrayList<>();
        for (String cinemaFrame : cinemaFrames) {
            Location location = convertStringToLocation(cinemaFrame);
            frames.add(location);
        }

        return new Cinema(name, frames);
    }


    public List<String> getCinemaFrames(String name) {
        return configuration.getStringList("cinemas." + name);
    }

    public void saveCinema(Cinema cinema) {
        String name = cinema.name;
        List<Location> frames = cinema.frames;
        putLocationOnConfig(name, frames);
    }

    public void putLocationOnConfig(String name, List<Location> list) {
        List<String> stringList = new ArrayList<>();

        for (Location location : list) {
            String s = convertLocationToString(location);
            stringList.add(s);
        }

        configuration.set("cinemas." + name, stringList);
        configuration.safeSave();
    }

    public String convertLocationToString(Location location) {
        return String.format("%s;%f;%f;%f;%f;%f",
                location.getWorld().getName(),
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
    }

    public Location convertStringToLocation(String string) {
        String[] parts = string.split(";");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid location string format");
        }
        World world = Bukkit.getWorld(parts[0]);
        double x = Double.parseDouble(parts[1].replace(',', '.'));
        double y = Double.parseDouble(parts[2].replace(',', '.'));
        double z = Double.parseDouble(parts[3].replace(',', '.'));
        float yaw = Float.parseFloat(parts[4].replace(',', '.'));
        float pitch = Float.parseFloat(parts[5].replace(',', '.'));
        return new Location(world, x, y, z, yaw, pitch);
    }


    @EventHandler
    public void playerExit(PlayerMoveEvent e) {
        if (locationMap.containsKey(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerExit(PlayerToggleSneakEvent e) {
        if (locationMap.containsKey(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    public class Cinema {
        String name;
        List<Location> frames;

        public Cinema(String name, List<Location> frames) {
            this.name = name;
            this.frames = frames;
        }
    }
}
