package com.dayton.oneinthechamber.events;

import com.dayton.oneinthechamber.core.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinArenaEvent extends Event implements Cancellable {

    private Player player;
    private Arena arena;
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    public PlayerJoinArenaEvent(Player player, Arena arena) {
        this.player = player;
        this.arena = arena;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Arena getArena() {
        return arena;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
