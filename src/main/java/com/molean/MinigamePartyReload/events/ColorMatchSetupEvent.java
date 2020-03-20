package com.molean.MinigamePartyReload.events;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class ColorMatchSetupEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    Location location;
    public ColorMatchSetupEvent(Location location)
    {
        this.location = location;
    }
    public Location getLocation()
    {
        return location;
    }
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
