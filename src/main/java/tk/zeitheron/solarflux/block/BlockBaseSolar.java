package tk.zeitheron.solarflux.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.api.SolarFluxAPI;
import tk.zeitheron.solarflux.api.SolarInfo;
import tk.zeitheron.solarflux.api.SolarInstance;
import tk.zeitheron.solarflux.block.tile.TileBaseSolar;
import tk.zeitheron.solarflux.items.ItemUpgrade;
import tk.zeitheron.solarflux.utils.PositionedStateImplementation;

public class BlockBaseSolar
		extends Block
		implements ITileEntityProvider
{
	public final SolarInfo solarInfo;

	public BlockBaseSolar(SolarInfo solarInfo)
	{
		super(Material.IRON);
		setCreativeTab(SolarFluxAPI.tab);
		setSoundType(SoundType.METAL);
		this.solarInfo = solarInfo;
		ResourceLocation r = solarInfo.getRegistryName();
		setRegistryName(r.getNamespace(), (solarInfo.isCustom ? "custom_" : "") + "solar_panel_" + r.getPath());
		setTranslationKey(getRegistryName().toString());
		setHardness(4F);
		setHarvestLevel("pickaxe", 2);
	}

	protected AxisAlignedBB aabb = new AxisAlignedBB(0, 0, 0, 1, 0.375, 1);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if(Math.abs(solarInfo.getHeight() / 16F - aabb.maxY) > 1.0E-4)
			aabb = new AxisAlignedBB(0, 0, 0, 1, solarInfo.getHeight() + 1 / 64F, 1);
		return aabb;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		if(isSideSolid(state, worldIn, pos, face))
			return BlockFaceShape.SOLID;
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return side == EnumFacing.DOWN;
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(solarInfo.getGeneration() <= 0 || solarInfo.getCapacity() <= 0 || solarInfo.getTransfer() <= 0)
			return;
		super.getSubBlocks(tab, items);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		SolarInstance i = new SolarInstance();
		i.delegate = solarInfo.getRegistryName();
		i.reset();
		return new TileBaseSolar(i);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			TileBaseSolar spt;
			TileEntity tile = worldIn.getTileEntity(pos);
			if(tile instanceof TileBaseSolar) spt = (TileBaseSolar) tile;
			else
			{
				spt = (TileBaseSolar) createNewTileEntity(worldIn, 0);
				worldIn.setTileEntity(pos, spt);
			}
			spt.loadFromItem(stack);
		}
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		if(!canHarvestBlock(worldIn, pos, player))
		{
			super.onBlockHarvested(worldIn, pos, state, player);
			return;
		}

		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(tileentity instanceof TileBaseSolar)
		{
			TileBaseSolar te = (TileBaseSolar) tileentity;
			if(!worldIn.isRemote)
			{
				if(!player.isSneaking())
				{
					te.upgradeInventory.drop(worldIn, pos);
					te.chargeInventory.drop(worldIn, pos);
				}
				EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + getPanelData().getHeight() / 2F, pos.getZ() + 0.5, player.isSneaking() ? te.generateItem(Item.getItemFromBlock(this)) : new ItemStack(this));
				item.setDefaultPickupDelay();
				worldIn.spawnEntity(item);
			}
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TileBaseSolar)
		{
			ItemStack held = playerIn.getHeldItem(hand);
			if(!held.isEmpty() && held.getItem() instanceof ItemUpgrade)
			{
				TileBaseSolar tbs = (TileBaseSolar) worldIn.getTileEntity(pos);
				int amt = tbs.getUpgrades(held.getItem());
				ItemUpgrade iu = (ItemUpgrade) held.getItem();
				if(amt < iu.getMaxUpgrades() && iu.canInstall(tbs, held, tbs.upgradeInventory))
				{
					boolean installed = false;
					for(int i = 0; i < tbs.upgradeInventory.getSizeInventory(); ++i)
					{
						ItemStack stack = tbs.upgradeInventory.getStackInSlot(i);
						if(stack.isItemEqual(held) && ItemStack.areItemStackTagsEqual(stack, held))
						{
							int allow = Math.min(iu.getMaxUpgrades() - tbs.getUpgrades(iu), Math.min(iu.getItemStackLimit(stack) - stack.getCount(), held.getCount()));
							stack.grow(allow);
							held.shrink(allow);
							installed = true;
							break;
						} else if(stack.isEmpty())
						{
							int allow = Math.min(iu.getMaxUpgrades() - tbs.getUpgrades(iu), held.getCount());
							ItemStack copy = held.copy();
							held.shrink(allow);
							copy.setCount(allow);
							tbs.upgradeInventory.setInventorySlotContents(i, copy);
							installed = true;
							break;
						}
					}
					if(installed)
					{
						worldIn.playSound(null, pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, .1F, 1F);
						return true;
					}
				}
			}
			playerIn.openGui(SolarFlux.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return PositionedStateImplementation.actualize(state, world, pos);
	}

	public SolarInfo getPanelData()
	{
		return solarInfo;
	}
}