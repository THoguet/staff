package fr.nessar;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatMessage {
	private UUID authorUUID;
	private String author;
	private long messageDate;
	private String message;

	public ChatMessage(UUID author, long messageDate, String message) {
		this.authorUUID = author;
		this.messageDate = messageDate;
		this.message = message;
		this.author = Bukkit.getOfflinePlayer(authorUUID).getName();
	}

	public ChatMessage(Player p, long messageDate, String message) {
		this(p.getUniqueId(), messageDate, message);
		this.author = p.getDisplayName();
	}

	private String getPrettyHour() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(this.messageDate));
		return ChatColor.GRAY + "[" + Staff.getNumberPlusZero(c.get(Calendar.DAY_OF_MONTH)) + "/"
				+ Staff.getNumberPlusZero(c.get(Calendar.MONTH)) + "/"
				+ c.get(Calendar.YEAR) + " " + Staff.getNumberPlusZero(c.get(Calendar.HOUR_OF_DAY)) + ":"
				+ Staff.getNumberPlusZero(c.get(Calendar.MINUTE)) + ":"
				+ Staff.getNumberPlusZero(c.get(Calendar.SECOND)) + "]";
	}

	public String getPrettyMessage() {
		boolean online = Bukkit.getPlayer(author) != null;
		return getPrettyHour() + " " + (online ? ChatColor.GREEN : ChatColor.RED) + author + ChatColor.GRAY + ": "
				+ ChatColor.WHITE + message;
	}

	public UUID getAuthorUUID() {
		return authorUUID;
	}

	public String getMessage() {
		return message;
	}

	public long getMessageDate() {
		return messageDate;
	}
}
