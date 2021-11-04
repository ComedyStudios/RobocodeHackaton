package RuberDuckDev;

import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.MoveCompleteCondition;
import robocode.TurnCompleteCondition;

import java.awt.geom.Point2D;

public class TestBot extends AdvancedRobot
{

    private double minDistanceToBorder = 50 ;
    private double speed = 5000;
    private boolean movingForward;
    private boolean isNextToWall = false;
    public void run()
    {
        movingForward = true;
        setAhead(speed);
        while(true)
        {
            if(getBattleFieldWidth()-getX() <= minDistanceToBorder || getX() <= minDistanceToBorder|| getBattleFieldHeight()-getY() <= minDistanceToBorder || getY() <= minDistanceToBorder)
            {
                if(isNextToWall == false)
                {
                    ReverseDirection();
                    isNextToWall = true;
                }
            }
            else if(isNextToWall == true&&getBattleFieldWidth()-getX() > minDistanceToBorder && getX() > minDistanceToBorder && getBattleFieldHeight()-getY() > minDistanceToBorder && getY() > minDistanceToBorder)
            {
                isNextToWall = false;
            }
            execute();
        }
    }

    private void ReverseDirection() {
        if (this.movingForward) {
            this.setBack(40000.0D);
            this.movingForward = false;
        } else {
            this.setAhead(40000.0D);
            this.movingForward = true;
        }
    }

    private boolean botNextToBorder()
    {
        double x = getX();
        double y = getY();
        double arenaHeight = getBattleFieldHeight();
        double arenaWidth = getBattleFieldWidth();

        //this is retarded -> redo


        if(arenaWidth-x < minDistanceToBorder || x < minDistanceToBorder|| arenaHeight-y < minDistanceToBorder || y < minDistanceToBorder)
        {
            return true;
        }
        else if (arenaWidth-x > minDistanceToBorder || x > minDistanceToBorder|| arenaHeight-y > minDistanceToBorder || y > minDistanceToBorder)
        {
            return false;
        }
        return false;
    }

    public void onHitWall(HitWallEvent event)
    {

    }
        //TODO: when shoot;TrackEnemy; Shooting power ; Avoid Enemy; Handle Colision with enemy; Handle Colision with Bullet;

}