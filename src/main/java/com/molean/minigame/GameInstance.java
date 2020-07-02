package com.molean.minigame;

import com.molean.minigame.events.MinigameFinishEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class GameInstance {
    private Minigame minigame;
    private List<Player> playerList = new ArrayList<>();

    private enum GameStatus {WAIT, PRE_START, STARTED}

    private GameStatus gameStatus;

    private BukkitTask broadcastTask;
    private BukkitTask timeoutTask;
    private BukkitTask timeoutStartTask;
    private BukkitTask playerEnoughTask;

    public GameInstance(Minigame minigame, Player sponsor) {
        this.minigame = minigame;
        gameStatus = GameStatus.WAIT;
        playerList.add(sponsor);
        broadcastTask = Utils.runTaskTimer(
                () -> Bukkit.getServer().broadcastMessage(Utils.getMessage("General.GameWait"))
                , 0L, 300L);
        timeoutTask = Utils.runTaskLater(() -> {
            broadcastTask.cancel();
            if (playerList.size() >= 2) {
                Bukkit.getServer().broadcastMessage(Utils.getMessage("General.TimeOutPreStart"));
                gameStatus = GameStatus.STARTED;
                timeoutStartTask = Utils.runTaskLater(() -> {
                    minigame.init(playerList);
                    minigame.start();
                }, 100L);
            } else {
                Bukkit.getServer().broadcastMessage(Utils.getMessage("General.TimeOutStopGame"));
                Bukkit.getPluginManager().callEvent(new MinigameFinishEvent(minigame.getName()));
            }
        }, 1200L);
    }

    public void join(Player player) {
        if (gameStatus == GameStatus.STARTED) {
            player.sendMessage(Utils.getMessage("General.JoinStartedGame"));
            return;
        }
        if (!playerList.contains(player)) {
            if (playerList.size() >= 8) {
                player.sendMessage(Utils.getMessage("General.JoinFullGame"));
            }
            playerList.add(player);
            player.sendMessage(Utils.getMessage("General.Join"));
            if (playerList.size() >= 4) {
                Bukkit.getServer().broadcastMessage(Utils.getMessage("General.PlayerEnoughPreStart"));
                broadcastTask.cancel();
                timeoutTask.cancel();
                gameStatus = GameStatus.PRE_START;
                playerEnoughTask = Utils.runTaskLater(() -> {
                    gameStatus = GameStatus.STARTED;
                    minigame.init(playerList);
                    minigame.start();
                }, 200L);
            }
        } else {
            player.sendMessage(Utils.getMessage("General.JoinJoinedGame"));
        }
    }

    public void left(Player player) {
        if (!playerList.contains(player)) {
            player.sendMessage(Utils.getMessage("General.LeftNoGame"));
        }
        playerList.remove(player);
        player.sendMessage(Utils.getMessage("General.Left"));
    }

    public void stop() {
        if (broadcastTask != null) {
            broadcastTask.cancel();
        }
        if (timeoutTask != null) {
            timeoutTask.cancel();
        }
        if (timeoutStartTask != null) {
            timeoutStartTask.cancel();
        }
        if (playerEnoughTask != null) {
            playerEnoughTask.cancel();
        }
        minigame.inGame = false;
        playerList.clear();
    }

}
