package RuberDuckDev;

import robocode.*;
import robocode.util.Utils;

import java.awt.geom.Point2D;
import java.util.Random;

public class DogeBot extends AdvancedRobot
{
    public boolean enemyFound = false;
    public double enemyHealth = -1;
    private Point2D.Double EnemyLocaiton= new Point2D.Double(0,0);
    private String EnemyName;
    private double count;
    private double gunTurnAmt = 10;
    private RobotStatus robotStatus;

    public void run()
    {
       while (true)
        {
            if(!enemyFound) {
                turnGunRight(gunTurnAmt);
                count++;
                if (count > 2) {
                    gunTurnAmt = -10;
                }
                if (count > 5) {
                    gunTurnAmt = 10;
                }
                if (count > 11) {
                    EnemyName = null;
                }
            }
            execute();
        }
    }

    public void onStatus(StatusEvent e) {
        this.robotStatus = e.getStatus();
    }

    public Point2D.Double GetEnemyPosition(ScannedRobotEvent event)
    {
        double angleToEnemy = event.getBearing();
        // Calculate the angle to the scanned robot
        double angle = Math.toRadians((robotStatus.getHeading() + angleToEnemy % 360));

        // Calculate the coordinates of the robot
        double enemyX = (robotStatus.getX() + Math.sin(angle) * event.getDistance());
        double enemyY = (robotStatus.getY() + Math.cos(angle) * event.getDistance());

        return new Point2D.Double(enemyX, enemyY);
    }

    public void onScannedRobot(ScannedRobotEvent event)
    {
        count = 0;
        enemyFound = true;

        // look in the direction of the enemy

        var enemyBearing = event.getBearing()-90;
        setTurnRight(enemyBearing);
        setTurnGunRight(-enemyBearing);
        waitFor(new TurnCompleteCondition(this));


        // get info about enemy

        if(enemyHealth < 0|| event.getName() != EnemyName)
        {
            enemyHealth = event.getEnergy();
        }

        //check if the enemy has shot
        if(event.getName() == this.EnemyName&& 1<=Math.abs(enemyHealth-event.getEnergy()) && Math.abs(enemyHealth-event.getEnergy()) <=3)
        {
            this.enemyHealth = event.getEnergy();
            var moveDistance = Math.random()*100-200;
            var distanceToEnemy = event.getDistance();
            var angle = 90+Math.toDegrees(Math.atan(distanceToEnemy/moveDistance));
            setAhead(moveDistance);
            this.out.println(angle);
            setTurnGunLeft(angle);
            waitFor(new TurnCompleteCondition(this));
        }
        this.EnemyName = event.getName();


    }
    double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2-x1;
        double yo = y2-y1;
        double hyp = Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing;
    }


}
