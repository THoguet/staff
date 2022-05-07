package fr.nessar.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import fr.nessar.Report;
import fr.nessar.ReportStatus;
import fr.nessar.Reputation;
import fr.nessar.Staff;
import fr.nessar.StaffMod;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

public class Static {
	public static String getConnectionStr(String name) {
		return ChatColor.GRAY + "("
				+ (Bukkit.getPlayer(name) != null ? ChatColor.GREEN + "connecté" : ChatColor.RED + "déconnecté")
				+ ChatColor.GRAY + ")";
	}

	public static ItemStack getReportHead(boolean reporter, String name, boolean report, UUID playerUUID,
			Staff plugin) {
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

	public static ItemStack getSavedData(Report r) {
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

	public static void sendFrozeMessage(Player p) {
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

	public static Inventory getBase(InventoryHolder invHolder) {
		Inventory gui = Bukkit.createInventory(invHolder, 54, "");
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

	public static ItemStack getReportItem(Report r, int i) {
		List<String> itemLore = r.getLore();
		ItemStack itemReport;
		if (r.isReport())
			itemReport = StaffMod.setNameItem(ChatColor.RED + "Report " + ChatColor.GRAY + "#" + i,
					new ItemStack(Material.PAPER));
		else
			itemReport = StaffMod.setNameItem(ChatColor.BLUE + "Ticket " + ChatColor.GRAY + "#" + i,
					new ItemStack(Material.EMPTY_MAP));
		itemReport = StaffMod.setLoreitem(itemLore, itemReport);
		if (r.getStatus() != ReportStatus.WAITING)
			itemReport = Static.addGlow(itemReport);
		return itemReport;
	}
}
