package com.c1ouds.cauldronrecipes.utils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import java.util.HashMap;
import java.util.Map;

public class CauldronWorldData extends WorldSavedData {
    private static final String DATA_NAME = "CauldronRecipes_CauldronData";

    // "X,Y,Z" -> "ItemName"
    public final Map<String, String> activeCauldrons = new HashMap<>();

    public CauldronWorldData(String name) {
        super(name);
    }

    public static CauldronWorldData get(World world) {
        CauldronWorldData instance = (CauldronWorldData) world.loadItemData(CauldronWorldData.class, DATA_NAME);
        if (instance == null) {
            instance = new CauldronWorldData(DATA_NAME);
            world.setItemData(DATA_NAME, instance);
        }
        return instance;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("Cauldrons", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            activeCauldrons.put(compound.getString("Pos"), compound.getString("ItemKey"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<String, String> entry : activeCauldrons.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("Pos", entry.getKey());
            compound.setString("ItemKey", entry.getValue());
            list.appendTag(compound);
        }
        nbt.setTag("Cauldrons", list);
    }
}
