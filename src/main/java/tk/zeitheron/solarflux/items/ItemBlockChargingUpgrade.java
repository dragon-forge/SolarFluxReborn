package tk.zeitheron.solarflux.items;

import java.util.List;

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
import tk.zeitheron.solarflux.block.SolarPanelTile;
import tk.zeitheron.solarflux.util.BlockPosFace;
import tk.zeitheron.solarflux.util.SimpleInventory;

public class ItemBlockChargingUpgrade extends UpgradeItem
{
	public ItemBlockChargingUpgrade()
	{
		super(1);
		setRegistryName("block_charging_upgrade");
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		if(hasEffect(stack))
		{
			CompoundNBT nbt = stack.getTag();
			if(nbt.contains("Dim", NBT.TAG_STRING))
				tooltip.add(new StringTextComponent("Dimension: " + nbt.getString("Dim")));
			tooltip.add(new StringTextComponent("Facing: " + Direction.values()[nbt.getByte("Face")]));
			BlockPos pos = BlockPos.fromLong(nbt.getLong("Pos"));
			tooltip.add(new StringTextComponent("X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ()));
		}
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context)
	{
		TileEntity tile = context.getWorld().getTileEntity(context.getPos());
		return tile != null ? tile.getCapability(CapabilityEnergy.ENERGY, context.getFace()).filter(IEnergyStorage::canReceive).map(estorage ->
		{
			ItemStack held = context.getItem();
			CompoundNBT nbt = held.getTag();
			if(nbt == null)
				held.setTag(nbt = new CompoundNBT());
			nbt.putString("Dim", context.getWorld().func_234923_W_().getRegistryName().toString());
			nbt.putLong("Pos", context.getPos().toLong());
			nbt.putByte("Face", (byte) context.getFace().ordinal());
			estorage = null;
			context.getWorld().playSound(null, context.getPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, .25F, 1.8F);
			return ActionResultType.SUCCESS;
		}).orElse(ActionResultType.FAIL) : ActionResultType.FAIL;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return stack.hasTag() && stack.getTag().contains("Pos", NBT.TAG_LONG) && stack.getTag().contains("Face", NBT.TAG_BYTE);
	}
	
	@Override
	public boolean canInstall(SolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		BlockPos pos;
		TileEntity t;
		return stack.hasTag() && stack.getTag().contains("Pos", NBT.TAG_LONG) && stack.getTag().contains("Face", NBT.TAG_BYTE) && (!stack.getTag().contains("Dim", NBT.TAG_STRING) || tile.getWorld().func_234923_W_().getRegistryName().toString().equals(stack.getTag().getString("Dim"))) && tile.getPos().distanceSq(BlockPos.fromLong(stack.getTag().getLong("Pos"))) <= 256D && (pos = BlockPos.fromLong(stack.getTag().getLong("Pos"))) != null && (t = tile.getWorld().getTileEntity(pos)) != null && t.getCapability(CapabilityEnergy.ENERGY, Direction.values()[stack.getTag().getByte("Face")]).isPresent();
	}
	
	@Override
	public boolean canStayInPanel(SolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return canInstall(tile, stack, upgradeInv);
	}
	
	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		CompoundNBT nbt = stack.getTag();
		if(tile.getWorld().getDayTime() % 20L == 0L)
		{
			BlockPos pos = BlockPos.fromLong(nbt.getLong("Pos"));
			
			double d;
			if((d = tile.getPos().distanceSq(pos)) <= 256D)
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