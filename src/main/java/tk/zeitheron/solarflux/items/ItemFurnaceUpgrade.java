package tk.zeitheron.solarflux.items;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntity;

public class ItemFurnaceUpgrade extends ItemUpgrade
{
	public ItemFurnaceUpgrade()
	{
		setRegistryName(InfoSF.MOD_ID, "furnace_upgrade");
	}
	
	@Override
	public int getMaxUpgrades()
	{
		return 1;
	}
	
	@Override
	public void update(TileBaseSolar tile, ItemStack stack, int amount)
	{
		TileEntity t = tile.getWorld().getTileEntity(tile.getPos().down());
		if(t instanceof TileEntityFurnace)
		{
			TileEntityFurnace tf = (TileEntityFurnace) t;
			if(tf.getField(0) < 1 && tf.canSmelt() && tile.energy >= 1000)
			{
				tf.setField(0, 20_1);
				tf.setField(1, 20_1);
				tile.energy -= 1000;
			}
			if(tf.getField(2) > 0)
			{
				System.out.println(tf.getField(2));
//				int maxOverclock = Math.min(20, (int) Math.round(Math.cbrt(tile.energy / 10)));
//				for(int i = 0; i < maxOverclock && tile.energy >= 100; ++i)
//				{
//					tile.energy -= 100;
//					tf.update();
//					if(tf.getField(0) < 1 && tf.canSmelt() && tile.energy >= 1000)
//					{
//						tf.setField(0, 20_0);
//						tf.setField(1, 20_0);
//						tile.energy -= 1000;
//					}
//				}
			}
		}
	}
}