package com.dayton.oneinthechamber.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.dayton.oneinthechamber.OITC;

public class Message {

	@SafeVarargs
	@SuppressWarnings("deprecation")
	public static void sendMessage(CommandSender p, String str, SimpleEntry<String, Object>... placeHolders) {
		FileConfiguration messages = Config.getConfig("Messages").get();
		if (messages.getStringList(str) != null) {
			for (String s : messages.getStringList(str)) {
				s = ChatColor.translateAlternateColorCodes('&', s).replace("{prefix}", OITC.prefix);
				for (SimpleEntry<String, Object> places : placeHolders) {
					s = s.replace("{" + places.getKey() + "}", places.getValue().toString());
				}
				p.sendMessage(s);
			}
		} else {
			InputStream in = OITC.plugin.getResource("Messages.yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(in);
			if (config.getStringList(str) != null) {
				messages.set(str, config.getStringList(str));
				Config.getConfig("Messages").save();
				try {
					in.close();
				} catch (IOException e) {}
				for (String s : messages.getStringList(str)) {
					s = ChatColor.translateAlternateColorCodes('&', s).replace("{prefix}", OITC.prefix);
					for (SimpleEntry<String, Object> places : placeHolders) {
						s = s.replace("%" + places.getKey() + "%", places.getValue().toString());

					}
					p.sendMessage(s);
				}
			}
		}
		return;
	}

	public static SimpleEntry<String, Object> makePlaceholder(String key, Object value) {
		return new SimpleEntry<>(key, value);
	}

	@SuppressWarnings("deprecation")
	public static void sendMessage(CommandSender p, String str) {
		FileConfiguration messages = Config.getConfig("Messages").get();
		if (messages.getStringList(str) != null) {
			for (String s : messages.getStringList(str)) {
				s = ChatColor.translateAlternateColorCodes('&', s).replace("{prefix}", OITC.prefix);
				p.sendMessage(s);
			}
		} else {
			InputStream in = OITC.plugin.getResource("Messages.yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(in);
			if (config.getStringList(str) != null) {
				messages.set(str, config.getStringList(str));
				Config.getConfig("Messages").save();
				try {
					in.close();
				} catch (IOException e) {}
				for (String s : messages.getStringList(str)) {
					s = ChatColor.translateAlternateColorCodes('&', s).replace("{prefix}", OITC.prefix);
					p.sendMessage(s);
				}
			}
		}
		return;
	}

	@SuppressWarnings("deprecation")
	public static String getMessage(String str) {
		FileConfiguration messages = Config.getConfig("Messages").get();
		if (messages.isString(str)) {
			return ChatColor.translateAlternateColorCodes('&', messages.getString(str));
		} else {
			InputStream in = OITC.plugin.getResource("Messages.yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(in);
			if (config.isString(str)) {
				messages.set(str, config.getString(str));
				Config.getConfig("Messages").save();
				try {
					in.close();
				} catch (IOException e) {}
				return ChatColor.translateAlternateColorCodes('&', messages.getString(str));
			}
		}
		return "";
	}
}
