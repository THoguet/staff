package fr.nessar;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandListener implements Listener {
	private static final String USAGE = ChatColor.RED + "Usage: /// <message>";

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		if (event.getMessage().startsWith("///")) {
			event.setMessage("dummy");
			p.sendMessage(USAGE);
			event.setCancelled(true);
		} else if (event.getMessage().startsWith("// ")) {
			String reason = event.getMessage().substring(3);
			event.setMessage("/ticket " + reason);
		}
		if (event.getMessage().startsWith("/pardon")) {
			if (event.getMessage().startsWith("/pardon ")) {
				String playerName = event.getMessage().substring(8);
				event.setMessage("/tempban " + playerName + " 0");
			} else {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "Usage: /pardon <Player>");
			}
		}
		if (event.getMessage().startsWith("/ban")) {
			if (event.getMessage().startsWith("/ban ")) {
				String playerNameAndReason = event.getMessage().substring(5);
				event.setMessage("/permaban " + playerNameAndReason);
			} else {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "Usage: /ban <Player> (reason)");
			}
		}
	}

	@EventHandler
	public void onConsoleCommand(ServerCommandEvent event) {
		if (event.getCommand().startsWith("pardon")) {
			if (event.getCommand().startsWith("pardon ")) {
				String playerName = event.getCommand().substring(7);
				event.setCommand("tempban " + playerName + " 0");
			} else {
				event.setCancelled(true);
				event.getSender().sendMessage(ChatColor.RED + "Usage: /pardon <Player>");
			}
		}
		if (event.getCommand().startsWith("ban")) {
			if (event.getCommand().startsWith("ban ")) {
				String playerNameAndReason = event.getCommand().substring(4);
				event.setCommand("permaban " + playerNameAndReason);
			} else {
				event.setCancelled(true);
				event.getSender().sendMessage(ChatColor.RED + "Usage: /ban <Player> (reason)");
			}
		}
	}
}