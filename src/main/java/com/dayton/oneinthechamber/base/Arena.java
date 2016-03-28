package com.dayton.oneinthechamber.base;

import com.dayton.oneinthechamber.tasks.StartCountdown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class Arena {

    public enum ArenaState {
        WAITING, COUNTDOWN, IN_GAME;
    }

    public static List<Arena> arenas = new ArrayList<>();

    public static List<Arena> getArenas() {
        return arenas;
    }

    private String name;
    private ArenaState state;
    private List<Location> spawnLocations;
    private Map<String, Integer> players;
    private int maxScore;
    private int minPlayers, maxPlayers;

    public Arena(String name, ConfigurationSection section) {
        this.name = name;
        this.state = ArenaState.WAITING;
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
            if (s.equals("MaxScore")) {
                this.maxScore = section.getInt("MaxScore");
            }
            if (s.equals("MinPlayers")) {
                this.maxScore = section.getInt("MaxScore");
            }
            if (s.equals("MaxPlayers")) {
                this.maxScore = section.getInt("MaxScore");
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
        if (state != ArenaState.WAITING) {
            p.sendMessage("§cGame already started.");
            return;
        }
        if (!hasPlayer(p)) {
            players.put(p.getName(), 0);
            p.setWalkSpeed(0);
            respawn(p);
        } else {
            p.sendMessage("§cAlready is this game.");
        }
    }

    public void removePlayer(Player p) {
        if (hasPlayer(p)) {
            players.remove(p.getName());
            p.performCommand("spawn");
        }
    }

    public void messageAll(String message) {
        for (String s : players.keySet()) {
            Bukkit.getPlayer(s).sendMessage(message);
        }
    }

    public void start() {
        for (String s : players.keySet()) {
            Bukkit.getPlayer(s).setWalkSpeed(0.2f);
        }
    }

    public void startCountdown() {
        new StartCountdown(this, 10);
    }

    public void respawn(Player p) {
        int x = new Random().nextInt(spawnLocations.size());
        p.teleport(spawnLocations.get(x));
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

    public ArenaState getState() {
        return state;
    }
}
