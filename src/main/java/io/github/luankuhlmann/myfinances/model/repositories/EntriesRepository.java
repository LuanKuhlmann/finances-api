package io.github.luankuhlmann.myfinances.model.repositories;

import io.github.luankuhlmann.myfinances.model.entities.Entries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntriesRepository extends JpaRepository<Entries, Long> {
}
