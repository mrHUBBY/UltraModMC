package com.hubby.ultra.setup.client;

import com.hubby.ultra.setup.CommonProxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenderers() {
//        RenderingRegistry.registerEntityRenderingHandler(NitroEntityFireball.class, new RenderSnowball(NitroInterface.nitroFireball));
//        RenderingRegistry.registerEntityRenderingHandler(NitroEntityGrenade.class, new RenderSnowball(NitroInterface.nitroGrenade));
//        RenderingRegistry.registerEntityRenderingHandler(NitroEntityDestroyer.class, new NitroRenderEntityDestroyer(new NitroModelDestroyer(), 0.5F));
//        RenderingRegistry.registerEntityRenderingHandler(NitroEntityDeathPhantom.class, new NitroRenderEntityDeathPhantom(new NitroModelDeathPhantom(), 0.5F));
//        RenderingRegistry.registerEntityRenderingHandler(NitroEntityZombie.class, new NitroRenderEntityZombie(new NitroModelZombie(), 0.5F));
    }

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

    // returns an instance of the Gui you made earlier
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

    @Override
    // Register packet handler
    public void registerPacketHandler() {
  //      NitroInterface.networkChannel.register(new NitroClientPacketHandler());
    }
}
