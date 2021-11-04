package ComedyStudios;

import robocode.AdvancedRobot;
import robocode.MoveCompleteCondition;
import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;

import javax.swing.text.Position;
import java.awt.geom.Point2D;

public class ComedyStudiosBot extends AdvancedRobot
{
    Point2D.Double arenaSize;
    boolean nextToBorder;
    double turnAng = 30;
    String enemyName;
    double enemyHealth;

    public void run()
    {
        arenaSize = new Point2D.Double(getBattleFieldWidth(), getBattleFieldWidth());

        // search for a border to crawl to !DONE : search for the closest wall
        while(true)
        {
            if(!nextToBorder)
            {
                goToBorder(arenaSize);
            }

            else
            {
                setTurnGunRight(turnAng);
            }

            execute();
        }
        // position yourself paralel to the border !Done
        // drive left and right to avoid bullets
        // avoid pushing tanks
        // track enemies using Saurs Code
        // Calculate Enemy position using current enemy speed and heading
    }

    private void goToBorder(Point2D.Double arenaSize )
    {
        //if Time remains: go to closest Border
        var position = new Point2D.Double(getX(), getY());

        // Determine target cordinates
        var TargetPositon = new Point2D.Double(position.x,arenaSize.y -70);
        var TargetDistance = getDistanceRobotPoint(TargetPositon);
        var heading = this.getHeading();
        var TargetRotaion = absoluteBearing(position.x, position.y, TargetPositon.x, TargetPositon.y) - heading;
        // rotate to Target position;
        this.out.println("robot heading to border Target: "+ TargetPositon + "Distance:" + TargetDistance + " Angle: " + TargetRotaion);
        turnRight(TargetRotaion);
        waitFor(new TurnCompleteCondition(this));

        //TODO: if no robot insight to positon else search other angle
        ahead(TargetDistance);
        waitFor(new MoveCompleteCondition(this));
        turnRight(90);
        waitFor(new TurnCompleteCondition(this));
        nextToBorder = true;
    }


    public void onScannedRobot(ScannedRobotEvent event)
    {
        if(nextToBorder)
        {
            this.out.println("robot detected");
            //shoot and avoid enemy bot;

            //if bot to close drive away

            // get info about enemy
            if(enemyHealth < 0|| event.getName() != enemyName)
            {
                enemyHealth = event.getEnergy();
            }

            var healthDiference = Math.abs(enemyHealth-event.getEnergy());
            if(event.getName() == enemyName && 1<=healthDiference && healthDiference <=3)
            {
                var moveDistance = Math.random()*100-200;
                //ahead(moveDistance);
                this.enemyHealth = event.getEnergy();
                this.out.println("the enemy has Shot");
            }
            //if enemy shoots avoid and shoot back;
        }
    }

    private double getDistanceRobotPoint(Point2D.Double target) {
        return Math.sqrt(Math.pow(target.x-getX(),2) + Math.pow(target.y-getY(),2));
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
