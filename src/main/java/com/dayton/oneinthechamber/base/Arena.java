package com.dayton.oneinthechamber.base;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arena {

    public static List<Arena> arenas = new ArrayList<>();

    public static List<Arena> getArenas() {
        return arenas;
    }

    private String name;
    private List<Location> spawnLocations;
    private Map<String, Integer> players;

    public Arena(String name, ConfigurationSection section) {
        this.name = name;
        this.spawnLocations = new ArrayList<>();
        this.players = new HashMap<>();
        for (String s : section.getKeys(false)) {
            if (s.equals("Locations")) {
                for (int i = 0; i < section.getConfigurationSection("Locations").getKeys(false).size(); i++) {
                    World w = Bukkit.getWorld(section.getString("Locations." + i + ".World"));
                    double x = section.getDouble("Locations." + i + ".x");
                    double y = section.getDouble("Locations." + i + ".y");
                    double z = section.getDouble("Locations." + i + ".z");
                    float yaw = (float) section.getDouble("Locations." + i + ".yaw");
                    float pitch = (float) section.getDouble("Locations." + i + ".pitch");
                    Location loc = new Location(w, x, y, z, yaw, pitch);
                    spawnLocations.add(loc);
                }
            }
        }
        arenas.add(this);
    }

    public boolean hasPlayer(Player p) {
        for (String s : players.keySet()) {
            if (s.equals(p.getName())) {
                return true;
            }
        }
        return false;
    }

    public void addPlayer(Player p) {
        if (!hasPlayer(p)) {

        }
    }

    public String getName() {
        return name;
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public Map<String, Integer> getPlayers() {
        return players;
    }
}
