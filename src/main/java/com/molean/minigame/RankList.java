package com.molean.minigame;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class RankList {
    private Boolean hasWinner = false;
    List<String> players = new LinkedList<>();

    public void addFirst(Player player) {
        if (!players.contains(player.getName()))
            players.add(0, player.getName());
    }

    public void addEnd(Player player) {
        if (!players.contains(player.getName()))
            players.add(player.getName());
    }

    public Boolean hasWinner() {
        return hasWinner;
    }

    public void setHasWinner(Boolean hasWinner) {
        this.hasWinner = hasWinner;
    }

    @Override
    public String toString() {
        return players.toString();
    }
}
