package com.linecorp.linesdk.internal.nwclient;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.internal.JWKSet;
import com.linecorp.linesdk.internal.JWKSet.JWK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

final class JWKSetParser extends JsonToObjectBaseResponseParser<JWKSet> {
    @NonNull
    @Override
    protected JWKSet parseJsonToObject(@NonNull final JSONObject jsonObject) throws JSONException {
        final List<JWK> jwkList = new ArrayList<>();

        final JSONArray keyArray = jsonObject.getJSONArray("keys");
        for (int idx = 0; idx < keyArray.length(); idx++) {
            final JSONObject keyObj = keyArray.getJSONObject(idx);

            final JWK jwk = new JWKSet.JWK.Builder()
                    .keyType(keyObj.getString("kty"))
                    .algorithm(keyObj.getString("alg"))
                    .use(keyObj.getString("use"))
                    .keyId(keyObj.getString("kid"))
                    .curve(keyObj.getString("crv"))
                    .x(keyObj.getString("x"))
                    .y(keyObj.getString("y"))
                    .build();

            jwkList.add(jwk);
        }

        return new JWKSet.Builder()
                .keys(jwkList)
                .build();
    }
}
