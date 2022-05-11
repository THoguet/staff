package fr.nessar;

public enum PunishType {
	NULL(0),
	MUTE(1),
	BAN(2),
	REPORT(3),
	TICKET(4),
	COOLDOWN(5);

	private final int punishCode;

	private PunishType(int code) {
		this.punishCode = code;
	}

	public int getPunishCode() {
		return this.punishCode;
	}

	public boolean isBanned() {
		return this == BAN;
	}

	public boolean isMuted() {
		return this == MUTE;
	}

	public boolean isReport() {
		return this == REPORT;
	}

	public boolean isTicket() {
		return this == TICKET;
	}

	public boolean isCoolDown() {
		return this == COOLDOWN;
	}

	public static PunishType valueOf(int punishCode) {
		for (PunishType pType : PunishType.values()) {
			if (pType.getPunishCode() == punishCode) {
				return pType;
			}
		}
		return null;
	}

}
