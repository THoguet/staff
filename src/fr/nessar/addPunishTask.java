package fr.nessar;

import java.util.Date;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class addPunishTask implements Runnable {
	private CommandSender sender;
	private Staff plugin;
	private String reason;
	private Date endTime;
	private UUID punishedUUID;
	private PunishType pType;

	public addPunishTask(CommandSender sender, Staff plugin, String reason, Date endtime, UUID punishedUUID,
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
		if (this.punishedUUID != null) {
			plugin.newPunishment((Player) sender, this.punishedUUID, reason, this.pType,
					new Date().getTime(), endTime.getTime(), -1);
		} else {
			sender.sendMessage(Staff.getREPORT_PREFIX() + ChatColor.RED + "La sanction n'a pas pu être appliqué.");
		}
	}
}
