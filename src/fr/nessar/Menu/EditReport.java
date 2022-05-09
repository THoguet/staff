package fr.nessar.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import fr.nessar.Report;
import fr.nessar.ReportStatus;
import fr.nessar.Staff;
import fr.nessar.StaffMod;
import net.md_5.bungee.api.ChatColor;

public class EditReport implements InventoryHolder {

	private Report r;
	private int indexReport;
	private Staff plugin;
	private boolean fromArchive;

	public EditReport(Report r, int indexReport, Staff plugin) {
		this.r = r;
		this.indexReport = indexReport;
		this.plugin = plugin;
		this.fromArchive = false;
	}

	public EditReport(Report r, int indexReport, Staff plugin, boolean fromArchive) {
		this(r, indexReport, plugin);
		this.fromArchive = true;
	}

	@Override
	public Inventory getInventory() {
		String title;
		if (r.isReport())
			title = ChatColor.RED + "Report ";
		else
			title = ChatColor.BLUE + "Ticket ";
		title += ChatColor.GRAY + "#" + indexReport;
		Inventory gui = Static.getBase(this, title);
		ItemStack back;
		if (!fromArchive)
			back = StaffMod.setNameItem(ChatColor.GOLD + "Liste des reports",
					new ItemStack(Material.BOOKSHELF));
		else
			back = StaffMod.setNameItem(ChatColor.GOLD + "Retour aux archives",
					new ItemStack(Material.BOOKSHELF));
		gui.setItem(0, back);
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
				+ " pour sanctionner le joueur ");
		lore.add(ChatColor.GRAY + "ayant envoyé ce report: " + ChatColor.GREEN + r.getReporter().getName());
		abusiveReport = StaffMod.setLoreitem(lore, abusiveReport);
		if (r.isReport()) {
			ItemStack reportedHead = Static.getReportHead(false, r.getReported().getName(), r.isReport(),
					UUID.fromString(r.getReported().getUniqueId()), this.plugin);
			gui.setItem(23, reportedHead);
			ItemStack data = Static.getSavedData(r);
			gui.setItem(26, data);
		}
		String startStatusLore = ChatColor.GOLD + "Clic " + ChatColor.GRAY + "pour ";
		List<String> loreStatus = new ArrayList<>();
		loreStatus.add(startStatusLore + "marquer le statut du");
		loreStatus.add(ChatColor.GRAY + "signalement comme: " + ReportStatus.WAITING.getStatusName(r.isReport()));
		ItemStack waiting = StaffMod.setNameItem(
				ChatColor.YELLOW + "Marquer comme: " + ReportStatus.WAITING.getStatusName(r.isReport()),
				new ItemStack(Material.STAINED_CLAY, 1, (byte) 5));
		waiting = StaffMod.setLoreitem(loreStatus, waiting);
		loreStatus.set(1, ChatColor.GRAY + "signalement comme: " + ReportStatus.INPROGRESS.getStatusName(r.isReport()));
		ItemStack inProgress = StaffMod.setNameItem(
				ChatColor.YELLOW + "Marquer comme: " + ReportStatus.INPROGRESS.getStatusName(r.isReport()),
				new ItemStack(Material.STAINED_CLAY, 1, (byte) 4));
		inProgress = StaffMod.setLoreitem(loreStatus, inProgress);
		loreStatus.set(1, ChatColor.GRAY + "signalement comme: " + ReportStatus.IMPORTANT.getStatusName(r.isReport()));
		ItemStack important = StaffMod.setNameItem(
				ChatColor.YELLOW + "Marquer comme: " + ReportStatus.IMPORTANT.getStatusName(r.isReport()),
				new ItemStack(Material.TNT));
		important = StaffMod.setLoreitem(loreStatus, important);
		ItemStack glassSep = StaffMod.setNameItem(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));
		String reportNamePlusIndex = "";
		if (r.isReport())
			reportNamePlusIndex = ChatColor.RED + "Report " + ChatColor.GRAY + "#" + indexReport;
		else
			reportNamePlusIndex = ChatColor.BLUE + "Ticket " + ChatColor.GRAY + "#" + indexReport;
		loreStatus.set(0, ChatColor.GOLD + "Clic " + ChatColor.GRAY + "pour traiter " + reportNamePlusIndex);
		loreStatus.set(1, ChatColor.GRAY + "comme " + ReportStatus.CLASSED_FALSE.getStatusName(r.isReport()));
		ItemStack classed_false = StaffMod.setNameItem(
				ChatColor.YELLOW + "Traiter comme: " + ReportStatus.CLASSED_FALSE.getStatusName(r.isReport()),
				new ItemStack(Material.STAINED_CLAY, 1, (byte) 14));
		classed_false = StaffMod.setLoreitem(loreStatus, classed_false);
		loreStatus.set(1, ChatColor.GRAY + "comme " + ReportStatus.CLASSED_NOTSURE.getStatusName(r.isReport()));
		ItemStack classed_notsure = StaffMod.setNameItem(
				ChatColor.YELLOW + "Traiter comme: " + ReportStatus.CLASSED_NOTSURE.getStatusName(r.isReport()),
				new ItemStack(Material.STAINED_CLAY, 1, (byte) 1));
		classed_notsure = StaffMod.setLoreitem(loreStatus, classed_notsure);
		loreStatus.set(1, ChatColor.GRAY + "comme " + ReportStatus.CLASSED_TRUE.getStatusName(r.isReport()));
		ItemStack classed_true = StaffMod.setNameItem(
				ChatColor.YELLOW + "Traiter comme: " + ReportStatus.CLASSED_TRUE.getStatusName(r.isReport()),
				new ItemStack(Material.STAINED_CLAY, 1, (byte) 13));
		classed_true = StaffMod.setLoreitem(loreStatus, classed_true);
		switch (r.getStatus()) {
			case WAITING:
				waiting = Static.addGlow(waiting);
				break;
			case INPROGRESS:
				inProgress = Static.addGlow(inProgress);
				break;
			case IMPORTANT:
				important = Static.addGlow(important);
				break;
			case CLASSED_ABUSIVE:
				abusiveReport = Static.addGlow(abusiveReport);
				break;
			case CLASSED_FALSE:
				classed_false = Static.addGlow(classed_false);
				break;
			case CLASSED_NOTSURE:
				classed_notsure = Static.addGlow(classed_notsure);
				break;
			case CLASSED_TRUE:
				classed_true = Static.addGlow(classed_true);
				break;
			default:
				break;
		}
		gui.setItem(22, abusiveReport);
		gui.setItem(28, waiting);
		gui.setItem(29, inProgress);
		gui.setItem(30, important);
		gui.setItem(31, glassSep);
		gui.setItem(32, classed_false);
		gui.setItem(33, classed_notsure);
		gui.setItem(34, classed_true);
		return gui;
	}

}
