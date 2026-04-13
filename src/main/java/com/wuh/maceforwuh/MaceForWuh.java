package com.wuh.maceforwuh;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MaceForWuh extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.GOLD + "=== Wuh's Mace Plugin Enabled ===");
        
      
        getServer().getPluginManager().registerEvents(new MaceListener(this), this);
        
        // Register command
        getCommand("wuhsmace").setExecutor(this);
        
        getLogger().info(ChatColor.GOLD + "Wuh's Mace is ready!");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "Wuh's Mace Plugin Disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("wuhsmace")) {
            ItemStack mace = createWuhsMace();
            player.getInventory().addItem(mace);
            player.sendMessage(ChatColor.GOLD + "You received " + ChatColor.DARK_PURPLE + "Wuh's Mace" + ChatColor.GOLD + "!");
            player.sendMessage(ChatColor.YELLOW + "Fall while holding it to break shields and get launched up 5 blocks!");
            return true;
        }

        return false;
    }

    public ItemStack createWuhsMace() {
        ItemStack mace = new ItemStack(Material.MACE);
        ItemMeta meta = mace.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_PURPLE + "Wuh's Mace");
            meta.setLore(java.util.Arrays.asList(
                ChatColor.YELLOW + "Use while falling to:",
                ChatColor.YELLOW + "- Break shields in one hit",
                ChatColor.YELLOW + "- Launch yourself up 5 blocks",
                ChatColor.GRAY + "Cooldown: 30 seconds"
            ));
            mace.setItemMeta(meta);
        }
        
        return mace;
    }
}
