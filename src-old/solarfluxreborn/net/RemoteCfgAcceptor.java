package com.zeitheron.solarfluxreborn.net;

import java.io.InputStream;

import com.zeitheron.hammercore.net.transport.ITransportAcceptor;
import com.zeitheron.solarfluxreborn.config.RemoteConfigs;

public class RemoteCfgAcceptor implements ITransportAcceptor
{
	@Override
	public void read(InputStream readable, int length)
	{
		RemoteConfigs.unpack(readable);
	}
}