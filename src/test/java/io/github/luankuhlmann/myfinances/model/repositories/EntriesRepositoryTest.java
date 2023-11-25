package io.github.luankuhlmann.myfinances.model.repositories;

import io.github.luankuhlmann.myfinances.model.entities.Entries;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntryStatus;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntryType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//Do not allow that spring reconfigure application-test.properties
public class EntriesRepositoryTest {

    @Autowired
    EntriesRepository entriesRepository;

    @Autowired
    TestEntityManager entityManager;

    public static Entries createEntries() {
        Entries entries = new Entries();

        entries.setYear(2023);
        entries.setMonth(1);
        entries.setDescription("Some entries");
        entries.setValue(BigDecimal.valueOf(10));
        entries.setType(EntryType.REVENUE);
        entries.setStatus(EntryStatus.PENDING);
        entries.setRegisterDate(LocalDate.now());

        return entries;
    }

    private Entries persistEntries() {
        Entries entries = createEntries();
        entries = entityManager.persist(entries);
        return entries;
    }

    @Test
    public void shallSaveAEntries() {
        Entries entries = createEntries();

        Entries savedEntries =  entriesRepository.save(entries);

        Assertions.assertNotNull(savedEntries);
    }

    @Test
    public void shallDeleteAEntries() {
        Entries entries = persistEntries();

        entries = entityManager.find(Entries.class, entries.getId());

        entriesRepository.delete(entries);

        Entries deletedEntries = entityManager.find(Entries.class, entries.getId());

        assertNull(deletedEntries);
    }

    @Test
    public void shallUpdateAEntries() {
        Entries entries = persistEntries();

        entries.setYear(2022);
        entries.setDescription("Update description");
        entries.setStatus(EntryStatus.CANCELED);

        entriesRepository.save(entries);

        Entries updatedEntries = entityManager.find(Entries.class, entries.getId());

        assertEquals(updatedEntries.getYear(), 2022);
        assertEquals(updatedEntries.getDescription(), "Update description");
        assertEquals(updatedEntries.getStatus(), EntryStatus.CANCELED);
    }

    @Test
    public void shallSearchEntriesById() {
        Entries entries = persistEntries();

        Optional<Entries> foundEntries = entriesRepository.findById(entries.getId());

        assertTrue(foundEntries.isPresent());
    }
}