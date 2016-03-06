package org.iguana.datadependent.env.array;

import java.util.Arrays;

import com.google.common.math.IntMath;
import org.iguana.datadependent.env.Environment;

public class ArrayEnvironment implements Environment {

    private final Object[] values;

    /*
     * As an environment is always associated with a specific grammar position,
     * only values participate in structural equality
     */
	private final int hash;
	
	static public final ArrayEnvironment EMPTY = new ArrayEnvironment(null, 0);
	
	private ArrayEnvironment(Object[] values, int hash) {
        this.values = values;
		this.hash = hash;
	}

    @Override
	public boolean isEmpty() {
		return this == EMPTY;
	}

	@Override
	public int hashCode() {
		return hash;
	}

    /*
     * As an environment is always associated with a specific grammar position,
     * no fast check on length of values
     */
	@Override
	public boolean equals(Object other) {

        if (this == other)
			return true;
		
		if (!(other instanceof ArrayEnvironment)
                || this.hash != other.hashCode())
			return false;
		
		ArrayEnvironment that = (ArrayEnvironment) other;

        assert values.length == that.values.length;
		
		for (int i = 0; i < values.length; i++)
			if (!values[i].equals(that.values[i]))
				return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(values);
	}

	@Override
	public Environment declare(Object value) {

		int length = values == null ? 0 : values.length;

        if (length == 0)
            return new ArrayEnvironment(new Object[] { value }, 31 + (value == null ? 0 : value.hashCode()));

        Object[] values = new Object[length + 1];
        System.arraycopy(this.values, 0, values, 0, length);
		values[length] = value;

		return new ArrayEnvironment(values, 31 * hash + (value == null ? 0 : value.hashCode()));
	}

	@Override
	public Environment declare(Object... values) {
        if (values.length == 0)
            return this;

		int length = this.values == null ? 0 : this.values.length;

        Object[] vs = new Object[length + values.length];

		if (length != 0)
			System.arraycopy(this.values, 0, vs, 0, length);
		
		int j = 0;
        int hash = this.hash == 0 ? 1 : this.hash;
		for (int i = length; i < length + values.length; i++) {
            Object value = values[j];
			vs[i] = value;
			hash = 31 * hash + (value == null ? 0 : value.hashCode());
			j++;
		}
		
		return new ArrayEnvironment(vs, hash);
	}

	@Override
	public Environment store(int i, Object value) {

        int length = values.length;

        if (length == 1)
            return new ArrayEnvironment(new Object[] { value }, 31 + (value == null ? 0 : value.hashCode()));

        Object[] vs = new Object[length];
        System.arraycopy(values, 0, vs, 0, length);

        Object v = vs[i];
        int hash = this.hash - ((v == null ? 0 : v.hashCode()) - (value == null ? 0 : value.hashCode())) * pow(length - i - 1);
        vs[i] = value;

		return new ArrayEnvironment(vs, hash);
	}

	@Override
	public Object lookup(int i) {
		return values[i];
	}

    private static int pow(int k) {
        int b = 31;
        int a = 1;

        while (true) {
            switch (k) {
                case 0: return a;
                case 1: return b * a;
            }
            a *= (k&1)==0 ? 1 : b;
            b *= b;
            k >>= 1;
        }
    }

}
