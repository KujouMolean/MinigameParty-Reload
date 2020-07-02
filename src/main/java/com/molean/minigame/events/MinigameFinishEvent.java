package com.molean.minigame.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MinigameFinishEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final String minigame;

    public MinigameFinishEvent(String minigame) {
        this.minigame = minigame;
    }

    public String getMinigame() {
        return minigame;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

}
