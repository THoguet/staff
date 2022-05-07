package fr.nessar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

public class Menu implements Listener {

	private Player user;
	private MenuType menuType;
	private Staff plugin;
	private int indexMenu;

	public Menu(Player p, MenuType menuType, Staff plugin, int indexOpenMenu) {
		this.user = p;
		this.indexMenu = indexOpenMenu;
		this.menuType = menuType;
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.user.openInventory(getInv(-1));
	}

	public Menu(Player p, MenuType menuType, Staff plugin, int page, int indexOpenMenu) {
		this.user = p;
		this.indexMenu = indexOpenMenu;
		this.menuType = menuType;
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.user.openInventory(getInv(page));
	}

	public Menu(Player p, MenuType menuType, Staff plugin, Report r, int index, int indexOpenMenu) {
		this.user = p;
		this.indexMenu = indexOpenMenu;
		this.menuType = menuType;
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.user.openInventory(getInv(r, index));
	}

	public void closeMenu() {
		plugin.removeOpenMenu(this.indexMenu);
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

	public Inventory getInv(Report r, int index) {
		switch (this.menuType) {
			case INREPORT:
				return getEditReport(r, index);
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
				this.menuType.getMenuName(report) + completeName + (page != -1 ? String.valueOf(page) : ""));
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

	public ItemStack getReportItem(Report r, int i) {
		List<String> itemLore = r.getLore();
		ItemStack itemReport;
		if (r.isReport())
			itemReport = StaffMod.setNameItem(ChatColor.RED + "Report " + ChatColor.GRAY + "#" + i,
					new ItemStack(Material.PAPER));
		else
			itemReport = StaffMod.setNameItem(ChatColor.BLUE + "Ticket " + ChatColor.GRAY + "#" + i,
					new ItemStack(Material.EMPTY_MAP));
		itemReport = StaffMod.setLoreitem(itemLore, itemReport);
		return itemReport;
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
			ItemStack itemReport = getReportItem(report, i);
			if (report.getStatus() != ReportStatus.WAITING)
				itemReport = addGlow(itemReport);
			gui.setItem(caseNumber, itemReport);
		}
		return gui;
	}

	public String getConnectionStr(String name) {
		return ChatColor.GRAY + "("
				+ (Bukkit.getPlayer(name) != null ? ChatColor.GREEN + "connecté" : ChatColor.RED + "déconnecté")
				+ ChatColor.GRAY + ")";
	}

	public ItemStack getReportHead(boolean reporter, String name, boolean report, UUID playerUUID) {
		ItemStack head = getPlayerHead(name);
		head = StaffMod.setNameItem(ChatColor.GRAY + "Signalé: "
				+ (reporter ? ChatColor.GREEN : ChatColor.RED) + name + " " + getConnectionStr(name), head);
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Réputation: "
				+ Reputation.getRepStr(playerUUID, plugin.getReports(), plugin.getPunishments()));
		lore.add(
				ChatColor.GRAY + "Signalments effectués: " + ChatColor.BLUE
						+ Reputation.getReportsFrom(playerUUID, plugin.getReports()).size());
		lore.add(ChatColor.GRAY + "Signalments reçus: " + ChatColor.BLUE
				+ Reputation.getReportOf(playerUUID, plugin.getReports()).size());
		lore.add(" ");
		lore.add(ChatColor.GOLD + "Clic gauche " + ChatColor.GRAY
				+ "pour vous téléporter à la position actuelle du joueur " + ChatColor.YELLOW + name);
		lore.add(ChatColor.GOLD + "Clic droit " + ChatColor.GRAY
				+ "pour vous téléporter à l'ancienne position du joueur " + ChatColor.YELLOW + name);
		head = StaffMod.setLoreitem(lore, head);
		return head;
	}

