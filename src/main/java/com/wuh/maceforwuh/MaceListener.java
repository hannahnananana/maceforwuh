package com.wuh.maceforwuh;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MaceListener implements Listener {

    private final MaceForWuh plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_MS = 30000; // 30 seconds

    public MaceListener(MaceForWuh plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Check if damager is a player
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        
        // Check if the damaged entity is a player (shield holder)
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player shieldHolder = (Player) event.getEntity();

        // Check if damager is holding Wuh's Mace
        ItemStack heldItem = damager.getInventory().getItemInMainHand();
        if (!isWuhsMace(heldItem)) {
            return;
        }

        // Check if damager is falling
        if (!isFalling(damager)) {
            event.setCancelled(true);
            return;
        }

        // Check if shield holder is blocking (holding shield)
        ItemStack offhand = shieldHolder.getInventory().getItemInOffHand();
        if (offhand.getType() != Material.SHIELD) {
            return;
        }

        // Check cooldown
        UUID damagerUUID = damager.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        if (cooldowns.containsKey(damagerUUID)) {
            long lastUse = cooldowns.get(damagerUUID);
            if (currentTime - lastUse < COOLDOWN_MS) {
                long remaining = (COOLDOWN_MS - (currentTime - lastUse)) / 1000;
                damager.sendMessage(ChatColor.RED + "Shield break on cooldown! (" + remaining + "s)");
                event.setCancelled(true);
                return;
            }
        }

        // Break the shield
        offhand.setType(Material.AIR);
        shieldHolder.getInventory().setItemInOffHand(offhand);
        
        // Launch damager upward 5 blocks
        Vector velocity = damager.getVelocity();
        velocity.setY(Math.sqrt(2 * 9.81 * 5)); // Physics formula to reach 5 blocks height
        damager.setVelocity(velocity);

        // Set cooldown
        cooldowns.put(damagerUUID, currentTime);

        // Send messages
        damager.sendMessage(ChatColor.GOLD + "Shield broken! You've been launched!");
        shieldHolder.sendMessage(ChatColor.RED + "Your shield was broken by " + damager.getName() + "!");

        // Cancel normal damage
        event.setDamage(0);
    }

private boolean isWuhsMace(ItemStack item) { // <--- Changed to match line 45
    if (item == null || !item.hasItemMeta()) {
        return false;
    }
    String displayName = item.getItemMeta().getDisplayName();
    return displayName != null && displayName.contains("Wuh's Mace");
}
    private boolean isFalling(Player player) {
        // Player is falling if they are not on ground and have negative Y velocity
        return !player.isOnGround() && player.getVelocity().getY() < 0;
    }
}
