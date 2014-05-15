package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.planet.PlanetBind;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class StillGuard implements MoveDestProvider {

  private final PlanetBind myPlanetBind;
  private final float myDesiredSpdLen;
  private Vector2 myDest;

  public StillGuard(Vector2 target, SolGame game, ShipConfig sc) {
    myDest = new Vector2(target);
    myPlanetBind = PlanetBind.tryBind(game, myDest, 0);
    myDesiredSpdLen = sc.hull.type == HullConfig.Type.BIG ? Const.BIG_AI_SPD : Const.DEFAULT_AI_SPD;
  }

  @Override
  public Vector2 getDest() {
    return myDest;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return false;
  }

  @Override
  public float getDesiredSpdLen() {
    return myDesiredSpdLen;
  }

  @Override
  public boolean shouldStopNearDest() {
    return true;
  }

  @Override
  public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig) {
    if (myPlanetBind != null) {
      Vector2 diff = SolMath.getVec();
      myPlanetBind.setDiff(diff, shipPos, false);
      myDest.add(diff);
      SolMath.free(diff);
    }
  }

  @Override
  public Boolean shouldManeuver(boolean canShoot, SolShip nearestEnemy, boolean nearGround) {
    return true;
  }
}
