package org.zeith.solarflux.items.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

import java.util.List;

@SimplyRegister
public record PanelDataComponent(long energy, List<ItemStack> upgrades, List<ItemStack> chargeable)
{
	public static final PanelDataComponent EMPTY = new PanelDataComponent(0L, List.of(), List.of());
	
	public static final Codec<PanelDataComponent> CODEC = RecordCodecBuilder.create(inst ->
			inst.group(
					Codec.LONG.fieldOf("Energy").forGetter(PanelDataComponent::energy),
					ItemStack.OPTIONAL_CODEC.listOf().fieldOf("Upgrades").forGetter(PanelDataComponent::upgrades),
					ItemStack.OPTIONAL_CODEC.listOf().fieldOf("Chargeable").forGetter(PanelDataComponent::chargeable)
			).apply(inst, PanelDataComponent::new)
	);
	
	private static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> STACK_LIST_ST_CODEC = StreamCodec.of((buf, items) ->
	{
		buf.writeVarInt(items.size());
		for(var it : items) ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, it);
	}, buf ->
	{
		NonNullList<ItemStack> items = NonNullList.withSize(buf.readVarInt(), ItemStack.EMPTY);
		items.replaceAll(ignore -> ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
		return List.copyOf(items);
	});
	
	public static final StreamCodec<RegistryFriendlyByteBuf, PanelDataComponent> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, PanelDataComponent::energy,
			STACK_LIST_ST_CODEC, PanelDataComponent::upgrades,
			STACK_LIST_ST_CODEC, PanelDataComponent::chargeable,
			PanelDataComponent::new
	);
	
	@RegistryName("solar_panel")
	public static final DataComponentType<PanelDataComponent> TYPE = DataComponentType.<PanelDataComponent>builder()
			.persistent(CODEC)
			.networkSynchronized(STREAM_CODEC)
			.cacheEncoding()
			.build();
	
	public boolean isEmpty()
	{
		return this == EMPTY || (energy == 0L && upgrades.stream().allMatch(ItemStack::isEmpty) && chargeable.stream().allMatch(ItemStack::isEmpty));
	}
}