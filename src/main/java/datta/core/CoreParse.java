package datta.core;

import datta.core.content.utils.EventPlayer;
import datta.core.games.games.ElSueloEsLava;
import datta.core.games.games.ReyDeLaColina;
import datta.core.services.list.TimerService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import static datta.core.content.builders.ColorBuilder.formatTime;
import static datta.core.games.games.ReyDeLaColina.getTop;

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

        EventPlayer eventPlayer = new EventPlayer(offlinePlayer.getPlayer());

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

        if (param.equalsIgnoreCase("lavastatus")) {
            return String.valueOf(ElSueloEsLava.level);
        }

        if (param.equalsIgnoreCase("points")) {
            return ReyDeLaColina.getPlayerTop(offlinePlayer.getPlayer());
        }

        if (param.contains("top_")) {
            int lastCharacterIndex = param.length() - 1;
            char lastChar = param.charAt(lastCharacterIndex); // Obtener el último carácter
            int lastCharacter = Character.getNumericValue(lastChar); // Convertir el último carácter en un número

            return getTop(lastCharacter);
        }

        if (param.equalsIgnoreCase("color")) {
            return eventPlayer.color();
        }

        if (param.equalsIgnoreCase("endat")) {
            return Core.endAt;
        }

        if (param.equalsIgnoreCase("bossbartime")) {
            return formatTime(TimerService.bossbarTime);
        }

        if (param.equalsIgnoreCase("actionbartime")) {
            return formatTime(TimerService.actionbarTime);
        }

        return param;
    }
}
