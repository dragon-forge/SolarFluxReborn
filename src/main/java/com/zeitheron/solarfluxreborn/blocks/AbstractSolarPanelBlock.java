package com.zeitheron.solarfluxreborn.blocks;

import java.util.ArrayList;
import java.util.List;

import com.zeitheron.solarfluxreborn.creativetab.CreativeTabSFR;
import com.zeitheron.solarfluxreborn.reference.InfoSFR;
import com.zeitheron.solarfluxreborn.reference.NBTConstants;
import com.zeitheron.solarfluxreborn.te.AbstractSolarPanelTileEntity;
import com.zeitheron.solarfluxreborn.te.SolarPanelTileEntity;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class AbstractSolarPanelBlock extends SolarPanelBlock
{
	public boolean renderConnectedTextures = false;
	public final int cap, transfer, maxGen;
	protected String name;
	
	public AbstractSolarPanelBlock(String pName, int cap, int transfer, int maxGen)
	{
		super(pName, 0);
		setUnlocalizedName(InfoSFR.MOD_ID + ":" + pName);
		name = pName;
		this.cap = cap;
		this.transfer = transfer;
		this.maxGen = maxGen;
		setCreativeTab(CreativeTabSFR.MOD_TAB);
		setHardness(3.0F);
		setHarvestLevel("pickaxe", 0);
		setResistance(5.0F);
		setSoundType(SoundType.METAL);
		setLightOpacity(255);
		useNeighborBrightness = true;
	}
	
	public AbstractSolarPanelBlock setUseConnectedTextures()
	{
		renderConnectedTextures = true;
		return this;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState p_isOpaqueCube_1_)
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState p_isFullCube_1_)
	{
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World pWorld, int pMetadata)
	{
		return new AbstractSolarPanelTileEntity(name, cap, transfer, maxGen);
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState p_getRenderType_1_)
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public void onBlockPlacedBy(World w, BlockPos pos, IBlockState s, EntityLivingBase ent, ItemStack stack)
	{
		super.onBlockPlacedBy(w, pos, s, ent, stack);
		
		// When the solar panel is placed, we restore its energy from the Item.
		if(stack.getTagCompound() != null)
		{
			// TODO Consider moving this logic to the Tile Entity class. (could
			// prevent exposing internals of the tile entity) (e.g.
			// readFromItemStack/writeToItemStack)
			SolarPanelTileEntity localTileCell = (SolarPanelTileEntity) w.getTileEntity(pos);
			localTileCell.getInventory().readFromNBT(stack.getTagCompound().getCompoundTag(NBTConstants.ITEMS));
			// Force update to refresh the upgrade cache before restoring the
			// energy.
			localTileCell.markDirty();
			localTileCell.setEnergyStored(stack.getTagCompound().getInteger(NBTConstants.ENERGY));
			
			if(localTileCell instanceof AbstractSolarPanelTileEntity && stack.getTagCompound().hasKey("MaxGen", NBT.TAG_INT))
			{
				AbstractSolarPanelTileEntity te = (AbstractSolarPanelTileEntity) localTileCell;
				NBTTagCompound nbt = new NBTTagCompound();
				te.writeNBT(nbt);
				nbt.setInteger("MaxGen", stack.getTagCompound().getInteger("MaxGen"));
				te.readNBT(nbt);
			}
		}
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess p_getDrops_1_, BlockPos p_getDrops_2_, IBlockState p_getDrops_3_, int p_getDrops_4_)
	{
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public int getCapacity()
	{
		return cap;
	}
	
	@Override
	public String getSubResource(String sub)
	{
		return InfoSFR.MOD_ID + ":blocks/" + name + "_" + sub;
	}
}