package com.molean.MinigamePartyReload.minigame;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class ChickenTag extends Minigame {
    @Override
    public void init(List<Player> players) {
        players.get(1).setGlowing(true);
    }
    @Override
    public void start() {

    }

    @Override
    public void setup(World world, int x, int y, int z) {

    }

    @Override
    public void setup(Location l) {

    }
}
