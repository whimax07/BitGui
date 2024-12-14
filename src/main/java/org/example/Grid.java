package org.example;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Grid<T> extends Collection<T> {

    Pattern NORTH      = new IncrementingPattern( 0, -1);
    Pattern SOUTH      = new IncrementingPattern( 0,  1);
    Pattern EAST       = new IncrementingPattern( 1,  0);
    Pattern WEST       = new IncrementingPattern(-1,  0);
    Pattern NORTH_EAST = new IncrementingPattern( 1, -1);
    Pattern NORTH_WEST = new IncrementingPattern(-1, -1);
    Pattern SOUTH_EAST = new IncrementingPattern( 1,  1);
    Pattern SOUTH_WEST = new IncrementingPattern(-1,  1);

    // =================================================================================================================
    // ==== Basic Accessors ============================================================================================

    T get(int x, int y);

    default T get(V2 position) {
        return get(position.x, position.y);
    }

    boolean exists(int x, int y);

    default boolean exists(V2 position) {
        return exists(position.x, position.y);
    }

    default boolean inBounds(int x, int y) {
        return exists(x, y);
    }

    default boolean inBounds(V2 position) {
        return exists(position);
    }


    // =================================================================================================================
    // ==== Iterators ==================================================================================================

    Sequence<T> iterator();

    Sequence<T> iterator(V2 start, Pattern pattern);

    Sequence<Locatable<T>> locatableIterator();

    Sequence<Locatable<T>> locatableIterator(V2 start, Pattern pattern);


    // =================================================================================================================
    // ==== Spliterators ===============================================================================================

    default Spliterator<T> spliterator(V2 start, Pattern pattern) {
        return Spliterators.spliteratorUnknownSize(iterator(start, pattern), 0);
    }

    default Spliterator<Locatable<T>> locatableSpliterator() {
        return Spliterators.spliteratorUnknownSize(locatableIterator(), 0);
    }

    default Spliterator<Locatable<T>> locatableSpliterator(V2 start, Pattern pattern) {
        return Spliterators.spliteratorUnknownSize(locatableIterator(start, pattern), 0);
    }


    // =================================================================================================================
    // ==== "Naive" Streams or Whole Collection Streams ================================================================

    default Stream<Locatable<T>> locatableStream() {
        return StreamSupport.stream(locatableSpliterator(), false);
    }


    // =================================================================================================================
    // ==== Pattern Navigation Streams =================================================================================

    default Stream<T> stream(V2 start, Pattern pattern) {
        return StreamSupport.stream(spliterator(start, pattern), false);
    }

    default Stream<Locatable<T>> locatableStream(V2 start, Pattern pattern) {
        return StreamSupport.stream(locatableSpliterator(start, pattern), false);
    }

    default Stream<Stream<T>> streamAll(Pattern pattern) {
        final Sequence<T> sequence = iterator();
        final V2 start = sequence.peekLocation().orElse(null);

        // tryNext will tell you if the thing you have just peeked was there, if it was the iterator is advanced.
        return Stream.iterate(
                stream(start, pattern),
                unused -> sequence.tryNext(),
                unused -> stream(sequence.peekLocation().orElse(null), pattern)
        );
    }

    default Stream<Stream<Locatable<T>>> locatableStreamAll(Pattern pattern) {
        final Sequence<Locatable<T>> sequence = locatableIterator();
        final V2 start = sequence.peekLocation().orElse(null);

        // tryNext will tell you if the thing you have just peeked was there, if it was the iterator is advanced.
        return Stream.iterate(
                locatableStream(start, pattern),
                unused -> sequence.tryNext(),
                unused -> locatableStream(sequence.peekLocation().orElse(null), pattern)
        );
    }


    // =================================================================================================================
    // ==== Supporting Classes =========================================================================================

    interface Locatable<T> {
        T get();
        V2 location();
    }

    interface Sequence<T> extends Iterator<T> {
        boolean hasNextLocation();

        V2 nextLocation();
        Optional<T> peek();

        Optional<V2> peekLocation();
        default boolean tryNext() {
            final boolean hasNext = hasNext();
            // NOTE(Max): We default to using nextLocation as it is assumed it is cheaper than next.
            if (hasNext) nextLocation();
            return hasNext;
        }

    }

    @FunctionalInterface
    interface Pattern {
        Optional<V2> next(V2 current);
    }



    record V2(int x, int y) {}

    record IncrementingPattern(int xIncrement, int yIncrement) implements Pattern {
        @Override
        public Optional<V2> next(V2 current) {
            final int newX = current.x + xIncrement;
            final int newY = current.y + yIncrement;
            return Optional.of(new V2(newX, newY));
        }
    }

}
