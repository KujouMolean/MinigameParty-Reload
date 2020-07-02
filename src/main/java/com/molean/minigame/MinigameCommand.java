package com.molean.minigame;

import com.molean.minigame.minigame.ColorMatch;
import com.molean.minigame.minigame.Spleef;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


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
                if(strings[1].equalsIgnoreCase("colormatch")){
                    new ColorMatch().setup(player.getWorld(),
                            player.getLocation().getBlockX(),
                            player.getLocation().getBlockY(),
                            player.getLocation().getBlockZ());
                }else if(strings[1].equalsIgnoreCase("spleef")){
                    new Spleef().setup(player.getWorld(),
                            player.getLocation().getBlockX(),
                            player.getLocation().getBlockY(),
                            player.getLocation().getBlockZ());
                }
                break;
            case "join":
                MinigameManager.getInstance().join(player);
                break;
            case "left":
                MinigameManager.getInstance().left(player);
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        String subcmds[] = {"start", "setup"};
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