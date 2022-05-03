package fr.nessar;

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
