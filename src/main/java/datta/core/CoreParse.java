package datta.core;

import datta.core.games.games.ReyDeLaColinaGame;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import static datta.core.games.games.ReyDeLaColinaGame.getTop;

public class CoreParse extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "core";
    }

    @Override
    public @NotNull String getAuthor() {
        return "datta";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.1.1";
    }

    @Override
    public @NotNull String onRequest(OfflinePlayer offlinePlayer, String param) {

        if (param.equalsIgnoreCase("prefix")) {
            return Core.getInstance().prefix;
        }

        if (param.equalsIgnoreCase("zombies")) {
            return String.valueOf(Bukkit.getWorlds().get(0).getEntities().stream()
                    .filter(entity -> entity.getType() == EntityType.ZOMBIE)
                    .count());
        }

        if (param.equalsIgnoreCase("alive")) {
            return String.valueOf(Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                    .count());
        }

        if (param.equalsIgnoreCase("points")) {
            return ReyDeLaColinaGame.getPlayerTop(offlinePlayer.getPlayer());
        }
        if (param.contains("top_")) {
            int lastCharacterIndex = param.length() - 1;
            char lastChar = param.charAt(lastCharacterIndex); // Obtener el último carácter
            int lastCharacter = Character.getNumericValue(lastChar); // Convertir el último carácter en un número

            return getTop(lastCharacter);
        }
        return param;
    }
}
