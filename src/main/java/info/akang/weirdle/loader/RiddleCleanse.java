package info.akang.weirdle.loader;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static info.akang.weirdle.loader.RiddleLoader.RIDDLE_QUESTION_ANSWER_DELIMITER;

@Slf4j
public class RiddleCleanse {
    public static void main(String[] args) throws Exception {
        new RiddleCleanse().cleanse("riddle/riddles_original.txt", "riddle/riddles.txt");
    }

    public void cleanse(String inputResourcePath, String outputResourcePath) throws URISyntaxException, IOException {
        RiddleLoader loader = new RiddleLoader(inputResourcePath);
        List<Puzzle> puzzles = loader.load();

        List<Puzzle> cleansed = puzzles.stream()
                .filter(removeLongAnswers)
                .filter(debug(removeAnswersWithNumbers))
                .map(toLowerCase)
                .map(removePunctuationAndWords)
                .filter(debug(removeAnswersWithSpecialChars))
                .map(removeQuestionWords)
                .map(trim)
                .collect(Collectors.toList());

        //debug(cleansed);

        String text = cleansed.stream()
                .map(p -> p.getMessage() + RIDDLE_QUESTION_ANSWER_DELIMITER + p.getAnswer())
                .collect(Collectors.joining("\n"));

        Path path = Paths.get(new File("src/main/resources/" + outputResourcePath).getAbsolutePath());
        System.out.println("writing new file to: " + path);

        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        Files.write(path, bytes, StandardOpenOption.TRUNCATE_EXISTING);
    }


    Predicate<Puzzle> removeLongAnswers = p -> p.getAnswer().length() < 20;

    Pattern NUMBER_PATTERN = Pattern.compile(".*[0-9].*");
    Predicate<Puzzle> removeAnswersWithNumbers = p -> !NUMBER_PATTERN.matcher(p.getAnswer()).matches();

    Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[,'\"].*");
    Predicate<Puzzle> removeAnswersWithSpecialChars = p -> !SPECIAL_CHAR_PATTERN.matcher(p.getAnswer()).matches();

    Function<Puzzle, Puzzle> toLowerCase = p -> p.withAnswer(p.getAnswer().toLowerCase(Locale.ROOT));

    Set<String> REMOVE_WORDS = Set.of("lol", "lolz");
    Set<String> REMOVE_STARTS_WITH = Set.of("a ", "i'm ", "i'm a", "i'm an", "an ", "the ", "your ", "i am ", "i am the ", "i am a ", "they are ");
    Set<String> QUESTION_REMOVE_STARTS_WITH = Set.of("{ ");

    Function<Puzzle, Puzzle> removePunctuationAndWords = p -> {
        String cleansedAnswer = p.getAnswer().replaceAll("[^a-zA-Z0-9,'\" ]", "");
        for (String removeWord: REMOVE_WORDS) {
            cleansedAnswer = cleansedAnswer.replace(removeWord, "");
        }
        for (String removeStartsWith: REMOVE_STARTS_WITH) {
            if (cleansedAnswer.startsWith(removeStartsWith)) {
                cleansedAnswer = cleansedAnswer.substring(removeStartsWith.length());
            }
        }
        return p.withAnswer(cleansedAnswer);
    };

    Function<Puzzle, Puzzle> removeQuestionWords = p -> {
        String cleansedQuestion = p.getMessage();
        for (String removeStartsWith: QUESTION_REMOVE_STARTS_WITH) {
            if (cleansedQuestion.startsWith(removeStartsWith)) {
                cleansedQuestion = cleansedQuestion.substring(removeStartsWith.length());
            }
        }
        return p.withMessage(cleansedQuestion);
    };

    Function<Puzzle, Puzzle> trim = p -> new Puzzle(p.getMessage().trim(), p.getAnswer().trim(), p.getIgnore());

    private void debug(List<Puzzle> puzzles) {
        int count = 0;
        for (Puzzle puzzle: puzzles) {
            if (puzzle.getAnswer().length() < 20) {
                count++;
                System.out.printf("%-20s %s%n", puzzle.getAnswer(), puzzle.getMessage());
            }
        }
        System.out.println("\nTotal count of puzzles: " + count);
    }

    private Predicate<Puzzle> debug(Predicate<Puzzle> predicate) {
        Predicate<Puzzle> wrappedPredicate = (Puzzle puzzle) -> {
            boolean result = predicate.test(puzzle);
            if (!result) {
                System.out.printf("removing answer %s%n", puzzle.getAnswer());
            }
            return result;
        };
        return wrappedPredicate;
    }
}
