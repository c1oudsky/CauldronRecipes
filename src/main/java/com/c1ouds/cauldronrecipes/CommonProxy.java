package com.c1ouds.cauldronrecipes;

import com.c1ouds.cauldronrecipes.mods.CTcompat;
import com.c1ouds.cauldronrecipes.utils.CauldronRecipe;
import com.c1ouds.cauldronrecipes.utils.ItemMetaKey;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static com.c1ouds.cauldronrecipes.utils.CauldronRecipe.RecipeRegistry;
import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        CauldronRecipes.LOG.info("I am BetterIron at version " + Tags.VERSION);
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {
        if(Loader.isModLoaded("MineTweaker3")) CTcompat.postInit();

        RecipeRegistry.put(new ItemMetaKey(new ItemStack(Blocks.gravel)).intern(), new CauldronRecipe(
            new ItemStack(Blocks.gravel), new ItemStack(Items.flint), new ItemStack(Items.clay_ball, 2), 0.3f ));

        var item = new ItemStack(Blocks.wool, 1, WILDCARD_VALUE);
        RecipeRegistry.put(new ItemMetaKey(Item.getItemFromBlock(Blocks.wool), WILDCARD_VALUE).intern(),
            new CauldronRecipe(item, new ItemStack(Blocks.wool), 2) );
        item = new ItemStack(Blocks.stained_glass, 1, WILDCARD_VALUE);
        RecipeRegistry.put( new ItemMetaKey(item).intern(), new CauldronRecipe(item, new ItemStack(Blocks.glass), 1) );
        item = new ItemStack(Blocks.stained_glass_pane, 1, WILDCARD_VALUE);
        RecipeRegistry.put( new ItemMetaKey(item).intern(), new CauldronRecipe(item, new ItemStack(Blocks.glass_pane), 1) );

        item = new ItemStack(Blocks.dirt);
        RecipeRegistry.put( new ItemMetaKey(item).intern(), new CauldronRecipe(item, item, new ItemStack(Items.wheat_seeds), 0.3) );
        item = new ItemStack(Items.coal, 1, WILDCARD_VALUE);
        RecipeRegistry.put( new ItemMetaKey(item).intern(), new CauldronRecipe(item, null, new ItemStack(Items.dye, 2), 0.5));
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
