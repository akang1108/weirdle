package info.akang.weirdle.javacord;

import info.akang.weirdle.game.Play;
import org.javacord.api.DiscordApi;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class JavacordMessageListenerTest {
    @Mock
    Play play;

    @Mock
    DiscordApi api;

    @ParameterizedTest
    @MethodSource("provideMessages")
    void testExtractMessage(String message, String expected) {
        JavacordMessageListener listener = new JavacordMessageListener(play, api);
        assertEquals(expected, listener.extractMessage(message));
    }

    private static Stream<Arguments> provideMessages() {
        return Stream.of(
                Arguments.of("<@!2347127391>  <@!1239123821398> hello there!", "hello there!"),
                Arguments.of("<@&2347127391>hello there!", "hello there!"),
                Arguments.of("hello <@2347127391> there!", "hello  there!"),
                Arguments.of("hello there!", "hello there!")
        );
    }

    @ParameterizedTest
    @MethodSource("provideMentionedUserMessages")
    void testExtractMentionedUsers(String message, Set<Long> expected) {
        JavacordMessageListener listener = new JavacordMessageListener(play, api);
        Set<Long> actual = listener.extractMentionedUserIds(message);
        assertEquals(expected, actual);
    }

    private static Stream<Arguments> provideMentionedUserMessages() {
        return Stream.of(
                Arguments.of("<@!2347127391>  <@!1239123821398> hello there!", Set.of(2347127391L, 1239123821398L)),
                Arguments.of("<@&2347127391>hello there!", Set.of(2347127391L)),
                Arguments.of("hello <@2347127391> there!", Set.of(2347127391L)),
                Arguments.of("hello there!", Set.of())
        );
    }
}
