package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;

public class SoundArrowVariant extends ArrowVariant {
    private ArrayList<String> sounds;

    private int range;

    public SoundArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Sound", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Sound.Sounds", new ArrayList<String>() {
                    {
                        this.add("ENTITY_ENDERDRAGON_DEATH");
                        this.add("ENTITY_ENDERMEN_SCREAM");
                        this.add("ENTITY_BAT_TAKEOFF");
                        this.add("ENTITY_GHAST_SCREAM");
                    }
                }));
                this.add(new ConfigValue<>("Recipes.Sound.Range", -1));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.sounds = (ArrayList<String>) configValues.get(0).getValue(config);
        this.range = Convert.Convert(Integer.class, configValues.get(1).getValue(config)).intValue();
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        playSounds(event.getHitEntity().getLocation());
    }

    public void onBlockHit(ProjectileHitEvent event) {
        playSounds(event.getHitBlock().getLocation());
    }

    private void playSounds(Location loc) {
        long delay = 0L;
        if (this.range == -1) {
            for (String sound : this.sounds) {
                Sound s = Sound.valueOf(sound);
                Bukkit.getScheduler().scheduleSyncDelayedTask(CraftArrows.instance, () -> loc.getWorld().playSound(loc, s, 100.0F, 1.0F), delay);
                delay += 10L;
            }
        } else {
            for (Player player : loc.getWorld().getPlayers()) {
                if (player.getLocation().distanceSquared(loc) < (this.range * this.range)) {
                    delay = 0L;
                    for (String sound : this.sounds) {
                        Sound s = Sound.valueOf(sound);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(CraftArrows.instance, () -> player.playSound(loc, s, 100.0F, 1.0F), delay);
                        delay += 10L;
                    }
                }
            }
        }
    }
}
