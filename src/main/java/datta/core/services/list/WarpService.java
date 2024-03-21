package datta.core.services.list;

import co.aikar.commands.annotation.*;
import datta.core.Core;
import datta.core.content.configuration.Configuration;
import datta.core.services.Service;
import datta.core.utils.SenderUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


@CommandPermission("spreenstudios.warps")
@CommandAlias("warps")
public class WarpService extends Service {
    private final Configuration configuration = Core.getInstance().configurationManager.getConfig("warps.yml");

    @Override
    public Core instance() {
        return Core.getInstance();
    }

    @Override
    public String name() {
        return "warps";
    }

    @Override
    public String[] info() {
        return new String[0];
    }

    @Override
    public void onLoad() {
        register(true, true);

        update();
    }
    @Override
    public void onUnload() {

    }



    public void update() {
        instance().commandManager.getCommandCompletions().registerCompletion("warps", c -> {
            return getWarps();
        });
    }

    public List<String> getWarps(){
        ConfigurationSection configurationSection = configuration.getConfigurationSection("warps");
        if (configurationSection == null){
            configurationSection.createSection("warps");
            configuration.safeSave();
        }

        return new ArrayList<>(configurationSection.getKeys(false));
    }
    public void createWarp(String name, Location location) {
        configuration.setLocation("warps." + name, location, true);
        configuration.safeSave();
        Core.info("Se creo una nueva warp llamada " + name + ".");

        update();
    }

    public void deleteWarp(String name) {
        configuration.set("warps." + name, null);
        configuration.safeSave();
        Core.info("La warp " + name + " fue eliminada");

        update();
    }

    public void teleportWarp(Player player, String name) {
        Location location = configuration.getLocation("warps." + name,true);
        if (location == null) {
            SenderUtil.sendMessage(player, "%core_prefix% &cEsta warp no existe!");
            return;
        }
        SenderUtil.sendMessage(player, "%core_prefix% &eFuiste teletransportando a la warp " + name + ".");
        player.teleport(location.toCenterLocation());
    }

    @CommandCompletion("@warps")
    @Default
    public void teleportCMD(Player player, String name) {
        teleportWarp(player, name);
    }

    @Subcommand("admin create")
    public void createCMD(Player player, String name) {
        createWarp(name, player.getLocation().toCenterLocation());
        SenderUtil.sendMessage(player, "%core_prefix% &eWarp " + name + " fue creada con éxito.");

    }

    @Subcommand("admin remove")
    public void remove(String name) {
        deleteWarp(name);
    }
}

