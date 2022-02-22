package info.akang.weirdle.guess;

import info.akang.weirdle.loader.Puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuessCalculator {
    private final List<GuessValidator> guessValidators;

    public GuessCalculator() {
        this(new ArrayList<>());
    }

    public GuessCalculator(List<GuessValidator> guessValidators) {
        this.guessValidators = guessValidators;
    }

    public GuessCalculation calculate(String guess, Puzzle puzzle) {
        List<GuessValidationResponse> validationResponses = guessValidators.stream()
                .map(guessValidator -> guessValidator.validate(guess, puzzle)).collect(Collectors.toList());

        boolean valid = validationResponses.stream()
                .map(GuessValidationResponse::isValid)
                .reduce(true, (valid1, valid2) -> valid1 && valid2);

        if (!valid) {
            return new GuessCalculation(guess, false, validationResponses, false, null);
        }

        String answer = puzzle.getAnswer();
        boolean correct = true;

        List<GuessCharState> guessCharStates = new ArrayList<>();

        for (int i = 0; i < answer.length(); i++) {
            String a = String.valueOf(answer.charAt(i)).toUpperCase();
            String g = String.valueOf(guess.charAt(i)).toUpperCase();

            if (a.equals(g)) {
                guessCharStates.add(GuessCharState.RIGHT);
            } else if (answer.contains(g)) {
                correct = false;
                guessCharStates.add(GuessCharState.WRONG);
            } else {
                correct = false;
                guessCharStates.add(GuessCharState.WRONG_POSITION);
            }
        }

        return new GuessCalculation(guess, valid, validationResponses, correct, guessCharStates);
    }
}
