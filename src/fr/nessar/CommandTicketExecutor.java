package fr.nessar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTicketExecutor implements CommandExecutor {

	private static final int cooldownMinute = 10;

	private static final String PREFIX_PERMISSION = "staff.";
	private Staff plugin;

	public CommandTicketExecutor(Staff plugin) {
		this.plugin = plugin;
	}

	public String permissionErrMessage() {
		return "You don't have the permission to do this command !";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			Bukkit.getConsoleSender()
					.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Only player can use this command.");
			return true;
		}
		Player p = (Player) sender;
		if (commandLabel.equalsIgnoreCase("ticket") && args.length >= 1) {
			if (plugin.getLastReportInCoolDown(p, false) != -1) {
				p.sendMessage(
						Staff.getREPORT_PREFIX() + ChatColor.RED + "Vous avez déjà fait un ticket il y a moins de "
								+ cooldownMinute + " minutes.");
				return true;
			} else {
				int index = plugin.isPunished(p, PunishType.TICKET);
				if (index != -1) {
					p.sendMessage(plugin.getPunishments().get(index).getPrettyMessage());
					return true;
				}
			}
			String reason = "";
			for (String arg : args) {
				reason += arg += " ";
			}
			plugin.newTicket(p, reason);
			return true;
		}
		sender.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Mauvais usage de la commande.");
		return false;
	}
}
