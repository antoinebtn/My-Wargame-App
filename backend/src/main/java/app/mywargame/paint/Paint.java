package app.mywargame.paint;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Read-only catalogue entry; rows are inserted by {@link PaintCatalogImporter}. */
@Entity
@Table(name = "paint")
@Getter
@NoArgsConstructor
public class Paint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PaintBrand brand;

    private String name;

    private String code;

    @Column(name = "paint_set")
    private String set;

    private String hex;

    private int r;

    private int g;

    private int b;
}
