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
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

import static com.c1ouds.cauldronrecipes.utils.CauldronRecipe.RecipeRegistry;

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
    private static void addRecipeHelper(IIngredient input, IItemStack output, int waterUsed, IItemStack bonus, float chance) {
        Object inputInternal = input.getInternal();
        List<IItemStack> list = new ArrayList<>();
        if (inputInternal instanceof ItemStack itemstack)
            list.add(new MCItemStack(itemstack));
        else
            list = input.getItems();
        for(IItemStack iitem : list) {
            ItemStack itemstack = toItemStack(iitem);
            if (itemstack != null) {
                var inputkey = new ItemMetaKey(itemstack).intern();
                if(!RecipeRegistry.containsKey(inputkey))
                    MineTweakerAPI.apply( new AddAction(inputkey, itemstack, toItemStack(output), waterUsed, toItemStack(bonus), chance) );
                else MineTweakerAPI.logError("Cauldron already has a recipe with input " + itemstack.getDisplayName());
            }
        }

    }

    @ZenMethod
    public static void addRecipe(IIngredient input, IItemStack output, int waterUsed) {
        addRecipeHelper(input, output, waterUsed, null, 0);
    }
    @ZenMethod
    public static void addRecipe(IIngredient input, IItemStack output, IItemStack bonus, @Optional float chance) {
        addRecipeHelper(input, output, 1, bonus, chance==0f ? 1f : chance);
    }

    @ZenMethod
    public static void removeRecipe(IIngredient input) {
        Object inputInternal = input.getInternal();
        if (inputInternal instanceof ItemStack itemstack) {
            var inputkey = new ItemMetaKey(itemstack).intern();
            if(RecipeRegistry.containsKey(inputkey))
                MineTweakerAPI.apply(new RemoveAction(inputkey, itemstack));
            else MineTweakerAPI.logError("No cauldron recipe with input " + itemstack.getDisplayName());
        }
        else {
            var list = input.getItems();
            for (IItemStack iitem : list) {
                var itemstack = toItemStack(iitem);
                if (itemstack != null) {
                    var inputkey = new ItemMetaKey(itemstack).intern();
                    if(RecipeRegistry.containsKey(inputkey))
                        MineTweakerAPI.apply(new RemoveAction(inputkey, itemstack));
                    else MineTweakerAPI.logError("No cauldron recipe with input " + itemstack.getDisplayName());
                }
            }
        }
    }

    private static class AddAction implements IUndoableAction {

        final private ItemStack input;
        final private ItemStack output;
        final private ItemStack bonusoutput;
        final private int waterUsed;
        final private ItemMetaKey inputkey;
        final private float chance;

        public AddAction(ItemMetaKey inputkey, ItemStack input, ItemStack output, int waterUsed, ItemStack bonusoutput, float chance) {
            this.input = input;
            this.output = output;
            this.bonusoutput = bonusoutput;
            this.waterUsed = waterUsed;
            this.inputkey = inputkey;
            this.chance = chance;
        }

        @Override
        public void apply() {
            CauldronRecipe recipe;
            if(bonusoutput == null) recipe = new CauldronRecipe(input, output, waterUsed);
            else recipe = new CauldronRecipe(input, output, bonusoutput, chance);
            RecipeRegistry.put(inputkey, recipe);
        }

        @Override
        public boolean canUndo() { return true; }

        @Override
        public void undo() {
            RecipeRegistry.remove(inputkey);
        }

        @Override
        public String describe() {
            return "Adding cauldron recipe with input " + input.getDisplayName();
        }

        @Override
        public String describeUndo() {
            return "Removing cauldron recipe with input " + input.getDisplayName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class RemoveAction implements IUndoableAction {

        final private String inputname;
        final private ItemMetaKey inputkey;
        final private CauldronRecipe recipe;

        public RemoveAction(ItemMetaKey inputkey, ItemStack input) {
            this.inputkey = inputkey;
            this.inputname = input.getDisplayName();
            this.recipe = RecipeRegistry.get(inputkey);
        }

        @Override
        public void apply() {
            RecipeRegistry.remove(inputkey);
        }

        @Override
        public boolean canUndo() { return true; }

        @Override
        public void undo() {
            RecipeRegistry.put(inputkey, recipe);
        }

        @Override
        public String describe() {
            return "Removing cauldron recipe with input " + inputname;
        }

        @Override
        public String describeUndo() {
            return "Re-adding cauldron recipe with input " + inputname;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
