package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import datta.core.services.list.ToggleService;


@CommandPermission("spreenstudios.call")
@CommandAlias("call")
public class CallCMD extends BaseCommand {

    @CommandCompletion(" true|false")
    @Subcommand("call")
    public static void callToggleable(ToggleService.Toggleable toggleable, boolean call) {
        toggleable.set(call);
    }

    @CommandCompletion(" true|false true|false")
    @Subcommand("call")
    public static void callToggleable(ToggleService.Toggleable toggleable, boolean call, boolean silens) {
        toggleable.set(call, silens);
    }
}