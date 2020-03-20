package com.molean.MinigamePartyReload.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveMinigame extends Event {
    Player player;
    public PlayerLeaveMinigame(Player player)
    {
        this.player  = player;
    }
    public Player getPlayer()
    {
        return player;
    }
    private static final HandlerList handlerList = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlerList;

    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
