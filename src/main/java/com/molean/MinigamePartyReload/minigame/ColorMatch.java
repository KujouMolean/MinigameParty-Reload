package com.molean.MinigamePartyReload.minigame;

import com.molean.MinigamePartyReload.RankList;
import com.molean.MinigamePartyReload.Utils;
import com.molean.MinigamePartyReload.events.MinigameFinishEvent;
import com.molean.MinigamePartyReload.events.PlayerLeaveMinigame;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ColorMatch extends Minigame {

    private Random r = new Random();
    private int x, y, z;
    private World world;
    private boolean inGame;
    List<Player> players = new ArrayList<>();
    List<Player> spectators = new ArrayList<>();
    Material[][] structureData = new Material[16][16];
    RankList rankList = new RankList();

    @EventHandler
    public void onMinigameFinish(MinigameFinishEvent e)
    {
        if(e.getMinigame().getName().contains(this.getClass().getName()))
        {
            for(Player player:spectators)
            {
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                player.setGameMode(GameMode.SURVIVAL);
                            }
                        }.runTask(Utils.getPlugin());
//                        org.spigotmc.AsyncCatcher.enabled = false;
//                        player.setGameMode(GameMode.SURVIVAL);
//                        org.spigotmc.AsyncCatcher.enabled = true;
                    }
                }.runTaskAsynchronously(Utils.getPlugin());

            }
            spectators.clear();
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e)
    {
        if(players.contains(e.getPlayer())||spectators.contains(e.getPlayer()))
            e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerLeftGame(PlayerLeaveMinigame e)
    {
        players.remove(e);
        spectators.remove(e);
        //todo
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(!inGame)
            return;
        Player player = e.getPlayer();
        if (players.contains(player))
        {
            Location location = player.getLocation();
            if (!location.getWorld().getName().equals(world.getName()) ||
                    location.getBlockX() < x ||
                    location.getBlockZ() < z ||
                    location.getBlockX() > x + 64 ||
                    location.getBlockZ() > z + 64 ||
                    location.getBlockY() > y + 10 ||
                    location.getBlockY() < y - 3) {
                player.sendTitle("失败","你的游戏结束了",0,100,0);
                addToSpectator(player);
                rankList.addFirst(player);
                rankList.setHasWinner(true);
            }
        }else if(spectators.contains(player))
        {
            Location location = player.getLocation();
            if(     location.getBlockY() > y + 10 ||
                    location.getBlockY() < y )
            {
                player.teleport(new Location(world,location.getBlockX(),y+3,location.getBlockZ()));
            }
            if(location.getBlockX() < x )
            {
                player.teleport(new Location(world,x+1,location.getBlockY(),location.getBlockZ()));
            }
            if(location.getBlockX() > x + 64)
            {
                player.teleport(new Location(world,x+63,location.getBlockY(),location.getBlockZ()));
            }
            if(location.getBlockZ() < z)
            {
                player.teleport(new Location(world,location.getBlockX(),location.getBlockY(),z+1));
            }
            if(location.getBlockZ() > z + 64)
            {
                player.teleport(new Location(world,location.getBlockX(),location.getBlockY(),z+63));
            }

        }

    }
    private void addToSpectator(Player player)
    {
        if(players.contains(player))
        {
            players.remove(player);
            spectators.add(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(getSafeLanding());
        }

    }
    static ArrayList<Material> wools = new ArrayList<Material>(
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


    private Material randomWool() {
        return wools.get(r.nextInt(wools.size()));
    }

    public void init(List<Player> players) {

        rankList.clear();

        if (!Utils.getConfig().getBoolean("ColorMatch.hasGenerated")) {
            return;
        }

        this.players=players;

        x = Utils.getConfig().getInt("ColorMatch.x");
        y = Utils.getConfig().getInt("ColorMatch.y");
        z = Utils.getConfig().getInt("ColorMatch.z");
        String worldName = Utils.getConfig().getString("ColorMatch.world");
        world = Utils.getServer().getWorld(worldName);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Material wool = randomWool();
                structureData[i][j] = wool;
                setUnitMaterial(i, j, wool);
            }
        }

        for(Player player:players)
        {
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(getSafeLanding());
        }
    }

    private Material getUnitMaterial(int relative_x, int relative_y) {
        return world.getBlockAt(x + relative_x * 16, y, z + relative_y * 16).getType();
    }

    private void setUnitMaterial(int relative_x, int relative_y, Material material) {

        for (int m = 0; m < 4; m++) {
            for (int n = 0; n < 4; n++) {
                Block block = world.getBlockAt(x + relative_x * 4 + m, y, z + relative_y * 4 + n);
                block.setType(material);
            }
        }
    }

    public void setup(World world, int x, int y, int z) {
        Utils.getConfig().set("ColorMatch.hasGenerated", true);
        Utils.getConfig().set("ColorMatch.world", world.getName());
        Utils.getConfig().set("ColorMatch.x", x);
        Utils.getConfig().set("ColorMatch.y", y);
        Utils.getConfig().set("ColorMatch.z", z);
        Utils.saveConfig();

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
    public void setup(Location location) {
        setup(location.getWorld(),location.getBlockX(),location.getBlockY(),location.getBlockZ());

    }
    public Location getSafeLanding() {
        return new Location(world, x + r.nextInt(64), y + 3, z + r.nextInt(64));
    }

    public void trimUnit(Material wool) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (structureData[i][j] != wool) {
                    setUnitMaterial(i, j, Material.AIR);
                }
            }
        }

    }

    public void recoveryAllUnit() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                setUnitMaterial(i, j, structureData[i][j]);
            }
        }
    }

    public void start() {
        inGame=true;
        Utils.runTaskAsynchronously(new BukkitRunnable() {
            @Override
            public void run() {
                Utils.runTask(new BukkitRunnable() { // prepare
                    @Override
                    public void run() {
                        for (Player player : players) {
                            player.sendTitle("彩虹大作战", "站在与物品栏颜色对应的羊毛上", 0, 100, 0);
                        }
                    }
                });
                BukkitTask bukkitTask=new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (players.size() == 0)
                        {
                            Utils.broadcast("人全部挂了,游戏结束");
                            inGame = false;
                            Utils.getPluginManager().callEvent(new MinigameFinishEvent(this.getClass(), rankList, true) {
                            });
                            cancel();
                        }
                        final Material wool = randomWool();
                        BossBar bossBar = Utils.createBar("站在与物品栏相应颜色的羊毛上",BarColor.BLUE,BarStyle.SOLID);
                        for (Player player : players) {

                            player.getInventory().clear();
                            for (int i = 0; i < 9; i++) {
                                player.getInventory().setItem(i, new ItemStack(wool));
                            }
                            bossBar.addPlayer(player);
                        }
                        Utils.setBarAutoProgress(bossBar,80,()->{bossBar.removeAll();});

                        Utils.runTaskLater(new BukkitRunnable() {
                            @Override
                            public void run() {
                                if(inGame)
                                    trimUnit(wool);
                            }
                        }, 80l);
                        Utils.runTaskLater(new BukkitRunnable() {
                            @Override
                            public void run() {
                                if(inGame)
                                      recoveryAllUnit();
                            }
                        }, 150l);

                    }
                }.runTaskTimerAsynchronously(Utils.getPlugin(),20,150);
                Utils.runTaskLaterAsynchronously(new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!bukkitTask.isCancelled())
                        {
                            Utils.broadcast("时间到了");
                            inGame = false;
                            bukkitTask.cancel();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if(inGame)
                                        recoveryAllUnit();
                                }
                            }.runTask(Utils.getPlugin());
                            Utils.getPluginManager().callEvent(new MinigameFinishEvent(this.getClass(),rankList,true));
                        }
                    }
                }, 600l);
            }
        });
    }
}
