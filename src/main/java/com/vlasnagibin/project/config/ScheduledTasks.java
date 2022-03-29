package com.vlasnagibin.project.config;

import com.vlasnagibin.project.searcher.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Value("${search.column}")
    private int searchColumn;

    private final ApplicationArguments applicationArguments;

    @Autowired
    public ScheduledTasks(ApplicationArguments applicationArguments) {
        this.applicationArguments = applicationArguments;
    }

    @Scheduled(fixedRate = 5000)
    public void findAirports() {
        long memory = Runtime.getRuntime().freeMemory();
        if (applicationArguments.getSourceArgs().length != 0) {
            try {
                searchColumn = Integer.parseInt(applicationArguments.getSourceArgs()[0]);
            } catch (NumberFormatException e) {
                System.err.println("Ошибка! Неверный формат колонки.");
                e.printStackTrace();
            }
        }
        Searcher searcher = new Searcher(2);
        searcher.run();
        long end = Runtime.getRuntime().freeMemory();
        long memoTaken = memory - end;
        System.out.printf("Памяти использовано: %f%n", memoTaken / 1e+6);
    }
}
