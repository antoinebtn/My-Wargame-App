package app.mywargame.paint;

public record PaintDto(Long id, String brand, String brandSlug, String name, String code, String set, String hex) {

    static PaintDto of(Paint p) {
        return new PaintDto(p.getId(), p.getBrand().getName(), p.getBrand().getSlug(),
                p.getName(), p.getCode(), p.getSet(), p.getHex());
    }
}
