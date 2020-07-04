package tk.zeitheron.solarflux.api;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.block.BlockBaseSolar;
import tk.zeitheron.solarflux.init.ItemsSF;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class SolarFluxAPI
{
	public static final CreativeTabs tab = new CreativeTabs(InfoSF.MOD_ID)
	{
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_2);
		}

		@Override
		public void displayAllRelevantItems(NonNullList<ItemStack> items)
		{
			NonNullList<ItemStack> sub = NonNullList.create();
			super.displayAllRelevantItems(sub);

			for(int i = 0; i < sub.size(); ++i)
			{
				Item it = sub.get(i).getItem();
				if(it instanceof ItemBlock)
				{
					ItemBlock ib = (ItemBlock) it;
					if(ib.getBlock() instanceof BlockBaseSolar)
					{
						BlockBaseSolar bs = (BlockBaseSolar) ib.getBlock();
						if(bs.solarInfo.getGeneration() <= 0)
						{
							sub.remove(i);
							--i;
						}
					}
				}
			}

			sub.sort((a, b) ->
			{
				if(a.getItem() instanceof ItemBlock && b.getItem() instanceof ItemBlock)
				{
					ItemBlock aib = (ItemBlock) a.getItem();
					ItemBlock bib = (ItemBlock) b.getItem();
					if(aib.getBlock() instanceof BlockBaseSolar && bib.getBlock() instanceof BlockBaseSolar)
					{
						BlockBaseSolar abs = (BlockBaseSolar) aib.getBlock();
						BlockBaseSolar bbs = (BlockBaseSolar) bib.getBlock();
						return (int) Math.max(Math.min(abs.solarInfo.getGeneration() - bbs.solarInfo.getGeneration(), Integer.MAX_VALUE), Integer.MIN_VALUE);
					}
				}

				return a.getItem().getRegistryName().toString().compareTo(b.getItem().getRegistryName().toString());
			});
			items.addAll(sub);
		}
	};

	public static Consumer<Item> registerItem =  item ->
	{
		item.setTranslationKey(item.getRegistryName().toString());
		ForgeRegistries.ITEMS.register(item);
		SolarFluxAPI.renderRenderer.accept(item);
		item.setCreativeTab(SolarFluxAPI.tab);
	};

	public static IForgeRegistry<SolarInfo> SOLAR_PANELS = null;
	public static Consumer<Item> renderRenderer = null;
	
	public static final Set<String> resourceDomains = new HashSet<>();
	static
	{
		resourceDomains.add("solarflux");
	}
}