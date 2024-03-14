package datta.core.paper.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import datta.core.paper.Core;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static datta.core.paper.utilities.Utils.send;
import static datta.core.paper.utilities.Utils.sendA;

public class GlobalCMD extends BaseCommand {

    @CommandPermission("core.admin")
    @CommandAlias("tpall")
    public void tpAll(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player) {
                p.teleport(player);
            }
        }

        send(player, "&a&lEvento &8> &fTeletransportando a todos los jugadores...");
    }

    @CommandPermission("core.admin")
    @CommandAlias("otpall")
        public void optimizedTpall(Player player) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (onlinePlayers.contains(player))
            onlinePlayers.remove(player);

        int totalPlayers = onlinePlayers.size();
        final int playersPerTick = Math.max(1, totalPlayers / 10);

        send(player, "&a&lEvento &8> &fTeletransportando...");

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                int endIndex = Math.min(index + playersPerTick, totalPlayers);
                for (; index < endIndex; index++) {
                    Player target = onlinePlayers.get(index);
                    teleportAroundPlayer(target, player);
                    sendA(player, "&a> " + target.getName() + "...");
                }

                if (index >= totalPlayers) {
                    cancel();
                    send(player, "&a&lEvento &8> &f¡Teleportación completada para todos los jugadores!");
                }
            }
        }.runTaskTimer(Core.getInstance(), 0L, 5L);
    }

    private void teleportAroundPlayer(Player target, Player player) {
        Location playerLocation = player.getLocation();

        double radius = 1.5;
        double offsetX = (int) (Math.random() * (2 * radius + 1)) - radius;
        double offsetZ = (int) (Math.random() * (2 * radius + 1)) - radius;

        Location newLocation = new Location(player.getWorld(),
                playerLocation.getX() + offsetX,
                playerLocation.getY(),
                playerLocation.getZ() + offsetZ);

        target.teleport(newLocation);
    }

    @CommandPermission("core.admin")
    @CommandAlias("giveitem")
    public void giveItem(Player player, OnlinePlayer onlinePlayer) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        Player target = onlinePlayer.getPlayer();
        if (target != null) {
            target.getInventory().addItem(itemInMainHand);
            send(player, "&a&lEvento &8> &fHas dado el objeto a " + target.getName());
        } else {
            send(player, "&c&lEvento &8> &f¡Jugador no encontrado o no está en línea!");
        }
    }

    @CommandPermission("core.admin")
    @CommandAlias("setslots")
    public void setslots(CommandSender sender, int count) {

    }

    @CommandPermission("core.admin")
    @CommandAlias("giveitemall")
    public void giveItemToAll(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
            onlinePlayer.getInventory().addItem(itemInMainHand);
        }

        send(player, "&a&lEvento &8> &fHas dado el objeto a todos los jugadores!");
    }

    @CommandPermission("core.admin")
    @CommandAlias("healall")
    public void healAllPlayers(Player player) {
        for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
            onlinePlayer.setHealth(onlinePlayer.getMaxHealth());
            onlinePlayer.setFoodLevel(20);
        }
        send(player, "&a&lEvento &8> &f¡Has curado a todos los jugadores!");
    }


    @CommandPermission("core.admin")
    @CommandAlias("setspawn")
    public void setWorldSpawn(Player player) {
        World world = player.getWorld();
        world.setSpawnLocation(player.getLocation().toCenterLocation());
        Core.spawn = player.getLocation().toCenterLocation();

        send(player, "&a&lEvento &8> &f¡Has establecido el punto de aparición del mundo!");
    }

    @CommandPermission("core.spawn")
    @CommandAlias("spawn")
    public void spawn(Player player) {
        Location spawnLocation = player.getWorld().getSpawnLocation();
        player.teleport(spawnLocation.toCenterLocation());
        send(player, "&a&lEvento &8> &f¡Has sido teletransportado al punto de aparición!");
    }
}