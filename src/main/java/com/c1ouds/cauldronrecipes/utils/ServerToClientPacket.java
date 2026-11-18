package com.c1ouds.cauldronrecipes.utils;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class ServerToClientPacket implements IMessage {
    int actionID;
    private NBTTagCompound cauldrondata;
    public final static int // Action names for easier use
        CauldronDataAction = 0,
        CauldronData_updateBlock = 1,
        Action3 = 2;
    private int X,Y,Z;

    public ServerToClientPacket() {}

    public ServerToClientPacket(CauldronWorldData data) {
        this.actionID = CauldronDataAction;
        cauldrondata = new NBTTagCompound();
        data.writeToNBT(cauldrondata);
    }
    public ServerToClientPacket(CauldronWorldData data, int x,int y,int z) {
        this(data);
        this.actionID = CauldronData_updateBlock;
        X=x; Y=y; Z=z;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(actionID);
        if (actionID < Action3) {
            ByteBufUtils.writeTag(buf, cauldrondata);
            if(actionID == CauldronData_updateBlock) {
                buf.writeInt(X); buf.writeInt(Y); buf.writeInt(Z);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.actionID = buf.readInt();
        if (actionID < Action3) {
            cauldrondata = ByteBufUtils.readTag(buf);
            if(actionID == CauldronData_updateBlock) {
                X=buf.readInt(); Y=buf.readInt(); Z=buf.readInt();
            }
        }
    }

    public static class Handler implements IMessageHandler<ServerToClientPacket, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ServerToClientPacket message, MessageContext ctx) {
            switch (message.actionID) {
                case CauldronDataAction -> CauldronWorldData.currentData.readFromNBT(message.cauldrondata);
                case CauldronData_updateBlock -> {
                    CauldronWorldData.currentData.readFromNBT(message.cauldrondata);
                    Minecraft.getMinecraft().theWorld.markBlockRangeForRenderUpdate(message.X, message.Y, message.Z, message.X, message.Y, message.Z);
                }
            }
            return null;
        }
    }
}
