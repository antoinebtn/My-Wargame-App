package app.mywargame.army;

import app.mywargame.user.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "army")
@Getter
@Setter
@NoArgsConstructor
public class Army {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    private String name;

    private String faction;

    private String gameSystem;

    private String description;

    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "army", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<ArmyUnit> units = new ArrayList<>();
}
