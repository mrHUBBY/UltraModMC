package com.hubby.ultra.setup.client;

import java.util.ArrayList;
import java.util.List;

import com.hubby.network.HubbyClientPacketHandler;
import com.hubby.ultra.setup.UltraCommonProxy;
import com.hubby.ultra.setup.UltraRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * The proxy object that is used on the client
 * @author davidleistiko
 */
public class UltraClientProxy extends UltraCommonProxy {

    /**
     * Register any renderers needed on the client
     */
    @Override
    public void registerRenderers() {
//        RenderingRegistry.registerEntityRenderingHandler(NitroEntityFireball.class, new RenderSnowball(NitroInterface.nitroFireball));
//        RenderingRegistry.registerEntityRenderingHandler(NitroEntityGrenade.class, new RenderSnowball(NitroInterface.nitroGrenade));
//        RenderingRegistry.registerEntityRenderingHandler(NitroEntityDestroyer.class, new NitroRenderEntityDestroyer(new NitroModelDestroyer(), 0.5F));
//        RenderingRegistry.registerEntityRenderingHandler(NitroEntityDeathPhantom.class, new NitroRenderEntityDeathPhantom(new NitroModelDeathPhantom(), 0.5F));
//        RenderingRegistry.registerEntityRenderingHandler(NitroEntityZombie.class, new NitroRenderEntityZombie(new NitroModelZombie(), 0.5F));
    }

    /**
     * Get the gui element for the server
     */
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    	BlockPos pos = new BlockPos(x, y, z);
        TileEntity tileEntity = world.getTileEntity(pos);

//        if (id == NitroInterface.nitroBlockGuiContainerID) {
//            if (tileEntity instanceof NitroChestTileEntity) {
//                NitroBlockContainer c = new NitroBlockContainer(player.inventory, (NitroChestTileEntity) tileEntity);
//                c.init(new ScaledResolution(Minecraft.getMinecraft().gameSettings, 176, 166));
//                return c;
//            }
//         }

        return null;
    }

    /**
     * Return the client side gui element
     */
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    	BlockPos pos = new BlockPos(x, y, z);
        TileEntity tileEntity = world.getTileEntity(pos);

//        if (id == NitroInterface.nitroBlockGuiContainerID) {
//            if (tileEntity instanceof NitroChestTileEntity) {
//                return new NitroBlockGuiContainer(player.inventory, (NitroChestTileEntity) tileEntity);
//            }
//         }

        return null;
    }

    /**
     * Register any required packet handlers
     */
    @Override
    // Register packet handler
    public void registerPacketHandler() {
        List<String> networkChannels = new ArrayList<String>();
        networkChannels.add(UltraRegistry.ultraNetworkChannelName);    
        UltraRegistry.ultraNetworkChannel.register(new HubbyClientPacketHandler(networkChannels));
    }
    
    /**
     * Are we in single player mode?
     * @return boolean - always return true for the client
     */
    public boolean isSinglePlayer() {
        return true;
    }
}
