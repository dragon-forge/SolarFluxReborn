package org.zeith.solarflux.items;

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
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.init.ItemsSF;
import org.zeith.solarflux.util.BlockPosFace;

import javax.annotation.Nullable;
import java.util.List;

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
		BlockEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());
		
		// Allow furnaces to be bound as well.
		if(tile instanceof AbstractFurnaceBlockEntity furnace && context.getClickedFace() == Direction.UP)
		{
			ItemStack held = context.getItemInHand();
			CompoundTag nbt = held.getTag();
			if(nbt == null)
				held.setTag(nbt = new CompoundTag());
			nbt.putString("Dim", context.getLevel().dimension().location().toString());
			nbt.putLong("Pos", context.getClickedPos().asLong());
			nbt.putByte("Face", (byte) context.getClickedFace().ordinal());
			context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, .25F, 1.8F);
			return InteractionResult.SUCCESS;
		}
		
		return tile != null ? tile.getCapability(ForgeCapabilities.ENERGY, context.getClickedFace()).filter(IEnergyStorage::canReceive).map(estorage ->
		{
			ItemStack held = context.getItemInHand();
			CompoundTag nbt = held.getTag();
			if(nbt == null)
				held.setTag(nbt = new CompoundTag());
			nbt.putString("Dim", context.getLevel().dimension().location().toString());
			nbt.putLong("Pos", context.getClickedPos().asLong());
			nbt.putByte("Face", (byte) context.getClickedFace().ordinal());
			context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, .25F, 1.8F);
			return InteractionResult.SUCCESS;
		}).orElse(InteractionResult.FAIL) : InteractionResult.FAIL;
	}
	
	@Override
	public boolean isFoil(ItemStack stack)
	{
		return stack.hasTag() && stack.getTag().contains("Pos", Tag.TAG_LONG) && stack.getTag().contains("Face", Tag.TAG_BYTE);
	}
	
	@Override
	public boolean canInstall(SolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		BlockPos pos;
		BlockEntity t;
		return isFoil(stack) &&
				(!stack.getTag().contains("Dim", Tag.TAG_STRING)
						|| tile.getLevel().dimension().location().toString().equals(stack.getTag().getString("Dim")))
				&& tile.getBlockPos().distSqr(pos = BlockPos.of(stack.getTag().getLong("Pos"))) <= 256D
				&& (t = tile.getLevel().getBlockEntity(pos)) != null
				&& (
				(t instanceof AbstractFurnaceBlockEntity && DIRECTIONS[stack.getTag().getByte("Face")] == Direction.UP)
						|| t.getCapability(ForgeCapabilities.ENERGY, DIRECTIONS[stack.getTag().getByte("Face")]).isPresent()
		);
	}
	
	@Override
	public boolean canStayInPanel(SolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return canInstall(tile, stack, upgradeInv);
	}
	
	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		CompoundTag nbt = stack.getTag();
		if(tile.getLevel().getDayTime() % 20L == 0L)
		{
			BlockPos pos = BlockPos.of(nbt.getLong("Pos"));
			
			double d;
			if((d = tile.getBlockPos().distSqr(pos)) <= 256D)
			{
				d /= 256;
				tile.traversal.clear();
				if(tile.getUpgrades(ItemsSF.TRAVERSAL_UPGRADE) > 0)
				{
					ItemTraversalUpgrade.cache.clear();
					ItemTraversalUpgrade.cache.add(pos);
					ItemTraversalUpgrade.findMachines(tile, ItemTraversalUpgrade.cache, tile.traversal);
				}
				tile.traversal.add(new BlockPosFace(pos, Direction.values()[nbt.getByte("Face")], (float) (1 - d)));
			}
		}
	}
}