package fr.nessar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
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
		int indexStaffMod = plugin.isInStaffMod(p);
		if (indexStaffMod != -1) {
			if (plugin.getInStaffMod().get(indexStaffMod).isStaffInv()) {
				event.setCancelled(true);
				p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED
						+ "Vous ne pouvez pas interagir avec le monde en staff mod.");
			} else {
				if (event.getOldCursor().getItemMeta().getDisplayName().equals(
						ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
					event.setCancelled(true);
			}
		}
		int indexFrozen = plugin.isFrozen(p);
		if (indexFrozen != -1) {
			event.setCancelled(true);
			Static.sendFrozeMessage(p);
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
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getWhoClicked();
		int indexStaffMod = plugin.isInStaffMod(p);
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
			final int page = Staff.getNumberAtEndStr(event.getInventory().getName());
			boolean isArchive = !event.getInventory().getItem(8).getItemMeta().getDisplayName()
					.contains("Archives");
			boolean only_Report = event.getInventory().getItem(53).getItemMeta().getDisplayName()
					.contains("report");
			boolean only_Ticket = event.getInventory().getItem(53).getItemMeta().getDisplayName()
					.contains("ticket");
			boolean important = event.getInventory().getItem(52).getItemMeta().getDisplayName()
					.contains("important");
			if (caseNumber == 8) {
				p.openInventory(new ReportList(plugin, p, 1, !isArchive, only_Report, only_Ticket, important)
						.getInventory());
			} else if (caseNumber == 52) {
				p.openInventory(new ReportList(plugin, p, page, isArchive, only_Report, only_Ticket, !important)
						.getInventory());
			} else if (caseNumber == 53) {
				if (only_Report == only_Ticket) {
					p.openInventory(new ReportList(plugin, p, page, isArchive, only_Report, !only_Ticket, important)
							.getInventory());
				} else if (only_Ticket) {
					p.openInventory(new ReportList(plugin, p, page, isArchive, !only_Report, !only_Ticket, important)
							.getInventory());
				} else {
					p.openInventory(new ReportList(plugin, p, page, isArchive, !only_Report, only_Ticket, important)
							.getInventory());
				}
			} else if (caseNumber == 3) {
				if (item != null && !item.getType().equals(Material.AIR)) {
					p.openInventory(new ReportList(plugin, p, page - 1, isArchive, only_Report, only_Ticket, important)
							.getInventory());
				}
			} else if (caseNumber == 5) {
				if (item != null && !item.getType().equals(Material.AIR)) {
					p.openInventory(new ReportList(plugin, p, page + 1, isArchive, only_Report, only_Ticket, important)
							.getInventory());
				}
			} else {
				List<Report> reports = plugin.getReports();
				if (caseNumber >= 18 && caseNumber <= 44) {
					if (item != null && !item.getType().equals(Material.AIR)) {
						int reportNumber = Staff.getNumberAtEndStr(itemM.getDisplayName());
						p.openInventory(
								new EditReport(reports.get(reportNumber), reportNumber, plugin, isArchive)
										.getInventory());
					}
				}
			}
		} else if (event.getClickedInventory().getHolder() instanceof StaffList) {
			final int page = Staff.getNumberAtEndStr(event.getInventory().getName());
			if (caseNumber == 0) {
				p.openInventory(new ReportList(plugin, p, 1, false, false, false, false).getInventory());
			} else if (caseNumber == 8) {
				p.openInventory(new ReportList(plugin, p, 1, true, false, false, false).getInventory());
			} else if (caseNumber == 3) {
				if (item != null && !item.getType().equals(Material.AIR)) {
					p.openInventory(new StaffList(page - 1, plugin, p).getInventory());
				}
			} else if (caseNumber == 5) {
				if (item != null && !item.getType().equals(Material.AIR)) {
					p.openInventory(new StaffList(page + 1, plugin, p).getInventory());
				}
			} else {
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
			}
		} else if (event.getClickedInventory().getHolder() instanceof EditReport) {
			boolean fromArchive = event.getInventory().getItem(0).getItemMeta().getDisplayName()
					.contains("Retour aux archives");
			if (caseNumber == 0) {
				if (fromArchive)
					p.openInventory(new ReportList(plugin, p, 1, true, false, false, false).getInventory());
				else
					p.openInventory(new ReportList(plugin, p, 1, false, false, false, false).getInventory());
			}
			String displayNameReport = event.getInventory().getItem(4).getItemMeta().getDisplayName();
			int indexReport = Staff.getNumberAtEndStr(displayNameReport);
			Report report = plugin.getReports().get(indexReport);
			if (caseNumber == 21 && item.getType() != Material.AIR) {
				if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
					p.teleport(report.getReporter().getLocation());
					p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.GREEN
							+ "Vous avez bien été téléporté à l'ancienne position de "
							+ report.getReporter().getDisplayName());
				} else if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT)
					if (report.getReporter().isOnline()) {
						p.teleport(
								Bukkit.getPlayer(UUID.fromString(report.getReporter().getPlayeruuid())).getLocation());
						p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.GREEN
								+ "Vous avez bien été téléporté à "
								+ report.getReporter().getDisplayName());
					}
			} else if (caseNumber == 22) {
				report.changeStatus(ReportStatus.CLASSED_ABUSIVE);
				plugin.updateReport(indexReport, report, p);
				p.openInventory(
						new EditReport(plugin.getReports().get(indexReport), indexReport, plugin)
								.getInventory());
			} else if (caseNumber == 23 && item.getType() != Material.AIR) {
				if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
					p.teleport(report.getReported().getLocation());
					p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.GREEN
							+ "Vous avez bien été téléporté à l'ancienne position de "
							+ report.getReported().getDisplayName());
				} else if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT)
					if (report.getReported().isOnline()) {
						p.teleport(
								Bukkit.getPlayer(UUID.fromString(report.getReported().getPlayeruuid())).getLocation());
						p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.GREEN
								+ "Vous avez bien été téléporté à "
								+ report.getReported().getDisplayName());
					}
			} else if (caseNumber == 26) {
				int nbmessage = 30;
				List<ChatMessage> chatHistory;
				try {
					chatHistory = Database.loadchatHistoryFromDB(report.getReportTime(), nbmessage,
							UUID.fromString(report.getReporter().getUniqueId()),
							UUID.fromString(report.getReported().getUniqueId()));
				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Cannot load chat history ...");
					chatHistory = new ArrayList<>();
				}
				p.sendMessage(
						Staff.getSTAFF_PREFIX() + ChatColor.GOLD + "Liste des " + nbmessage
								+ " derniers messages des deux joueurs concernés: ");
				p.sendMessage(" ");
				for (ChatMessage cMessage : chatHistory) {
					p.sendMessage(cMessage.getPrettyMessage());
				}
				p.sendMessage(" ");
				p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.GOLD + "Fin des messages.");
			} else if (caseNumber == 28) {
				report.changeStatus(ReportStatus.WAITING);
				plugin.updateReport(indexReport, report, p);
				p.openInventory(
						new EditReport(plugin.getReports().get(indexReport), indexReport, plugin, fromArchive)
								.getInventory());
			} else if (caseNumber == 29) {
				report.changeStatus(ReportStatus.INPROGRESS);
				plugin.updateReport(indexReport, report, p);
				p.openInventory(
						new EditReport(plugin.getReports().get(indexReport), indexReport, plugin, fromArchive)
								.getInventory());
			} else if (caseNumber == 30) {
				report.changeStatus(ReportStatus.IMPORTANT);
				plugin.updateReport(indexReport, report, p);
				p.openInventory(
						new EditReport(plugin.getReports().get(indexReport), indexReport, plugin, fromArchive)
								.getInventory());
			} else if (caseNumber == 32) {
				report.changeStatus(ReportStatus.CLASSED_FALSE);
				plugin.updateReport(indexReport, report, p);
				p.openInventory(
						new EditReport(plugin.getReports().get(indexReport), indexReport, plugin, fromArchive)
								.getInventory());
			} else if (caseNumber == 33) {
				report.changeStatus(ReportStatus.CLASSED_NOTSURE);
				plugin.updateReport(indexReport, report, p);
				p.openInventory(
						new EditReport(plugin.getReports().get(indexReport), indexReport, plugin, fromArchive)
								.getInventory());
			} else if (caseNumber == 34) {
				report.changeStatus(ReportStatus.CLASSED_TRUE);
				plugin.updateReport(indexReport, report, p);
				p.openInventory(
						new EditReport(plugin.getReports().get(indexReport), indexReport, plugin, fromArchive)
								.getInventory());
			}
		} else if (plugin.isFrozen(p) != -1) {
			event.setCancelled(true);
			plugin.getFrozen().get(plugin.isFrozen(p)).setRedPaneInv(p);
			Static.sendFrozeMessage(p);
			return;
		} else if (indexStaffMod != -1) {
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
		if (caseNumber >= 0 && caseNumber < 54
				&& !(event.getClickedInventory().getHolder() instanceof CraftPlayer)) {
			event.setCancelled(true);
			if (itemM != null && itemM.getDisplayName().contains("Fermer")) {
				p.closeInventory();
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
		int indexStaffMod = plugin.isInStaffMod(p);
		if (indexStaffMod != -1) {
			if (plugin.getInStaffMod().get(indexStaffMod).isStaffInv()) {
				event.setCancelled(true);
				p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED
						+ "Vous ne pouvez pas interagir avec le monde en staff mod.");
			} else {
				if (!p.getInventory().getItem(8).getItemMeta().getDisplayName().equals(
						ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
					event.setCancelled(true);
			}
			int indexFrozen = plugin.isFrozen(p);
			if (indexFrozen != -1) {
				event.setCancelled(true);
				Static.sendFrozeMessage(p);
			}
		}
	}

	private boolean isDragOnCreatedInv(InventoryDragEvent event) {
		return event.getRawSlots().stream().anyMatch(
				value -> IntStream.rangeClosed(0, 54).anyMatch(rangeValue -> rangeValue == value));
	}

	@EventHandler
	public void onInventoryCreativeEvent(InventoryCreativeEvent event) {
		Player p = (Player) event.getWhoClicked();
		int indexStaffMod = plugin.isInStaffMod(p);
		if (indexStaffMod != -1) {
			if (plugin.getInStaffMod().get(indexStaffMod).isStaffInv()) {
				event.setCancelled(true);
				p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED
						+ "Vous ne pouvez pas interagir avec le monde en staff mod.");
			} else {
				if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null
						&& event.getCurrentItem().getItemMeta().getDisplayName() != null
						&& event.getCurrentItem().getItemMeta().getDisplayName()
								.equals(ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
					event.setCancelled(true);
			}
			int indexFrozen = plugin.isFrozen(p);
			if (indexFrozen != -1) {
				event.setCancelled(true);
				Static.sendFrozeMessage(p);
			}
		}
	}
}
