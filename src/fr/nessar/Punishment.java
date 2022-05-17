package fr.nessar;

import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Punishment {
	public static final long yearMs = Long.valueOf("31560000000");
	public static final long monthMs = Long.valueOf("2628000000");
	public static final long dayMs = Long.valueOf("86400000");
	public static final long hourMs = Long.valueOf("3600000");
	public static final long minuteMs = Long.valueOf("60000");
	public static final long secondMs = Long.valueOf("1000");
	private UUID punishedUUID;
	private UUID punisherUUID;
	private String punisherName;
	private String message;
	private PunishType pType;
	private long startTime;
	private long endTime;
	private int reportID;

	public Punishment(UUID punishedUUID, UUID punisherUUID, String message, PunishType pType, long startTime,
			long endtime, int reportID) {
		if (punisherUUID == null)
			this.punisherName = "Console";
		else
			this.punisherName = Bukkit.getOfflinePlayer(punisherUUID).getName();
		this.punishedUUID = punishedUUID;
		this.punisherUUID = punisherUUID;
		this.message = message;
		this.pType = pType;
		this.startTime = startTime;
		this.endTime = endtime;
		this.reportID = reportID;
		Player punished = Bukkit.getPlayer(punishedUUID);
		if (punished != null && this.isActive())
			switch (this.pType) {
				case BAN:
					punished.kickPlayer(this.getPrettyMessage());
					break;
				default:
					punished.sendMessage(this.getPrettyMessage());
					break;
			}
	}

	public String getPunisherName() {
		return this.punisherName;
	}

	public Punishment(Player punished, Player punisher, String message, PunishType pType, long startTime, long endtime,
			int reportID) {
		this(punished.getUniqueId(), punisher.getUniqueId(), message, pType, startTime, endtime, reportID);
	}

	public Punishment(UUID punishedUUID, UUID punisherUUID, String message, PunishType pType, long startTime,
			long endtime) {
		this(punishedUUID, punisherUUID, message, pType, startTime, endtime, -1);
	}

	public void setEndTime(long newEnd) {
		this.endTime = newEnd;
	}

	public void setmessage(String newMessage) {
		this.message = newMessage;
	}

	public String getTimeLeft() {
		long timeLeft = this.timeLeft();
		if (this.isPermanent())
			return ChatColor.DARK_RED + ChatColor.UNDERLINE.toString() + "Permanent";
		int year = 0;
		int month = 0;
		int day = 0;
		int hour = 0;
		int minute = 0;
		int second = 0;
		while (timeLeft >= Punishment.yearMs) {
			timeLeft -= Punishment.yearMs;
			year++;
		}
		while (timeLeft >= Punishment.monthMs) {
			timeLeft -= Punishment.monthMs;
			month++;
		}
		while (timeLeft >= Punishment.dayMs) {
			timeLeft -= Punishment.dayMs;
			day++;
		}
		while (timeLeft >= Punishment.hourMs) {
			timeLeft -= Punishment.hourMs;
			hour++;
		}
		while (timeLeft >= Punishment.minuteMs) {
			timeLeft -= Punishment.minuteMs;
			minute++;
		}
		while (timeLeft >= Punishment.secondMs) {
			timeLeft -= Punishment.secondMs;
			second++;
		}
		String yearStr = "";
		String monthStr = "";
		String dayStr = "";
		String hourStr = "";
		String minuteStr = "";
		String secondStr = "";
		if (year > 0)
			if (year > 1)
				yearStr = year + " ans ";
			else
				yearStr = year + " an ";
		if (month > 0)
			monthStr = month + " mois ";
		if (day > 0)
			if (day > 1)
				dayStr = day + " jours ";
			else
				dayStr = day + " jour ";
		if (hour > 0)
			if (hour > 1)
				hourStr = hour + " heures ";
			else
				hourStr = hour + " heure ";
		if (minute > 0)
			if (minute > 1)
				minuteStr = minute + " minutes ";
			else
				minuteStr = minute + " minute ";
		if (second > 0)
			if (second > 1)
				secondStr = second + " secondes ";
			else
				secondStr = second + " seconde ";
		return yearStr + monthStr + dayStr + hourStr + minuteStr + secondStr;
	}

	public String getPrettyMessage() {
		String l1;
		String l2 = ChatColor.GOLD + ChatColor.UNDERLINE.toString() + "Raison:" + ChatColor.RESET + " "
				+ this.message;
		String l3 = ChatColor.GOLD + ChatColor.UNDERLINE.toString() + "Sanctionné par:" + ChatColor.RESET + " "
				+ this.punisherName;
		String l4 = ChatColor.GOLD + ChatColor.UNDERLINE.toString() + "Temps restant:" + ChatColor.RESET + " "
				+ this.getTimeLeft();
		switch (this.pType) {
			case BAN:
				l1 = ChatColor.DARK_RED + ChatColor.BOLD.toString() + "VOUS ÊTES BANNI !";
				return l1 + "\n\n" + l2 + "\n\n" + l3 + "\n\n" + l4;
			case MUTE:
				l1 = ChatColor.DARK_RED + ChatColor.BOLD.toString() + "VOUS ÊTES MUTE !";
				return ChatColor.RED + "===============================================\n" + l1 + "\n \n" + l2 + "\n \n"
						+ l3 + "\n \n" + l4 + ChatColor.RED + "===============================================";
			case REPORT:
				l1 = ChatColor.DARK_RED + ChatColor.BOLD.toString() + "VOUS ÊTES INTERDIT DE REPORT !";
				return ChatColor.RED + "===============================================\n" + l1 + "\n \n" + l2 + "\n \n"
						+ l3 + "\n \n" + l4 + ChatColor.RED + "===============================================";
			case TICKET:
				l1 = ChatColor.DARK_RED + ChatColor.BOLD.toString() + "VOUS ÊTES INTERDIT DE TICKET !";
				return ChatColor.RED + "===============================================\n" + l1 + "\n \n" + l2 + "\n \n"
						+ l3 + "\n \n" + l4 + ChatColor.RED + "===============================================";
			default:
				return "";
		}
	}

	public UUID getPunishedUUID() {
		return punishedUUID;
	}

	public UUID getPunisherUUID() {
		return punisherUUID;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getMessage() {
		return message;
	}

	public int getReportID() {
		return reportID;
	}

	public long getStartTime() {
		return startTime;
	}

	public PunishType getpType() {
		return pType;
	}

	public long timeLeft() {
		return this.endTime - new Date().getTime();
	}

	public long getDuration() {
		return this.endTime - this.startTime;
	}

	public boolean isPermanent() {
		return this.endTime < 0;
	}

	public void setPunisher(UUID newPunisher) {
		this.punisherUUID = newPunisher;
		if (newPunisher != null)
			this.punisherName = Bukkit.getOfflinePlayer(punisherUUID).getName();
		else
			this.punisherName = "Console";
	}

	public boolean isActive() {
		return endTime < 0 || endTime > new Date().getTime();
	}
}
