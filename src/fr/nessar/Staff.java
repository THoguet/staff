package fr.nessar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nessar.Menu.Freeze;
import net.milkbowl.vault.permission.Permission;

public class Staff extends JavaPlugin {

    private Permission perms = null;
    private Location tpHereWhenDcInStaffMod;
    private static String urlDB;
    private static String STAFF_PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "Staff" + ChatColor.GRAY + "] "
            + ChatColor.RESET;
    private static String REPORT_PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "Report" + ChatColor.GRAY + "] "
            + ChatColor.RESET;
    private List<Report> reports = null;
    private List<Template> templates = null;
    private List<Punishment> punishments = null;
    private List<StaffMod> inStaffMod;
    private List<Froze> frozen;
    private List<OfflinePlayer> staffList;

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        this.inStaffMod = new ArrayList<StaffMod>();
        this.frozen = new ArrayList<Froze>();
        this.staffList = new ArrayList<OfflinePlayer>();
        if (this.getConfig().getBoolean("tpWorldSpawnWhenDCinStaffMod")) {
            this.tpHereWhenDcInStaffMod = Bukkit.getWorld("world").getSpawnLocation();
        } else {
            List<Integer> tpHereloc = this.getConfig().getIntegerList("tpHereWhenDCinStaffMod");
            this.tpHereWhenDcInStaffMod = new Location(Bukkit.getServer().getWorld("world"), tpHereloc.get(0),
                    tpHereloc.get(1), tpHereloc.get(2));
        }
        Staff.urlDB = "jdbc:h2:" + getDataFolder().getAbsolutePath() + "/data/database";
        Database.initializeDatabase();
        try {
            this.reports = Database.loadReportsFromDB();
            this.templates = Database.loadTemplatesFromDB();
            this.punishments = Database.loadPunishmentsFromDB();
        } catch (Exception e) {
            Bukkit.getServer().getConsoleSender()
                    .sendMessage(Staff.STAFF_PREFIX + ChatColor.RED + "ERROR CANNOT LOAD DATA, DISABLING" + e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        for (Report r : reports) {
            Bukkit.getConsoleSender().sendMessage(r.getReportReason());
        }
        for (Template t : templates) {
            Bukkit.getConsoleSender().sendMessage(t.getName());
        }
        for (Punishment punish : punishments) {
            Bukkit.getConsoleSender().sendMessage(punish.getMessage());
        }
        new UpdateStaffPerm(this).runTaskTimerAsynchronously(this, 0, 200);
        setupPermissions();
        getCommand("staff").setExecutor(new CommandStaffExecutor(this));
        getCommand("freeze").setExecutor(new CommandFreezeExecutor(this));
        getCommand("unfreeze").setExecutor(new CommandFreezeExecutor(this));
        getCommand("report").setExecutor(new CommandReportExecutor(this));
        // getCommand("staff").setTabCompleter(new StaffTabCompletion(this));
        this.getServer().getPluginManager().registerEvents(new InventoryEvents(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerEvent(this), this);
        Bukkit.getServer().getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.GREEN + "Loaded.");
    }

    @Override
    public void onDisable() {
        List<StaffMod> TODOStaff = new ArrayList<>(this.inStaffMod);
        for (StaffMod sm : TODOStaff) {
            if (sm != null)
                toggleStaffMod(sm.getPlayer());
        }
        Bukkit.getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.GREEN + "All staff mod are disabled");
        List<Froze> TODOFrozes = new ArrayList<>(this.frozen);
        for (Froze froze : TODOFrozes) {
            if (froze != null)
                toggleFreeze(froze.getFrozenPlayer());
        }
        Bukkit.getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.GREEN + "UnFreeze All players");
        Bukkit.getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.RED + "Unloaded.");
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public Permission getPerms() {
        return this.perms;
    }

    public void setStaff(List<OfflinePlayer> newlist) {
        this.staffList = newlist;
    }

    public List<OfflinePlayer> getStaff() {
        return this.staffList;
    }

    public void newReport(Player reporter, Player reported, String reportReason) {
        Report freshReport = new Report(reporter, reported, reportReason, true);
        try {
            Database.addReportToDB(freshReport);
        } catch (SQLException e) {
            reporter.sendMessage(
                    REPORT_PREFIX + ChatColor.RED + "Votre report n'a pas pu être pris en compte désolé." + e);
            Bukkit.getConsoleSender().sendMessage(
                    REPORT_PREFIX + ChatColor.RED + "Ajout a la DB impossible, report non pris en compte." + e);
            return;
        }
        reporter.sendMessage(Staff.getREPORT_PREFIX() + ChatColor.GREEN + "Votre report a bien été prit en compte !");
        this.reports.add(freshReport);
    }

    public void newTicket(Player reporter, Player reported, String reportReason) {
        Report freshTicket = new Report(reporter, reported, reportReason, false);
        try {
            Database.addReportToDB(freshTicket);
        } catch (SQLException e) {
            reporter.sendMessage(REPORT_PREFIX + "Votre ticket n'a pas pu être pris en compte désolé.");
            Bukkit.getConsoleSender().sendMessage(
                    REPORT_PREFIX + ChatColor.RED + "Ajout a la DB impossible, ticket non pris en compte.");
            return;
        }
        this.reports.add(freshTicket);
    }

    public static String getUrlDB() {
        return Staff.urlDB;
    }

    public int getNbReport() {
        return this.reports.size();
    }

    public int getNbReport(ReportStatus status, int classed, int report) {
        int ret = 0;
        boolean want_only_ticket = report == 0;
        boolean want_only_report = report == 1;
        boolean want_all_type = report == 2;
        boolean want_classed = classed == 1;
        boolean want_only_classed = classed == 2;
        for (Report r : this.reports) {
            if (r.getStatus().isGreaterOrEqualThan(status)
                    && (want_all_type || want_only_ticket == r.isTicket() || want_only_report == r.isReport())
                    && (want_classed || want_only_classed == r.getStatus().isClassed())) {
                ret++;
            }
        }
        return ret;
    }

    public void toggleStaffMod(Player p) {
        int indexStaffMod = isInStaffMod(p);
        if (indexStaffMod != -1) {
            this.inStaffMod.get(indexStaffMod).disableStaffMod();
            this.inStaffMod.remove(indexStaffMod);
        } else {
            this.inStaffMod.add(new StaffMod(this, p));
        }
    }

    public int isInStaffMod(Player p) {
        for (int i = 0; i < this.inStaffMod.size(); i++) {
            if (inStaffMod.get(i).isHim(p)) {
                return i;
            }
        }
        return -1;
    }

    public int isFrozen(Player p) {
        for (int i = 0; i < this.frozen.size(); i++) {
            if (frozen.get(i).getFrozenPlayer().getUniqueId().equals(p.getUniqueId())) {
                return i;
            }
        }
        return -1;
    }

    public void toggleFreeze(Player p) {
        int indexFroze = isFrozen(p);
        if (indexFroze != -1) {
            Froze toload = this.frozen.get(indexFroze);
            this.frozen.remove(indexFroze);
            p.removePotionEffect(PotionEffectType.SLOW);
            p.closeInventory();
            toload.loadInv();
        } else {
            this.frozen.add(new Froze(p));
            p.openInventory(new Freeze().getInventory());
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 255, true, false));
        }
    }

    public Location getLocToTp() {
        return this.tpHereWhenDcInStaffMod;
    }

    public List<StaffMod> getInStaffMod() {
        return this.inStaffMod;
    }

    public List<Froze> getFrozen() {
        return this.frozen;
    }

    public List<Report> getReports() {
        return this.reports;
    }

    public List<Punishment> getPunishments() {
        return this.punishments;
    }

    public static String getSTAFF_PREFIX() {
        return Staff.STAFF_PREFIX;
    }

    public static String getREPORT_PREFIX() {
        return Staff.REPORT_PREFIX;
    }
}
