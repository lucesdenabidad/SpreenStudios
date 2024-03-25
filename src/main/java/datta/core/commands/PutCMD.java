package datta.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import datta.core.Core;
import datta.core.content.configuration.Configuration;
import datta.core.utils.SenderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@CommandPermission("spreenstudios.print")
@CommandAlias("print|put")
public class PutCMD extends BaseCommand {
    public Configuration configuration = Core.getInstance().configurationManager.getConfig("latest.yml");

    @Subcommand("players")
    public void printPlayers() {
        List<String> list = new ArrayList<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            SenderUtil.sendActionbar(onlinePlayer, "&2(!) &aTu nombre se guardo con éxito en 'latest.yml'", Sound.BLOCK_NOTE_BLOCK_BIT);
            list.add(onlinePlayer.getName());
        }

        printList(list);
    }


    @CommandCompletion("@range:100")
    @Subcommand("fixedprint")
    public void filterPrint(int id) {
        String ID = "ID-" + id;
        List<String> stringList = configuration.getStringList("logs." + ID + ".printed");

        HashSet<String> set = new HashSet<>(stringList);
        List<String> fixedList = new ArrayList<>(set);

        printList(fixedList);
    }

    @CommandCompletion("@range:100")
    @Subcommand("size")
    public void printSizeCMD(int id) {
        int i = printSize(id);

    }

    // # Metodos


    public int printSize(int id) {
        String ID = "ID-" + id;
        List<String> stringList = configuration.getStringList("logs." + ID + ".printed", new ArrayList<>());
        Core.info("Tamaño: &a" + stringList.size());
        return stringList.size();
    }

    public void printList(List<String> print) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dia = sdf.format(new Date());

        sdf.applyPattern("HH:mm");
        String hora = sdf.format(new Date());

        int id = configuration.getInt("logs.next_id", 1);
        configuration.set("logs.next_id", id + 1);

        String idToString = "ID-" + id;
        configuration.set("logs." + idToString + ".info.time", hora);
        configuration.set("logs." + idToString + ".info.day", dia);
        configuration.set("logs." + idToString + ".printed", print);
        configuration.safeSave();

        Core.info("Se han printeado la lista ingresada con éxito en 'latest.yml' con la ID: " + id);
    }


    @Subcommand("filterlog")
    public void filterLog(String logFileName) {
        List<String> filterList = new ArrayList<>();

        // Obtener la carpeta de datos de tu plugin
        File pluginDir = Core.getInstance().getDataFolder();
        File logFile = new File(pluginDir, logFileName);

        if (!logFile.exists()) {
            System.err.println("El archivo de registro no existe: " + logFile.getPath());
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;
            while ((line = reader.readLine()) != null) {
                filterList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<String> filteredLines = filterLogs(filterList);
        List<String> fixLines = filteredLines.stream()
                .map(line -> line.replaceAll(" ", "")).collect(Collectors.toList());
        printList(fixLines);
    }


    public static List<String> filterLogs(List<String> logs) {
        List<String> filteredLogs = new ArrayList<>();


        String[] removeLeters = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ",", ".", ")",""};
        for (String log : logs) {
            if (log.contains("logged")) {

                for (String removeLeter : removeLeters)
                    log = log.replace(removeLeter, "");

                log = log.substring(26);
                log = log.replace("[<ip address withheld>] logged in with entity id  at ([world]  -", "");

                log = log.replace("lostconnection:Youloggedinfromanotherlocation", "");
                filteredLogs.add(log);
            }
        }

        return filteredLogs;
    }
}