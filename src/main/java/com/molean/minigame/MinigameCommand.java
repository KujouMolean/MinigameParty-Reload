package com.molean.minigame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

import static com.molean.minigame.Utils.getMessage;


public class MinigameCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("Only player can use this command.");
            return false;
        }
        Player player = (Player) commandSender;
        if (strings.length == 0) {
            return false;
        }
        String subcmd = strings[0].toLowerCase();
        switch (subcmd) {
            case "start":
                MinigameManager.getInstance().start(strings[1], player);
                break;
            case "setup":
                try {
                    Class<?> aClass = Class.forName("com.molean.minigame.minigame." + strings[1]);
                    Minigame minigame = ((Minigame) aClass.newInstance());
                    minigame.setup(player.getWorld(),
                            player.getLocation().getBlockX(),
                            player.getLocation().getBlockY(),
                            player.getLocation().getBlockZ());
                } catch (ClassNotFoundException e) {
                    player.sendMessage(getMessage("General.GameDoesNotExsit"));
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    player.sendMessage(getMessage("General.InternalError"));
                }
                break;
            case "join":
                MinigameManager.getInstance().join(player);
                break;
            case "left":
                MinigameManager.getInstance().left(player);
                break;
            case "debug":
                Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective objective = newScoreboard.registerNewObjective("SheepFreezy", "dummy", "");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setDisplayName(ChatColor.RED + "YourScoreboardTitle");
                Score score = objective.getScore(player.getName());
                score.setScore(10);
                player.setScoreboard(newScoreboard);
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        String subcmds[] = {"start", "setup", "join"};
        List<String> returns = new ArrayList<>();
        if (strings.length == 1) {
            for (String subcmd : subcmds) {
                if (subcmd.startsWith(strings[0])) {
                    returns.add(subcmd);
                }
            }
            return returns;
        }
        return null;
    }

}