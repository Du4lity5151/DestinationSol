/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.entitysystem;

import com.google.common.collect.Lists;
import org.destinationsol.assets.Assets;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;
import org.terasology.gestalt.entitysystem.component.store.ArrayComponentStore;
import org.terasology.gestalt.entitysystem.component.store.ComponentStore;
import org.terasology.gestalt.entitysystem.component.store.ConcurrentComponentStore;
import org.terasology.gestalt.entitysystem.entity.EntityIterator;
import org.terasology.gestalt.entitysystem.entity.EntityManager;
import org.terasology.gestalt.entitysystem.entity.manager.CoreEntityManager;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.gestalt.entitysystem.event.EventSystem;
import org.terasology.gestalt.entitysystem.event.impl.EventReceiverMethodSupport;
import org.terasology.gestalt.entitysystem.event.impl.EventSystemImpl;
import org.terasology.gestalt.entitysystem.prefab.GeneratedFromRecipeComponent;
import org.terasology.gestalt.entitysystem.prefab.Prefab;
import org.terasology.gestalt.module.ModuleEnvironment;

import java.util.List;

public class EntitySystemManager {

    private static EntityManager entityManager;
    private static EventSystem eventSystem = new EventSystemImpl();
    private static EventReceiverMethodSupport eventReceiverMethodSupport = new EventReceiverMethodSupport();

    public EntitySystemManager(ModuleEnvironment environment, ComponentManager componentManager){

        List<ComponentStore<?>> stores = Lists.newArrayList();
        for (Class<? extends Component> componentType : environment.getSubtypesOf(Component.class)) {
            stores.add(
                    new ConcurrentComponentStore<>(new ArrayComponentStore<>(componentManager.getType(componentType))));
        }
        stores.add(new ConcurrentComponentStore<>(
                new ArrayComponentStore<>(componentManager.getType(GeneratedFromRecipeComponent.class))));

        entityManager = new CoreEntityManager(stores);

        for (Class<?> eventReceivers : environment.getTypesAnnotatedWith(RegisterEventReceivers.class)) {
            try {
                eventReceiverMethodSupport.register(eventReceivers.newInstance(), eventSystem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Assets.getAssetHelper().list(Prefab.class).forEach(urn -> {
            Assets.getAssetHelper().get(urn, Prefab.class).ifPresent(prefab -> {
                entityManager.createEntity(prefab);
            });
        });
    }

    public EntityIterator getEntities(Component... components) {
        return entityManager.iterate(components);
    }

    public void sendEvent(Event event, Component... components) {
        EntityIterator iterator = getEntities(components);
        while (iterator.next()) {
            eventSystem.send(event, iterator.getEntity());
        }
        eventSystem.processEvents();
    }
}
