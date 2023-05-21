import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> wordToPages;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        wordToPages = new HashMap<>();

        // Проходим по всем pdf-файлам в указанной директории
        for (File file : pdfsDir.listFiles((dir, name) -> name.endsWith(".pdf"))) {
            // Извлекаем текст из pdf-документа
            PDDocument document = PDDocument.load(file);
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();

            // Установите область текста, охватывающую всю страницу
            stripper.setSortByPosition(true);
            stripper.addRegion("page", new Rectangle2D.Float(0, 0, PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight()));

            // Пройдитесь по каждой странице и извлеките текст
            int numberOfPages = document.getNumberOfPages();
            for (int i = 0; i < numberOfPages; i++) {
                PDPage page = document.getPage(i);
                stripper.extractRegions(page);

                // Получите текст текущей страницы
                String pageText = stripper.getTextForRegion("page");
                String[] words = pageText.split("\\P{L}+");

                Map<String, Integer> wordCountMap = new HashMap<>(); // Мапа для подсчета количества слов на странице

                for (String word : words) {
                    word = word.toLowerCase();

                   /* // Регулярное выражение для поиска знаков препинания
                    String punctuationRegex = "[!\"#$%&'()-*+,./:;<=>?@[\\\\]^_`{|}~]";

                    // Поиск и добавление знаков препинания к слову
                    Pattern pattern = Pattern.compile(punctuationRegex);
                    Matcher matcher = pattern.matcher(word);
                    StringBuilder sb = new StringBuilder(word);

                    while (matcher.find()) {
                        sb.append(matcher.group());
                    }

                    String wordWithPunctuation = sb.toString();*/

                    // Увеличиваем счетчик для данного слова
                    int count = wordCountMap.getOrDefault(word, 0) + 1;
                    wordCountMap.put(word, count);
                }

                // Создаем объекты PageEntry и добавляем их в соответствующий список для каждого слова на странице
                for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
                    String word = entry.getKey();
                    int count = entry.getValue();

                    if (!wordToPages.containsKey(word)) {
                        wordToPages.put(word, new ArrayList<>());
                    }
                    wordToPages.get(word).add(new PageEntry(file.getName(), i + 1, count));
                }
            }
            document.close();
        }
    }

    @Override
    public List<PageEntry> search(String word) throws JsonProcessingException {
        word = word.toLowerCase();
        List<PageEntry> entries = wordToPages.getOrDefault(word, Collections.emptyList());
        entries.sort(Comparator.comparingInt(PageEntry::getCount).reversed()); // Сортировка по убыванию счетчика

        return entries;
    }

}