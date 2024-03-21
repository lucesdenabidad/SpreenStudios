package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import datta.core.utils.SenderUtil;
import org.bukkit.entity.Player;

@CommandAlias("flyspeed|speed")
public class SpeedCMD extends BaseCommand {

    @Default
    @CommandPermission("spreenstudios.speed")
    public void flyCMD(Player player, float v) {
        if (v > 10.0 || v < 1.0) {
            SenderUtil.sendMessage(player, "%core_prefix% &cLa velocidad debe estar entre 1.0 y 10.0");
            return;
        }

        changeSpeed(player, player.isFlying(), v);
    }

    public void changeSpeed(Player player, boolean isFly, float v) {
        if (isFly) {
            player.setFlySpeed(v / 10.0f);
            SenderUtil.sendMessage(player, "%core_prefix% &eCambiaste la velocidad al volar a " + v);
        } else {
            player.setWalkSpeed(v / 10.0f);
            SenderUtil.sendMessage(player, "%core_prefix% &eCambiaste la velocidad al caminar a " + v);
        }
    }
}
