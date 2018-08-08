package com.zeitheron.solarfluxreborn.bars.energy;

import java.util.ArrayList;
import java.util.List;

public class NumShortening
{
	private static final List<Long> nums = new ArrayList<>();
	private static final List<Long> numsorted = new ArrayList<>();
	private static final List<String> numn = new ArrayList<>();
	
	static
	{
		assignNumber(1_000L, "K");
		assignNumber(1_000_000L, "M");
		assignNumber(1_000_000_000L, "B");
		assignNumber(1_000_000_000_000L, "T");
	}
	
	public static void assignNumber(long n, String id)
	{
		if(!nums.contains(n))
		{
			nums.add(n);
			numn.add(id);
			
			numsorted.clear();
			numsorted.addAll(nums);
			numsorted.sort((l1, l2) -> (int) (l2 > l1 ? 1 : l2 < l1 ? -1 : 0));
		}
	}
	
	public static String shorten(long n, int afterDot)
	{
		for(int i = 0; i < numsorted.size(); ++i)
		{
			long b = numsorted.get(i);
			
			if(n >= b)
			{
				float f = (float) (n / (double) b);
				String fs = "" + f;
				if(f % 1F > 0)
				{
					float h = (f % 1F) * (10 ^ afterDot);
					fs = "" + (int) f + "." + Math.round(h);
				} else
					fs = "" + (long) f;
				return fs + " " + numn.get(nums.indexOf(b));
			}
		}
		
		return n + "";
	}
}