package com.dayton.oneinthechamber.commands;

import com.dayton.oneinthechamber.core.ArenaMap;
import com.dayton.oneinthechamber.utils.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CmdManager.CmdInfo(aliases = { "mapcreate", "mc" }, description = "Create a map", permission = "oitc.mapcreate", usage = "<name>")
public class MapCreate extends CmdManager.Cmd {

    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {

            Player p = (Player) sender;

            String name = args[0];

            if (ArenaMap.getMap(name) != null) {
                Message.sendMessage(p, "map-exists", Message.makePlaceholder("map", name));
                return;
            }

            ArenaMap.addMap(name, p.getWorld());
            Message.sendMessage(p, "map-created", Message.makePlaceholder("map", name));
        } else {
            sender.sendMessage("You need to be a player to do this.");
        }
    }

}
