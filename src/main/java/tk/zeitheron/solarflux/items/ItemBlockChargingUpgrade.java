package tk.zeitheron.solarflux.items;

import java.util.List;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.block.tile.TileBaseSolar;
import tk.zeitheron.solarflux.init.ItemsSF;
import tk.zeitheron.solarflux.utils.BlockPosFace;
import tk.zeitheron.solarflux.utils.InventoryDummy;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockChargingUpgrade extends ItemUpgrade
{
	public ItemBlockChargingUpgrade()
	{
		setRegistryName(InfoSF.MOD_ID, "block_charging_upgrade");
	}
	
	IEnergyStorage estorage;
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing) && (estorage = tile.getCapability(CapabilityEnergy.ENERGY, facing)) != null && estorage.canReceive())
		{
			ItemStack held = player.getHeldItem(hand);
			NBTTagCompound nbt = held.getTagCompound();
			if(nbt == null)
				held.setTagCompound(nbt = new NBTTagCompound());
			nbt.setInteger("Dim", worldIn.provider.getDimension());
			nbt.setLong("Pos", pos.toLong());
			nbt.setByte("Face", (byte) facing.ordinal());
			estorage = null;
			worldIn.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, .25F, 1.8F);
			return EnumActionResult.SUCCESS;
		}
		estorage = null;
		return EnumActionResult.FAIL;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("Pos", NBT.TAG_LONG) && stack.getTagCompound().hasKey("Face", NBT.TAG_BYTE);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if(hasEffect(stack))
		{
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("Dim", NBT.TAG_INT))
				tooltip.add("Dimension: " + nbt.getInteger("Dim"));
			tooltip.add("Facing: " + EnumFacing.values()[nbt.getByte("Face")]);
			BlockPos pos = BlockPos.fromLong(nbt.getLong("Pos"));
			tooltip.add("X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ());
		}
	}
	
	@Override
	public int getMaxUpgrades()
	{
		return 1;
	}
	
	@Override
	public boolean canInstall(TileBaseSolar tile, ItemStack stack, InventoryDummy upgradeInv)
	{
		BlockPos pos;
		TileEntity t;
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("Pos", NBT.TAG_LONG) && stack.getTagCompound().hasKey("Face", NBT.TAG_BYTE) && (!stack.getTagCompound().hasKey("Dim", NBT.TAG_INT) || tile.getWorld().provider.getDimension() == stack.getTagCompound().getInteger("Dim")) && tile.getPos().distanceSq(BlockPos.fromLong(stack.getTagCompound().getLong("Pos"))) <= 256D && (pos = BlockPos.fromLong(stack.getTagCompound().getLong("Pos"))) != null && (t = tile.getWorld().getTileEntity(pos)) != null && t.hasCapability(CapabilityEnergy.ENERGY, EnumFacing.values()[stack.getTagCompound().getByte("Face")]);
	}
	
	@Override
	public boolean canStayInPanel(TileBaseSolar tile, ItemStack stack, InventoryDummy upgradeInv)
	{
		return canInstall(tile, stack, upgradeInv);
	}
	
	@Override
	public void update(TileBaseSolar tile, ItemStack stack, int amount)
	{
		NBTTagCompound nbt = stack.getTagCompound();
		if(tile.getWorld().getTotalWorldTime() % 20L == 0L)
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
				tile.traversal.add(new BlockPosFace(pos, EnumFacing.values()[nbt.getByte("Face")], (float) (1 - d)));
			}
		}
	}
}