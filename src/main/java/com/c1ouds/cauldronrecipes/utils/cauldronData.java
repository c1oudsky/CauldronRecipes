package com.c1ouds.cauldronrecipes.utils;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class cauldronData {
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
            if (gameItem != null) this.itemMeta = new ItemMetaKey(gameItem, Integer.parseInt(itemname[2]));
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
