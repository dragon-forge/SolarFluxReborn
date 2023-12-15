package org.zeith.solarflux.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.items._base.UpgradeItem;
import org.zeith.solarflux.panels.SolarPanel;

import java.util.ArrayList;
import java.util.List;

public class SolarPanelBlock
		extends ContainerBlock
		implements ICustomBlockItem
{
	public final SolarPanel panel;
	
	public final ResourceLocation identifier;
	
	public SolarPanelBlock(ResourceLocation identifier, SolarPanel panel)
	{
		super(Properties.of(Material.METAL).noOcclusion().harvestLevel(1).harvestTool(ToolType.PICKAXE).strength(1.5F).sound(SoundType.METAL));
		this.identifier = identifier;
		this.panel = panel;
	}
	
	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(stack.hasTag())
		{
			SolarPanelTile spt = null;
			TileEntity tile = worldIn.getBlockEntity(pos);
			if(tile instanceof SolarPanelTile)
				spt = (SolarPanelTile) tile;
			else
			{
				spt = (SolarPanelTile) newBlockEntity(worldIn);
				worldIn.setBlockEntity(pos, spt);
			}
			spt.loadFromItem(stack);
		}
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		NonNullList<ItemStack> stacks = NonNullList.create();
		
		TileEntity tileentity = builder.getParameter(LootParameters.BLOCK_ENTITY);
		if(tileentity instanceof SolarPanelTile)
		{
			SolarPanelTile te = (SolarPanelTile) tileentity;
			stacks.add(te.generateItem(panel));
		} else
			stacks.add(new ItemStack(panel));
		
		return stacks;
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState p_149645_1_)
	{
		return BlockRenderType.MODEL;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		TileEntity tile = world.getBlockEntity(pos);
		SolarPanelTile spt = tile instanceof SolarPanelTile ? (SolarPanelTile) tile : null;
		if(spt != null)
			return spt.getShape(this);
		return VoxelShapes.box(0, 0, 0, 1, panel.networkData.height, 1);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
		TileEntity tile = worldIn.getBlockEntity(pos);
		SolarPanelTile spt = tile instanceof SolarPanelTile ? (SolarPanelTile) tile : null;
		if(spt != null)
			spt.resetVoxelShape();
	}
	
	public VoxelShape recalcShape(IBlockReader world, BlockPos pos)
	{
		VoxelShape baseShape = VoxelShapes.box(0, 0, 0, 1, panel.networkData.height, 1);
		List<VoxelShape> shapes = new ArrayList<>(8);
		
		boolean west = false, east = false, north = false, south = false;
		
		float h = panel.getPanelData().height, h2 = h + 0.25F / 16F;
		
		if(west = world.getBlockState(pos.west()).getBlock() != this)
			shapes.add(VoxelShapes.box(0, h, 1 / 16F, 1 / 16F, h2, 15 / 16F));
		
		if(east = world.getBlockState(pos.east()).getBlock() != this)
			shapes.add(VoxelShapes.box(15 / 16F, h, 1 / 16F, 1, h2, 15 / 16F));
		
		if(north = world.getBlockState(pos.north()).getBlock() != this)
			shapes.add(VoxelShapes.box(1 / 16F, h, 0, 15 / 16F, h2, 1 / 16F));
		
		if(south = world.getBlockState(pos.south()).getBlock() != this)
			shapes.add(VoxelShapes.box(1 / 16F, h, 15 / 16F, 15 / 16F, h2, 1));
		
		if(west || north || world.getBlockState(pos.west().north()).getBlock() != this)
			shapes.add(VoxelShapes.box(0, h, 0, 1 / 16F, h2, 1 / 16F));
		
		if(east || north || world.getBlockState(pos.east().north()).getBlock() != this)
			shapes.add(VoxelShapes.box(15 / 16F, h, 0, 1, h2, 1 / 16F));
		
		if(south || east || world.getBlockState(pos.south().east()).getBlock() != this)
			shapes.add(VoxelShapes.box(15 / 16F, h, 15 / 16F, 1, h2, 1));
		
		if(west || south || world.getBlockState(pos.west().south()).getBlock() != this)
			shapes.add(VoxelShapes.box(0, h, 15 / 16F, 1 / 16F, h2, 1));
		
		return VoxelShapes.or(baseShape, shapes.toArray(new VoxelShape[0]));
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		TileEntity te = worldIn.getBlockEntity(pos);
		SolarPanelTile tbs = te instanceof SolarPanelTile ? (SolarPanelTile) te : null;
		if(player instanceof ServerPlayerEntity && tbs != null)
		{
			ItemStack held = player.getItemInHand(handIn);
			if(!held.isEmpty() && held.getItem() instanceof UpgradeItem)
			{
				int amt = tbs.getUpgrades(held.getItem());
				UpgradeItem iu = (UpgradeItem) held.getItem();
				if(amt < iu.getMaxUpgradesInstalled(tbs) && iu.canInstall(tbs, held, tbs.upgradeInventory))
				{
					int installed = 0;
					for(int i = 0; i < tbs.upgradeInventory.getSlots(); ++i)
					{
						ItemStack stack = tbs.upgradeInventory.getStackInSlot(i);
						if(stack.sameItem(held) && ItemStack.tagMatches(stack, held))
						{
							int allow = Math.min(iu.getMaxUpgradesInstalled(tbs) - tbs.getUpgrades(iu), Math.min(iu.getItemStackLimit(stack) - stack.getCount(), held.getCount()));
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
						worldIn.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundCategory.BLOCKS, .1F, 1F);
						return ActionResultType.SUCCESS;
					}
				}
			}
			ContainerAPI.openContainerTile(player, tbs);
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		return adjacentBlockState.getBlock() == state.getBlock() && side != Direction.UP;
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state)
	{
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos)
	{
		TileEntity tile = worldIn.getBlockEntity(pos);
		if(tile instanceof SolarPanelTile)
		{
			SolarPanelTile sp = (SolarPanelTile) tile;
			long cap = sp.capacity.getValueL();
			return cap > 0L ? (int) Math.round(15D * sp.energy / cap) : 0;
		}
		return 0;
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn)
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
	
	@Override
	public BlockItem createBlockItem()
	{
		return new SolarPanelBlockItem(this, new Item.Properties().tab(SolarFlux.ITEM_GROUP));
	}
}