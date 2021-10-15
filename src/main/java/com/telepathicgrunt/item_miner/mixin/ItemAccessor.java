package com.telepathicgrunt.item_miner.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Item.class)
public interface ItemAccessor {
    @Invoker("getPlayerPOVHitResult")
    static BlockRayTraceResult callGetPlayerPOVHitResult(World world, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        throw new UnsupportedOperationException();
    }
}
