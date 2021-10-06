package org.zeith.solarflux.compat;

import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;

import java.io.File;

public interface ISFCompat
{
	default void setupConfigFile(File file)
	{
	}

	void registerPanels();

	void reloadRecipes(RegisterRecipesEvent e);
}