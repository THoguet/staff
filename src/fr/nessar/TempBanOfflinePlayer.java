package fr.nessar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class TempBanOfflinePlayer implements Runnable {
	private String pseudo;
	private CommandSender sender;
	private Staff plugin;
	private String reason;
	private PunishType pType;
	private Date endTime;

	public TempBanOfflinePlayer(String pseudo, CommandSender sender, Staff plugin, String reason, Date endtime,
			PunishType pType) {
		this.pseudo = pseudo;
		this.sender = sender;
		this.plugin = plugin;
		this.reason = reason;
		this.endTime = endtime;
		this.pType = pType;
	}

	public String addChar(String str, char ch, int position) {
		return str.substring(0, position) + ch + str.substring(position);
	}

	@Override
	public void run() {
		final int SIZEBEFOREUUID = 17;
		final int SIZEAFTERUUID = 2;
		String response = "";
		try {
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + pseudo);
			InputStream is = url.openStream();
			int rep = is.read();
			while (rep != -1) {
				response += Character.valueOf((char) rep);
				rep = is.read();
			}
		} catch (IOException e) {
			return;
		}
		response = response.substring(SIZEBEFOREUUID + pseudo.length(), response.length() - SIZEAFTERUUID);
		response = addChar(response, '-', 8);
		response = addChar(response, '-', 13);
		response = addChar(response, '-', 18);
		response = addChar(response, '-', 23);
		UUID punishedUUID = UUID.fromString(response);
		Bukkit.getScheduler().runTask(this.plugin,
				new addPunishTask(sender, plugin, reason, this.endTime, punishedUUID, pType));
	}
}
