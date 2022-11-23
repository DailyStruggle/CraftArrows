package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Listener.CraftArrowListener;
import io.github.dailystruggle.craftarrows.Util.VectorHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class VortexArrowVariant extends ArrowVariant {
    public VortexArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Vortex");
    }

    protected void loadDetails(FileConfiguration config) {
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        createVortex(event.getEntity(), event.getHitEntity());
    }

    public void onBlockHit(ProjectileHitEvent event) {
        createVortex(event.getEntity(), null);
    }

    private void createVortex(Entity arrow, final Entity hitEntity) {
        final Location hitLocation = arrow.getLocation();
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                for (Entity entity : hitEntity.getNearbyEntities(5.0D, 5.0D, 5.0D)) {
                    if (entity != hitEntity && !CraftArrowListener.ignoredEntities.contains(entity))
                        entity.setVelocity(VectorHelper.getVectorBetween(entity.getLocation(), (hitEntity != null) ? hitEntity.getLocation() : hitLocation, 0.0D, 0.5D));
                }
            }
        };
        BukkitTask task = runnable.runTaskTimer(CraftArrows.instance, 2L, 2L);
        Bukkit.getScheduler().runTaskLater(CraftArrows.instance, () -> Bukkit.getScheduler().cancelTask(task.getTaskId()), 20L);
    }
}
