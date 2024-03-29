package fr.nessar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Report {
	private static final long cooldownTime = Punishment.minuteMs * 10;
	private boolean report;
	private PlayerSave reporter;
	private PlayerSave reported;
	private String reportReason;
	private long reportTime;
	private ReportStatus status;
	private boolean ignoreCoolDown;

	public Report(Player reporter, Player reported, String reportReason, long reportTime, boolean report,
			ReportStatus status) {
		this(new PlayerSave(reporter), report ? new PlayerSave(reported) : null, reportReason, reportTime, report,
				status, false);
	}

	public Report(PlayerSave reporter, PlayerSave reported, String reportReason, long reportTime, boolean report,
			ReportStatus status, boolean ignoreCoolDown) {
		this.reporter = reporter;
		this.reported = reported;
		this.reportReason = reportReason;
		this.reportTime = reportTime;
		this.report = report;
		this.status = status;
		this.ignoreCoolDown = ignoreCoolDown;
	}

	public List<String> getLore() {
		final int nbCharPerLineReport = 20;
		List<String> reasonStrList = new ArrayList<>();
		String copyReason = this.reportReason.toString();
		if (copyReason.length() <= nbCharPerLineReport) {
			reasonStrList.add(copyReason);
		} else {
			while (copyReason.length() > nbCharPerLineReport) {
				int cutAt = copyReason.substring(nbCharPerLineReport).indexOf(' ');
				if (cutAt != -1) {
					reasonStrList.add(copyReason.subSequence(0, nbCharPerLineReport + cutAt).toString());
					copyReason = copyReason.substring(nbCharPerLineReport + cutAt + 1);
				} else {
					break;
				}
			}
			reasonStrList.add(copyReason);
		}
		if (reasonStrList.size() % 2 == 0) {
			reasonStrList.add("");
		}
		List<String> ret = new ArrayList<>();
		ret.add(ChatColor.GRAY + "Statut: " + this.status.getStatusName(this.report));
		ret.add(ChatColor.GRAY + "Date: " + ChatColor.YELLOW
				+ Staff.getNumberPlusZero(this.getReportTimeDay()) + "/"
				+ Staff.getNumberPlusZero(this.getReportTimeMonth()) + "/"
				+ Staff.getNumberPlusZero(this.getReportTimeYear()) + " "
				+ Staff.getNumberPlusZero(this.getReportTimeHour()) + ":"
				+ Staff.getNumberPlusZero(this.getReportTimeMinute()) + ":"
				+ Staff.getNumberPlusZero(this.getReportTimeSecond()));
		ret.add(" ");
		ret.add(ChatColor.GRAY + "Signaleur: " + ChatColor.GREEN + this.reporter.getName() + (this.reporter.isOnline()
				? ChatColor.GRAY + " (" + ChatColor.GREEN + "Connecté" + ChatColor.GRAY + ")"
				: ChatColor.GRAY + " (" + ChatColor.RED + "Déconnecté" + ChatColor.GRAY + ")"));
		if (this.report)
			ret.add(ChatColor.GRAY + "Signalé: " + ChatColor.RED + this.reported.getName() + (this.reported.isOnline()
					? ChatColor.GRAY + " (" + ChatColor.GREEN + "Connecté" + ChatColor.GRAY + ")"
					: ChatColor.GRAY + " (" + ChatColor.RED + "Déconnecté" + ChatColor.GRAY + ")"));
		ret.add(ChatColor.GRAY + "Raison: " + ChatColor.GOLD + reasonStrList.get(0));
		reasonStrList.remove(0);
		for (int i = 0; i < reasonStrList.size(); i += 2) {
			ret.add(ChatColor.GOLD + reasonStrList.get(i) + reasonStrList.get(i + 1));
		}
		ret.add("  ");
		ret.add(ChatColor.GOLD + "Clic" + ChatColor.GRAY + " pour afficher les détails.");
		return ret;
	}

	public TextComponent[] getChatMessage(String before, int index) {
		String loreOneString = "";
		if (this.isReport())
			loreOneString += ChatColor.RED + "Report " + ChatColor.GRAY + "#" + index;
		else
			loreOneString += ChatColor.BLUE + "Ticket " + ChatColor.GRAY + "#" + index;
		for (String lore : this.getLore()) {
			loreOneString += "\n" + lore;
		}
		TextComponent title = new TextComponent(before);
		TextComponent message = new TextComponent(
				(this.isTicket() ? ChatColor.BLUE + "Ticket " : ChatColor.RED + "Report ") + ChatColor.GRAY + "#"
						+ index);
		message.setHoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(loreOneString).create()));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/staff editreport " + index));
		TextComponent[] ret = { title, message };
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

	public boolean getIgnoreCooldown() {
		return this.ignoreCoolDown;
	}

	public void setIgnoreCooldown(boolean newStatus) {
		this.ignoreCoolDown = newStatus;
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

	public boolean isInCooldown() {
		return !this.ignoreCoolDown && new Date().getTime() < this.reportTime + cooldownTime;
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
