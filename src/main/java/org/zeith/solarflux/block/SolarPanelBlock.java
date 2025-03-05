package org.zeith.solarflux.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.forge.*;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.solarflux.items.upgrades._base.UpgradeItem;
import org.zeith.solarflux.panels.SolarPanel;

import java.util.List;
import java.util.stream.Stream;

public class SolarPanelBlock
		extends BaseEntityBlock
		implements ICustomBlockItem
{
	public final SolarPanel panel;
	
	public final MapCodec<SolarPanelBlock> CODEC;
	
	public SolarPanelBlock(SolarPanel panel, Properties properties)
	{
		super(properties);
		bindTool();
		this.panel = panel;
		this.CODEC = simpleCodec(props -> new SolarPanelBlock(panel, props));;
	}
	
	protected void bindTool()
	{
		BlockHarvestAdapter.bindTool(BlockHarvestAdapter.MineableType.PICKAXE, ToolMaterial.IRON, this);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return BlockAPI.ticker();
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
	{
		NonNullList<ItemStack> stacks = NonNullList.create();
		
		BlockEntity tileentity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
		if(tileentity instanceof SolarPanelTile te)
			stacks.add(te.generateItem(panel));
		else
			stacks.add(new ItemStack(panel));
		
		return stacks;
	}
	
	@Override
	protected MapCodec<? extends BaseEntityBlock> codec()
	{
		return CODEC;
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
		return Shapes.create(0, 0, 0, 1, panel.getPanelData().height, 1);
	}
	
	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston)
	{
		super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
		if(level.getBlockEntity(pos) instanceof SolarPanelTile spt)
			spt.resetVoxelShape();
	}
	
	public VoxelShape recalcShape(BlockGetter world, BlockPos pos)
	{
		var pd = panel.getPanelData();
		var ph = pd.height;
		
		VoxelShape baseShape = Shapes.create(0, 0, 0, 1, ph, 1);
		Stream.Builder<VoxelShape> shapes = Stream.builder();
		
		boolean west = false, east = false, north = false, south = false;
		
		float h = ph, h2 = h + 0.25F / 16F;
		
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
	protected InteractionResult useItemOn(ItemStack held, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand p_316595_, BlockHitResult hit)
	{
		if(player instanceof ServerPlayer && worldIn.getBlockEntity(pos) instanceof SolarPanelTile tbs && !held.isEmpty() && held.getItem() instanceof UpgradeItem iu)
		{
			int amt = tbs.getUpgrades(iu);
			if(amt < iu.getMaxUpgradesInstalled(tbs) && iu.canInstall(tbs, held, tbs.upgradeInventory))
			{
				int installed = 0;
				for(int i = 0; i < tbs.upgradeInventory.getSlots(); ++i)
				{
					ItemStack stack = tbs.upgradeInventory.getStackInSlot(i);
					if(ItemStack.isSameItemSameComponents(stack, held))
					{
						int allow = Math.min(iu.getMaxUpgradesInstalled(tbs) - tbs.getUpgrades(iu), Math.min(iu.getMaxStackSize(stack) - stack.getCount(), held.getCount()));
						stack.grow(allow);
						held.shrink(allow);
						installed += allow;
						break;
					} else if(stack.isEmpty())
					{
						int allow = Math.min(iu.getMaxUpgradesInstalled(tbs) - tbs.getUpgrades(iu), held.getCount());
						ItemStack copy = held.copy();
						held.shrink(allow);
						copy.setCount(allow);
						tbs.upgradeInventory.setStackInSlot(i, copy);
						installed += allow;
						break;
					}
				}
				
				if(installed > 0)
				{
					iu.onInstalled(tbs, amt, tbs.getUpgrades(iu));
					worldIn.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, .1F, 1F);
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}
	
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit)
	{
		if(player instanceof ServerPlayer && worldIn.getBlockEntity(pos) instanceof SolarPanelTile tbs)
			ContainerAPI.openContainerTile(player, tbs);
		return InteractionResult.SUCCESS;
	}
	
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
	protected boolean propagatesSkylightDown(BlockState state)
	{
		return false;
	}
	
	@Override
	public BlockItem createBlockItem(ResourceKey<Item> id)
	{
		return new SolarPanelBlockItem(this, new Item.Properties());
	}
}