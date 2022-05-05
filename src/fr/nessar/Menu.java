package fr.nessar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import net.md_5.bungee.api.ChatColor;

public class Menu implements Listener {

	private Player user;
	private MenuType menuType;
	private Staff plugin;

	public Menu(Player p, MenuType menuType, Staff plugin) {
		this.user = p;
		this.menuType = menuType;
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.user.openInventory(getInv(-1));
	}

	public Menu(Player p, MenuType menuType, Staff plugin, int page) {
		this.user = p;
		this.menuType = menuType;
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.user.openInventory(getInv(page));
	}

	public void closeMenu() {
		Player toClose = this.user;
		this.user = null;
		toClose.closeInventory();
	}

	public Inventory getInv(int page) {
		switch (this.menuType) {
			case FREEZE:
				return getFreeze();
			case NEWREPORT:
				return null;
			case REPORTLIST:
				return getReports(page);
			case STAFFLIST:
				return getStaff(page);
			case INREPORT:
				return null;
			case EDITSTATUS:
				return null;
			case ARCHIVES:
				return null;
			case ACTIVEPUNISH:
				return null;
			case REPORTPUNISH:
				return null;
			case TEMPLATES:
				return null;
			case NEWTEMPLATE:
				return null;
			default:
				return null;
		}
	}

