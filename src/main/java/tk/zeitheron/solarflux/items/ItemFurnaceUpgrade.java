package tk.zeitheron.solarflux.items;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.block.SolarPanelTile;

public class ItemFurnaceUpgrade extends UpgradeItem
{
	public ItemFurnaceUpgrade()
	{
		super(1);
		setRegistryName(InfoSF.MOD_ID, "furnace_upgrade");
	}
	
	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		TileEntity t = tile.getWorld().getTileEntity(tile.getPos().down());
		if(t instanceof AbstractFurnaceTileEntity)
		{
			AbstractFurnaceTileEntity tf = (AbstractFurnaceTileEntity) t;
			
			AbstractCookingRecipe irecipe = tf.getWorld().getRecipeManager().getRecipe((IRecipeType<AbstractCookingRecipe>) tf.recipeType, tf, tf.getWorld()).orElse(null);
			
			if(tf.burnTime < 1 && canSmelt(tf, irecipe) && tile.energy >= 1000)
			{
				tf.recipesUsed = tf.burnTime = irecipe.getCookTime() + 1;
				tile.energy -= 1000;
			}
		}
	}
	
	public static boolean canSmelt(AbstractFurnaceTileEntity f, @Nullable IRecipe<?> recipeIn)
	{
		if(!f.items.get(0).isEmpty() && recipeIn != null)
		{
			ItemStack itemstack = recipeIn.getRecipeOutput();
			if(itemstack.isEmpty())
			{
				return false;
			} else
			{
				ItemStack itemstack1 = f.items.get(2);
				if(itemstack1.isEmpty())
				{
					return true;
				} else if(!itemstack1.isItemEqual(itemstack))
				{
					return false;
				} else if(itemstack1.getCount() + itemstack.getCount() <= f.getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize())
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