package io.github.luankuhlmann.myfinances.service;

import io.github.luankuhlmann.myfinances.exception.BusinessRuleException;
import io.github.luankuhlmann.myfinances.model.entities.Entries;
import io.github.luankuhlmann.myfinances.model.entities.User;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntryStatus;
import io.github.luankuhlmann.myfinances.model.repositories.EntriesRepository;
import io.github.luankuhlmann.myfinances.model.repositories.EntriesRepositoryTest;
import io.github.luankuhlmann.myfinances.service.impl.EntriesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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
    public void shallRegisterAEntry(){
        Entries entriesToSave = EntriesRepositoryTest.createEntries();
        doNothing().when(entriesService).validate(entriesToSave);

        Entries savedEntries = EntriesRepositoryTest.createEntries();
        savedEntries.setId(entriesToSave.getId());
        savedEntries.setStatus(EntryStatus.PENDING);
        when(entriesRepository.save(entriesToSave)).thenReturn(entriesToSave);

        Entries entries = entriesService.register(entriesToSave);

        assertEquals(entries.getId(), savedEntries.getId());
        assertEquals(entries.getStatus(), savedEntries.getStatus());
    }

    @Test
    public void shallNotRegisterAEntryWhenTheresAValidationError() {
        Entries entriesToSave = EntriesRepositoryTest.createEntries();
        doThrow(BusinessRuleException.class).when(entriesService).validate(entriesToSave);

        assertThrows(BusinessRuleException.class, () -> entriesService.register(entriesToSave));

        verify(entriesRepository, never()).save(entriesToSave);
    }

    @Test
    public void shallUpdateAEntry(){
        Entries entries = EntriesRepositoryTest.createEntries();
        entries.setId(1l);
        entries.setStatus(EntryStatus.PENDING);

        doNothing().when(entriesService).validate(entries);

        when(entriesRepository.save(entries)).thenReturn(entries);

        entriesService.update(entries);

        verify(entriesRepository, times(1)).save(entries);
    }

    @Test
    public void shallNotUpdateAEntryThatWasNotRegisteredYet() {
        Entries entries = EntriesRepositoryTest.createEntries();

        assertThrows(NullPointerException.class, () -> entriesService.update(entries));
        verify(entriesRepository, never()).save(entries);
    }

    @Test
    public void shallDeleteAEntry() {
        Entries entries = EntriesRepositoryTest.createEntries();
        entries.setId(1l);

        entriesService.delete(entries);

        verify(entriesRepository).delete(entries);
    }

    @Test
    public void shallThrowErrorWhenTryingToDeleteANonRegisteredEntry() {
        Entries entries = EntriesRepositoryTest.createEntries();

        assertThrows(NullPointerException.class, () -> entriesService.delete(entries));

        verify(entriesRepository, never()).delete(entries);
    }

    @Test
    public void shallFilterEntries() {
        Entries entries = EntriesRepositoryTest.createEntries();
        entries.setId(1l);

        List<Entries> list = Arrays.asList(entries);
        when(entriesRepository.findAll(any(Example.class))).thenReturn(list);

        List<Entries> result = entriesService.search(entries);

        assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .contains(entries);
    }

    @Test
    public void shallUpdateEntryStatus() {
        Entries entries = EntriesRepositoryTest.createEntries();
        entries.setId(1l);
        entries.setStatus(EntryStatus.PENDING);

        EntryStatus newStatus = EntryStatus.COMPLETED;
        doReturn(entries).when(entriesService).update(entries);

        entriesService.updateStatus(entries, newStatus);

        assertThat(entries.getStatus()).isEqualTo(newStatus);
        verify(entriesService).update(entries);
    }

    @Test
    public void shallFindEntryByID() {
        Long id = 1l;

        Entries entries = EntriesRepositoryTest.createEntries();
        entries.setId(1l);

        when(entriesRepository.findById(id)).thenReturn(Optional.of(entries));

        Optional<Entries> result = entriesService.findById(id);

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    public void shallReturnEmptyWhenEntryNotFound() {
        Long id = 1l;

        Entries entries = EntriesRepositoryTest.createEntries();
        entries.setId(1l);

        when(entriesRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Entries> result = entriesService.findById(id);

        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void shallThrowExceptionWhenValidatingAEntry() {
        Entries entries = new Entries();

        Throwable error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a valid description");

        entries.setDescription("");

        error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a valid description");

        entries.setDescription("Income");

        error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a valid month");

        entries.setMonth(0);

        error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a valid month");

        entries.setMonth(13);

        error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a valid month");

        entries.setMonth(6);

        error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a valid year");

        entries.setYear(202);

        error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a valid year");

        entries.setYear(2023);

        error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a valid user");

        entries.setUser(new User());

        error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a valid user");

        entries.getUser().setId(1l);

        error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a valid value");

        entries.setValue(BigDecimal.ZERO);

        error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a valid value");

        entries.setValue(BigDecimal.valueOf(1));

        error = catchThrowable(() -> entriesService.validate(entries));
        assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Inform a entry type");
    }
}