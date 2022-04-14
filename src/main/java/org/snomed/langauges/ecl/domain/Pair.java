package org.snomed.langauges.ecl.domain;

public class Pair<T> {

	private T first;
	private T second;

	@SuppressWarnings("unused")
	private Pair() {
		// For JSON
	}

	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "Pair{" +
				"first=" + first +
				", second=" + second +
				'}';
	}
}
