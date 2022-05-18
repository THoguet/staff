package fr.nessar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nessar.Menu.EditReport;
import fr.nessar.Menu.ReportList;
import fr.nessar.Menu.StaffList;

public class CommandStaffExecutor implements CommandExecutor {

	private static final String PREFIX_PERMISSION = "staff.";
	private Staff plugin;

	public CommandStaffExecutor(Staff plugin) {
		this.plugin = plugin;
	}

	private String permissionErrMessage() {
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
			if (args[0].equalsIgnoreCase("list")) {
				if (!p.hasPermission(PREFIX_PERMISSION + "list")) {
					p.sendMessage(ChatColor.RED + permissionErrMessage());
					return true;
				}
				p.openInventory(new StaffList(1, plugin, p).getInventory());
				return true;
			} else if (args[0].equalsIgnoreCase("reports")) {
				if (!p.hasPermission(PREFIX_PERMISSION + "reports")) {
					p.sendMessage(ChatColor.RED + permissionErrMessage());
					return true;
				}
				p.openInventory(new ReportList(plugin, p, 1, false, false, false, false).getInventory());
				return true;
			}
		} else if (commandLabel.equalsIgnoreCase("staff") && args.length == 2) {
			if (args[0].equalsIgnoreCase("editreport")) {
				if (!p.hasPermission(PREFIX_PERMISSION + "editreport")) {
					p.sendMessage(ChatColor.RED + permissionErrMessage());
					return true;
				}
				int t;
				try {
					t = Integer.parseInt(args[1]);
				} catch (Exception e) {
					p.sendMessage(Staff.getREPORT_PREFIX() + ChatColor.RED + "Le report n'est pas correct.");
					return false;
				}
				if (t < 0 || plugin.getReports().size() - 1 < t) {
					p.sendMessage(Staff.getREPORT_PREFIX() + ChatColor.RED + "Le report n'existe pas.");
					return false;
				}
				p.openInventory(new EditReport(plugin.getReports().get(t), t, plugin).getInventory());
				return true;
			} else if (args[0].equalsIgnoreCase("cancelcooldown")) {
				if (!p.hasPermission(PREFIX_PERMISSION + "cancelcooldown")) {
					p.sendMessage(ChatColor.RED + permissionErrMessage());
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					p.sendMessage(
							Staff.getREPORT_PREFIX() + ChatColor.RED + "Le joueur " + args[1] + " est introuvable.");
					return true;
				}
				int indexReport = plugin.getLastReportInCoolDown(target, true);
				int indexTicket = plugin.getLastReportInCoolDown(target, false);
				if (indexReport == -1 && indexTicket == -1) {
					p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Le joueur n'est pas en cooldown.");
					return true;
				}
				if (indexReport != -1)
					plugin.getReports().get(indexReport).setIgnoreCooldown(true);
				if (indexTicket != -1)
					plugin.getReports().get(indexTicket).setIgnoreCooldown(true);
				p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.GREEN + "Le joueur n'est plus en cooldown.");
				return true;
			}
		}
		p.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Mauvais usage de la commande." + args.length);
		return false;
	}
}
