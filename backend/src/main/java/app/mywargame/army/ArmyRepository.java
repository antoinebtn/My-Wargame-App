package app.mywargame.army;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArmyRepository extends JpaRepository<Army, Long> {

    interface ArmySummaryRow {
        Long getId();

        String getName();

        String getFaction();

        String getGameSystem();

        long getMiniatureCount();

        double getProgress();
    }

    /** Progress = average stage of all miniatures, 0 (all unassembled) to 100 (all based). */
    @Query(nativeQuery = true, value = """
            select a.id, a.name, a.faction, a.game_system as "gameSystem",
                   count(m.id) as "miniatureCount",
                   (coalesce(avg(case m.stage
                       when 'UNASSEMBLED' then 0 when 'ASSEMBLED' then 1 when 'PRIMED' then 2
                       when 'PAINTED' then 3 when 'BASED' then 4 end), 0) * 25)::float8 as progress
            from army a
            left join army_unit u on u.army_id = a.id
            left join miniature m on m.unit_id = u.id
            where a.user_id = :userId
            group by a.id
            order by a.created_at desc
            """)
    List<ArmySummaryRow> findSummaries(Long userId);

    Optional<Army> findByIdAndUserId(Long id, Long userId);
}
