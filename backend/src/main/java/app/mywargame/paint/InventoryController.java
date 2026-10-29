package app.mywargame.paint;

import app.mywargame.user.AppUserRepository;
import app.mywargame.user.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    public record AddPaintRequest(@NotNull Long paintId, @Min(1) Integer quantity) {
    }

    public record InventoryItem(Long id, PaintDto paint, int quantity, PaintLevel level, String notes) {
        static InventoryItem of(UserPaint up) {
            return new InventoryItem(up.getId(), PaintDto.of(up.getPaint()), up.getQuantity(), up.getLevel(), up.getNotes());
        }
    }

    private final UserPaintRepository inventory;
    private final PaintRepository paints;
    private final AppUserRepository users;

    public InventoryController(UserPaintRepository inventory, PaintRepository paints, AppUserRepository users) {
        this.inventory = inventory;
        this.paints = paints;
        this.users = users;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<InventoryItem> list(@AuthenticationPrincipal Jwt jwt) {
        return inventory.findInventory(CurrentUser.id(jwt)).stream().map(InventoryItem::of).toList();
    }

    /** Adds a pot, or bumps the quantity when the paint is already owned. */
    @PostMapping
    @Transactional
    public InventoryItem add(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AddPaintRequest req) {
        Long userId = CurrentUser.id(jwt);
        int quantity = req.quantity() == null ? 1 : req.quantity();
        var item = inventory.findByUserIdAndPaintId(userId, req.paintId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    return existing;
                })
                .orElseGet(() -> {
                    var paint = paints.findById(req.paintId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Peinture inconnue"));
                    var created = new UserPaint(users.getReferenceById(userId), paint);
                    created.setQuantity(quantity);
                    return inventory.save(created);
                });
        return InventoryItem.of(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void remove(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        var item = inventory.findByIdAndUserId(id, CurrentUser.id(jwt))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        inventory.delete(item);
    }
}
