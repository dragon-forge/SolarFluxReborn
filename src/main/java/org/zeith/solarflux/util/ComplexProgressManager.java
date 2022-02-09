package org.zeith.solarflux.util;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiConsumer;

public class ComplexProgressManager
{
	static final ThreadLocal<ByteBuffer> buffers2 = ThreadLocal.withInitial(() -> ByteBuffer.allocate(2));
	static final ThreadLocal<ByteBuffer> buffers4 = ThreadLocal.withInitial(() -> ByteBuffer.allocate(4));
	static final ThreadLocal<ByteBuffer> buffers8 = ThreadLocal.withInitial(() -> ByteBuffer.allocate(8));
	final byte[] buffer, previousBuffer;
	final int startIndex;

	public ComplexProgressManager(int size, int startIndex)
	{
		this.buffer = new byte[size];
		this.previousBuffer = new byte[size];
		this.startIndex = startIndex;
	}

	public void detectAndSendChanges(AbstractContainerMenu owner)
	{
		detectAndSendChanges(owner, owner.containerListeners);
	}

	public void detectAndSendChanges(AbstractContainerMenu owner, List<ContainerListener> listeners)
	{
		if(owner.synchronizer == null) return;
		detectAndSendChanges((i, s) ->
		{
			for(ContainerListener j : listeners)
				j.dataChanged(owner, i, s);
			if(owner.synchronizer != null)
				owner.synchronizer.sendDataChange(owner, i, s);
		});
	}

	public void detectAndSendChanges(BiConsumer<Integer, Byte> propertySender)
	{
		for(int i = 0; i < buffer.length; ++i)
			if(buffer[i] != previousBuffer[i])
			{
				propertySender.accept(startIndex + i, buffer[i]);
				previousBuffer[i] = buffer[i];
			}
	}

	public void updateChange(int id, int data)
	{
		id -= startIndex;
		if(id >= 0 && id < buffer.length)
			buffer[id] = (byte) data;
	}

	public void putBoolean(int bytePos, boolean value)
	{
		putByte(bytePos, (byte) (value ? 1 : 0));
	}

	public boolean getBoolean(int bytePos)
	{
		return buffer[bytePos] > 0;
	}

	public void putByte(int bytePos, byte value)
	{
		buffer[bytePos] = value;
	}

	public byte getByte(int bytePos)
	{
		return buffer[bytePos];
	}

	public void putShort(int bytePos, short value)
	{
		ByteBuffer buf = buffers2.get();
		buf.position(0);
		buf.putShort(value);
		buf.flip();
		buf.get(buffer, bytePos, 2);
	}

	public short getShort(int bytePos)
	{
		ByteBuffer buf = buffers2.get();
		buf.position(0);
		buf.put(buffer, bytePos, 2);
		buf.flip();
		return buf.getShort();
	}

	public void putInt(int bytePos, int value)
	{
		ByteBuffer buf = buffers4.get();
		buf.position(0);
		buf.putInt(value);
		buf.flip();
		buf.get(buffer, bytePos, 4);
	}

	public int getInt(int bytePos)
	{
		ByteBuffer buf = buffers4.get();
		buf.position(0);
		buf.put(buffer, bytePos, 4);
		buf.flip();
		return buf.getInt();
	}

	public void putFloat(int bytePos, float value)
	{
		ByteBuffer buf = buffers4.get();
		buf.position(0);
		buf.putFloat(value);
		buf.flip();
		buf.get(buffer, bytePos, 4);
	}

	public float getFloat(int bytePos)
	{
		ByteBuffer buf = buffers4.get();
		buf.position(0);
		buf.put(buffer, bytePos, 4);
		buf.flip();
		return buf.getFloat();
	}

	public void putLong(int bytePos, long value)
	{
		ByteBuffer buf = buffers8.get();
		buf.position(0);
		buf.putLong(value);
		buf.flip();
		buf.get(buffer, bytePos, 8);
	}

	public long getLong(int bytePos)
	{
		ByteBuffer buf = buffers8.get();
		buf.position(0);
		buf.put(buffer, bytePos, 8);
		buf.flip();
		return buf.getLong();
	}

	public void putDouble(int bytePos, double value)
	{
		ByteBuffer buf = buffers8.get();
		buf.position(0);
		buf.putDouble(value);
		buf.flip();
		buf.get(buffer, bytePos, 8);
	}

	public double getDouble(int bytePos)
	{
		ByteBuffer buf = buffers8.get();
		buf.position(0);
		buf.put(buffer, bytePos, 8);
		buf.flip();
		return buf.getDouble();
	}
}