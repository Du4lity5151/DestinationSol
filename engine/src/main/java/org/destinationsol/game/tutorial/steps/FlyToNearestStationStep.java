/*
 * Copyright 2023 The Terasology Foundation
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

package org.destinationsol.game.tutorial.steps;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.ui.nui.screens.TutorialScreen;

public class FlyToNearestStationStep extends FlyToWaypointStep {
    public FlyToNearestStationStep(TutorialScreen tutorialScreen, SolGame game, String message) {
        super(tutorialScreen, game, Vector2.Zero, message);
    }

    @Override
    public void start() {
        Vector2 heroPosition = game.getHero().getPosition();
        float nearestStationDistance = Integer.MAX_VALUE;
        Vector2 nearestStationPosition = new Vector2(Integer.MAX_VALUE, Integer.MAX_VALUE);

        for (SolObject solObject : game.getObjectManager().getObjects()) {
            if (solObject instanceof SolShip &&
                    ((SolShip) solObject).getHull().getHullConfig().getType() == HullConfig.Type.STATION) {
                float stationDistance = solObject.getPosition().dst(heroPosition);
                if (stationDistance < nearestStationDistance) {
                    nearestStationDistance = stationDistance;
                    nearestStationPosition.set(solObject.getPosition());
                    nearestStationPosition.add(((SolShip) solObject).getHull().getHullConfig().getForceBeaconPositions().get(0));
                }
            }
        }
        for (FarShip farShip : game.getObjectManager().getFarShips()) {
            if (farShip.getHullConfig().getType() == HullConfig.Type.STATION) {
                float stationDistance = farShip.getPosition().dst(heroPosition);
                if (stationDistance < nearestStationDistance) {
                    nearestStationDistance = stationDistance;
                    nearestStationPosition.set(farShip.getPosition());
                    nearestStationPosition.add(farShip.getHullConfig().getForceBeaconPositions().get(0));
                }
            }
        }
        waypointPosition = nearestStationPosition;
        super.start();
    }
}
