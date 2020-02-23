package tk.zeitheron.solarflux.utils;

import java.nio.ByteBuffer;

public class FByteHelper
{
	public static final ThreadLocal<ByteBuffer> BBUF_4 = ThreadLocal.withInitial(() -> ByteBuffer.allocate(4));
	
	public static int toInt(float f)
	{
		BBUF_4.get().position(0);
		BBUF_4.get().putFloat(f);
		BBUF_4.get().flip();
		return BBUF_4.get().getInt();
	}
	
	public static float toFloat(int i)
	{
		BBUF_4.get().position(0);
		BBUF_4.get().putInt(i);
		BBUF_4.get().flip();
		return BBUF_4.get().getFloat();
	}
}