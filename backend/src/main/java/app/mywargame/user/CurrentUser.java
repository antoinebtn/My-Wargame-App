package app.mywargame.user;

import org.springframework.security.oauth2.jwt.Jwt;

/** The JWT subject is the user id (see {@link app.mywargame.auth.TokenService}). */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static Long id(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
