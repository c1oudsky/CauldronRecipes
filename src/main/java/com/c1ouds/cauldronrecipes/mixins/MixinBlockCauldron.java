package com.c1ouds.cauldronrecipes.mixins;

import com.c1ouds.cauldronrecipes.Config;
import com.c1ouds.cauldronrecipes.utils.CauldronRecipe;
import com.c1ouds.cauldronrecipes.utils.ItemMetaKey;
import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.c1ouds.cauldronrecipes.utils.CauldronRecipe.RecipeRegistry;

@Mixin(BlockCauldron.class)
public abstract class MixinBlockCauldron {
    @Shadow abstract void func_150024_a(World worldIn, int x, int y, int z, int level);

    @Inject(method = "onBlockActivated", at = @At("HEAD"), cancellable = true)
    private void onCauldronActivated(World worldIn, int x, int y, int z, EntityPlayer player,
    int side, float subX, float subY, float subZ, CallbackInfoReturnable<Boolean> cir) {
        if (!worldIn.isRemote) {
            ItemStack itemstack = player.inventory.getCurrentItem();
            int meta = worldIn.getBlockMetadata(x, y, z);
            if (itemstack != null) {
                if (meta > 0) {
                    var itemkey = new ItemMetaKey(itemstack);
                    if(RecipeRegistry.containsKey(itemkey)) {
                        CauldronRecipe recipe = RecipeRegistry.get(itemkey);
                        if (!player.capabilities.isCreativeMode) {
                            itemstack.stackSize--; meta--;
                            this.func_150024_a(worldIn, x, y, z, meta);
                            if (itemstack.stackSize <= 0)
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);

                            if (player instanceof EntityPlayerMP playerMP) playerMP.sendContainerToPlayer(player.inventoryContainer);
                        }
                        if (recipe.get_itemstack(1) != null) CauldronRecipe.spawnItem(worldIn, x,y,z, recipe.get_itemstack(1));
                        if (meta == 0 && recipe.get_itemstack(2) != null) CauldronRecipe.spawnItem(worldIn, x,y,z, recipe.get_itemstack(2));

                        cir.setReturnValue(true);
                    }
                }
                if (FluidContainerRegistry.isFilledContainer(itemstack)) {
                    FluidStack container = FluidContainerRegistry.getFluidForFilledItem(itemstack);
                    if (container.getFluid() == FluidRegistry.WATER && meta < 3) {
                        if (!player.capabilities.isCreativeMode)
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, FluidContainerRegistry.drainFluidContainer(itemstack));
                        this.func_150024_a(worldIn, x, y, z, 3);
                        //System.out.println("[CauldronRecipes] Mixed in cauldron check!");
                        cir.setReturnValue(true);
                    }
                    else if (Config.EFRloaded && container.getFluid() == FluidRegistry.LAVA && meta>3 && meta<6) {
                        if (!player.capabilities.isCreativeMode)
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, FluidContainerRegistry.drainFluidContainer(itemstack));
                        this.func_150024_a(worldIn, x, y, z, 6);
                        //System.out.println("[CauldronRecipes] Mixed in cauldron check with EFR!");
                        cir.setReturnValue(true);
                    }
                }
            }
            else if (player.isSneaking() && meta > 0) {
                this.func_150024_a(worldIn, x,y,z, 0);
                //worldIn.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, meta > 3 ? "random.fizz" : "etfuturum:item.bucket.empty", 0.5F, 1F);
            }
        }
    }
}
