package fr.nessar;

import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nessar.Menu.EditReport;
import fr.nessar.Menu.Freeze;
import fr.nessar.Menu.ReportList;
import fr.nessar.Menu.StaffList;
import fr.nessar.Menu.Static;

public class InventoryEvents implements Listener {

	private Staff plugin;

	public InventoryEvents(Staff plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onInventoryDragEvent(InventoryDragEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (event.getInventory().getHolder() instanceof Freeze) {
			event.setCancelled(true);
			Static.sendFrozeMessage(p);
		} else if (event.getInventory().getHolder() instanceof StaffList) {
			// test if the drag hit any menu slot
			event.setCancelled(isDragOnCreatedInv(event));
		} else if (event.getInventory().getHolder() instanceof ReportList) {
			event.setCancelled(isDragOnCreatedInv(event));
		}
	}

	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		Player p = (Player) event.getPlayer();
		if (event.getInventory().getHolder() instanceof Freeze) {
			if (this.plugin.isFrozen(p) != -1) {
				Bukkit.getServer().getScheduler().runTask(this.plugin, new Runnable() {
					@Override
					public void run() {
						p.openInventory(new Freeze().getInventory());
					}
				});
				Static.sendFrozeMessage(p);
			}
		}
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			int indexStaffMod = plugin.isInStaffMod(p);
			if (indexStaffMod != -1) {
				if (plugin.getInStaffMod().get(indexStaffMod).isStaffInv()) {
					event.setCancelled(true);
					p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED
							+ "Vous ne pouvez pas interagir avec le monde en staff mod.");
					plugin.getInStaffMod().get(indexStaffMod).setItem();
				} else {
					if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null
							&& event.getCurrentItem().getItemMeta().getDisplayName() != null
							&& event.getCurrentItem().getItemMeta().getDisplayName().equals(
									ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off")) {
						event.setCancelled(true);
						plugin.getInStaffMod().get(indexStaffMod).setItem();
					}
				}
			}
			int caseNumber = -999;
			caseNumber = event.getRawSlot();
			ItemStack item = event.getCurrentItem();
			ItemMeta itemM = null;
			if (item != null)
				itemM = item.getItemMeta();
			if (event.getClickedInventory() == null) {
				return;
			}
			if (event.getClickedInventory().getHolder() instanceof Freeze) {
				event.setCancelled(true);
				plugin.getFrozen().get(plugin.isFrozen(p)).setRedPaneInv(p);
				Static.sendFrozeMessage(p);
				return;
			} else if (event.getClickedInventory().getHolder() instanceof ReportList) {
				List<Report> reports = plugin.getReports();
				int indexReportList = caseNumber - 18;
				if (indexReportList >= 0 && indexReportList < reports.size()) {
					p.openInventory(
							new EditReport(reports.get(indexReportList), indexReportList, plugin).getInventory());
				}
			} else if (event.getClickedInventory().getHolder() instanceof StaffList) {
				List<OfflinePlayer> staffList = plugin.getStaff();
				int indexStaffList = caseNumber - 18;
				if (indexStaffList >= 0 && indexStaffList < staffList.size()) {
					Player staff = staffList.get(indexStaffList).getPlayer();
					if (staff == null) {
						p.sendMessage(
								Staff.getSTAFF_PREFIX() + ChatColor.RED
										+ "Le joueur est Hors-Ligne, impossible de se téléporter à lui.");
					} else {
						p.teleport(staff.getLocation());
						p.sendMessage(Staff.getSTAFF_PREFIX() + "Vous avez bien été TP à "
								+ staff.getName() + ".");
					}
				}
			} else if (event.getClickedInventory().getHolder() instanceof EditReport) {
				if (caseNumber == 0) {
					p.openInventory(new ReportList(plugin, p, 1).getInventory());
				}
			} else if (plugin.isFrozen(p) != -1) {
				event.setCancelled(true);
				plugin.getFrozen().get(plugin.isFrozen(p)).setRedPaneInv(p);
				Static.sendFrozeMessage(p);
				return;
			}
			if (caseNumber >= 0 && caseNumber < 54) {
				event.setCancelled(true);
				if (itemM != null && itemM.getDisplayName().contains("Fermer")) {
					p.closeInventory();
				}
			}
		}
	}

	@EventHandler
	public void onInventoryInteractEvent(InventoryInteractEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (event.getInventory().getHolder() instanceof Freeze) {
			event.setCancelled(true);
			Static.sendFrozeMessage(p);
		}
	}

	private boolean isDragOnCreatedInv(InventoryDragEvent event) {
		return event.getRawSlots().stream().anyMatch(
				value -> IntStream.rangeClosed(0, 54).anyMatch(rangeValue -> rangeValue == value));
	}

}
