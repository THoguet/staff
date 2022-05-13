package fr.nessar;

import java.util.Date;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class addPunishTask implements Runnable {
	private CommandSender sender;
	private Staff plugin;
	private String reason;
	private long endTime;
	private UUID punishedUUID;
	private PunishType pType;
	private boolean edit = false;
	private int pID;
	private int addOrRemove;

	public addPunishTask(CommandSender sender, Staff plugin, String reason, long endtime, int addOrRemove,
			UUID punishedUUID,
			PunishType pType, boolean edit, int editid) {
		this.sender = sender;
		this.plugin = plugin;
		this.reason = reason;
		this.endTime = endtime;
		this.punishedUUID = punishedUUID;
		this.pType = pType;
		this.edit = edit;
		this.pID = editid;
		this.addOrRemove = addOrRemove;
	}

	public addPunishTask(CommandSender sender, Staff plugin, String reason, long endtime, UUID punishedUUID,
			PunishType pType) {
		this.sender = sender;
		this.plugin = plugin;
		this.reason = reason;
		this.endTime = endtime;
		this.punishedUUID = punishedUUID;
		this.pType = pType;
	}

	@Override
	public void run() {
		Player punisher = null;
		if (sender instanceof Player) {
			punisher = (Player) sender;
		}
		if (this.punishedUUID != null) {
			if (edit) {
				if (addOrRemove == -1) {
					endTime = plugin.getPunishments().get(pID).getEndTime() - endTime;
				} else if (addOrRemove == 1)
					endTime = plugin.getPunishments().get(pID).getEndTime() + endTime;
				plugin.editPunishment(pID, this.punishedUUID, punisher, endTime);
			} else
				plugin.newPunishment(punisher, this.punishedUUID, reason, this.pType,
						new Date().getTime(), endTime, -1);
		} else {
			sender.sendMessage(Staff.getREPORT_PREFIX() + ChatColor.RED + "La sanction n'a pas pu être appliqué.");
		}
	}
}
