package fr.nessar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.plugin.java.JavaPlugin;

public class Staff extends JavaPlugin implements Listener {

    private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "Staff" + ChatColor.GRAY + "] "
            + ChatColor.RESET;
    private List<Report> reports;
    private DataManager dManager;
    private List<Player> inStaffMod;
    private List<Inventory> inventories;

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        this.dManager = new DataManager();
        this.reports = this.dManager.loadReports();
        if (this.reports == null) {
            Bukkit.getServer().getConsoleSender()
                    .sendMessage(Staff.PREFIX + ChatColor.RED + "ERROR CANNOT LOAD DATA, CREATING EMPTY ONE");
            this.reports = new ArrayList<>();
        }
        this.inStaffMod = new ArrayList<Player>();
        this.inventories = new ArrayList<Inventory>();
        getCommand("staff").setExecutor(new CommandStaffExecutor(this));
        getCommand("staff").setTabCompleter(new StaffTabCompletion(this));
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getConsoleSender().sendMessage(Staff.PREFIX + ChatColor.GREEN + "Loaded.");
    }

    @Override
    public void onDisable() {
        this.dManager.saveReports(this.reports);
        Bukkit.getConsoleSender().sendMessage(Staff.PREFIX + ChatColor.RED + "Reports saved.");
        Bukkit.getConsoleSender().sendMessage(Staff.PREFIX + ChatColor.RED + "Unloaded.");
    }

    public List<ItemStack> saveArmor(Player p) {
        final List<ItemStack> armorCont = new ArrayList<>();
        for (ItemStack stack : p.getInventory().getArmorContents()) {
            if (stack != null)
                armorCont.add(stack);
        }
        return armorCont;
    }

    public List<ItemStack> saveInv(Player p) {
        final List<ItemStack> invCont = new ArrayList<>();
        for (ItemStack stack : p.getInventory().getContents()) {
            if (stack != null)
                invCont.add(stack);
        }
        return invCont;
    }

    public void saveAllInv(Player p) {
        this.inventories.add(new Inventory(p, saveInv(p), saveArmor(p)));
    }

    public void deleteSavedInv(Player p) {
        for (int i = 0; i < this.inventories.size(); i++) {
            if (this.inventories.get(i).getOwner().getUniqueId().equals(p.getUniqueId())) {
                this.inventories.remove(i);
            }
        }
    }

    public void clear(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[4]);
    }

    public void setStaffItem(Player p) {
        // p.getInventory().setContents();
    }

    public void setStaffMod(Player p) {
    }

    public boolean isInStaffMod(Player p) {
        for (Player p2 : this.inStaffMod) {
            if (p2.getUniqueId().equals(p.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public void changeStaffMod(Player p) {

    }

    public List<Report> getReports() {
        return this.reports;
    }

    public static String getPREFIX() {
        return Staff.PREFIX;
    }
}
