package app.mywargame.paint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/** Seeds the paint catalogue from classpath:catalog/*.md on first start (when the table is empty). */
@Slf4j
@Component
public class PaintCatalogImporter implements ApplicationRunner {

    private final PaintRepository paints;
    private final PaintBrandRepository brands;
    private final JdbcTemplate jdbc;

    public PaintCatalogImporter(PaintRepository paints, PaintBrandRepository brands, JdbcTemplate jdbc) {
        this.paints = paints;
        this.brands = brands;
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        if (paints.count() > 0) {
            return;
        }
        Resource[] files = new PathMatchingResourcePatternResolver().getResources("classpath:catalog/*.md");
        int total = 0;
        for (Resource file : files) {
            String slug = file.getFilename().replace(".md", "").toLowerCase(Locale.ROOT);
            var parsed = PaintCatalogParser.parse(slug, file.getContentAsString(StandardCharsets.UTF_8).lines().toList());
            var brand = brands.save(new PaintBrand(parsed.name(), slug));
            jdbc.batchUpdate(
                    "insert into paint (brand_id, name, code, paint_set, hex, r, g, b) values (?, ?, ?, ?, ?, ?, ?, ?)",
                    parsed.paints(), 500, (ps, p) -> {
                        ps.setLong(1, brand.getId());
                        ps.setString(2, p.name());
                        ps.setString(3, p.code());
                        ps.setString(4, p.set());
                        ps.setString(5, p.hex());
                        ps.setInt(6, p.r());
                        ps.setInt(7, p.g());
                        ps.setInt(8, p.b());
                    });
            total += parsed.paints().size();
        }
        log.info("Imported {} paints from {} brands", total, files.length);
    }
}
