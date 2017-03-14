package com.mrdimka.solarfluxreborn.blocks;

import java.util.ArrayList;
import java.util.List;

import com.mrdimka.solarfluxreborn.SolarFluxRebornMod;
import com.mrdimka.solarfluxreborn.config.ModConfiguration;
import com.mrdimka.solarfluxreborn.config.RemoteConfigs;
import com.mrdimka.solarfluxreborn.creativetab.ModCreativeTab;
import com.mrdimka.solarfluxreborn.reference.NBTConstants;
import com.mrdimka.solarfluxreborn.reference.Reference;
import com.mrdimka.solarfluxreborn.te.SolarPanelTileEntity;
import com.mrdimka.solarfluxreborn.utility.Lang;
import com.mrdimka.solarfluxreborn.utility.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SolarPanelBlock extends BlockContainer
{
    private final int mTierIndex;
    
    public SolarPanelBlock(String pName, int pTierIndex)
    {
    	super(Material.IRON);
    	setUnlocalizedName(Reference.MOD_ID + ":" + pName);
        mTierIndex = pTierIndex;
        setCreativeTab(ModCreativeTab.MOD_TAB);
        setHardness(3.0F);
        setHarvestLevel("pickaxe", 0);
        setResistance(5.0F);
        setSoundType(SoundType.METAL);
        setLightOpacity(255);
        useNeighborBrightness = true;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState p_isOpaqueCube_1_)
    {
    	return false;
    }
    
    @Override
    public boolean isFullyOpaque(IBlockState p_isFullyOpaque_1_)
    {
    	return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState p_isFullCube_1_)
    {
    	return false;
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState s, IBlockAccess w, BlockPos p)
    {
    	return new AxisAlignedBB(0, 0, 0, 1, 6D / 16D, 1);
    }
    
    @Override
    public TileEntity createNewTileEntity(World pWorld, int pMetadata)
    {
        return new SolarPanelTileEntity(mTierIndex);
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState p_getRenderType_1_)
    {
    	return EnumBlockRenderType.MODEL;
    }
    
    @Override
    public void onBlockPlacedBy(World w, BlockPos pos, IBlockState s, EntityLivingBase ent, ItemStack stack)
    {
    	super.onBlockPlacedBy(w, pos, s, ent, stack);
    	
    	// When the solar panel is placed, we restore its energy from the Item.
        if(stack.getTagCompound() != null)
        {
            // TODO Consider moving this logic to the Tile Entity class. (could prevent exposing internals of the tile entity) (e.g. readFromItemStack/writeToItemStack)
            SolarPanelTileEntity localTileCell = (SolarPanelTileEntity) w.getTileEntity(pos);
            localTileCell.getInventory().readFromNBT(stack.getTagCompound().getCompoundTag(NBTConstants.ITEMS));
            // Force update to refresh the upgrade cache before restoring the energy.
            localTileCell.markDirty();
            localTileCell.setEnergyStored(stack.getTagCompound().getInteger(NBTConstants.ENERGY));
        }
    }
    
    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState s, EntityPlayer player, EnumHand hand, EnumFacing side, float p_onBlockActivated_8_, float p_onBlockActivated_9_, float p_onBlockActivated_10_)
    {
    	if(player.isSneaking())
        {
            if(Utils.hasUsableWrench(player, pos))
            {
                dismantleBlock(w, pos);
                return true;
            }
        }else if(!w.isRemote)
        {
            if(w.getTileEntity(pos) instanceof SolarPanelTileEntity)
            	player.openGui(SolarFluxRebornMod.instance, 0, w, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    
    private void displayChatInformation(World pWorld, BlockPos pos, EntityPlayer pPlayer) {
        SolarPanelTileEntity tile = (SolarPanelTileEntity) pWorld.getTileEntity(pos);
        String message = String.format(
                "%s: [%d%%] %,d / %,d %s: %,d",
                Lang.localise("energy.stored"),
                tile.getPercentageEnergyStored(),
                tile.getEnergyStored(),
                tile.getMaxEnergyStored(),
                Lang.localise("energy.generation"),
                tile.getCurrentEnergyGeneration());
        pPlayer.sendMessage(new TextComponentString(message));
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
    	dismantleBlock(world, pos);
    	super.breakBlock(world, pos, state);
    }
    
    @Override
    public List<ItemStack> getDrops(IBlockAccess p_getDrops_1_, BlockPos p_getDrops_2_, IBlockState p_getDrops_3_, int p_getDrops_4_)
    {
    	return new ArrayList<ItemStack>();
    }
    
    /**
     * Dismantles the block and drops it in the air.
     * Used when wrenched.
     */
    public void dismantleBlock(World pWorld, BlockPos pos)
    {
    	if(!(pWorld.getTileEntity(pos) instanceof SolarPanelTileEntity)) return; //Issue #38 fix
    	
        // TODO Consider moving this logic to the Tile Entity class. (could prevent exposing internals of the tile entity) (e.g. readFromItemStack/writeToItemStack)
        ItemStack itemStack = new ItemStack(this);
        
        // Store the energy in the ItemStack (from the TileEntity)
        if(ModConfiguration.doesKeepEnergyWhenDismantled())
        {
            SolarPanelTileEntity localTileCell = (SolarPanelTileEntity) pWorld.getTileEntity(pos);
            int internalEnergy = localTileCell.getEnergyStored();
            if(internalEnergy > 0)
            {
                if(itemStack.getTagCompound() == null) itemStack.setTagCompound(new NBTTagCompound());
                itemStack.getTagCompound().setInteger(NBTConstants.ENERGY, (int) (internalEnergy * 0.01F));
            }
        }
        
        // Store the inventory in the ItemStack (from the TileEntity)
        if(ModConfiguration.doesKeepInventoryWhenDismantled())
        {
            SolarPanelTileEntity localTileCell = (SolarPanelTileEntity) pWorld.getTileEntity(pos);
            int upgradeInstalled = localTileCell.getTotalUpgradeInstalled();
            
            if(upgradeInstalled > 0)
            {
                if(itemStack.getTagCompound() == null) itemStack.setTagCompound(new NBTTagCompound());
                NBTTagCompound nbt = itemStack.getTagCompound().getCompoundTag(NBTConstants.ITEMS);
                localTileCell.getInventory().writeToNBT(nbt);
                itemStack.getTagCompound().setTag(NBTConstants.ITEMS, nbt);
                
                // Adding info for tooltip.
                itemStack.getTagCompound().setInteger(NBTConstants.TOOLTIP_UPGRADE_COUNT, upgradeInstalled);
                itemStack.getTagCompound().setInteger(NBTConstants.TOOLTIP_CAPACITY, localTileCell.getEnergyStorage().getMaxEnergyStored());
                itemStack.getTagCompound().setInteger(NBTConstants.TOOLTIP_TRANSFER_RATE, localTileCell.getEnergyStorage().getMaxTransferExtract());
                
                // We remove the tile entity to avoid having the inventory content dropped into the world.
                pWorld.removeTileEntity(pos);
            }
        }else
        {
        	SolarPanelTileEntity localTileCell = (SolarPanelTileEntity) pWorld.getTileEntity(pos);
            int upgradeInstalled = localTileCell.getTotalUpgradeInstalled();
            if(upgradeInstalled > 0) localTileCell.getInventory().drop(pWorld, pos);
        }
        
        pWorld.setBlockToAir(pos);
        pWorld.spawnEntity(new EntityItem(pWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, itemStack));
    }
    
    public int getTierIndex()
    {
        return mTierIndex;
    }
    
    public int getCapacity()
    {
    	return RemoteConfigs.getTierConfiguration(mTierIndex).getCapacity();
    }
}