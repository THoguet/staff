package fr.nessar.Menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import fr.nessar.Report;
import fr.nessar.Staff;
import fr.nessar.StaffMod;
import net.md_5.bungee.api.ChatColor;

public class ReportList implements InventoryHolder {

	private Staff plugin;
	private Player user;
	private int page;

	public ReportList(Staff plugin, Player user, int page) {
		this.page = page;
		this.user = user;
		this.plugin = plugin;
	}

	@Override
	public Inventory getInventory() {
		boolean permReport = false;
		boolean permTicket = false;
		List<Report> reportsList = plugin.getReports();
		ItemStack reportOrTickets = new ItemStack(Material.BOOKSHELF);
		String nameReportsOrTickets = "";
		if (this.user.hasPermission("staff.tickets") && this.user.hasPermission("staff.reports"))
			nameReportsOrTickets = "Tickets / Reports";
		else {
			if (this.user.hasPermission("staff.tickets")) {
				nameReportsOrTickets = "Tickets";
				permTicket = true;
			}
			if (this.user.hasPermission("staff.reports")) {
				nameReportsOrTickets = "Reports";
				permReport = true;
			}
		}
		Inventory gui = Static.getBase(this);
		gui.setItem(4, StaffMod.setNameItem(ChatColor.GOLD + nameReportsOrTickets, reportOrTickets));
		ItemStack archives = StaffMod.setNameItem(ChatColor.GOLD + "Archives", new ItemStack(Material.BOOKSHELF));
		gui.setItem(8, archives);
		for (int i = 28 * (page - 1); i < reportsList.size() && i < 28 * page; i++) {
			Report report = reportsList.get(i);
			if (report.isReport() != permReport && report.isTicket() != permTicket)
				continue;
			int caseNumber = 18 + i - (28 * (page - 1));
			ItemStack itemReport = Static.getReportItem(report, i);
			gui.setItem(caseNumber, itemReport);
		}
		return gui;
	}

}
