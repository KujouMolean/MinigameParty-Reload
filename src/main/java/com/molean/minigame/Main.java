package com.molean.minigame;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import sun.security.krb5.Config;

import java.util.Objects;


public class Main extends JavaPlugin implements Listener {
    private static Main plugin;

    public static Main getPlugin() {
        return plugin;
    }


    @Override
    public void onEnable() {
        //initialize some objects

        plugin = this;
        ConfigUtils.setupConfig(this);
        ConfigUtils.configOuput("message.yml");

        //register command
        Objects.requireNonNull(getCommand("minigame")).setExecutor(new MinigameCommand());
        Objects.requireNonNull(getCommand("minigame")).setTabCompleter(new MinigameCommand());

        getServer().getPluginManager().registerEvents(MinigameManager.getInstance(),this);

    }

    @Override
    public void onDisable() {
        Utils.saveConfig();
    }



}
