package fr.nessar;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Report {
	private String reporterUUID;
	private String reportedUUID;
	private transient Player reporter;
	private transient Player reported;
	private String reportReason;
	private String reportMessage;
	private transient Date reportTime;
	private long reportTimeMs;

	public Report(Player reporter, Player reported, String reportReason, String reportMessage, Date reportTime) {
		this.reporterUUID = reporter.getUniqueId().toString();
		this.reportedUUID = reported.getUniqueId().toString();
		this.reporter = reporter;
		this.reported = reported;
		this.reportReason = reportReason;
		this.reportMessage = reportMessage;
		this.reportTime = reportTime;
		this.reportTimeMs = this.reportTime.getTime();
	}

	public Report(Player reporter, Player reported, String reportReason, String reportMessage) {
		new Report(reporter, reported, reportReason, reportMessage, new Date());
	}

	public Report(Player reporter, Player reported, String reportReason, String reportMessage, long reportTimeMs) {
		new Report(reporter, reported, reportReason, reportMessage, new Date(reportTimeMs));
	}

	public Report(String reporterUUID, String reportedUUID, String reportReason, String reportMessage, Date reportTime)
			throws Exception {
		Player reporter = Bukkit.getServer().getPlayer(UUID.fromString(reporterUUID));
		Player reported = Bukkit.getServer().getPlayer(UUID.fromString(reportedUUID));
		if (this.reporter == null || this.reported == null) {
			throw new Exception("Unable to find Player.");
		}
		new Report(reporter, reported, reportReason, reportMessage, reportTime);
	}

	public Report(String reporterUUID, String reportedUUID, String reportReason, String reportMessage)
			throws Exception {
		new Report(reporterUUID, reportedUUID, reportReason, reportMessage, new Date());
	}

	public Report(String reporterUUID, String reportedUUID, String reportReason, String reportMessage,
			long reportTimeMs)
			throws Exception {
		new Report(reporterUUID, reportedUUID, reportReason, reportMessage, new Date(reportTimeMs));
	}

	public long getReportTimeMs() {
		return this.reportTimeMs;
	}

	public String getReporterUUID() {
		return this.reporterUUID;
	}

	public Player getReporter() {
		return this.reporter;
	}

	public String getReportedUUID() {
		return this.reportedUUID;
	}

	public Player getReported() {
		return this.reported;
	}

	public String getReportMessage() {
		return this.reportMessage;
	}

	public String getReportReason() {
		return this.reportReason;
	}

	public Date getReportTime() {
		return this.reportTime;
	}

	public int getReportTimeSecond() {
		Calendar c = Calendar.getInstance();
		c.setTime(this.reportTime);
		return c.get(Calendar.SECOND);
	}

	public int getReportTimeMinute() {
		Calendar c = Calendar.getInstance();
		c.setTime(this.reportTime);
		return c.get(Calendar.MINUTE);
	}

	public int getReportTimeHour() {
		Calendar c = Calendar.getInstance();
		c.setTime(this.reportTime);
		return c.get(Calendar.HOUR_OF_DAY);
	}

	public int getReportTimeDay() {
		Calendar c = Calendar.getInstance();
		c.setTime(this.reportTime);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public int getReportTimeMonth() {
		Calendar c = Calendar.getInstance();
		c.setTime(this.reportTime);
		return c.get(Calendar.MONTH);
	}

	public int getReportTimeYear() {
		Calendar c = Calendar.getInstance();
		c.setTime(this.reportTime);
		return c.get(Calendar.YEAR);
	}
}
