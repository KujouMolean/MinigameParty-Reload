package com.molean.MinigamePartyReload.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RoundStartEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlerList;

    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
