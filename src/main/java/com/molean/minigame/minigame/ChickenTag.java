package com.molean.minigame.minigame;

import com.molean.minigame.Minigame;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class ChickenTag extends Minigame {
    public ChickenTag(String name) {
        super(name);
    }

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
    public Location getSafeLanding() {
        return null;
    }


}
