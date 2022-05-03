package fr.nessar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SaveInventory {
	private Player owner;
	private List<ItemStack> inventory;
	private List<ItemStack> armor;

	public SaveInventory(Player p, List<ItemStack> inventory, List<ItemStack> armor) {
		this.owner = p;
		this.armor = armor;
		this.inventory = inventory;
	}

	public SaveInventory(Player p) {
		this.owner = p;
		this.armor = saveArmor(p);
		this.inventory = saveInv(p);
	}

	public List<ItemStack> saveInv(Player p) {
		final List<ItemStack> invCont = new ArrayList<>();
		for (ItemStack stack : p.getInventory().getContents()) {
			if (stack != null)
				invCont.add(stack);
		}
		return invCont;
	}

	public List<ItemStack> saveArmor(Player p) {
		final List<ItemStack> armorCont = new ArrayList<>();
		for (ItemStack stack : p.getInventory().getArmorContents()) {
			if (stack != null)
				armorCont.add(stack);
		}
		return armorCont;
	}

	public void loadInventory() {
		SaveInventory.clear(this.owner);
		for (int i = 0; i < this.inventory.size(); i++) {
			this.owner.getInventory().setItem(i, this.inventory.get(i));
		}
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

	public static void clear(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
	}
}
