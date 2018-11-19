package com.linecorp.linesdk.internal;

import android.text.TextUtils;

import java.util.List;

/**
 * JSON Web Key Set [JWK] document. <br></br>
 * Please refer to specification: <a href="https://tools.ietf.org/html/rfc7517">JSON Web Key (JWK)</a>
 */
public class JWKSet {
    private final List<JWK> keys;

    private JWKSet(final Builder builder) {keys = builder.keys;}

    public List<JWK> getKeys() {
        return keys;
    }

    public JWK getJWK(final String keyId) {
        for (final JWK jwk : keys) {
            if (TextUtils.equals(jwk.getKeyId(), keyId)) {
                return jwk;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "JWKSet{" +
               "keys=" + keys +
               '}';
    }

    public static final class Builder {
        private List<JWK> keys;

        public Builder() {}

        public Builder keys(final List<JWK> keys) {
            this.keys = keys;
            return this;
        }

        public JWKSet build() {
            return new JWKSet(this);
        }
    }

    public static class JWK {
        private final String keyType;

        private final String algorithm;

        private final String use;

        private final String keyId;

        private final String curve;

        private final String x;

        private final String y;

        private JWK(final Builder builder) {
            keyType = builder.keyType;
            algorithm = builder.algorithm;
            use = builder.use;
            keyId = builder.keyId;
            curve = builder.curve;
            x = builder.x;
            y = builder.y;
        }

        public String getKeyType() {
            return keyType;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public String getUse() {
            return use;
        }

        public String getKeyId() {
            return keyId;
        }

        public String getCurve() {
            return curve;
        }

        public String getX() {
            return x;
        }

        public String getY() {
            return y;
        }

        @Override
        public String toString() {
            return "JWK{" +
                   "keyType='" + keyType + '\'' +
                   ", algorithm='" + algorithm + '\'' +
                   ", use='" + use + '\'' +
                   ", keyId='" + keyId + '\'' +
                   ", curve='" + curve + '\'' +
                   ", x='" + x + '\'' +
                   ", y='" + y + '\'' +
                   '}';
        }

        public static final class Builder {
            private String keyType;
            private String algorithm;
            private String use;
            private String keyId;
            private String curve;
            private String x;
            private String y;

            public Builder() {}

            public Builder keyType(final String keyType) {
                this.keyType = keyType;
                return this;
            }

            public Builder algorithm(final String algorithm) {
                this.algorithm = algorithm;
                return this;
            }

            public Builder use(final String use) {
                this.use = use;
                return this;
            }

            public Builder keyId(final String keyId) {
                this.keyId = keyId;
                return this;
            }

            public Builder curve(final String curve) {
                this.curve = curve;
                return this;
            }

            public Builder x(final String x) {
                this.x = x;
                return this;
            }

            public Builder y(final String y) {
                this.y = y;
                return this;
            }

            public JWK build() {
                return new JWK(this);
            }
        }
    }
}
