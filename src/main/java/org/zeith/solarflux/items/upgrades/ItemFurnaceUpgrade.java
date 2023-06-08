package org.zeith.solarflux.items.upgrades;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.items.upgrades._base.UpgradeItem;
import org.zeith.solarflux.mixins.AbstractFurnaceBlockEntityAccessor;
import org.zeith.solarflux.util.BlockPosFace;

import javax.annotation.Nullable;

public class ItemFurnaceUpgrade
		extends UpgradeItem
{
	public ItemFurnaceUpgrade()
	{
		super(1);
	}
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		Level lvl = tile.level();
		BlockPos pos = tile.pos().below();
		
		updateFurnaceAt(tile, lvl, pos);
		
		for(BlockPosFace face : tile.traversal())
			if(face.face == Direction.UP)
				updateFurnaceAt(tile, lvl, face.pos);
	}
	
	public void updateFurnaceAt(ISolarPanelTile solar, Level lvl, BlockPos pos)
	{
		if(lvl.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity tf && tf instanceof AbstractFurnaceBlockEntityAccessor a)
		{
			AbstractCookingRecipe irecipe = tf.getLevel().getRecipeManager().getRecipeFor(a.getRecipeType(), tf, tf.getLevel()).orElse(null);
			
			if(tf.litTime <= 1 && irecipe != null && canSmelt(tf, irecipe) && solar.energy() >= 1000)
			{
				tf.litTime += 200;
				tf.litDuration = 200;
				solar.energy(solar.energy() - 1000L);
				
				if(!lvl.getBlockState(pos).getValue(AbstractFurnaceBlock.LIT))
				{
					BlockState state = lvl.getBlockState(pos).setValue(AbstractFurnaceBlock.LIT, true);
					lvl.setBlock(pos, state, 3);
					
					lvl.blockEntityChanged(pos);
					if(!state.isAir())
						lvl.updateNeighbourForOutputSignal(pos, state.getBlock());
				}
			}
		}
	}
	
	public static boolean canSmelt(AbstractFurnaceBlockEntity f, @Nullable Recipe<?> recipe)
	{
		if(!f.getItem(0).isEmpty() && recipe != null)
		{
			ItemStack result = recipe.getResultItem(f.getLevel().registryAccess());
			if(result.isEmpty()) return false;
			else
			{
				ItemStack curResIt = f.getItem(2);
				if(curResIt.isEmpty())
				{
					return true;
				} else if(!ItemStack.isSameItem(curResIt, result))
				{
					return false;
				} else if(curResIt.getCount() + result.getCount() <= f.getMaxStackSize() && curResIt.getCount() + result.getCount() <= curResIt.getMaxStackSize())
				{
					return true;
				} else
				{
					return curResIt.getCount() + result.getCount() <= result.getMaxStackSize();
				}
			}
		} else
		{
			return false;
		}
	}
}