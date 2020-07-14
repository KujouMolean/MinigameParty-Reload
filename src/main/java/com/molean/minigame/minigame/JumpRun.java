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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class JumpRun extends Minigame {

    final private Random r = new Random();
    final private Map<Player, Integer> maxscore = new HashMap<>();

    public JumpRun() {
        super("JumpRun");
    }

    public void genStructrue() {
        /* gen start plaform */
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(randomWool());
            }
        }
        /* calc block location */
        List<Integer> structure = new ArrayList<>();
        int[] height = new int[54];
        Arrays.fill(height, -1);
        for (int i = r.nextInt(4); i < 54; i += r.nextInt(4) + 1) {
            structure.add(i);
            height[i] = 0;
        }
        /* move some block higher */
        Collections.shuffle(structure);
        for (int i = 0; i < 12; i++) {
            for (int j = structure.get(i); j < 54; j++) {
                if (height[j] >= 0)
                    height[j]++;
            }
        }
        /* remove other block */
        for (int i = 0; i < 54; i++) {
            for (int j = 0; j < 15; j++) {
                for (int k = 0; k < 64; k++) {
                    Block block = world.getBlockAt(x + i + 5, y + j, z + k);
                    block.setType(Material.AIR);
                }
            }
        }
        /* place block */
        for (int i = 0; i < height.length; i++) {
            if (height[i] >= 0) {
                Material material = randomWool();
                for (int j = 0; j < 64; j++) {
                    Block block = world.getBlockAt(x + i + 5, y + height[i], z + j);
                    block.setType(material);
                }
            }
        }
        /* end platform */
        for (int i = 64 - 5; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Block block = world.getBlockAt(x + i, y + 12, z + j);
                block.setType(randomWool());
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
                Map<String, Integer> map = new HashMap<>();
                players.forEach((player, miniGameMode) -> {
                    Integer integer = maxscore.get(player);
                    map.put(player.getName(), (int) (integer / 59.0 * 100.0));
                });
                setSoreborad(getName(), map);
            }
            List<Player> playerList = new ArrayList<>(getPlayerList());
            playerList.sort(Comparator.comparingInt(maxscore::get));
            Collections.reverse(playerList);
            for (Player player : playerList) {
                rankList.addEnd(player);
            }
            stop();
        });
    }

    @Override
    public Location getSafeLanding() {
        return new Location(world, x + r.nextInt(5), y + 3, z + r.nextInt(64), -90, 0);

    }

    @Override
    public boolean canRebornIfOut(Player player) {
        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        super.onPlayerMove(event);
        Player player = event.getPlayer();
        if (inGame && players.containsKey(player) && players.get(player).equals(MiniGameMode.gaming)) {
            Integer prev = maxscore.get(player);
            if (prev == null)
                prev = 0;
            int now = player.getLocation().getBlockX() - x;
            int modi = Math.min(Math.max(prev, now), 59);
            maxscore.put(player, modi);
            if (maxscore.get(player) > 59) {
                setMiniGameMode(player, MiniGameMode.spectator);
                rankList.addEnd(player);
            }
        }
    }

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
}
