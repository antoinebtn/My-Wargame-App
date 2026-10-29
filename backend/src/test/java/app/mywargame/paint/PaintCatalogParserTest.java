package app.mywargame.paint;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaintCatalogParserTest {

    @Test
    void parsesTableWithoutCodeColumn() {
        var brand = PaintCatalogParser.parse("citadel_colour", List.of(
                "# Citadel Colour",
                "![Citadel_Colour](../logos/Citadel_Colour.png \"Citadel_Colour\")",
                "",
                "|Name|Set|R|G|B|Hex|",
                "|---|---|---|---|---|---|",
                "|'Ardcoat|Technical|249|249|249|![#F9F9F9](https://placehold.co/15x15/F9F9F9/F9F9F9.png) `#F9F9F9`|",
                "|Abaddon Black|Base|0|0|0|![#000000](https://placehold.co/15x15/000000/000000.png) `#000000`|"));

        assertThat(brand.name()).isEqualTo("Citadel Colour");
        assertThat(brand.paints()).containsExactly(
                new PaintCatalogParser.ParsedPaint("'Ardcoat", null, "Technical", "#F9F9F9", 249, 249, 249),
                new PaintCatalogParser.ParsedPaint("Abaddon Black", null, "Base", "#000000", 0, 0, 0));
    }

    @Test
    void readsCodeColumnTreatsNullAsMissingAndDropsDuplicates() {
        var brand = PaintCatalogParser.parse("army_painter", List.of(
                "# Army Painter",
                "|Name|Code|Set|R|G|B|Hex|",
                "|---|---|---|---|---|---|---|",
                "|Abomination Gore|WP1401|Warpaints|163|47|38|![#A32F26](x) `#a32f26`|",
                "|Absolution Green|null|Speedpaint Set|34|49|31|![#22311F](x) `#22311F`|",
                "|Absolution Green|null|Speedpaint Set|34|49|31|![#22311F](x) `#22311F`|"));

        assertThat(brand.paints()).containsExactly(
                new PaintCatalogParser.ParsedPaint("Abomination Gore", "WP1401", "Warpaints", "#A32F26", 163, 47, 38),
                new PaintCatalogParser.ParsedPaint("Absolution Green", null, "Speedpaint Set", "#22311F", 34, 49, 31));
    }

    @Test
    void fallsBackToRgbWhenHexIsMissing() {
        var brand = PaintCatalogParser.parse("x", List.of(
                "|Name|Set|R|G|B|Hex|",
                "|---|---|---|---|---|---|",
                "|Red|Base|255|0|16||"));

        assertThat(brand.name()).isEqualTo("x");
        assertThat(brand.paints().getFirst().hex()).isEqualTo("#FF0010");
    }
}
