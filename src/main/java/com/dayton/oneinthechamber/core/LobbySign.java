package com.dayton.oneinthechamber.core;

import com.dayton.oneinthechamber.OITC;
import com.dayton.oneinthechamber.utils.Config;
import org.bukkit.Location;
import org.bukkit.World;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LobbySign {

    private static List<LobbySign> signs = new ArrayList<>();

    public static void loadSigns() {
        signs.clear();
        FileConfiguration config = Config.getConfig("Signs").get();
        for (int i = 0; i < config.getConfigurationSection("Signs").getKeys(false).size(); i++) {
            ConfigurationSection section = config.getConfigurationSection("Signs." + i);
            World w = Bukkit.getWorld(section.getString("world"));
            Arena arena = Arena.getArena(section.getString("arena"));
            double x = section.getDouble("x");
            double y = section.getDouble("y");
            double z = section.getDouble("z");
            Location loc = new Location(w, x, y, z);
            if (!(loc.getBlock().getState() instanceof Sign)) {
                config.set("Signs." + i, null);
                Config.getConfig("Signs").save();
                continue;
            }
            Sign sign = (Sign) loc.getBlock().getState();
            if (arena == null) {
                sign.getBlock().breakNaturally();
                config.set("Signs." + i, null);
                Config.getConfig("Signs").save();
                continue;
            }
            LobbySign ls = new LobbySign(sign, arena, i);
            ls.update();
            signs.add(ls);
        }
        System.out.println("[OITC] Loaded " + signs.size() + " signs.");
    }

    public static LobbySign getSign(Location loc) {
        for (LobbySign sign : signs) {
            if (sign.getSign().getLocation().getX() == loc.getX() && sign.getSign().getLocation().getY()
                    == loc.getY() && sign.getSign().getLocation().getZ() == loc.getZ()) {
                return sign;
            }
        }
        return null;
    }

    public static LobbySign getSign(Arena arena) {
        for (LobbySign sign : signs) {
            if (sign.getArena().getName().equals(arena.getName())) {
                return sign;
            }
        }
        return null;
    }

    public static void addSign(Location loc, Arena arena) {
        FileConfiguration config = Config.getConfig("Signs").get();
        int size =  config.getConfigurationSection("Signs").getKeys(false).size();
        config.set("Signs." + size + ".world", loc.getWorld().getName());
        config.set("Signs." + size + ".arena", arena.getName());
        config.set("Signs." + size + ".x", loc.getX());
        config.set("Signs." + size + ".y", loc.getY());
        config.set("Signs." + size + ".z", loc.getZ());
        Config.getConfig("Signs").save();
        loadSigns();
    }

    public static void deleteSign(LobbySign sign) {
        FileConfiguration config = Config.getConfig("Signs").get();
        config.set("Signs." + sign.getId(), null);
        Config.getConfig("Signs").save();
        loadSigns();
    }

    private int id;
    private Arena arena;
    private Sign sign;

    public LobbySign(Sign s, Arena arena, int id) {
        this.id = id;
        this.arena = arena;
        this.sign = s;
    }

    public void update() {
        this.sign.setLine(0, "§a" + arena.getMap().getName());
        this.sign.setLine(1, WordUtils.capitalizeFully(arena.getState().name().replace("_", " ")));
        this.sign.setLine(2, "Players: " + arena.getPlayers().size() + "/" + arena.getMaxPlayers());
        this.sign.setLine(3, "§4" + arena.getName());
        new BukkitRunnable() {
            public void run() {
                sign.update();
                sign.update(true);
            }
        }.runTaskLater(OITC.plugin, 2L);
    }

    public Sign getSign() {
        return sign;
    }

    public int getId() {
        return id;
    }

    public Arena getArena() {
        return arena;
    }

}
