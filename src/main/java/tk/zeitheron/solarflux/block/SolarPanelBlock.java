package tk.zeitheron.solarflux.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import tk.zeitheron.solarflux.items.UpgradeItem;
import tk.zeitheron.solarflux.panels.SolarPanel;

import java.util.ArrayList;
import java.util.List;

public class SolarPanelBlock
		extends ContainerBlock
{
	public final SolarPanel panel;

	public SolarPanelBlock(SolarPanel panel)
	{
		super(Properties.create(Material.IRON).notSolid().harvestLevel(1).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.5F).variableOpacity().sound(SoundType.METAL));
		this.panel = panel;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(stack.hasTag())
		{
			SolarPanelTile spt = null;
			TileEntity tile = worldIn.getTileEntity(pos);
			if(tile instanceof SolarPanelTile)
				spt = (SolarPanelTile) tile;
			else
			{
				spt = (SolarPanelTile) createNewTileEntity(worldIn);
				worldIn.setTileEntity(pos, spt);
			}
			spt.loadFromItem(stack);
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		NonNullList<ItemStack> stacks = NonNullList.create();

		TileEntity tileentity = builder.get(LootParameters.BLOCK_ENTITY);
		if(tileentity instanceof SolarPanelTile)
		{
			SolarPanelTile te = (SolarPanelTile) tileentity;
			stacks.add(te.generateItem(panel));
		} else
			stacks.add(new ItemStack(panel));

		return stacks;
	}

	@Override
	public BlockRenderType getRenderType(BlockState p_149645_1_)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		TileEntity tile = world.getTileEntity(pos);
		SolarPanelTile spt = tile instanceof SolarPanelTile ? (SolarPanelTile) tile : null;
		if(spt != null)
			return spt.getShape(this);
		return VoxelShapes.create(0, 0, 0, 1, panel.networkData.height, 1);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
		TileEntity tile = worldIn.getTileEntity(pos);
		SolarPanelTile spt = tile instanceof SolarPanelTile ? (SolarPanelTile) tile : null;
		if(spt != null)
			spt.resetVoxelShape();
	}

	public VoxelShape recalcShape(IBlockReader world, BlockPos pos)
	{
		VoxelShape baseShape = VoxelShapes.create(0, 0, 0, 1, panel.networkData.height, 1);
		List<VoxelShape> shapes = new ArrayList<>(8);

		boolean west = false, east = false, north = false, south = false;

		float h = panel.getPanelData().height, h2 = h + 0.25F / 16F;

		if(west = world.getBlockState(pos.west()).getBlock() != this)
			shapes.add(VoxelShapes.create(0, h, 1 / 16F, 1 / 16F, h2, 15 / 16F));

		if(east = world.getBlockState(pos.east()).getBlock() != this)
			shapes.add(VoxelShapes.create(15 / 16F, h, 1 / 16F, 1, h2, 15 / 16F));

		if(north = world.getBlockState(pos.north()).getBlock() != this)
			shapes.add(VoxelShapes.create(1 / 16F, h, 0, 15 / 16F, h2, 1 / 16F));

		if(south = world.getBlockState(pos.south()).getBlock() != this)
			shapes.add(VoxelShapes.create(1 / 16F, h, 15 / 16F, 15 / 16F, h2, 1));

		if(west || north || world.getBlockState(pos.west().north()).getBlock() != this)
			shapes.add(VoxelShapes.create(0, h, 0, 1 / 16F, h2, 1 / 16F));

		if(east || north || world.getBlockState(pos.east().north()).getBlock() != this)
			shapes.add(VoxelShapes.create(15 / 16F, h, 0, 1, h2, 1 / 16F));

		if(south || east || world.getBlockState(pos.south().east()).getBlock() != this)
			shapes.add(VoxelShapes.create(15 / 16F, h, 15 / 16F, 1, h2, 1));

		if(west || south || world.getBlockState(pos.west().south()).getBlock() != this)
			shapes.add(VoxelShapes.create(0, h, 15 / 16F, 1 / 16F, h2, 1));

		return VoxelShapes.or(baseShape, shapes.toArray(new VoxelShape[shapes.size()]));
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		SolarPanelTile tbs = te instanceof SolarPanelTile ? (SolarPanelTile) te : null;
		if(player instanceof ServerPlayerEntity && tbs != null)
		{
			ItemStack held = player.getHeldItem(handIn);
			if(!held.isEmpty() && held.getItem() instanceof UpgradeItem)
			{
				int amt = tbs.getUpgrades(held.getItem());
				UpgradeItem iu = (UpgradeItem) held.getItem();
				if(amt < held.getMaxStackSize() && iu.canInstall(tbs, held, tbs.upgradeInventory))
				{
					boolean installed = false;
					for(int i = 0; i < tbs.upgradeInventory.getSlots(); ++i)
					{
						ItemStack stack = tbs.upgradeInventory.getStackInSlot(i);
						if(stack.isItemEqual(held) && ItemStack.areItemStackTagsEqual(stack, held))
						{
							int allow = Math.min(held.getMaxStackSize() - tbs.getUpgrades(iu), Math.min(iu.getItemStackLimit(stack) - stack.getCount(), held.getCount()));
							stack.grow(allow);
							held.shrink(allow);
							installed = true;
							break;
						} else if(stack.isEmpty())
						{
							int allow = Math.min(held.getMaxStackSize() - tbs.getUpgrades(iu), held.getCount());
							ItemStack copy = held.copy();
							held.shrink(allow);
							copy.setCount(allow);
							tbs.upgradeInventory.setStackInSlot(i, copy);
							installed = true;
							break;
						}
					}
					if(installed)
					{
						worldIn.playSound(null, pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, .1F, 1F);
						return ActionResultType.SUCCESS;
					}
				}
			}
			NetworkHooks.openGui((ServerPlayerEntity) player, tbs, buf -> buf.writeBlockPos(pos));
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		return adjacentBlockState.getBlock() == state.getBlock() && side != Direction.UP;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof SolarPanelTile)
		{
			SolarPanelTile sp = (SolarPanelTile) tile;
			long cap = sp.capacity.getValueL();
			return cap > 0L ? (int) Math.round(15D * sp.energy / cap) : 0;
		}
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn)
	{
		SolarPanelTile tile = new SolarPanelTile();
		tile.setDelegate(panel);
		return tile;
	}

//	@Override
//	public FluidState getFluidState(BlockState state)
//	{
//		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
//	}
//
//	@Override
//	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
//	{
//		if(stateIn.get(WATERLOGGED))
//		{
//			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
//		}
//
//		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
//	}
//
//	@Override
//	public BlockState getStateForPlacement(BlockItemUseContext context)
//	{
//		FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
//		return getDefaultState().with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
//	}
//
//	@Override
//	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
//	{
//		builder.add(WATERLOGGED);
//	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
	{
		return true;
	}
}