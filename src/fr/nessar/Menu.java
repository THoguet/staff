package fr.nessar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
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
import org.bukkit.inventory.meta.SkullMeta;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;

public class Menu implements Listener {

	private Player user;
	private MenuType menuType;
	private Staff plugin;
	private List<OfflinePlayer> staffList = null;

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
				return null;
			case STAFFLIST:
				this.staffList = getStaffList();
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
		Inventory gui = Bukkit.createInventory(null, 54,
				this.menuType.getMenuName(false) + (page != -1 ? String.valueOf(page) : ""));
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

	private List<OfflinePlayer> getStaffList() {
		List<OfflinePlayer> staff = new ArrayList<>();
		List<OfflinePlayer> offlineP = Arrays.asList(Bukkit.getOfflinePlayers());
		for (OfflinePlayer p : offlineP) {
			if (plugin.getPerms().playerHas("world", p, "staff"))
				staff.add(p);
		}
		return staff;
	}

	public Inventory getStaff(int page) {
		Inventory gui = getBase(false, page);
		ItemStack teteHero = StaffMod.setNameItem(ChatColor.GOLD + "Staff en ligne",
				Menu.getPlayerHead("MHF_Herobrine"));
		gui.setItem(4, teteHero);
		for (int i = 28 * (page - 1); i < this.staffList.size() && i < 28 * page; i++) {
			int caseNumber = 18 + i - (28 * (page - 1));
			List<String> itemLore = new ArrayList<>();
			OfflinePlayer playerStaff = this.staffList.get(i);
			boolean isStaffOnline = this.staffList.get(i).isOnline();
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

	public Inventory getNewReport() {
		return null;
	}

	public void interactItem(int caseNumber, ClickType clickType) {
		caseNumber -= 18;
		switch (this.menuType) {
			case STAFFLIST:
				Player p = this.staffList.get(caseNumber).getPlayer();
				if (caseNumber >= 0 && caseNumber < this.staffList.size() && p != null) {
					this.user.sendMessage(Staff.getSTAFF_PREFIX() + "Vous avez bien été TP.");
					this.user.teleport(p.getLocation());
				}
				break;

			default:
				break;
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
	public void frozePlayerEvent(PlayerArmorStandManipulateEvent event) {
		cancelAndMessageFrozenPlayer(event);
	}

	@EventHandler
	public void frozePlayerEvent(PlayerMoveEvent event) {
		cancelAndMessageFrozenPlayer(event);
	}

	@EventHandler
	public void frozePlayerEvent(PlayerDropItemEvent event) {
		cancelAndMessageFrozenPlayer(event);
	}

	@EventHandler
	public void frozePlayerEvent(PlayerPickupItemEvent event) {
		cancelAndMessageFrozenPlayer(event);
	}

	@EventHandler
	public void frozePlayerEvent(PlayerPortalEvent event) {
		cancelAndMessageFrozenPlayer(event);
	}

	@EventHandler
	public void frozePlayerEvent(InventoryDragEvent event) {
		cancelAndMessageFrozenPlayer(event);
	}

	@EventHandler
	public void frozePlayerEvent(InventoryCloseEvent event) {
		cancelAndMessageFrozenPlayer(event);
	}

	@EventHandler
	public void frozePlayerEvent(InventoryClickEvent event) {
		cancelAndMessageFrozenPlayer(event);
	}

	@EventHandler
	public void frozePlayerEvent(InventoryInteractEvent event) {
		cancelAndMessageFrozenPlayer(event);
	}

	@EventHandler
	public void frozePlayerEvent(FoodLevelChangeEvent event) {
		cancelAndMessageFrozenPlayer(event);
	}

	@EventHandler
	public void frozePlayerEvent(EntityDamageEvent event) {
		cancelAndMessageFrozenPlayer(event);
	}

	private void cancelAndMessageFrozenPlayer(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	private void cancelAndMessageFrozenPlayer(PlayerArmorStandManipulateEvent event) {
		Player p = event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	private void cancelAndMessageFrozenPlayer(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	private void cancelAndMessageFrozenPlayer(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	private void cancelAndMessageFrozenPlayer(PlayerPortalEvent event) {
		Player p = event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	private void cancelAndMessageFrozenPlayer(InventoryDragEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	private void cancelAndMessageFrozenPlayer(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
				if (this.menuType.eventBehavour(event)) {
					event.setCancelled(true);
				}
			}
		}
	}

	private void cancelAndMessageFrozenPlayer(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
				if (this.menuType.eventBehavour(event)) {
					event.setCancelled(true);
				}
			}
		}
	}

	public void cancelAndMessageFrozenPlayer(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				this.interactItem(event.getRawSlot(), event.getClick());
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}

	public void cancelAndMessageFrozenPlayer(InventoryCloseEvent event) {
		Player p = (Player) event.getPlayer();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				Inventory toOpen = this.getInv(-1);
				Bukkit.getServer().getScheduler().runTask(this.plugin, new Runnable() {
					@Override
					public void run() {
						p.openInventory(toOpen);
					}
				});
				sendFrozeMessage(p);
			}
		}
	}

	private void cancelAndMessageFrozenPlayer(InventoryInteractEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (this.user != null && this.user.getUniqueId().equals(p.getUniqueId())) {
			if (this.menuType.eventBehavour(event)) {
				event.setCancelled(true);
				sendFrozeMessage(p);
			}
		}
	}
}
