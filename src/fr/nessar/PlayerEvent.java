package fr.nessar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import fr.nessar.Menu.Static;

public class PlayerEvent implements Listener {

	private Staff plugin;

	public PlayerEvent(Staff plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		int indexFrozen = plugin.isFrozen(p);
		if (indexFrozen != -1) {
			event.setCancelled(true);
			Static.sendFrozeMessage(p);
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (plugin.isInStaffMod(p) != -1) {
			plugin.toggleStaffMod(p);
			p.teleport(plugin.getLocToTp());
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		int indexStaffMod = plugin.isInStaffMod(p);
		if (indexStaffMod != -1) {
			if (plugin.getInStaffMod().get(indexStaffMod).isStaffInv()) {
				event.setCancelled(true);
				p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED
						+ "Vous ne pouvez pas interagir avec le monde en staff mod.");
			} else {
				if (event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(
						ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
					event.setCancelled(true);
			}
		}
		if (plugin.isFrozen(p) != -1) {
			event.setCancelled(true);
			Static.sendFrozeMessage(p);
		}
	}

	@EventHandler
	public void onPlayerInventoryDragEvent(InventoryDragEvent event) {
		Player p = (Player) event.getWhoClicked();
		int indexStaffMod = plugin.isInStaffMod(p);
		if (indexStaffMod != -1) {
			if (plugin.getInStaffMod().get(indexStaffMod).isStaffInv()) {
				event.setCancelled(true);
				p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED
						+ "Vous ne pouvez pas interagir avec le monde en staff mod.");
			} else {
				if (event.getOldCursor().getItemMeta().getDisplayName().equals(
						ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerInventoryDragEvent(PlayerPickupItemEvent event) {
		Player p = (Player) event.getPlayer();
		int indexStaffMod = plugin.isInStaffMod(p);
		if (indexStaffMod != -1) {
			if (plugin.getInStaffMod().get(indexStaffMod).isStaffInv()) {
				event.setCancelled(true);
				p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED
						+ "Vous ne pouvez pas interagir avec le monde en staff mod.");
			}
		}
	}

	@EventHandler
	public void onPlayerSneaking(PlayerToggleSneakEvent event) {
		Player p = event.getPlayer();
		int indexStaffMod = plugin.isInStaffMod(p);
		if (indexStaffMod != -1) {
			plugin.getInStaffMod().get(indexStaffMod).toggleSneakStatus();
		}
	}

	@EventHandler
	public void onPlayerInventoryInteractEvent(InventoryInteractEvent event) {
		Player p = (Player) event.getWhoClicked();
		int indexStaffMod = plugin.isInStaffMod(p);
		if (indexStaffMod != -1) {
			if (plugin.getInStaffMod().get(indexStaffMod).isStaffInv()) {
			}
			event.setCancelled(true);
			p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED
					+ "Vous ne pouvez pas interagir avec le monde en staff mod.");
		} else {
			if (!p.getInventory().getItem(8).getItemMeta().getDisplayName().equals(
					ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInventoryCreativeEvent(InventoryCreativeEvent event) {
		Player p = (Player) event.getWhoClicked();
		int indexStaffMod = plugin.isInStaffMod(p);
		if (indexStaffMod != -1) {
			if (plugin.getInStaffMod().get(indexStaffMod).isStaffInv()) {
			}
			event.setCancelled(true);
			p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED
					+ "Vous ne pouvez pas interagir avec le monde en staff mod.");
		} else {
			if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta().getDisplayName()
					.equals(ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPlayerInteractEvent(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		int indexStaffMod = plugin.isInStaffMod(p);
		if (indexStaffMod != -1) {
			ItemStack item = event.getItem();
			if (item != null && item.getItemMeta().getDisplayName().equals(
					ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off")
					|| plugin.getInStaffMod().get(indexStaffMod).isStaffInv()) {
				event.setCancelled(true);
				plugin.getInStaffMod().get(indexStaffMod).toggleOrUseSlot(event.getMaterial());
			}
		}
	}

	@EventHandler
	public void onPlayerArmorStandManipulationEvent(PlayerArmorStandManipulateEvent event) {
		Player p = event.getPlayer();
		if (plugin.isInStaffMod(p) != -1) {
			event.setCancelled(true);
		}
		if (plugin.isFrozen(p) != -1) {
			event.setCancelled(true);
			Static.sendFrozeMessage(p);
		}
	}

	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		if (plugin.isInStaffMod(p) != -1) {
			event.setCancelled(true);
		}
		if (plugin.isFrozen(p) != -1) {
			event.setCancelled(true);
			Static.sendFrozeMessage(p);
		}
	}

	@EventHandler
	public void handleEvent(PlayerPortalEvent event) {
		Player p = event.getPlayer();
		if (plugin.isInStaffMod(p) != -1) {
			event.setCancelled(true);
		}
		if (plugin.isFrozen(p) != -1) {
			event.setCancelled(true);
			Static.sendFrozeMessage(p);
		}
	}

	@EventHandler
	public void onPlayerPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		int indexStaffMod = plugin.isInStaffMod(p);
		if (indexStaffMod != -1) {
			event.setCancelled(true);
			ItemStack item = p.getItemInHand();
			if (item != null && item.getType().equals(Material.ICE)
					|| item.getType().equals(Material.PACKED_ICE)) {
				if (event.getRightClicked() instanceof Player) {
					Player target = (Player) event.getRightClicked();
					plugin.toggleFreeze(target);
				}
			}
		}
	}

	@EventHandler
	public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (plugin.isFrozen(p) != -1) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (plugin.isFrozen(p) != -1) {
				event.setCancelled(true);
			}
		}
	}
}
