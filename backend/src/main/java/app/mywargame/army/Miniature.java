package app.mywargame.army;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "miniature")
@Getter
@Setter
@NoArgsConstructor
public class Miniature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ArmyUnit unit;

    private String name;

    @Enumerated(EnumType.STRING)
    private MiniatureStage stage = MiniatureStage.UNASSEMBLED;
}
