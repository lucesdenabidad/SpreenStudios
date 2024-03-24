package datta.core.services.individual;

import datta.core.Core;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Glow {

    public static List<Player> inGlow = new ArrayList<>();

    public Glow() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : inGlow) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 120, 1, false, false, false));
                }
            }
        }.runTaskTimer(Core.getInstance(), 0, 20L);
    }

    public static void glowPlayer(Player player, boolean set) {
        if (set) {
            inGlow.add(player);
        } else {
            inGlow.remove(player);
        }
    }
}