package com.zeitheron.solarfluxreborn.network.energy.cable;

import java.util.HashSet;
import java.util.Set;

import com.zeitheron.solarfluxreborn.te.cable.TileAbstractCable;

public class CableNetwork
{
	public final Set<TileAbstractCable> wires = new HashSet<TileAbstractCable>();
	public final Set<TileAbstractCable> wiresTicked = new HashSet<TileAbstractCable>();
	
	public double energy, capacity;
	public int updateRate = 0;
	
	public void setNet(CableNetwork net)
	{
		if(net != null)
		{
			net.capacity += capacity;
			net.energy += energy;
		}
		
		for(TileAbstractCable w : wires.toArray(new TileAbstractCable[0]))
		{
			w.network = net;
			if(net != null)
				net.wires.add(w);
		}
		
		wires.clear();
		wiresTicked.clear();
		updateCapacity();
	}
	
	public void markTicked(TileAbstractCable wire)
	{
		if(wires.contains(wire))
			wiresTicked.add(wire);
		if(wiresTicked.size() == wires.size())
		{
			wiresTicked.clear();
			masterTick();
		}
	}
	
	public void masterTick()
	{
		if(updateRate++ >= 80)
		{
			updateRate = 0;
			updateCapacity();
		}
	}
	
	public void updateCapacity()
	{
		capacity = 0D;
		for(TileAbstractCable w : wires.toArray(new TileAbstractCable[0]))
			capacity = Math.max(w.getCapacityAddedToNet(), capacity);
		for(TileAbstractCable w : wires.toArray(new TileAbstractCable[0]))
		{
			if(w.internal > 0D)
			{
				energy += w.internal;
				if(energy > capacity)
				{
					w.internal += energy - capacity;
					energy = capacity;
				}
			}
		}
	}
	
	public void splitEnergyTo1Wire(TileAbstractCable wire)
	{
		double eq = getEqualAmtFor1Wire();
		wire.internal += eq;
		energy -= eq;
	}
	
	public double getEqualAmtFor1Wire()
	{
		if(wires.size() == 0)
			return 0D;
		return energy / wires.size();
	}
	
	public void disconnect(TileAbstractCable wire)
	{
		wires.remove(wire);
		wiresTicked.remove(wire);
		wire.network = null;
		die();
	}
	
	public void connect(TileAbstractCable wire)
	{
		wires.add(wire);
		wire.network = this;
	}
	
	public void merge(CableNetwork network)
	{
		network.setNet(this);
	}
	
	public void die()
	{
		setNet(null);
	}
}