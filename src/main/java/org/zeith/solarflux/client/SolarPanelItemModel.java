package org.zeith.solarflux.client;

import com.google.gson.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.*;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.*;
import org.zeith.hammerlib.client.model.*;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.solarflux.block.SolarPanelBlock;
import org.zeith.solarflux.init.SolarPanelsSF;

import java.util.*;

import static org.zeith.solarflux.client.SolarPanelBakedModel.createSolarPanelQuads;

@LoadUnbakedGeometry(path = "solar_panel")
public class SolarPanelItemModel
		implements IUnbakedGeometry
{
	private final ResourceLocation id;
	private final TextureSlots.Data textureSlots;
	
	public SolarPanelItemModel(JsonObject obj, JsonDeserializationContext context)
	{
		this.id = Resources.location(GsonHelper.getAsString(obj, "panel"));
		
		var slots = new TextureSlots.Data.Builder();
		
		var atlas = TextureAtlas.LOCATION_BLOCKS;
		
		slots.addTexture("base", new Material(atlas, Resources.location(id.getNamespace(), "block/" + id.getPath() + "_base")));
		slots.addTexture("top", new Material(atlas, Resources.location(id.getNamespace(), "block/" + id.getPath() + "_top")));
		
		textureSlots = slots.build();
	}
	
	@Override
	public TextureSlots.Data getTextureSlots()
	{
		return textureSlots;
	}
	
	@Override
	public BakedModel bake(TextureSlots textures, ModelBaker baker, ModelState modelState, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms itemTransforms, ContextMap additionalProperties)
	{
		var spriteGetter = baker.sprites();
		return new Baked(id, spriteGetter.get(textures.getMaterial("top")), spriteGetter.get(textures.getMaterial("base")));
	}
	
	@Override
	public void resolveDependencies(Resolver resolver)
	{
	}
	
	private static class Baked
			implements IBakedModel
	{
		private SolarPanelBlock blockCached;
		
		public final ResourceLocation id;
		public final TextureAtlasSprite top, base;
		
		public Baked(ResourceLocation id, TextureAtlasSprite top, TextureAtlasSprite base)
		{
			this.id = id;
			this.top = top;
			this.base = base;
		}
		
		public SolarPanelBlock getBlock()
		{
			if(blockCached != null) return blockCached;
			var sp = SolarPanelsSF.PANELS_BY_ID.get(id);
			if(sp == null) return null;
			return this.blockCached = sp.getBlock();
		}
		
		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction sideIn, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
		{
			SolarPanelModelData spmd = SolarPanelModelData.findOrItem(data);
			List<BakedQuad> quads = new ArrayList<>();
			
			SolarPanelBlock block = getBlock();
			if(block == null) return quads;
			
			float h = block.panel.getPanelData().height * 16F;
			
			Direction[] sides = sideIn == null ? Direction.values() : new Direction[] {sideIn};
			for(Direction side : sides)
				if(side != null)
					createSolarPanelQuads(quads, side, h, top, base, spmd);
			
			return quads;
		}
		
		@Override
		public boolean useAmbientOcclusion()
		{
			return false;
		}
		
		@Override
		public boolean isGui3d()
		{
			return false;
		}
		
		@Override
		public boolean usesBlockLight()
		{
			return true;
		}
		
		final RandomSource rng = RandomSource.create();
		
		@Override
		public TextureAtlasSprite getParticleIcon()
		{
			return rng.nextInt(3) == 0 ? top : base;
		}
	}
}