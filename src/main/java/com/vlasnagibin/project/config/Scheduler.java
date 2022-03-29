package com.vlasnagibin.project.config;

import com.vlasnagibin.project.exception.EmptyFileException;
import com.vlasnagibin.project.searcher.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class Scheduler {
    @Value("${search.column}")
    private int searchColumn;
    private final ApplicationArguments applicationArguments;

    // Dependency Injection
    @Autowired
    public Scheduler(ApplicationArguments applicationArguments) {
        this.applicationArguments = applicationArguments;
    }

    /**
     * Запускает поисковик через каждые 3 мс
     */
    @Scheduled(fixedRate = 3000)
    public void scheduleSearching() {
        try {
            if (applicationArguments.getSourceArgs().length != 0) {
                try {
                    searchColumn = Integer.parseInt(applicationArguments.getSourceArgs()[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Неверный формат колонки!");
                    e.printStackTrace();
                }
            }
            Searcher searcher = new Searcher(searchColumn);
            searcher.run();

        } catch (EmptyFileException e) {
            System.err.println("Пустой файл!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Файл не найден!");
            e.printStackTrace();
        }
    }
}
