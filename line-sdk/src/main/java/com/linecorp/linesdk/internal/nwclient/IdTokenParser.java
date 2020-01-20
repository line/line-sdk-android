package com.linecorp.linesdk.internal.nwclient;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.LineIdToken;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;

final class IdTokenParser {
    private static final String TAG = "IdTokenParser";

    // allowed clock skew: 10000 days
    // which means, skip timestamp check here, since we will check it later in IdTokenValidator
    private static final long ALLOWED_CLOCK_SKEW_SECONDS = TimeUnit.DAYS.toSeconds(10000);

    private IdTokenParser() { }

    public static LineIdToken parse(final String idTokenStr, final SigningKeyResolver signingKeyResolver)
            throws Exception {
        if (TextUtils.isEmpty(idTokenStr)) {
            return null;
        }

        try {
            final Claims claims = Jwts.parser()
                                      .setAllowedClockSkewSeconds(ALLOWED_CLOCK_SKEW_SECONDS)
                                      .setSigningKeyResolver(signingKeyResolver)
                                      .parseClaimsJws(idTokenStr)
                                      .getBody();

            return buildIdToken(idTokenStr, claims);
        } catch (final Exception e) {
            Log.e(TAG, "failed to parse IdToken: " + idTokenStr, e);
            throw e;
        }
    }

    @NonNull
    private static LineIdToken buildIdToken(final String idTokenStr, final Claims claims) {
        return new LineIdToken.Builder()
                .rawString(idTokenStr)
                .issuer(claims.getIssuer())
                .subject(claims.getSubject())
                .audience(claims.getAudience())
                .expiresAt(claims.getExpiration())
                .issuedAt(claims.getIssuedAt())
                .authTime(claims.get("auth_time", Date.class))
                .nonce(claims.get("nonce", String.class))
                .amr(claims.get("amr", List.class))
                .name(claims.get("name", String.class))
                .picture(claims.get("picture", String.class))
                .phoneNumber(claims.get("phone_number", String.class))
                .email(claims.get("email", String.class))
                .gender(claims.get("gender", String.class))
                .birthdate(claims.get("birthdate", String.class))
                .address(buildAddress(claims))
                .givenName(claims.get("given_name", String.class))
                .givenNamePronunciation(claims.get("given_name_pronunciation", String.class))
                .middleName(claims.get("middle_name", String.class))
                .familyName(claims.get("family_name", String.class))
                .familyNamePronunciation(claims.get("family_name_pronunciation", String.class))
                .build();
    }

    private static LineIdToken.Address buildAddress(final Claims claims) {
        final Map<String, String> addressClaims = claims.get("address", Map.class);

        if (addressClaims == null) {
            return null;
        }

        return new LineIdToken.Address.Builder()
                .streetAddress(addressClaims.get("street_address"))
                .locality(addressClaims.get("locality"))
                .region(addressClaims.get("region"))
                .postalCode(addressClaims.get("postal_code"))
                .country(addressClaims.get("country"))
                .build();
    }
}
