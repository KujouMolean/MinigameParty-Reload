package com.molean.minigame.minigame;

import com.molean.minigame.Main;
import com.molean.minigame.Minigame;
import com.molean.minigame.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ColorMatch extends Minigame {
    /**
     * The map was generated every time, so here save the map data.
     */
    private Material[][] structureData = new Material[16][16];


    /*set game name*/
    public ColorMatch() {
        super("ColorMatch");
    }

    /**
     * prepare the game location.
     *
     * @param world world
     * @param x     x
     * @param y     y
     * @param z     z
     */
    @Override
    public void setup(World world, int x, int y, int z) {
        super.setup(world, x, y, z);

        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Material wool = randomWool();
                structureData[i][j] = wool;
                setUnitMaterial(i, j, wool);
            }
        }
    }

    /**
     * initialize game
     *
     * @param playerss players who are join game
     */
    @Override
    public void init(List<Player> playerss) {
        super.init(playerss);

        setRestrict(new Location(world, x - 1, y - 1, z - 1), new Location(world, x + 65, y + 65, z + 65));
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Material wool = randomWool();
                structureData[i][j] = wool;
                setUnitMaterial(i, j, wool);
            }
        }
    }

    /**
     * Start a game, you should init() before.
     */
    public void start() {
        super.start();

        // You should start a game asynchronously, otherwise the main thread would stop.
        Utils.runTaskAsynchronously(() -> {
            Utils.delay(100L);
            for (int r = 0; isInGame() && r < 40; r++) {
                Map<String, Integer> map = new HashMap<>();
                map.put("Round Left", 40 - r);
                setSoreborad(getName(), map);

                final Material wool = randomWool();
                BossBar bossBar = Utils.createBar("站在与物品栏相应颜色的羊毛上", BarColor.BLUE, BarStyle.SOLID);
                for (Player player : getPlayerList()) {
                    player.getInventory().clear();
                    bossBar.addPlayer(player);
                    for (int i = 0; i < 9; i++) {
                        player.getInventory().setItem(i, new ItemStack(wool));
                    }
                }
                Utils.setBarAutoProgress(bossBar, r * 2 - 80, bossBar::removeAll);
                Utils.delay(80L - r * 2);
                trimUnit(wool);
                Utils.delay(40L);
                recoveryAllUnit();
                if (getPlayerList().size() == 0) {
                    rankList.setHasWinner(true);
                    break;
                }

            }
            stop();
        });
    }

    /**
     * get a Location where player can stand with safe
     *
     * @return Location
     */
    @Override
    public Location getSafeLanding() {
        Random r = new Random();
        return new Location(world, x + r.nextInt(64), y + 3, z + r.nextInt(64));
    }

    /**
     * @return a random wool material that in variable "wools"
     */
    private Material randomWool() {
        Random r = new Random();
        ArrayList<Material> wools = new ArrayList<>(
                Arrays.asList(
                        Material.BLUE_WOOL,
                        Material.RED_WOOL,
                        Material.CYAN_WOOL,
                        Material.BLACK_WOOL,
                        Material.GREEN_WOOL,
                        Material.YELLOW_WOOL,
                        Material.ORANGE_WOOL,
                        Material.PURPLE_WOOL,
                        Material.LIME_WOOL));
        return wools.get(r.nextInt(wools.size()));
    }


    /**
     * same color of 4*4 blocks called unit.
     *
     * @param relative_x the relative x position of the unit
     * @param relative_y the relative x position of the unit
     * @param material   the material you want to set
     */
    private void setUnitMaterial(int relative_x, int relative_y, Material material) {
        for (int m = 0; m < 4; m++) {
            for (int n = 0; n < 4; n++) {
                Block block = world.getBlockAt(x + relative_x * 4 + m, y, z + relative_y * 4 + n);
                block.setType(material);
            }
        }
    }

    /**
     * set all wool to air except the wool you provide.
     *
     * @param wool the wool you don't want to set to air
     */
    public void trimUnit(Material wool) {
        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    if (structureData[i][j] != wool) {
                        setUnitMaterial(i, j, Material.AIR);
                    }
                }
            }
        });

    }

    /**
     * recover all wool to initial with the structureData
     */
    public void recoveryAllUnit() {
        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    setUnitMaterial(i, j, structureData[i][j]);
                }
            }
        });
    }
}
