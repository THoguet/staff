package fr.nessar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class StaffTabCompletion implements TabCompleter {

	private static final List<String> firstArg = new ArrayList<>(
			Arrays.asList("new", "delete", "list", "start", "stop", "setchest", "save", "reloadconfig"));
	private List<Report> reports;

	public StaffTabCompletion(Staff plugin) {
		this.reports = plugin.getReports();
	}

	@Override
	public List<String> onTabComplete(CommandSender sen, Command cmd, String alias, String[] args) {
		List<String> ret = new ArrayList<>();
		if (args.length == 1) {
			for (String st : firstArg) {
				if (st.startsWith(args[0]))
					ret.add(st);
			}
		}
		return ret;
	}

}
