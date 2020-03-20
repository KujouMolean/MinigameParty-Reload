package com.molean.MinigamePartyReload.minigame;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class Minigame implements Listener {
    public abstract void init(List<Player> players);
    public abstract void start();
    public abstract void setup(World world, int x, int y, int z);
    public abstract void setup(Location l);
}
