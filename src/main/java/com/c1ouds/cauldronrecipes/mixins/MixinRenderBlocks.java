package com.c1ouds.cauldronrecipes.mixins;
// Will need to figure out client-server logic to make any liquid work
//@Mixin(RenderBlocks.class)
public class MixinRenderBlocks {
    /*@Shadow private IBlockAccess blockAccess;
    private static CauldronWorldData cachedWorldData = null;
    private int lastProvidedWorld = -999;

    @Redirect(
        method = "renderBlockCauldron", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockLiquid;getLiquidIcon(Ljava/lang/String;)Lnet/minecraft/util/IIcon;"
        )
    )
    private IIcon redirectCauldronTexture(String name, BlockCauldron cauldron, int x, int y, int z) {
        IIcon icon = BlockLiquid.getLiquidIcon("water_still");
        String posKey = x+","+y+","+ z;
        if (this.blockAccess instanceof World thisworld) {
            if (cachedWorldData == null || lastProvidedWorld != thisworld.provider.dimensionId) cachedWorldData = CauldronWorldData.get(thisworld);
            var cauldrondata = cachedWorldData.boundCauldrons.get(posKey);
            if (cauldrondata != null) {
                IIcon fluidIcon = cauldrondata.fluid.getStillIcon();
                if (fluidIcon != null)  icon = fluidIcon;
            }
        }
        return icon;
    }*/
}
