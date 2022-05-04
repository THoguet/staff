package fr.nessar;

public enum PunishType {
	NULL(0),
	MUTE(1),
	BAN(2);

	private final int punishCode;

	private PunishType(int code) {
		this.punishCode = code;
	}

	public int getPunishCode() {
		return this.punishCode;
	}

	public boolean isBanned() {
		return this.punishCode == 2;
	}

	public boolean isMuted() {
		return this.punishCode == 1;
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
