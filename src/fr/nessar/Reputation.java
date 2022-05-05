package fr.nessar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Reputation {
	private static byte reporterValues[] = { 0, 0, 0, 0, -50, -15, 0, 25 };
	private static byte reportedValues[] = { 0, 0, 0, 0, 15, 5, 0, -50 };
	private static byte ticketValues[] = { 0, 0, 0, 0, -20, -5, 0, 10 };
	private static byte punishmentValues[] = { 0, -25, -50 };

	// TODO use db precise request instead of all the reports and punishments
	public static byte getRep(Player p, List<Report> reports, List<Punishment> punishList) {
		short reporterRep = calcRepFromReporter(getReportsFrom(p, reports));
		short reportedRep = calcRepFromReported(getReportOf(p, reports));
		short punishRep = calcRepFromPunishments(getPunishmentsof(p, punishList));
		if (punishRep + reportedRep + reporterRep > 127)
			return 127;
		if (punishRep + reportedRep + reporterRep < -128)
			return -128;
		return (byte) (punishRep + reportedRep + reporterRep);
	}

	public static List<Report> getReportsFrom(Player p, List<Report> reports) {
		List<Report> ret = new ArrayList<>();
		for (Report report : reports) {
			if (UUID.fromString(report.getReporter().getUniqueId()).equals(p.getUniqueId())) {
			}
			ret.add(report);
		}
		return ret;
	}

	public static List<Report> getReportOf(Player p, List<Report> reports) {
		List<Report> ret = new ArrayList<>();
		for (Report report : reports) {
			if (report.isTicket())
				continue;
			if (UUID.fromString(report.getReported().getUniqueId()).equals(p.getUniqueId())) {
				ret.add(report);
			}
		}
		return ret;
	}

	public static List<Punishment> getPunishmentsof(Player p, List<Punishment> punishments) {
		List<Punishment> ret = new ArrayList<>();
		for (Punishment punishment : punishments) {
			if (punishment.getPunishedUUID().equals(p.getUniqueId())) {
				ret.add(punishment);
			}
		}
		return ret;
	}

	private static short calcRepFromReporter(List<Report> reports) {
		short ret = 0;
		for (Report report : reports) {
			if (report.isReport())
				ret -= reporterValues[report.getStatus().getStatusCode()];
			else
				ret -= ticketValues[report.getStatus().getStatusCode()];
		}
		return ret;
	}

	private static short calcRepFromReported(List<Report> reports) {
		short ret = 0;
		for (Report report : reports) {
			if (report.isReport())
				ret -= reportedValues[report.getStatus().getStatusCode()];
		}
		return ret;
	}

	private static short calcRepFromPunishments(List<Punishment> punishments) {
		short ret = 0;
		for (Punishment punishment : punishments) {
			ret -= punishmentValues[punishment.getpType().getPunishCode()];
		}
		return ret;
	}
}
