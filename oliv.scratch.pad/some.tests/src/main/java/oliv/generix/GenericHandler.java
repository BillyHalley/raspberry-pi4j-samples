package oliv.generix;

public class GenericHandler<X> {

	private X storage;

	public void put(X object) {
		this.storage = object;
	}

	public X get() {
		return this.storage;
	}

	public static <T> GenericHandler<T> of(T obj) {
		GenericHandler gh = new GenericHandler<T>();
		gh.put(obj);
		return gh;
	}
}
