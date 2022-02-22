package info.akang.weirdle.loader;

import lombok.Value;

import java.util.Set;

@Value
public class Puzzle {
    String message;
    String answer;
    Set<Character> ignore;

    public Puzzle withAnswer(String answer) {
        return new Puzzle(this.message, answer, this.ignore);
    }

    public Puzzle withMessage(String message) {
        return new Puzzle(message, this.answer, this.ignore);
    }
}
