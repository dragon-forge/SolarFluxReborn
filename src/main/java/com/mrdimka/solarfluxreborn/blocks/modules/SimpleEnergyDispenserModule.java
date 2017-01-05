package com.mrdimka.solarfluxreborn.blocks.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.BlockFurnace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import cofh.api.energy.IEnergyReceiver;

import com.google.common.collect.Lists;
import com.mrdimka.solarfluxreborn.blocks.StatefulEnergyStorage;
import com.mrdimka.solarfluxreborn.config.ModConfiguration;
import com.mrdimka.solarfluxreborn.init.ModItems;
import com.mrdimka.solarfluxreborn.intr.tesla.TeslaAPI;
import com.mrdimka.solarfluxreborn.te.DraconicSolarPanelTileEntity;
import com.mrdimka.solarfluxreborn.te.SolarPanelTileEntity;

/**
 * Module used to distribute energy to neighbor blocks.
 */
public class SimpleEnergyDispenserModule extends AbstractSolarPanelModule {
    private final List<BlockPos> mTargets = Lists.newArrayList();
    private final Map<BlockPos, EnumFacing> mFacings = new HashMap<BlockPos, EnumFacing>();
    private int mTargetStartingIndex;
    private int mFurnaceEnergyBuffer;

    public SimpleEnergyDispenserModule(SolarPanelTileEntity pSolarPanelTileEntity) {
        super(pSolarPanelTileEntity);
    }

    @Override
    public void tick() {
        sendEnergyToTargets();
        if (atRate(getTargetRefreshRate())) {
            searchTargets();
        }
    }

    protected int getTargetRefreshRate() {
        //TODO Should this be a config option?
        return 2 * 20;
    }

    protected void searchTargets() {
        mTargets.clear();
        BlockPos position = getTileEntity().getPos();
        for (EnumFacing direction : EnumFacing.VALUES) {
            BlockPos neighbor = position.offset(direction);
            if (isValidTarget(neighbor)) {
                mTargets.add(neighbor);
                mFacings.put(neighbor, direction);
            }
        }
    }

    protected List<BlockPos> getTargets() {
        return mTargets;
    }

    protected void sendEnergyToTargets() {
        if (mTargets.size() > 0 && getTileEntity().getEnergyStored() > 0) {
            for (int i = 0; i < mTargets.size(); ++i) {
            	BlockPos pos = mTargets.get((mTargetStartingIndex + i) % mTargets.size());
                sendEnergyTo(pos, mFacings.get(pos));
            }
            mTargetStartingIndex = (mTargetStartingIndex + 1) % mTargets.size();
        }
    }

    protected boolean isValidTarget(BlockPos pos) {
        TileEntity tile = getTileEntity().getWorld().getTileEntity(pos);
        return tile instanceof IEnergyReceiver || (getTileEntity().getUpgradeCount(ModItems.mUpgradeFurnace) > 0 && tile instanceof TileEntityFurnace);
    }

    protected void sendEnergyTo(BlockPos pos, EnumFacing to)
    {
        TileEntity tile = getTileEntity().getWorld().getTileEntity(pos);
        if (tile instanceof IEnergyReceiver) {
            sendEnergyToReceiver((IEnergyReceiver) tile, to.getOpposite());
        } else if (getTileEntity().getUpgradeCount(ModItems.mUpgradeFurnace) > 0 && tile instanceof TileEntityFurnace) {
            sendEnergyToFurnace((TileEntityFurnace) tile);
        } else if(TeslaAPI.isTeslaConsumer(tile))
        {
        	sendEnergyToTeslaReceiver(tile, to.getOpposite());
        }
    }

    protected void sendEnergyToReceiver(IEnergyReceiver pEnergyReceiver, EnumFacing pFrom)
    {
        getTileEntity().getEnergyStorage().sendMaxTo(pEnergyReceiver, pFrom);
    }
    
    protected void sendEnergyToTeslaReceiver(TileEntity t, EnumFacing pFrom)
    {
    	if(TeslaAPI.isTeslaConsumer(t))
    	{
    		StatefulEnergyStorage storage = getTileEntity().getEnergyStorage();
        	storage.extractEnergy((int) TeslaAPI.givePowerToConsumer(t, storage.getMaxExtract(), false), false);
    	}
    }

    protected void sendEnergyToFurnace(TileEntityFurnace pFurnace)
    {
        final int FURNACE_COOKING_TICKS = 200;
        final int FURNACE_COOKING_ENERGY = FURNACE_COOKING_TICKS * ModConfiguration.getFurnaceUpgradeHeatingConsumption();
        
        if(mFurnaceEnergyBuffer < FURNACE_COOKING_ENERGY)
            mFurnaceEnergyBuffer += getTileEntity().getEnergyStorage().extractEnergy(FURNACE_COOKING_ENERGY - mFurnaceEnergyBuffer, false);
        
//        SolarPanelTileEntity solar = getTileEntity();
//        if(solar.getTier() > 0 && !(solar instanceof DraconicSolarPanelTileEntity))
//        {
//        	pFurnace.setField(2, pFurnace.getField(2) + 8);
//        }
        
        // Is there anything to smell?
        if(pFurnace.getStackInSlot(0) != null && pFurnace.getField(0) < FURNACE_COOKING_TICKS)
        {
            int burnTicksAvailable = mFurnaceEnergyBuffer / ModConfiguration.getFurnaceUpgradeHeatingConsumption();
            if(burnTicksAvailable >= FURNACE_COOKING_TICKS)
            {
                if(pFurnace.getField(0) == 0)
                {
                    // Add 1 as first tick is not counted in the burning process.
                    pFurnace.setField(0, pFurnace.getField(0) + 1);
                    BlockFurnace.setState(pFurnace.getField(0) > 0, pFurnace.getWorld(), pFurnace.getPos());
                }
                pFurnace.setField(0, pFurnace.getField(0) + FURNACE_COOKING_TICKS);
                mFurnaceEnergyBuffer -= FURNACE_COOKING_TICKS * ModConfiguration.getFurnaceUpgradeHeatingConsumption();
            }
        }
    }
}
