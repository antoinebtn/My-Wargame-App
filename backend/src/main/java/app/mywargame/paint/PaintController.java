package app.mywargame.paint;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaintController {

    public record BrandDto(Long id, String name, String slug) {
    }

    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    }

    private final PaintRepository paints;
    private final PaintBrandRepository brands;

    public PaintController(PaintRepository paints, PaintBrandRepository brands) {
        this.paints = paints;
        this.brands = brands;
    }

    @GetMapping("/brands")
    public List<BrandDto> brands() {
        return brands.findAllByName().stream()
                .map(b -> new BrandDto(b.getId(), b.getName(), b.getSlug()))
                .toList();
    }

    @GetMapping("/paints")
    public PageResponse<PaintDto> search(@RequestParam(defaultValue = "") String brand,
                                         @RequestParam(defaultValue = "") String q,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "50") int size) {
        var result = paints.search(brand.strip(), q.strip(),
                PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, 200)));
        return new PageResponse<>(result.map(PaintDto::of).getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }
}
