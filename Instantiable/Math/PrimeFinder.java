package Reika.DragonAPI.Instantiable.Math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import Reika.DragonAPI.Exception.MisuseException;


public class PrimeFinder {

	private long countLimit;

	private final HashSet<Long> primes;

	/** The expected highest value you will query. It can be expanded later, with a brief performance cost. */
	public PrimeFinder(long limit) {
		countLimit = limit;
		primes = new HashSet((int)Math.sqrt(limit), 0.5F);
		this.calculateFrom(3, limit);
	}

	public void expandTo(long limit) {
		this.calculateFrom(countLimit, limit);
		countLimit = limit;
	}

	private void calculateFrom(long low, long hi) {
		if (low%2 == 0)
			low = low+1;
		if (hi%2 == 0)
			hi = hi-1;
		for (long val = low; val <= hi; val += 2) {
			if (this.evaluate(val)) {
				primes.add(val);
			}
		}
	}

	private boolean evaluate(long val) {
		if (val < 3)
			return false;
		if ((val&1) == 0)
			return false;
		return this.checkPrimesDivision(val);
	}

	private boolean checkPrimesDivision(long val) {
		for (long p : primes) {
			if (val%p == 0)
				return false;
		}
		return true;
	}

	public boolean isPrime(long val) {
		val = Math.abs(val);
		if (val > countLimit)
			throw new MisuseException("You cannot query values larger than the calculated range!");
		return primes.contains(val);
	}

	public ArrayList<Long> getPrimes() {
		ArrayList<Long> li = new ArrayList(primes);
		Collections.sort(li);
		return li;
	}

}
