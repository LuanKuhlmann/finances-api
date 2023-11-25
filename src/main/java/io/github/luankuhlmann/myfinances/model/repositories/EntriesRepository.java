package io.github.luankuhlmann.myfinances.model.repositories;

import io.github.luankuhlmann.myfinances.model.entities.Entries;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface EntriesRepository extends JpaRepository<Entries, Long> {

    @Query(value = "SELECT sum(e.value) FROM Entries e JOIN e.user u " +
            "WHERE u.id = :userId AND e.type =:type GROUP BY u")
    BigDecimal getBalancePerEntriesStatusAndUser(@Param("userId") Long userId, @Param("type") EntryType type);
}
