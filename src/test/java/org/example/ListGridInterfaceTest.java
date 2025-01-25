package org.example;

import org.example.Grid.Locatable;
import org.example.Grid.V2;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ListGridInterfaceTest {

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
     *    N=1 , E=2 , S=4 ,  W=8
     */
    @Test
    public void maze() {
        List.of(
                List.of( 2, 12,  6, 10, 10,  8),
                List.of( 6, 15, 15, 10, 10, 12),
                List.of( 1,  0,  0,  0,  0,  1),
                List.of()
        );

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

}
