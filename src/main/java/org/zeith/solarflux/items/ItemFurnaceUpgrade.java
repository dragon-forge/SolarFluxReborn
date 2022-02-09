package org.zeith.solarflux.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.solarflux.InfoSF;
import org.zeith.solarflux.block.SolarPanelTile;

import javax.annotation.Nullable;

public class ItemFurnaceUpgrade
		extends UpgradeItem
{
	public ItemFurnaceUpgrade()
	{
		super(1);
		setRegistryName(InfoSF.MOD_ID, "furnace_upgrade");
	}

	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		Level lvl = tile.getLevel();
		BlockPos pos = tile.getBlockPos().below();

		if(lvl.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity tf)
		{
			AbstractCookingRecipe irecipe = tf.getLevel().getRecipeManager().getRecipeFor(tf.recipeType, tf, tf.getLevel()).orElse(null);

			if(tf.litTime <= 1 && irecipe != null && canSmelt(tf, irecipe) && tile.energy >= 1000)
			{
				tf.litTime += 200;
				tf.litDuration = 200;
				tile.energy -= 1000;

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

	public static boolean canSmelt(AbstractFurnaceBlockEntity f, @Nullable Recipe<?> recipeIn)
	{
		if(!f.getItem(0).isEmpty() && recipeIn != null)
		{
			ItemStack itemstack = recipeIn.getResultItem();
			if(itemstack.isEmpty())
			{
				return false;
			} else
			{
				ItemStack itemstack1 = f.getItem(2);
				if(itemstack1.isEmpty())
				{
					return true;
				} else if(!itemstack1.sameItem(itemstack))
				{
					return false;
				} else if(itemstack1.getCount() + itemstack.getCount() <= f.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize())
				{
					return true;
				} else
				{
					return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
				}
			}
		} else
		{
			return false;
		}
	}
}