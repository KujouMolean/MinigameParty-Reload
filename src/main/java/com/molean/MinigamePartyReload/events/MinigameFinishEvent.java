package com.molean.MinigamePartyReload.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MinigameFinishEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    Class minigame;

    public MinigameFinishEvent(Class minigame,boolean isAsync) {
        super(isAsync);
        this.minigame = minigame;

    }

    public Class getMinigame() {
        return minigame;
    }

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
