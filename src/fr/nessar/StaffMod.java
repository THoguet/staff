package fr.nessar;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import fr.nessar.Menu.ReportList;
import fr.nessar.Menu.StaffList;
import fr.nessar.Menu.Static;

public class StaffMod {
	private Player who;
	private Staff plugin;
	private SaveInventory savedInv;
	private ItemStack vanish;
	private ItemStack freeze;
	private ItemStack reportsOrStaff;
	private ItemStack only_important_item;
	private ItemStack statusListening;
	private ItemStack freeInv;
	private boolean vanished = false;
	private boolean isSneaking = false;
	private boolean only_important = false;
	private boolean only_ticket = false;
	private boolean only_report = false;
	private boolean staffInv = true;

	public StaffMod(Staff plugin, Player p) {
		this.plugin = plugin;
		this.who = p;
		this.savedInv = new SaveInventory(p);
		this.setItem();
		p.sendMessage(Staff.getSTAFF_PREFIX() + "Vous êtes desormais en staff mod !");
	}

	public void toggleSneakStatus() {
		this.isSneaking = !this.isSneaking;
		if (this.staffInv)
			this.setItem();
	}

	public void toggleStatusListenting() {
		if (!this.only_ticket && !this.only_report)
			this.only_ticket = true;
		else if (!this.only_report) {
			this.only_report = true;
			this.only_ticket = false;
		} else {
			this.only_report = false;
		}
		this.setItem();
	}

	public void toggleVanish() {
		if (this.vanished) {
			this.who.performCommand("v off");
			this.vanished = false;
		} else {
			this.who.performCommand("v on");
			this.vanished = true;
		}
		this.setItem();
	}

	public void toggleImportant() {
		this.only_important = !this.only_important;
		this.setItem();
	}

	public void toggleStaffInv() {
		this.staffInv = !this.staffInv;
		this.setItem();
	}

	public void toggleOrUseSlot(Material m) {
		if (m.equals(Material.INK_SACK))
			toggleVanish();
		else if (m.equals(Material.PAPER)) {
			this.who.openInventory(
					new ReportList(plugin, who, 1, false, only_report, only_ticket, only_important).getInventory());
		} else if (m.equals(Material.SKULL_ITEM)) {
			this.who.openInventory(new StaffList(1, plugin, who).getInventory());
		} else if (m.equals(Material.REDSTONE) || m.equals(Material.SULPHUR))
			toggleImportant();
		else if (m.equals(Material.TORCH) || m.equals(Material.LEVER) || m.equals(Material.REDSTONE_TORCH_ON))
			toggleStatusListenting();
		else if (m.equals(Material.BLAZE_ROD) || m.equals(Material.STICK))
			toggleStaffInv();
	}

	public static ItemStack setNameItem(String name, ItemStack item) {
		ItemMeta itemM = item.getItemMeta();
		itemM.setDisplayName(name);
		item.setItemMeta(itemM);
		return item;
	}

	public static ItemStack setLoreitem(List<String> lore, ItemStack item) {
		ItemMeta itemM = item.getItemMeta();
		itemM.setLore(lore);
		item.setItemMeta(itemM);
		return item;
	}

	public void updateItems() {
		if (this.staffInv) {
			if (this.vanished) {
				this.vanish = StaffMod.setNameItem(
						ChatColor.GOLD + "Vanish" + ChatColor.GRAY + ": " + ChatColor.GREEN + "On",
						new Dye(DyeColor.LIME).toItemStack(1));
			} else {
				this.vanish = StaffMod.setNameItem(
						ChatColor.GOLD + "Vanish" + ChatColor.GRAY + ": " + ChatColor.RED + "Off",
						new Dye(DyeColor.GRAY).toItemStack(1));
			}
			this.freeze = StaffMod.setNameItem(ChatColor.BLUE + "Freeze", new ItemStack(Material.ICE));
			if (isSneaking) {
				this.reportsOrStaff = setNameItem(ChatColor.GOLD + "Staff list",
						Static.getPlayerHead("MHF_Herobrine"));
			} else {
				ReportStatus minimalStatus = ReportStatus.WAITING;
				if (this.only_important) {
					minimalStatus = ReportStatus.IMPORTANT;
				}
				int nbReport = this.plugin.getNbReport(minimalStatus, 0, this.getNbReportReportArg());
				this.reportsOrStaff = StaffMod.setNameItem(this.getItemNamePaper(minimalStatus),
						new ItemStack(Material.PAPER, nbReport));
			}
			if (this.only_important) {
				this.only_important_item = setNameItem(ChatColor.GOLD + "Only Important",
						new ItemStack(Material.REDSTONE));
			} else {
				this.only_important_item = setNameItem(ChatColor.GOLD + "Not Only Important",
						new ItemStack(Material.SULPHUR));
			}
			if (!only_ticket && !only_report) {
				this.statusListening = setNameItem(ChatColor.GOLD + "All waiting", new ItemStack(Material.TORCH));
			} else if (only_ticket) {
				this.statusListening = setNameItem(ChatColor.GOLD + "Only Ticket", new ItemStack(Material.LEVER));
			} else {
				this.statusListening = setNameItem(ChatColor.GOLD + "Only report",
						new ItemStack(Material.REDSTONE_TORCH_ON));
			}
			this.freeInv = setNameItem(ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.GREEN + "On",
					new ItemStack(Material.BLAZE_ROD));
		} else {
			this.freeInv = setNameItem(ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off",
					new ItemStack(Material.STICK));
			this.vanish = null;
			this.freeze = null;
			this.reportsOrStaff = null;
			this.only_important_item = null;
			this.statusListening = null;
		}
	}

	public String getItemNamePaper(ReportStatus minimalStatus) {
		String ret = ChatColor.GOLD + "" + ChatColor.BOLD;
		if (this.only_report) {
			ret += "Report list ";
		} else if (this.only_ticket) {
			ret += "Ticket list ";
		} else {
			ret += "Report & Ticket list ";
		}
		return ret += "(" + ChatColor.GRAY + ChatColor.BOLD.toString()
				+ String.valueOf(this.plugin.getNbReport(minimalStatus, 0, this.getNbReportReportArg()))
				+ ChatColor.GOLD + ChatColor.GOLD + ChatColor.BOLD.toString() + ")";
	}

	public void setItem() {
		SaveInventory.clear(this.who);
		this.updateItems();
		ItemStack[] listItem = { this.vanish, this.freeze, null, null, this.reportsOrStaff, null,
				this.only_important_item, this.statusListening, this.freeInv };
		for (int i = 0; i < listItem.length; i++) {
			if (listItem[i] != null) {
				this.who.getInventory().setItem(i, listItem[i]);
			} else if (this.staffInv) {
				this.who.getInventory().setItem(i, new ItemStack(Material.AIR));
			}
		}
		this.who.updateInventory();
	}

	public boolean isHim(Player p) {
		return this.who.getUniqueId().equals(p.getUniqueId());
	}

	public Player getPlayer() {
		return this.who;
	}

	public boolean isStaffInv() {
		return this.staffInv;
	}

	public int getNbReportReportArg() {
		return this.only_ticket ? 0 : this.only_report ? 1 : 2;
	}

	public void disableStaffMod() {
		this.savedInv.loadInventory();
		this.who.sendMessage(Staff.getSTAFF_PREFIX() + "Vous n'êtes plus en staff mod !");
	}
}
