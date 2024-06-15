package rcs.mulder.utils.struct;

import java.util.Iterator;

public class MulderArray<T extends MulderGarbageCollectable> extends MulderCollection<T> {

  private T[] array;
  
  private int capacity;
  
  private int count = 0;

  public MulderArray(int size) {
    this.capacity = size;
    clear();
  }

  public void add(T t) {
    array[count++] = t;
  }
  
  @SuppressWarnings("unchecked")
  public void clear() {
    array = (T[]) new MulderGarbageCollectable[capacity];
    count = 0;
  }

  public int size() {
    return capacity;
  }
  
  public int count() {
    return count;
  }

  @Override
  public Iterator<T> iterator() {
    return new ArrayIterator();
  }
  
  private class ArrayIterator implements Iterator<T> { 
    private int index = 0;

    @Override
    public boolean hasNext() {
      return index < count;
    }

    @Override
    public T next() {
      if (!hasNext()) {
        return null;
      }
      return array[index++];
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException(
          "Use " + MulderLinkedList.class.getName() + " for removal capability");
    } 
  }
}
