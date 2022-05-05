package fr.nessar;

import org.bukkit.ChatColor;
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

public enum MenuType {
	FREEZE(0),
	NEWREPORT(1),
	REPORTLIST(2),
	STAFFLIST(3),
	INREPORT(4),
	EDITSTATUS(5),
	ARCHIVES(6),
	ACTIVEPUNISH(7),
	REPORTPUNISH(8),
	TEMPLATES(9),
	NEWTEMPLATE(10);

	private int menuCode;

	MenuType(int code) {
		this.menuCode = code;
	}

	public int getMenuCode() {
		return menuCode;
	}

	public String getMenuName(boolean report) {
		switch (this) {
			case FREEZE:
				return ChatColor.BOLD + ChatColor.RED.toString() + "VOUS ETES FREEZE";
			case NEWREPORT:
				return ChatColor.GOLD + (report ? "Report" : "Ticket") + ChatColor.GRAY + " > " + ChatColor.YELLOW
						+ "Nouveau";
			case REPORTLIST:
				return ChatColor.GOLD + (report ? "Report / Tickets" : "Ticket") + ChatColor.GRAY + " > "
						+ ChatColor.YELLOW + "Page ";
			case STAFFLIST:
				return ChatColor.GOLD + "Staff en ligne" + ChatColor.GRAY + " > " + ChatColor.YELLOW
						+ "Page ";
			case INREPORT:
				return ChatColor.GOLD + (report ? "Report" : "Ticket") + ChatColor.GRAY + " > "
						+ (report ? ChatColor.RED + "Report " + ChatColor.GRAY + "#"
								: ChatColor.BLUE + "Ticket " + ChatColor.GRAY + "#");
			case EDITSTATUS:
				return ChatColor.GOLD + "Traiter" + ChatColor.GRAY + " > "
						+ (report ? ChatColor.RED + "Report " + ChatColor.GRAY + "#"
								: ChatColor.BLUE + "Ticket " + ChatColor.GRAY + "#");
			case ARCHIVES:
				return ChatColor.GOLD + "Archives" + ChatColor.GRAY + " > " + ChatColor.YELLOW
						+ "Page ";
			case ACTIVEPUNISH:
				return ChatColor.GOLD + "Sanctions" + ChatColor.GRAY + " > " + ChatColor.YELLOW
						+ "Page ";
			case REPORTPUNISH:
				return ChatColor.GOLD + "Sanctionner" + ChatColor.GRAY + " > " + ChatColor.YELLOW;
			case TEMPLATES:
				return ChatColor.GOLD + "Modèles" + ChatColor.GRAY + " > " + ChatColor.YELLOW
						+ "Page ";
			case NEWTEMPLATE:
				return ChatColor.GOLD + "Modèle" + ChatColor.GRAY + " > " + ChatColor.YELLOW
						+ "Nouveau";
			default:
				return "";
		}
	}

	public boolean eventBehavour(InventoryClickEvent event) {
		switch (this) {
			case FREEZE:
				return true;
			case NEWREPORT:
				return false;
			case REPORTLIST:
				return true;
			case STAFFLIST:
				return true;
			case INREPORT:
				return false;
			case EDITSTATUS:
				return false;
			case ARCHIVES:
				return false;
			case ACTIVEPUNISH:
				return false;
			case REPORTPUNISH:
				return false;
			case TEMPLATES:
				return false;
			case NEWTEMPLATE:
				return false;
			default:
				return false;
		}
	}

	public boolean eventBehavour(InventoryCloseEvent event) {
		switch (this) {
			case FREEZE:
				return true;
			case NEWREPORT:
				return false;
			case REPORTLIST:
				return true;
			case STAFFLIST:
				return true;
			case INREPORT:
				return false;
			case EDITSTATUS:
				return false;
			case ARCHIVES:
				return false;
			case ACTIVEPUNISH:
				return false;
			case REPORTPUNISH:
				return false;
			case TEMPLATES:
				return false;
			case NEWTEMPLATE:
				return false;
			default:
				return false;
		}
	}

	public boolean eventBehavour(PlayerArmorStandManipulateEvent event) {
		switch (this) {
			case FREEZE:
				return true;
			case NEWREPORT:
				return false;
			case REPORTLIST:
				return false;
			case STAFFLIST:
				return false;
			case INREPORT:
				return false;
			case EDITSTATUS:
				return false;
			case ARCHIVES:
				return false;
			case ACTIVEPUNISH:
				return false;
			case REPORTPUNISH:
				return false;
			case TEMPLATES:
				return false;
			case NEWTEMPLATE:
				return false;
			default:
				return false;
		}
	}

