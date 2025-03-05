package org.zeith.solarflux.client;

import com.google.gson.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.*;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.*;
import org.zeith.hammerlib.client.model.*;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.solarflux.block.SolarPanelBlock;

import java.util.*;

import static org.zeith.solarflux.client.SolarPanelBakedModel.createSolarPanelQuads;

@LoadUnbakedGeometry(path = "solar_panel")
public class SolarPanelItemModel
		implements IUnbakedGeometry
{
	final SolarPanelBlock block;
	private final TextureSlots.Data textureSlots;
	
	public SolarPanelItemModel(JsonObject obj, JsonDeserializationContext context)
	{
		this.block = Cast.optionally(BuiltInRegistries.BLOCK.get(Resources.location(GsonHelper.getAsString(obj, "panel"))), SolarPanelBlock.class)
						 .orElseThrow(() -> new JsonSyntaxException("Unable to find solar panel block by id '" + GsonHelper.getAsString(obj, "panel") + "'"));
		
		var id = BuiltInRegistries.BLOCK.getKey(block);
		
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
		return new Baked(block, spriteGetter.get(textures.getMaterial("top")), spriteGetter.get(textures.getMaterial("base")));
	}
	
	@Override
	public void resolveDependencies(Resolver resolver)
	{
	}
	
	private static class Baked
			implements IBakedModel
	{
		public final SolarPanelBlock block;
		public final TextureAtlasSprite top, base;
		
		public Baked(SolarPanelBlock block, TextureAtlasSprite top, TextureAtlasSprite base)
		{
			this.block = block;
			this.top = top;
			this.base = base;
		}
		
		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction sideIn, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
		{
			SolarPanelModelData spmd = SolarPanelModelData.findOrItem(data);
			List<BakedQuad> quads = new ArrayList<>();
			Direction[] sides = sideIn == null ? Direction.values() : new Direction[] {sideIn};
			float h = block.panel.getPanelData().height * 16F;
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