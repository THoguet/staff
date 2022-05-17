package fr.nessar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
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
            Bukkit.getConsoleSender()
                    .sendMessage((punish.isActive() ? ChatColor.GREEN : ChatColor.RED) + punish.getMessage());
            Bukkit.getConsoleSender().sendMessage(punish.getPunishedUUID().toString());
        }
        new UpdateStaffPerm(this).runTaskTimerAsynchronously(this, 0, 200);
        setupPermissions();
        CommandExecutor punishExec = new CommandPunishExecutor(this);
        getCommand("staff").setExecutor(new CommandStaffExecutor(this));
        getCommand("freeze").setExecutor(new CommandFreezeExecutor(this));
        getCommand("unfreeze").setExecutor(new CommandFreezeExecutor(this));
        getCommand("report").setExecutor(new CommandReportExecutor(this));
        getCommand("ticket").setExecutor(new CommandTicketExecutor(this));
        getCommand("tempban").setExecutor(punishExec);
        getCommand("permaban").setExecutor(punishExec);
        getCommand("tempmute").setExecutor(punishExec);
        getCommand("permamute").setExecutor(punishExec);
        getCommand("tempticket").setExecutor(punishExec);
        getCommand("permaticket").setExecutor(punishExec);
        getCommand("tempreport").setExecutor(punishExec);
        getCommand("permareport").setExecutor(punishExec);
        // getCommand("staff").setTabCompleter(new StaffTabCompletion(this));
        this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
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

    public void notifyStaff(String message) {
        for (OfflinePlayer offlinePlayer : staffList) {
            Player isOnline = Bukkit.getPlayer(offlinePlayer.getUniqueId());
            if (isOnline != null)
                isOnline.sendMessage(message);
        }
    }

    public static String getNumberPlusZero(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return String.valueOf(time);
    }

    public int isPunished(Player p, PunishType pType) {
        for (int i = 0; i < punishments.size(); i++) {
            if (punishments.get(i).getPunishedUUID().equals(p.getUniqueId()) && punishments.get(i).isActive()
                    && punishments.get(i).getpType().equals(pType))
                return i;
        }
        return -1;
    }

    public void setStaff(List<OfflinePlayer> newlist) {
        this.staffList = newlist;
    }

    public List<OfflinePlayer> getStaff() {
        return this.staffList;
    }

    public boolean editPunishment(int id, UUID punishedPlayer, Player editor, long endtime) {
        String nameEditor = "Console";
        if (editor != null)
            nameEditor = editor.getName();
        try {
            if (endtime != -1)
                punishments.get(id).setEndTime(endtime);
            punishments.get(id)
                    .setmessage(punishments.get(id).getMessage() + ChatColor.GRAY + "(Modifié par " + nameEditor + ")");
            Database.updatePunishment(punishments.get(id), id + 1);
        } catch (SQLException e) {
            if (editor == null)
                Bukkit.getConsoleSender()
                        .sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Impossible de modifier la sanction.");
            else
                editor.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Impossible de modifier la sanction.");
            return false;
        } catch (IndexOutOfBoundsException e) {
            if (editor == null)
                Bukkit.getConsoleSender()
                        .sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Impossible de modifier la sanction.");
            else
                editor.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.RED + "Impossible de modifier la sanction.");
            return false;
        }
        if (editor == null)
            Bukkit.getConsoleSender()
                    .sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.GREEN + "Sanction #" + id + " modifié.");
        else
            editor.sendMessage(Staff.getSTAFF_PREFIX() + ChatColor.GREEN + "Sanction #" + id + " modifié.");
        return true;
    }

    public int getActivePunishId(UUID punishedPlayer, PunishType pType) {
        for (int i = 0; i < punishments.size(); i++) {
            Punishment p = this.punishments.get(i);
            if (p.getPunishedUUID().equals(punishedPlayer) && p.getpType().equals(pType) && p.isActive())
                return i;
        }
        return -1;
    }

    public void newPunishment(Player from, UUID to, String message, PunishType pType, long startTime, long endtime,
            int reportId) {
        if (message == null || message.equals("")) {
            message = "Aucune raison spécifié.";
        }
        UUID UUIDfrom = null;
        if (from != null) {
            UUIDfrom = from.getUniqueId();
        }
        Punishment newPunish = new Punishment(to, UUIDfrom, message, pType, startTime, endtime, reportId);
        try {
            Database.addPunishmentToDB(newPunish);
        } catch (SQLException e) {
            from.sendMessage(
                    REPORT_PREFIX + ChatColor.RED + "Votre sanction n'a pas pu être prise en compte désolé." + e);
            Bukkit.getConsoleSender().sendMessage(
                    REPORT_PREFIX + ChatColor.RED + "Ajout a la DB impossible, punish non pris en compte." + e);
            return;
        }
        if (from == null)
            Bukkit.getConsoleSender().sendMessage(
                    Staff.getSTAFF_PREFIX() + ChatColor.GREEN + "Votre sanction a bien été prise en compte !");
        else
            from.sendMessage(
                    Staff.getSTAFF_PREFIX() + ChatColor.GREEN + "Votre sanction a bien été prise en compte !");
        this.punishments.add(newPunish);
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
        reporter.sendMessage(Staff.getREPORT_PREFIX() + ChatColor.GREEN + "Votre report a bien été pris en compte !");
        this.reports.add(freshReport);
        this.notifyStaff(Staff.getREPORT_PREFIX() + ChatColor.GREEN + "Nouveau "
                + freshReport.getChatMessage(this.reports.size() - 1)); // TODO
    }

    public void updateReport(int indexReportToUpdate, Report newReport, Player updater) {
        try {
            Database.updateReport(newReport, indexReportToUpdate + 1);
        } catch (SQLException e) {
            updater.sendMessage(
                    REPORT_PREFIX + ChatColor.RED + "Le report n'a pas pu être mis à jour désolé." + e);
            Bukkit.getConsoleSender().sendMessage(
                    REPORT_PREFIX + ChatColor.RED + "Edit DB impossible, update report non pris en compte." + e);
            return;
        }
        updater.sendMessage(Staff.getREPORT_PREFIX() + ChatColor.GREEN + "Le report a bien été mis à jour !");
        this.reports.set(indexReportToUpdate, newReport);
        Player reporter = Bukkit.getPlayer(UUID.fromString(newReport.getReporter().getUniqueId()));
        if (reporter != null)
            reporter.sendMessage(Staff.getREPORT_PREFIX() + "Votre report a été actualisé"); // TODO
    }

    public void newTicket(Player reporter, String reportReason) {
        Report freshTicket = new Report(reporter, null, reportReason, false);
        try {
            Database.addReportToDB(freshTicket);
        } catch (SQLException e) {
            reporter.sendMessage(REPORT_PREFIX + "Votre ticket n'a pas pu être pris en compte désolé.");
            Bukkit.getConsoleSender().sendMessage(
                    REPORT_PREFIX + ChatColor.RED + "Ajout a la DB impossible, ticket non pris en compte.");
            return;
        }
        reporter.sendMessage(Staff.getREPORT_PREFIX() + ChatColor.GREEN + "Votre ticket a bien été pris en compte !");
        this.reports.add(freshTicket);
        this.notifyStaff(Staff.getREPORT_PREFIX() + ChatColor.GREEN + "Nouveau "
                + freshTicket.getChatMessage((this.reports.size() - 1)).toLegacyText()); // TODO
    }

    public static String getUrlDB() {
        return Staff.urlDB;
    }

    public int getNbReport() {
        return this.reports.size();
    }

    public boolean isLastReportInCoolDown(Player p, boolean report) {
        for (int i = reports.size() - 1; i > 0; i--) {
            if (reports.get(i).getReporter().getUniqueId().equals(p.getUniqueId().toString())
                    && reports.get(i).isReport() == report)
                return reports.get(i).isInCooldown();
        }
        return false;
    }

    public static int getNumberAtEndStr(String analyze) {
        String toConvert = "";
        for (int i = analyze.length() - 1; i > 0; i--) {
            char charTest = analyze.charAt(i);
            if ((int) charTest >= 48 && (int) charTest <= 57) {
                toConvert = String.join("", charTest + "", toConvert);
            } else
                return Integer.parseInt(toConvert);
        }
        return Integer.parseInt(toConvert);
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
