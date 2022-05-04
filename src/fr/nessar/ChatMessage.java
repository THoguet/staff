package fr.nessar;

import java.util.UUID;

import org.bukkit.entity.Player;

public class ChatMessage {
	private UUID authorUUID;
	private long messageDate;
	private String message;

	public ChatMessage(UUID author, long messageDate, String message) {
		this.authorUUID = author;
		this.messageDate = messageDate;
		this.message = message;
	}

	public ChatMessage(Player p, long messageDate, String message) {
		new ChatMessage(p.getUniqueId(), messageDate, message);
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
