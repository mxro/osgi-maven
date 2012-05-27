package de.mxro.thrd.hamcrest.internal;

import java.util.Iterator;

public class ArrayIterator implements Iterator<Object> {
    private final Object array;
    private int currentIndex = 0;
    
    public ArrayIterator(Object array) {
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("not an array");
        }
        this.array = array;
    }
    
    @Override
	public boolean hasNext() {
        return currentIndex < ArrayAccess.getLength(array);
    }

    @Override
	public Object next() {
        return ArrayAccess.get(array, currentIndex++);
    }
    
    @Override
	public void remove() {
        throw new UnsupportedOperationException("cannot remove items from an array");
    }
}
