package com.linecorp.linesdk.internal.nwclient;

import androidx.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.internal.JWKSet;
import com.linecorp.linesdk.internal.JWKSet.JWK;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.jce.spec.ECNamedCurveSpec;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.security.SecurityException;

public class OpenIdSigningKeyResolver implements SigningKeyResolver {
    private static final String TAG = "OpenIdSignKeyResolver";

    @NonNull
    private final LineAuthenticationApiClient apiClient;

    public OpenIdSigningKeyResolver(@NonNull final LineAuthenticationApiClient apiClient) {
        this.apiClient = apiClient;
    }

    private static ECPublicKey generateECPublicKey(final JWK jwk) {
        final BigInteger x = decodeBase64(jwk.getX());
        final BigInteger y = decodeBase64(jwk.getY());

        try {
            final KeyFactory factory = KeyFactory.getInstance("EC");

            final ECPoint point = new ECPoint(x, y);
            final ECNamedCurveParameterSpec paramSpec = ECNamedCurveTable.getParameterSpec(jwk.getCurve());
            final ECNamedCurveSpec params = new ECNamedCurveSpec(jwk.getCurve(),
                                                                 paramSpec.getCurve(),
                                                                 paramSpec.getG(),
                                                                 paramSpec.getN());

            final ECPublicKeySpec spec = new ECPublicKeySpec(point, params);

            return (ECPublicKey) factory.generatePublic(spec);
        } catch (final NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.e(TAG, "failed to generate EC Public Key from JWK: " + jwk, e);

            return null;
        }
    }

    private static BigInteger decodeBase64(final String base64Str) {
        final byte[] bytes = Base64.decode(base64Str, Base64.URL_SAFE);

        return new BigInteger(1, // "x" and "y" coordinates are always positive value
                              bytes);
    }

    @Override
    public Key resolveSigningKey(final JwsHeader header, final Claims claims) {
        return resolveSigningKey(header);
    }

    @Override
    public Key resolveSigningKey(final JwsHeader header, final String plaintext) {
        return resolveSigningKey(header);
    }

    private Key resolveSigningKey(final JwsHeader header) {
        final LineApiResponse<JWKSet> response = apiClient.getJWKSet();
        if (!response.isSuccess()) {
            Log.e(TAG, "failed to get LINE JSON Web Key Set [JWK] document.");

            return null;
        }

        final JWKSet jwkSet = response.getResponseData();

        final String keyId = header.getKeyId();
        final JWK jwk = jwkSet.getJWK(keyId);
        if (jwk == null) {
            Log.e(TAG, "failed to find Key by Id: " + keyId);

            return null;
        }

        final String algorithm = header.getAlgorithm();
        final SignatureAlgorithm alg = SignatureAlgorithm.forName(algorithm);
        if (alg.isEllipticCurve()) {
            return generateECPublicKey(jwk);
        }

        throw new SecurityException("Unsupported signature algorithm '" + algorithm + '\'');
    }
}
