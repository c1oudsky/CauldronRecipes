package com.c1ouds.cauldronrecipes.utils;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.Sys;

public class ServerToClientPacket implements IMessage {
    int actionID;
    private NBTTagCompound cauldrondata;
    public final static int // Action names for easier use
        CauldronDataAction = 0,
        Action1 = 1;

    public ServerToClientPacket() {}

    public ServerToClientPacket(int action, CauldronWorldData data) {
        this.actionID = action;
        cauldrondata = new NBTTagCompound();
        data.writeToNBT(cauldrondata);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(actionID);
        switch (actionID) {
            case CauldronDataAction:
                ByteBufUtils.writeTag(buf, cauldrondata);
                break;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.actionID = buf.readInt();
        switch (actionID) {
            case CauldronDataAction:
                cauldrondata = ByteBufUtils.readTag(buf);
                break;
        }
    }

    public static class Handler implements IMessageHandler<ServerToClientPacket, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ServerToClientPacket message, MessageContext ctx) {
            switch (message.actionID) {
                case CauldronDataAction:
                    CauldronWorldData.currentData.readFromNBT(message.cauldrondata);
                    break;
            }
            return null;
        }
    }
}
