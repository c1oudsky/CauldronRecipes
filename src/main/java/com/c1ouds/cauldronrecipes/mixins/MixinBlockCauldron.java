package com.c1ouds.cauldronrecipes.mixins;

import com.c1ouds.cauldronrecipes.utils.CauldronWorldData;
import com.c1ouds.cauldronrecipes.utils.CauldronWorldData.cauldronData;
import com.c1ouds.cauldronrecipes.utils.CauldronRecipe;
import com.c1ouds.cauldronrecipes.utils.ItemMetaKey;
import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.c1ouds.cauldronrecipes.utils.CauldronRecipe.RecipeRegistry;
import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

@Mixin(BlockCauldron.class)
public abstract class MixinBlockCauldron {
    @Shadow abstract void func_150024_a(World worldIn, int x, int y, int z, int level);

    @Inject(method = "onBlockActivated", at = @At("HEAD"), cancellable = true)
    private void onCauldronActivated(World worldIn, int x, int y, int z, EntityPlayer player,
    int side, float subX, float subY, float subZ, CallbackInfoReturnable<Boolean> cir) {
        if (!worldIn.isRemote) {
            ItemStack itemstack = player.inventory.getCurrentItem();
            int meta = worldIn.getBlockMetadata(x, y, z);
            //System.out.println("[CauldronRecipes] onCauldronActivated with meta "+meta);
            String posKey = x + "," + y + "," + z;
            var data = CauldronWorldData.get(worldIn);
            var currentBoundData = data.boundCauldrons.get(posKey);
            Fluid currentFluid = data.activeCauldrons.get(posKey);
            if (currentFluid == null) currentFluid = FluidRegistry.WATER;
            if (itemstack != null) {
                if (meta > 0) {
                    var heldItem = new ItemMetaKey(itemstack).intern();
                    if(!RecipeRegistry.containsKey(heldItem)) heldItem = heldItem.withMeta(WILDCARD_VALUE);
                    if(RecipeRegistry.containsKey(heldItem)) {
                        //System.out.println("[CauldronRecipes] Found recipe for "+heldItem.item.getUnlocalizedName()+":"+heldItem.meta);
                        ItemMetaKey boundItem = null; Fluid boundFluid = null;
                        if(currentBoundData != null) {
                            boundItem = currentBoundData.itemMeta;
                            boundFluid = currentBoundData.fluid;
                            if(!boundItem.equals(heldItem)) {
                                // To not lose bonus from recipe
                                cir.setReturnValue(true);
                                return;
                            }
                        }
                        CauldronRecipe recipe = RecipeRegistry.get(heldItem);
                        if( meta >= recipe.waterUsed && (itemstack.stackSize >= recipe.get_itemstack(0).stackSize || player.capabilities.isCreativeMode) ) {
                            if (!player.capabilities.isCreativeMode) {
                                itemstack.stackSize -= recipe.get_itemstack(0).stackSize;
                                if (itemstack.stackSize <= 0)
                                    player.setCurrentItemOrArmor(0, null);
                                player.inventoryContainer.detectAndSendChanges();
                            }
                            meta -= recipe.waterUsed;
                            this.func_150024_a(worldIn, x, y, z, meta);
                            // result output
                            if (recipe.get_itemstack(1) != null)
                                CauldronRecipe.spawnItem(worldIn, x, y, z, recipe.get_itemstack(1));
                            // bonus output
                            if (meta == 0 && recipe.get_itemstack(2) != null) {
                                if(currentBoundData != null) {
                                    var bonus_output = recipe.get_itemstack(2);
                                    if (recipe.clustered || bonus_output.stackSize == 1 || recipe.bonus_probability == 1) {
                                        if(worldIn.rand.nextFloat() <= recipe.bonus_probability)
                                            CauldronRecipe.spawnItem(worldIn, x, y, z, bonus_output);
                                    }
                                    else {
                                        int count = 0;
                                        for(int i=0; i<bonus_output.stackSize; i++)
                                            if(worldIn.rand.nextFloat() <= recipe.bonus_probability) count++;
                                        if (count > 0) {
                                            bonus_output.stackSize = count;
                                            CauldronRecipe.spawnItem(worldIn, x, y, z, bonus_output);
                                        }
                                    }
                                }
                            // bound data clean
                                data.boundCauldrons.remove(posKey); data.activeCauldrons.remove(posKey);
                                data.markDirty();
                            }
                            // bind recipe with bonus to this cauldron if start with full water
                            if (meta == 2 /*&& currentBoundData == null //(redundant)*/&& recipe.get_itemstack(2) != null) {
                                currentBoundData = new cauldronData(heldItem, currentFluid);
                                data.boundCauldrons.put(posKey, currentBoundData);
                                data.markDirty();
                            }
                            cir.setReturnValue(true);
                        }
                    }
                }
                if (meta < 3 && FluidContainerRegistry.isFilledContainer(itemstack)) {
                    if(currentBoundData == null)
                        if (FluidContainerRegistry.getFluidForFilledItem(itemstack).getFluid() == FluidRegistry.WATER) {
                            if (!player.capabilities.isCreativeMode)
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, FluidContainerRegistry.drainFluidContainer(itemstack));
                            this.func_150024_a(worldIn, x, y, z, 3);
                            data.activeCauldrons.put(posKey, FluidRegistry.WATER);
                        }
                    cir.setReturnValue(true);
                }
            }
            else if (player.isSneaking() && meta > 0) {
                this.func_150024_a(worldIn, x,y,z, 0);
                data.boundCauldrons.remove(posKey); data.activeCauldrons.remove(posKey);
                data.markDirty();
                //worldIn.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, meta > 3 ? "random.fizz" : "etfuturum:itemmeta.bucket.empty", 0.5F, 1F);
                cir.setReturnValue(true);
            }
        }
    }
}
