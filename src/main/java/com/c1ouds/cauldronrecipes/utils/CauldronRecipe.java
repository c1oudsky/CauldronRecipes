package com.c1ouds.cauldronrecipes.utils;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.c1ouds.cauldronrecipes.CauldronRecipes.LOG;

public class CauldronRecipe {
    public static Map<RecipeEntry, CauldronRecipe> RecipeRegistry = new HashMap<>();

    final private ItemStack input;
    final public int waterUsed;
    final private ItemStack resultOutput;
    final private ItemStack bonusOutput;
    final public Fluid liquid;
    final public double bonus_probability;
    final public boolean clustered;

    public static boolean AddRecipe(CauldronRecipe recipe) {
        RecipeEntry entry = new RecipeEntry(new ItemMetaKey(recipe.input).intern(), recipe.liquid).intern();
        boolean added = !RecipeRegistry.containsKey(entry);
        if (added) RecipeRegistry.put(entry, recipe);
        return added;
    }

    public CauldronRecipe(ItemStack input, ItemStack output, int waterUsed, Fluid fluid) {
        this.input = input; this.resultOutput = output;
        this.waterUsed = waterUsed; bonusOutput = null;
        bonus_probability = 0;
        clustered = true;
        liquid = fluid;
    }
    public CauldronRecipe(ItemStack input, ItemStack output, ItemStack bonus, double probability, Fluid fluid) {
        this(input, output, bonus, probability, false, fluid);
    }
    public CauldronRecipe(ItemStack input, ItemStack output, ItemStack bonus, double probability, boolean clustered, Fluid fluid) {
        this.input = input; this.resultOutput = output;
        this.waterUsed = output==null? 0 : 1; bonusOutput = bonus;
        bonus_probability = probability;
        this.clustered = clustered;
        liquid = fluid;
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

    @SuppressWarnings("UnstableApiUsage")
    static public class RecipeEntry {
        // Automatic reduction of identical objects in favor of earliest one
        private static final Interner<RecipeEntry> POOL = Interners.newWeakInterner();
        public RecipeEntry intern() { return POOL.intern(this); }

        final private ItemMetaKey item; final private Fluid fluid;
        public RecipeEntry(ItemMetaKey item, Fluid fluid) { this.item = item; this.fluid = fluid; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RecipeEntry that = (RecipeEntry) o;
            return this.item.equals(that.item) && this.fluid.equals(that.fluid);
        }
        @Override public int hashCode() {
            return Objects.hash(item, fluid);
        }
        public RecipeEntry withMeta(int meta) {
            return new RecipeEntry(item.withMeta(meta), fluid).intern();
        }
    }
}
