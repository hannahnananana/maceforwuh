package com.wuh.maceforwuh;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class MaceForWuh extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        // Register the listener
        getServer().getPluginManager().registerEvents(new MaceListener(this), this);
        
        // Register the command
        if (getCommand("wuhsmace") != null) {
            getCommand("wuhsmace").setExecutor(this);
        }
        
        getLogger().info(ChatColor.GOLD + "Wuh's Mace is ready and loaded!");
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

        // Check for permission (optional but recommended)
        if (!player.hasPermission("wuhsmace.give")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("wuhsmace")) {
            ItemStack mace = createWuhsMace();
            player.getInventory().addItem(mace);
            
            player.sendMessage(ChatColor.GOLD + "You received " + ChatColor.DARK_PURPLE + "Wuh's Mace" + ChatColor.GOLD + "!");
            player.sendMessage(ChatColor.YELLOW + "Fall and hit a blocking player to shatter their shield!");
            return true;
        }

        return false;
    }

    public ItemStack createWuhsMace() {
        // Using Diamond Hoe as the base
        ItemStack mace = new ItemStack(Material.DIAMOND_HOE);
        ItemMeta meta = mace.getItemMeta();
        
        if (meta != null) {
            // Set the custom name
            meta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Wuh's Mace");
            
            // Set the custom lore
            meta.setLore(Arrays.asList(
                "",
                ChatColor.YELLOW + "Ability: " + ChatColor.WHITE + "Shield Breaker",
                ChatColor.GRAY + "While falling, strike a blocking",
                ChatColor.GRAY + "player to shatter their shield",
                ChatColor.GRAY + "and launch yourself upward.",
                "",
                ChatColor.RED + "Cooldown: 30 Seconds"
            ));

            // Make it look clean: Hide the fact that it's a Diamond Hoe
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            
            mace.setItemMeta(meta);
        }
        
        return mace;
    }
}
