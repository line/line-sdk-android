package com.linecorp.linesdk.openchat;

import org.json.JSONException;
import org.json.JSONObject;

public class OpenChatParameters {
    private static int DEFAULT_CATEGORY_ID = 17; // Game
    private OpenChatParameters() {}

    String name;
    String description;
    String creatorDisplayName;
    int categoryId = DEFAULT_CATEGORY_ID;
    Boolean isSearchable = true;

    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", name);
            jsonObject.put("description", description);
            jsonObject.put("creatorDisplayName", creatorDisplayName);
            jsonObject.put("category", categoryId);
            jsonObject.put("allowSearch", isSearchable);
        } catch (JSONException exception) {
            return "{}";
        }

        return jsonObject.toString();
    }

    public static class Builder  {
        private OpenChatParameters openChatParameters = new OpenChatParameters();

        public Builder setName(String name) {
            if (name == null || name.length() > 50) {
                throw new IllegalArgumentException("String size needs to be less or equal to 50");
            }
            openChatParameters.name = name;

            return this;
        }

        public Builder setDescription(String description) {
            if (description == null || description.length() > 200) {
                throw new IllegalArgumentException("String size needs to be less or equal to 200");
            }
            openChatParameters.description = description;

            return this;
        }

        public Builder setCreatorDisplayName(String creatorDisplayName) {
            if (creatorDisplayName == null || creatorDisplayName.length() > 50) {
                throw new IllegalArgumentException("String size needs to be less or equal to 50");
            }
            openChatParameters.creatorDisplayName = creatorDisplayName;

            return this;
        }

        public Builder setCategoryId(int categoryId) {
            openChatParameters.categoryId = categoryId;
            return this;
        }

        public Builder setIsSearchable(Boolean isSearchable) {
            openChatParameters.isSearchable = isSearchable;
            return this;
        }

        public OpenChatParameters build() {
            if (openChatParameters.name == null
                        || openChatParameters.name.isEmpty()
                        || openChatParameters.creatorDisplayName == null
                        || openChatParameters.creatorDisplayName.isEmpty()) {
                throw new IllegalArgumentException("Parameters are not correctly set up");
            }

            return openChatParameters;
        }
    }
}
