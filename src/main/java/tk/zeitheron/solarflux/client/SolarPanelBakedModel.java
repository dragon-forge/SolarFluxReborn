package tk.zeitheron.solarflux.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;
import tk.zeitheron.solarflux.block.BlockBaseSolar;
import tk.zeitheron.solarflux.utils.PositionedStateImplementation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SideOnly(Side.CLIENT)
public class SolarPanelBakedModel
		implements IBakedModel
{
	public static final FaceBakery COOKER = new FaceBakery();
	public final BlockBaseSolar block;

	public SolarPanelBakedModel(BlockBaseSolar spb)
	{
		this.block = spb;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing sideIn, long rand)
	{
		IBlockAccess world;
		BlockPos pos;

		if(state instanceof PositionedStateImplementation)
		{
			PositionedStateImplementation pstate = (PositionedStateImplementation) state;
			world = pstate.getWorld();
			pos = pstate.getPos();
		} else return Collections.emptyList();

		boolean ctn = block.getPanelData().hasConnectedTextures();

		List<BakedQuad> quads = new ArrayList<>();
		EnumFacing[] sides = sideIn == null ? EnumFacing.VALUES : new EnumFacing[]{ sideIn };
		for(EnumFacing side : sides)
			if(side != null)
			{
				TextureAtlasSprite top = t_top(), base = t_base();

				float h = block.getPanelData().getHeight() * 16F;

				quads.add(COOKER.makeBakedQuad( //
						new Vector3f(0, 0, 0), new Vector3f(16, h, 16), //
						new BlockPartFace(null, 0, "#0", new BlockFaceUV(new float[]{
								0,
								side.getAxis() == EnumFacing.Axis.Y ? 0 : (16F - h),
								16,
								16
						}, 4)), //
						side == EnumFacing.UP ? top : base, side, ModelRotation.X0_Y0, null, true, true));

				// world/pos not set? no connected textures == no crash!
				if(world == null || pos == null)
					return quads;

				boolean west = false, east = false, north = false, south = false;

				if(west = world.getBlockState(pos.west()).getBlock() != block || !ctn)
					quads.add(COOKER.makeBakedQuad( //
							new Vector3f(0, h, 1), new Vector3f(1, h + 0.25F, 15), //
							new BlockPartFace(null, 0, "#0", new BlockFaceUV(side != EnumFacing.UP ? new float[]{
									0,
									0,
									16,
									1
							} : new float[]{
									0,
									0,
									1,
									16
							}, 4)), //
							base, side, ModelRotation.X0_Y0, null, true, true));

				if(east = world.getBlockState(pos.east()).getBlock() != block || !ctn)
					quads.add(COOKER.makeBakedQuad( //
							new Vector3f(15, h, 1), new Vector3f(16, h + 0.25F, 15), //
							new BlockPartFace(null, 0, "#0", new BlockFaceUV(side != EnumFacing.UP ? new float[]{
									0,
									0,
									16,
									1
							} : new float[]{
									15,
									0,
									16,
									16
							}, 4)), //
							base, side, ModelRotation.X0_Y0, null, true, true));

				if(north = world.getBlockState(pos.north()).getBlock() != block || !ctn)
					quads.add(COOKER.makeBakedQuad( //
							new Vector3f(1, h, 0), new Vector3f(15, h + 0.25F, 1), //
							new BlockPartFace(null, 0, "#0", new BlockFaceUV(new float[]{
									0,
									0,
									16,
									1
							}, 4)), //
							base, side, ModelRotation.X0_Y0, null, true, true));

				if(south = world.getBlockState(pos.south()).getBlock() != block || !ctn)
					quads.add(COOKER.makeBakedQuad( //
							new Vector3f(1, h, 15), new Vector3f(15, h + 0.25F, 16), //
							new BlockPartFace(null, 0, "#0", new BlockFaceUV(new float[]{
									0,
									0,
									16,
									1
							}, 4)), //
							base, side, ModelRotation.X0_Y0, null, true, true));

				if(west || north || world.getBlockState(pos.west().north()).getBlock() != block || !ctn)
					quads.add(COOKER.makeBakedQuad( //
							new Vector3f(0, h, 0), new Vector3f(1, h + 0.25F, 1), //
							new BlockPartFace(null, 0, "#0", new BlockFaceUV(new float[]{
									0,
									0,
									1,
									1
							}, 4)), //
							base, side, ModelRotation.X0_Y0, null, true, true));

				if(east || north || world.getBlockState(pos.east().north()).getBlock() != block || !ctn)
					quads.add(COOKER.makeBakedQuad( //
							new Vector3f(15, h, 0), new Vector3f(16, h + 0.25F, 1), //
							new BlockPartFace(null, 0, "#0", new BlockFaceUV(new float[]{
									15,
									0,
									16,
									1
							}, 4)), //
							base, side, ModelRotation.X0_Y0, null, true, true));

				if(south || east || world.getBlockState(pos.south().east()).getBlock() != block || !ctn)
					quads.add(COOKER.makeBakedQuad( //
							new Vector3f(15, h, 15), new Vector3f(16, h + 0.25F, 16), //
							new BlockPartFace(null, 0, "#0", new BlockFaceUV(new float[]{
									15,
									15,
									16,
									16
							}, 4)), //
							base, side, ModelRotation.X0_Y0, null, true, true));

				if(west || south || world.getBlockState(pos.west().south()).getBlock() != block || !ctn)
					quads.add(COOKER.makeBakedQuad( //
							new Vector3f(0, h, 15), new Vector3f(1, h + 0.25F, 16), //
							new BlockPartFace(null, 0, "#0", new BlockFaceUV(new float[]{
									0,
									15,
									1,
									16
							}, 4)), //
							base, side, ModelRotation.X0_Y0, null, true, true));
			}
		return quads;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return new ItemCameraTransforms(getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND), getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND), getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND), getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND), getTransform(ItemCameraTransforms.TransformType.HEAD), getTransform(ItemCameraTransforms.TransformType.GUI), getTransform(ItemCameraTransforms.TransformType.GROUND), getTransform(ItemCameraTransforms.TransformType.FIXED));
	}

	public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type)
	{
		switch(type)
		{
			case GUI:
				return new ItemTransformVec3f(new Vector3f(30, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.625F, 0.625F, 0.625F));
			default:
				break;
		}
		return ItemTransformVec3f.DEFAULT;
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return ThreadLocalRandom.current().nextInt(5) > 0 ? t_base() : t_top();
	}

	public TextureAtlasSprite t_base()
	{
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(block.getRegistryName().getNamespace() + ":blocks/" + block.getRegistryName().getPath() + "_base");
	}

	public TextureAtlasSprite t_top()
	{
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(block.getRegistryName().getNamespace() + ":blocks/" + block.getRegistryName().getPath() + "_top");
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return false;
	}

	@Override
	public boolean isGui3d()
	{
		return false;
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return false;
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return ItemOverrideList.NONE;
	}
}