package io.github.dailystruggle.craftarrows.Arrows;

import io.github.dailystruggle.craftarrows.CraftArrows;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class TorchArrowVariant extends ArrowVariant {
    public TorchArrowVariant(FileConfiguration config) {
        super(config, "Recipes.", "Torch");
    }

    protected void loadDetails(FileConfiguration config) {
    }

    public void onShoot(EntityShootBowEvent event) {
    }

    public void onEntityHit(ProjectileHitEvent event) {
        if (event.getHitEntity() != null)
            lightTorch(event.getHitEntity().getLocation().add(0.0D, -1.0D, 0.0D).getBlock(), BlockFace.UP);
    }

    public void onBlockHit(ProjectileHitEvent event) {
        if (event.getHitBlock() != null) {
            BlockFace face = event.getHitBlockFace();
            if (face == BlockFace.DOWN) {
                ItemStack torchArrow = CraftArrows.getArrowVariantBySimpleName("Torch").getRecipe().getItem();
                torchArrow.setAmount(1);
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), torchArrow);
            } else {
                lightTorch(event.getHitBlock(), face);
            }
        }
    }

    private void lightTorch(Block block, BlockFace blockFace) {
        block = block.getRelative(blockFace);
        if (blockFace == BlockFace.UP) {
            block.setType(Material.TORCH);
        } else {
            block.setType(Material.WALL_TORCH);
            Directional torch = (Directional) block.getBlockData();
            torch.setFacing(blockFace);
            block.setBlockData(torch);
        }
    }
}
