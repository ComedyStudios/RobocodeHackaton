package ComedyStudios;

import robocode.AdvancedRobot;
import robocode.MoveCompleteCondition;
import robocode.TurnCompleteCondition;

import javax.swing.text.Position;
import java.awt.geom.Point2D;

public class ComedyStudiosBot extends AdvancedRobot
{
    double heading;
    Point2D.Double position;
    Point2D.Double arenaSize;
    boolean nextToBorder;
    public void run()
    {
        arenaSize = new Point2D.Double(getBattleFieldWidth(), getBattleFieldWidth());

        // search for a border to crawl to
        while(true)
        {
            if(!nextToBorder)
            {
                goToBorder(arenaSize);
            }
            execute();
        }
        // position yourself paralel to the border
        // drive left and right to avoid bullets
        // avoid pushing tanks
        // track enemies using Saurs Code
        // Calculate Enemy position using current enemy speed and heading
    }

    private void goToBorder(Point2D.Double arenaSize )
    {
        //if Time remains: go to closest Border
        position = new Point2D.Double(getX(), getY());

        // Determine target cordinates
        var TargetPositon = new Point2D.Double(position.x,arenaSize.y -70);
        var TargetDistance = Math.sqrt(Math.pow(TargetPositon.x-position.x,2) + Math.pow(TargetPositon.y-position.y,2));
        heading = this.getHeading();
        var TargetRotaion = absoluteBearing(position.x, position.y, TargetPositon.x, TargetPositon.y) - heading;
        // rotate to Target position;
        this.out.println("robot heading to border Target: "+ TargetPositon + "Distance:" + TargetDistance + " Angle: " + TargetRotaion);
        turnRight(TargetRotaion);
        waitFor(new TurnCompleteCondition(this));
        //TODO: if no robot insight to positon else search other angle
        ahead(TargetDistance);
        waitFor(new MoveCompleteCondition(this));
        turnRight(90);
        nextToBorder = true;

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
