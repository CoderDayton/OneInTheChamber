package com.dayton.oneinthechamber;

import com.dayton.oneinthechamber.commands.CmdManager;
import com.dayton.oneinthechamber.core.Arena;
import com.dayton.oneinthechamber.core.ArenaMap;
import com.dayton.oneinthechamber.core.LobbySign;
import com.dayton.oneinthechamber.listeners.EntityListeners;
import com.dayton.oneinthechamber.listeners.LobbySignListeners;
import com.dayton.oneinthechamber.listeners.PlayerListeners;
import com.dayton.oneinthechamber.utils.Config;
import com.dayton.oneinthechamber.utils.Message;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class OITC extends JavaPlugin {

    public static OITC plugin;
    public static String prefix;

    public void onEnable() {
        plugin = this;

        registerListeners();
        registerCommands();
        loadConfigs();

        ArenaMap.loadMaps();
        Arena.loadArenas();
        LobbySign.loadSigns();

        prefix = Message.getMessage("Prefix") + " ";
    }

    private void registerCommands() {
        getCommand("oitc").setExecutor(new CmdManager());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new LobbySignListeners(), this);
        pm.registerEvents(new PlayerListeners(), this);
        pm.registerEvents(new EntityListeners(), this);
    }

    private void loadConfigs() {
        new Config("Arenas.yml").saveDefaultConfig();
        new Config("Maps.yml").saveDefaultConfig();
        new Config("Signs.yml").saveDefaultConfig();
        new Config("Messages.yml").saveDefaultConfig();
    }

}
