package com.dayton.oneinthechamber.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dayton.oneinthechamber.core.Arena;
import com.dayton.oneinthechamber.utils.Message;

@CmdManager.CmdInfo(aliases = { "endgame", "end" }, description = "Forcefully end a game", permission = "oitc.endgame", usage = "<arena>")
public class EndGame extends CmdManager.Cmd {

    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {

            Player p = (Player) sender;

            String name = args[0];

            if (Arena.getArena(name) == null) {
                Message.sendMessage(p, "arena-nonexists", Message.makePlaceholder("arena", name));
                return;
            }

            if (Arena.getArena(name).getState() != Arena.ArenaState.IN_GAME) {

            }

            Arena.getArena(name).end(false);
            Message.sendMessage(p, "arena-forceended", Message.makePlaceholder("arena", name));
        } else {
            sender.sendMessage("You need to be a player to do this.");
        }
    }
}
