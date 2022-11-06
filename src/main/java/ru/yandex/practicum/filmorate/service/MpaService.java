package ru.yandex.practicum.filmorate.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.DbMpaStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MpaService {
    private final DbMpaStorage mpaStorage;

    @Autowired
    public MpaService(DbMpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<MPA> getAllMpa() {
        return mpaStorage.getAll().stream()
                .sorted(Comparator.comparing(MPA::getMpaId))
                .collect(Collectors.toList());
    }

    public MPA getMpaById(Long id) {
        return mpaStorage.getMpaId(id);
    }
}