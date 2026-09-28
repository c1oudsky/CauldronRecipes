package com.c1ouds.cauldronrecipes.mixins;

import com.c1ouds.cauldronrecipes.utils.CauldronWorldData;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Will need to figure out client-server logic to make any liquid work
@Mixin(RenderBlocks.class)
public class MixinRenderBlocks {

    @Redirect(
        method = "renderBlockCauldron", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockLiquid;getLiquidIcon(Ljava/lang/String;)Lnet/minecraft/util/IIcon;"
        )
    )
    private IIcon redirectCauldronTexture(String name, BlockCauldron cauldron, int x, int y, int z) {
        IIcon icon = BlockLiquid.getLiquidIcon("water_still");
        String posKey = x+","+y+","+ z;
        if (CauldronWorldData.currentData.activeCauldrons.containsKey(posKey)) {
            var fluid = CauldronWorldData.currentData.activeCauldrons.get(posKey);
            return fluid.getIcon();
        }
        return icon;
    }
}
