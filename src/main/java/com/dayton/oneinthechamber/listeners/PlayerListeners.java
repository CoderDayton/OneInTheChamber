package com.dayton.oneinthechamber.listeners;

import com.dayton.oneinthechamber.utils.Message;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.dayton.oneinthechamber.OITC;
import com.dayton.oneinthechamber.core.Arena;
import com.dayton.oneinthechamber.core.LobbySign;

public class PlayerListeners implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        final Player p = e.getEntity();
        if (Arena.inArena(p)) {
            final Arena arena = Arena.getArena(p);
            arena.takeLife(p);
            e.getDrops().clear();
            e.setDroppedExp(0);
            p.updateInventory();
            e.setDeathMessage(null);
            if (arena.getPlayerLives(p) >= 1) {
                p.setHealth(p.getMaxHealth());
                p.spigot().respawn();
            } else {
                p.setHealth(p.getMaxHealth());
                arena.getSpectate().addSpectator(e.getEntity());
                e.getEntity().setAllowFlight(true);
                e.getEntity().setFlying(true);
                Message.sendMessage(p, "no-lives");
            }
            if (p.getKiller() != null) {
                Player killer = p.getKiller();
                int amount = killer.getInventory().getItem(8) != null ? killer.getInventory().getItem(8).getAmount() : 1;
                killer.getInventory().setItem(8, new ItemStack(Material.ARROW, amount));
                arena.addKill(killer);
                arena.messageAll("death-message", Message.makePlaceholder("player", p.getName()), Message.makePlaceholder("killer", killer.getName()));
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        if (Arena.inArena(p)) {
            final Arena arena = Arena.getArena(p);
            e.setRespawnLocation(arena.randomRespawn());
            new BukkitRunnable() {
                public void run() {
                    arena.giveItems(p);
                }
            }.runTaskLater(OITC.plugin, 2L);
        }
    }

    @EventHandler
    public void onPlayerHitByArrow(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player hit = (Player) e.getEntity();
            if (e.getDamager() != null && e.getDamager() instanceof Arrow) {
                if (Arena.inArena(hit)) {
                    hit.setHealth(0);
                    e.getDamager().remove();
                }
            }
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getState() instanceof Sign) {
                if (LobbySign.getSign(e.getClickedBlock().getLocation()) != null) {
                    Sign s = (Sign) e.getClickedBlock().getState();
                    Arena arena = Arena.getArena(ChatColor.stripColor(s.getLine(3)));
                    arena.addPlayer(e.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (Arena.inArena(p)) {
                e.setCancelled(true);
            }
        }
    }

}
