package org.zeith.solarflux.items.upgrades;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import org.zeith.solarflux.items.upgrades._base.UpgradeItem;
import org.zeith.solarflux.util.BlockPosFace;

import javax.annotation.Nullable;
import java.util.List;

import static org.zeith.solarflux.init.SolarPanelsSF.BLOCK_CHARGING_UPGRADE_RANGE;

public class ItemBlockChargingUpgrade
		extends UpgradeItem
{
	public ItemBlockChargingUpgrade()
	{
		super(1);
	}
	
	// We really don't need to make a copy of all values every tick, so this constant is here to save the day.
	private static final Direction[] DIRECTIONS = Direction.values();
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		if(isFoil(stack))
		{
			CompoundTag nbt = stack.getTag();
			if(nbt.contains("Dim", Tag.TAG_STRING))
				tooltip.add(Component.literal("Dimension: " + nbt.getString("Dim")));
			tooltip.add(Component.literal("Facing: " + DIRECTIONS[nbt.getByte("Face")]));
			BlockPos pos = BlockPos.of(nbt.getLong("Pos"));
			tooltip.add(Component.literal("X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ()));
		}
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
			CompoundTag nbt = held.getTag();
			if(nbt == null)
				held.setTag(nbt = new CompoundTag());
			nbt.putString("Dim", level.dimension().location().toString());
			nbt.putLong("Pos", pos.asLong());
			nbt.putByte("Face", (byte) context.getClickedFace().ordinal());
			level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, .25F, 1.8F);
			return InteractionResult.SUCCESS;
		}
		
		var estorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, context.getClickedFace());
		if(estorage != null && estorage.canReceive())
		{
			ItemStack held = context.getItemInHand();
			CompoundTag nbt = held.getTag();
			if(nbt == null)
				held.setTag(nbt = new CompoundTag());
			nbt.putString("Dim", level.dimension().location().toString());
			nbt.putLong("Pos", pos.asLong());
			nbt.putByte("Face", (byte) context.getClickedFace().ordinal());
			level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, .25F, 1.8F);
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.FAIL;
	}
	
	@Override
	public boolean isFoil(ItemStack stack)
	{
		return stack.hasTag() && stack.getTag().contains("Pos", Tag.TAG_LONG) && stack.getTag().contains("Face", Tag.TAG_BYTE);
	}
	
	@Override
	public boolean canInstall(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		BlockPos pos;
		return isFoil(stack) &&
			   (
					   !stack.getTag().contains("Dim", Tag.TAG_STRING)
					   || tile.level().dimension().location().toString().equals(stack.getTag().getString("Dim"))
			   )
			   && tile.pos().distSqr(pos = BlockPos.of(stack.getTag().getLong("Pos"))) <= BLOCK_CHARGING_UPGRADE_RANGE
			   
			   && (
					   (tile.level().getBlockEntity(pos) instanceof IFurnaceBlockEntity furnace && DIRECTIONS[stack.getTag().getByte("Face")] == furnace.getSideForSolarPanel() && tile.getUpgrades(ItemsSF.FURNACE_UPGRADE) > 0)
					   || tile.level().getCapability(Capabilities.EnergyStorage.BLOCK, pos, DIRECTIONS[stack.getTag().getByte("Face")]) != null
			   );
	}
	
	@Override
	public boolean canStayInPanel(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return canInstall(tile, stack, upgradeInv);
	}
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		CompoundTag nbt = stack.getTag();
		if(tile.level().getDayTime() % 20L == 0L)
		{
			BlockPos pos = BlockPos.of(nbt.getLong("Pos"));
			
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
				tile.traversal().add(new BlockPosFace(pos, Direction.values()[nbt.getByte("Face")], (float) (1 - d)));
			}
		}
	}
	
	@Override
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[] { Math.round((float) Math.sqrt(BLOCK_CHARGING_UPGRADE_RANGE)) };
	}
}