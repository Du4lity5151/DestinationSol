/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.assets.json;

import org.json.JSONObject;
import org.terasology.gestalt.assets.Asset;
import org.terasology.gestalt.assets.AssetType;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.module.annotations.RegisterAssetType;

@RegisterAssetType(folderName = {"collisionMeshes", "ships", "factions", "items", "configs", "grounds", "mazes", "asteroids", "schemas"}, factoryClass = JsonFactory.class)
public class Json extends Asset<JsonData> {
    private JsonData jsonData;

    public Json(ResourceUrn urn, AssetType<?, JsonData> assetType, JsonData data) {
        super(urn, assetType);
        reload(data);
    }

    @Override
    protected void doReload(JsonData data) {
        this.jsonData = data;
    }

    public JSONObject getJsonValue() {
        return jsonData.getJsonValue();
    }
}
