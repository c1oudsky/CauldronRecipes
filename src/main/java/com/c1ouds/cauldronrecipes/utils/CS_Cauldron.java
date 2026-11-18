package com.c1ouds.cauldronrecipes.utils;

import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public interface CS_Cauldron {
    public boolean hasFluid(Fluid fluid, World worldIn, int x, int y, int z);
}
