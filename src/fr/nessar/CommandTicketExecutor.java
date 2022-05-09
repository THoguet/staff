package fr.nessar;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandTicketExecutor implements Listener {

	private static final String PREFIX_PERMISSION = "staff.";
	private Staff plugin;

	private static final String USAGE = ChatColor.RED + "Usage: /// <message>";

	public CommandTicketExecutor(Staff plugin) {
		this.plugin = plugin;
	}

	private String permissionErrMessage() {
		return "You don't have the permission to do this command !";
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		if (event.getMessage().startsWith("//")) {
			if (!event.getMessage().startsWith("// ")) {
				p.sendMessage(USAGE);
			}
			String reason = event.getMessage().substring(3);
			event.setMessage("ticket" + reason);
			event.setCancelled(true);
			plugin.newTicket(p, reason);
		}
	}
}