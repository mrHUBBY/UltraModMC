package com.hubby.network;

import java.io.IOException;

import com.hubby.utils.HubbyConstants;
import com.hubby.utils.HubbyConstants.LogChannel;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

public class HubbyServerPacketProcessorPlayerInventory implements HubbyServerPacketProcessorInterface {

    /**
     * Handle processing the packet that has come down from the client
     * @param packet - the actual packet
     * @param buffer - the buffer with our data
     * @param side - the side that this is occurring on
     * @param player - the multiplayer player
     */
    @Override
    public boolean processServerPacket(FMLProxyPacket packet, ByteBuf buffer, Side side, EntityPlayerMP player) throws IOException {
        
        // read the packet to get all values
        PacketBuffer packetBuffer = new PacketBuffer(buffer);
        packet.readPacketData(packetBuffer);

        // switch on the packet type
        int value = buffer.readInt();
        Enum packetType = HubbyNetworkHelper.getPacketTypeForValue(value);
        String name = HubbyNetworkHelper.getNameForPacketType(packetType);
        
        if (packetType == HubbyConstants.HubbyClientPacketType.PLAYER_INVENTORY) {
            ItemStack stack = packetBuffer.readItemStackFromBuffer();
            int slot = packetBuffer.readInt();
       
            // write out the log message that shows the values that we are grabbing from the packet buffer
            LogChannel.INFO.start(HubbyServerPacketProcessorPlayerInventory.class, "%s", name).
            append("\tItemStack: %s", (stack != null && stack.getItem() != null) ? stack.getItem().getUnlocalizedName() : "null").
            append("\tSlot: %d", slot).
            end();

            // place the itemstack into the player's inventory
            player.inventoryContainer.setPlayerIsPresent(player, true);
            player.inventoryContainer.putStackInSlot(slot, stack);
            return true;
        }
        return false;
    }
}
