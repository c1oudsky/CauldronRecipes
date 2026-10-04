package com.c1ouds.cauldronrecipes;

import java.io.File;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;

public class Config {

    public static boolean addRecipes;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        addRecipes = configuration.getBoolean("addDefaultRecipes", Configuration.CATEGORY_GENERAL, true,
            "Adds default built-in recipes to cauldron. Disable if you'd like to only use your own recipes via zenscripts.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
