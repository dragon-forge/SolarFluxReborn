package com.zeitheron.solarfluxreborn.utility;

import java.util.Collection;
import java.util.HashSet;

public class ArrayHashSet<E> extends HashSet<E>
{
	public E[] arr = null;
	
	@Override
	public boolean add(E e)
	{
		boolean bool = super.add(e);
		if(bool)
			updateArray();
		return bool;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		boolean bool = super.addAll(c);
		if(bool)
			updateArray();
		return bool;
	}
	
	@Override
	public boolean remove(Object o)
	{
		boolean bool = super.remove(o);
		if(bool)
			updateArray();
		return bool;
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean bool = super.removeAll(c);
		if(bool)
			updateArray();
		return bool;
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		boolean bool = super.retainAll(c);
		if(bool)
			updateArray();
		return bool;
	}
	
	@Override
	public void clear()
	{
		super.clear();
		updateArray();
	}
	
	public void updateArray()
	{
		arr = toArray((E[]) new Object[0]);
	}
	
	public E[] asArray()
	{
		if(arr == null || size() != arr.length)
			updateArray();
		return arr;
	}
}