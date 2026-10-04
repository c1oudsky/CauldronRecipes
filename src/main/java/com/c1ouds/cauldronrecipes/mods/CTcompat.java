package com.c1ouds.cauldronrecipes.mods;

import com.c1ouds.cauldronrecipes.utils.CauldronRecipe;
import com.c1ouds.cauldronrecipes.utils.ItemMetaKey;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.mc1710.item.MCItemStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import java.util.ArrayList;
import java.util.List;
import static com.c1ouds.cauldronrecipes.utils.CauldronRecipe.RecipeRegistry;

@SuppressWarnings("unused")
@ZenClass("mods.cauldronrecipes")
public class CTcompat {

    public static void postInit() {
        MineTweakerAPI.registerClass(CTcompat.class);
    }

    private static ItemStack toItemStack(IItemStack iitem) {
        if (iitem != null) {
            if (iitem.getInternal() instanceof ItemStack stack)
                return stack;
        }
        return null;
    }
    private static void addRecipeHelper(IIngredient input, IItemStack output, int waterUsed, IItemStack bonus, Fluid liquid, double chance, boolean clustered) {
        if (input == null) {
            MineTweakerAPI.logError("Can't use null as recipe input!");
            return;
        }
        if (output == null && bonus == null) {
            MineTweakerAPI.logError("Recipe for "+input.getInternal()+" doesn't output anything!");
            return;
        }
        if (waterUsed < 0 || waterUsed > 3) {
            MineTweakerAPI.logError("Used water can't be "+(waterUsed < 0 ? "less than 0!" : "more than 3!")+" (recipe for "+input.getInternal()+")");
            return;
        }
        if (chance < 0 || chance > 1) {
            MineTweakerAPI.logError("Probability can't be "+(chance < 0 ? "below 0!" : "above 1!")+" (recipe for "+input.getInternal()+")");
            return;
        }
        if (liquid == null) {
            MineTweakerAPI.logError("liquid in recipe can't be null! Like, seriously.");
            return;
        }
        Object inputInternal = input.getInternal();
        List<IItemStack> list = new ArrayList<>();
        if (inputInternal instanceof ItemStack itemstack)
            list.add(new MCItemStack(itemstack));
        else
            list = input.getItems();
        for(IItemStack iitem : list) {
            ItemStack itemstack = toItemStack(iitem);
            if (itemstack != null) {
                itemstack.stackSize = input.getAmount();
                var inputkey = new ItemMetaKey(itemstack).intern();
                var inputentry = new CauldronRecipe.RecipeEntry(inputkey, liquid).intern();
                if(!RecipeRegistry.containsKey(inputentry))
                    MineTweakerAPI.apply( new AddAction(inputentry, itemstack, toItemStack(output), waterUsed, toItemStack(bonus), liquid, chance, clustered) );
                else MineTweakerAPI.logError("Cauldron already has a recipe with input " + itemstack.getDisplayName() + " in " + liquid.getName());
            }
        }

    }

    @ZenMethod
    public static void addRecipe(IIngredient input, IItemStack output, int waterUsed, @Optional ILiquidStack liquid) {
        Fluid fluid;
        if (liquid != null) {
            FluidStack stack = (FluidStack) liquid.getInternal();
            fluid = stack.getFluid();
        } else fluid = FluidRegistry.WATER;
        addRecipeHelper(input, output, waterUsed, null, fluid, 1d, true);
    }
    @ZenMethod
    public static void addRecipe(IIngredient input, IItemStack output, IItemStack bonus, @Optional double chance, @Optional boolean clustered, @Optional ILiquidStack liquid) {
        Fluid fluid;
        if (chance == 0) chance = 1;
        if (liquid != null) {
            FluidStack stack = (FluidStack) liquid.getInternal();
            fluid = stack.getFluid();
        } else fluid = FluidRegistry.WATER;
        // waterUsed is irrelevant here, is set automatically for this kind of recipes in CauldronRecipe
        addRecipeHelper(input, output, 0, bonus, fluid, chance, clustered);
    }

    @ZenMethod
    public static void removeRecipe(IIngredient input, @Optional ILiquidStack liquid) {
        Fluid fluid;
        if (liquid != null) {
            FluidStack stack = (FluidStack) liquid.getInternal();
            fluid = stack.getFluid();
        } else fluid = FluidRegistry.WATER;
        Object inputInternal = input.getInternal();
        List<IItemStack> list = new ArrayList<>();
        if (inputInternal instanceof ItemStack itemstack)
            list.add(new MCItemStack(itemstack));
        else
            list = input.getItems();
        for (IItemStack iitem : list) {
            var itemstack = toItemStack(iitem);
            if (itemstack != null) {
                var inputkey = new ItemMetaKey(itemstack).intern();
                var inputentry = new CauldronRecipe.RecipeEntry(inputkey, fluid).intern();
                if(RecipeRegistry.containsKey(inputentry))
                    MineTweakerAPI.apply(new RemoveAction(inputentry, itemstack));
                else MineTweakerAPI.logError("No cauldron recipe with input " + itemstack.getDisplayName() + " in " + fluid.getName());
            }
        }

    }

    private static class AddAction implements IUndoableAction {

        final private ItemStack input;
        final private ItemStack output;
        final private ItemStack bonusoutput;
        final private int waterUsed;
        final private CauldronRecipe.RecipeEntry inputentry;
        final private double chance;
        final private boolean clustered;
        final private Fluid liquid;

        public AddAction(CauldronRecipe.RecipeEntry inputentry, ItemStack input, ItemStack output, int waterUsed, ItemStack bonusoutput, Fluid liquid, double chance, boolean clustered) {
            this.input = input;
            this.output = output;
            this.bonusoutput = bonusoutput;
            this.waterUsed = waterUsed;
            this.inputentry = inputentry;
            this.chance = chance;
            this.clustered = clustered;
            this.liquid = liquid;
        }

        @Override
        public void apply() {
            CauldronRecipe recipe;
            if(bonusoutput == null) recipe = new CauldronRecipe(input, output, waterUsed, liquid);
            else recipe = new CauldronRecipe(input, output, bonusoutput, chance, clustered, liquid);
            RecipeRegistry.put(inputentry, recipe);
        }

        @Override
        public boolean canUndo() { return true; }

        @Override
        public void undo() {
            RecipeRegistry.remove(inputentry);
        }

        @Override
        public String describe() {
            return "Adding cauldron recipe with input " + input.getDisplayName() + " in " + liquid.getName();
        }

        @Override
        public String describeUndo() {
            return "Removing cauldron recipe with input " + input.getDisplayName() + " in " + liquid.getName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class RemoveAction implements IUndoableAction {

        final private String inputname;
        final private CauldronRecipe.RecipeEntry inputentry;
        final private CauldronRecipe recipe;

        public RemoveAction(CauldronRecipe.RecipeEntry inputentry, ItemStack input) {
            this.inputentry = inputentry;
            this.inputname = input.getDisplayName();
            this.recipe = RecipeRegistry.get(inputentry);
        }

        @Override
        public void apply() {
            RecipeRegistry.remove(inputentry);
        }

        @Override
        public boolean canUndo() { return true; }

        @Override
        public void undo() {
            RecipeRegistry.put(inputentry, recipe);
        }

        @Override
        public String describe() {
            return "Removing cauldron recipe with input " + inputname + " in " + recipe.liquid.getName();
        }

        @Override
        public String describeUndo() {
            return "Re-adding cauldron recipe with input " + inputname + " in " + recipe.liquid.getName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
