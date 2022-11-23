package io.github.dailystruggle.craftarrows.Npc;

import io.github.dailystruggle.craftarrows.Listener.CraftArrowListener;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

@TraitName("nocraftarrows")
public class NoCraftArrowTrait extends Trait {
    public NoCraftArrowTrait() {
        super("nocraftarrows");
    }

    public void onSpawn() {
        CraftArrowListener.ignoredEntities.add(getNPC().getEntity());
    }

    public void onRemove() {
        CraftArrowListener.ignoredEntities.remove(getNPC().getEntity());
    }
}
