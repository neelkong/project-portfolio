package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comp;


    public MaxArrayDeque(Comparator<T> c) {
        super();
        comp = c;
    }

    public T max() {
        return max(this.comp);
    }

    public T max(Comparator<T> c) {
        T ret = (T) this.get(0);
        for (int i = 1; i < size(); i++) {
            if (c.compare(ret, (T) this.get(i)) < 0) {
                ret = (T) this.get(i);
            }
        }
        return ret;
    }

}
