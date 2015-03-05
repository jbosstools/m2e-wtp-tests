package utils;

import java.io.Serializable;

public class Pair<A, B> implements Serializable {

	private static final long serialVersionUID = 1L;

	public final A first;
	public final B second;

	public Pair(A a_first, B a_second) {
		first = a_first;
		second = a_second;
	}

	public static <U, V> Pair<U, V> of(U a_first, V a_second) {
		return new Pair<U, V>(a_first, a_second);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pair))
			return false;
		Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}
}
