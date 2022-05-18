package fr.nessar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReportExecutor implements CommandExecutor {

	private static final int cooldownMinute = 10;

	private static final String PREFIX_PERMISSION = "staff.";
	private Staff plugin;

	public CommandReportExecutor(Staff plugin) {
		this.plugin = plugin;
	}

	private String permissionErrMessage() {
		return "You don't have the permission to do this command !";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(
					Staff.getSTAFF_PREFIX() + ChatColor.RED + "Seul les joueurs peuvent utiliser ces commandes.");
			return true;
		}
		Player p = (Player) sender;
		if (commandLabel.equalsIgnoreCase("report")) {
			if (plugin.getLastReportInCoolDown(p, true) != -1) {
				p.sendMessage(
						Staff.getREPORT_PREFIX() + ChatColor.RED + "Vous avez déjà fait un report il y a moins de "
								+ cooldownMinute + " minutes.");
				return true;
			} else {
				int index = plugin.isPunished(p, PunishType.REPORT);
				if (index != -1) {
					p.sendMessage(plugin.getPunishments().get(index).getPrettyMessage());
					return true;
				}
			}
			if (args.length == 1) {
				Player reported = Bukkit.getPlayer(args[0]);
				if (reported == null) {
					p.sendMessage(
							Staff.getREPORT_PREFIX() + ChatColor.RED + "Joueur " + args[0] + " introuvable ...");
					return true;
				}
				// TODO
				return true;
			} else if (args.length >= 2) {
				Player reported = Bukkit.getPlayer(args[0]);
				if (reported == null) {
					p.sendMessage(
							Staff.getREPORT_PREFIX() + ChatColor.RED + "Joueur " + args[0] + " introuvable ...");
					return true;
				}
				String reason = "";
				for (int i = 1; i < args.length; i++) {
					reason += args[i] + " ";
				}
				plugin.newReport(p, reported, reason);
				return true;
			}
		}
		p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Mauvais usage de la commande."
				+ args.length);
		return false;
	}
}