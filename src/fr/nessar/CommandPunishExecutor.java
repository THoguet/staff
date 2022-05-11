package fr.nessar;

import java.util.Date;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPunishExecutor implements CommandExecutor {
	private static final String PREFIX_PERMISSION = "staff.";
	private Staff plugin;

	public CommandPunishExecutor(Staff plugin) {
		this.plugin = plugin;
	}

	public String permissionErrMessage() {
		return "You don't have the permission to do this command !";
	}

	public int getIntFromChar(char toConvert) {
		if ((int) toConvert >= 48 && (int) toConvert <= 57) {
			return (int) (toConvert - 48);
		} else
			return -1;
	}

	public Date getDateFromStr(String analyze) {
		int from = 0;
		int nbYear = 0;
		int nbMonth = 0;
		int nbDay = 0;
		int nbHour = 0;
		int nbMinute = 0;
		int nbSecond = 0;
		for (int i = 0; i < analyze.length(); i++) {
			switch (analyze.charAt(i)) {
				case 'Y':
				case 'y':
					nbYear = Integer.parseInt(analyze.substring(from, i));
					from = i + 1;
					break;
				case 'M':
					nbMonth = Integer.parseInt(analyze.substring(from, i));
					from = i + 1;
					break;
				case 'D':
				case 'd':
					nbDay = Integer.parseInt(analyze.substring(from, i));
					from = i + 1;
					break;
				case 'h':
				case 'H':
					nbHour = Integer.parseInt(analyze.substring(from, i));
					from = i + 1;
					break;
				case 'm':
					nbMinute = Integer.parseInt(analyze.substring(from, i));
					from = i + 1;
					break;
				case 's':
				case 'S':
					nbSecond = Integer.parseInt(analyze.substring(from, i));
					from = i + 1;
					break;
				default:
					break;
			}
		}
		long ret = new Date().getTime();
		ret += Punishment.yearMs * nbYear;
		ret += Punishment.monthMs * nbMonth;
		ret += Punishment.dayMs * nbDay;
		ret += Punishment.hourMs * nbHour;
		ret += Punishment.minuteMs * nbMinute;
		ret += Punishment.secondMs * nbSecond;
		return (new Date(ret));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("tempban") && args.length >= 2) {
			String reason = "";
			for (int i = 2; i < args.length; i++) {
				reason += args[i] += " ";
			}
			if (sender instanceof Player) {
				UUID punishedUUID;
				Player punished = Bukkit.getPlayer(args[0]);
				Date endtime = getDateFromStr(args[1]);
				if (punished == null) {
					Bukkit.getScheduler().runTaskAsynchronously(this.plugin,
							new TempBanOfflinePlayer(args[0], sender, this.plugin, reason, endtime, PunishType.BAN));
					return true;
				} else {
					punishedUUID = punished.getUniqueId();
					Bukkit.broadcastMessage(punishedUUID.toString());
					Bukkit.broadcastMessage(reason);
					if (punishedUUID != null)
						plugin.newPunishment((Player) sender, punishedUUID, reason, PunishType.BAN,
								new Date().getTime(), endtime.getTime(), -1);
					return true;
				}
			}
		}
		sender.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Mauvais usage de la commande.");
		return false;
	}
}
