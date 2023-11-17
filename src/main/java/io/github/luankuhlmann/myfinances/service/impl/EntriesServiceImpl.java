package io.github.luankuhlmann.myfinances.service.impl;

import io.github.luankuhlmann.myfinances.exception.BusinessRuleException;
import io.github.luankuhlmann.myfinances.model.entities.Entries;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntriesStatus;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntriesType;
import io.github.luankuhlmann.myfinances.model.repositories.EntriesRepository;
import io.github.luankuhlmann.myfinances.service.EntriesService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EntriesServiceImpl implements EntriesService {
    private final EntriesRepository entriesRepository;

    @Autowired
    public EntriesServiceImpl(EntriesRepository entriesRepository) {
        this.entriesRepository = entriesRepository;
    }

    @Override
    @Transactional
    public Entries register(Entries entries) {
        validate(entries);
        entries.setStatus(EntriesStatus.PENDING);
        return entriesRepository.save(entries);
    }

    @Override
    @Transactional
    public Entries update(Entries entries) {
        Objects.requireNonNull(entries.getId());
        validate(entries);
        return entriesRepository.save(entries);
    }

    @Override
    @Transactional
    public void delete(Entries entries) {
        Objects.requireNonNull(entries.getId());
        entriesRepository.delete(entries);
    }

    @Override
    public List<Entries> search(Entries entriesFilter) {
        Example<Entries> example = Example.of(entriesFilter,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return entriesRepository.findAll(example);
    }

    @Override
    public void updateStatus(Entries entries, EntriesStatus entriesStatus) {
        entries.setStatus(entriesStatus);
        update(entries);
    }

    @Override
    public void validate(Entries entries) {
        if(entries.getDescription() == null || entries.getDescription().trim().equals("")) {
            throw new BusinessRuleException("Inform a valid description");
        }

        if(entries.getMonth() == null || entries.getMonth() < 1 || entries.getMonth() > 12) {
            throw new BusinessRuleException("Inform a valid month");
        }

        if(entries.getYear() == null || entries.getYear().toString().length() != 4) {
            throw new BusinessRuleException("Inform a valid year");
        }

        if(entries.getUser() == null || entries.getUser().getId() == null) {
            throw new BusinessRuleException("Inform a valid user");
        }

        if(entries.getValue() == null || entries.getValue().compareTo(BigDecimal.ZERO) < 1) {
            throw new BusinessRuleException("Inform a valid value");
        }

        if(entries.getType() == null) {
            throw new BusinessRuleException("Inform a entries type");
        }
    }

    @Override
    public Optional<Entries> findById(Long id) {
        return entriesRepository.findById(id);
    }

    @Override
    public BigDecimal getBalancePerUser(Long id) {
        BigDecimal revenue = entriesRepository.getBalancePerEntriesStatusAndUser(id, EntriesType.REVENUE);
        BigDecimal expense = entriesRepository.getBalancePerEntriesStatusAndUser(id, EntriesType.EXPENSE);

        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }
        if (expense == null) {
            revenue = BigDecimal.ZERO;
        }

        return revenue.subtract(expense);
    }
}
