package com.dayton.oneinthechamber.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.dayton.oneinthechamber.OITC;

public class Config {

	public static List<Config> configs = new ArrayList<>();

	public static Config getConfig(String name) {
		for (Config config : configs) {
			if (config.getName().replace(".yml", "").equalsIgnoreCase(name)) {
				return config;
			}
		}
		return null;
	}

	private final String fileName;
	private final Plugin plugin;
	private File configFile;
	private FileConfiguration fileConfiguration;

	public Config(String fileName) {
		this.plugin = OITC.plugin;
		if (plugin == null) {
			throw new IllegalArgumentException("plugin cannot be null");
		}
		this.fileName = fileName;
		File dataFolder = plugin.getDataFolder();
		if (dataFolder == null) {
			throw new IllegalStateException();
		}
		this.configFile = new File(plugin.getDataFolder(), fileName);
		configs.add(this);
	}

	public void reload() {
		this.fileConfiguration = YamlConfiguration.loadConfiguration(this.configFile);

		InputStream defConfigStream = this.plugin.getResource(this.fileName);
		if (defConfigStream != null) {
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.fileConfiguration.setDefaults(defConfig);
		}
	}

	public FileConfiguration get() {
		if (this.fileConfiguration == null) {
			reload();
		}
		return this.fileConfiguration;
	}

	public void save() {
		if ((this.fileConfiguration == null) || (this.configFile == null)) {
			return;
		}
		try {
			get().save(this.configFile);
		} catch (IOException ex) {
			this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, ex);
		}
	}

	public void saveDefaultConfig() {
		if (!this.configFile.exists()) {
			this.plugin.saveResource(this.fileName, false);
		}
	}

	public String getName() {
		return fileName;
	}
}
