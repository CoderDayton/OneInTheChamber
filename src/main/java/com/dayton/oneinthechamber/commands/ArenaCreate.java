package com.dayton.oneinthechamber.commands;

import com.dayton.oneinthechamber.core.Arena;
import com.dayton.oneinthechamber.core.ArenaMap;
import com.dayton.oneinthechamber.utils.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CmdManager.CmdInfo(aliases = { "arenacreate", "ac" }, description = "Create an arena", permission = "oitc.arenacreate", usage = "<name>")
public class ArenaCreate extends CmdManager.Cmd {

    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length < 2) {
                Message.sendMessage(p, "need-args");
                return;
            }

            if (args.length > 2) {
                Message.sendMessage(p, "less-args");
                return;
            }

            String arenaName = args[0];
            ArenaMap map = ArenaMap.getMap(args[1]);

            if (map == null) {
                Message.sendMessage(p, "map-nonexists", Message.makePlaceholder("map", args[1]));
                return;
            }

            if (Arena.getArena(arenaName) != null) {
                Message.sendMessage(p, "arena-exists", Message.makePlaceholder("arena", args[0]));
                return;
            }

            Arena.addArena(arenaName, map);
            Message.sendMessage(p, "arena-created", Message.makePlaceholder("arena", arenaName));
        }
    }
}
