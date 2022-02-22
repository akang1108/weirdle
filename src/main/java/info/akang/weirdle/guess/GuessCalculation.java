package info.akang.weirdle.guess;

import lombok.Value;

import java.util.List;

@Value
public class GuessCalculation {
    String guess;
    boolean valid;
    List<GuessValidationResponse> guessValidationResponses;
    boolean correct;
    List<GuessCharState> guessCharStates;
}
