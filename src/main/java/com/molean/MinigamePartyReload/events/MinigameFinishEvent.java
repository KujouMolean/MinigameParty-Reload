package com.molean.MinigamePartyReload.events;

import com.molean.MinigamePartyReload.RankList;
import com.molean.MinigamePartyReload.Utils;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MinigameFinishEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    RankList rankList;
    Class minigame;

    public MinigameFinishEvent(Class minigame, RankList rankList, boolean isAsync) {
        super(isAsync);
        this.minigame = minigame;
        this.rankList=rankList;
        Utils.info("rank:" + rankList.toString());

    }
    public MinigameFinishEvent(Class minigame, RankList rankList) {
        this(minigame,rankList,false);

    }
    public RankList getRankList()
    {
        return rankList;
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
