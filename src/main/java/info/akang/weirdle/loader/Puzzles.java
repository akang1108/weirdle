package info.akang.weirdle.loader;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
public class Puzzles {
    List<Puzzle> puzzles;

    public Puzzles() {
        this.puzzles = new RiddleLoader().load();
    }

    public Puzzle getPuzzle() {
        int num = this.puzzles.size();
        int randomIndex = (int)(Math.random() * num);
        return this.puzzles.get(randomIndex);
    }
}
