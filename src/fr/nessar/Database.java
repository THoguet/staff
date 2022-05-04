package fr.nessar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
					"CREATE TABLE IF NOT EXISTS templates(id INT AUTO_INCREMENT PRIMARY KEY, templateName VARCHAR(32), message VARCHAR(256), duration BIGINT, type TINYINT, itemName VARCHAR(64));");
			statement.execute(
					"CREATE TABLE IF NOT EXISTS punishments(id INT AUTO_INCREMENT PRIMARY KEY, punishedUUID UUID, message VARCHAR(256), punishmentType TINYINT, endtime BIGINT, startTime BIGINT, punisherUUID UUID, reportID INT);");
			connection.close();
			Bukkit.getConsoleSender().sendMessage(Staff.getSTAFF_PREFIX() + "Database loaded");
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(Staff.getSTAFF_PREFIX() + "Couldn't load database :( " + e);
		}
	}

	public static List<Template> loadTemplatesFromDB() throws Exception {
		Connection connection = Database.getConnection();
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement("SELECT * FROM templates");
		} catch (SQLException e) {
			throw new Exception("Cannot get * from templates " + e);
		}
		ResultSet rows = statement.executeQuery();
		List<Template> ret = new ArrayList<Template>();
		while (rows.next()) {
			ret.add(new Template(rows.getString("itemName"), rows.getString("templateName"), rows.getString("message"),
					rows.getInt("type"), rows.getLong("duration")));
		}
		connection.close();
		return ret;
	}

	public static List<Punishment> loadPunishmentsFromDB() throws Exception {
		Connection connection = Database.getConnection();
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement("SELECT * FROM punishments");
		} catch (SQLException e) {
			throw new Exception("Cannot get * from punishments " + e);
		}
		ResultSet rows = statement.executeQuery();
		List<Punishment> ret = new ArrayList<Punishment>();
		while (rows.next()) {
			ret.add(new Punishment(rows.getObject("punishedUUID", UUID.class),
					rows.getObject("punisherUUID", UUID.class),
					rows.getString("message"), rows.getInt("pType"), rows.getLong("startTime"), rows.getLong("endtime"),
					rows.getInt("reportID")));
		}
		connection.close();
		return ret;
	}

	public static List<ChatMessage> loadchatHistoryFromDB(long maxDate, int quantity, UUID from) throws Exception {
		Connection connection = Database.getConnection();
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(
					"SELECT * FROM (SELECT * FROM chatHistory WHERE messageDate < ? AND messageAuthor=? ORDER BY id DESC LIMIT ?) sub ORDER BY id ASC");
			statement.setLong(0, maxDate);
			statement.setObject(1, from);
			statement.setInt(2, quantity);
		} catch (SQLException e) {
			throw new Exception("Cannot get chats from chatHistory " + e);
		}
		ResultSet rows = statement.executeQuery();
		List<ChatMessage> ret = new ArrayList<ChatMessage>(quantity);
		while (rows.next()) {
			ret.add(new ChatMessage(rows.getObject("messageAuthor", UUID.class), rows.getLong("messageDate"),
					rows.getString("message")));
		}
		connection.close();
		return ret;
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

	public static void addTemplateToDB(Template template) throws SQLException {
		Connection conn = Database.getConnection();
		PreparedStatement statement;
		statement = conn.prepareStatement(
				"INSERT INTO templates(templateName,message,duration,type,itemName) VALUES (?,?,?,?)");
		statement.setString(0, template.getName());
		statement.setString(1, template.getMessage());
		statement.setLong(2, template.getDuration());
		statement.setInt(3, template.getpType().getPunishCode());
		statement.setString(4, template.getItem().toString());
		statement.execute();
		conn.close();
	}

	public static void addChatMessageToDB(ChatMessage chat) throws SQLException {
		Connection conn = Database.getConnection();
		PreparedStatement statement;
		statement = conn.prepareStatement(
				"INSERT INTO chatHistory(messageDate,messageAuthor,message) VALUES (?,?,?)");
		statement.setLong(0, chat.getMessageDate());
		statement.setObject(1, chat.getAuthorUUID());
		statement.setString(2, chat.getMessage());
		statement.execute();
		conn.close();
	}

	public static void addPunishmentToDB(Punishment punishment) throws SQLException {
		Connection conn = Database.getConnection();
		PreparedStatement statement;
		statement = conn.prepareStatement(
				"INSERT INTO punishments(punishedUUID,message,punishmentType,endtime,startTime, punisherUUID,reportID) VALUES (?,?,?,?,?,?)");
		statement.setObject(0, punishment.getPunishedUUID());
		statement.setString(1, punishment.getMessage());
		statement.setInt(2, punishment.getpType().getPunishCode());
		statement.setLong(3, punishment.getEndTime());
		statement.setLong(4, punishment.getStartTime());
		statement.setObject(5, punishment.getPunisherUUID());
		statement.setInt(6, punishment.getReportID());
		statement.execute();
		conn.close();
	}
}
