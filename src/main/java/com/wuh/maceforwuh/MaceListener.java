package com.wuh.maceforwuh;

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
    private static final long COOLDOWN_MS = 30000;

    public MaceListener(MaceForWuh plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        Player shieldHolder = (Player) event.getEntity();

        // 1. Check if it's the custom item
        ItemStack heldItem = damager.getInventory().getItemInMainHand();
        if (!isWuhsMace(heldItem)) {
            return;
        }

        // 2. Check if the damager is falling (Critting)
        if (!isFalling(damager)) {
            return;
        }

        // 3. Check if the victim is actually blocking with a shield
        if (!shieldHolder.isBlocking()) {
            return;
        }

        // 4. Cooldown Check
        UUID damagerUUID = damager.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        if (cooldowns.containsKey(damagerUUID)) {
            long lastUse = cooldowns.get(damagerUUID);
            if (currentTime - lastUse < COOLDOWN_MS) {
                long remaining = (COOLDOWN_MS - (currentTime - lastUse)) / 1000;
                damager.sendMessage(ChatColor.RED + "Shield break on cooldown! (" + remaining + "s)");
                return; // Allow normal damage, just don't break the shield
            }
        }

        // 5. BREAK THE SHIELD
        // We find the shield (could be main hand or offhand)
        ItemStack main = shieldHolder.getInventory().getItemInMainHand();
        ItemStack off = shieldHolder.getInventory().getItemInOffHand();

        if (main.getType() == Material.SHIELD) {
            main.setAmount(0);
        } else if (off.getType() == Material.SHIELD) {
            off.setAmount(0);
        }
        
        // 6. Launch damager upward (1.2 is roughly 5-6 blocks)
        damager.setVelocity(new Vector(0, 1.2, 0));

        // Set cooldown and notify
        cooldowns.put(damagerUUID, currentTime);
        damager.sendMessage(ChatColor.GOLD + "SHIELD CRUSHED!");
        shieldHolder.sendMessage(ChatColor.RED + "Your shield was shattered!");

        // Cancel the actual heart damage so the shield break is the primary effect
        event.setDamage(0);
    }

    private boolean isWuhsMace(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String displayName = item.getItemMeta().getDisplayName();
        // Using ChatColor.stripColor helps if you use colors in your item name!
        return displayName != null && ChatColor.stripColor(displayName).contains("Wuh's Mace");
    }

    private boolean isFalling(Player player) {
        // Fall distance > 0 is a very reliable way to check if they are falling/jumping
        return player.getFallDistance() > 0.0F && !player.isOnGround();
    }
}
