package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class JackOArrow extends ArrowVariant {
    private final boolean dropPreviousHelmet = false;
    private boolean placeBlock = true;
    private double damage = 3.0D;
    private boolean changeHelmet = true;

    public JackOArrow(FileConfiguration config) {
        super(config, "Recipes.", "JackO", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.JackO.PlaceBlock", true));
                this.add(new ConfigValue<>("Recipes.JackO.Damage", 3.0));
                this.add(new ConfigValue<>("Recipes.JackO.ChangeHelmet", true));
                this.add(new ConfigValue<>("Recipes.JackO.DropPreviousHelmet", false));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.placeBlock = ((Boolean) configValues.get(0).getValue(config)).booleanValue();
        this.damage = Convert.Convert(Double.class, configValues.get(1).getValue(config)).doubleValue();
        this.changeHelmet = ((Boolean) configValues.get(2).getValue(config)).booleanValue();
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        Entity entity = event.getHitEntity();
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).damage(this.damage);
            if (this.changeHelmet) {
                ItemStack helmet = ((LivingEntity) entity).getEquipment().getHelmet();
                if (helmet != null || helmet.getType() != Material.AIR)
                    if (this.dropPreviousHelmet) {
                        entity.getWorld().dropItemNaturally(entity.getLocation(), helmet);
                    } else if (entity instanceof Player) {
                        ((Player) entity).getInventory().addItem(helmet);
                    }
                ((LivingEntity) entity).getEquipment().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
            }
        }
    }

    public void onBlockHit(ProjectileHitEvent event) {
        if (this.placeBlock) {
            Block block = event.getHitBlock();
            while (block.getType() != Material.AIR)
                block = block.getRelative(0, 1, 0);
            block.setType(Material.JACK_O_LANTERN);
        }
    }
}
