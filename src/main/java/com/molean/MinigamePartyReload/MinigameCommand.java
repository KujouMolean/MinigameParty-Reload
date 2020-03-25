package com.molean.MinigamePartyReload;

import com.molean.MinigamePartyReload.events.MinigameFinishEvent;
import com.molean.MinigamePartyReload.events.MinigameSetupEvent;
import com.molean.MinigamePartyReload.events.RoundStartEvent;

import com.molean.MinigamePartyReload.minigame.ColorMatch;
import com.molean.MinigamePartyReload.minigame.Spleef;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;

import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class MinigameCommand implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command,
                             String s, String[] strings) {

        if(commandSender.getName()=="Server")
            return false;

        if (strings.length == 0) {
            return false;
        } else {
            if(strings[0].equalsIgnoreCase("test"))
            {
                if(strings.length<3)
                {
                    commandSender.sendMessage("参数不足");
                    return false;
                }
                if(strings[1].equalsIgnoreCase("bar"))
                {
                    BossBar bossBar = Utils.createBar("",BarColor.BLUE,BarStyle.SOLID);
                    bossBar.addPlayer(Utils.getPlayer(commandSender));
                    bossBar.setVisible(true);
                    Utils.setBarAutoProgress(bossBar,Integer.parseInt(strings[2]),()->{bossBar.setVisible(false);});
                }
            }
            if(strings[0].equalsIgnoreCase("start"))
            {
                commandSender.sendMessage("准备开始游戏");
                Utils.getPluginManager().callEvent(new RoundStartEvent());
            }
            if(strings[0].equalsIgnoreCase("join"))
            {
                Player player = Utils.getPlayer(commandSender.getName());
                Utils.getMinigameManager().addPlayer(player);
            }
            if(strings[0].equalsIgnoreCase("colormatch"))
            {
                if(strings[1].equalsIgnoreCase("setup"))
                {
                    Utils.broadcast("已为服务器添加任务.");
                    Utils.getPluginManager().callEvent(new MinigameSetupEvent(Utils.getPlayer(commandSender.getName()).getLocation(), ColorMatch.class) {
                    });
                }
            }
            if(strings[0].equalsIgnoreCase("spleef"))
            {
                if(strings[1].equalsIgnoreCase("setup"))
                {
                    Utils.broadcast("已为服务器添加任务");
                    Utils.getPluginManager().callEvent(new MinigameSetupEvent(Utils.getPlayer(commandSender.getName()).getLocation()
                            , Spleef.class));
                }
            }
        }

        return true;
    }
}
