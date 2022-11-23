package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import io.github.dailystruggle.craftarrows.Objects.ConfigValue;
import io.github.dailystruggle.craftarrows.Util.Convert;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Ladder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class LadderArrowVariant extends ArrowVariant {
    private final ArrayList<Material> replaceableBlocks = new ArrayList<Material>() {

    };
    private int PlacementDelay;
    private Particle ParticleOnPlacement;
    private int MaxLength;
    private int MaxOffset;
    private boolean OnlyForwards;
    private boolean PlaceInAir;

    public LadderArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Ladder", new ArrayList<ConfigValue<?>>() {
            {
                this.add(new ConfigValue<>("Recipes.Ladder.PlacementDelay", 2));
                this.add(new ConfigValue<>("Recipes.Ladder.ParticleOnPlacement", "SMOKE_NORMAL"));
                this.add(new ConfigValue<>("Recipes.Ladder.MaxLength", 8));
                this.add(new ConfigValue<>("Recipes.Ladder.MaxOffset", 1));
                this.add(new ConfigValue<>("Recipes.Ladder.OnlyForwards", true));
                this.add(new ConfigValue<>("Recipes.Ladder.PlaceInAir", true));
            }
        });
    }

    protected void loadDetails(FileConfiguration config) {
        ArrayList<ConfigValue<?>> configValues = getConfigValues();
        this.PlacementDelay = Convert.Convert(Integer.class, configValues.get(0).getValue(config)).intValue();
        this.ParticleOnPlacement = Particle.valueOf((String) configValues.get(1).getValue(config));
        this.MaxLength = Convert.Convert(Integer.class, configValues.get(2).getValue(config)).intValue();
        this.MaxOffset = Convert.Convert(Integer.class, configValues.get(3).getValue(config)).intValue();
        this.OnlyForwards = ((Boolean) configValues.get(4).getValue(config)).booleanValue();
        this.PlaceInAir = ((Boolean) configValues.get(5).getValue(config)).booleanValue();
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
    }

    public void onBlockHit(ProjectileHitEvent event) {
        Block b = event.getHitBlock();
        BlockFace hitFace = event.getHitBlockFace();
        if (hitFace == BlockFace.UP || hitFace == BlockFace.DOWN) {
            Vector offset = (hitFace == BlockFace.UP) ? new Vector(0, 1, 0) : new Vector(0, -1, 0);
            ItemStack ladderArrow = CraftArrows.getArrowVariantBySimpleName("Ladder").getRecipe().getItem();
            ladderArrow.setAmount(1);
            b.getWorld().dropItemNaturally(b.getLocation().add(offset), ladderArrow);
            return;
        }
        iterate(0, b, hitFace);
    }

    private void iterate(int i, Block b, BlockFace hitFace) {
        if (this.MaxLength < i)
            return;
        if (i > 0) {
            Block thisB = getNextBlock(b, hitFace);
            if (thisB == null)
                return;
            if (!this.replaceableBlocks.contains(thisB.getType()))
                return;
            thisB.setType(Material.LADDER);
            Ladder ladder = (Ladder) thisB.getBlockData();
            ladder.setFacing(hitFace);
            thisB.setBlockData(ladder);
            if (this.ParticleOnPlacement != null)
                b.getWorld().spawnParticle(this.ParticleOnPlacement, thisB.getLocation().add(0.5D, 0.5D, 0.5D), 10, 0.25D, 0.25D, 0.25D, 0.0D);
            b = thisB.getRelative(0, -1, 0);
        }
        Block block = b;
        if (this.PlacementDelay > 0) {
            Bukkit.getScheduler().runTaskLater(CraftArrows.instance, () -> iterate(i + 1, block, hitFace), this.PlacementDelay);
        } else {
            iterate(i + 1, b, hitFace);
        }
    }

    private Block getNextBlock(Block start, BlockFace face) {
        BlockFace opposite = face.getOppositeFace();
        if (this.replaceableBlocks.contains(start.getType()) && !this.replaceableBlocks.contains(start.getRelative(opposite).getType()))
            return start;
        for (int i = 1; i < this.MaxOffset + 1; i++) {
            Vector positiveDirection = face.getDirection().multiply(i);
            Vector negativeDirection = face.getDirection().multiply(-i);
            if (!this.OnlyForwards) {
                Block neg = start.getRelative(negativeDirection.getBlockX(), negativeDirection.getBlockY(), negativeDirection.getBlockZ());
                if (this.replaceableBlocks.contains(neg.getType()) && !this.replaceableBlocks.contains(neg.getRelative(opposite).getType()))
                    return neg;
            }
            Block pos = start.getRelative(positiveDirection.getBlockX(), positiveDirection.getBlockY(), positiveDirection.getBlockZ());
            if (this.replaceableBlocks.contains(pos.getType()) && !this.replaceableBlocks.contains(pos.getRelative(opposite).getType()))
                return pos;
        }
        if (this.PlaceInAir)
            return start;
        return null;
    }
}
