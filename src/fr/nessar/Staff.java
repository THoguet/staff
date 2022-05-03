package fr.nessar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Staff extends JavaPlugin implements Listener {

    private static String urlDB;
    private static String STAFF_PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "Staff" + ChatColor.GRAY + "] "
            + ChatColor.RESET;
    private static String REPORT_PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "Report" + ChatColor.GRAY + "] "
            + ChatColor.RESET;
    private List<Report> reports;
    private List<StaffMod> inStaffMod;
    private List<Player> frozen;
    private static Inventory freezeInv = Menu.getFreeze();

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        this.inStaffMod = new ArrayList<StaffMod>();
        this.frozen = new ArrayList<Player>();
        Staff.urlDB = "jdbc:h2:" + getDataFolder().getAbsolutePath() + "/data/database";
        Database.initializeDatabase();
        try {
            this.reports = Database.loadReportsFromDB();
        } catch (Exception e) {
            Bukkit.getServer().getConsoleSender()
                    .sendMessage(Staff.STAFF_PREFIX + ChatColor.RED + "ERROR CANNOT LOAD DATA, DISABLING" + e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getCommand("staff").setExecutor(new CommandStaffExecutor(this));
        getCommand("freeze").setExecutor(new CommandFreezeExecutor(this));
        getCommand("unfreeze").setExecutor(new CommandFreezeExecutor(this));
        // getCommand("staff").setTabCompleter(new StaffTabCompletion(this));
        Bukkit.getServer().getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.GREEN + "Loaded.");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        for (StaffMod sm : this.inStaffMod) {
            toggleStaffMod(sm.getPlayer());
        }
        Bukkit.getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.RED + "Reports saved.");
        Bukkit.getConsoleSender().sendMessage(Staff.STAFF_PREFIX + ChatColor.RED + "Unloaded.");
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
            if (frozen.get(i).getUniqueId().equals(p.getUniqueId())) {
                return i;
            }
        }
        return -1;
    }

    public void toggleFreeze(Player p) {
        int indexFroze = isFrozen(p);
        if (indexFroze != -1) {
            this.frozen.remove(indexFroze);
            p.removePotionEffect(PotionEffectType.SLOW);
            p.closeInventory();
        } else {
            this.frozen.add(p);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 255, true, true));
            p.openInventory(Staff.freezeInv);
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
                if (!p.getInventory().getItem(8).getType().equals(Material.STICK))
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
                if (!p.getInventory().getItem(8).getType().equals(Material.STICK))
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
                if (!p.getInventory().getItem(8).getType().equals(Material.STICK))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPlayerInteractEvent(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        int indexStaffMod = isInStaffMod(p);
        if (indexStaffMod != -1) {
            if (event.getMaterial().equals(Material.STICK) || inStaffMod.get(indexStaffMod).isStaffInv()) {
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
            if (event.getRightClicked() instanceof Player) {
                Player target = (Player) event.getRightClicked();
                this.frozen.add(target);
            }
        }
    }

    public void sendFrozeMessage(Player p) {
        p.sendMessage(
                "" + ChatColor.GOLD + "████████████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD
                        + "████████████████");
        p.sendMessage("" + ChatColor.GOLD + "███████████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█"
                + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███████████████");
        p.sendMessage("" + ChatColor.GOLD + "██████████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███"
                + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "██████████████");
        p.sendMessage("" + ChatColor.GOLD + "█████████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████"
                + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████████████");
        p.sendMessage("" + ChatColor.GOLD + "████████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███████"
                + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "████████████");
        p.sendMessage("" + ChatColor.GOLD + "███████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "████"
                + ChatColor.DARK_RED + "██" + ChatColor.GOLD + "███" + ChatColor.DARK_RED
                + "█" + ChatColor.GOLD + "███████████");
        p.sendMessage("" + ChatColor.GOLD + "██████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "████"
                + ChatColor.DARK_RED
                + "████" + ChatColor.GOLD + "███" + ChatColor.DARK_RED
                + "█" + ChatColor.GOLD + "██████████");
        p.sendMessage("" + ChatColor.GOLD + "█████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████"
                + ChatColor.DARK_RED
                + "████" + ChatColor.GOLD + "████" + ChatColor.DARK_RED
                + "█" + ChatColor.GOLD + "█████████");
        p.sendMessage("" + ChatColor.GOLD + "████████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "██████"
                + ChatColor.DARK_RED
                + "████" + ChatColor.GOLD + "█████" + ChatColor.DARK_RED
                + "█" + ChatColor.GOLD + "████████");
        p.sendMessage("" + ChatColor.GOLD + "███████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███████"
                + ChatColor.DARK_RED
                + "████" + ChatColor.GOLD + "██████" + ChatColor.DARK_RED
                + "█" + ChatColor.GOLD + "███████");
        p.sendMessage("" + ChatColor.GOLD + "██████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████████"
                + ChatColor.DARK_RED + "██" + ChatColor.GOLD + "████████" + ChatColor.DARK_RED
                + "█" + ChatColor.GOLD + "██████");
        p.sendMessage(
                "" + ChatColor.GOLD + "█████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████████████████████"
                        + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████");
        p.sendMessage("" + ChatColor.GOLD + "████" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███████████"
                + ChatColor.DARK_RED + "██" + ChatColor.GOLD + "██████████" + ChatColor.DARK_RED
                + "█" + ChatColor.GOLD + "████");
        p.sendMessage("" + ChatColor.GOLD + "███" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "███████████"
                + ChatColor.DARK_RED
                + "████" + ChatColor.GOLD + "██████████" + ChatColor.DARK_RED
                + "█" + ChatColor.GOLD + "███");
        p.sendMessage("" + ChatColor.GOLD + "██" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████████████"
                + ChatColor.DARK_RED + "██" + ChatColor.GOLD + "████████████" + ChatColor.DARK_RED
                + "█" + ChatColor.GOLD + "██");
        p.sendMessage(
                "" + ChatColor.GOLD + "█" + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█████████████████████████████"
                        + ChatColor.DARK_RED + "█" + ChatColor.GOLD + "█");
        p.sendMessage("" + ChatColor.DARK_RED + "█████████████████████████████████");
    }

    public void cancelAndMessageFrozenPlayer(InventoryClickEvent event) {
        if (!event.isCancelled()) {
            Player p = (Player) event.getWhoClicked();
            if (isFrozen(p) != -1) {
                event.setCancelled(true);
                sendFrozeMessage(p);
            }
        }
    }

    public void cancelAndMessageFrozenPlayer(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if (isFrozen(p) != -1) {
            Bukkit.getServer().getScheduler().runTask(this, new Runnable() {
                @Override
                public void run() {
                    p.openInventory(Staff.freezeInv);
                }
            });
            sendFrozeMessage(p);
        }
    }

    @EventHandler
    public void frozePlayerEvent(PlayerArmorStandManipulateEvent event) {
        cancelAndMessageFrozenPlayer(event);
    }

    @EventHandler
    public void frozePlayerEvent(PlayerMoveEvent event) {
        cancelAndMessageFrozenPlayer(event);
    }

    private void cancelAndMessageFrozenPlayer(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (isFrozen(p) != -1) {
            event.setCancelled(true);
            sendFrozeMessage(p);
        }
    }

    private void cancelAndMessageFrozenPlayer(PlayerArmorStandManipulateEvent event) {
        Player p = event.getPlayer();
        if (isFrozen(p) != -1) {
            event.setCancelled(true);
            sendFrozeMessage(p);
        }
    }

    @EventHandler
    public void frozePlayerEvent(PlayerDropItemEvent event) {
        cancelAndMessageFrozenPlayer(event);
    }

    private void cancelAndMessageFrozenPlayer(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        if (isFrozen(p) != -1) {
            event.setCancelled(true);
            sendFrozeMessage(p);
        }
    }

    @EventHandler
    public void frozePlayerEvent(PlayerPickupItemEvent event) {
        cancelAndMessageFrozenPlayer(event);
    }

    private void cancelAndMessageFrozenPlayer(PlayerPickupItemEvent event) {
        Player p = event.getPlayer();
        if (isFrozen(p) != -1) {
            event.setCancelled(true);
            sendFrozeMessage(p);
        }
    }

    @EventHandler
    public void frozePlayerEvent(PlayerPortalEvent event) {
        cancelAndMessageFrozenPlayer(event);
    }

    private void cancelAndMessageFrozenPlayer(PlayerPortalEvent event) {
        Player p = event.getPlayer();
        if (isFrozen(p) != -1) {
            event.setCancelled(true);
            sendFrozeMessage(p);
        }
    }

    @EventHandler
    public void frozePlayerEvent(InventoryDragEvent event) {
        cancelAndMessageFrozenPlayer(event);
    }

    private void cancelAndMessageFrozenPlayer(InventoryDragEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (isFrozen(p) != -1) {
            event.setCancelled(true);
            sendFrozeMessage(p);
        }
    }

    @EventHandler
    public void frozePlayerEvent(InventoryCloseEvent event) {
        cancelAndMessageFrozenPlayer(event);
    }

    @EventHandler
    public void frozePlayerEvent(InventoryClickEvent event) {
        cancelAndMessageFrozenPlayer(event);
    }

    @EventHandler
    public void frozePlayerEvent(InventoryInteractEvent event) {
        cancelAndMessageFrozenPlayer(event);
    }

    private void cancelAndMessageFrozenPlayer(InventoryInteractEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (isFrozen(p) != -1) {
            event.setCancelled(true);
            sendFrozeMessage(p);
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
