package info.akang.weirdle.loader;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class RiddleLoader {
    public static final Set<Character> RIDDLE_DEFAULT_IGNORE = Set.of(' ');
    public static final String RIDDLE_QUESTION_ANSWER_DELIMITER = "&";
    private final String resourcePath;

    public RiddleLoader() {
        this.resourcePath = "riddle/riddles.txt";
    }
    public RiddleLoader(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public List<Puzzle> load() {
        try {
            URL resource = getClass().getClassLoader().getResource(resourcePath);
            Path path = Paths.get(resource.toURI());
            List<String> allLines = Files.readAllLines(path);

            List<Puzzle> puzzles = new ArrayList<>();
            for (String line : allLines) {
                line = line.trim();
                if ("".equals(line)) {
                    log.debug("Blank line read, skipping");
                    continue;
                }

                String[] questionAndAnswer = line.split(RIDDLE_QUESTION_ANSWER_DELIMITER);
                if (questionAndAnswer.length != 2) {
                    log.debug("Line with not exactly 2 parts, skipping. Line: {}", line);
                    continue;
                }

                puzzles.add(new Puzzle(questionAndAnswer[0], questionAndAnswer[1], RIDDLE_DEFAULT_IGNORE));
            }

            return puzzles;
        } catch (IOException | URISyntaxException e) {
            throw new LoaderException(e);
        }
    }
}
