package fr.nessar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Froze {
	private Player frozenPlayer;
	private SaveInventory frozeInventory;
	private int indexOpenMenu;

	public Froze(Player p, int indexOpenMenu) {
		this.frozenPlayer = p;
		this.frozeInventory = new SaveInventory(p);
		this.indexOpenMenu = indexOpenMenu;
		this.setRedPaneInv(p);
		p.getInventory().setHeldItemSlot(0);
		p.getInventory().setItemInHand(StaffMod.setNameItem(" ", new ItemStack(Material.PACKED_ICE)));
	}

	public int getIndexOpenMenu() {
		return indexOpenMenu;
	}

	public SaveInventory getFrozeInventory() {
		return this.frozeInventory;
	}

	public Player getFrozenPlayer() {
		return frozenPlayer;
	}

	public void loadInv() {
		this.frozeInventory.loadInventory();
	}

	public void setRedPaneInv(Player p) {
		SaveInventory.clear(p);
		ItemStack redPane = StaffMod.setNameItem(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14));
		List<ItemStack> fullinvRedPane = new ArrayList<>(36);
		for (int i = 0; i < 36; i++) {
			fullinvRedPane.add(redPane);
		}
		ItemStack[] ret = {};
		p.getInventory().setContents(fullinvRedPane.toArray(ret));
		p.getInventory().setArmorContents(
				new ItemStack[] { new ItemStack(Material.PACKED_ICE), new ItemStack(Material.PACKED_ICE),
						new ItemStack(Material.PACKED_ICE), new ItemStack(Material.PACKED_ICE) });
	}
}