	public boolean eventBehavour(PlayerMoveEvent event) {
		switch (this) {
			case FREEZE:
				return true;
			case NEWREPORT:
				return false;
			case REPORTLIST:
				return false;
			case STAFFLIST:
				return false;
			case INREPORT:
				return false;
			case EDITSTATUS:
				return false;
			case ARCHIVES:
				return false;
			case ACTIVEPUNISH:
				return false;
			case REPORTPUNISH:
				return false;
			case TEMPLATES:
				return false;
			case NEWTEMPLATE:
				return false;
			default:
				return false;
		}
	}

	public boolean eventBehavour(PlayerDropItemEvent event) {
		switch (this) {
			case FREEZE:
				return true;
			case NEWREPORT:
				return false;
			case REPORTLIST:
				return false;
			case STAFFLIST:
				return false;
			case INREPORT:
				return false;
			case EDITSTATUS:
				return false;
			case ARCHIVES:
				return false;
			case ACTIVEPUNISH:
				return false;
			case REPORTPUNISH:
				return false;
			case TEMPLATES:
				return false;
			case NEWTEMPLATE:
				return false;
			default:
				return false;
		}
	}

	public boolean eventBehavour(PlayerPickupItemEvent event) {
		switch (this) {
			case FREEZE:
				return true;
			case NEWREPORT:
				return false;
			case REPORTLIST:
				return false;
			case STAFFLIST:
				return false;
			case INREPORT:
				return false;
			case EDITSTATUS:
				return false;
			case ARCHIVES:
				return false;
			case ACTIVEPUNISH:
				return false;
			case REPORTPUNISH:
				return false;
			case TEMPLATES:
				return false;
			case NEWTEMPLATE:
				return false;
			default:
				return false;
		}
	}

	public boolean eventBehavour(PlayerPortalEvent event) {
		switch (this) {
			case FREEZE:
				return true;
			case NEWREPORT:
				return false;
			case REPORTLIST:
				return false;
			case STAFFLIST:
				return false;
			case INREPORT:
				return false;
			case EDITSTATUS:
				return false;
			case ARCHIVES:
				return false;
			case ACTIVEPUNISH:
				return false;
			case REPORTPUNISH:
				return false;
			case TEMPLATES:
				return false;
			case NEWTEMPLATE:
				return false;
			default:
				return false;
		}
	}

	public boolean eventBehavour(InventoryDragEvent event) {
		switch (this) {
			case FREEZE:
				return true;
			case NEWREPORT:
				return false;
			case REPORTLIST:
				return true;
			case STAFFLIST:
				return true;
			case INREPORT:
				return false;
			case EDITSTATUS:
				return false;
			case ARCHIVES:
				return false;
			case ACTIVEPUNISH:
				return false;
			case REPORTPUNISH:
				return false;
			case TEMPLATES:
				return false;
			case NEWTEMPLATE:
				return false;
			default:
				return false;
		}
	}

	public boolean eventBehavour(InventoryInteractEvent event) {
		switch (this) {
			case FREEZE:
				return true;
			case NEWREPORT:
				return false;
			case REPORTLIST:
				return true;
			case STAFFLIST:
				return true;
			case INREPORT:
				return false;
			case EDITSTATUS:
				return false;
			case ARCHIVES:
				return false;
			case ACTIVEPUNISH:
				return false;
			case REPORTPUNISH:
				return false;
			case TEMPLATES:
				return false;
			case NEWTEMPLATE:
				return false;
			default:
				return false;
		}
	}

	public boolean eventBehavour(FoodLevelChangeEvent event) {
		switch (this) {
			case FREEZE:
				return true;
			case NEWREPORT:
				return false;
			case REPORTLIST:
				return false;
			case STAFFLIST:
				return false;
			case INREPORT:
				return false;
			case EDITSTATUS:
				return false;
			case ARCHIVES:
				return false;
			case ACTIVEPUNISH:
				return false;
			case REPORTPUNISH:
				return false;
			case TEMPLATES:
				return false;
			case NEWTEMPLATE:
				return false;
			default:
				return false;
		}
	}

	public boolean eventBehavour(EntityDamageEvent event) {
		switch (this) {
			case FREEZE:
				return true;
			case NEWREPORT:
				return false;
			case REPORTLIST:
				return false;
			case STAFFLIST:
				return false;
			case INREPORT:
				return false;
			case EDITSTATUS:
				return false;
			case ARCHIVES:
				return false;
			case ACTIVEPUNISH:
				return false;
			case REPORTPUNISH:
				return false;
			case TEMPLATES:
				return false;
			case NEWTEMPLATE:
				return false;
			default:
				return false;
		}
	}
}
