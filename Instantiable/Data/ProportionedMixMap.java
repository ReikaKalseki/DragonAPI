/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;


public final class ProportionedMixMap<V, K> {

	private final PluralMap<K> data = new PluralMap(2);

	public ProportionedMixMap() {

	}

	public void addMix(V e1, int amt1, V e2, int amt2, K out) {
		MixPut k1 = new MixPut(e1, amt1);
		MixPut k2 = new MixPut(e2, amt2);
		data.put(out, k1, k2);
	}

	public K getMix(V e1, int amt1, V e2, int amt2) {
		MixPut k1 = new MixPut(e1, amt1);
		MixPut k2 = new MixPut(e2, amt2);
		return data.get(k1, k2);
	}

	/**"Note: this class has a natural ordering that is inconsistent with equals."*/
	private static final class MixPut<V> implements Comparable<MixPut> {

		private final V entry;
		private final int amount;

		private MixPut(V v, int amt) {
			entry = v;
			amount = amt;
		}

		@Override
		public int hashCode() {
			return entry.hashCode() ^ amount;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof MixPut) {
				MixPut m = (MixPut)o;
				return m.entry.equals(entry) && m.amount == amount;
			}
			return false;
		}

		@Override
		public int compareTo(MixPut o) {
			return this.entry == o.entry ? this.amount - o.amount : 0;
		}

	}

}
