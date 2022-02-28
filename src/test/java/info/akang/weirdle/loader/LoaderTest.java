package info.akang.weirdle.loader;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoaderTest {
    @Test
    void load() {
        RiddleLoader loader = new RiddleLoader();
        List<Puzzle> puzzles = loader.load();
        assertTrue(puzzles.size() > 0);
    }
}
