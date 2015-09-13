package com.hubby.ultra.setup;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {

    // Client stuff
    public void registerRenderers() {
    	// Nothing here as the server doesn't render graphics or entities!
    }

   	 @Override
     public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
   		 BlockPos pos = new BlockPos(x, y, z);
         TileEntity tileEntity = world.getTileEntity(pos);

//         if (id ==  NitroInterface.nitroBlockGuiContainerID) {
//        	 if (tileEntity instanceof NitroChestTileEntity){
//        		 NitroBlockContainer c = new NitroBlockContainer(player.inventory, (NitroChestTileEntity) tileEntity);
//        		 c.init(new ScaledResolution(Minecraft.getMinecraft().gameSettings, 176, 166));
//        		 return c;
//        	 }
//         }

         return null;
     }

     //returns an instance of the Gui you made earlier
     @Override
     public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    	 BlockPos pos = new BlockPos(x, y, z);
         TileEntity tileEntity = world.getTileEntity(pos);

//         if (id ==  NitroInterface.nitroBlockGuiContainerID) {
//        	 if (tileEntity instanceof NitroChestTileEntity){
//        		 return new NitroBlockGuiContainer(player.inventory, (NitroChestTileEntity) tileEntity);
//        	 }
//         }

         return null;
     }

     // Register packet handler
     public void registerPacketHandler() {
// 		NitroInterface.networkChannel.register(new NitroServerPacketHandler());
     }
}
