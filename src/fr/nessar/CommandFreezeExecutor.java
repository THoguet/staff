package fr.nessar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFreezeExecutor implements CommandExecutor {

	private static final String PREFIX_PERMISSION = "staff.";
	private Staff plugin;

	public CommandFreezeExecutor(Staff plugin) {
		this.plugin = plugin;
	}

	public String permissionErrMessage() {
		return "You don't have the permission to do this command !";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if ((commandLabel.equalsIgnoreCase("freeze") || commandLabel.equalsIgnoreCase("block")) && args.length == 1) {
			Player target = Bukkit.getServer().getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Joueur " + args[0] + " introuvable.");
				return true;
			}
			if (plugin.isFrozen(target) != -1) {
				sender.sendMessage(
						Staff.getSTAFF_PREFIX() + "Le joueur " + target.getDisplayName() + " est deja freeze !");
				return true;
			}
			sender.sendMessage(
					Staff.getSTAFF_PREFIX() + "Le joueur " + target.getDisplayName() + " a bien été freeze !");
			plugin.toggleFreeze(target);
			return true;
		}
		if ((commandLabel.equalsIgnoreCase("unfreeze") || commandLabel.equalsIgnoreCase("unblock")
				|| commandLabel.equalsIgnoreCase("free")) && args.length == 1) {
			Player target = Bukkit.getServer().getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Joueur " + args[0] + " introuvable.");
				return true;
			}
			if (plugin.isFrozen(target) == -1) {
				sender.sendMessage(
						Staff.getSTAFF_PREFIX() + "Le joueur " + target.getDisplayName() + " n'est pas freeze !");
				return true;
			}
			sender.sendMessage(
					Staff.getSTAFF_PREFIX() + "Le joueur " + target.getDisplayName() + " a bien été libéré !");
			plugin.toggleFreeze(target);
			return true;
		}
		sender.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Mauvais usage de la commande.");
		return false;
	}
}
