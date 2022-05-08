package fr.nessar;

import net.md_5.bungee.api.ChatColor;

public enum ReportStatus {
	WAITING(0),
	INPROGRESS(1),
	IMPORTANT(2),
	CLASSED_ABUSIVE(4),
	CLASSED_FALSE(5),
	CLASSED_NOTSURE(6),
	CLASSED_TRUE(7);

	private final int statusCode;

	private ReportStatus(int code) {
		this.statusCode = code;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public static ReportStatus getReportStatusFromInt(int i) {
		if (i == 0)
			return ReportStatus.WAITING;
		if (i == 1)
			return ReportStatus.INPROGRESS;
		if (i == 2)
			return ReportStatus.IMPORTANT;
		if (i == 4)
			return ReportStatus.CLASSED_ABUSIVE;
		if (i == 5)
			return ReportStatus.CLASSED_FALSE;
		if (i == 6)
			return ReportStatus.CLASSED_NOTSURE;
		if (i == 7)
			return ReportStatus.CLASSED_TRUE;
		else
			return null;
	}

	public String getStatusName(boolean report) {
		switch (this) {
			case WAITING:
				return ChatColor.GREEN + "En attente";
			case INPROGRESS:
				return ChatColor.GOLD + "En cours";
			case IMPORTANT:
				return ChatColor.DARK_RED + "Important";
			case CLASSED_ABUSIVE:
				return ChatColor.BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.DARK_RED + "abusif";
			case CLASSED_FALSE:
				if (report)
					return ChatColor.BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.RED + "Faux";
				return ChatColor.BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.RED
						+ "pas de solution";
			case CLASSED_NOTSURE:
				if (report)
					return ChatColor.BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Incertain";
				return ChatColor.BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.GOLD + "non-résolu";
			case CLASSED_TRUE:
				if (report)
					return ChatColor.BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.GREEN + "vrai";
				return ChatColor.BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.GREEN + "résolu";
			default:
				return "";
		}
	}

	public boolean isImportant() {
		return this == ReportStatus.IMPORTANT;
	}

	public boolean isGreaterThan(ReportStatus r) {
		return this.statusCode > r.getStatusCode();
	}

	public boolean isGreaterOrEqualThan(ReportStatus r) {
		return this.statusCode >= r.getStatusCode();
	}

	public boolean equals(ReportStatus r) {
		return this.statusCode == r.getStatusCode();
	}

	public boolean isClassed() {
		return this.statusCode > 2;
	}
}
