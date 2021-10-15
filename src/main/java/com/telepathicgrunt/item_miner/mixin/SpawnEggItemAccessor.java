package com.telepathicgrunt.item_miner.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpawnEggItem.class)
public interface SpawnEggItemAccessor {
    @Accessor("defaultType")
    EntityType<?> getDefaultType();
}
