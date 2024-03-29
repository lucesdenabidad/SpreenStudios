package datta.core.services.list;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import datta.core.Core;
import datta.core.content.configuration.Configuration;
import datta.core.services.Service;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LightService extends Service {

    public Configuration configuration = instance().getConfig();

    public BukkitTask task;

    @Override
    public Core instance() {
        return Core.getInstance();
    }

    @Override
    public String name() {
        return "light";
    }

    @Override
    public String[] info() {
        return new String[0];
    }

    @Override
    public void onLoad() {
        register(true, false);
        checkTask();
    }

    @Override
    public void onUnload() {
        if (task != null) {
            task.cancel();
        }
    }

    @CommandPermission("spreenstudios.lights")
    @CommandAlias("light|lights|luces|luce")
    public void toggleLights(CommandSender sender) {
        setStatus(!getStatus());

        if (task != null) task.cancel();
        checkTask();

        SenderUtil.sendMessage(sender, "%core_prefix% &eAlternaste el estado de las luces de los jugadores a " + getStatus());
    }

    public boolean getStatus() {
        return configuration.getBoolean("lights", true);
    }

    public void setStatus(boolean b) {
        configuration.set("lights", b);
        configuration.safeSave();

        if (!b) {
            for (Player t : Bukkit.getOnlinePlayers())
                t.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }

        for (Player t : Bukkit.getOnlinePlayers()) {
            SenderUtil.sendSound(t, "qsm:light", 1, 1);
        }

    }

    public void checkTask() {

        if (getStatus()) {

            task = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player t : Bukkit.getOnlinePlayers()) {
                        if (!t.getActivePotionEffects().contains(PotionEffectType.NIGHT_VISION)) {
                            t.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 120*5, 0, false, false, false));
                        }
                    }
                }
            }.runTaskTimer(Core.getInstance(), 0, 20L);
        }
    }
}
