package com.c1ouds.cauldronrecipes.eventhandlers;

import com.c1ouds.cauldronrecipes.utils.CauldronWorldData;
import com.c1ouds.cauldronrecipes.utils.ServerToClientPacket;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;

import static com.c1ouds.cauldronrecipes.CommonProxy.NETWORK;

public class CpwEventHandler {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            CauldronWorldData worldData = CauldronWorldData.get(player.worldObj);
            NETWORK.sendTo(new ServerToClientPacket(ServerToClientPacket.CauldronDataAction, worldData), player);
        }
    }
    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            CauldronWorldData worldData = CauldronWorldData.get(player.worldObj);
            NETWORK.sendTo(new ServerToClientPacket(ServerToClientPacket.CauldronDataAction, worldData), player);
        }
    }
}
