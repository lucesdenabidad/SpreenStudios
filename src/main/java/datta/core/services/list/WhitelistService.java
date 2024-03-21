package datta.core.services.list;

import co.aikar.commands.annotation.*;
import datta.core.Core;
import datta.core.content.configuration.Configuration;
import datta.core.services.Service;
import datta.core.utils.SenderUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;

import static datta.core.content.builders.ColorBuilder.color;


@CommandPermission("|spreenstudios.*|spreenstudios.whitelist")
@CommandAlias("whitelist|wl|swl|xwl|uwl|pwl|wl|listablanca|lista")
public class WhitelistService extends Service {
    Configuration configuration = Core.getInstance().configurationManager.getConfig("configuration.yml");

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
            return getWhitelist();
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

    @CommandCompletion("@players")
    @Subcommand("add")
    public void addWhitelist(CommandSender sender, String target) {
        addToWhitelist(target);
        SenderUtil.sendMessage(sender, "%core_prefix% &aSe ha agregado a " + target + " a la lista de ingreso.");
    }

    @CommandCompletion("@whitelist_players")
    @Subcommand("remove")
    public void removeWhitelist(CommandSender sender, String target) {
        removeFromWhitelist(target);
        SenderUtil.sendMessage(sender, "%core_prefix% &cSe ha eliminado a " + target + " a la lista de ingreso.");
    }

    @Subcommand("list")
    public void getList(CommandSender sender) {
        List<String> whitelist = getWhitelist();
        SenderUtil.sendMessage(sender,
                "&8&m                                   &f",
                "&bLista blanca:");

        for (String s : whitelist) {
            SenderUtil.sendMessage(sender, "&9 • &f" + s);
        }

        SenderUtil.sendMessage(sender, "&8&m                                   &f");
    }


    public List<String> getWhitelist() {
        return configuration.getStringList("whitelist.list");
    }

    public boolean status() {
        return configuration.getBoolean("whitelist.status", false);
    }



    public void setStatus( boolean v) {
        configuration.set("whitelist.status", v);
        configuration.safeSave();
    }
    public boolean playerIsInWhitelist(String target) {
        List<String> whitelist = getWhitelist();
        return whitelist.contains(target);
    }

    public void addToWhitelist(String target) {
        List<String> whitelist = getWhitelist();
        if (!whitelist.contains(target)) {
            whitelist.add(target);
        }

        configuration.set("whitelist.list", whitelist);
        configuration.safeSave();

        refreshList();
    }

    public void removeFromWhitelist(String target) {
        List<String> whitelist = getWhitelist();
        if (whitelist.contains(target)) {
            whitelist.remove(target);
        }

        configuration.set("whitelist.list", whitelist);
        configuration.safeSave();

        refreshList();
    }


    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        String name = event.getName();

        if (status()) {
            if (!playerIsInWhitelist(name)) {
                Core.info("El jugador " + name + " intentó unirse pero no está en la lista blanca.");
                event.setKickMessage(color("&cNo estás en la lista de ingreso."));
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_FULL);
            }
        }
    }
}