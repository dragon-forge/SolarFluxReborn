package org.zeith.solarflux.items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.items._base.UpgradeItem;

import javax.annotation.Nullable;

@SimplyRegister
public class ItemFurnaceUpgrade
		extends UpgradeItem
{
	@RegistryName("furnace_upgrade")
	public static final ItemFurnaceUpgrade FURNACE_UPGRADE = new ItemFurnaceUpgrade();
	
	public ItemFurnaceUpgrade()
	{
		super(1);
	}
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		TileEntity t = tile.level().getBlockEntity(tile.pos().below());
		if(t instanceof AbstractFurnaceTileEntity)
		{
			AbstractFurnaceTileEntity tf = (AbstractFurnaceTileEntity) t;
			AbstractCookingRecipe recipe = tf.getLevel().getRecipeManager().getRecipeFor(tf.recipeType, tf, tf.getLevel()).orElse(null);
			if(tf.litTime <= 1 && recipe != null && canSmelt(tf, recipe) && tile.energy() >= 1000)
			{
				int ct = recipe.getCookingTime();
				tf.litDuration = tf.litTime = ct;
				tile.energy(tile.energy() - (long) (1000 * (ct / 200F)));
			}
		}
	}
	
	public static boolean canSmelt(AbstractFurnaceTileEntity f, @Nullable IRecipe<?> recipeIn)
	{
		return f.canBurn(recipeIn);
	}
}