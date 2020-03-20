package com.molean.MinigamePartyReload;

import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import java.util.logging.Logger;

public class Utils {
    public static Main getPlugin() {
        return Main.plugin;
    }
    public static FileConfiguration getConfig()
    {
        return Main.config;
    }
    public static Server getServer(){
        return getPlugin().getServer();
    }
    public static void info(String s) {
        getPlugin().getLogger().info(s);
    }

    public static Logger getLogger() {
        return getPlugin().getLogger();
    }
    public static void setDefault(String s, Object obj)
    {
        getConfig().addDefault(s, true);
        getConfig().set(s, getConfig().get(s));
    }
    public static void saveConfig(){
        getPlugin().saveConfig();
    }
    public static Player getPlayer(String s)
    {
        return getServer().getPlayer(s);
    }
    public static BukkitScheduler getScheduler()
    {

        return getServer().getScheduler();
    }
    public static BukkitTask runTaskLater(Runnable runnable, Long delay)
    {
        return getScheduler().runTaskLater(getPlugin(),runnable,delay);
    }
    public static BukkitTask runTaskLaterAsynchronously(Runnable runnable, Long delay)
    {
        return getScheduler().runTaskLaterAsynchronously(getPlugin(),runnable,delay);
    }
    public static BukkitTask runTaskTimerAsynchronously(Runnable runnable, long interval, long delay)
    {
        return getScheduler().runTaskTimerAsynchronously(getPlugin(),runnable,interval,delay);
    }
    public static BukkitTask runTaskAsynchronously(Runnable runnable)
    {
        return getScheduler().runTaskAsynchronously(getPlugin(),runnable);
    }
    public static PluginManager getPluginManager()
    {
        return getServer().getPluginManager();
    }

    public static BukkitTask runTask(Runnable runnable)
    {
        return getScheduler().runTask(getPlugin(),runnable);
    }
    public static void broadcast(String s)
    {
        getServer().broadcastMessage(s);
    }
    public static MinigameManager getMinigameManager()
    {
        return Main.minigameManager;
    }
    public static Player getPlayer(CommandSender commandSender)
    {
        return getPlayer(commandSender.getName());
    }
    public static BossBar createBar(String title,BarColor barColor,BarStyle barStyle)
    {
        return getServer().createBossBar(title, barColor, barStyle);
    }
    public static void setBarAutoProgress(BossBar bossBar, int time,Runnable afterFinish)
    {
        final Integer i= new Integer(0);
        bossBar.setVisible(false);
        if(time>0)
            bossBar.setProgress(0);
        else
            bossBar.setProgress(1);

        bossBar.setVisible(true);

        final BukkitTask bukkitTask=new BukkitRunnable() {
            @Override

            public void run() {
                double progress = bossBar.getProgress()+1.0/time;
                if(progress>1.0)
                    progress=1.0;
                if(progress<0)
                    progress=0;
                bossBar.setProgress(progress);
            }
        }.runTaskTimerAsynchronously(getPlugin(),0,1);

        new BukkitRunnable(){
            @Override
            public void run() {
                bukkitTask.cancel();
                afterFinish.run();
            }
        }.runTaskLaterAsynchronously(getPlugin(),Math.abs(time));

    }


}
