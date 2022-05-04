package fr.nessar;

import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Punishment {
	private UUID punishedUUID;
	private UUID punisherUUID;
	private String message;
	private PunishType pType;
	private long startTime;
	private long endTime;
	private int reportID;

	public Punishment(UUID punishedUUID, UUID punisherUUID, String message, PunishType pType, long startTime,
			long endtime, int reportID) {
		this.punishedUUID = punishedUUID;
		this.punisherUUID = punisherUUID;
		this.message = message;
		this.pType = pType;
		this.startTime = startTime;
		this.endTime = endtime;
		this.reportID = reportID;
	}

	public Punishment(Player punished, Player punisher, String message, PunishType pType, long startTime, long endtime,
			int reportID) {
		new Punishment(punished.getUniqueId(), punisher.getUniqueId(), message, pType, startTime, endtime, reportID);
	}

	public Punishment(UUID punishedUUID, UUID punisherUUID, String message, int pType, long startTime,
			long endtime, int reportID) {
		new Punishment(punishedUUID, punisherUUID, message, PunishType.valueOf(pType), startTime, endtime, reportID);
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
		return new Date().getTime() - this.endTime;
	}

	public long getDuration() {
		return this.endTime - this.startTime;
	}
}
