package com.molean.MinigamePartyReload.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract public class MinigameFinishEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
    public MinigameFinishEvent(boolean isAsync) {
        super(isAsync);
    }

}
