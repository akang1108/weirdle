package info.akang.weirdle.guess;

import lombok.Value;

@Value
public class GuessValidationResponse {
    boolean valid;
    String message;
}
