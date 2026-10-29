package app.mywargame.paint;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaintBrandRepository extends JpaRepository<PaintBrand, Long> {

    default List<PaintBrand> findAllByName() {
        return findAll(Sort.by("name"));
    }
}
