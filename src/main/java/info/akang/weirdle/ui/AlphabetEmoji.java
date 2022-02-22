package info.akang.weirdle.ui;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class AlphabetEmoji {
    private final String EMOJIS = "🇦 🇧 🇨 🇩 🇪 🇫 🇬 🇭 🇮 🇯 🇰 🇱 🇲 🇳 🇴 🇵 🇶 🇷 🇸 🇹 🇺 🇻 🇼 🇽 🇾 🇿";

    @Getter
    private final Map<Character, String> alphabet;

    public static void main(String[] args) {
        AlphabetEmoji alphabetEmoji = new AlphabetEmoji();
        for (String emoji: alphabetEmoji.getAlphabet().values()) {
            System.out.println("emoji: ** " + emoji + " **");
        }
    }

    public AlphabetEmoji() {
        alphabet = new HashMap<>();

        String[] emojis = EMOJIS.split(" ");
        for (int i = 0; i < 26; i++) {
            alphabet.put((char) (i + 97), emojis[i]);
        }
    }

    public String get(Character c) {
        return alphabet.get(Character.toLowerCase(c));
    }
}