	public ItemStack getSavedData(Report r) {
		ItemStack data = StaffMod.setNameItem(ChatColor.YELLOW + "Données collectées",
				new ItemStack(Material.ENCHANTED_BOOK));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "Signaleur: " + ChatColor.RED + r.getReported().getName()
				+ " " + getConnectionStr(r.getReported().getName()));
		lore.add(
				ChatColor.GRAY + "  Gamemode: " + ChatColor.BLUE + r.getReported().getGamemodeStr() + ChatColor.GRAY
						+ ", Au sol: "
						+ (r.getReported().isFlying() ? ChatColor.RED + "non" : ChatColor.GREEN + "oui"));
		lore.add(ChatColor.GRAY + "  Sneak: "
				+ (r.getReported().isSneaking() ? ChatColor.RED + "non" : ChatColor.GREEN + "oui") + ChatColor.GRAY
				+ ", Sprint: " + (r.getReported().isSprinting() ? ChatColor.RED + "non" : ChatColor.GREEN + "oui"));
		lore.add(ChatColor.GRAY + "  Vie: " + ChatColor.RED + r.getReported().getHealth() + "/"
				+ r.getReported().getMaxhealth() + ChatColor.GRAY + ", Nourriture: " + ChatColor.GOLD
				+ r.getReported().getFood());
		lore.add(ChatColor.GRAY + "  UUID: " + ChatColor.DARK_GRAY + r.getReported().getUniqueId());
		lore.add(ChatColor.GRAY + "  IP: " + ChatColor.YELLOW + r.getReported().getIp());
		lore.add(" ");
		lore.add(ChatColor.YELLOW + "Signaleur: " + ChatColor.GREEN + r.getReporter().getName()
				+ " " + getConnectionStr(r.getReporter().getName()));
		lore.add(ChatColor.GRAY + "  UUID: " + ChatColor.DARK_GRAY + r.getReporter().getUniqueId());
		lore.add(ChatColor.GRAY + "  IP: " + ChatColor.YELLOW + r.getReporter().getIp());
		lore.add("  ");
		lore.add(ChatColor.GOLD + "Clic gauche" + ChatColor.GRAY + " pour afficher l'historique des messages.");
		data = StaffMod.setLoreitem(lore, data);
		return data;
	}

	public Inventory getEditReport(Report r, int index) {
		Inventory gui = getBase(r.isReport(), -1, String.valueOf(index));
		ItemStack backToReportList = StaffMod.setNameItem(ChatColor.GOLD + "Liste des reports",
				new ItemStack(Material.BOOKSHELF));
		gui.setItem(0, backToReportList);
		ItemStack reportitem = getReportItem(r, index);
		gui.setItem(4, reportitem);
		gui.setItem(18, reportitem);
		ItemStack reporterHead = getReportHead(true, r.getReporter().getName(), r.isReport(),
				UUID.fromString(r.getReporter().getUniqueId()));
		gui.setItem(21, reporterHead);
		ItemStack abusiveReport = StaffMod.setNameItem(ChatColor.YELLOW + "Signalement abusif",
				new ItemStack(Material.GOLD_AXE));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD + "Clic" + ChatColor.GRAY
				+ " pour sanctionner le joueur ayant envoyé ce report: " + ChatColor.GREEN + r.getReporter().getName());
		abusiveReport = StaffMod.setLoreitem(lore, abusiveReport);
		gui.setItem(22, abusiveReport);
		ItemStack reportedHead = getReportHead(false, r.getReported().getName(), r.isReport(),
				UUID.fromString(r.getReported().getUniqueId()));
		gui.setItem(23, reportedHead);
		ItemStack data = getSavedData(r);
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

	public Inventory getNewReport() {
		return null;
	}

	public void interactItem(InventoryClickEvent event) {
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
				List<OfflinePlayer> staffList = this.plugin.getStaff();
				int indexStaffList = caseNumber - 18;
				if (indexStaffList >= 0 && indexStaffList < staffList.size()) {
					Player p = staffList.get(indexStaffList).getPlayer();
					if (p != null) {
						this.user.sendMessage(Staff.getSTAFF_PREFIX() + "Vous avez bien été TP.");
						this.user.teleport(p.getLocation());
						break;
					}
				}
			case REPORTLIST:
				List<Report> reports = plugin.getReports();
				int indexReportList = caseNumber - 18;
				if (indexReportList >= 0 && indexReportList < reports.size()) {
					plugin.addOpenMenu(new Menu(this.user, MenuType.INREPORT, this.plugin, reports.get(indexReportList),
							indexReportList, plugin.sizeOpenMenu() - 1));
					this.user = null;
					break;
				}
			case INREPORT:
				if (caseNumber == 0) {
					plugin.addOpenMenu(
							new Menu(this.user, MenuType.REPORTLIST, this.plugin, 1, plugin.sizeOpenMenu() - 1));
					this.user = null;
					break;
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

	public static ItemStack addGlow(ItemStack item) {
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;
		if (!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		if (tag == null)
			tag = nmsStack.getTag();
		NBTTagList ench = new NBTTagList();
		tag.set("ench", ench);
		nmsStack.setTag(tag);
		return CraftItemStack.asCraftMirror(nmsStack);
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
						Bukkit.broadcastMessage("event close");
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
