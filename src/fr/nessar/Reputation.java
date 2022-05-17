package fr.nessar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;

public class Reputation {
	private static byte reporterValues[] = { 0, 0, 0, 0, -40, -15, 0, 15 };
	private static byte reportedValues[] = { 0, 0, 0, 0, 5, 2, 0, -40 };
	private static byte ticketValues[] = { 0, 0, 0, 0, -20, -5, -2, 5 };
	private static byte punishmentValues[] = { 0, -25, -50, 0, 0 };

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
		String colorEnd;
		if (rep == 0) {
			colorEnd = sqrBLUE;
			sqrBLUE = sqrBLUE + ChatColor.UNDERLINE.toString();
		} else if (rep < -80) {
			colorEnd = sqrDARKRED;
			sqrDARKRED = sqrDARKRED + ChatColor.UNDERLINE.toString();
		} else if (rep < -40) {
			colorEnd = sqrRED;
			sqrRED = sqrRED + ChatColor.UNDERLINE.toString();
		} else if (rep < 0) {
			colorEnd = sqrGOLD;
			sqrGOLD = sqrGOLD + ChatColor.UNDERLINE.toString();
		} else if (rep < 40) {
			colorEnd = sqrYELLOW;
			sqrYELLOW = sqrYELLOW + ChatColor.UNDERLINE.toString();
		} else if (rep < 80) {
			colorEnd = sqrGREEN;
			sqrGREEN = sqrGREEN + ChatColor.UNDERLINE.toString();
		} else {
			colorEnd = sqrDARKGREEN;
			sqrDARKGREEN = sqrDARKGREEN + ChatColor.UNDERLINE.toString();
		}
		return sqrDARKRED + symbol + sqrRED + symbol + sqrGOLD + symbol + sqrBLUE + symbol + sqrYELLOW + symbol
				+ sqrGREEN + symbol + sqrDARKGREEN + symbol + colorEnd + " (" + rep + ")";
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
				ret.add(report);
			}
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
				ret += reporterValues[report.getStatus().getStatusCode()];
			else
				ret += ticketValues[report.getStatus().getStatusCode()];
		}
		return ret;
	}

	private static short calcRepFromReported(List<Report> reports) {
		short ret = 0;
		for (Report report : reports) {
			if (report.isReport())
				ret += reportedValues[report.getStatus().getStatusCode()];
		}
		return ret;
	}

	private static short calcRepFromPunishments(List<Punishment> punishments) {
		short ret = 0;
		for (Punishment punishment : punishments) {
			ret += punishmentValues[punishment.getpType().getPunishCode()];
		}
		return ret;
	}
}
