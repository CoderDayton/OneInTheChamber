package com.dayton.oneinthechamber.listeners;

import com.dayton.oneinthechamber.core.Arena;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class EntityListeners implements Listener {

	@EventHandler
	public void onArrowHit(ProjectileHitEvent e) {
		if (e.getEntity() instanceof Arrow) {
			if (e.getEntity().getShooter() instanceof Player) {
                Player p = (Player) e.getEntity().getShooter();

                if (Arena.inArena(p)) {
                    e.getEntity().remove();
                }
			}
		}
	}

}
