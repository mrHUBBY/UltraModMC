package com.hubby.ultra;

import com.hubby.ultra.items.UltraItemTeleportArtifact;
import com.hubby.utils.HubbyUtils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item.ToolMaterial;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UltraEventHooks {
    
    private int _entityId = 0;
    private EntityLivingBase _ent = null;

    /**
     * This event is fired when an entity is spawned and joining the world
     * for the first time.
     * @param event - The <code>Event</code> being fired
     */
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event) {
	    
	    // Check for the client
	    if (!HubbyUtils.isClientSide(event.world)) {
	        return;
	    }
	    
	    // TODO:
	    // Change this as this is just test code
	    // If we have this item in our possession then new entities should get some
	    // dazzling golden armor just for fun
        if (HubbyUtils.getInventoryItemLocations(UltraItemTeleportArtifact.class).size() > 0) {
            if (EntitySkeleton.class.isInstance(event.entity) || EntityZombie.class.isInstance(event.entity)) {
                HubbyUtils.addFullVanillaArmorToEntity((EntityLivingBase)event.entity, ToolMaterial.GOLD); 
            }
        }
	}
	
	@SubscribeEvent
	public void onEntityLivingSpawned(LivingSpawnEvent event) { 
	}

	@SubscribeEvent
	public void onAttemptEntityDespawn(AllowDespawn event) {
	}

	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent event) {
	}

	@SubscribeEvent
	public void onTargetAcquired(LivingSetAttackTargetEvent event) {
	}

	@SubscribeEvent
	public void onBucketFill(FillBucketEvent event) {

//        ItemStack result = NitroInterface.fillBucketWithLiquid(event.world, event.target);
//
//		if (result == null) {
//			return;
//		}
//
//		event.result = result;
//		event.setResult(Result.ALLOW);
	}

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
	}

	@SubscribeEvent
	public void onEntityAttacked(LivingAttackEvent event) {
//		EntityLivingBase e = event.entityLiving;
//		DamageSource d = event.source;
//		boolean isVillager = e instanceof EntityVillager;
//
//        if ((e.isPotionActive(NitroInterface.nitroPotion) || d.getDamageType() == NitroInterface.nitroDamageSource.getDamageType()) && isVillager) {
//            EntityZombie entityzombie = new EntityZombie(event.entityLiving.worldObj);
//			entityzombie.copyLocationAndAnglesFrom(event.entityLiving);
//
//			event.entityLiving.worldObj.removeEntity(event.entityLiving);
//			event.entityLiving.worldObj.spawnEntityInWorld(entityzombie);
//		}
//
//		// If we were attacked by nitro destroyer do extra damage
//		if (d.getSourceOfDamage() instanceof NitroEntityDestroyer && event.source != NitroInterface.nitroDamageSource) {
//            NitroEntityDestroyer ent = (NitroEntityDestroyer) d.getSourceOfDamage();
//			if (ent.getHeldItem() != null) {
//                double damage = ent.getEntityAttribute(NitroInterface.nitroDestroyerSwordAttackDamageAttribute).getAttributeValue();
//                e.attackEntityFrom(NitroInterface.nitroDamageSource, (float) damage);
//			}
//		}
//
//		// Handle the player using the nitro sword
//		EntityPlayer player = d.getSourceOfDamage() instanceof EntityPlayer ? (EntityPlayer) d.getSourceOfDamage() : null;
//		if (player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == NitroInterface.nitroSword) {
//            e.attackEntityFrom(NitroInterface.nitroDamageSource, NitroInterface.nitroSwordDamage);
//		}
//
//		// set the ent we are tracking since we attacked it
//		if (player != null && e != null) {
//			NitroInterface.nitroEntityGui.setActiveTarget(e);
//		}
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
//		EntityLivingBase e = event.entityLiving;
//
//        if (e instanceof EntityZombie && e.isPotionActive(NitroInterface.nitroPotion)) {
//			e.dropItem(NitroInterface.nitroIngot, 2);
//		}
//        if (e instanceof EntityEnderman && e.isPotionActive(NitroInterface.nitroPotion)) {
//            EntityVillager entityVillager = new EntityVillager(event.entityLiving.worldObj);
//			entityVillager.copyLocationAndAnglesFrom(event.entityLiving);
//
//			event.entityLiving.worldObj.removeEntity(event.entityLiving);
//			event.entityLiving.worldObj.spawnEntityInWorld(entityVillager);
//		}
	}

	@SubscribeEvent
	public void onPlayerItemUsed(PlayerUseItemEvent.Start event) {

//		EntityPlayer player = event.entityPlayer;
//		ItemStack item = event.item;
//		boolean isNitroPotion = false;
//
//		if (item.getItem() instanceof ItemPotion) {
//			ItemPotion p = (ItemPotion) item.getItem();
//			List<PotionEffect> effects = p.getEffects(item);
//
//			if (effects != null) {
//    			for (PotionEffect e : effects) {
//    				if (e.getPotionID() == NitroInterface.nitroPotion.id) {
//
//    					NitroInterface.onNitroPotionUsed(player, p);
//    					break;
//    				}
//    			}
//			}
//		}
	}

	@SubscribeEvent
	public void onPlayerJumped(LivingJumpEvent event) {

//		if (event.entityLiving == NitroInterface.nitroPlayer) {
//            if (event.entityLiving.isPotionActive(NitroInterface.nitroPotion.id)) {
//				NitroInterface.nitroPlayer.motionY += NitroInterface.nitroJumpModifier;
//				NitroInterface.nitroPlayer.velocityChanged = true;
//			}
//		}
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {

//		// If the player broke a nitro block, check their tool they used
//        if (Block.getIdFromBlock(event.block) == Block.getIdFromBlock(NitroInterface.nitroBlock)) {
//			ItemStack equipped = event.getPlayer().getCurrentEquippedItem();
//			Item item = equipped != null ? equipped.getItem() : null;
//            if (item != null && Item.getIdFromItem(item) == Item.getIdFromItem(NitroInterface.nitroPickaxe)) {
//                event.world.createExplosion(event.getPlayer(), event.x, event.y, event.z, NitroInterface.nitroBreakExplosionRadius, true);
//			}
//		}
	}
}
