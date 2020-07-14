package com.molean.minigame;

import com.molean.minigame.events.MinigameFinishEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

import static com.molean.minigame.Utils.*;

public abstract class Minigame implements Listener {
    private final String name;
    private final Map<Player, Location> playerLocationMap = new HashMap<>();
    private final Map<Player, ItemStack[]> playerInventory = new HashMap<>();


    public String getName() {
        return name;
    }

    public enum MiniGameMode {gaming, spectator}

    protected Map<Player, MiniGameMode> players = new HashMap<>();
    protected RankList rankList = new RankList();


    protected int x, y, z;
    protected World world;
    protected boolean inGame;

    public boolean isInGame() {
        return inGame;
    }

    public Minigame(String name) {
        this.name = name;
    }

    public void setup(World world, int x, int y, int z) {
        ConfigUtils.getConfig("config.yml").set(name + ".hasGenerated", true);
        ConfigUtils.getConfig("config.yml").set(name + ".x", x);
        ConfigUtils.getConfig("config.yml").set(name + ".y", y);
        ConfigUtils.getConfig("config.yml").set(name + ".z", z);
        ConfigUtils.getConfig("config.yml").set(name + ".world", world.getName());
        ConfigUtils.saveConfig("config.yml");
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void init(List<Player> players) {
        Utils.registerEvents(this);
        if (!ConfigUtils.getConfig("config.yml").getBoolean(name + ".hasGenerated")) {
            return;
        }
        x = ConfigUtils.getConfig("config.yml").getInt(name + ".x");
        y = ConfigUtils.getConfig("config.yml").getInt(name + ".y");
        z = ConfigUtils.getConfig("config.yml").getInt(name + ".z");
        String worldName = ConfigUtils.getConfig("config.yml").getString(name + ".world");
        world = Utils.getServer().getWorld(Objects.requireNonNull(worldName));

        for (Player player : players) {
            playerLocationMap.put(player, player.getLocation());
            playerInventory.put(player, player.getInventory().getContents());
            Utils.info(player.getName() + " inventory: " + Arrays.toString(player.getInventory().getContents()));
            player.getInventory().clear();
            setMiniGameMode(player, MiniGameMode.gaming);
        }
    }

    public void start() {
        inGame = true;
    }

    public void setSoreborad(String title, Map<String, Integer> map) {
        runTask(() -> {
            Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = newScoreboard.registerNewObjective(name, "dummy", title);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            map.forEach((entry, value) -> {
                Score score = objective.getScore(entry);
                score.setScore(value);
            });
            for (Player player : players.keySet()) {
                player.setScoreboard(newScoreboard);
            }
        });

    }

    public abstract Location getSafeLanding();

    public void setMiniGameMode(Player player, MiniGameMode miniGameMode) {
        players.put(player, miniGameMode);
        if (miniGameMode.equals(MiniGameMode.spectator)) {
            player.setGameMode(GameMode.SPECTATOR);
        }
        if (miniGameMode.equals(MiniGameMode.gaming)) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
            player.setFoodLevel(20);
        }
        player.teleport(getSafeLanding());
    }

    public void stop() {
        inGame = false;
        Utils.runTask(() -> {
            List<Player> tobeRemove = new ArrayList<>();
            players.forEach((player, miniGameMode) -> {
                tobeRemove.add(player);
            });
            tobeRemove.forEach(this::quit);
            HandlerList.unregisterAll(this);
            Bukkit.getPluginManager().callEvent(new MinigameFinishEvent(name));
            Bukkit.broadcastMessage(rankList.toString());
        });
    }

    public void quit(Player player) {
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(playerLocationMap.get(player));
        players.remove(player);
        player.getInventory().setContents(playerInventory.get(player));
        Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        player.setScoreboard(newScoreboard);
    }

    public List<Player> getPlayerList() {
        List<Player> playerList = new ArrayList<Player>();
        players.forEach((player, miniGameMode) -> {
            if (miniGameMode.equals(MiniGameMode.gaming))
                playerList.add(player);
        });
        return playerList;
    }

    private Location pos1, pos2;

    public void setRestrict(Location pos1, Location pos2) {
        if (!pos1.getWorld().equals(pos2.getWorld())) {
            throw new RuntimeException();
        }
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.pos1 = new Location(pos1.getWorld(), minX, minY, minZ);
        this.pos2 = new Location(pos1.getWorld(), maxX, maxY, maxZ);
    }

    public boolean canBuild(Player player, Location location) {
        return false;
    }

    public boolean canRebornIfOut(Player player) {
        return false;
    }

    public boolean canCommand(Player player, String command) {
        return false;
    }

    public boolean canDamage(Player player, EntityDamageEvent.DamageCause damageCause) {
        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (pos1 == null || pos2 == null)
            return;
        Player player = e.getPlayer();
        if (players.containsKey(player)) {
            if (!inBox(pos1, pos2, player.getLocation())) {
                if (players.get(player).equals(MiniGameMode.gaming)) {
                    if (canRebornIfOut(player)) {
                        player.teleport(getSafeLanding());
                    } else {
                        setMiniGameMode(player, MiniGameMode.spectator);
                        rankList.addFirst(player);
                    }
                } else if (players.get(player).equals(MiniGameMode.spectator)) {
                    Location location = Utils.recentInBox(pos1, pos2, player.getLocation());
                    player.teleport(location);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (players.containsKey(event.getPlayer())) {
            if (!canBuild(event.getPlayer(), event.getBlock().getLocation()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if (players.containsKey(event.getPlayer())) {
            if (!canBuild(event.getPlayer(), event.getBlock().getLocation()))
                event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (players.containsKey(e.getPlayer())) {
            if (!canCommand(e.getPlayer(), e.getMessage()))
                e.setCancelled(true);
            if (e.getMessage().equalsIgnoreCase("/q"))
                e.getPlayer().sendMessage(getMessage("General.Quit"));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (players.containsKey(event.getPlayer())) {
            quit(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        if (players.containsKey(event.getEntity())) {
            if (!canRebornIfOut(event.getEntity())) {
                setMiniGameMode(event.getEntity(), MiniGameMode.spectator);
                rankList.addFirst(event.getEntity());
            }

        }
    }

    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event){
        if(!(event.getEntity() instanceof  Player))
            return;
        Player player = (Player) event.getEntity();
        if(!players.containsKey(player))
            return;
        if(!canDamage(player, event.getCause()))
            event.setCancelled(true);
    }
}
