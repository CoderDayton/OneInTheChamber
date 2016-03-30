package com.dayton.oneinthechamber.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import com.dayton.oneinthechamber.utils.Message;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.dayton.oneinthechamber.OITC;

public class CmdManager implements CommandExecutor {

	public static ArrayList<Cmd> cmds;

	public CmdManager() {
		cmds = new ArrayList<>();
		cmds.add(new ArenaCreate());
		cmds.add(new MapCreate());
		cmds.add(new SetSpawn());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		CommandSender p = sender;

		if (cmd.getName().equalsIgnoreCase("oitc")) {
			if (args.length == 0) {
				for (Cmd cm : cmds) {
					CmdInfo info = cm.getClass().getAnnotation(CmdInfo.class);
					p.sendMessage(OITC.prefix + "§7/oitc " + StringUtils.join(info.aliases(), " | ") + " "
							+ info.usage() + " - " + info.description());
				}
				return true;
			}

			Cmd command = null;

			for (Cmd cm : cmds) {
				CmdInfo info = cm.getClass().getAnnotation(CmdInfo.class);
				for (String aliases : info.aliases()) {
					if (aliases.equals(args[0])) {
						command = cm;
						break;
					}
				}
			}

			if (command == null) {
				Message.sendMessage(p, "invalid-cmd");
				return true;
			}

			if (!p.hasPermission(command.getClass().getAnnotation(CmdInfo.class).permission())) {
				Message.sendMessage(p, "no-permission");
				return true;
			}

			Vector<String> a = new Vector<>(Arrays.asList(args));
			a.remove(0);
			args = a.toArray(new String[a.size()]);

			command.onCommand(p, args);
		}
		return false;
	}
	
	public static abstract class Cmd {
		public abstract void onCommand(CommandSender sender, String[] args);
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface CmdInfo {

		String description();

		String usage();

		String[] aliases();

		String permission();
	}
	
}
