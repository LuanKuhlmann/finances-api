package io.github.luankuhlmann.myfinances.service;

import io.github.luankuhlmann.myfinances.model.entities.Entries;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntriesStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EntriesService {
    Entries register(Entries entries);
    Entries update(Entries entries);
    void delete(Entries entries);
    List<Entries> search(Entries entriesFilter);
    void updateStatus(Entries entries, EntriesStatus entriesStatus);
    void validate(Entries entries);
    Optional<Entries> findById(Long id);
    BigDecimal getBalancePerUser(Long id);
}
