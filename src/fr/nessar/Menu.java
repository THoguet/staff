package fr.nessar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class Menu {

	public static Inventory getFreeze() {
		Inventory gui = Bukkit.createInventory(null, 27,
				ChatColor.BOLD + ChatColor.RED.toString() + "VOUS ETES FREEZE");
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
}
