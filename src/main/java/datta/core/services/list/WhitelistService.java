package datta.core.services.list;

import co.aikar.commands.annotation.*;
import datta.core.Core;
import datta.core.content.configuration.Configuration;
import datta.core.services.Service;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.ArrayList;
import java.util.List;

import static datta.core.content.builders.ColorBuilder.color;


@CommandPermission("|spreenstudios.*|spreenstudios.whitelist")
@CommandAlias("whitelist|wl|swl|xwl|uwl|pwl|wl|listablanca|lista")
public class WhitelistService extends Service {
    Configuration configuration = Core.getInstance().configurationManager.getConfig("whitelist.yml");

    @Override
    public Core instance() {
        return Core.getInstance();
    }

    @Override
    public String name() {
        return "Whitelist";
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


    public void refreshList() {
        instance().commandManager.getCommandCompletions().registerCompletion("whitelist_players", c -> {
            return getWhitelist(getType());
        });
    }


    @HelpCommand
    public void whitelistHelp(CommandSender sender) {
        SenderUtil.sendMessage(sender,
                "",
                "&eComandos de lista blanca",
                "&9 • &aadd {nick} &7- &fAgregar jugador a whitelist",
                "&9 • &aremove {nick} &7- &fEliminar jugador de whitelist",
                "&9 • &aon &7- &fActivar whitelist",
                "&9 • &aoff  &7- &fDesactivar whitelist",
                "&9 • &alist &7- &fVer jugadores en  whitelist",
                "");
    }


    @Subcommand("on")
    public void onWhitelist(CommandSender sender) {
        setStatus(true);
        SenderUtil.sendMessage(sender, "%core_prefix% &aLa lista blanca se activo con exito.");
    }

    @Subcommand("off")
    public void offWhitelist(CommandSender sender) {
        setStatus(false);
        SenderUtil.sendMessage(sender, "%core_prefix% &cLa lista blanca se desactivo con exito.");
    }

    @CommandCompletion(" @players")
    @Subcommand("add")
    public void addWhitelist(CommandSender sender, WhitelistType type, String target) {
        addToWhitelist(type, target);
        SenderUtil.sendMessage(sender, "%core_prefix% &aSe ha agregado a " + target + " a la lista de ingreso.");
    }

    @Subcommand("add all")
    public void addAllWhitelist(CommandSender sender, WhitelistType type) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            addToWhitelist(type, onlinePlayer.getName());


        SenderUtil.sendMessage(sender, "%core_prefix% &aSe han agregado todos los conectados a la lista de ingreso.");
    }


    @CommandCompletion(" @whitelist_players")
    @Subcommand("remove")
    public void removeWhitelist(CommandSender sender, WhitelistType type, String target) {
        removeFromWhitelist(type, target);
        SenderUtil.sendMessage(sender, "%core_prefix% &cSe ha eliminado a " + target + " a la lista de ingreso.");
    }

    @Subcommand("clear")
    public void clearWhitelist(CommandSender sender, WhitelistType type) {

        List<String> list = new ArrayList<>(List.of("datta", "idpabloski", "Badendingg"));

        String lowerCase = type.name().toLowerCase();
        configuration.set("whitelist." + lowerCase, list);
        configuration.safeSave();
        getWhitelist(type).clear();
        refreshList();

        SenderUtil.sendMessage(sender, "%core_prefix% &cSe ha limpiado la whitelist.");
    }


    @Subcommand("list")
    public void getList(CommandSender sender, WhitelistType type) {
        List<String> whitelist = getWhitelist(type);
        SenderUtil.sendMessage(sender,
                "&8&m                                   &f",
                "&bLista blanca:");

        for (String s : whitelist) {
            SenderUtil.sendMessage(sender, "&9 • &f" + s);
        }

        SenderUtil.sendMessage(sender, "&8&m                                   &f");
    }

    @Subcommand("type")
    public void settype(CommandSender sender, WhitelistType type) {
        setWhitelistType(type);
    }


    public List<String> getWhitelist(WhitelistType type) {
        return configuration.getStringList("whitelist." + type.name().toLowerCase(), new ArrayList<>(List.of("null")));
    }

    public boolean status() {
        return configuration.getBoolean("whitelist.status", true);
    }


    public void setStatus(boolean v) {
        configuration.set("whitelist.status", v);
        configuration.safeSave();
    }

    public boolean playerIsInWhitelist(WhitelistType type, String target) {
        List<String> whitelist = getWhitelist(type);
        return whitelist.contains(target);
    }

    public void addToWhitelist(WhitelistType type, String target) {
        String typeName = type.name().toLowerCase();
        List<String> whitelist = getWhitelist(type);

        if (!whitelist.contains(target)) {
            whitelist.add(target);
        }

        configuration.set("whitelist." + typeName, whitelist);
        configuration.safeSave();

        refreshList();
    }

    public void removeFromWhitelist(WhitelistType type, String target) {
        String typeName = type.name().toLowerCase();

        List<String> whitelist = getWhitelist(type);
        whitelist.remove(target);


        configuration.set("whitelist." + typeName, whitelist);
        configuration.safeSave();

        refreshList();
    }

    public void setWhitelistType(WhitelistType type) {
        configuration.set("whitelist.type", type.name().toUpperCase());
        configuration.safeSave();

        Core.info("El tipo de whitelist fue cambiado a " + type.name() + ".");
    }

    public WhitelistType getType() {
        return WhitelistType.valueOf(configuration.getString("whitelist.type", "STAFF").toUpperCase());
    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        String name = event.getName();

        if (status()) {
            WhitelistType type = getType();

            if (type == WhitelistType.ALL) {
                if (playerIsInWhitelist(WhitelistType.STREAMERS, name) || playerIsInWhitelist(WhitelistType.STAFF, name) || playerIsInWhitelist(WhitelistType.PLAYERS, name)) {
                    // Permitir que el jugador entre si está en alguna de las tres listas
                } else {
                    Core.info("El jugador " + name + " intentó unirse pero no está en ninguna de las listas blancas.");
                    event.setKickMessage(color("&cNo estás en ninguna lista de ingreso permitida."));
                    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_FULL);
                }
            } else {
                if (!playerIsInWhitelist(type, name)) {
                    Core.info("El jugador " + name + " intentó unirse pero no está en la lista blanca de tipo: " + type.toString());
                    event.setKickMessage(color("&cNo estás en la lista de ingreso."));
                    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_FULL);
                }
            }
        }
    }



    public enum WhitelistType {
        STAFF,
        STREAMERS,
        PLAYERS,
        ALL
    }
}