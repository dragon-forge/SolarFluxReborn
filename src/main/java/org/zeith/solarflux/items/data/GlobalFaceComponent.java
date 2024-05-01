package org.zeith.solarflux.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.StreamCodec;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

@SimplyRegister
public record GlobalFaceComponent(GlobalPos pos, Direction dir)
{
	public static final Codec<GlobalFaceComponent> CODEC = RecordCodecBuilder.create(inst ->
			inst.group(
					GlobalPos.CODEC.fieldOf("Pos").forGetter(GlobalFaceComponent::pos),
					Direction.CODEC.fieldOf("Face").forGetter(GlobalFaceComponent::dir)
			).apply(inst, GlobalFaceComponent::new)
	);
	
	public static final StreamCodec<ByteBuf, GlobalFaceComponent> STREAM_CODEC = StreamCodec.composite(
			GlobalPos.STREAM_CODEC, GlobalFaceComponent::pos,
			Direction.STREAM_CODEC, GlobalFaceComponent::dir,
			GlobalFaceComponent::new
	);
	
	@RegistryName("global_face")
	public static final DataComponentType<GlobalFaceComponent> TYPE = DataComponentType.<GlobalFaceComponent>builder()
			.persistent(CODEC)
			.networkSynchronized(STREAM_CODEC)
			.cacheEncoding()
			.build();
}