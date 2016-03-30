package com.dayton.oneinthechamber.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.dayton.oneinthechamber.core.Arena;

public class ArenaStateChangeEvent extends Event implements Cancellable {

    private Arena arena;
    private Arena.ArenaState oldState;
    private Arena.ArenaState newState;
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    public ArenaStateChangeEvent(Arena arena, Arena.ArenaState oldState, Arena.ArenaState newState) {
        this.arena = arena;
        this.oldState = oldState;
        this.newState = newState;
        this.cancelled = false;
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
