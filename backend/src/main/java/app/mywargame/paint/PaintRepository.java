package app.mywargame.paint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaintRepository extends JpaRepository<Paint, Long> {

    /** Empty strings mean "no filter" (null parameters trip up PostgreSQL type inference). */
    @Query(value = """
            select p from Paint p join fetch p.brand b
            where (:brand = '' or b.slug = :brand)
              and (:q = '' or lower(p.name) like lower(concat('%', :q, '%'))
                           or lower(p.code) like lower(concat('%', :q, '%')))
            order by b.name, p.name, p.set
            """,
            countQuery = """
            select count(p) from Paint p join p.brand b
            where (:brand = '' or b.slug = :brand)
              and (:q = '' or lower(p.name) like lower(concat('%', :q, '%'))
                           or lower(p.code) like lower(concat('%', :q, '%')))
            """)
    Page<Paint> search(String brand, String q, Pageable pageable);
}
