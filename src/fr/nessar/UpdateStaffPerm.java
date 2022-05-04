package fr.nessar;

import org.bukkit.scheduler.BukkitRunnable;

public class UpdateStaffPerm extends BukkitRunnable {
	private Staff plugin;

	public UpdateStaffPerm(Staff plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		new getStaffTask(this.plugin, this.plugin.getPerms()).run();
	}
}
