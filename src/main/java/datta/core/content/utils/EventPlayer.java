package datta.core.content.utils;

import org.bukkit.entity.Player;

import java.util.List;

import static datta.core.content.utils.EventUtils.COLORS;

public class EventPlayer {

    public Player player;

    public EventPlayer(Player player) {
        this.player = player;
    }

    public boolean isInList(List list) {
        return list.contains(player);
    }

    public boolean isStaff() {
        return player.isOp() || player.hasPermission("*") || player.hasPermission("spreenstudios.staff");
    }

    public String color() {
        return COLORS.getOrDefault(player, "");
    }
}