package com.linecorp.linesdk.internal.nwclient;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.utils.JSONUtils;
import com.linecorp.linesdk.internal.OpenIdDiscoveryDocument;

import org.json.JSONException;
import org.json.JSONObject;

final class OpenIdDiscoveryDocumentParser extends JsonToObjectBaseResponseParser<OpenIdDiscoveryDocument> {
    @NonNull
    @Override
    protected OpenIdDiscoveryDocument parseJsonToObject(@NonNull final JSONObject jsonObject)
            throws JSONException {
        return new OpenIdDiscoveryDocument.Builder()
                .issuer(
                        jsonObject.getString("issuer")
                )
                .authorizationEndpoint(
                        jsonObject.getString("authorization_endpoint")
                )
                .tokenEndpoint(
                        jsonObject.getString("token_endpoint")
                )
                .jwksUri(
                        jsonObject.getString("jwks_uri")
                )
                .responseTypesSupported(
                        JSONUtils.toStringList(
                                jsonObject.getJSONArray("response_types_supported")
                        )
                )
                .subjectTypesSupported(
                        JSONUtils.toStringList(
                                jsonObject.getJSONArray("subject_types_supported")
                        )
                )
                .idTokenSigningAlgValuesSupported(
                        JSONUtils.toStringList(
                                jsonObject.getJSONArray("id_token_signing_alg_values_supported"))
                )
                .build();
    }
}
