package datta.core.content.portals;

import datta.core.Core;
import datta.core.content.utils.build.consts.Cuboid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Portal {
    @Getter @Setter
    public String id;

    @Getter@Setter
    public Cuboid cuboid;

    @Getter@Setter
    public Location pos1;

    @Getter@Setter
    public Location pos2;

    @Getter@Setter
    public Location teleport;

    @Getter@Setter
    public boolean particleStatus;

    @Getter @Setter
    public Particle showParticle;
    public final long taskDelayTicks = 20L;
    public BukkitTask task;

    public Portal(String id, Cuboid cuboid, Location teleport, boolean particleStatus, Particle showParticle) {
        this.id = id;
        this.cuboid = cuboid;
        this.teleport = teleport;
        this.particleStatus = particleStatus;
        this.showParticle = showParticle;

        this.pos1 = cuboid.getPoint1();
        this.pos2 = cuboid.getPoint2();

        startTask();
    }

    public void startTask() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }

        task = new BukkitRunnable() {
            @Override
            public void run() {
                World world = pos1.getWorld();
                for (int x = Math.min(pos1.getBlockX(), pos2.getBlockX()); x <= Math.max(pos1.getBlockX(), pos2.getBlockX()); x++) {
                    for (int y = Math.min(pos1.getBlockY(), pos2.getBlockY()); y <= Math.max(pos1.getBlockY(), pos2.getBlockY()); y++) {
                        for (int z = Math.min(pos1.getBlockZ(), pos2.getBlockZ()); z <= Math.max(pos1.getBlockZ(), pos2.getBlockZ()); z++) {
                            Location blockLocation = new Location(world, x, y, z).toCenterLocation();
                            world.spawnParticle(showParticle, blockLocation, 5,0.3,0.3,0.3,0);
                        }
                    }
                }
            }
        }.runTaskTimer(Core.getInstance(), 0L, taskDelayTicks);
    }

    public void stopTask() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    public void playerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (cuboid.isIn(player)) {
            player.teleport(teleport);
            Bukkit.getPluginManager().callEvent(new PlayerEntryPortalEvent(player, this));

        }
    }
}
