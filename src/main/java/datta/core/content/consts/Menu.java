package datta.core.content.consts;

import org.bukkit.entity.Player;

public abstract class Menu {

    public abstract String title();
    public abstract int size();

    public abstract void open(Player player);
}
