package com.molean.MinigamePartyReload;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class RankList {
    Boolean hasWinner;
    List<String> players = new LinkedList<>();
    public void addFirst(Player player)
    {
        players.add(0,player.getName());
    }
    public void addEnd(Player player)
    {
        players.add(player.getName());
    }

    public Boolean getHasWinner() {
        return hasWinner;
    }
    public void setHasWinner(Boolean hasWinner) {
        this.hasWinner = hasWinner;
    }
    public void clear()
    {
        players.clear();
        hasWinner=false;
    }

    @Override
    public String toString() {
        return players.toString();
    }
}
