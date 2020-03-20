package com.molean.MinigamePartyReload;

import com.molean.MinigamePartyReload.minigame.ColorMatch;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener {
    static Main plugin;
    static FileConfiguration config;
    static ColorMatch colorMatch;
    static MinigameManager minigameManager;
    @Override
    public void onEnable()
    {
        //initialize some objects
        plugin = this;
        config = getConfig();
        minigameManager = new MinigameManager();

        minigameManager.prepare();
        //config
        initConfig();

        //register event handlers
        getServer().getPluginManager().registerEvents(minigameManager, this);

        //register command
        getCommand("minigame").setExecutor(new MinigameCommand());



    }
    @Override
    public void onDisable(){
        Utils.saveConfig();
    }
    void initConfig() {

    }


}
