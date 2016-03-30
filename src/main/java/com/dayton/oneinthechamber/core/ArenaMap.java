package com.dayton.oneinthechamber.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.dayton.oneinthechamber.utils.Config;

public class ArenaMap {

	private static List<ArenaMap> maps = new ArrayList<>();
	private String name;
	private World world;
	private List<Location> locations;

	public ArenaMap(String name, World world, List<Location> locations) {
		this.name = name;
		this.world = world;
		this.locations = locations;
	}

	public static void addMap(String name, World world) {
		FileConfiguration maps = Config.getConfig("Maps").get();
		maps.set("Maps." + name + ".World", world.getName());
		maps.set("Maps." + name + ".Locations", new ArrayList<>());
		Config.getConfig("Maps").save();
		loadMaps();
	}

	public static void deleteMap(String name) {
		FileConfiguration maps = Config.getConfig("Maps").get();
		maps.set("Maps." + name, null);
		Config.getConfig("Maps").save();
		loadMaps();
	}

	public static void loadMaps() {
		maps.clear();
		FileConfiguration config = Config.getConfig("Maps").get();
		for (String map : config.getConfigurationSection("Maps").getKeys(false)) {
			World world = Bukkit.getWorld(config.getString("Maps." + map + ".World"));
			List<Location> locations = new ArrayList<>();
			int amt = config.getConfigurationSection("Maps." + map + ".Locations") == null ? 0 : config.getConfigurationSection("Maps." + map + ".Locations").getKeys(false).size();
			for (int i = 0; i < amt; i++) {
				String key = "Maps." + map + ".Locations." + i;
				double x = config.getDouble(key + ".x");
				double y = config.getDouble(key + ".y");
				double z = config.getDouble(key + ".z");
				double yaw = config.getDouble(key + ".yaw");
				double pitch = config.getDouble(key + ".pitch");
				Location loc = new Location(world, x, y, z, (float) yaw, (float) pitch);
				locations.add(loc);
			}
			ArenaMap m = new ArenaMap(map, world, locations);
			maps.add(m);
		}
		System.out.println("[OITC] Loaded " + maps.size() + " map(s).");
	}

	public static ArenaMap getMap(String name) {
		for (ArenaMap map : maps) {
			if (map.getName().equals(name)) {
				return map;
			}
		}
		return null;
	}

	public void saveLocations() {
		FileConfiguration config = Config.getConfig("Maps").get();
		for (int i = 0; i < locations.size(); i++) {
			Location loc = locations.get(i);
			String key = "Maps." + name + ".Locations." + i;
			config.set(key + ".x", loc.getX());
			config.set(key + ".y", loc.getY());
			config.set(key + ".z", loc.getZ());
			config.set(key + ".yaw", loc.getYaw());
			config.set(key + ".pitch", loc.getPitch());
		}
		Config.getConfig("Maps").save();
	}

	public void addLocation(Location loc) {
		for (Location l : locations) {
			if (l.getX() == loc.getX() && l.getY() == loc.getY() && l.getZ() == loc.getZ()) {
				return;
			}
		}
		locations.add(loc);
		saveLocations();
		loadMaps();
	}

	public String getName() {
		return name;
	}

	public World getWorld() {
		return world;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}
}
