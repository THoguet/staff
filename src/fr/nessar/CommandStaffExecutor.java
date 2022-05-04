package fr.nessar;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStaffExecutor implements CommandExecutor {

	private static final String PREFIX_PERMISSION = "staff.";
	private Staff plugin;

	public CommandStaffExecutor(Staff plugin) {
		this.plugin = plugin;
	}

	public String permissionErrMessage() {
		return "You don't have the permission to do this command !";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(
					Staff.getSTAFF_PREFIX() + ChatColor.RED + "Seul les joueurs peuvent utiliser ces commandes.");
			return true;
		}
		Player p = (Player) sender;
		if (commandLabel.equalsIgnoreCase("staff") && args.length == 0) {
			this.plugin.toggleStaffMod(p);
			return true;
		} else if (commandLabel.equalsIgnoreCase("staff") && args.length == 1) {
			if (args[0].equalsIgnoreCase("list") && p.hasPermission(PREFIX_PERMISSION + "list")) {
				new Menu(p, MenuType.STAFFLIST, this.plugin, 1);
				return true;
			}
		}
		p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Mauvais usage de la commande.");
		return false;
	}
}
