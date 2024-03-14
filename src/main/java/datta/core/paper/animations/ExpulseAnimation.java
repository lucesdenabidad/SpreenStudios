package datta.core.paper.animations;

import datta.core.paper.Core;
import datta.core.paper.animations.am.Animation;
import datta.core.paper.utilities.builders.FireworkBuilder;
import datta.core.paper.utilities.etc.BukkitDelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static datta.core.paper.utilities.Color.format;

public class ExpulseAnimation extends Animation {
    @Override
    public String name() {
        return "expulse";
    }

    @Override
    public void play(Player... players) {
        Player player = players[0];
        Player target = players[1];
        FireworkBuilder firework = new FireworkBuilder()
                .colors(Color.RED, Color.WHITE)
                .power(1)
                .duration(1L);

        firework.spawn(target.getLocation());
        target.setVelocity(new Vector(0, 1, 0));

        BukkitDelayedTask.runTask(Core.getInstance(), () -> {
            Bukkit.broadcastMessage(format("&e&lEvento &8> &f¡Se ha expulsado a &a" + target.getName() + "&f!"));
            target.getLocation().getWorld().spigot().strikeLightningEffect(player.getLocation().toCenterLocation(), false);
            target.kickPlayer(format("&c¡GRACIAS POR JUGAR!"));
        }, 20L);
    }
}