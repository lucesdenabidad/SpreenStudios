package datta.core.games;

import datta.core.Core;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class CommandGame {
    private final Core instance;
    public List<Game> gameList = new ArrayList<>();

    public CommandGame(Core instance) {
        this.instance = instance;
    }

    public void registerGame(Game game, boolean cmd, boolean event) {
        if (cmd)
            Core.getInstance().commandManager.registerCommand(game);

        gameList.add(game);
    }

    public void unregisterGame(Game game) {
        gameList.remove(game);
        unregisterGameEvents(game);
    }

    private void registerGameEvents(Game game) {
        Core.getInstance().getServer().getPluginManager().registerEvents(game, instance);
    }

    private void unregisterGameEvents(Game game) {
        HandlerList.unregisterAll(game);
    }
}
