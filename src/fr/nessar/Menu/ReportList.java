package fr.nessar.Menu;

import java.util.ArrayList;
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
	private boolean archive;
	private boolean only_Report;
	private boolean only_Ticket;
	private boolean only_important;

	public ReportList(Staff plugin, Player user, int page, boolean archive, boolean only_Report, boolean only_Ticket,
			boolean only_important) {
		this.page = page;
		this.user = user;
		this.plugin = plugin;
		this.archive = archive;
		this.only_Report = only_Report;
		this.only_Ticket = only_Ticket;
		this.only_important = only_important;
	}

	@Override
	public Inventory getInventory() {
		ItemStack filterItem;
		ItemStack ImportantItem;
		List<String> lorefilter = new ArrayList<>();
		lorefilter.add(ChatColor.GOLD + "Clic" + ChatColor.GRAY + " pour changer le filtre");
		if (only_Report == only_Ticket)
			filterItem = StaffMod.setNameItem(ChatColor.GOLD + "Pas de filtre", new ItemStack(Material.TORCH));
		else if (only_Report)
			filterItem = StaffMod.setNameItem(ChatColor.GOLD + "Seulement les reports",
					new ItemStack(Material.REDSTONE_TORCH_ON));
		else
			filterItem = StaffMod.setNameItem(ChatColor.GOLD + "Seulement les tickets", new ItemStack(Material.LEVER));
		filterItem = StaffMod.setLoreitem(lorefilter, filterItem);
		if (only_important)
			ImportantItem = StaffMod.setNameItem(ChatColor.GOLD + "Seulement important",
					new ItemStack(Material.REDSTONE));
		else
			ImportantItem = StaffMod.setNameItem(ChatColor.GOLD + "Pas de filtre", new ItemStack(Material.SULPHUR));
		ImportantItem = StaffMod.setLoreitem(lorefilter, ImportantItem);
		boolean permReport = false;
		boolean permTicket = false;
		List<Report> reportsList = plugin.getReports();
		ItemStack reportOrTickets = new ItemStack(Material.BOOKSHELF);
		String nameReportsOrTickets = "";
		if (this.user.hasPermission("staff.tickets") && this.user.hasPermission("staff.reports")) {
			nameReportsOrTickets = "Tickets / Reports";
			permTicket = true;
			permReport = true;
		} else {
			if (this.user.hasPermission("staff.tickets")) {
				nameReportsOrTickets = "Tickets";
				permTicket = true;
			}
			if (this.user.hasPermission("staff.reports")) {
				nameReportsOrTickets = "Reports";
				permReport = true;
			}
		}
		String title = ChatColor.GOLD.toString();
		if (archive) {
			title += "Archives";
		} else if (permReport && permTicket)
			title += "Reports / Tickets";
		else if (permReport)
			title += "Reports";
		else if (permTicket)
			title += "Tickets";
		title += ChatColor.GRAY + " > ";
		title += ChatColor.YELLOW + "Page " + this.page;
		Inventory gui = Static.getBase(this, title);
		gui.setItem(52, ImportantItem);
		gui.setItem(53, filterItem);
		gui.setItem(4, StaffMod.setNameItem(ChatColor.GOLD + nameReportsOrTickets, reportOrTickets));
		ItemStack archives = StaffMod.setNameItem(ChatColor.GOLD + (this.archive ? "Sortir des archives" : "Archives"),
				new ItemStack(Material.BOOKSHELF));
		gui.setItem(8, archives);
		int caseNumber = 18 - (28 * (page - 1));
		for (int i = 28 * (page - 1); i < reportsList.size() && i < 28 * page; i++) {
			Report report = reportsList.get(i);
			// filter reports / tickets by filter and perms and archives
			boolean filter = only_Ticket != only_Report
					&& (report.isReport() == only_Ticket || report.isTicket() == only_Report);
			boolean filterImportant = only_important && !report.getStatus().isImportant();
			if (filter || filterImportant || (report.isReport() != permReport && report.isTicket() != permTicket)
					|| report.getStatus().isClassed() != archive)
				continue;
			ItemStack itemReport = Static.getReportItem(report, i);
			gui.setItem(caseNumber, itemReport);
			caseNumber++;
		}
		return gui;
	}

}
