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

	public long getDateFromStr(String analyze, long startTime) throws NumberFormatException {
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
		startTime += Punishment.yearMs * nbYear;
		startTime += Punishment.monthMs * nbMonth;
		startTime += Punishment.dayMs * nbDay;
		startTime += Punishment.hourMs * nbHour;
		startTime += Punishment.minuteMs * nbMinute;
		startTime += Punishment.secondMs * nbSecond;
		return startTime;
	}

	public long getDateFromStr(String analyze) {
		return getDateFromStr(analyze, new Date().getTime());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if ((commandLabel.equalsIgnoreCase("tempban") || commandLabel.equalsIgnoreCase("tempmute")
				|| commandLabel.equalsIgnoreCase("tempreport") || commandLabel.equalsIgnoreCase("tempticket"))
				&& args.length >= 2
				|| ((commandLabel.equalsIgnoreCase("permaban") || commandLabel.equalsIgnoreCase("permamute"))
						&& args.length >= 1)) {
			PunishType pType = PunishType.NULL;
			if (commandLabel.equalsIgnoreCase("tempban") || commandLabel.equalsIgnoreCase("permaban"))
				pType = PunishType.BAN;
			else if (commandLabel.equalsIgnoreCase("tempmute") || commandLabel.equalsIgnoreCase("permamute"))
				pType = PunishType.MUTE;
			else if (commandLabel.equalsIgnoreCase("tempreport") || commandLabel.equalsIgnoreCase("permareport"))
				pType = PunishType.REPORT;
			else if (commandLabel.equalsIgnoreCase("tempticket") || commandLabel.equalsIgnoreCase("permaticket"))
				pType = PunishType.TICKET;
			int startArgs = 0;
			int perma = 0;
			int addOrRemove = 0; // -1 == remove / 0 == set / 1 == add
			long endTime;
			try {
				if (commandLabel.startsWith("perma")) {
					endTime = -1;
					perma = 1;
				} else if (args[0].equals("+")) {
					startArgs++;
					addOrRemove = 1;
					endTime = getDateFromStr(args[startArgs + 1], 0);
				} else if (args[0].equals("-")) {
					startArgs++;
					addOrRemove = -1;
					endTime = getDateFromStr(args[startArgs + 1], 0);
				} else {
					endTime = getDateFromStr(args[startArgs + 1]);
				}
			} catch (NumberFormatException e) {
				sender.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Temps Incorrect.");
				return false;
			}
			String reason = "";
			for (int i = startArgs + 2 - perma; i < args.length; i++) {
				reason += args[i] += " ";
			}
			Player p = null;
			if (sender instanceof Player) {
				p = (Player) sender;
			}
			UUID punishedUUID;
			Player punished = Bukkit.getPlayer(args[startArgs + 0]);
			if (punished == null) {
				Bukkit.getScheduler().runTaskAsynchronously(this.plugin,
						new PunishOfflinePlayer(args[startArgs + 0], p, this.plugin, reason, endTime,
								addOrRemove, pType));
				return true;
			} else {
				punishedUUID = punished.getUniqueId();
				int pID = plugin.getActivePunishId(punishedUUID, pType);
				if (pID != -1) {
					if (addOrRemove == -1) {
						endTime = plugin.getPunishments().get(pID).getEndTime() - endTime;
					} else if (addOrRemove == 1)
						endTime = plugin.getPunishments().get(pID).getEndTime() + endTime;
					plugin.editPunishment(pID, punishedUUID, p, endTime);
				} else {
					plugin.newPunishment(p, punishedUUID, reason, pType, new Date().getTime(),
							endTime, -1);
				}
				return true;
			}
		}
		sender.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Mauvais usage de la commande.");
		return false;
	}
}