	public Inventory getFreeze() {
		Inventory gui = Bukkit.createInventory(null, 27, this.menuType.getMenuName(false));
		List<ItemStack> listItem = new ArrayList<>();
		ItemStack redPane = StaffMod.setNameItem(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14));
		for (int i = 0; i < 27; i++) {
			listItem.add(redPane);
		}
		ItemStack packedIce = StaffMod.setNameItem(ChatColor.GOLD + "Vous avez été " + ChatColor.BLUE + "FREEZE",
				new ItemStack(Material.PACKED_ICE));
		listItem.set(11, packedIce);
		ItemStack tnt = StaffMod.setNameItem(ChatColor.RED + ChatColor.BOLD.toString() + "DECONNEXION = BAN",
				new ItemStack(Material.TNT));
		listItem.set(13, tnt);
		ItemStack netherStar = StaffMod.setNameItem(
				ChatColor.GOLD + "Allez sur ts.nessar.fr pour parler avec l'equipe de modération !",
				new ItemStack(Material.NETHER_STAR));
		listItem.set(15, netherStar);
		ItemStack[] ret = {};
		gui.setContents(listItem.toArray(ret));
		return gui;
	}

	private Inventory getBase(boolean report, int page) {
		return getBase(report, page, "");
	}

	private Inventory getBase(boolean report, int page, String completeName) {
		Inventory gui = Bukkit.createInventory(null, 54,
				this.menuType.getMenuName(false) + completeName + (page != -1 ? String.valueOf(page) : ""));
		List<ItemStack> listItem = new ArrayList<>();
		for (int i = 0; i < 54; i++) {
			listItem.add(new ItemStack(Material.AIR));
		}
		ItemStack darkPane = StaffMod.setNameItem(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));
		ItemStack quit = StaffMod.setNameItem(ChatColor.RED + "Fermer", new ItemStack(Material.BARRIER));
		for (int i = 9; i < 18; i++) {
			listItem.set(i, darkPane);
		}
		for (int i = 45; i < 54; i++) {
			listItem.set(i, darkPane);
		}
		listItem.set(49, quit);
		ItemStack[] ret = new ItemStack[54];
		gui.setContents(listItem.toArray(ret));
		return gui;
	}

	public Inventory getStaff(int page) {
		List<OfflinePlayer> staffList = this.plugin.getStaff();
		Inventory gui = getBase(false, page);
		ItemStack teteHero = StaffMod.setNameItem(ChatColor.GOLD + "Staff en ligne",
				Menu.getPlayerHead("MHF_Herobrine"));
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
		reportOrTickets = StaffMod.setNameItem(ChatColor.GOLD + nameReportsOrTickets, reportOrTickets);
		gui.setItem(0, reportOrTickets);
		ItemStack archives = StaffMod.setNameItem(ChatColor.GOLD + "Archives", new ItemStack(Material.BOOKSHELF));
		gui.setItem(8, archives);
		for (int i = 28 * (page - 1); i < staffList.size() && i < 28 * page; i++) {
			int caseNumber = 18 + i - (28 * (page - 1));
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
							Menu.getPlayerHead(playerStaff.getName())));
			gui.setItem(caseNumber, staffHead);
		}
		return gui;
	}

	public Inventory getReports(int page) {
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
		Inventory gui = getBase(permReport, page);
		gui.setItem(4, StaffMod.setNameItem(ChatColor.GOLD + nameReportsOrTickets, reportOrTickets));

		ItemStack archives = StaffMod.setNameItem(ChatColor.GOLD + "Archives", new ItemStack(Material.BOOKSHELF));
		gui.setItem(8, archives);
		for (int i = 28 * (page - 1); i < reportsList.size() && i < 28 * page; i++) {
			Report report = reportsList.get(i);
			if (report.isReport() != permReport && report.isTicket() != permTicket)
				continue;
			int caseNumber = 18 + i - (28 * (page - 1));
			List<String> itemLore = report.getLore();
			ItemStack itemReport;
			if (report.isReport())
				itemReport = StaffMod.setNameItem(ChatColor.RED + "Report " + ChatColor.GRAY + "#" + i,
						new ItemStack(Material.PAPER));
			else
				itemReport = StaffMod.setNameItem(ChatColor.BLUE + "Ticket " + ChatColor.GRAY + "#" + i,
						new ItemStack(Material.EMPTY_MAP));
			itemReport = StaffMod.setLoreitem(itemLore, itemReport);
			gui.setItem(caseNumber, itemReport);
		}
		return gui;
	}

	public Inventory getNewReport() {
		return null;
	}

	public void interactItem(InventoryClickEvent event) {
		List<OfflinePlayer> staffList = this.plugin.getStaff();
		int caseNumber = -999;
		caseNumber = event.getRawSlot();
		ItemStack item = event.getCurrentItem();
		ItemMeta itemM = null;
		if (item != null)
			itemM = item.getItemMeta();
		switch (this.menuType) {
			case FREEZE:
				event.setCancelled(true);
				break;
			case STAFFLIST:
				int indexStaffList = caseNumber - 18;
				if (indexStaffList >= 0 && indexStaffList < staffList.size()) {
					Player p = staffList.get(indexStaffList).getPlayer();
					if (p != null) {
						this.user.sendMessage(Staff.getSTAFF_PREFIX() + "Vous avez bien été TP.");
						this.user.teleport(p.getLocation());
					}
				}
			default:
				if (caseNumber >= 0 && caseNumber < 54) {
					event.setCancelled(true);
					if (itemM != null && itemM.getDisplayName().contains("Fermer")) {
						this.closeMenu();
					}
					break;
				}
		}
	}

	public static ItemStack getPlayerHead(String name) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta itemM = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		itemM.setOwner(name);
		skull.setItemMeta(itemM);
		return skull;
	}

	public void sendFrozeMessage(Player p) {
		if (this.menuType != MenuType.FREEZE)
			return;
		p.sendMessage(
				"" + ChatColor.GOLD + "████████████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD
						+ "████████████████");
		p.sendMessage("" + ChatColor.GOLD + "███████████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█"
				+ ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███████████████");
		p.sendMessage("" + ChatColor.GOLD + "██████████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███"
				+ ChatColor.DARK_RED + "█" + ChatColor.GOLD + "██████████████");
		p.sendMessage("" + ChatColor.GOLD + "█████████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████"
				+ ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████████████");
		p.sendMessage("" + ChatColor.GOLD + "████████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███████"
				+ ChatColor.DARK_RED + "█" + ChatColor.GOLD + "████████████");
		p.sendMessage("" + ChatColor.GOLD + "███████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "████"
				+ ChatColor.DARK_RED + "██" + ChatColor.GOLD + "███" + ChatColor.DARK_RED
				+ "█" + ChatColor.GOLD + "███████████");
		p.sendMessage("" + ChatColor.GOLD + "██████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "████"
				+ ChatColor.DARK_RED
				+ "████" + ChatColor.GOLD + "███" + ChatColor.DARK_RED
				+ "█" + ChatColor.GOLD + "██████████");
		p.sendMessage("" + ChatColor.GOLD + "█████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████"
				+ ChatColor.DARK_RED
				+ "████" + ChatColor.GOLD + "████" + ChatColor.DARK_RED
				+ "█" + ChatColor.GOLD + "█████████");
		p.sendMessage("" + ChatColor.GOLD + "████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "██████"
				+ ChatColor.DARK_RED
				+ "████" + ChatColor.GOLD + "█████" + ChatColor.DARK_RED
				+ "█" + ChatColor.GOLD + "████████");
		p.sendMessage("" + ChatColor.GOLD + "███████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███████"
				+ ChatColor.DARK_RED
				+ "████" + ChatColor.GOLD + "██████" + ChatColor.DARK_RED
				+ "█" + ChatColor.GOLD + "███████");
		p.sendMessage("" + ChatColor.GOLD + "██████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████████"
				+ ChatColor.DARK_RED + "██" + ChatColor.GOLD + "████████" + ChatColor.DARK_RED
				+ "█" + ChatColor.GOLD + "██████");
		p.sendMessage(
				"" + ChatColor.GOLD + "█████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████████████████████"
						+ ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████");
		p.sendMessage("" + ChatColor.GOLD + "████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███████████"
				+ ChatColor.DARK_RED + "██" + ChatColor.GOLD + "██████████" + ChatColor.DARK_RED
				+ "█" + ChatColor.GOLD + "████");
		p.sendMessage("" + ChatColor.GOLD + "███" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███████████"
				+ ChatColor.DARK_RED
				+ "████" + ChatColor.GOLD + "██████████" + ChatColor.DARK_RED
				+ "█" + ChatColor.GOLD + "███");
		p.sendMessage("" + ChatColor.GOLD + "██" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████████████"
				+ ChatColor.DARK_RED + "██" + ChatColor.GOLD + "████████████" + ChatColor.DARK_RED
				+ "█" + ChatColor.GOLD + "██");
		p.sendMessage(
				"" + ChatColor.GOLD + "█" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████████████████████████████"
						+ ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█");
		p.sendMessage("" + ChatColor.DARK_RED + "█████████████████████████████████");
	}

	@EventHandler
	public void handleEvent(PlayerArmorStandManipulateEvent event) {
		eventBehavour(event);
	}

	@EventHandler
	public void handleEvent(PlayerMoveEvent event) {
		eventBehavour(event);
	}

	@EventHandler
	public void handleEvent(PlayerDropItemEvent event) {
		eventBehavour(event);
	}

	@EventHandler
	public void handleEvent(PlayerPickupItemEvent event) {
		eventBehavour(event);
	}

	@EventHandler
	public void handleEvent(PlayerPortalEvent event) {
		eventBehavour(event);
	}

	@EventHandler
	public void handleEvent(InventoryDragEvent event) {
		eventBehavour(event);
	}

	@EventHandler
	public void handleEvent(InventoryCloseEvent event) {
		eventBehavour(event);
	}

	@EventHandler
	public void handleEvent(InventoryClickEvent event) {
		eventBehavour(event);
	}

	@EventHandler
	public void handleEvent(InventoryInteractEvent event) {
		eventBehavour(event);
	}

	@EventHandler
	public void handleEvent(FoodLevelChangeEvent event) {
		eventBehavour(event);
	}

	@EventHandler
	public void handleEvent(EntityDamageEvent event) {
		eventBehavour(event);
	}

	private void eventBehavour(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	private void eventBehavour(PlayerArmorStandManipulateEvent event) {
		Player p = event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	private void eventBehavour(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	private void eventBehavour(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	private void eventBehavour(PlayerPortalEvent event) {
		Player p = event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	private boolean isDragOnCreatedInv(InventoryDragEvent event) {
		return event.getRawSlots().stream().anyMatch(
				value -> IntStream.rangeClosed(0, 54).anyMatch(rangeValue -> rangeValue == value));
	}

	private void eventBehavour(InventoryDragEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				switch (this.menuType) {
					case FREEZE:
						event.setCancelled(true);
						sendFrozeMessage(p);
						break;
					case STAFFLIST:
						// test if the drag hit any menu slot
						event.setCancelled(isDragOnCreatedInv(event));
					case REPORTLIST:
						event.setCancelled(isDragOnCreatedInv(event));
					default:
						break;
				}
			}
		}
	}

	private void eventBehavour(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
				if (this.menuType.eventBehavour(event)) {
					event.setCancelled(true);
				}
			}
		}
	}

	private void eventBehavour(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
				if (this.menuType.eventBehavour(event)) {
					event.setCancelled(true);
				}
			}
		}
	}

	public void eventBehavour(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				this.interactItem(event);
				sendFrozeMessage(p);
			}
		}
	}

	public void eventBehavour(InventoryCloseEvent event) {
		Player p = (Player) event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				switch (this.menuType) {
					case FREEZE:
						Inventory toOpen = this.getInv(-1);
						Bukkit.getServer().getScheduler().runTask(this.plugin, new Runnable() {
							@Override
							public void run() {
								p.openInventory(toOpen);
							}
						});
						sendFrozeMessage(p);
						break;
					default:
						this.closeMenu();
						break;
				}
			}
		}
	}

	private void eventBehavour(InventoryInteractEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}
}
