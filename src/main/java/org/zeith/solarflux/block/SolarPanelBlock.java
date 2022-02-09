package org.zeith.solarflux.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.solarflux.items.UpgradeItem;
import org.zeith.solarflux.panels.SolarPanel;

import java.util.List;
import java.util.stream.Stream;

public class SolarPanelBlock
		extends BaseEntityBlock
{
	public final SolarPanel panel;

	public SolarPanelBlock(SolarPanel panel)
	{
		super(Properties.of(Material.METAL).dynamicShape().requiresCorrectToolForDrops().strength(1.5F).noOcclusion().sound(SoundType.METAL));
		BlockHarvestAdapter.bindToolType(BlockHarvestAdapter.MineableType.PICKAXE, this);
		this.panel = panel;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
	{
		return BlockAPI.ticker();
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(stack.hasTag())
		{
			SolarPanelTile spt = null;
			BlockEntity tile = worldIn.getBlockEntity(pos);
			if(tile instanceof SolarPanelTile)
				spt = (SolarPanelTile) tile;
			else
			{
				spt = (SolarPanelTile) newBlockEntity(pos, state);
				worldIn.setBlockEntity(spt);
			}
			spt.loadFromItem(stack);
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		NonNullList<ItemStack> stacks = NonNullList.create();

		BlockEntity tileentity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
		if(tileentity instanceof SolarPanelTile)
		{
			SolarPanelTile te = (SolarPanelTile) tileentity;
			stacks.add(te.generateItem(panel));
		} else
			stacks.add(new ItemStack(panel));

		return stacks;
	}

	@Override
	public RenderShape getRenderShape(BlockState p_49232_)
	{
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		if(world.getBlockEntity(pos) instanceof SolarPanelTile spt) return spt.getShape(this);
		return Shapes.create(0, 0, 0, 1, panel.networkData.height, 1);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
		BlockEntity tile = worldIn.getBlockEntity(pos);
		SolarPanelTile spt = tile instanceof SolarPanelTile ? (SolarPanelTile) tile : null;
		if(spt != null)
			spt.resetVoxelShape();
	}

	public VoxelShape recalcShape(BlockGetter world, BlockPos pos)
	{
		VoxelShape baseShape = Shapes.create(0, 0, 0, 1, panel.networkData.height, 1);
		Stream.Builder<VoxelShape> shapes = Stream.builder();

		boolean west = false, east = false, north = false, south = false;

		float h = panel.getPanelData().height, h2 = h + 0.25F / 16F;

		if(west = world.getBlockState(pos.west()).getBlock() != this)
			shapes.add(Shapes.create(0, h, 1 / 16F, 1 / 16F, h2, 15 / 16F));

		if(east = world.getBlockState(pos.east()).getBlock() != this)
			shapes.add(Shapes.create(15 / 16F, h, 1 / 16F, 1, h2, 15 / 16F));

		if(north = world.getBlockState(pos.north()).getBlock() != this)
			shapes.add(Shapes.create(1 / 16F, h, 0, 15 / 16F, h2, 1 / 16F));

		if(south = world.getBlockState(pos.south()).getBlock() != this)
			shapes.add(Shapes.create(1 / 16F, h, 15 / 16F, 15 / 16F, h2, 1));

		if(west || north || world.getBlockState(pos.west().north()).getBlock() != this)
			shapes.add(Shapes.create(0, h, 0, 1 / 16F, h2, 1 / 16F));

		if(east || north || world.getBlockState(pos.east().north()).getBlock() != this)
			shapes.add(Shapes.create(15 / 16F, h, 0, 1, h2, 1 / 16F));

		if(south || east || world.getBlockState(pos.south().east()).getBlock() != this)
			shapes.add(Shapes.create(15 / 16F, h, 15 / 16F, 1, h2, 1));

		if(west || south || world.getBlockState(pos.west().south()).getBlock() != this)
			shapes.add(Shapes.create(0, h, 15 / 16F, 1 / 16F, h2, 1));

		return Shapes.or(baseShape, shapes.build().toArray(VoxelShape[]::new));
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
	{
		if(player instanceof ServerPlayer && worldIn.getBlockEntity(pos) instanceof SolarPanelTile tbs)
		{
			ItemStack held = player.getItemInHand(handIn);
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
						if(stack.sameItem(held) && ItemStack.tagMatches(stack, held))
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
						worldIn.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, .1F, 1F);
						return InteractionResult.SUCCESS;
					}
				}
			}
			ContainerAPI.openContainerTile(player, tbs);
		}
		return InteractionResult.SUCCESS;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
//	{
//		return adjacentBlockState.getBlock() == state.getBlock() && side != Direction.UP;
//	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState p_60457_)
	{
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level worldIn, BlockPos pos)
	{
		if(worldIn.getBlockEntity(pos) instanceof SolarPanelTile sp)
		{
			long cap = sp.capacity.getValueL();
			return cap > 0L ? (int) Math.round(15D * sp.energy / cap) : 0;
		}

		return 0;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		SolarPanelTile tile = new SolarPanelTile(pos, state);
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
	public boolean propagatesSkylightDown(BlockState p_49928_, BlockGetter p_49929_, BlockPos p_49930_)
	{
		return false;
	}
}