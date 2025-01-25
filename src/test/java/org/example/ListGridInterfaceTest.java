package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.AbstractGrid.UnsafePointer;
import org.example.Grid.CardanlPattern;
import org.example.Grid.Locatable;
import org.example.Grid.Pattern;
import org.example.Grid.V2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class ListGridInterfaceTest {

    private static final int N = 1;
    private static final int E = 2;
    private static final int S = 4;
    private static final int W = 8;

    @Test
    public void enhancedForLoop() {
        final ListGrid<Integer> listGrid = ListGrid.immutable(List.of(
                List.of(10, 20, 30),
                List.of(40, 50, 60),
                List.of(70, 80, 90)
        ));

        final V2 middle = new V2(1, 1);
        final V2 origin = new V2(0, 0);

        System.out.println("Fo loop, all items");
        for (Locatable<Integer> i : listGrid.locatableIterable()) {
            System.out.println(i);
        }

        System.out.println("For Loop, patterned");
        for (Integer i : listGrid.iterable(middle, Grid.NORTH)) {
            System.out.println(i);
        }

        System.out.println("Stream, patterned and limited");
        listGrid.stream(origin, Grid.SOUTH_EAST)
                .limit(2)
                .forEach(System.out::println);
    }

    /**
     *   ┌───┬─────┐
     *   │   │     │
     *   ├─┐ ┼ ────┤
     *   │         │
     *   │ ┌ ┐ ──┐ │
     *   │ │ │   │ │
     *   ├─┼ ┼─┬ ┼ │
     *   │     │   │
     *   │ ┌ ──┼─┬ │
     *   │ │   │   │
     *   └─┴───┴───┘
     */
    @Test
    public void maze() {
        //      0     1     2   3    4
        //  0   E,   SW,   ES, EW,   W
        //  1  ES, NESW, NESW, EW,  SW
        //  2   N,   NS,   NE, SW,  NS
        //  3  ES, NESW,    W, NE, NSW
        //  4   N,   NE,    W, E,  NW
        final List<List<Integer>> maze = List.of(
                List.of(E, S + W, E + S, E + W, W),
                List.of(E + S, N + E + S + W, N + E + S + W, E + W, S + W),
                List.of(N, N + S, N + E, S + W, N + S),
                List.of(E + S, N + E + S + W, W, N + E, N + S + W),
                List.of(N, N + E, W, E, N + W)
        );

        final ListGrid<Integer> listGrid = new ListGrid<>(maze);

        boolean noMistakes = true;
        for (Locatable<Integer> me : listGrid.locatableIterable()) {
            final UnsafePointer<Integer> pointer = new UnsafePointer<>(listGrid, me.location());

            noMistakes &= check(pointer, Grid.NORTH);
            noMistakes &= check(pointer, Grid.EAST);
            noMistakes &= check(pointer, Grid.SOUTH);
            noMistakes &= check(pointer, Grid.WEST);
        }

        assertTrue(noMistakes);

        /*
        var ref = grid.ref(start)

        progress(visited, ref, pattern, patterns) {
            var next = ref.tryNext(pattern)
            if (isBad(next) || visited.contains(next.loc)) {
                return bad;
            }

            visited.add(ref.loc);
            for (newPattern : patterns) {
                var result = progress(visited, next, newPattern, patterns)
                if (isBad(result)) continue;
                return result.append(ref);
            }
            return bad;

        }
         */
    }



    private static boolean check(UnsafePointer<Integer> me, Pattern dir) {
        final int dirEncoding = switch (dir) {
            case CardanlPattern.NORTH -> N;
            case CardanlPattern.EAST -> E;
            case CardanlPattern.SOUTH -> S;
            case CardanlPattern.WEST -> W;
            default -> throw new IllegalStateException("Unexpected value: " + dir);
        };

        final int reverseEncoding = switch (dirEncoding) {
            case N -> S;
            case E -> W;
            case S -> N;
            case W -> E;
            default -> throw new IllegalStateException("Unexpected value: " + dirEncoding);
        };

        // Safe as we only integrate over present values.
        final int directionsFromMe = me.poll();

        if ((directionsFromMe & dirEncoding) != 0) {
            final UnsafePointer<Integer> next = me.next(dir);
            final Integer poll = next.poll();
            if (poll == null || (poll & reverseEncoding) == 0) {
                log.warn("Inconsistent map. [Pos={}, Looking=North, Failed={}]", me.position(), poll);
                return false;
            }
        }

        return true;
    }

}
