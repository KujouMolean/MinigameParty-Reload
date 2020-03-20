package com.molean.MinigamePartyReload;

import com.mojang.datafixers.TypeRewriteRule;
import com.molean.MinigamePartyReload.Utils;
import com.molean.MinigamePartyReload.events.MinigameFinishEvent;
import com.molean.MinigamePartyReload.events.PlayerLeaveMinigame;
import com.molean.MinigamePartyReload.events.RoundStartEvent;
import com.molean.MinigamePartyReload.minigame.ColorMatch;
import com.molean.MinigamePartyReload.minigame.Minigame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinigameManager implements Listener {
    private List<Player> inGamePlayers = new ArrayList<>();
    private List<Player> waitPlayers = new ArrayList<>();
    private List<Minigame> minigames = new ArrayList<>();

    int nowGame;

    public void prepare() {
        minigames.add(new ColorMatch());

        //todo
        //add all mini game here.

        Collections.shuffle(minigames);

        for (Minigame minigame : minigames) {
            Utils.getServer().getPluginManager().registerEvents(minigame, Utils.getPlugin());
        }
        nowGame = 0;
    }

    public boolean isPlayerIngame(Player player) {
        return inGamePlayers.contains(player);
    }


    @EventHandler
    public void onRoundStart(RoundStartEvent e) {
        prepare();
        nextGame();
    }

    private void updatePlayer() {
        inGamePlayers.addAll(waitPlayers);
        waitPlayers.clear();
    }

    private void nextGame() {
        if (nowGame < minigames.size()) {
            updatePlayer();
            if (inGamePlayers.size() > 0) {
                List<Player> tempPlayers = new ArrayList<>();
                tempPlayers.addAll(inGamePlayers);
                minigames.get(nowGame).init(tempPlayers);
                minigames.get(nowGame).start();
            } else {
                Utils.broadcast("没有玩家在游戏中,游戏停止.");
            }
            nowGame++;
        } else {
            Utils.broadcast("游戏全部结束");
        }

    }

    public void addPlayer(Player player) {
        if (!waitPlayers.contains(player) && !inGamePlayers.contains(player))
            waitPlayers.add(player);
    }

    public void removePlayer(Player player) {
        waitPlayers.remove(player);
        inGamePlayers.remove(player);
    }

    @EventHandler
    public void onMinigameFinish(MinigameFinishEvent e) {
        nextGame();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Utils.getPluginManager().callEvent(new PlayerLeaveMinigame(e.getPlayer()));
    }
    @EventHandler
    public void onPlayerLeaveMinigame(PlayerLeaveMinigame e)
    {
        inGamePlayers.remove(e.getPlayer());
        waitPlayers.remove(e.getPlayer());
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent e) {

        if (inGamePlayers.contains(e.getPlayer())) {
            String s = e.getMessage();
            if (s.equalsIgnoreCase("/minigame left"))
            {
                Utils.getPluginManager().callEvent(new PlayerLeaveMinigame(e.getPlayer()));
                e.getPlayer().sendMessage("你已离开了迷你游戏派对.");
            }
            else e.setCancelled(true);
        }


    }


}
