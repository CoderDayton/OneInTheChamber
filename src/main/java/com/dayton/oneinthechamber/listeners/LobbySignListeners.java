package com.dayton.oneinthechamber.listeners;

import com.dayton.oneinthechamber.OITC;
import com.dayton.oneinthechamber.core.Arena;
import com.dayton.oneinthechamber.core.LobbySign;
import com.dayton.oneinthechamber.events.ArenaStateChangeEvent;
import com.dayton.oneinthechamber.events.PlayerJoinArenaEvent;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.World;

public class LobbySignListeners implements Listener {

    @EventHandler
    public void createArenaStateSign(SignChangeEvent e) {
        String line1 = e.getLine(0);
        String line2 = e.getLine(1);
        Player p = e.getPlayer();
        if (p.hasPermission("oitc.createsigns")) {
            if (line1.equalsIgnoreCase("oitc")) {
                World w = p.getWorld();
                double x = e.getBlock().getLocation().getX();
                double y = e.getBlock().getLocation().getY();
                double z = e.getBlock().getLocation().getZ();
                Location loc = new Location(w, x, y, z);
                Arena arena = Arena.getArena(line2);
                if (arena == null) {
                    e.getPlayer().sendMessage(OITC.prefix + "§cThat arena doesn't exist.");
                    e.getBlock().breakNaturally();
                    return;
                }
                LobbySign.addSign(loc, arena);
                LobbySign ls = LobbySign.getSign(loc);
                ls.update();
            }
            return;
        }
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent e) {
        if (e.getBlock().getState() instanceof Sign) {
            if (LobbySign.getSign(e.getBlock().getLocation()) != null) {
                LobbySign sign = LobbySign.getSign(e.getBlock().getLocation());
                LobbySign.deleteSign(sign);
            }
        }
    }

    @EventHandler
    public void onArenaJoin(PlayerJoinArenaEvent e) {
        LobbySign.getSign(e.getArena()).update();
    }

    @EventHandler
    public void onArenaJoin(ArenaStateChangeEvent e) {
        LobbySign.getSign(e.getArena()).update();
    }

}
