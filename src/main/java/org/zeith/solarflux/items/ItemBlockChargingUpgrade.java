package org.zeith.solarflux.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.util.BlockPosFace;

import java.util.List;

@SimplyRegister
public class ItemBlockChargingUpgrade
		extends UpgradeItem
{
	@RegistryName("block_charging_upgrade")
	public static final ItemBlockChargingUpgrade BLOCK_CHARGING_UPGRADE = new ItemBlockChargingUpgrade();

	public ItemBlockChargingUpgrade()
	{
		super(1);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		if(isFoil(stack))
		{
			CompoundNBT nbt = stack.getTag();
			if(nbt.contains("Dim", NBT.TAG_STRING))
				tooltip.add(new StringTextComponent("Dimension: " + nbt.getString("Dim")));
			tooltip.add(new StringTextComponent("Facing: " + Direction.values()[nbt.getByte("Face")]));
			BlockPos pos = BlockPos.of(nbt.getLong("Pos"));
			tooltip.add(new StringTextComponent("X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ()));
		}
	}

	@Override
	public ActionResultType useOn(ItemUseContext context)
	{
		TileEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());
		return tile != null ? tile.getCapability(CapabilityEnergy.ENERGY, context.getClickedFace()).filter(IEnergyStorage::canReceive).map(estorage ->
		{
			ItemStack held = context.getItemInHand();
			CompoundNBT nbt = held.getTag();
			if(nbt == null)
				held.setTag(nbt = new CompoundNBT());
			nbt.putString("Dim", context.getLevel().dimension().location().toString());
			nbt.putLong("Pos", context.getClickedPos().asLong());
			nbt.putByte("Face", (byte) context.getClickedFace().ordinal());
			estorage = null;
			context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.PLAYER_LEVELUP, SoundCategory.PLAYERS, .25F, 1.8F);
			return ActionResultType.SUCCESS;
		}).orElse(ActionResultType.FAIL) : ActionResultType.FAIL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack)
	{
		return stack.hasTag() && stack.getTag().contains("Pos", NBT.TAG_LONG) && stack.getTag().contains("Face", NBT.TAG_BYTE);
	}

	@Override
	public boolean canInstall(SolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		BlockPos pos;
		TileEntity t;
		return stack.hasTag() && stack.getTag().contains("Pos", NBT.TAG_LONG) && stack.getTag().contains("Face", NBT.TAG_BYTE) && (!stack.getTag().contains("Dim", NBT.TAG_STRING) || tile.getLevel().dimension().location().toString().equals(stack.getTag().getString("Dim"))) && tile.getBlockPos().distSqr(pos = BlockPos.of(stack.getTag().getLong("Pos"))) <= 256D && (t = tile.getLevel().getBlockEntity(pos)) != null && t.getCapability(CapabilityEnergy.ENERGY, Direction.values()[stack.getTag().getByte("Face")]).isPresent();
	}

	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		CompoundNBT nbt = stack.getTag();
		if(tile.getLevel().getDayTime() % 20L == 0L)
		{
			BlockPos pos = BlockPos.of(nbt.getLong("Pos"));

			double d;
			if((d = tile.getBlockPos().distSqr(pos)) <= 256D)
			{
				d /= 256;
				tile.traversal.clear();
				if(tile.getUpgrades(ItemTraversalUpgrade.TRAVERSAL_UPGRADE) > 0)
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