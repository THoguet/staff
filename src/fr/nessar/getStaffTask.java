package fr.nessar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.permission.Permission;

public class getStaffTask extends BukkitRunnable {

	Staff plugin;
	Permission perm;

	public getStaffTask(Staff plugin, Permission perm) {
		this.plugin = plugin;
		this.perm = perm;
	}

	@Override
	public void run() {
		List<OfflinePlayer> staff = new ArrayList<>();
		List<OfflinePlayer> offlineP = Arrays.asList(Bukkit.getOfflinePlayers());

		for (OfflinePlayer p : offlineP) {
			if (this.perm.playerHas("world", p, "staff"))
				staff.add(p);
		}
		this.plugin.setStaff(staff);
	}
}
