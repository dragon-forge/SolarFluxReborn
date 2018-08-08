package com.zeitheron.solarflux.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class TextureAtlasSpriteFull extends TextureAtlasSprite
{
	public static final TextureAtlasSpriteFull sprite = new TextureAtlasSpriteFull("full");
	
	protected TextureAtlasSpriteFull(String spriteName)
	{
		super(spriteName);
		width = 256;
		height = 256;
		initSprite(256, 256, 0, 0, false);
	}
}