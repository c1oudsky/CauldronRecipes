package com.c1ouds.cauldronrecipes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.Loader;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public class Config {

    public static Map<String, String> watercontainers_names = new HashMap<>();
    //public static Map<ItemStack, ItemStack> watercontainers_items = new HashMap<>();
    public static boolean EFRloaded;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);
        EFRloaded = Loader.isModLoaded("etfuturum");

        String[] defaults = new String[] {
        };

        String[] bucketPairs = configuration.get(Configuration.CATEGORY_GENERAL, "water_containers", defaults,
            "Water containers in modid:filled_container_name=modid:empty_container_name per line format").getStringList();
       for (String pair : bucketPairs) {
            if (pair != null && pair.contains("=")) {
                String[] parts = pair.split("=");
                if (parts.length == 2) {
                    String fullBucket = parts[0].trim();
                    String emptyBucket = parts[1].trim();
                    watercontainers_names.put(fullBucket, emptyBucket);
                }
            }
        }

        if (Loader.isModLoaded("simpleores")) watercontainers_names.put("simpleores:copper_bucket_water", "simpleores:copper_bucket");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
