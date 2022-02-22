package info.akang.weirdle.guess;

import info.akang.weirdle.loader.Puzzle;

@FunctionalInterface
public interface GuessValidator {
    GuessValidationResponse validate(String guess, Puzzle puzzle);
}
