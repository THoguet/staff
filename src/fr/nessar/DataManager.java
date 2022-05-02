package fr.nessar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;

import com.google.gson.Gson;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;

public class DataManager {
	private Staff plugin = Staff.getPlugin(Staff.class);

	// Files & File Configs Here
	public File reportFile;

	public DataManager() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}

		this.reportFile = new File(plugin.getDataFolder(), "data.json");

		if (!this.reportFile.exists()) {
			try {
				this.reportFile.createNewFile();
				Bukkit.getServer().getConsoleSender()
						.sendMessage(Staff.getPREFIX() + ChatColor.GREEN + "The data.json file has been created");
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(Staff.getPREFIX() + ChatColor.RED + "Could not create the data.json file");
			}
		} else {
			Bukkit.getServer().getConsoleSender().sendMessage(Staff.getPREFIX() + ChatColor.GREEN + "data.json found");
		}
	}

	public void saveReports(List<Report> Reports) {
		Gson gson = new Gson();
		try {
			Files.write(this.reportFile.toPath(), gson.toJson(Reports).getBytes());
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(Staff.getPREFIX() + ChatColor.RED + "Couldn't save in the data.json file");
		}
	}

	public List<Report> loadReports() {
		Gson gson = new Gson();
		List<Report> ret = null;
		Report[] Reports = null;
		if (reportFile.exists()) {
			Reader reader;
			try {
				reader = new FileReader(reportFile);
			} catch (FileNotFoundException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(Staff.getPREFIX() + ChatColor.RED + "Couldn't found data.json");
				return new ArrayList<Report>();
			}
			Reports = gson.fromJson(reader, Report[].class);
			if (Reports != null) {
				ret = new ArrayList<Report>(Arrays.asList(Reports));
			} else {
				ret = new ArrayList<Report>();
			}
		}
		return ret;
	}

}
