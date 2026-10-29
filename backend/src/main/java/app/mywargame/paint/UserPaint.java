package app.mywargame.paint;

import app.mywargame.user.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "user_paint")
@Getter
@Setter
@NoArgsConstructor
public class UserPaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Paint paint;

    private int quantity = 1;

    @Enumerated(EnumType.STRING)
    private PaintLevel level = PaintLevel.FULL;

    private String notes;

    private Instant createdAt = Instant.now();

    public UserPaint(AppUser user, Paint paint) {
        this.user = user;
        this.paint = paint;
    }
}
