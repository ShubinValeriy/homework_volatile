import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static AtomicInteger threeCounter = new AtomicInteger(0);
    public static AtomicInteger fourCounter = new AtomicInteger(0);
    public static AtomicInteger fiveCounter = new AtomicInteger(0);


    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        List<Thread> threads = new ArrayList<>();

        // сгенерированное слово является палиндромом
        Thread polynomialThread = new Thread(() -> {
            for (String text : texts) {
                int length = text.length();
                // уберем никнеймы, которые проверяются другим потоком
                boolean check = !checkSameLetters(text);
                // Двигаемся с обоих концов слова к серидине
                for (int i = 0; i < (length / 2); i++) {
                    // Сравниваем символы попарно
                    if (text.charAt(i) != text.charAt(length - i - 1)) {
                        // Если найдено несоответствие - слово не палиндром
                        check = false;
                        break;
                    }
                }
                adderCount(text, check);
            }
        });
        polynomialThread.start();
        threads.add(polynomialThread);

        // сгенерированное слово состроит из одного и того же символа
        Thread sameLatterThread = new Thread(() -> {
            for (String text : texts) {
                adderCount(text, checkSameLetters(text));
            }
        });
        sameLatterThread.start();
        threads.add(sameLatterThread);

        // В сгенерированном слове буквы идут по возрастанию
        Thread ascendingLettersThread = new Thread(() -> {
            for (String text : texts) {
                // уберем никнеймы, которые проверяются другим потоком
                boolean check = !checkSameLetters(text);
                for (int i = 0; i < text.length() - 1; i++) {
                    if (text.charAt(i) > text.charAt(i + 1)) {
                        check = false;
                        break;
                    }
                }
                adderCount(text, check);
            }
        });
        ascendingLettersThread.start();
        threads.add(ascendingLettersThread);


        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Красивых слов с длиной 3: " + threeCounter + " шт");
        System.out.println("Красивых слов с длиной 4: " + fourCounter + " шт");
        System.out.println("Красивых слов с длиной 5: " + fiveCounter + " шт");

    }


    public static boolean checkSameLetters(String text) {
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) != text.charAt(i + 1)) {
                return false;
            }
        }
        return true;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void adderCount(String text, boolean check) {
        if (check) {
            switch (text.length()) {
                case (3):
                    threeCounter.getAndIncrement();
                    break;
                case (4):
                    fourCounter.getAndIncrement();
                    break;
                case (5):
                    fiveCounter.getAndIncrement();
                    break;
            }
        }
    }
}
