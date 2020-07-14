package com.molean.minigame.minigame;

import com.molean.minigame.Minigame;
import com.molean.minigame.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class DisIntegration extends Minigame {

    private final Random r = new Random();

    public DisIntegration() {
        super("DisIntegration");
    }

    @Override
    public void setup(World world, int x, int y, int z) {
        super.setup(world, x, y, z);
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.WHITE_WOOL);
            }
        }
    }

    @Override
    public void init(List<Player> players) {
        super.init(players);
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.WHITE_WOOL);
            }
        }
        setRestrict(new Location(world, x - 1, y - 1, z - 1), new Location(world, x + 65, y + 65, z + 65));
    }

    /**
     * Start mini game
     */
    @Override
    public void start() {
        super.start();
        Utils.runTaskAsynchronously(() -> {
            for (int i = 0; inGame && i < 150; i++) {
                Utils.delay(40L);
                Utils.runTask(() -> {
                    for (int j = 0; j < 64; j++) {
                        for (int k = 0; k < 64; k++) {
                            if (r.nextBoolean() && r.nextBoolean()) {
                                Block block = world.getBlockAt(x + j, y, z + k);
                                switch (block.getType()) {
                                    case WHITE_WOOL:
                                        block.setType(Material.YELLOW_WOOL);
                                        break;
                                    case YELLOW_WOOL:
                                        block.setType(Material.ORANGE_WOOL);
                                        break;
                                    case ORANGE_WOOL:
                                        block.setType(Material.RED_WOOL);
                                        break;
                                    case RED_WOOL:
                                        block.setType(Material.AIR);
                                        break;
                                }
                            }
                        }
                    }
                });
                if (getPlayerList().size() == 0)
                    stop();
            }

        });

    }

    @Override
    public Location getSafeLanding() {
        return new Location(world, x + r.nextInt(64), y + 3, z + r.nextInt(64));
    }
}
