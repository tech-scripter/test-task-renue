package com.vlasnagibin.project.reader;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader {

    /**
     * Чтение пользовательского ввода - желаемая строка для поиска
     * @return ввод пользователя
     */
    public String readConsole() {
        Scanner scn = new Scanner(System.in);
        System.out.print("\nВведите строку (\"exit\" для выхода): ");
        String input = scn.nextLine().trim();

        if (input.equals("exit")) {
            System.exit(0);
        }

        String regex = "^[-.?!)(,:^]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (input.isEmpty() || matcher.find()) {
            System.err.println("Некорректный ввод!\n");
            readConsole();
        }

        return input;
    }
}
