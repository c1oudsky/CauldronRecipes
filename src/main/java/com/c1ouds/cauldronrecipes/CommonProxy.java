package com.c1ouds.cauldronrecipes;

import com.c1ouds.cauldronrecipes.eventhandlers.sendOnJoinDimension;
import com.c1ouds.cauldronrecipes.mods.CTcompat;
import com.c1ouds.cauldronrecipes.utils.CauldronRecipe;
import com.c1ouds.cauldronrecipes.utils.ServerToClientPacket;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static com.c1ouds.cauldronrecipes.utils.CauldronRecipe.AddRecipe;
import static net.minecraftforge.fluids.FluidRegistry.LAVA;
import static net.minecraftforge.fluids.FluidRegistry.WATER;
import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class CommonProxy {
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("CauldronRecipes");

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        CauldronRecipes.LOG.info("I am BetterIron at version " + Tags.VERSION);

        NETWORK.registerMessage(ServerToClientPacket.Handler.class, ServerToClientPacket.class, 0, Side.CLIENT);
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {
        if(Loader.isModLoaded("MineTweaker3")) CTcompat.postInit();
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new sendOnJoinDimension());

        if(Config.addRecipes) initRecipes();
    }

    private static void initRecipes() {
        AddRecipe(new CauldronRecipe(new ItemStack(Blocks.gravel), new ItemStack(Items.flint),
            new ItemStack(Items.clay_ball, 2), 0.3f, WATER));

        var item = new ItemStack(Blocks.wool, 1, WILDCARD_VALUE);
        AddRecipe(new CauldronRecipe(item, new ItemStack(Blocks.wool), 2, WATER));
        AddRecipe(new CauldronRecipe(item, new ItemStack(Items.string, 2), 1, LAVA));
        item = new ItemStack(Blocks.stained_glass, 1, WILDCARD_VALUE);
        AddRecipe(new CauldronRecipe(item, new ItemStack(Blocks.glass), 1, WATER));
        item = new ItemStack(Blocks.stained_glass_pane, 1, WILDCARD_VALUE);
        AddRecipe(new CauldronRecipe(item, new ItemStack(Blocks.glass_pane), 1, WATER));
        item = new ItemStack(Blocks.stained_hardened_clay, 1, WILDCARD_VALUE);
        AddRecipe(new CauldronRecipe(item, new ItemStack(Blocks.hardened_clay), 1, LAVA));

        item = new ItemStack(Blocks.dirt);
        AddRecipe(new CauldronRecipe(item, item, new ItemStack(Items.wheat_seeds), 0.3, WATER));
        item = new ItemStack(Items.coal, 3, WILDCARD_VALUE);
        AddRecipe(new CauldronRecipe(item, null, new ItemStack(Items.dye, 3), 0.5, WATER));
        item = new ItemStack(Items.magma_cream, 2);
        AddRecipe(new CauldronRecipe(item, WATER, LAVA));
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
