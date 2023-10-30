package io.github.luankuhlmann.myfinances.model.entities;

import io.github.luankuhlmann.myfinances.model.entities.enums.EntriesStatus;
import io.github.luankuhlmann.myfinances.model.entities.enums.EntriesType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "entries", schema = "finance")
@Data
@NoArgsConstructor
public class Entries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private Integer month;

    private Integer year;

    private BigDecimal value;

    @Enumerated(value = EnumType.STRING)
    private EntriesType type;

    @Enumerated(value = EnumType.STRING)
    private EntriesStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "register_date")
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate registerDate;

}
