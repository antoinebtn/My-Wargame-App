package app.mywargame.paint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserPaintRepository extends JpaRepository<UserPaint, Long> {

    @Query("""
            select up from UserPaint up join fetch up.paint p join fetch p.brand b
            where up.user.id = :userId
            order by b.name, p.name
            """)
    List<UserPaint> findInventory(Long userId);

    Optional<UserPaint> findByUserIdAndPaintId(Long userId, Long paintId);

    Optional<UserPaint> findByIdAndUserId(Long id, Long userId);
}
