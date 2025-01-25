package org.example;

import lombok.NonNull;

import java.util.AbstractCollection;
import java.util.NoSuchElementException;
import java.util.Optional;

public abstract class AbstractGrid<T> extends AbstractCollection<T> implements Grid<T> {

    @Override
    public Sequence<T> iterator(V2 start, Pattern pattern) {
        return new Itor<>(this, pattern, start);
    }

    @Override
    public Sequence<Locatable<T>> locatableIterator() {
        return new SequenceBride<>(iterator());
    }

    @Override
    public Sequence<Locatable<T>> locatableIterator(V2 start, Pattern pattern) {
        return new SequenceBride<>(iterator(start, pattern));
    }



    public record LocationPair<T>(T get, V2 location) implements Locatable<T> { }

    public static class UnsafePointer<T> {

        private final Grid<T> grid;
        private final V2 currentPos;



        public UnsafePointer(Grid<T> grid, V2 item) {
            this.grid = grid;
            currentPos = item;
        }



        /**
         * Nullable version of peek. Tries to get the element the pointer points to. Null if it doesn't exist.
         */
        public T poll() {
            return grid.tryGet(currentPos).orElse(null);
        }

        public Optional<T> peek() {
            return grid.tryGet(currentPos);
        }

        public V2 position() {
            return currentPos;
        }

        public UnsafePointer<T> next(Pattern pattern) {
            final Optional<V2> next = pattern.next(currentPos);
            if (next.isEmpty()) return null;
            return new UnsafePointer<>(grid, next.get());
        }

    }



    protected static class Itor<T> implements Sequence<T> {

        private final Grid<T> grid;
        private final Pattern pattern;

        private V2 next;



        public Itor(Grid<T> grid, Pattern pattern, V2 start) {
            this.grid = grid;
            this.pattern = pattern;
            // You can't start an iteration from somewhere that doesn't exist as it is not clear where you should go
            // next in general.
            this.next = (start != null && grid.exists(start)) ? start : null;
        }



        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public boolean hasNextLocation() {
            return next != null;
        }

        @Override
        public T next() {
            if (next == null) throw new NoSuchElementException();
            final T current = grid.get(next);
            next = progress();
            return current;
        }

        @Override
        public V2 nextLocation() {
            if (next == null) throw new NoSuchElementException();
            final V2 current = next;
            next = progress();
            return current;
        }

        @Override
        public Optional<T> peek() {
            // Exists is checked at next assignment.
            if (next == null) return Optional.empty();
            return Optional.of(grid.get(next));
        }

        @Override
        public Optional<V2> peekLocation() {
            // Exists is checked at next assignment.
            return Optional.ofNullable(next);
        }

        private V2 progress() {
            return pattern.next(next)
                    // Filter out V2s that doesn't exist on the grid.
                    .filter(grid::exists)
                    .orElse(null);
        }

    }

    protected static class EmptySequence<T> implements Sequence<T> {

        public static final EmptySequence<?> EMPTY_SEQUENCE = new EmptySequence<>();

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public boolean hasNextLocation() {
            return false;
        }

        @Override
        public T next() {
            throw new NoSuchElementException();
        }

        @Override
        public V2 nextLocation() {
            throw new NoSuchElementException();
        }

        @Override
        public Optional<T> peek() {
            return Optional.empty();
        }

        @Override
        public Optional<V2> peekLocation() {
            return Optional.empty();
        }

    }



    private static class SequenceBride<T> implements Sequence<Locatable<T>> {

        private final Sequence<T> position;



        public SequenceBride(Sequence<T> iterator) {
            this.position = iterator;
        }



        @Override
        public boolean hasNextLocation() {
            return position.hasNextLocation();
        }

        @Override
        public V2 nextLocation() {
            return position.nextLocation();
        }

        @Override
        public Optional<Locatable<T>> peek() {
            final T peek = position.peek().orElse(null);
            if (peek == null) return Optional.empty();

            final V2 location = position.peekLocation().orElseThrow(() -> new RuntimeException(String.format(
                    "%s has failed %s contract, peek has returned a non-empty value while peekLocation has not.",
                    position.getClass().getSimpleName(), Sequence.class.getSimpleName()
            )));

            return Optional.of(new LocationPair<>(peek, location));
        }

        @Override
        public Optional<V2> peekLocation() {
            return position.peekLocation();
        }

        @Override
        public boolean hasNext() {
            return position.hasNext();
        }

        @NonNull
        @Override
        public Locatable<T> next() {
            // If loc is null next will throw due to Sequence's preconditions.
            final V2 loc = position.peekLocation().orElse(null);
            final T next = position.next();
            return new LocationPair<>(next, loc);
        }

    }

}
