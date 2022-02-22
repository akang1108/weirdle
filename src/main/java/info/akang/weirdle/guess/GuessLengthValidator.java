package info.akang.weirdle.guess;

import info.akang.weirdle.loader.Puzzle;

public class GuessLengthValidator implements GuessValidator {
    @Override
    public GuessValidationResponse validate(String guess, Puzzle puzzle) {
        if (guess.length() != puzzle.getAnswer().length()) {
            return new GuessValidationResponse(false,
                    "Your guess must be " + puzzle.getAnswer().length() + " characters long (include the spaces)");
        } else {
            return new GuessValidationResponse(true, "");
        }
    }
}
