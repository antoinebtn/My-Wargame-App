package app.mywargame.paint;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "paint_brand")
@Getter
@Setter
@NoArgsConstructor
public class PaintBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String slug;

    public PaintBrand(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
