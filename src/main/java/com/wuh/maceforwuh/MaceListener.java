package com.wuh.maceforwuh;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

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

        // 1. Check if it's the custom item using CustomModelData
        ItemStack heldItem = damager.getInventory().getItemInMainHand();
        if (!isWuhsMace(heldItem)) {
            return;
        }

        // 2. Check if the damager is falling
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
                return; 
            }
        }

        // 5. BREAK THE SHIELD
        ItemStack main = shieldHolder.getInventory().getItemInMainHand();
        ItemStack off = shieldHolder.getInventory().getItemInOffHand();

        if (main != null && main.getType() == Material.SHIELD) {
            shieldHolder.getInventory().setItemInMainHand(null); // Cleaner than setAmount(0)
        } else if (off != null && off.getType() == Material.SHIELD) {
            shieldHolder.getInventory().setItemInOffHand(null);
        }
        
        // 6. Launch damager upward
        damager.setVelocity(new Vector(0, 1.2, 0));

        // Set cooldown and notify
        cooldowns.put(damagerUUID, currentTime);
        damager.sendMessage(ChatColor.GOLD + "SHIELD CRUSHED!");
        shieldHolder.sendMessage(ChatColor.RED + "Your shield was shattered!");

        event.setDamage(0);
    }

    private boolean isWuhsMace(ItemStack item) {
        if (item == null || item.getType() != Material.DIAMOND_HOE || !item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        // This makes it "un-fakeable" by checking the secret ID number
        return meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 123456;
    }

    private boolean isFalling(Player player) {
        return player.getFallDistance() > 0.0F && !player.isOnGround();
    }
}
