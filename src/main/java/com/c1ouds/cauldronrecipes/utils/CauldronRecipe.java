package com.c1ouds.cauldronrecipes.utils;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.HashMap;
import java.util.Map;

public class CauldronRecipe {
    public static Map<ItemMetaKey, CauldronRecipe> RecipeRegistry = new HashMap<>();

    final private ItemStack input;
    final public int waterUsed;
    final private ItemStack resultOutput;
    final private ItemStack bonusOutput;
    final public Fluid liquid = FluidRegistry.WATER;
    final public double bonus_probability;
    final public boolean clustered;

    public CauldronRecipe(ItemStack input, ItemStack output, int waterUsed) {
        this.input = input; this.resultOutput = output;
        this.waterUsed = waterUsed; bonusOutput = null;
        bonus_probability = 0;
        clustered = true;
    }
    public CauldronRecipe(ItemStack input, ItemStack output, ItemStack bonus, double probability) {
        this(input, output, bonus, probability, false);
    }
    public CauldronRecipe(ItemStack input, ItemStack output, ItemStack bonus, double probability, boolean clustered) {
        this.input = input; this.resultOutput = output;
        waterUsed = 1; bonusOutput = bonus;
        bonus_probability = probability;
        this.clustered = clustered;
    }
    public ItemStack get_itemstack(int arg) {
        return arg==0 ? safecopy(input) : (arg==1 ? safecopy(resultOutput) : safecopy(bonusOutput));
    }
    ItemStack safecopy(ItemStack stack) {
        return stack==null ? null : stack.copy();
    }

    static public void spawnItem(World world, int x, int y, int z, ItemStack stack) {
        EntityItem entityItem = new EntityItem(world, x+0.5, y+1, z+0.5, stack);
        /* Unneeded
        entityItem.motionY = 0.2;
        entityItem.motionX = (world.rand.nextDouble() - 0.5) * 0.04;
        entityItem.motionZ = (world.rand.nextDouble() - 0.5) * 0.04;
        */
        //entityItem.delayBeforeCanPickup = 5; //seems better without delay
        world.spawnEntityInWorld(entityItem);
    }
}
