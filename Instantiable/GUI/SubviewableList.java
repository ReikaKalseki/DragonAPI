package Reika.DragonAPI.Instantiable.GUI;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.MathHelper;

public class SubviewableList<E> {

	private final List<E> data;
	public final int subviewSize;

	private int viewOffset;

	public SubviewableList(List<E> li, int s) {
		data = li;
		this.subviewSize = s;
	}

	public List<E> getVisibleSublist() {
		this.viewOffset = Math.min(this.viewOffset, this.getMaxOffset());
		List<E> ret = new ArrayList();
		if (data.isEmpty())
			return ret;
		int i0 = this.viewOffset;
		int i1 = i0+this.clampedSize();
		for (int i = i0; i < i1; i++) {
			ret.add(data.get(i));
		}
		return ret;
	}

	public E getEntryAtRelativeIndex(int idx) {
		idx -= this.viewOffset;
		return this.data.get(idx);
	}

	public int getAbsoluteIndex(int rel) {
		return rel+this.viewOffset;
	}

	public void stepOffset(int d) {
		this.viewOffset = MathHelper.clamp_int(this.viewOffset+d, 0, this.getMaxOffset());
	}

	public int size() {
		return this.data.size();
	}

	public int clampedSize() {
		return Math.min(this.data.size(), this.subviewSize);
	}

	private int getMaxOffset() {
		return Math.max(0, data.size()-this.subviewSize);
	}

}
