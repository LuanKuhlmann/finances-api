package io.github.luankuhlmann.myfinances.controller;

import io.github.luankuhlmann.myfinances.dto.EntriesDTO;
import io.github.luankuhlmann.myfinances.dto.StatusUpdateDTO;
import io.github.luankuhlmann.myfinances.exception.BusinessRuleException;
import io.github.luankuhlmann.myfinances.model.entities.Entries;
import io.github.luankuhlmann.myfinances.model.entities.User;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntriesStatus;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntriesType;
import io.github.luankuhlmann.myfinances.service.EntriesService;
import io.github.luankuhlmann.myfinances.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/entries")
public class EntriesController {
    private final EntriesService entriesService;
    private final UserService userService;

    public EntriesController(EntriesService entriesService, UserService userService) {
        this.entriesService = entriesService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity search(
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam("user") Long userId) {

        Entries entriesFilter = new Entries();
        entriesFilter.setDescription(description);
        entriesFilter.setMonth(month);
        entriesFilter.setYear(year);

        Optional<User> user = userService.findById(userId);
        if(user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }else {
            entriesFilter.setUser(user.get());
        }

        List<Entries> entriesList = entriesService.search(entriesFilter);
        return ResponseEntity.ok(entriesList);
    }

    @PostMapping
    public ResponseEntity register(@RequestBody EntriesDTO entriesDTO){
        try {
            Entries entries = converter(entriesDTO);
            entriesService.register(entries);
            return new ResponseEntity(entries, HttpStatus.CREATED);

        }catch (BusinessRuleException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody EntriesDTO entriesDTO) {
        return entriesService.findById(id).map(entity -> {
            try {
                Entries entries = converter(entriesDTO);
                entries.setId(entity.getId());
                entriesService.update(entries);
                return ResponseEntity.ok(entries);
            }catch (BusinessRuleException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Entries not found on database", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/status-update/{id}")
    public ResponseEntity updateStatus(@PathVariable("id") Long id, @RequestBody StatusUpdateDTO statusUpdateDTO) {
        return entriesService.findById(id).map(entity -> {
            EntriesStatus entriesStatus = EntriesStatus.valueOf(statusUpdateDTO.getStatus());

            if (entriesStatus == null) {
                return ResponseEntity.badRequest().body("Status update not possible, inform a valid request");
            }

            try {
                entity.setStatus(entriesStatus);
                entriesService.update(entity);
                return ResponseEntity.ok(entity);
            } catch (BusinessRuleException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Entries not found on database", HttpStatus.BAD_REQUEST));
    }

        @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        return entriesService.findById(id).map(entity -> {
            entriesService.delete(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> new ResponseEntity("Entries not found on database", HttpStatus.BAD_REQUEST));
    }

    private Entries converter(EntriesDTO entriesDTO) {
        Entries entries = new Entries();

        entries.setId(entries.getId());
        entries.setDescription(entriesDTO.getDescription());
        entries.setMonth(entriesDTO.getMonth());
        entries.setYear(entriesDTO.getYear());
        entries.setValue(entriesDTO.getValue());

        entries.setUser(userService.findById(entriesDTO.getUser())
                .orElseThrow( () -> new BusinessRuleException("User not found")));

        if(entriesDTO.getType() != null) {
            entries.setType(EntriesType.valueOf(entriesDTO.getType()));
        }

        if(entriesDTO.getStatus() != null) {
            entries.setStatus(EntriesStatus.valueOf(entriesDTO.getStatus()));
        }

        return entries;
    }
}
