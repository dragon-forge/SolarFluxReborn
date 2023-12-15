package org.zeith.solarflux.compat.ae2;

import appeng.core.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.compat.*;
import org.zeith.solarflux.compat.ae2.tile.IAE2SolarPanelTile;
import org.zeith.solarflux.items.ItemsSF;
import org.zeith.solarflux.panels.SolarPanels;

import java.util.*;

@SFCompat("appliedenergistics2")
public class AE2Compat
		implements ISFCompat
{
	public final ResourceLocation aeuEnergyUpgrade = new ResourceLocation(SolarFlux.MOD_ID, "ae2/energy_upgrade");
	
	public AE2Compat()
	{
		IEventBus mcf = MinecraftForge.EVENT_BUS;
		
		mcf.addListener(this::onWorldTick);
		mcf.addListener(this::onUnloadChunk);
		mcf.addListener(this::onLoadWorld);
		mcf.addListener(this::onUnloadWorld);
		mcf.addListener(this::shutdown);
		
		createRegistrationKernel(ContentsSFAE2.class, "ae2/");
	}
	
	@Override
	public void registerPanels()
	{
	}
	
	@Override
	public void indexRecipes(IRecipeIndexer indexer)
	{
		indexer.index(aeuEnergyUpgrade);
	}
	
	@Override
	public void reloadRecipes(RegisterRecipesEvent e)
	{
		e.shapeless()
				.id(aeuEnergyUpgrade)
				.add(Api.instance().definitions().blocks().energyAcceptor().asItem())
				.add(ItemsSF.BLANK_UPGRADE)
				.result(ContentsSFAE2.ENERGY_UPGRADE)
				.registerIf(SolarPanels::isRecipeActive);
	}
	
	public void onWorldTick(TickEvent.WorldTickEvent ev)
	{
		World world = ev.world;
		if(!world.isClientSide && ev.side == LogicalSide.SERVER)
		{
			if(ev.phase == TickEvent.Phase.END)
			{
				this.readyTiles(world);
			}
		}
	}
	
	public void shutdown(FMLServerStoppingEvent e)
	{
		tiles.clear();
	}
	
	public void onUnloadChunk(ChunkEvent.Unload ev)
	{
		if(!ev.getWorld().isClientSide())
		{
			tiles.removeWorldChunk(ev.getWorld(), ev.getChunk().getPos().toLong());
		}
	}
	
	public void onLoadWorld(WorldEvent.Load ev)
	{
		if(!ev.getWorld().isClientSide())
		{
			tiles.addWorld(ev.getWorld());
		}
	}
	
	public void onUnloadWorld(WorldEvent.Unload ev)
	{
		if(!ev.getWorld().isClientSide())
		{
			tiles.removeWorld(ev.getWorld());
		}
	}
	
	public static void addInit(IAE2SolarPanelTile tile)
	{
		if(!tile.level().isClientSide())
		{
			Objects.requireNonNull(tile);
			tiles.addTile(tile);
		}
	}
	
	private static final ServerTileRepo tiles = new ServerTileRepo();
	
	private void readyTiles(IWorld world)
	{
		AbstractChunkProvider chunkProvider = world.getChunkSource();
		Long2ObjectMap<List<IAE2SolarPanelTile>> worldQueue = tiles.getTiles(world);
		long[] workSet = worldQueue.keySet().toLongArray();
		long[] var5 = workSet;
		int var6 = workSet.length;
		
		for(int var7 = 0; var7 < var6; ++var7)
		{
			long packedChunkPos = var5[var7];
			ChunkPos chunkPos = new ChunkPos(packedChunkPos);
			BlockPos testBlockPos = new BlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
			if(world.hasChunk(chunkPos.x, chunkPos.z) && chunkProvider.isTickingChunk(testBlockPos))
			{
				List<IAE2SolarPanelTile> chunkQueue = worldQueue.remove(packedChunkPos);
				
				if(chunkQueue == null)
				{
					AELog.warn("Chunk %s was unloaded while we were readying tiles", chunkPos);
				} else
				{
					for(IAE2SolarPanelTile bt : chunkQueue)
						if(!((TileEntity) bt).isRemoved())
							bt.onReady();
				}
			}
		}
		
	}
	
	static class ServerTileRepo
	{
		private final Map<IWorld, Long2ObjectMap<List<IAE2SolarPanelTile>>> tiles = new Object2ObjectOpenHashMap<>();
		
		ServerTileRepo()
		{
		}
		
		void clear()
		{
			this.tiles.clear();
		}
		
		synchronized void addTile(IAE2SolarPanelTile tile)
		{
			IWorld world = tile.level();
			int x = tile.pos().getX() >> 4;
			int z = tile.pos().getZ() >> 4;
			long chunkPos = ChunkPos.asLong(x, z);
			Long2ObjectMap<List<IAE2SolarPanelTile>> worldQueue = this.tiles.get(world);
			(worldQueue.computeIfAbsent(chunkPos, (key) -> new ArrayList<>()))
					.add(tile);
		}
		
		synchronized void addWorld(IWorld world)
		{
			this.tiles.computeIfAbsent(world, (key) -> new Long2ObjectOpenHashMap<>());
		}
		
		synchronized void removeWorld(IWorld world)
		{
			this.tiles.remove(world);
		}
		
		synchronized void removeWorldChunk(IWorld world, long chunkPos)
		{
			Map<Long, List<IAE2SolarPanelTile>> queue = this.tiles.get(world);
			if(queue != null)
				queue.remove(chunkPos);
		}
		
		public Long2ObjectMap<List<IAE2SolarPanelTile>> getTiles(IWorld world)
		{
			return this.tiles.get(world);
		}
	}
}