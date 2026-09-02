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
            new ItemStack(Blocks.gravel), new ItemStack(Items.flint), new ItemStack(Items.clay_ball), 0.6f ));

        var block = new ItemStack(Blocks.wool, 1, WILDCARD_VALUE);
        RecipeRegistry.put( new ItemMetaKey(block).intern(), new CauldronRecipe(block, new ItemStack(Blocks.wool), 2) );

        block = new ItemStack(Blocks.stained_glass, 1, WILDCARD_VALUE);
        RecipeRegistry.put( new ItemMetaKey(block).intern(), new CauldronRecipe(block, new ItemStack(Blocks.glass), 1) );

        block = new ItemStack(Blocks.dirt);
        RecipeRegistry.put( new ItemMetaKey(block).intern(), new CauldronRecipe(block, block, new ItemStack(Items.wheat_seeds), 0.3f) );
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
