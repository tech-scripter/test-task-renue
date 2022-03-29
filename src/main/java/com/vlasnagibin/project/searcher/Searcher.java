package com.vlasnagibin.project.searcher;

import com.vlasnagibin.project.pojo.IndexItem;
import com.vlasnagibin.project.reader.Reader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.*;

public class Searcher {

    private static final String FILE_NAME = "airports.csv";
    private final String path = String.valueOf(Paths.get(new File(".").getCanonicalPath(), FILE_NAME));
    private final int column;

    public Searcher(int column) throws IOException {
        this.column = column;
    }

    /**
     * Запускает поиск
     */
    public void run() {
        try {
            readFile();
            Reader reader = new Reader();
            String userInput = reader.readConsole();
            System.out.println();
            search(userInput);
        } catch (RuntimeException e) {
            System.err.println("Что-то пошло не так! Перезапустите программу.");
            e.printStackTrace();
        }
    }

    /**
     * Читает файл airports.csv по выбранной колонке и создает массив объектов, имеющих пару - ЗНАЧЕНИЕ КОЛОНКИ : СМЕЩЕНИЕ ОТ НАЧАЛА СТРОКИ ДО ПОЗИЦИИ ВЫБРАННОЙ КОЛОНКИ
     * @return отсортированный список объектов
     */
    private IndexItem[] readFile() {
        ArrayList<IndexItem> arrayList = new ArrayList<>();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r")) {
            String row;
            String columnValue;
            long offset;
            while ((row = randomAccessFile.readLine()) != null) {
                columnValue = getColumnValue(row);
                long distanceFromZeroToNeededColumn = getOffset(row);
                offset = randomAccessFile.getFilePointer() + distanceFromZeroToNeededColumn - row.length();
                arrayList.add(new IndexItem(columnValue, offset));
            }
        } catch (IOException e) {
            System.err.println("Файл не найден!");
            e.printStackTrace();
        }
        arrayList.sort(Comparator.comparing(IndexItem::getColumnValue));
        IndexItem[] indexItems = new IndexItem[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            indexItems[i] = arrayList.get(i);
        }

        return indexItems;
    }

    /**
     * Осуществляет бинарный поиск по массиву объектов и выводит результаты поиска
     * @param prefix ввод пользователя
     */
    private void search(String prefix) {
        long time = System.currentTimeMillis();
        IndexItem[] indexItems = readFile();
        int counter = 0;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r")) {
            long minIndex = findBound(indexItems, prefix, false);

            if (minIndex == -1) {
                return;
            }

            long maxIndex = findBound(indexItems, prefix, true);
            for (long i = minIndex; i <= maxIndex; i++) {
                randomAccessFile.seek(indexItems[(int) i].getOffset());
                System.out.println(randomAccessFile.readLine());
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Файл не найден!");
        }

        System.out.printf("%nКоличество найденных строк: %d%n", counter);
        System.out.printf("Времени потрачено на поиск: %d мс%n", (System.currentTimeMillis() - time));
    }

    /**
     * Кастомный бинарный поиск по массиву объектов с учетом нахождения префикса в значении колонки
     * @param index массив объектов
     * @param prefix ввод пользователя
     * @param isLast Вхождение. Если isLast = false, то первое вхождение (нижняя граница) и наоборот, если isLast = true, то последнее вхождение (верхняя граница)
     * @return граница
     */
    private long findBound(IndexItem[] index, String prefix, boolean isLast) {
        long min = 0;
        long max = index.length - 1;
        long result = -1;

        while (min <= max) {
            long mid = (min + max) / 2;
            switch ((int) prefixCompare(index[(int) mid].getColumnValue(), prefix)) {
                case 1:
                    max = mid - 1;
                    break;
                case -1:
                    min = mid + 1;
                    break;
                default:
                    result = mid;
                    if (isLast) {
                        min = mid + 1;
                    } else {
                        max = mid - 1;
                    }
                    break;
            }
        }

        return result;
    }

    /**
     * Ищет наличие префикса в значении колонки
     * @param columnValue значение колонки
     * @param prefix ввод пользователя
     * @return результат сравнения от -1 до 1
     */
    private long prefixCompare(String columnValue, String prefix) {
        for (int i = 0; i < prefix.length(); i++) {

            if (i == columnValue.length()) {
                return -1;
            }

            int result = Character.compare(columnValue.charAt(i), prefix.charAt(i));

            if (result != 0) {
                return result > 0 ? 1 : -1;
            }

        }

        return 0;
    }

    /**
     * Возвращает значение колонки
     * @param line строка файла
     * @return значение колонки
     */
    private String getColumnValue(String line) {
        return line.split(",")[column - 1].replace("\"", "");   // Переделать -> не оптимизировано
    }

    /**
     * Возвращает смещение
     * @param line строка файла
     * @return смещение от начала строки до позиции колонки
     */
    private long getOffset(String line) {
        String[] arrayOfLine = line.split(",");
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < column - 1; i++) {
            str.append(arrayOfLine[i].concat(","));
        }

        return str.toString().length() - 1;
    }
}
