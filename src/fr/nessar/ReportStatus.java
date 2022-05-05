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

	public String getStatusName(boolean report) {
		switch (this) {
			case WAITING:
				return ChatColor.GREEN + "En attente";
			case INPROGRESS:
				return ChatColor.GOLD + "En cours";
			case IMPORTANT:
				return ChatColor.DARK_RED + "En cours (Important)";
			case CLASSED_ABUSIVE:
				return ChatColor.DARK_BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.DARK_RED + "abusif";
			case CLASSED_FALSE:
				if (report)
					return ChatColor.DARK_BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.RED + "Faux";
				return ChatColor.DARK_BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.RED
						+ "pas de solution";
			case CLASSED_NOTSURE:
				if (report)
					return ChatColor.DARK_BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.GOLD + "Incertain";
				return ChatColor.DARK_BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.GOLD + "non-résolu";
			case CLASSED_TRUE:
				if (report)
					return ChatColor.DARK_BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.GREEN + "vrai";
				return ChatColor.DARK_BLUE + "Archivé" + ChatColor.GRAY + ", " + ChatColor.GREEN + "résolu";
			default:
				return "";
		}
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
