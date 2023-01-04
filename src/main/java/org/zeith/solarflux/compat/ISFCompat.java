package org.zeith.solarflux.compat;

import net.minecraft.util.ResourceLocation;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;

import java.io.File;

public interface ISFCompat
{
	default void construct()
	{
	}
	
	default void setupConfigFile(File file)
	{
	}
	
	default void indexRecipes(IRecipeIndexer indexer)
	{
	}
	
	void registerPanels();
	
	void reloadRecipes(RegisterRecipesEvent e);
	
	@FunctionalInterface
	public interface IRecipeIndexer
	{
		void index(ResourceLocation... ids);
	}
}