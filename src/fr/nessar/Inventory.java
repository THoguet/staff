package fr.nessar;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Inventory {
	private Player owner;
	private List<ItemStack> inventory;
	private List<ItemStack> armor;

	public Inventory(Player p, List<ItemStack> inventory, List<ItemStack> armor) {
		this.owner = p;
		this.armor = armor;
		this.inventory = inventory;
	}

	public Player getOwner() {
		return this.owner;
	}

	public List<ItemStack> getInventory() {
		return this.inventory;
	}

	public List<ItemStack> getArmor() {
		return this.armor;
	}
}
