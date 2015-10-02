package com.hubby.network;

import java.io.IOException;
import java.util.Map;

import com.hubby.network.HubbyNetworkHelper.ProcessPacketResult;
import com.hubby.utils.HubbyConstants.HubbyClientPacketType;
import com.hubby.utils.HubbyConstants.LogChannel;
import com.hubby.utils.HubbyEnumValueInterface;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

public class HubbyServerPacketProcessorPlayerInventory extends HubbyServerPacketProcessorInterface {

    /**
     * Constructor
     * @param packetType - the packet type we care about
     */
    public HubbyServerPacketProcessorPlayerInventory(Enum<? extends HubbyEnumValueInterface> packetType) {
        super(packetType);
    }

    /**
     * Handle processing the packet that has come down from the client
     * @param packet - the actual packet
     * @param buffer - the buffer with our data
     * @param side - the side that this is occurring on
     * @param player - the multiplayer player
     * @return ProcessPacketResult - the result of the processing
     */
    @Override
    public ProcessPacketResult processServerPacket(FMLProxyPacket packet, ByteBuf buffer, Side side, EntityPlayerMP player) throws IOException {
        
        // read the packet to get all values
        Map<String, Object> results = HubbyNetworkHelper.readPacketHeader(packet);
        Enum packetType = HubbyNetworkHelper.getPacketTypeForValue((Integer)results.get("packetType"));
        String name = HubbyNetworkHelper.getNameForPacketType(packetType);
        PacketBuffer packetBuffer = (PacketBuffer)results.get("buffer");
        
        String expectedPacketName = HubbyNetworkHelper.getNameForPacketType(HubbyClientPacketType.PLAYER_INVENTORY);
        assert packetType == HubbyClientPacketType.PLAYER_INVENTORY : String.format("Wrong packet type sent to %s server processor", "");
        
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
        
        // default is to continue...
        return ProcessPacketResult.CONTINUE;
    }
}
