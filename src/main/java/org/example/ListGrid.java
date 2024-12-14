package org.example;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ListGrid<T> extends AbstractGrid<T> {

    private final List<List<T>> data;

    private int size = -1;



    /**
     * For use by subclasses.
     */
    protected ListGrid(List<List<T>> data) {
        this.data = data;
    }

    public static <T> ListGrid<T> noCopy(List<List<T>> data) {
        return new ListGrid<>(data);
    }

    public static <T> ListGrid<T> immutable(List<List<T>> data) {
        final List<List<T>> unmodifiableCopy = data.stream()
                .map(Collections::unmodifiableList)
                .toList();

        return new ListGrid<>(unmodifiableCopy);
    }



    @Override
    public T get(int x, int y) {
        return data.get(y).get(x);
    }

    @Override
    public boolean exists(int x, int y) {
        if (x < 0 || y < 0 || y >= data.size()) return false;
        final List<T> row = data.get(y);
        return x < row.size();
    }

    @Override
    public int size() {
        // This class doesn't enforce a regular grid, and therefore we can't do better than O(sizeof(data)) so we cash
        // the result as the collection is immutable.
        if (size == -1) {
            size = data.stream().mapToInt(List::size).sum();
        }
        return size;
    }

    @Override
    public Sequence<T> iterator() {
        final Pattern allItems = current -> {
            if (current.y() >= data.size()) return Optional.empty();

            final List<T> row = data.get(current.y());
            final int nextX = current.x() + 1;
            if (nextX < row.size()) {
                final V2 nextV2 = new V2(nextX, current.y());
                return Optional.of(nextV2);
            }

            for (int nextY = current.y() + 1; nextY < data.size(); nextY++) {
                if (data.get(nextY).isEmpty()) continue;
                final V2 nextV2 = new V2(0, nextY);
                return Optional.of(nextV2);
            }

            return Optional.empty();
        };

        final V2 preStart = new V2(-1, 0);
        final V2 outOfBounds = new V2(Integer.MAX_VALUE, Integer.MAX_VALUE);
        final V2 start = allItems.next(preStart).orElse(outOfBounds);

        return new Itor<>(this, allItems, start);
    }

}
