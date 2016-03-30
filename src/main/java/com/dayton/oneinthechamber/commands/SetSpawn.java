package com.dayton.oneinthechamber.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dayton.oneinthechamber.core.ArenaMap;
import com.dayton.oneinthechamber.utils.Message;

@CmdManager.CmdInfo(aliases = { "setspawn", "ss" }, description = "Add a spawn point to a map", permission = "oitc.setspawn", usage = "")
public class SetSpawn extends CmdManager.Cmd {

    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {

            Player p = (Player) sender;

            String name = args[0];

            if (ArenaMap.getMap(name) == null) {
                Message.sendMessage(p, "map-nonexists", Message.makePlaceholder("map", name));
                return;
            }

            ArenaMap map = ArenaMap.getMap(name);
            map.addLocation(p.getLocation());
            Message.sendMessage(p, "spawn-set", Message.makePlaceholder("map", name));
        } else {
            sender.sendMessage("You need to be a player to do this.");
        }
    }

}