package org.zeith.solarflux.client;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.util.shaded.json.JSONObject;
import org.zeith.solarflux.block.SolarPanelBlock;
import org.zeith.solarflux.init.ItemsSF;
import org.zeith.solarflux.init.SolarPanelsSF;

import java.io.*;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class SolarFluxResourcePack
		implements PackResources
{
	public final Map<ResourceLocation, IResourceStreamSupplier> resourceMap = new HashMap<>();
	
	private static IResourceStreamSupplier ofText(String text)
	{
		return IResourceStreamSupplier.create(() -> true, () -> new ByteArrayInputStream(text.getBytes()));
	}
	
	private static IResourceStreamSupplier ofFile(File file)
	{
		return IResourceStreamSupplier.create(file::isFile, () -> new FileInputStream(file));
	}
	
	static SolarFluxResourcePack packInstance;
	
	public static SolarFluxResourcePack getPackInstance()
	{
		if(packInstance == null)
			packInstance = new SolarFluxResourcePack();
		return packInstance;
	}
	
	private boolean hasInit = false;
	
	public void init()
	{
		if(hasInit) return;
		hasInit = true;
		
		resourceMap.clear();
		
		ItemsSF.JS_MATERIALS.forEach(i ->
		{
			ResourceLocation reg = i.getRegistryName();
			
			ResourceLocation models_item = new ResourceLocation(reg.getNamespace(), "models/item/" + reg.getPath() + ".json");
			
			resourceMap.put(models_item, ofText("{\"parent\":\"item/generated\",\"textures\":{\"layer0\":\"" + reg.getNamespace() + ":items/materials/" + reg.getPath() + "\"}}"));
			
			File textures = new File(SolarPanelsSF.CONFIG_DIR, "textures");
			File items = new File(textures, "items");
			ResourceLocation textures_items = new ResourceLocation(reg.getNamespace(), "textures/items/materials/" + reg.getPath() + ".png");
			{
				resourceMap.put(textures_items, ofFile(new File(items, reg.getPath() + ".png")));
			}
		});
		
		SolarPanelsSF.listPanels().forEach(si ->
		{
			SolarPanelBlock blk = si.getBlock();
			ResourceLocation reg = ForgeRegistries.BLOCKS.getKey(blk);
			
			ResourceLocation blockstate = new ResourceLocation(reg.getNamespace(), "blockstates/" + reg.getPath() + ".json");
			ResourceLocation models_block = new ResourceLocation(reg.getNamespace(), "models/block/" + reg.getPath() + ".json");
			ResourceLocation models_item = new ResourceLocation(reg.getNamespace(), "models/item/" + reg.getPath() + ".json");
			
			float thicc = si.getPanelData().height * 16F;
			float thic2 = thicc + 0.25F;
			float reverseThicc = 16 - thicc;
			
			resourceMap.put(blockstate, ofText("{\"variants\":{\"\":{\"model\":\"" + reg.getNamespace() + ":block/" + reg.getPath() + "\"}}}"));
			resourceMap.put(models_item, ofText("{\"parent\":\"" + reg.getNamespace() + ":block/" + reg.getPath() + "\"}"));
			
			// Block Model
			var blockModel = new JSONObject();
			blockModel.put("loader", "solarflux:solar_panel");
			blockModel.put("panel", reg.toString());
			resourceMap.put(models_block, ofText(blockModel.toString()));
			
			if(si.isCustom)
			{
				File textures = new File(SolarPanelsSF.CONFIG_DIR, "textures");
				File blocks = new File(textures, "blocks");
				ResourceLocation textures_blocks_base = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_base.png");
				ResourceLocation textures_blocks_top = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_top.png");
				ResourceLocation textures_blocks_base_mcmeta = new ResourceLocation(reg.getNamespace(), "textures/blocks/solar_base_" + reg.getPath() + "_base.png.mcmeta");
				ResourceLocation textures_blocks_top_mcmeta = new ResourceLocation(reg.getNamespace(), "textures/blocks/solar_top_" + reg.getPath() + "_top.png.mcmeta");
				{
					String n = reg.getPath().startsWith("sp_custom_") ? reg.getPath().substring(10) : reg.getPath().substring(3);
					resourceMap.put(textures_blocks_base, ofFile(new File(blocks, n + "_base.png")));
					resourceMap.put(textures_blocks_base_mcmeta, ofFile(new File(blocks, n + "_base.mcmeta")));
					resourceMap.put(textures_blocks_top, ofFile(new File(blocks, n + "_top.png")));
					resourceMap.put(textures_blocks_top_mcmeta, ofFile(new File(blocks, n + "_top.mcmeta")));
				}
			}
		});
	}
	
	@Override
	public void close()
	{
	}
	
	@Override
	public InputStream getRootResource(String fileName) throws IOException
	{
		throw new FileNotFoundException(fileName);
	}
	
	@Override
	public boolean isHidden()
	{
		return true;
	}
	
	@Override
	public InputStream getResource(PackType type, ResourceLocation location) throws IOException
	{
		try
		{
			init();
			InputStream in = resourceMap.get(location).create();
			return in;
		} catch(RuntimeException e)
		{
			if(e.getCause() instanceof IOException)
				throw (IOException) e.getCause();
			throw e;
		}
	}
	
	@Override
	public Collection<ResourceLocation> getResources(PackType p_215339_, String p_215340_, String p_215341_, Predicate<ResourceLocation> p_215342_)
	{
		return Collections.emptyList();
	}
	
	@Override
	public boolean hasResource(PackType type, ResourceLocation location)
	{
		init();
		IResourceStreamSupplier s;
		return (s = resourceMap.get(location)) != null && s.exists();
	}
	
	@Override
	public Set<String> getNamespaces(PackType type)
	{
		init();
		return Collections.singleton("solarflux");
	}
	
	@Override
	public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException
	{
		if(deserializer.getMetadataSectionName().equals("pack"))
		{
			JsonObject obj = new JsonObject();
			obj.addProperty("pack_format", 8);
			obj.addProperty("description", "Generated resources for SolarFlux");
			return deserializer.fromJson(obj);
		}
		return null;
	}
	
	@Override
	public String getName()
	{
		return "Solar Flux Generated Resources";
	}
	
	public interface IResourceStreamSupplier
	{
		static IResourceStreamSupplier create(BooleanSupplier exists, IIOSupplier<InputStream> streamable)
		{
			return new IResourceStreamSupplier()
			{
				@Override
				public boolean exists()
				{
					return exists.getAsBoolean();
				}
				
				@Override
				public InputStream create() throws IOException
				{
					return streamable.get();
				}
			};
		}
		
		boolean exists();
		
		InputStream create() throws IOException;
	}
	
	@FunctionalInterface
	public interface IIOSupplier<T>
	{
		T get() throws IOException;
	}
}