package com.dayton.oneinthechamber;

import com.dayton.oneinthechamber.core.Arena;
import com.dayton.oneinthechamber.utils.Config;
import org.bukkit.plugin.java.JavaPlugin;

public class OneInTheChamber extends JavaPlugin {

    public static OneInTheChamber plugin;
    public static String prefix = "§8§l(§6OITC§8§l) ";

    public void onEnable() {
        plugin = this;

        registerListeners();
        registerCommands();
        reloadConfig();

        Arena.loadArenas();
    }

    private void registerCommands() {

    }

    private void registerListeners() {

    }

    private void loadConfigs() {
        new Config("Arenas.yml").saveDefaultConfig();
    }

}
