package fr.nessar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Report {
	private boolean report;
	private PlayerSave reporter;
	private PlayerSave reported;
	private String reportReason;
	private long reportTime;
	private ReportStatus status;

	public Report(Player reporter, Player reported, String reportReason, long reportTime, boolean report,
			ReportStatus status) {
		this(new PlayerSave(reporter), report ? new PlayerSave(reported) : null, reportReason, reportTime, report,
				status);
	}

	public Report(PlayerSave reporter, PlayerSave reported, String reportReason, long reportTime, boolean report,
			ReportStatus status) {
		this.reporter = reporter;
		this.reported = reported;
		this.reportReason = reportReason;
		this.reportTime = reportTime;
		this.report = report;
		this.status = status;
	}

	public List<String> getLore() {
		List<String> ret = new ArrayList<>();
		ret.add(ChatColor.GRAY + "Statut: " + this.status.getStatusName(this.report));
		ret.add(ChatColor.GRAY + "Date: " + ChatColor.YELLOW
				+ this.getNumberPlusZero(this.getReportTimeDay()) + "/"
				+ this.getNumberPlusZero(this.getReportTimeMonth()) + "/"
				+ this.getNumberPlusZero(this.getReportTimeYear()) + " "
				+ this.getNumberPlusZero(this.getReportTimeHour()) + ":"
				+ this.getNumberPlusZero(this.getReportTimeMinute()) + ":"
				+ this.getNumberPlusZero(this.getReportTimeSecond()));
		ret.add(" ");
		ret.add(ChatColor.GRAY + "Signaleur: " + ChatColor.GREEN + this.reporter.getName() + (this.reporter.isOnline()
				? ChatColor.GRAY + " (" + ChatColor.GREEN + "Connecté" + ChatColor.GRAY + ")"
				: ChatColor.GRAY + " (" + ChatColor.RED + "Déconnecté" + ChatColor.GRAY + ")"));
		if (this.report)
			ret.add(ChatColor.GRAY + "Signalé: " + ChatColor.RED + this.reported.getName() + (this.reported.isOnline()
					? ChatColor.GRAY + " (" + ChatColor.GREEN + "Connecté" + ChatColor.GRAY + ")"
					: ChatColor.GRAY + " (" + ChatColor.RED + "Déconnecté" + ChatColor.GRAY + ")"));
		ret.add(ChatColor.GRAY + "Raison: " + ChatColor.GOLD + this.reportReason);
		ret.add("  ");
		ret.add(ChatColor.GOLD + "Clic" + ChatColor.GRAY + " pour afficher les détails.");
		return ret;
	}

	public Report(Player reporter, Player reported, String reportReason, boolean report) {
		this(reporter, reported, reportReason, new Date().getTime(), report, ReportStatus.WAITING);
	}

	public Report(Player reporter, Player reported, String reportReason, long reportTime, boolean report) {
		this(reporter, reported, reportReason, new Date().getTime(), report, ReportStatus.WAITING);
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

	public PlayerSave getReporter() {
		return this.reporter;
	}

	public PlayerSave getReported() {
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

	public String getNumberPlusZero(int time) {
		if (time < 10) {
			return "0" + time;
		}
		return String.valueOf(time);
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
