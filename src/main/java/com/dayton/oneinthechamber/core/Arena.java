package com.dayton.oneinthechamber.core;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.dayton.oneinthechamber.events.ArenaStateChangeEvent;
import com.dayton.oneinthechamber.events.PlayerJoinArenaEvent;
import com.dayton.oneinthechamber.events.PlayerLeaveArenaEvent;
import com.dayton.oneinthechamber.tasks.StartCountdown;
import com.dayton.oneinthechamber.utils.Config;
import com.dayton.oneinthechamber.utils.Message;
import com.dayton.oneinthechamber.utils.OrderedMap;

public class Arena {

	private static List<Arena> arenas = new ArrayList<>();

	private String name;
	private ArenaMap map;
	private ArenaState state;
	private Map<String, Integer> players;
	private Map<String, Integer> playerLives;
	private int maxScore;
	private int minPlayers, maxPlayers, killsToWin;
	private Spectate spectate;

	private Arena(String name, ConfigurationSection section) {
		this.name = name;
		this.state = ArenaState.WAITING;
		this.players = new HashMap<>();
		this.playerLives = new HashMap<>();
		this.spectate = new Spectate(this);
		for (String s : section.getKeys(false)) {
			if (s.equals("Map")) {
				this.map = ArenaMap.getMap(section.getString("Map"));
			}
			if (s.equals("MaxScore")) {
				this.maxScore = section.getInt("MaxScore");
			}
			if (s.equals("MinPlayers")) {
				this.minPlayers = section.getInt("MinPlayers");
			}
			if (s.equals("MaxPlayers")) {
				this.maxPlayers = section.getInt("MaxPlayers");
			}
		}
	}

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

	public static void addArena(String name, ArenaMap map) {
		FileConfiguration config = Config.getConfig("Arenas").get();
		config.set("Arenas." + name + ".Map", map.getName());
		config.set("Arenas." + name + ".MaxScore", 10);
		config.set("Arenas." + name + ".MinPlayers", 2);
		config.set("Arenas." + name + ".MaxPlayers", 8);
		Config.getConfig("Arenas").save();
		loadArenas();
	}

	public static void loadArenas() {
		FileConfiguration config = Config.getConfig("Arenas").get();
		for (String arena : config.getConfigurationSection("Arenas").getKeys(false)) {
			Arena a = new Arena(arena, config.getConfigurationSection("Arenas." + arena));
			arenas.add(a);
		}
		System.out.println("[OITC] Loaded " + arenas.size() + " arena(s).");
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
		if (!(state == ArenaState.WAITING || state == ArenaState.IN_LOBBY)) {
			p.sendMessage("§cGame already started.");
			return;
		}
		if (!hasPlayer(p)) {
			if (state != ArenaState.IN_LOBBY) {
				state = ArenaState.IN_LOBBY;
				Bukkit.getPluginManager().callEvent(new ArenaStateChangeEvent(this, ArenaState.WAITING, ArenaState.IN_LOBBY));
			}
			players.put(p.getName(), 0);
			playerLives.put(p.getName(), 5);
			teleportToLobby(p);
			Bukkit.getPluginManager().callEvent(new PlayerJoinArenaEvent(p, this));
			if (players.size() >= minPlayers) {
				state = ArenaState.COUNTDOWN;
				Bukkit.getPluginManager().callEvent(new ArenaStateChangeEvent(this, ArenaState.IN_LOBBY, ArenaState.WAITING));
				startCountdown();
			}
		} else {
			p.sendMessage("§cAlready is this game.");
		}
	}

	private void removePlayer(Player p) {
		if (hasPlayer(p)) {
			players.remove(p.getName());
			playerLives.remove(p.getName());
			p.performCommand("spawn");
			spectate.removeSpectator(p);
			Bukkit.getPluginManager().callEvent(new PlayerLeaveArenaEvent(p, this));
		}
	}

	public void messageAll(String message, AbstractMap.SimpleEntry<String, Object>... placeholders) {
		for (String s : players.keySet()) {
			Message.sendMessage(Bukkit.getPlayer(s), message, placeholders);
		}
	}

	public void start() {
		state = ArenaState.IN_GAME;
		Bukkit.getPluginManager().callEvent(new ArenaStateChangeEvent(this, ArenaState.IN_LOBBY, ArenaState.IN_GAME));
		for (String s : players.keySet()) {
			Player p = Bukkit.getPlayer(s);
			giveItems(p);
			respawn(p);
			p.setVelocity(new Vector(0, 0, 0));
		}
	}

	public void end(boolean forced) {
        state = ArenaState.WAITING;
        Bukkit.getPluginManager().callEvent(new ArenaStateChangeEvent(this, ArenaState.IN_GAME, ArenaState.WAITING));
		if (!forced) {
			Iterator<String> names = getTopPlayers().iterator();
			Iterator<Integer> kills = getTopScores().iterator();
			messageAll("arena-ended", Message.makePlaceholder("winner", names.next()),
					Message.makePlaceholder("top1", names.next()),
					Message.makePlaceholder("top2", names.next()),
					Message.makePlaceholder("top3", names.next()), Message.makePlaceholder("score1", kills.next()),
					Message.makePlaceholder("score2", kills.next()), Message.makePlaceholder("score3", kills.next()));
		}
        for (String s : players.keySet()) {
            Player p = Bukkit.getPlayer(s);
            removePlayer(p);
            spectate.removeSpectator(p);
        }
        spectate.getSpectators().clear();
        players.clear();
        playerLives.clear();
    }

	private void startCountdown() {
		new StartCountdown(this, 10);
	}

	private void teleportToLobby(Player p) {
		p.teleport(map.getLobbySpawn());
	}

	public void respawn(Player p) {
		int x = new Random().nextInt(map.getLocations().size());
		p.teleport(map.getLocations().get(x));
	}

	public Location randomRespawn() {
		int x = new Random().nextInt(map.getLocations().size());
		return map.getLocations().get(x);
	}

	public void giveItems(Player p) {
		p.getInventory().clear();
		p.getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD));
		p.getInventory().setItem(1, new ItemStack(Material.BOW));
		p.getInventory().setItem(7, new ItemStack(Material.REDSTONE, getPlayerLives(p)));
		p.getInventory().setItem(8, new ItemStack(Material.ARROW));
		p.updateInventory();
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

	private TreeMap<String, Integer> getScoresOrdered() {
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

	public Spectate getSpectate() {
		return spectate;
	}

	public enum ArenaState {
		WAITING, IN_LOBBY, COUNTDOWN, IN_GAME
	}
}
