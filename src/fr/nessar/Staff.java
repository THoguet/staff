package fr.nessar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.milkbowl.vault.permission.Permission;

public class Staff extends JavaPlugin implements Listener {

    private Permission perms = null;
    private Location tpHereWhenDcInStaffMod;
    private static String urlDB;
    private static String STAFF_PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "Staff" + ChatColor.GRAY + "] "
            + ChatColor.RESET;
    private static String REPORT_PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "Report" + ChatColor.GRAY + "] "
            + ChatColor.RESET;
    private List<Report> reports;
    private List<Template> templates;
    private List<Punishment> punishments;
    private List<StaffMod> inStaffMod;
    private List<Froze> frozen;
    private List<Menu> openMenu;
    private List<OfflinePlayer> staffList;

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
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
        this.inStaffMod = new ArrayList<StaffMod>();
        this.frozen = new ArrayList<Froze>();
        this.openMenu = new ArrayList<Menu>();
        this.staffList = new ArrayList<OfflinePlayer>();
        new UpdateStaffPerm(this).runTaskTimerAsynchronously(this, 0, 200);
        setupPermissions();
        getCommand("staff").setExecutor(new CommandStaffExecutor(this));
        getCommand("freeze").setExecutor(new CommandFreezeExecutor(this));
        getCommand("unfreeze").setExecutor(new CommandFreezeExecutor(this));
        // getCommand("staff").setTabCompleter(new StaffTabCompletion(this));
        Bukkit.getServer().getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.GREEN + "Loaded.");
        getServer().getPluginManager().registerEvents(this, this);
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
        try {
            for (Report report : this.reports) {
                Database.addReportToDB(report);
            }
            Bukkit.getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.GREEN + "Reports saved.");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.RED + "Reports cannot be saved.");
        }
        try {
            for (Template template : this.templates) {
                Database.addTemplateToDB(template);
            }
            Bukkit.getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.GREEN + "Templates saved.");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.RED + "Templates cannot be saved.");
        }
        try {
            for (Punishment punishment : this.punishments) {
                Database.addPunishmentToDB(punishment);
            }
            Bukkit.getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.GREEN + "Punishments saved.");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.RED + "Punishments cannot be saved.");
        }
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
            reporter.sendMessage(REPORT_PREFIX + "Votre report n'a pas pu être pris en compte désolé.");
            Bukkit.getConsoleSender().sendMessage(
                    REPORT_PREFIX + ChatColor.RED + "Ajout a la DB impossible, report non pris en compte.");
            return;
        }
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
            this.openMenu.get(toload.indexOpenMenu).closeMenu();
            this.openMenu.remove(toload.indexOpenMenu);
            this.frozen.remove(indexFroze);
            p.removePotionEffect(PotionEffectType.SLOW);
            toload.loadInv();
        } else {
            this.openMenu.add(new Menu(p, MenuType.FREEZE, this));
            this.frozen.add(new Froze(p, this.openMenu.size() - 1));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 255, true, true));
        }
    }

    @EventHandler
    public void onStaffQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (this.isInStaffMod(p) != -1) {
            toggleStaffMod(p);
            p.teleport(this.tpHereWhenDcInStaffMod);
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        int indexStaffMod = isInStaffMod(p);
        if (indexStaffMod != -1) {
            if (inStaffMod.get(indexStaffMod).isStaffInv())
                event.setCancelled(true);
            else {
                if (event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(
                        ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInventoryDragEvent(InventoryDragEvent event) {
        Player p = (Player) event.getWhoClicked();
        int indexStaffMod = isInStaffMod(p);
        if (indexStaffMod != -1) {
            if (inStaffMod.get(indexStaffMod).isStaffInv())
                event.setCancelled(true);
            else {
                if (event.getOldCursor().getItemMeta().getDisplayName().equals(
                        ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInventoryDragEvent(PlayerPickupItemEvent event) {
        Player p = (Player) event.getPlayer();
        int indexStaffMod = isInStaffMod(p);
        if (indexStaffMod != -1) {
            if (inStaffMod.get(indexStaffMod).isStaffInv())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSneaking(PlayerToggleSneakEvent event) {
        Player p = event.getPlayer();
        int indexStaffMod = isInStaffMod(p);
        if (indexStaffMod != -1) {
            inStaffMod.get(indexStaffMod).toggleSneakStatus();
        }
    }

    @EventHandler
    public void onPlayerInventoryInteractEvent(InventoryInteractEvent event) {
        Player p = (Player) event.getWhoClicked();
        int indexStaffMod = isInStaffMod(p);
        if (indexStaffMod != -1) {
            if (inStaffMod.get(indexStaffMod).isStaffInv())
                event.setCancelled(true);
            else {
                if (!p.getInventory().getItem(8).getItemMeta().getDisplayName().equals(
                        ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInventoryCreativeEvent(InventoryCreativeEvent event) {
        Player p = (Player) event.getWhoClicked();
        int indexStaffMod = isInStaffMod(p);
        if (indexStaffMod != -1) {
            if (inStaffMod.get(indexStaffMod).isStaffInv())
                event.setCancelled(true);
            else {
                if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta().getDisplayName()
                        .equals(ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player p = (Player) event.getWhoClicked();
            int indexStaffMod = isInStaffMod(p);
            if (indexStaffMod != -1) {
                if (inStaffMod.get(indexStaffMod).isStaffInv())
                    event.setCancelled(true);
                else {
                    if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta().getDisplayName().equals(
                            ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off"))
                        event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPlayerInteractEvent(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        int indexStaffMod = isInStaffMod(p);
        if (indexStaffMod != -1) {
            if (event.getItem().getItemMeta().getDisplayName().equals(
                    ChatColor.GOLD + "Staff Inv" + ChatColor.GRAY + ": " + ChatColor.RED + "Off")
                    || inStaffMod.get(indexStaffMod).isStaffInv()) {
                event.setCancelled(true);
                inStaffMod.get(indexStaffMod).toggleOrUseSlot(event.getMaterial());
            }
        }
    }

    @EventHandler
    public void onPlayerPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        int indexStaffMod = isInStaffMod(p);
        if (indexStaffMod != -1) {
            event.setCancelled(true);
            if (p.getItemInHand().getType().equals(Material.ICE)
                    || p.getItemInHand().getType().equals(Material.PACKED_ICE)) {
                if (event.getRightClicked() instanceof Player) {
                    Player target = (Player) event.getRightClicked();
                    this.toggleFreeze(target);
                }
            }
        }
    }

    public List<Report> getReports() {
        return this.reports;
    }

    public static String getSTAFF_PREFIX() {
        return Staff.STAFF_PREFIX;
    }

    public static String getREPORT_PREFIX() {
        return Staff.REPORT_PREFIX;
    }
}
