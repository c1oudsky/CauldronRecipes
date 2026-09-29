package com.c1ouds.cauldronrecipes.mixins;

import com.c1ouds.cauldronrecipes.CommonProxy;
import com.c1ouds.cauldronrecipes.utils.CauldronWorldData;
import com.c1ouds.cauldronrecipes.utils.CauldronWorldData.cauldronData;
import com.c1ouds.cauldronrecipes.utils.CauldronRecipe;
import com.c1ouds.cauldronrecipes.utils.ItemMetaKey;
import com.c1ouds.cauldronrecipes.utils.ServerToClientPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.material.Material;
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
public abstract class MixinBlockCauldron extends Block {
    @Shadow abstract void func_150024_a(World worldIn, int x, int y, int z, int level);
    @Inject(method = "onBlockActivated", at = @At("HEAD"), cancellable = true)
    private void onCauldronActivated(World worldIn, int x, int y, int z, EntityPlayer player,
    int side, float subX, float subY, float subZ, CallbackInfoReturnable<Boolean> cir) {
        if (!worldIn.isRemote) {
            ItemStack itemstack = player.inventory.getCurrentItem();
            int meta = worldIn.getBlockMetadata(x, y, z);
            //System.out.println("[CauldronRecipes] onCauldronActivated with meta "+meta);
            String posKey = x + "," + y + "," + z;
            CauldronWorldData data = CauldronWorldData.get(worldIn);
            var currentBoundData = data.boundCauldrons.get(posKey);
            Fluid currentFluid = data.activeCauldrons.get(posKey);
            if (itemstack != null) {
                if (meta > 0) {
                    if (currentFluid == null) {
                        currentFluid = FluidRegistry.WATER;
                        data.activeCauldrons.put(posKey, currentFluid);
                    }
                    var heldItem = new ItemMetaKey(itemstack).intern();
                    if(!RecipeRegistry.containsKey(heldItem)) heldItem = heldItem.withMeta(WILDCARD_VALUE);
                    if(RecipeRegistry.containsKey(heldItem)) {
                        //System.out.println("[CauldronRecipes] Found recipe for "+heldItem.item.getUnlocalizedName()+":"+heldItem.meta);
                        CauldronRecipe recipe = RecipeRegistry.get(heldItem);
                        var recipe_input = recipe.get_itemstack(0);
                        var recipe_output = recipe.get_itemstack(1);
                        var compoundRecipe = recipe_output == null;
                        // Can't do compound recipe with partially full cauldron:
                        if (compoundRecipe && meta < 3) return;
                        // Can't do recipe with unmatching fluid:
                        if(!currentFluid.equals(recipe.liquid)) return;
                        if(currentBoundData != null) {
                            ItemMetaKey boundItem = currentBoundData.itemMeta;
                            // To not lose bonus from recipe
                            if( !(boundItem.equals(heldItem)) ) {
                                cir.setReturnValue(true);
                                return;
                            }
                        }
                        // Water and item deduction with outputs in the following branch
                        if( meta >= recipe.waterUsed && (
                            (!compoundRecipe && itemstack.stackSize >= recipe_input.stackSize)
                            || (compoundRecipe && itemstack.stackSize >= 1)
                            || player.capabilities.isCreativeMode) )
                        {
                            if (!player.capabilities.isCreativeMode) {
                                itemstack.stackSize -= compoundRecipe? 1 : recipe_input.stackSize;
                                if (itemstack.stackSize <= 0)
                                    player.setCurrentItemOrArmor(0, null);
                                player.inventoryContainer.detectAndSendChanges();
                            }
                            if (recipe.waterUsed > 0) {
                                meta -= recipe.waterUsed;
                                this.func_150024_a(worldIn, x, y, z, meta);
                            }
                            // result output
                            if (!compoundRecipe)
                                CauldronRecipe.spawnItem(worldIn, x, y, z, recipe.get_itemstack(1));

                            boolean compoundFinished = compoundRecipe && (recipe_input.stackSize < 2 ||
                                currentBoundData != null && currentBoundData.amount >= recipe_input.stackSize);
                            boolean bonusFinished = meta == 0 && currentBoundData != null;
                            // check for 'currentBoundData != null' in bonusFinished ensures it was bound at the first place (thus bonus gained fairly)
                            if ((bonusFinished || compoundFinished) && recipe.get_itemstack(2) != null) {
                                //System.out.println("bonus output (" + currentBoundData.amount + " in cauldron, "+recipe_input.stackSize+" in recipe)");

                                // If this was compound recipe - emptying cauldron
                                if (meta > 0) {
                                    meta = 0;
                                    this.func_150024_a(worldIn, x, y, z, 0);
                                }
                                var bonus_output = recipe.get_itemstack(2);
                                if (recipe.clustered || bonus_output.stackSize == 1 || recipe.bonus_probability == 1) {
                                    if(recipe.bonus_probability == 1 || worldIn.rand.nextFloat() <= recipe.bonus_probability)
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
                            if (meta == 0) {
                                data.boundCauldrons.remove(posKey); data.activeCauldrons.remove(posKey);
                                data.markDirty();
                                CommonProxy.NETWORK.sendToDimension(new ServerToClientPacket(ServerToClientPacket.CauldronDataAction, data), worldIn.provider.dimensionId);
                            }
                            // not fill sound (emptying bucket one) if either cauldron was emptied or this is a regular recipe in any fluid but lava
                            // (lava sounds better with fill sound for these, water sounds better with non-fill sound for regular recipes)
                            boolean emptySound = meta == 0 || (!compoundRecipe && currentFluid != FluidRegistry.LAVA);
                            // sounds provided by Et Futurum Requiem (if installed)
                            worldIn.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, EFRsound(currentFluid, emptySound), 0.5F, 1F);
                            // bind recipe with bonus to this cauldron if start with full water
                            if ((meta == 2  && recipe.get_itemstack(2) != null) || (meta == 3  && compoundRecipe)) {
                                if (currentBoundData == null)
                                    currentBoundData = new cauldronData(heldItem, 1);
                                currentBoundData.amount++;
                                data.boundCauldrons.put(posKey, currentBoundData);
                                data.markDirty();
                                CommonProxy.NETWORK.sendToDimension(new ServerToClientPacket(ServerToClientPacket.CauldronDataAction, data), worldIn.provider.dimensionId);
                            }
                            cir.setReturnValue(true);
                            return;
                        }
                    }
                }
                if (FluidContainerRegistry.isFilledContainer(itemstack)) {
                    var heldFluid = FluidContainerRegistry.getFluidForFilledItem(itemstack).getFluid();
                    // Fill with Fluid from held container (buckets included)
                    if(meta < 3)
                        if (currentFluid == null || heldFluid == currentFluid) {
                            if (!player.capabilities.isCreativeMode)
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, FluidContainerRegistry.drainFluidContainer(itemstack));
                            this.func_150024_a(worldIn, x, y, z, 3);
                            data.activeCauldrons.put(posKey, heldFluid);
                            data.markDirty();
                            if (heldFluid == FluidRegistry.LAVA) worldIn.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "minecraft_1.21:item.bucket.empty_lava", 0.5F, 1F);
                            CommonProxy.NETWORK.sendToDimension(new ServerToClientPacket(ServerToClientPacket.CauldronDataAction, data), worldIn.provider.dimensionId);
                        }
                    cir.setReturnValue(true);
                    return;
                }
            }
            else if (player.isSneaking() && meta > 0) {
                this.func_150024_a(worldIn, x,y,z, 0);
                var liquid = data.activeCauldrons.get(posKey);
                data.boundCauldrons.remove(posKey); data.activeCauldrons.remove(posKey);
                data.markDirty();
                CommonProxy.NETWORK.sendToDimension(new ServerToClientPacket(ServerToClientPacket.CauldronDataAction, data), worldIn.provider.dimensionId);
                // sounds provided by Et Futurum Requiem (if installed)
                worldIn.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, EFRsound(liquid, true), 0.5F, 1F);
                cir.setReturnValue(true);
                return;
            }
            if (meta > 0 && currentFluid != FluidRegistry.WATER) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        var data = CauldronWorldData.get(world);
        String posKey = x + "," + y + "," + z;
        if (data.activeCauldrons.containsKey(posKey) || data.boundCauldrons.containsKey(posKey)) {
            data.activeCauldrons.remove(posKey);
            data.boundCauldrons.remove(posKey);
            data.markDirty();
        }
    }
    @Override
    public int getMobilityFlag() {
        return 2;
    }

    String EFRsound(Fluid liquid, boolean fill) {
        if (fill)
           return !liquid.equals(FluidRegistry.LAVA) ? "minecraft_1.21:item.bucket.fill" : "minecraft_1.21:item.bucket.fill_lava";
        else
            return !liquid.equals(FluidRegistry.LAVA) ? "minecraft_1.21:item.bucket.empty" : "minecraft_1.21:item.bucket.empty_lava";
    }

    protected MixinBlockCauldron(Material materialIn) {
        super(materialIn);
    }
}
