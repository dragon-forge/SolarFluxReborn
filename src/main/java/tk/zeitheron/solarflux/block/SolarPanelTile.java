package tk.zeitheron.solarflux.block;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import tk.zeitheron.solarflux.panels.SolarPanels;

public class SolarPanelTile extends TileEntity implements ITickableTileEntity
{
	public SolarPanelTile()
	{
		super(SolarPanels.SOLAR_PANEL_TYPE);
	}
	
	@Override
	public void tick()
	{
		if(voxelTimer > 0)
			--voxelTimer;
	}
	
	public static final ModelProperty<World> WORLD_PROP = new ModelProperty<World>();
	public static final ModelProperty<BlockPos> POS_PROP = new ModelProperty<BlockPos>();
	
	@Override
	public IModelData getModelData()
	{
		return new ModelDataMap.Builder().withInitial(WORLD_PROP, world).withInitial(POS_PROP, pos).build();
	}
	
	private void writeNBT(CompoundNBT nbt)
	{
		
	}
	
	private void readNBT(CompoundNBT nbt)
	{
		
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound)
	{
		CompoundNBT panel;
		writeNBT(panel = new CompoundNBT());
		compound.put("panel", panel);
		return super.write(compound);
	}
	
	@Override
	public void read(CompoundNBT compound)
	{
		readNBT(compound.getCompound("panel"));
		super.read(compound);
	}
	
	int voxelTimer = 0;
	VoxelShape shape;
	
	public void resetVoxelShape()
	{
		shape = null;
	}
	
	public VoxelShape getShape(SolarPanelBlock block)
	{
		if(shape == null || voxelTimer <= 0)
		{
			shape = block.recalcShape(world, pos);
			voxelTimer = 20;
		}
		return shape;
	}
}