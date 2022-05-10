package fr.nessar.Menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import fr.nessar.Staff;
import fr.nessar.StaffMod;
import net.md_5.bungee.api.ChatColor;

public class StaffList implements InventoryHolder {

	private static final int NBHEADPERPAGE = 27;
	private static final int FIRSTCASEREPORT = 18;

	private int page;
	private Staff plugin;
	private Player user;

	public StaffList(int page, Staff plugin, Player user) {
		this.page = page;
		this.plugin = plugin;
		this.user = user;
	}

	@Override
	public Inventory getInventory() {
		List<OfflinePlayer> staffList = this.plugin.getStaff();
		final int NBSTAFF = staffList.size();
		String title = ChatColor.GOLD + "Staff " + ChatColor.GRAY + "> " + ChatColor.YELLOW + "Page " + page;
		Inventory gui = Static.getBase(this, title);
		ItemStack teteHero = StaffMod.setNameItem(ChatColor.GOLD + "Staff en ligne",
				Static.getPlayerHead("MHF_Herobrine"));
		gui.setItem(4, teteHero);
		ItemStack reportOrTickets = new ItemStack(Material.BOOKSHELF);
		String nameReportsOrTickets = "";
		if (this.user.hasPermission("staff.tickets") && this.user.hasPermission("staff.reports"))
			nameReportsOrTickets = "Tickets / Reports";
		else {
			if (this.user.hasPermission("staff.tickets"))
				nameReportsOrTickets = "Tickets";
			if (this.user.hasPermission("staff.reports"))
				nameReportsOrTickets = "Reports";
		}
		ItemStack prevpage = StaffMod.setNameItem(ChatColor.GOLD + "Page précédente", new ItemStack(Material.FEATHER));
		ItemStack nextpage = StaffMod.setNameItem(ChatColor.GOLD + "Page suivante", new ItemStack(Material.FEATHER));
		if (page != 1)
			gui.setItem(3, prevpage);
		if (NBSTAFF - NBHEADPERPAGE * (page - 1) > NBHEADPERPAGE)
			gui.setItem(5, nextpage);
		reportOrTickets = StaffMod.setNameItem(ChatColor.GOLD + nameReportsOrTickets, reportOrTickets);
		gui.setItem(0, reportOrTickets);
		ItemStack archives = StaffMod.setNameItem(ChatColor.GOLD + "Archives", new ItemStack(Material.BOOKSHELF));
		gui.setItem(8, archives);
		int caseNumber = FIRSTCASEREPORT;
		for (int i = NBHEADPERPAGE * (page - 1); i < staffList.size() && i < NBHEADPERPAGE * page; i++) {
			List<String> itemLore = new ArrayList<>();
			OfflinePlayer playerStaff = staffList.get(i);
			boolean isStaffOnline = staffList.get(i).isOnline();
			itemLore.add(ChatColor.GRAY + "En ligne: "
					+ (isStaffOnline ? ChatColor.GREEN + "Oui" : ChatColor.DARK_RED + "Non"));
			if (isStaffOnline) {
				boolean playerStaffinStaffMod = plugin.isInStaffMod(playerStaff.getPlayer()) != -1;
				itemLore.add(ChatColor.GRAY + "Staff mod: "
						+ (playerStaffinStaffMod ? ChatColor.GREEN + "On" : ChatColor.DARK_RED + "Off"));
				itemLore.add(" ");
				itemLore.add(ChatColor.GOLD + "Clic " + ChatColor.GRAY + "pour se tp à lui");
			}
			ItemStack staffHead = StaffMod.setLoreitem(itemLore,
					StaffMod.setNameItem(ChatColor.WHITE + playerStaff.getName(),
							Static.getPlayerHead(playerStaff.getName())));
			gui.setItem(caseNumber, staffHead);
			caseNumber++;
		}
		return gui;
	}

}
