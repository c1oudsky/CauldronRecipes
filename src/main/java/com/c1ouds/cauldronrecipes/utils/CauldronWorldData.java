package com.c1ouds.cauldronrecipes.utils;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.HashMap;
import java.util.Map;

public class CauldronWorldData extends WorldSavedData {
    private static final String DATA_NAME = "CauldronRecipes_cauldronsdata";

    // "X,Y,Z" -> "ItemName"
    public final Map<String, cauldronData> boundCauldrons = new HashMap<>();
    public final Map<String, Fluid> activeCauldrons = new HashMap<>();

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
        NBTTagList list = nbt.getTagList("CauldronsLiquids", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            var fluid = FluidRegistry.getFluid(compound.getString("Fluid"));
            if(fluid != null) activeCauldrons.put(compound.getString("Pos"), fluid);
        }

        NBTTagList listBindings = nbt.getTagList("CauldronsBindings", 10);
        for (int i = 0; i < listBindings.tagCount(); i++) {
            NBTTagCompound compound = listBindings.getCompoundTagAt(i);
            var data = new cauldronData(compound.getString("ItemKey"), compound.getString("Fluid"));
            if (data.itemMeta != null && data.fluid != null)
                boundCauldrons.put(compound.getString("Pos"), data);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<String, Fluid> entry : activeCauldrons.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("Pos", entry.getKey());
            compound.setString("Fluid", entry.getValue().getName());
            list.appendTag(compound);
        }
        nbt.setTag("CauldronsLiquids", list);

        NBTTagList listBindings = new NBTTagList();
        for (Map.Entry<String, cauldronData> entry : boundCauldrons.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("Pos", entry.getKey());
            compound.setString("ItemKey", entry.getValue().getItemString());
            compound.setString("Fluid", entry.getValue().getFluidString());
            listBindings.appendTag(compound);
        }
        nbt.setTag("CauldronsBindings", listBindings);
    }

    public static class cauldronData {
        public ItemMetaKey itemMeta;
        public Fluid fluid;
        public cauldronData(ItemMetaKey itemMeta, Fluid fluid) {
            this.itemMeta = itemMeta;
            this.fluid = fluid;
        }
        public cauldronData(String item, String fluid) {
            String[] itemname = item.split(":");
            if (itemname.length == 3) {
                var gameItem = GameRegistry.findItem(itemname[0], itemname[1]);
                if (gameItem != null) this.itemMeta = new ItemMetaKey(gameItem, Integer.parseInt(itemname[2])).intern();
                else this.itemMeta = null;
            } else this.itemMeta = null;
            this.fluid = FluidRegistry.getFluid(fluid);
        }

        public String getItemString() {
            var uuid = GameRegistry.findUniqueIdentifierFor(itemMeta.item);
            return uuid.modId + ":" + uuid.name + ":" + itemMeta.meta;
        }
        public String getFluidString() {
            return fluid.getName();
        }
    }
}
