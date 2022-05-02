package fr.nessar;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

public class StaffInv {
	private static final ItemStack[] def = { new ItemStack(Material.COMPASS), new ItemStack(Material.BLAZE_ROD),
			new ItemStack(Material.REDSTONE), new Dye(DyeColor.LIME).toItemStack() };
}
