package com.dayton.oneinthechamber.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.dayton.oneinthechamber.OITC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Spectate implements Listener {

	private List<String> spectators = new ArrayList<>();
	
	private Arena arena;
	
	public Spectate(Arena arena) {
		this.arena = arena;
		Bukkit.getServer().getPluginManager().registerEvents(this, OITC.plugin);
	}
	
	public void addSpectator(Player p) {
		spectators.add(p.getName());
		getHidden(p);
//		giveCompass(p);
	}
	
	public void removeSpectator(Player p) {
		spectators.remove(p.getName());
		getShown(p);
		p.getInventory().setItem(0, null);
        p.setAllowFlight(false);
        p.setFlying(false);
	}
	
	public boolean isSpectating(Player p) {
		return spectators.contains(p.getName());
	}
	
	private void giveCompass(Player p) {
		ItemStack item = new ItemStack(Material.COMPASS, 1);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§cPlayer Locator");
		item.setItemMeta(meta);
		
		p.getInventory().addItem(item);
		p.updateInventory();
	}

    private void getHidden(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15), true);
//		player.setCollidable(false);
	}

    private void getShown(Player player) {
		for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
//		player.setCollidable(true);
	}
	
	@EventHandler
	public void onCompassClick(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR)) {
			if (e.getPlayer().getItemInHand().getType() == Material.COMPASS) {
				List<String> pClone = new ArrayList<>();
				pClone.addAll(arena.getPlayers().keySet());
				
				ListIterator<String> it = pClone.listIterator();
				e.getPlayer().teleport(Bukkit.getPlayer(it.next()).getLocation());
			}
		}
	}
	
	public Arena getArena() {
		return arena;
	}

    public List<String> getSpectators() {
        return spectators;
    }
}
