package com.molean.minigame;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public class Utils {
    public static Main getPlugin() {
        return Main.getPlugin();
    }

    public static Server getServer() {
        return getPlugin().getServer();
    }

    public static void info(String s) {
        getPlugin().getLogger().info(s);
    }

    public static Logger getLogger() {
        return getPlugin().getLogger();
    }

    public static void saveConfig() {
        getPlugin().saveConfig();
    }

    public static Player getPlayer(String s) {
        return getServer().getPlayer(s);
    }

    public static BukkitScheduler getScheduler() {

        return getServer().getScheduler();
    }

    public static BukkitTask runTaskLater(Runnable runnable, Long delay) {
        return getScheduler().runTaskLater(getPlugin(), runnable, delay);
    }

    public static BukkitTask runTaskLaterAsynchronously(Runnable runnable, Long delay) {
        return getScheduler().runTaskLaterAsynchronously(getPlugin(), runnable, delay);
    }

    public static BukkitTask runTaskTimerAsynchronously(Runnable runnable, long interval, long delay) {
        return getScheduler().runTaskTimerAsynchronously(getPlugin(), runnable, interval, delay);
    }

    public static BukkitTask runTaskTimer(Runnable runnable, long interval, long delay) {
        return getScheduler().runTaskTimer(getPlugin(), runnable, interval, delay);
    }

    public static BukkitTask runTaskAsynchronously(Runnable runnable) {
        return getScheduler().runTaskAsynchronously(getPlugin(), runnable);
    }

    public static void delay(Long delay) {
        try {
            Thread.sleep(delay * 50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void callEvent(Event event) {
        runTask(() -> {
            Utils.getPluginManager().callEvent(event);
        });

    }

    public static void registerEvents(Listener listener) {
        getPluginManager().registerEvents(listener, getPlugin());
    }

    public static PluginManager getPluginManager() {
        return getServer().getPluginManager();
    }

    public static BukkitTask runTask(Runnable runnable) {
        return getScheduler().runTask(getPlugin(), runnable);
    }

    public static void broadcast(String s) {
        getServer().broadcastMessage(s);
    }

    public static String getMessage(String s) {
        return ConfigUtils.getConfig("message.yml").getString(s);
    }

    public static Player getPlayer(CommandSender commandSender) {
        return getPlayer(commandSender.getName());
    }

    public static BossBar createBar(String title, BarColor barColor, BarStyle barStyle) {
        return getServer().createBossBar(title, barColor, barStyle);
    }

    public static void setBarAutoProgress(BossBar bossBar, int time, Runnable afterFinish) {
        Utils.runTaskAsynchronously(() -> {
            final Integer i = new Integer(0);
            bossBar.setVisible(false);
            if (time > 0)
                bossBar.setProgress(0);
            else
                bossBar.setProgress(1);

            bossBar.setVisible(true);

            final BukkitTask bukkitTask = new BukkitRunnable() {
                @Override

                public void run() {
                    double progress = bossBar.getProgress() + 2.0 / time;
                    if (progress > 1.0)
                        progress = 1.0;
                    if (progress < 0)
                        progress = 0;
                    bossBar.setProgress(progress);
                }
            }.runTaskTimerAsynchronously(getPlugin(), 0, 2);

            new BukkitRunnable() {
                @Override
                public void run() {
                    bukkitTask.cancel();
                    afterFinish.run();
                }
            }.runTaskLaterAsynchronously(getPlugin(), Math.abs(time));
        });
    }

    public static boolean inBox(Location pos1, Location pos2, Location target) {
        if (!Objects.equals(pos1.getWorld(), pos2.getWorld()))
            return false;
        if (!Objects.equals(pos1.getWorld(), target.getWorld()))
            return false;
        if(target.getBlockX() < pos1.getBlockX())
            return false;
        if(target.getBlockY() < pos1.getBlockY())
            return false;
        if(target.getBlockZ() < pos1.getBlockZ())
            return false;
        if(target.getBlockX() > pos2.getBlockX())
            return false;
        if(target.getBlockY() > pos2.getBlockY())
            return false;
        if(target.getBlockZ() > pos2.getBlockZ())
            return false;
        return true;
    }
    public static Location recentInBox(Location pos1, Location pos2, Location target){
        if(inBox(pos1, pos2, target))
            return target;
        Location location = target.clone();
        if (location.getBlockY() > pos2.getBlockY()) {
            location.setY(pos2.getBlockY() - 1);
        } else if (location.getBlockY() < pos1.getBlockY()) {
            location.setY(pos1.getBlockY() + 1);
        } else if (location.getBlockX() < pos1.getBlockX()) {
            location.setX(pos1.getBlockX() + 1);
        } else if (location.getBlockX() > pos2.getBlockX()) {
            location.setX(pos2.getBlockX() - 1);
        } else if (location.getBlockZ() < pos1.getBlockZ()) {
            location.setZ(pos1.getBlockZ() + 1);
        } else if (location.getBlockZ() > pos2.getBlockZ()) {
            location.setZ(pos2.getBlockZ() - 1);
        }
        return location;
    }


}
