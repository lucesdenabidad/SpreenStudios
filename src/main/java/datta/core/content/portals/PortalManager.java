package datta.core.content.portals;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import datta.core.Core;
import datta.core.content.WorldEditService;
import datta.core.content.configuration.Configuration;
import datta.core.content.utils.build.consts.Cuboid;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@CommandPermission("spreenstudios.portals")
@CommandAlias("portals|portal|pt")
public class PortalManager extends BaseCommand implements Listener {

    private final Core core;
    private final Configuration config;
    public Map<String, Portal> portals;

    public PortalManager(Core core) {
        this.core = core;
        this.config = core.configurationManager.getConfig("cache.yml");
        this.portals = new HashMap<>();


        core.commandManager.registerCommand(this);

        loadPortals();
        core.getServer().getPluginManager().registerEvents(this, core);
    }


    public Portal getPortal(String id) {
        return portals.getOrDefault(id, null);
    }

    public void savePortal(Portal portal) {

        portal.stopTask();

        String id = portal.getId();
        config.set("cache.portals." + id + ".pos1", portal.getPos1());
        config.set("cache.portals." + id + ".pos2", portal.getPos2());
        config.set("cache.portals." + id + ".teleport", portal.getTeleport());
        config.set("cache.portals." + id + ".particle-status", portal.isParticleStatus());
        config.set("cache.portals." + id + ".particle", portal.getShowParticle().name());
        config.safeSave();

        loadPortals();
    }


    public void createPortal(String id, Location pos1, Location pos2, Location teleport, boolean activeParticles, Particle particle) {
        Cuboid cuboid = new Cuboid(pos1, pos2);
        Portal portal = new Portal(id, cuboid, teleport, activeParticles, particle);
        savePortal(portal);
    }

    public void removePortal(Portal portal) {
        for (Map.Entry<String, Portal> entry : portals.entrySet()) {
            if (entry.getValue().equals(portal)) {
                portal.stopTask();

                config.set("cache.portals." + entry.getKey(), null);
                config.safeSave();
                portals.remove(entry.getKey());
                loadPortals();
                break;
            }
        }
    }

    private String generatePortalId(Portal portal) {
        Location loc1 = portal.getPos1();
        Location loc2 = portal.getPos2();
        return String.format("%d-%d-%d_%d-%d-%d", loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(),
                loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
    }

    public void loadPortals() {
        portals.clear();

        ConfigurationSection portalSection = config.getConfigurationSection("cache.portals");

        if (portalSection != null) {
            for (String portalId : portalSection.getKeys(false)) {
                ConfigurationSection portalData = portalSection.getConfigurationSection(portalId);
                if (portalData != null) {
                    Location pos1 = portalData.getLocation("pos1");
                    Location pos2 = portalData.getLocation("pos2");
                    Location teleport = portalData.getLocation("teleport");
                    boolean particleStatus = portalData.getBoolean("particle-status");
                    Particle particle = Particle.valueOf(portalData.getString("particle"));

                    if (pos1 != null && pos2 != null && teleport != null) {
                        Cuboid cuboid = new Cuboid(pos1, pos2);
                        Portal portal = new Portal(portalId, cuboid, teleport, particleStatus, particle);
                        portals.put(portalId, portal);
                    }
                }
            }
        }

        List<String> portalIDS = new ArrayList<>(portals.keySet());

        core.commandManager.getCommandCompletions().registerCompletion("id", c -> {
            return portalIDS;
        });
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        for (Portal portal : portals.values()) {
            portal.playerMoveEvent(event);
        }
    }


    // # Commands
    @CommandCompletion("@id")
    @Subcommand("create")
    public void create(Player player, String id) {
        Region worldEditSelection = WorldEditService.getWorldEditSelection(player);
        if (worldEditSelection == null) {
            SenderUtil.sendMessage(player, "%core_prefix%  &cNo tienes una region de worldedit seleccionada!");
            return;
        }

        Location pos1 = blockVector3ToLocation(worldEditSelection.getMaximumPoint());
        Location pos2 = blockVector3ToLocation(worldEditSelection.getMinimumPoint());

        createPortal(id, pos1, pos2, player.getLocation().toCenterLocation(), true, Particle.PORTAL);

        SenderUtil.sendMessage(player, "%core_prefix% &ePortal con ID " + id + " fue creado con éxito!");
    }

    @CommandCompletion("@id")
    @Subcommand("delete")
    public void delete(Player player, String id) {
        Portal portal = getPortal(id);
        if (portal == null) {
            SenderUtil.sendMessage(player, "%core_prefix% &cEl portal con la id especificada no existe!");
            return;
        }

        removePortal(portal);

        SenderUtil.sendMessage(player, "%core_prefix% &ePortal con ID " + id + " fue eliminado con éxito!");
    }

    @CommandCompletion("@id")
    @Subcommand("teleport")
    public void teleport(Player player, String id) {
        Portal portal = getPortal(id);
        if (portal == null) {
            SenderUtil.sendMessage(player, "%core_prefix% &cEl portal con la id especificada no existe!");
            return;
        }

        Location teleport = portal.getTeleport();
        player.teleport(teleport);

        SenderUtil.sendMessage(player, "%core_prefix% &eTeletransportado al portal con ID " + id + ".");
    }

        @CommandCompletion("@id")
    @Subcommand("teleportportal")
    public void teleportToPortal(Player player, String id) {
        Portal portal = getPortal(id);
        if (portal == null) {
            SenderUtil.sendMessage(player, "%core_prefix% &cEl portal con la id especificada no existe!");
            return;
        }

        Location teleport = portal.getPos1().toCenterLocation().add(1,1,1);
        player.teleport(teleport);

        SenderUtil.sendMessage(player, "%core_prefix% &eTeletransportado al portal con ID " + id + ".");
    }


    @CommandCompletion("@id")
    @Subcommand("modify setteleport")
    public void settp(Player player, String id) {
        Portal portal = getPortal(id);
        if (portal == null) {
            SenderUtil.sendMessage(player, "%core_prefix% &cEl portal con la id especificada no existe!");
            return;
        }

        portal.teleport = player.getLocation().toCenterLocation();
        savePortal(portal);

        SenderUtil.sendMessage(player, "%core_prefix% &ePortal " + id + " fue actualizado con éxito.");
    }

    @CommandCompletion("@id")
    @Subcommand("modify setparticle")
    public void setParticle(Player player, String id, Particle particle) {
        Portal portal = getPortal(id);
        if (portal == null) {
            SenderUtil.sendMessage(player, "%core_prefix% &cEl portal con la id especificada no existe!");
            return;
        }

        portal.showParticle = particle;
        savePortal(portal);

        SenderUtil.sendMessage(player, "%core_prefix% &ePortal " + id + " fue actualizado con éxito.");
    }

    @CommandCompletion("@id")
    @Subcommand("modify toggleparticle")
    public void toggleparticle(Player player, String id) {
        Portal portal = getPortal(id);
        if (portal == null) {
            SenderUtil.sendMessage(player, "%core_prefix% &cEl portal con la id especificada no existe!");
            return;
        }

        portal.particleStatus = !portal.particleStatus;
        savePortal(portal);

        SenderUtil.sendMessage(player, "%core_prefix% &ePortal " + id + " fue actualizado con éxito.");

    }


    public Location blockVector3ToLocation(BlockVector3 blockVector3) {
        World world = Bukkit.getWorlds().get(0);
        int blockX = blockVector3.getBlockX();
        int blockY = blockVector3.getBlockY();
        int blockZ = blockVector3.getBlockZ();

        return new Location(world, blockX, blockY, blockZ);
    }
}