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
package org.destinationsol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.game.faction.Faction;
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.BeaconDestProvider;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.input.UiControlledPilot;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.ui.Waypoint;

class PlayerCreator {

    private static final int TUTORIAL_MONEY_AMOUNT = 200;
    private static final int SHIP_SPAWN_ANGLE = 0;
    private static final int SHIP_SPAWN_ROTATION_SPEED = 0;
    private static final boolean SHIP_SPAWN_HAS_REPAIRER = true;
    private static final int NUMBER_OF_TUTORIAL_ITEM_ADD_ATTEMPTS = 50;
    private static final float MAX_NUMBER_OF_TUTORIAL_ITEM_GROUPS = 1.5f * Const.ITEM_GROUPS_PER_PAGE;

    Hero createPlayer(ShipConfig shipConfig, boolean shouldSpawnOnGalaxySpawnPosition, RespawnState respawnState, SolGame game, boolean isMouseControl, boolean isNewShip) {
        Vector2 position = findPlayerSpawnPosition(shipConfig, shouldSpawnOnGalaxySpawnPosition, game);
        game.getCam().setPos(position);
        if (isMouseControl) {
            game.getBeaconHandler().init(game, position);
        }
        Hero hero = configureAndCreateHero(shipConfig, respawnState, game, isMouseControl, isNewShip, position);
        game.getObjectManager().addObjDelayed(hero.getShip());
        game.getObjectManager().resetDelays();
        return hero;
    }

    private Hero configureAndCreateHero(ShipConfig shipConfig, RespawnState respawnState, SolGame game, boolean isMouseControl, boolean isNewShip, Vector2 position) {
        Faction faction = game.getFactionMan().getPlayerFaction();
        Pilot pilot = createPilot(game, faction, isMouseControl);
        float money = grantPlayerMoney(shipConfig, respawnState, game);
        HullConfig hull = findHullConfig(shipConfig, respawnState);
        String items = findItems(shipConfig, respawnState);
        String waypoints = findWaypoints(shipConfig, respawnState);
        boolean giveAmmo = shouldGiveAmmo(respawnState, isNewShip);
        Hero hero = createHero(position, pilot, money, hull, items, giveAmmo, game);
        addAndEquipItems(hero, respawnState, game);
        addWaypoints(hero, waypoints, respawnState, game);
        return hero;
    }

    private void addAndEquipItems(Hero hero, RespawnState respawnState, SolGame game) {
        ItemContainer itemContainer = hero.getItemContainer();
        if (!respawnState.getRespawnItems().isEmpty()) {
            addAndEquipRespawnItems(hero, respawnState, itemContainer, game);
        }
        itemContainer.markAllAsSeen();
    }

    private void addWaypoints(Hero hero, String waypoints, RespawnState respawnState, SolGame game) {
        if (waypoints == "") {
            for (Waypoint waypoint : respawnState.getRespawnWaypoints()) {
                hero.getWaypoints().add(waypoint);
            }
            return;
        }
        String[] waypointStrings = waypoints.split(" ");

        for (String string : waypointStrings) {
            String[] values = string.split("_");
            if (values[0] == "") {
                continue;
            }
            Vector2 waypointPosition = new Vector2().fromString(values[0]);
            String[] colors = values[1].split(",");
            Color color = new Color(Float.valueOf(colors[0]),Float.valueOf(colors[1]),Float.valueOf(colors[2]),1.0f);
            Waypoint waypoint = new Waypoint(waypointPosition, color, game.getMapDrawer().getWaypointTexture());
            hero.addWaypoint(waypoint);
            game.getObjectManager().addObjDelayed(waypoint);
        }
    }

    private boolean isNoGunAndHasIcon(SolItem it, SolGame game) {
        return !(it instanceof Gun) && it.getIcon(game) != null;
    }

    private void addAndEquipRespawnItems(Hero hero, RespawnState respawnState, ItemContainer itemContainer, SolGame game) {
        for (SolItem item : respawnState.getRespawnItems()) {
            itemContainer.add(item);
            ensurePreviouslyEquippedItemStaysEquipped(item, hero, game);
        }
    }

    private void ensurePreviouslyEquippedItemStaysEquipped(SolItem item, Hero hero, SolGame game) {
        if (item.isEquipped() > 0) {
            if (item instanceof Gun) {
                hero.maybeEquip(game, item, item.isEquipped() == 2, true);
            } else {
                hero.maybeEquip(game, item, true);
            }
        }
    }

    private Hero createHero(Vector2 position, Pilot pilot, float money, HullConfig hull, String items, boolean giveAmmo, SolGame game) {
        FarShip farShip = game.getShipBuilder().buildNewFar(game,
                new Vector2(position),
                null,
                SHIP_SPAWN_ANGLE,
                SHIP_SPAWN_ROTATION_SPEED,
                pilot,
                items,
                hull,
                null,
                SHIP_SPAWN_HAS_REPAIRER,
                money,
                null,
                giveAmmo);
        return new Hero(farShip.toObject(game), game);
    }

    private boolean shouldGiveAmmo(RespawnState respawnState, boolean isNewShip) {
        return isNewShip && respawnState.getRespawnItems().isEmpty();
    }

    private String findItems(ShipConfig shipConfig, RespawnState respawnState) {
        if (!respawnState.getRespawnItems().isEmpty()) {
            return "";
        }
        return shipConfig.getItems();
    }

    private String findWaypoints(ShipConfig shipConfig, RespawnState respawnState) {
        if (!respawnState.getRespawnWaypoints().isEmpty()) {
            return "";
        }
        return shipConfig.getWaypoints();
    }

    private HullConfig findHullConfig(ShipConfig shipConfig, RespawnState respawnState) {
        if (respawnState.getRespawnHull() != null) {
            return respawnState.getRespawnHull();
        }
        return shipConfig.getHull();
    }

    private float grantPlayerMoney(ShipConfig shipConfig, RespawnState respawnState, SolGame game) {
        if (respawnState.getRespawnMoney() != 0) {
            return respawnState.getRespawnMoney();
        }
        if (game.isTutorial()) {
            return TUTORIAL_MONEY_AMOUNT;
        }
        return shipConfig.getMoney();
    }

    private Pilot createPilot(SolGame game, Faction faction, boolean isMouseControl) {
        if (isMouseControl) {
            return new AiPilot(new BeaconDestProvider(), true, faction, false, "you", Const.AI_DET_DIST);
        } else {
            return new UiControlledPilot(faction, game.getScreens().getOldMainGameScreen().getShipControl());
        }
    }

    private Vector2 findPlayerSpawnPosition(ShipConfig shipConfig, boolean shouldSpawnOnGalaxySpawnPosition, SolGame game) {
        if (shouldSpawnOnGalaxySpawnPosition) {
            return game.getGalaxyFiller().getPlayerSpawnPos(game);
        } else {
            return shipConfig.getSpawnPos();
        }
    }
}
