package me.bobthe28th.capturethefart.ctf;

import me.bobthe28th.capturethefart.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CTFTabCompletion implements TabCompleter {

    List<String> playerList() {
        List<String> playerNames = new ArrayList<>();
        Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
        Bukkit.getServer().getOnlinePlayers().toArray(players);
        for (Player player : players) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {


        switch (cmd.getName().toLowerCase()) {
            case "ctfjoin":
            case "ctfleave":
            case "ctfteamleave":
            case "ctfleaveclass":
            case "adminpm":
                if (args.length == 1) {
                    return playerList();
                }
                break;
            case "ctfteamjoin":
                switch (args.length) {
                    case 1:
                        return playerList();
                    case 2:
                        List<String> arguments = new ArrayList<>();

                        String[] teamNames = Main.getTeamNames();

                        Collections.addAll(arguments, teamNames);

                        return arguments;
                }
            case "ctfsetclass":
                switch (args.length) {
                    case 1:
                        return playerList();
                    case 2:
                        List<String> arguments = new ArrayList<>();

                        Collections.addAll(arguments, Main.CTFClassNames);

                        return arguments;
                }
            case "ctffulljoin":
                switch (args.length) {
                    case 1:
                        return playerList();
                    case 2:
                        List<String> arguments = new ArrayList<>();
                        Collections.addAll(arguments, Main.CTFClassNames);
                        return arguments;
                    case 3:
                        List<String> arguments2 = new ArrayList<>();
                        String[] teamNames = Main.getTeamNames();
                        Collections.addAll(arguments2, teamNames);
                        return arguments2;
                }
            case "ctfhelp":
                if (args.length == 1) {
                    List<String> arguments = new ArrayList<>();
                    Collections.addAll(arguments, Main.CTFClassNames);
                    return arguments;
                }
            case "ctfsetmap":
                if (args.length == 1) {
                    List<String> arguments = new ArrayList<>();
                    String[] mapNames = Main.getMapNames();
                    Collections.addAll(arguments, mapNames);
                    return arguments;
                }
            case "music":
                switch (args.length) {
                    case 1:
                        List<String> arguments = new ArrayList<>();
                        Collections.addAll(arguments, "play","stop","skip","queue");
                        return arguments;
                    case 2:
                        if (args[0] != null) {
                            if (args[0].equals("play")) {
                                List<String> arguments2 = new ArrayList<>();
                                Collections.addAll(arguments2, "now","queue");
                                return arguments2;
                            }
                        }
                        break;
                    case 3:
                        if (args[0] != null) {
                            if (args[0].equals("play")) {
                                List<String> arguments2 = new ArrayList<>();
                                Collections.addAll(arguments2, Main.music);
                                return arguments2;
                            }
                        }
                }
        }

        return new ArrayList<>();
    }
}
