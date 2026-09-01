package com.c1ouds.cauldronrecipes;

import java.io.File;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;

public class Config {

    public static boolean EFRloaded;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);
        EFRloaded = Loader.isModLoaded("etfuturum");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
