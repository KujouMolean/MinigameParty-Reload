package com.molean.minigame.minigame;

import com.molean.minigame.Minigame;
import com.molean.minigame.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MoreMine extends Minigame {
    private final Random r = new Random();

    public MoreMine() {
        super("MoreMine");
    }

    public void genStructrue() {
        Material[][][] data = new Material[64][12][64];
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 1; j++) {
                for (int k = 0; k < 64; k++) {
                    data[i][j][k] = Material.BEDROCK;
                }
            }
        }
        for (int i = 0; i < 64; i++) {
            for (int j = 1; j < 11; j++) {
                for (int k = 0; k < 64; k++) {
                    int num = r.nextInt(100);
                    if (num < 45) {
                        data[i][j][k] = Material.STONE;
                        continue;
                    }
                    if (num < 90) {
                        data[i][j][k] = Material.DIRT;
                        continue;
                    }
                    if (num < 97) {
                        data[i][j][k] = Material.COAL_ORE;
                        continue;
                    }
                    if (num < 99) {
                        data[i][j][k] = Material.DIAMOND_ORE;
                        continue;
                    }
                    if (num < 100) {
                        data[i][j][k] = Material.EMERALD_ORE;
                        continue;
                    }
                }
            }
        }
        for (int i = 0; i < 64; i++) {
            for (int j = 11; j < 12; j++) {
                for (int k = 0; k < 64; k++) {
                    data[i][j][k] = Material.GRASS_BLOCK;
                }
            }
        }
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 12; j++) {
                for (int k = 0; k < 64; k++) {
                    Block block = world.getBlockAt(x + i, y + j, z + k);
                    block.setType(data[i][j][k]);
                }
            }
        }
    }

    @Override
    public void setup(World world, int x, int y, int z) {
        super.setup(world, x, y, z);
        genStructrue();
    }

    @Override
    public void init(List<Player> players) {
        super.init(players);
        genStructrue();
        for (Player player : players) {
            ItemStack diamondShovel = new ItemStack(Material.DIAMOND_SHOVEL);
            diamondShovel.addEnchantment(Enchantment.DIG_SPEED, 4);
            ItemStack diamondPickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
            diamondPickaxe.addEnchantment(Enchantment.DIG_SPEED, 4);
            player.getInventory().addItem(diamondShovel, diamondPickaxe,new ItemStack(Material.TORCH,32));
        }
        setRestrict(new Location(world, x - 1, y - 1, z - 1), new Location(world, x + 65, y + 65, z + 65));
    }

    @Override
    public void start() {
        super.start();
        BossBar bossBar = Utils.createBar("剩余时间", BarColor.BLUE, BarStyle.SOLID);
        Utils.setBarAutoProgress(bossBar, -2400, bossBar::removeAll);
        players.keySet().forEach(bossBar::addPlayer);
        Utils.runTaskAsynchronously(() -> {
            for (int i = 0; inGame && i < 120; i++) {
                Utils.delay(20L);
                Utils.runTask(() -> {
                    List<Player> playerList = new ArrayList<>(players.keySet());
                    playerList.sort(Comparator.comparingInt(this::getScore));
                    Map<String, Integer> map = new HashMap<>();
                    for (Player player : playerList) {
                        map.put(player.getName(), getScore(player));
                    }
                    setSoreborad(getName(), map);
                });
                if (getPlayerList().size() == 0)
                    break;
            }
            List<Player> playerList = new ArrayList<>(players.keySet());
            playerList.sort(Comparator.comparingInt(this::getScore));
            for (Player player : playerList) {
                rankList.addEnd(player);
            }
            stop();
        });
    }

    @Override
    public Location getSafeLanding() {
        return new Location(world, x + r.nextInt(64), y + 15, z + r.nextInt(64));
    }

    private int getScore(Player player) {
        int score = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType().equals(Material.COAL)) {
                score += content.getAmount();
            }
            if (content != null && content.getType().equals(Material.DIAMOND)) {
                score += content.getAmount() * 3;
            }
            if (content != null && content.getType().equals(Material.EMERALD)) {
                score += content.getAmount() * 5;
            }
        }
        return score;
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        return Utils.inBox(pos1, pos2, location);
    }
}
