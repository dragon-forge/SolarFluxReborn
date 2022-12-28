package org.zeith.solarflux.compat._abilities;

import org.zeith.solarflux.panels.SolarPanel;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Represents a collection of solar panels added by a compatibility module.
 *
 * <p>This class allows compatibility modules to add new types of solar panels to the game. It stores
 * a supplier of a stream of solar panels that are provided by the compatibility module, and provides
 * a method for accessing the stream of solar panels.
 */
public class AddedSolarPanels
{
	private final Supplier<Stream<SolarPanel>> providedPanels;
	
	/**
	 * Constructs a new collection of solar panels added by a compatibility module.
	 *
	 * @param providedPanels
	 * 		a supplier of a stream of solar panels that are provided by the compatibility module
	 */
	public AddedSolarPanels(Supplier<Stream<SolarPanel>> providedPanels)
	{
		this.providedPanels = providedPanels;
	}
	
	/**
	 * Returns a stream of the solar panels added by the compatibility module.
	 *
	 * @return a stream of the solar panels added by the compatibility module
	 */
	public Stream<SolarPanel> panels()
	{
		return providedPanels.get();
	}
}