package fr.nessar;

import java.util.Calendar;
import java.util.Date;
import org.bukkit.entity.Player;

public class Report {
	private boolean report;
	private Player reporter;
	private Player reported;
	private String reportReason;
	private long reportTime;
	private ReportStatus status;

	public Report(Player reporter, Player reported, String reportReason, long reportTime, boolean report,
			ReportStatus status) {
		this.reporter = reporter;
		this.reported = reported;
		this.reportReason = reportReason;
		this.reportTime = reportTime;
		this.report = report;
		this.status = status;
	}

	public Report(Player reporter, Player reported, String reportReason, boolean report) {
		new Report(reporter, reported, reportReason, new Date().getTime(), report, ReportStatus.WAITING);
	}

	public Report(Player reporter, Player reported, String reportReason, long reportTime, boolean report) {
		new Report(reporter, reported, reportReason, new Date().getTime(), report, ReportStatus.WAITING);
	}

	public void changeStatus(ReportStatus newStatus) {
		this.status = newStatus;
	}

	public ReportStatus getStatus() {
		return this.status;
	}

	public boolean isReport() {
		return this.report;
	}

	public boolean isTicket() {
		return !this.report;
	}

	public long getReportTime() {
		return this.reportTime;
	}

	public Player getReporter() {
		return this.reporter;
	}

	public Player getReported() {
		return this.reported;
	}

	public String getReportReason() {
		return this.reportReason;
	}

	public int getReportTimeSecond() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(this.reportTime));
		return c.get(Calendar.SECOND);
	}

	public int getReportTimeMinute() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(this.reportTime));
		return c.get(Calendar.MINUTE);
	}

	public int getReportTimeHour() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(this.reportTime));
		return c.get(Calendar.HOUR_OF_DAY);
	}

	public int getReportTimeDay() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(this.reportTime));
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public int getReportTimeMonth() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(this.reportTime));
		return c.get(Calendar.MONTH);
	}

	public int getReportTimeYear() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(this.reportTime));
		return c.get(Calendar.YEAR);
	}
}
