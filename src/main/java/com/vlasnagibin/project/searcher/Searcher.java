package com.vlasnagibin.project.searcher;

import com.vlasnagibin.project.reader.Reader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class Searcher {

    private final String path = "C:\\Users\\Vlas\\Desktop\\airports.csv";
    private final int column;

    public Searcher(int column) {
        this.column = column;
    }

    /**
     * Запускает поиск совпадений
     */
    public void run() {
        try {
            Reader reader = new Reader();
            String prefix = reader.readConsole();
            readFile(prefix);
            search(prefix);
        } catch (RuntimeException e) {
            System.err.println("Что-то пошло не так! Перезапустите программу.");
            e.printStackTrace();
        }
    }

    /**
     * Возвращает дерево с парами значение колонки/смещение от начала строки до нужной колонки
     * @param userInput введенная строка с консоли
     * @return отсортированное дерево по ключам, где ключ - значение колонки
     */
    private Map<String, Long> readFile(String userInput) {
        HashMap<String, Long> hashMap = new HashMap<>();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r")) {
            String row;
            String columnValue;
            long offset;
            while ((row = randomAccessFile.readLine()) != null) {
                columnValue = getColumnValue(row); // получаем значение каждой колонки
                if (columnValue.startsWith(userInput)) {
                    long distanceFromZeroToNeededColumn = getOffset(row);
                    offset = randomAccessFile.getFilePointer() + distanceFromZeroToNeededColumn - row.length(); // смещение от выбранной колонки до конца строки
                    hashMap.put(columnValue, offset);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TreeMap<>(hashMap);
    }

    /**
     * Осуществляет поиск по ключам дерева
     * @param userInput ввод пользвателя
     */
    private void search(String userInput) {
        long time = System.currentTimeMillis();
        Map<String, Long> map = readFile(userInput);
        Set<String> keySet = map.keySet();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r")) {
            for (String key : keySet) {
                if (key.startsWith(userInput)) {
                    randomAccessFile.seek(map.get(key));
                    System.out.println(randomAccessFile.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("%nКоличество найденных строк: %d%n", keySet.size());
        System.out.printf("Времени потрачено на поиск: %d мс%n", (System.currentTimeMillis() - time));
    }

    /**
     * Возвращает данные из колонки
     * @param line строка из файла
     * @return данные из колонки
     */
    private String getColumnValue(String line) {
        return line.split(",")[column - 1].replace("\"", "");
    }

    /**
     * Возвращает смещение
     * @param line строка из файла
     * @return смещение от начала строки до требуемой колонки
     */
    private long getOffset(String line) {
        String[] lineArray = line.split(",");
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < column - 1; i++) {
            str.append(lineArray[i].concat(","));
        }
        return str.toString().length() - 1;
    }
}
