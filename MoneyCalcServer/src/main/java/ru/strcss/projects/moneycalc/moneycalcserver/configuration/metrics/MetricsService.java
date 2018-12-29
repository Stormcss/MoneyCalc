package ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by Stormcss
 * Date: 12.12.2018
 */
@Getter
@Service
public class MetricsService {
    private final MeterRegistry registry;

    private final Map<TimerType, Timer> timersStorage;

    @Autowired
    public MetricsService(MeterRegistry registry) {
        this.registry = registry;
        this.timersStorage = new ConcurrentHashMap<>();
        initMetrics();
    }

    private void initMetrics() {
        Stream.of(TimerType.values())
                .forEach(timerType -> timersStorage.put(timerType, generateTimer(timerType)));
    }

    private Timer generateTimer(TimerType timerType) {
        return Timer.builder(timerType.getName())
                .description(timerType.getDescription())
                .register(registry);
    }
}
