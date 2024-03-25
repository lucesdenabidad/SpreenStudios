package datta.core.games;

import datta.core.Core;
import datta.core.games.games.*;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    public final List<Game> games = new ArrayList<>();

    private final Core instance;

    private final ElSueloEsLava elSueloEsLava;
    private final Escondite escondite;
    private final Puertas puertas;
    private final ReyDeLaColina reyDeLaColina;
    private final SillasMusicales sillitasGame;

    public GameManager(Core instance) {
        this.instance = instance;
        games.add(elSueloEsLava = new ElSueloEsLava());
        games.add(escondite = new Escondite());
        games.add(puertas = new Puertas());
        games.add(reyDeLaColina = new ReyDeLaColina());
        games.add(sillitasGame = new SillasMusicales());

    }

    public void loadGamesFromList() {
        for (Game game : games) {
            loadGame(game);
        }
    }

    public void unloadGamesFromList() {
        for (Game game : games) {
            unloadGame(game);
        }
    }

    public void loadGame(Game game) {
        Core.getInstance().commandManager.registerCommand(game);
        Core.getInstance().getServer().getPluginManager().registerEvents(game, instance);
    }


    public void unloadGame(Game game) {
        games.remove(game);
        HandlerList.unregisterAll(game);
    }


    public Game getGame(String name) {
        for (Game game : games) {
            if (game.name().equalsIgnoreCase(name)) {
                return game;
            }
        }
        return null;
    }
}

