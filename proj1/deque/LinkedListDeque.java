package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class GeneralNode {
        private GeneralNode prev;
        private T item;
        private GeneralNode next;

        GeneralNode(GeneralNode p, T i, GeneralNode n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    private int size;
    private GeneralNode sentinel;

    /**
     * Creates an "empty" LinkedListDeque that includes a sentinel node
     **/
    public LinkedListDeque() {
        sentinel = new GeneralNode(null, null, null);
        size = 0;
    }

    public void addFirst(T i) {
        size += 1;
        GeneralNode newNode = new GeneralNode(sentinel, i, sentinel.next);
        sentinel.next = newNode;
        if (size == 1) {
            newNode.next = sentinel;
        }
        newNode.next.prev = newNode;
    }

    public void addLast(T i) {
        size += 1;
        GeneralNode newNode = new GeneralNode(sentinel.prev, i, sentinel);
        sentinel.prev = newNode;
        if (size == 1) {
            newNode.prev = sentinel;
        }
        newNode.prev.next = newNode;
    }


    public int size() {
        return size;
    }

    public void printDeque() {
        if (this.sentinel.next.item == null) {
            System.out.println();
        } else {
            printHelper(sentinel.next);
        }
    }

    /**
     * Helper method to print nodes that are not directly
     * accessible through instance variables associated with the sentinel node.
     **/
    private void printHelper(GeneralNode node) {
        System.out.print(node.item + " ");
        if (node.next.item == null) {
            System.out.println();
        } else {
            printHelper(node.next);
        }
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T ret = sentinel.next.item;
        if (ret == null) {
            return null;
        }
        size -= 1;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        return ret;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T ret = sentinel.prev.item;
        size -= 1;
        sentinel.prev = sentinel.prev.prev;
        if (size > 0) {
            sentinel.prev.next = sentinel;
        } else {
            sentinel.next = sentinel;
        }
        return ret;
    }

    public T get(int index) {
        GeneralNode tracker = sentinel.next;
        int counter = 0;
        while (counter < index) {
            tracker = tracker.next;
            counter += 1;
        }
        return tracker.item;
    }

    public T getRecursive(int index) {
        return recursiveHelper(index, sentinel.next);
    }

    private T recursiveHelper(int index, GeneralNode p) {
        if (index == 0) {
            return p.item;
        }
        return recursiveHelper(index - 1, p.next);
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
                if (!(this.get(i).equals(other.get(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public Iterator<T> iterator() {
        return new LLDequeIterator();
    }
    private class LLDequeIterator implements Iterator<T> {
        private int iterIndex;
        private GeneralNode p;
        LLDequeIterator() {
            p = sentinel.next;
            iterIndex = 0;
        }
        public boolean hasNext() {
            return iterIndex < size;
        }
        public T next() {
            T ret = p.item;
            p = p.next;
            iterIndex += 1;
            return ret;
        }
    }
}
