package fr.nessar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Database {
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("must have h2 driver");
			return null;
		}
		try {
			conn = DriverManager.getConnection(Staff.getUrlDB());
		} catch (SQLException ex) {
			System.out.println("There was a problem connecting database.");
		}
		return conn;
	}

	public static void initializeDatabase() {
		try {
			Connection connection = Database.getConnection();
			Statement statement = connection.createStatement();
			statement.execute(
					"CREATE TABLE IF NOT EXISTS reports(ID INT AUTO_INCREMENT PRIMARY KEY, report BOOLEAN, reportStatus TINYINT, reportTime BIGINT, reportReason VARCHAR(256), reported JAVA_OBJECT, reporter JAVA_OBJECT);");
			statement.execute(
					"CREATE TABLE IF NOT EXISTS chatHistory(messageId INT AUTO_INCREMENT PRIMARY KEY, messageDate BIGINT, messageAuthor UUID, message VARCHAR(256));");
			statement.execute(
					"CREATE TABLE IF NOT EXISTS templates(id INT AUTO_INCREMENT PRIMARY KEY, templateName VARCHAR(32), message VARCHAR(256), duration BIGINT, type TINYINT);");
			statement.execute(
					"CREATE TABLE IF NOT EXISTS punishments(id INT AUTO_INCREMENT PRIMARY KEY, punishedUUID UUID, message VARCHAR(256), punishmentType TINYINT, endtime BIGINT);");
			connection.close();
			Bukkit.getConsoleSender().sendMessage(Staff.getSTAFF_PREFIX() + "Database loaded");
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(Staff.getSTAFF_PREFIX() + "Couldn't load database :( " + e);
		}
	}

	public static List<Report> loadReportsFromDB() throws Exception {
		Connection connection = Database.getConnection();
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement("SELECT * FROM reports");
		} catch (SQLException e) {
			throw new Exception("Cannot get * from reports " + e);
		}
		ResultSet rows = statement.executeQuery();
		List<Report> ret = new ArrayList<Report>();
		while (rows.next()) {
			ret.add(new Report(rows.getObject("reporter", Player.class), rows.getObject("reported", Player.class),
					rows.getString("reportReason"), rows.getLong("reportTime"), rows.getBoolean("report")));
		}
		connection.close();
		return ret;
	}

	public static void addReportToDB(Report r) throws SQLException {
		Connection conn = Database.getConnection();
		PreparedStatement statement;
		statement = conn.prepareStatement(
				"INSERT INTO reports(report,reportStatus,reportTime,reportReason,reported, reporter) VALUES (?,?,?,?,?)");
		statement.setBoolean(0, r.isReport());
		statement.setInt(1, r.getStatus().getStatusCode());
		statement.setLong(2, r.getReportTime());
		statement.setString(3, r.getReportReason());
		statement.setObject(4, r.getReported());
		statement.setObject(4, r.getReporter());
		statement.execute();
		conn.close();
	}
}
