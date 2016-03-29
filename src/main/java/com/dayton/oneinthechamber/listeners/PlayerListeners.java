package com.dayton.oneinthechamber.listeners;

import com.dayton.oneinthechamber.core.Arena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListeners implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (Arena.inArena(p)) {
            Arena arena = Arena.getArena(p);
            if (arena.getState() == Arena.ArenaState.COUNTDOWN) {
                if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY()
                        != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
                    e.setTo(e.getFrom());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (Arena.inArena(p)) {
            Arena arena = Arena.getArena(p);
            arena.takeLife(p);
            if (p.getKiller() != null) {
                Player killer = p.getKiller();
                int amount = killer.getInventory().getItem(8) != null ? killer.getInventory().getItem(8).getAmount() : 1;
                killer.getInventory().setItem(8, new ItemStack(Material.ARROW, amount));
                arena.addKill(killer);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (Arena.inArena(p)) {
            Arena arena = Arena.getArena(p);
            arena.respawn(p);
        }
    }

    @EventHandler
    public void onPlayerHitByArrow(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player hit = (Player) e.getEntity();
            if (e.getDamager() != null && e.getDamager() instanceof Player) {
                Player damager = (Player) e.getDamager();
                if (Arena.inArena(hit)) {
                    hit.setHealth(0);
                }
            }
        }
    }

}
