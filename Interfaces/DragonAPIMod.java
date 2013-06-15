/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import java.net.MalformedURLException;
import java.net.URL;

public interface DragonAPIMod {

	public String getDisplayName();

	public String getModAuthorName();

	public URL getDocumentationSite() throws MalformedURLException;

	public boolean hasWiki();

	public URL getWiki() throws MalformedURLException;

}
