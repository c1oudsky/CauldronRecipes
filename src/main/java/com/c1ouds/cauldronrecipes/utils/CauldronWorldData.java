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
    //public static HashMap<World>
    static public CauldronWorldData currentData = new CauldronWorldData();

    // "X,Y,Z" -> "ItemName"
    public final Map<String, cauldronData> boundCauldrons = new HashMap<>();
    public final Map<String, Fluid> activeCauldrons = new HashMap<>();

    public CauldronWorldData(String name) { super(name); }
    public CauldronWorldData() { super(DATA_NAME); }

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
            var data = new cauldronData(compound.getString("ItemKey"), compound.getInteger("Amount"));
            if (data.itemMeta != null)
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
            compound.setInteger("Amount", entry.getValue().amount);
            listBindings.appendTag(compound);
        }
        nbt.setTag("CauldronsBindings", listBindings);
    }

    public static class cauldronData {
        public ItemMetaKey itemMeta;
        public int amount;
        public cauldronData(ItemMetaKey itemMeta, int amount) {
            this.itemMeta = itemMeta;
            this.amount = amount;
        }
        public cauldronData(String item, int amount) {
            String[] itemname = item.split(":");
            if (itemname.length == 3) {
                var gameItem = GameRegistry.findItem(itemname[0], itemname[1]);
                if (gameItem != null) this.itemMeta = new ItemMetaKey(gameItem, Integer.parseInt(itemname[2])).intern();
                else this.itemMeta = null;
            } else this.itemMeta = null;
            this.amount = amount;
        }

        public String getItemString() {
            var uuid = GameRegistry.findUniqueIdentifierFor(itemMeta.item);
            return uuid.modId + ":" + uuid.name + ":" + itemMeta.meta;
        }
    }
}
