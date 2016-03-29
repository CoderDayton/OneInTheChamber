package com.dayton.oneinthechamber.core;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.dayton.oneinthechamber.events.PlayerJoinArenaEvent;
import com.dayton.oneinthechamber.tasks.StartCountdown;
import com.dayton.oneinthechamber.utils.Config;
import com.dayton.oneinthechamber.utils.OrderedMap;

public class Arena {

    public enum ArenaState {
        WAITING, IN_LOBBY, COUNTDOWN, IN_GAME;
    }

    public static List<Arena> arenas = new ArrayList<>();

    public static List<Arena> getArenas() {
        return arenas;
    }

    public static Arena getArena(Player p) {
        for (Arena arena : arenas) {
            for (String s : arena.getPlayers().keySet()) {
                if (s.equals(p.getName())) {
                    return arena;
                }
            }
        }
        return null;
    }

    public static Arena getArena(String name) {
        for (Arena arena : arenas) {
            if (arena.getName().equals(name)) {
                return arena;
            }
        }
        return null;
    }

    public static boolean inArena(Player p) {
        for (Arena arena : arenas) {
            if (arena.hasPlayer(p)) {
                return true;
            }
        }
        return false;
    }

    public static void loadArenas() {
        FileConfiguration config = Config.getConfig("Arenas").get();
        for (String arena : config.getConfigurationSection("Arenas").getKeys(false)) {
            Arena a = new Arena(arena, config.getConfigurationSection("Arenas." + arena));
            arenas.add(a);
        }
        System.out.println("[OITC] Loaded " + arenas.size() + " arena(s).");
    }

    private String name;
    private ArenaMap map;
    private ArenaState state;
    private List<Location> spawnLocations;
    private Map<String, Integer> players;
    private Map<String, Integer> playerLives;
    private int maxScore;
    private int minPlayers, maxPlayers, killsToWin;

    public Arena(String name, ConfigurationSection section) {
        this.name = name;
        this.state = ArenaState.WAITING;
        this.spawnLocations = new ArrayList<>();
        this.players = new HashMap<>();
        this.playerLives = new HashMap<>();
        for (String s : section.getKeys(false)) {
            if (s.equals("Map")) {
                this.map = ArenaMap.getMap(section.getString("Map"));
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
            if (s.equals("KillsToWin")) {
                this.maxScore = section.getInt("KillsToWin");
            }
        }
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
            playerLives.put(p.getName(), 5);
            p.setWalkSpeed(0);
            respawn(p);
            Bukkit.getPluginManager().callEvent(new PlayerJoinArenaEvent(p, this));
            if (state != ArenaState.IN_LOBBY) {
                state = ArenaState.IN_LOBBY;
            }
        } else {
            p.sendMessage("§cAlready is this game.");
        }
    }

    public void removePlayer(Player p) {
        if (hasPlayer(p)) {
            players.remove(p.getName());
            playerLives.remove(p.getName());
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

    public void end() {
        state = ArenaState.WAITING;
        players.clear();
        playerLives.clear();
    }

    public void startCountdown() {
        new StartCountdown(this, 10);
    }

    public void respawn(Player p) {
        int x = new Random().nextInt(spawnLocations.size());
        p.teleport(spawnLocations.get(x));
    }

    public void giveItems(Player p) {
        p.getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD));
        p.getInventory().setItem(1, new ItemStack(Material.BOW));
        p.getInventory().setItem(7, new ItemStack(Material.REDSTONE, getPlayerLives(p)));
        p.getInventory().setItem(8, new ItemStack(Material.ARROW));
    }

    public int getPlayerLives(Player p) {
        return playerLives.get(p.getName());
    }

    public void takeLife(Player p) {
        playerLives.put(p.getName(), playerLives.get(p.getName()) - 1);
    }

    public void addKill(Player p) {
        players.put(p.getName(), players.get(p.getName()) + 1);
    }

    public TreeMap<String, Integer> getScoresOrdered() {
        OrderedMap orderedMap = new OrderedMap(players);
        TreeMap<String, Integer> treeMap = new TreeMap<>(orderedMap);
        treeMap.putAll(players);
        return treeMap;
    }

    public Set<String> getTopPlayers() {
        return getScoresOrdered().keySet();
    }

    public Collection<Integer> getTopScores() {
        return getScoresOrdered().values();
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

    public int getKillsToWin() {
        return killsToWin;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public ArenaMap getMap() {
        return map;
    }
}
