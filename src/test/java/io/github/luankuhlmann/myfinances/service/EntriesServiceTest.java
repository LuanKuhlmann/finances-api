package io.github.luankuhlmann.myfinances.service;

import io.github.luankuhlmann.myfinances.exception.BusinessRuleException;
import io.github.luankuhlmann.myfinances.model.entities.Entries;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntriesStatus;
import io.github.luankuhlmann.myfinances.model.repositories.EntriesRepository;
import io.github.luankuhlmann.myfinances.model.repositories.EntriesRepositoryTest;
import io.github.luankuhlmann.myfinances.service.impl.EntriesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class EntriesServiceTest {

    @SpyBean
    EntriesServiceImpl entriesService;

    @MockBean
    EntriesRepository entriesRepository;

    @Test
    public void shallRegisterAEntrie(){
        Entries entriesToSave = EntriesRepositoryTest.createEntries();
        doNothing().when(entriesService).validate(entriesToSave);

        Entries savedEntries = EntriesRepositoryTest.createEntries();
        savedEntries.setId(entriesToSave.getId());
        savedEntries.setStatus(EntriesStatus.PENDING);
        when(entriesRepository.save(entriesToSave)).thenReturn(entriesToSave);

        Entries entries = entriesService.register(entriesToSave);

        assertEquals(entries.getId(), savedEntries.getId());
        assertEquals(entries.getStatus(), savedEntries.getStatus());
    }

    @Test
    public void shallNotRegisterAEntrieWhenTheresAValidationError() {
        Entries entriesToSave = EntriesRepositoryTest.createEntries();
        doThrow(BusinessRuleException.class).when(entriesService).validate(entriesToSave);

        assertThrows(BusinessRuleException.class, () -> entriesService.register(entriesToSave));

        verify(entriesRepository, never()).save(entriesToSave);
    }

    public void shallUpdateAEntrie(){
        Entries savedEntries = EntriesRepositoryTest.createEntries();
        savedEntries.setId(1l);
        savedEntries.setStatus(EntriesStatus.PENDING);
        when(entriesRepository.save(entriesToSave)).thenReturn(entriesToSave);

        Entries entries = entriesService.register(entriesToSave);
    }
}