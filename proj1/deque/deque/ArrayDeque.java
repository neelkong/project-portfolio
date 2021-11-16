package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int trackerBeg;
    private int trackerEnd;
    private int size;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        trackerEnd = 4;
        trackerBeg = 3;
        size = 0;
    }


     //makes a new array of a new size and copies contents of original array into it
    private void resizeHelper(int factor) {
        int resizeFactor;
        if (factor == 0) {
            resizeFactor = items.length * 2;
        } else {
            resizeFactor = items.length / 2;
        }
        T[] a = (T[]) new Object[resizeFactor];
        int startingIndex = trackerProcessor(trackerBeg + 1);
        int endingIndex = trackerProcessor(trackerEnd - 1);
        for (int i = 0; i < size; i++) {
            a[i] = get(i);
        }
        items = a;
        trackerBeg = items.length - 1;
        trackerEnd = size;
    }
    /** Access the first item of the array **/
    private T getFirst() {
        return items[trackerProcessor(trackerBeg + 1)];
    }
    /**Access the last item of the array **/
    private T getLast() {
        return items[trackerProcessor(trackerEnd - 1)];
    }

    //Processes values outside of the normal bounds of the array.
    private int trackerProcessor(int trackerValue) {
        /** it needs to process the trackerBeg variable **/
        int ret = trackerValue;
        while (ret < 0) {
            ret += items.length;
        }
        /** it needs to process the trackerEnd variable **/
        while (ret >= items.length) {
            ret -= items.length;
        }
        return ret;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            this.resizeHelper(0);
        }
        items[trackerBeg] = item;
        trackerBeg -= 1;
        trackerBeg = trackerProcessor(trackerBeg);
        size += 1;
    }

    public void addLast(T item) {
        if (size == items.length) {
            this.resizeHelper(0);
        }
        items[trackerEnd] = item;
        trackerEnd += 1;
        trackerEnd = trackerProcessor(trackerEnd);
        size += 1;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int counter = 0;
        while (counter < size) {
            counter += 1;
            System.out.print(items[trackerProcessor(counter + trackerBeg)] + " ");
        }
        System.out.println();
    }

    public T removeLast() {
        if (size == 0) {
            return getLast();
        }
        if (size - 1 < (double) items.length / 4 && items.length > 8) {
            resizeHelper(1);
        }
        T ret = getLast();
        size -= 1;
        trackerEnd = trackerProcessor(trackerEnd - 1);
        return ret;
    }

    public T removeFirst() {
        if (size == 0) {
            return getFirst();
        }
        if (size - 1 < (double) items.length / 4 && items.length > 8) {
            resizeHelper(1);
        }
        T ret = getFirst();
        size -= 1;
        trackerBeg = trackerProcessor(trackerBeg + 1);
        return ret;
    }

    public T get(int index) {
        if (index < 0 || index > size) {
            return null;
        }
        return items[trackerProcessor(trackerBeg + index + 1)];
    }
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Deque) {
            Deque<T> other = (Deque<T>) o;
            if (other.size() != this.size()) {
                return false;
            }
            for (int i = 0; i < this.size(); i++) {
                if (!this.get(i).equals(other.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int iterIndex;

        ArrayDequeIterator() {
            iterIndex = 0;
        }
        public boolean hasNext() {
            return iterIndex < size;
        }
        public T next() {
            T ret = get(iterIndex);
            iterIndex += 1;
            return ret;
        }
    }
}
