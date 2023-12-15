package org.zeith.solarflux.compat;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.*;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.core.adapter.RegistrationKernel;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.util.mcf.ScanDataHelper;
import org.zeith.solarflux.compat.twilightforest.ContentsSFTF;

import java.io.File;
import java.lang.annotation.ElementType;
import java.util.Collections;

public interface ISFCompat
{
	default void createRegistrationKernel(Class<?> clazz, String prefix)
	{
		FMLJavaModLoadingContext.get().getModEventBus().register(new RegistrationKernel(
				new ScanDataHelper.ModAwareAnnotationData(
						new ModFileScanData.AnnotationData(Type.getType(SimplyRegister.class), ElementType.TYPE, Type.getType(clazz), null, Collections.singletonMap("prefix", prefix)),
						null
				),
				(FMLModContainer) ModLoadingContext.get().getActiveContainer()
		));
	}
	
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
	interface IRecipeIndexer
	{
		void index(ResourceLocation... ids);
	}
}