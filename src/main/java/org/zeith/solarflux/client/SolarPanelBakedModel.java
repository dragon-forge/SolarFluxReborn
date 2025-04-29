package org.zeith.solarflux.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.solarflux.block.SolarPanelBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class SolarPanelBakedModel
		implements IDynamicBakedModel
{
	public static final FaceBakery COOKER = new FaceBakery();
	public final SolarPanelBlock block;
	public final ResourceLocation registryName;
	
	public SolarPanelBakedModel(SolarPanelBlock spb)
	{
		this.block = spb;
		this.registryName = BuiltInRegistries.BLOCK.getKey(spb);
	}
	
	public static BakedQuad quad(Vector3f from, Vector3f to, float[] uv, int uvRot, TextureAtlasSprite sprite, Direction facing)
	{
		return COOKER.bakeQuad(
				from, to,
				new BlockElementFace(null, 0, "#0", new BlockFaceUV(uv, uvRot)),
				sprite, facing, BlockModelRotation.X0_Y0, null, true
		);
	}
	
	public static void createSolarPanelQuads(
			List<BakedQuad> quads, Direction side,
			float h, TextureAtlasSprite top, TextureAtlasSprite base,
			SolarPanelModelData data
	)
	{
		boolean west = !data.west(), east = !data.east(), north = !data.north(), south = !data.south();
		
		quads.add(quad(
				new Vector3f(0, 0, 0), new Vector3f(16, h, 16),
				new float[] {
						0,
						side.getAxis() == Direction.Axis.Y ? 0 : (16F - h),
						16,
						16
				},
				4, side == Direction.UP ? top : base, side
		));
		
		if(west)
			quads.add(quad(
					new Vector3f(0, h, 1), new Vector3f(1, h + 0.25F, 15),
					side != Direction.UP ? new float[] {
							0,
							0,
							16,
							1
					} : new float[] {
							0,
							0,
							1,
							16
					},
					4, base, side
			));
		
		if(east)
			quads.add(quad(
					new Vector3f(15, h, 1), new Vector3f(16, h + 0.25F, 15),
					side != Direction.UP ? new float[] {
							0,
							0,
							16,
							1
					} : new float[] {
							15,
							0,
							16,
							16
					},
					4, base, side
			));
		
		if(north)
			quads.add(quad(
					new Vector3f(1, h, 0), new Vector3f(15, h + 0.25F, 1),
					new float[] {
							0,
							0,
							16,
							1
					},
					4, base, side
			));
		
		if(south)
			quads.add(quad(
					new Vector3f(1, h, 15), new Vector3f(15, h + 0.25F, 16),
					new float[] {
							0,
							0,
							16,
							1
					},
					4, base, side
			));
		
		if(west || north || !data.westNorth())
			quads.add(quad(
					new Vector3f(0, h, 0), new Vector3f(1, h + 0.25F, 1),
					new float[] {
							0,
							0,
							1,
							1
					},
					4, base, side
			));
		
		if(east || north || !data.eastNorth())
			quads.add(quad(
					new Vector3f(15, h, 0), new Vector3f(16, h + 0.25F, 1),
					new float[] {
							15,
							0,
							16,
							1
					},
					4, base, side
			));
		
		if(south || east || !data.eastSouth())
			quads.add(quad(
					new Vector3f(15, h, 15), new Vector3f(16, h + 0.25F, 16),
					new float[] {
							15,
							15,
							16,
							16
					},
					4, base, side
			));
		
		if(west || south || !data.westSouth())
			quads.add(quad(
					new Vector3f(0, h, 15), new Vector3f(1, h + 0.25F, 16),
					new float[] {
							0,
							15,
							1,
							16
					},
					4, base, side
			));
	}
	
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction sideIn, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType)
	{
		SolarPanelModelData data = SolarPanelModelData.findOrItem(extraData);
		TextureAtlasSprite top = t_top(), base = t_base();
		float h = block.panel.getPanelData().height * 16F;
		
		List<BakedQuad> quads = new ArrayList<>();
		Direction[] sides = sideIn == null ? Direction.values() : new Direction[] {sideIn};
		for(Direction side : sides)
			if(side != null)
				createSolarPanelQuads(quads, side, h, top, base, data);
		
		return quads;
	}
	
	@Override
	public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData)
	{
		return modelData
				.derive()
				.with(SolarPanelModelData.PROPERTY, SolarPanelModelData.gather(level, pos, state.getBlock()))
				.build();
	}
	
	@Override
	public ItemTransforms getTransforms()
	{
		return new ItemTransforms(
				getTransform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND),
				getTransform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND),
				getTransform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND),
				getTransform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND),
				getTransform(ItemDisplayContext.HEAD),
				getTransform(ItemDisplayContext.GUI),
				getTransform(ItemDisplayContext.GROUND),
				getTransform(ItemDisplayContext.FIXED)
		);
	}
	
	@Override
	public ItemOverrides getOverrides()
	{
		return ItemOverrides.EMPTY;
	}
	
	public ItemTransform getTransform(ItemDisplayContext type)
	{
		switch(type)
		{
			case GUI:
				return new ItemTransform(new Vector3f(30, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.625F, 0.625F, 0.625F));
			default:
				break;
		}
		return ItemTransform.NO_TRANSFORM;
	}
	
	@Override
	public TextureAtlasSprite getParticleIcon()
	{
		return ThreadLocalRandom.current().nextInt(5) > 0 ? t_base() : t_top();
	}
	
	final Function<ResourceLocation, TextureAtlasSprite> spriteGetter = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
	ResourceLocation baseTx, topTx;
	
	public TextureAtlasSprite t_base()
	{
		if(baseTx == null)
			baseTx = Resources.location(registryName.getNamespace(), "block/" + registryName.getPath() + "_base");
		return spriteGetter.apply(baseTx);
	}
	
	public TextureAtlasSprite t_top()
	{
		if(topTx == null)
			topTx = Resources.location(registryName.getNamespace(), "block/" + registryName.getPath() + "_top");
		return spriteGetter.apply(topTx);
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
	
	@Override
	public boolean isCustomRenderer()
	{
		return false;
	}
}