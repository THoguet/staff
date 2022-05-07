package fr.nessar.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import fr.nessar.Report;
import fr.nessar.Staff;
import fr.nessar.StaffMod;
import net.md_5.bungee.api.ChatColor;

public class EditReport implements InventoryHolder {

	private Report r;
	private int indexReport;
	private Staff plugin;

	public EditReport(Report r, int indexReport, Staff plugin) {
		this.r = r;
		this.indexReport = indexReport;
		this.plugin = plugin;
	}

	@Override
	public Inventory getInventory() {
		Inventory gui = Static.getBase(this);
		ItemStack backToReportList = StaffMod.setNameItem(ChatColor.GOLD + "Liste des reports",
				new ItemStack(Material.BOOKSHELF));
		gui.setItem(0, backToReportList);
		ItemStack reportitem = Static.getReportItem(this.r, this.indexReport);
		gui.setItem(4, reportitem);
		gui.setItem(18, reportitem);
		ItemStack reporterHead = Static.getReportHead(true, r.getReporter().getName(), r.isReport(),
				UUID.fromString(r.getReporter().getUniqueId()), this.plugin);
		gui.setItem(21, reporterHead);
		ItemStack abusiveReport = StaffMod.setNameItem(ChatColor.YELLOW + "Signalement abusif",
				new ItemStack(Material.GOLD_AXE));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD + "Clic" + ChatColor.GRAY
				+ " pour sanctionner le joueur ayant envoyé ce report: " + ChatColor.GREEN + r.getReporter().getName());
		abusiveReport = StaffMod.setLoreitem(lore, abusiveReport);
		gui.setItem(22, abusiveReport);
		ItemStack reportedHead = Static.getReportHead(false, r.getReported().getName(), r.isReport(),
				UUID.fromString(r.getReported().getUniqueId()), this.plugin);
		gui.setItem(23, reportedHead);
		ItemStack data = Static.getSavedData(r);
		gui.setItem(26, data);
		ItemStack waiting = new ItemStack(Material.STAINED_CLAY, 1, (byte) 3);
		ItemStack inProgress = new ItemStack(Material.STAINED_CLAY, 1, (byte) 4);
		ItemStack important = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
		ItemStack classed = new ItemStack(Material.STAINED_CLAY, 1, (byte) 9);
		ItemStack delete = new ItemStack(Material.FLINT_AND_STEEL);
		gui.setItem(29, waiting);
		gui.setItem(30, inProgress);
		gui.setItem(31, important);
		gui.setItem(32, classed);
		gui.setItem(33, delete);
		return gui;
	}

}
