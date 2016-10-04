/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;



public class MoleculeHelper {

	private static int maxID = 3600;

	public static void addMoleculeWithDecomposition(String molec, String roomstatus, String... parts) {
		//ChemicalAPI.registerMolecule(maxID, molec, r, g, b, r2, g2, b2, roomstatus, parts);
		maxID++;
		//RecipeAPI.addDecompositionRecipe(input, outputs);
	}

}
