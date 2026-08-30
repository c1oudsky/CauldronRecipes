package com.c1ouds.cauldronrecipes;

import com.c1ouds.cauldronrecipes.utils.CauldronRecipe;
import com.c1ouds.cauldronrecipes.utils.ItemMetaKey;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        CauldronRecipes.LOG.info("I am BetterIron at version " + Tags.VERSION);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {}

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        /*for (Map.Entry<String, String> entry : Config.watercontainers_names.entrySet()) {
            String[] fullParts = entry.getKey().split(":");
            String[] emptyParts = entry.getValue().split(":");

            Item fullItem = GameRegistry.findItem(fullParts[0], fullParts[1]);
            Item emptyItem = GameRegistry.findItem(emptyParts[0], emptyParts[1]);

            if (fullItem != null && emptyItem != null) {
                CAULDRON_WATER_BUCKETS.put(fullItem, emptyItem);
            }
        }*/
        CauldronRecipe.RecipeRegistry.put(new ItemMetaKey(new ItemStack(Blocks.gravel)), new CauldronRecipe(
            new ItemStack(Blocks.gravel), new ItemStack(Items.flint), new ItemStack(Items.clay_ball) ));
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
