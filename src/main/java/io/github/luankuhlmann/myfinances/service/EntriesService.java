package io.github.luankuhlmann.myfinances.service;

import io.github.luankuhlmann.myfinances.model.entities.Entries;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntriesStatus;

import java.util.List;

public interface EntriesService {
    Entries register(Entries entries);
    Entries update(Entries entries);
    void delete(Entries entries);
    List<Entries> search(Entries entriesFilter);
    void updateStatus(Entries entries, EntriesStatus entriesStatus);

    void validate(Entries entries);
}
