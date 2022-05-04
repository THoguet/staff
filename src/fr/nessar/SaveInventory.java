package fr.nessar;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SaveInventory {
	private Player owner;
	private ItemStack[] inventory;
	private ItemStack[] armor;

	public SaveInventory(Player p) {
		this.owner = p;
		this.inventory = saveInv(p);
		this.armor = saveArmor(p);
	}

	public ItemStack[] saveInv(Player p) {
		ItemStack[] ret = new ItemStack[36];
		for (int i = 0; i < 36; i++) {
			ret[i] = p.getInventory().getItem(i);
		}
		return ret;
	}

	public ItemStack[] saveArmor(Player p) {
		ItemStack[] ret = new ItemStack[4];
		ret[3] = p.getInventory().getHelmet();
		ret[2] = p.getInventory().getChestplate();
		ret[1] = p.getInventory().getLeggings();
		ret[0] = p.getInventory().getBoots();
		return ret;
	}

	public void loadInventory() {
		SaveInventory.clear(this.owner);
		for (int i = 0; i < 36; i++) {
			this.owner.getInventory().setItem(i, this.inventory[i]);
		}
		this.owner.getInventory().setArmorContents(this.armor);
	}

	public Player getOwner() {
		return this.owner;
	}

	public static void clear(Player p) {
		for (int i = 0; i < 36; i++) {
			p.getInventory().setItem(i, new ItemStack(Material.AIR));
		}
		p.getInventory().setHelmet(new ItemStack(Material.AIR));
		p.getInventory().setChestplate(new ItemStack(Material.AIR));
		p.getInventory().setLeggings(new ItemStack(Material.AIR));
		p.getInventory().setBoots(new ItemStack(Material.AIR));
	}
}
