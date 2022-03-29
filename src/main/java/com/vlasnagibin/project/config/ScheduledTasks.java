package com.vlasnagibin.project.config;

import com.vlasnagibin.project.searcher.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class ScheduledTasks {

    @Value("${search.column}")
    private int searchColumn;

    private final ApplicationArguments applicationArguments;

    // Dependency Injection
    @Autowired
    public ScheduledTasks(ApplicationArguments applicationArguments) {
        this.applicationArguments = applicationArguments;
    }

    /**
     * Запускает поисковик через каждые 5 мс
     */
    @Scheduled(fixedRate = 5000)
    public void findAirports() {
        try {
            if (applicationArguments.getSourceArgs().length != 0) {
                try {
                    searchColumn = Integer.parseInt(applicationArguments.getSourceArgs()[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Ошибка! Неверный формат колонки.");
                    e.printStackTrace();
                }
            }
            Searcher searcher = new Searcher(searchColumn);
            searcher.run();
        } catch (IOException e) {
            System.err.println();
        }
    }
}
