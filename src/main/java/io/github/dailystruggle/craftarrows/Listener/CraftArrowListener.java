package io.github.dailystruggle.craftarrows.Listener;

import io.github.dailystruggle.craftarrows.Arrows.ArrowVariant;
import io.github.dailystruggle.craftarrows.Arrows.CombatArrowVariant;
import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Data.ArrowManager;
import io.github.dailystruggle.craftarrows.Objects.Properties;
import io.github.dailystruggle.craftarrows.Util.NmsHelper;
import io.github.dailystruggle.craftarrows.WorldGuard.NoCraftArrowFlag;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CraftArrowListener implements Listener {
    public static ArrayList<Entity> ignoredEntities = new ArrayList<>();

    public static ArrayList<Location> ignoredExplosions = new ArrayList<>();

    public static ArrayList<Arrow> removeArrowsOnHit = new ArrayList<>();

    @EventHandler(ignoreCancelled = true)
    public void onBowShoot(EntityShootBowEvent event) {
        if (event.getProjectile() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getProjectile();
            if (event.getEntity() instanceof Player) {
                ItemStack arrowItem = getArrowBeingShot((Player) event.getEntity());
                if (arrowItem != null && arrowItem.hasItemMeta()) {
                    ItemMeta itemMeta = Objects.requireNonNull(arrowItem.getItemMeta());
                    ArrowVariant variant = CraftArrows.getArrowVariantForItemName(itemMeta.getDisplayName().trim());
                    if (variant != null) {
                        if (Properties.UsePermissions && !event.getEntity().hasPermission("craftarrow." + variant.getName().toLowerCase()) && !event.getEntity().hasPermission("craftarrow.all")) {
                            event.getEntity().sendMessage(ChatColor.RED + "[CraftArrows] : You don't have permission to shoot, " + variant.getName() + " arrows");
                            event.setCancelled(true);
                            return;
                        }

                        ArrowManager.RegisterArrow(arrow, variant);
                        variant.onShoot(event);
                        if (!Properties.InfinityBowWorks && ((Player) event.getEntity()).getGameMode() != GameMode.CREATIVE) {
                            ItemStack mainHand = ((Player) event.getEntity()).getInventory().getItemInMainHand();
                            ItemStack offHand = ((Player) event.getEntity()).getInventory().getItemInOffHand();
                            if (mainHand.containsEnchantment(Enchantment.ARROW_INFINITE) || offHand.containsEnchantment(Enchantment.ARROW_INFINITE))
                                arrowItem.setAmount(arrowItem.getAmount() - 1);
                        }
                    }
                    else new IllegalStateException("unexpected arrow name - " + itemMeta.getDisplayName().trim()
                            + " \n for table - " + CraftArrows.getAllArrowVariants().stream().map(ArrowVariant::getName).collect(Collectors.toList()))
                            .printStackTrace();
                }
            } else if (Properties.SkeletonCanShootArrow) {
                ArrowVariant variant = CraftArrows.getRandomArrowVariant();
                if (variant != null) {
                    ArrowManager.RegisterArrow(arrow, variant);
                    variant.onShoot(event);
                }
            }
        }
    }

    private ItemStack getArrowBeingShot(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.ARROW)
                return item;
        }
        return null;
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileHitEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow) {
            Entity arrow = event.getDamager();
            Entity target = event.getEntity();
            ArrowVariant variant = ArrowManager.GetVariant(arrow);
            if (variant != null && target instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) target;
                if (ignoredEntities.contains(entity)) {
                    event.setCancelled(true);
                    return;
                }
                if (NmsHelper.getSimpleVersion() <= 12 &&
                        CraftArrows.WorldGuardEnabled &&
                        NoCraftArrowFlag.Enabled && NoCraftArrowFlag.IsProtectedRegion(entity.getLocation()))
                    return;
                if (variant instanceof CombatArrowVariant) {
                    ((CombatArrowVariant) variant).onEntityHit(event, (Projectile) event.getDamager(), entity);
                    event.getDamager().remove();
                }
                arrow.remove();
            }
            Double damage = ArrowManager.GetDamage(arrow);
            if (damage != null)
                event.setDamage(damage.doubleValue());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow) {
            if (removeArrowsOnHit.contains(projectile))
                projectile.remove();
            Arrow arrow = (Arrow) event.getEntity();
            ArrowVariant variant = ArrowManager.GetVariant(arrow);
            if (variant != null) {
                Entity entity = event.getHitEntity();
                Block block = event.getHitBlock();
                if (block == null)
                    block = event.getEntity().getLocation().getBlock();
                if (ignoredEntities.contains(entity))
                    return;
                if (NmsHelper.getSimpleVersion() <= 12 &&
                        CraftArrows.WorldGuardEnabled &&
                        NoCraftArrowFlag.Enabled && NoCraftArrowFlag.IsProtectedRegion(arrow.getLocation()))
                    return;
                if (entity != null) {
                    variant.onEntityHit(event);
                } else if (block != null) {
                    variant.onBlockHit(event);
                }
                arrow.remove();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosionDamage(EntityDamageEvent event) {
        if ((event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || event
                .getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) &&
                ignoredEntities.contains(event.getEntity())) {
            Location entityLocation = event.getEntity().getLocation();
            for (Location loc : ignoredExplosions) {
                if (loc.distanceSquared(entityLocation) < 25.0D) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSkeletonDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Skeleton)
            if (Properties.SkeletonsDropArrows) {
                if (Properties.SkeletonsDropArrowsOnlyOnPlayerKill &&
                        event.getEntity().getKiller() == null)
                    return;
                List<ItemStack> drops = event.getDrops();
                for (int i = 0; i < drops.size(); i++) {
                    ItemStack item = drops.get(i);
                    if (item != null &&
                            item.getType() == Material.ARROW)
                        drops.set(i, CraftArrows.getRandomArrowDrop());
                }
            }
    }

    public void onEntityExplosion(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof org.bukkit.entity.WitherSkull &&
                entity.hasMetadata("BreakBlocks") && entity.getMetadata("BreakBlocks").get(0).asBoolean())
            event.blockList().clear();
    }
}
