package fr.nessar;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandTicketListener implements Listener {

	private static final String USAGE = ChatColor.RED + "Usage: /// <message>";

	@EventHandler(priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		if (event.getMessage().startsWith("///")) {
			p.sendMessage(USAGE);
			event.setCancelled(true);
		} else if (event.getMessage().startsWith("// ")) {
			String reason = event.getMessage().substring(3);
			event.setMessage("/ticket " + reason);
		}
	}
}