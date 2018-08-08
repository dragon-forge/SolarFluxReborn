package com.zeitheron.solarfluxreborn.core;

import java.io.Serializable;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class SFRSerializedTransformer implements IClassTransformer, Serializable
{
	public static final String SerializableClass;
	static
	{
		String ser = Serializable.class.getName();
		char[] data = ser.toCharArray();
		for(int i = 0; i < data.length; ++i)
			if(data[i] == '.')
				data[i] = '/';
		ser = new String(data);
		SerializableClass = ser;
		
		// SFRLog.info("\"%s\" application for class * is \"%s\"",
		// Serializable.class.getName(), ser);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		// ClassNode cls = loadClass(basicClass);
		// if((!name.contains(".") || name.startsWith("net.minecraft") ||
		// name.startsWith("mcp") || name.startsWith("ibxm")) &&
		// cls.interfaces.size() == 0)
		// {
		// cls.interfaces.add(SerializableClass);
		// return writeClassToByteArray(cls);
		// }
		return basicClass;
	}
	
	public static ClassNode loadClass(byte[] data)
	{
		ClassReader reader = new ClassReader(data);
		ClassNode node = new ClassNode();
		reader.accept(node, 0);
		return node;
	}
	
	public static byte[] writeClassToByteArray(ClassNode node)
	{
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		node.accept(writer);
		return writer.toByteArray();
	}
}