package fr.nessar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;

public class Reputation {
	private static byte reporterValues[] = { 0, 0, 0, 0, -50, -15, 0, 25 };
	private static byte reportedValues[] = { 0, 0, 0, 0, 15, 5, 0, -50 };
	private static byte ticketValues[] = { 0, 0, 0, 0, -20, -5, 0, 10 };
	private static byte punishmentValues[] = { 0, -25, -50 };

	public static String getRepStr(UUID pUUID, List<Report> reports, List<Punishment> punishList) {
		byte rep = getRep(pUUID, reports, punishList);
		char symbol = '█';
		String sqrDARKRED = ChatColor.DARK_RED.toString();
		String sqrRED = ChatColor.RED.toString();
		String sqrGOLD = ChatColor.GOLD.toString();
		String sqrBLUE = ChatColor.BLUE.toString();
		String sqrYELLOW = ChatColor.YELLOW.toString();
		String sqrGREEN = ChatColor.GREEN.toString();
		String sqrDARKGREEN = ChatColor.DARK_GREEN.toString();
		if (rep == 0)
			sqrBLUE = sqrBLUE + ChatColor.UNDERLINE.toString();
		else if (rep < -80)
			sqrDARKRED = sqrDARKRED + ChatColor.UNDERLINE.toString();
		else if (rep < 40)
			sqrRED = sqrRED + ChatColor.UNDERLINE.toString();
		else if (rep < 0)
			sqrGOLD = sqrGOLD + ChatColor.UNDERLINE.toString();
		else if (rep < 40)
			sqrYELLOW = sqrYELLOW + ChatColor.UNDERLINE.toString();
		else if (rep < 80)
			sqrGREEN = sqrGREEN + ChatColor.UNDERLINE.toString();
		else
			sqrDARKGREEN = sqrDARKGREEN + ChatColor.UNDERLINE.toString();
		return sqrDARKRED + symbol + sqrRED + symbol + sqrGOLD + symbol + sqrBLUE + symbol + sqrYELLOW + symbol
				+ sqrGREEN + symbol + sqrDARKGREEN + symbol;
	}

	// TODO use db precise request instead of all the reports and punishments
	public static byte getRep(UUID pUUID, List<Report> reports, List<Punishment> punishList) {
		short reporterRep = calcRepFromReporter(getReportsFrom(pUUID, reports));
		short reportedRep = calcRepFromReported(getReportOf(pUUID, reports));
		short punishRep = calcRepFromPunishments(getPunishmentsof(pUUID, punishList));
		if (punishRep + reportedRep + reporterRep > 127)
			return 127;
		if (punishRep + reportedRep + reporterRep < -128)
			return -128;
		return (byte) (punishRep + reportedRep + reporterRep);
	}

	public static List<Report> getReportsFrom(UUID pUUID, List<Report> reports) {
		List<Report> ret = new ArrayList<>();
		for (Report report : reports) {
			if (UUID.fromString(report.getReporter().getUniqueId()).equals(pUUID)) {
			}
			ret.add(report);
		}
		return ret;
	}

	public static List<Report> getReportOf(UUID pUUID, List<Report> reports) {
		List<Report> ret = new ArrayList<>();
		for (Report report : reports) {
			if (report.isTicket())
				continue;
			if (UUID.fromString(report.getReported().getUniqueId()).equals(pUUID)) {
				ret.add(report);
			}
		}
		return ret;
	}

	public static List<Punishment> getPunishmentsof(UUID pUUID, List<Punishment> punishments) {
		List<Punishment> ret = new ArrayList<>();
		for (Punishment punishment : punishments) {
			if (punishment.getPunishedUUID().equals(pUUID)) {
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
