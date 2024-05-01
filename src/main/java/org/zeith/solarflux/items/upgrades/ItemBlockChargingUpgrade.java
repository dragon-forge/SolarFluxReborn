package org.zeith.solarflux.items.upgrades;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.solarflux.api.IFurnaceBlockEntity;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.init.ItemsSF;
import org.zeith.solarflux.items.data.GlobalFaceComponent;
import org.zeith.solarflux.items.upgrades._base.UpgradeItem;
import org.zeith.solarflux.util.BlockPosFace;

import java.util.List;

import static org.zeith.solarflux.init.SolarPanelsSF.BLOCK_CHARGING_UPGRADE_RANGE;

public class ItemBlockChargingUpgrade
		extends UpgradeItem
{
	public ItemBlockChargingUpgrade()
	{
		super(new Properties().stacksTo(1).component(GlobalFaceComponent.TYPE, null));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		var face = stack.get(GlobalFaceComponent.TYPE);
		if(face == null) return;
		
		tooltip.add(Component.literal("Dimension: " + face.pos().dimension().location()));
		tooltip.add(Component.literal("Facing: " + face.dir().getName()));
		BlockPos pos = face.pos().pos();
		tooltip.add(Component.literal("X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ()));
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockEntity tile = level.getBlockEntity(pos);
		
		// Allow furnaces to be bound as well.
		if(tile instanceof IFurnaceBlockEntity furnace && context.getClickedFace() == furnace.getSideForSolarPanel())
		{
			ItemStack held = context.getItemInHand();
			held.set(GlobalFaceComponent.TYPE, new GlobalFaceComponent(GlobalPos.of(level.dimension(), pos), context.getClickedFace()));
			level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, .25F, 1.8F);
			return InteractionResult.SUCCESS;
		}
		
		var estorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, context.getClickedFace());
		if(estorage != null && estorage.canReceive())
		{
			ItemStack held = context.getItemInHand();
			held.set(GlobalFaceComponent.TYPE, new GlobalFaceComponent(GlobalPos.of(level.dimension(), pos), context.getClickedFace()));
			level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, .25F, 1.8F);
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.FAIL;
	}
	
	@Override
	public boolean isFoil(ItemStack stack)
	{
		return stack.has(GlobalFaceComponent.TYPE);
	}
	
	@Override
	public boolean canInstall(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		var face = stack.get(GlobalFaceComponent.TYPE);
		
		BlockPos pos;
		return face != null
			   && tile.level().dimension().equals(face.pos().dimension())
			   && tile.pos().distSqr(pos = face.pos().pos()) <= BLOCK_CHARGING_UPGRADE_RANGE
			   && (tile.level().getCapability(Capabilities.EnergyStorage.BLOCK, pos, face.dir()) != null
				   || (tile.level().getBlockEntity(pos) instanceof IFurnaceBlockEntity furnace && face.dir() == furnace.getSideForSolarPanel() && tile.getUpgrades(ItemsSF.FURNACE_UPGRADE) > 0));
	}
	
	@Override
	public boolean canStayInPanel(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return canInstall(tile, stack, upgradeInv);
	}
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		var face = stack.get(GlobalFaceComponent.TYPE);
		if(face == null || tile.level().getDayTime() % 20L != 0L) return;
		
		BlockPos pos = face.pos().pos();
		
		double d;
		if((d = tile.pos().distSqr(pos)) <= BLOCK_CHARGING_UPGRADE_RANGE)
		{
			d /= BLOCK_CHARGING_UPGRADE_RANGE;
			tile.traversal().clear();
			if(tile.getUpgrades(ItemsSF.TRAVERSAL_UPGRADE) > 0)
			{
				ItemTraversalUpgrade.cache.clear();
				ItemTraversalUpgrade.cache.add(pos);
				ItemTraversalUpgrade.findMachines(tile, ItemTraversalUpgrade.cache, tile.traversal());
			}
			tile.traversal().add(new BlockPosFace(pos, face.dir(), (float) (1 - d)));
		}
	}
	
	@Override
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[] { Math.round((float) Math.sqrt(BLOCK_CHARGING_UPGRADE_RANGE)) };
	}
}