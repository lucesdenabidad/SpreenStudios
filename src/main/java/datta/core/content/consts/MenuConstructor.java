package datta.core.content.consts;

import org.bukkit.entity.Player;

import static datta.core.Core.menuBuilder;

public class MenuConstructor {
    Player player;

    public MenuConstructor(Player player, String title, int size, Runnable runnable) {
        this.player = player;
        menuBuilder.createMenu(player, title, size, false);
        menuBuilder.setContents(player, runnable);
    }
}