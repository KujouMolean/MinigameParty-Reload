package com.molean.MinigamePartyReload.events;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MinigameSetupEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    Location location;
    Class minigame;
    public MinigameSetupEvent(Location location,Class minigame)
    {
        this.location = location;
        this.minigame = minigame;
    }
    public Location getLocation()
    {
        return location;
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
}
