package app.mywargame.army;

import app.mywargame.user.AppUserRepository;
import app.mywargame.user.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/armies")
public class ArmyController {

    public record ArmyRequest(@NotBlank @Size(max = 150) String name,
                              @Size(max = 150) String faction,
                              @Size(max = 150) String gameSystem,
                              String description) {
    }

    public record ArmySummary(Long id, String name, String faction, String gameSystem,
                              long miniatureCount, int progress) {
    }

    private final ArmyRepository armies;
    private final AppUserRepository users;

    public ArmyController(ArmyRepository armies, AppUserRepository users) {
        this.armies = armies;
        this.users = users;
    }

    @GetMapping
    public List<ArmySummary> list(@AuthenticationPrincipal Jwt jwt) {
        return armies.findSummaries(CurrentUser.id(jwt)).stream()
                .map(r -> new ArmySummary(r.getId(), r.getName(), r.getFaction(), r.getGameSystem(),
                        r.getMiniatureCount(), (int) Math.round(r.getProgress())))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public ArmySummary create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ArmyRequest req) {
        var army = new Army();
        army.setUser(users.getReferenceById(CurrentUser.id(jwt)));
        army.setName(req.name().strip());
        army.setFaction(req.faction());
        army.setGameSystem(req.gameSystem());
        army.setDescription(req.description());
        army = armies.save(army);
        return new ArmySummary(army.getId(), army.getName(), army.getFaction(), army.getGameSystem(), 0, 0);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        var army = armies.findByIdAndUserId(id, CurrentUser.id(jwt))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        armies.delete(army);
    }
}
