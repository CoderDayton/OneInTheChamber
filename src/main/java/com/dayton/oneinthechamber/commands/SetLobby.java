package com.dayton.oneinthechamber.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dayton.oneinthechamber.core.ArenaMap;
import com.dayton.oneinthechamber.utils.Message;

@CmdManager.CmdInfo(aliases = { "setlobby", "sl" }, description = "Set the lobby spawn for a map", permission = "oitc.setlobby", usage = "")
public class SetLobby extends CmdManager.Cmd {

    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {

            Player p = (Player) sender;

            String name = args[0];

            if (ArenaMap.getMap(name) == null) {
                Message.sendMessage(p, "map-nonexists", Message.makePlaceholder("map", name));
                return;
            }

            ArenaMap map = ArenaMap.getMap(name);
            map.setLobbySpawn(p.getLocation());
            Message.sendMessage(p, "lobbyspawn-set", Message.makePlaceholder("map", name));
        } else {
            sender.sendMessage("You need to be a player to do this.");
        }
    }

}