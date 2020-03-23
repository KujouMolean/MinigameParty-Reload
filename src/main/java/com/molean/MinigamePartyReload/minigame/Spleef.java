package com.molean.MinigamePartyReload.minigame;

import com.molean.MinigamePartyReload.RankList;
import com.molean.MinigamePartyReload.Utils;
import com.molean.MinigamePartyReload.events.MinigameFinishEvent;
import com.molean.MinigamePartyReload.events.PlayerLeaveMinigame;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Spleef extends Minigame{
    private int x;
    private int y;
    private int z;
    World world;
    List<Player> players;
    List<Player> spectators = new ArrayList<>();
    boolean inGame;
    RankList rankList = new RankList();

    Random r = new Random();
    @EventHandler
    void onPlayerLeave(PlayerQuitEvent e)
    {
        e.getPlayer().setGameMode(GameMode.SURVIVAL);
        e.getPlayer().getInventory().clear();

    }
    @EventHandler
    void onBlockBreak(BlockBreakEvent e)
    {
        if(players.contains(e.getPlayer())&&e.getBlock().getType()==Material.SNOW_BLOCK)
        {
            e.getPlayer().getInventory().addItem(new ItemStack(Material.SNOWBALL,2));
        }
        e.setDropItems(false);
    }
    @EventHandler
    void onPlayerMove(PlayerMoveEvent e){
        if (!inGame)
            return;
        Player player = e.getPlayer();
        if (players.contains(player)) {
            Location location = player.getLocation();
            if (!location.getWorld().getName().equals(world.getName()) ||
                    location.getBlockX() < x ||
                    location.getBlockZ() < z ||
                    location.getBlockX() > x + 63 ||
                    location.getBlockZ() > z + 63 ||
                    location.getBlockY() > y + 10 ||
                    location.getBlockY() < y ) {
                player.sendTitle("失败", "你的游戏结束了", 0, 100, 0);
                addToSpectator(player);

                rankList.addFirst(player);
                rankList.setHasWinner(true);
            }
        } else if (spectators.contains(player)) {
            Location location = player.getLocation();
            if (location.getBlockY() > y + 63) {
                location.setY(y+62);
                player.teleport(location);
            }
            else if (location.getBlockY() < y) {
                location.setY(y+1);
                player.teleport(location);
            }
            else if (location.getBlockX() < x) {
                location.setX(x+1);
                player.teleport(location);
            }
            else if (location.getBlockX() > x + 63) {
                location.setX(x+62);
                player.teleport(location);
            }
            else if (location.getBlockZ() < z) {
                location.setZ(z+1);
                player.teleport(location);
            }
            else if (location.getBlockZ() > z + 63) {
                location.setZ(z+62);
                player.teleport(location);
            }

        }
    }

    private void addToSpectator(Player player) {
        spectators.add(player);
        players.remove(player);
        player.setGameMode(GameMode.SPECTATOR);
    }

    @Override
    public void init(List<Player> players) {
        this.x = Utils.getConfig().getInt("Spleef.x");
        this.y = Utils.getConfig().getInt("Spleef.y");
        this.z = Utils.getConfig().getInt("Spleef.z");
        String worldName = Utils.getConfig().getString("Spleef.world");
        this.world = Utils.getServer().getWorld(worldName);
        for(int i=0;i<64;i++)
        {
            for(int j=0;j<64;j++)
            {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.SNOW_BLOCK);
            }
        }
        this.players = new ArrayList<>(players);
        for(Player player:players)
        {
            player.getInventory().clear();
            ItemStack diamondShovel = new ItemStack(Material.DIAMOND_SHOVEL);
            ItemMeta diamondShovelItemMeta = diamondShovel.getItemMeta();
            diamondShovel.addEnchantment(Enchantment.DIG_SPEED,5);

            player.getInventory().addItem(diamondShovel);
            player.teleport(getSafeLanding());
            player.setGameMode(GameMode.SURVIVAL);
        }
    }
    Location getSafeLanding()
    {
        return new Location(world,x+r.nextInt(64),y+3,z+r.nextInt(64));
    }

    @Override
    public void start() {
        inGame = true;
        new BukkitRunnable(){
            @Override
            public void run() {
                if(inGame)
                {
                    inGame = false;
                    for(Player player:spectators)
                    {
                        if(player.isOnline())
                            player.setGameMode(GameMode.SURVIVAL);
                        player.getInventory().clear();
                    }
                    Utils.getPluginManager().callEvent(new MinigameFinishEvent(Spleef.class,rankList));
                }

            }
        }.runTaskLater(Utils.getPlugin(),1200l);
    }

    @Override
    public void setup(World world, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world=world;
        Utils.getConfig().set("Spleef.hasGenerated", true);
        Utils.getConfig().set("Spleef.world", world.getName());
        Utils.getConfig().set("Spleef.x", x);
        Utils.getConfig().set("Spleef.y", y);
        Utils.getConfig().set("Spleef.z", z);
        Utils.saveConfig();

        for(int i=0;i<64;i++)
        {
            for(int j=0;j<64;j++)
            {
                Block block = world.getBlockAt(x + i, y, z + j);
                block.setType(Material.SNOW_BLOCK);
            }
        }

    }

    @Override
    public void setup(Location l) {
        setup(l.getWorld(),l.getBlockX(),l.getBlockY(),l.getBlockZ());
    }
}
