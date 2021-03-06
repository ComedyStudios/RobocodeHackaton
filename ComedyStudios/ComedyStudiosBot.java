package ComedyStudios;

import robocode.*;

import javax.swing.text.Position;
import java.awt.geom.Point2D;
import java.util.Random;


public class ComedyStudiosBot extends AdvancedRobot
{
    double distance;
    Point2D.Double arenaSize;
    boolean nextToBorder;
    double turnAng = 15;
    int count;
    RobotStatus robotStatus;
    int time;
    int standingTime;

    public void run()
    {
        arenaSize = new Point2D.Double(getBattleFieldWidth(), getBattleFieldWidth());

        // search for a border to crawl to !DONE : search for the closest wall
        while(true)
        {
            time ++;
            if(!nextToBorder)
            {
                goToBorder(arenaSize);
            }

            else if(count > 10)
            {
                setTurnGunRight(turnAng);
            }
            else setTurnGunRight(turnAng/10);
            count++;
            if(time % 70 == 0)
            {
                goToBorder(arenaSize);
            }
            execute();
        }
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
        count = 300;
    }



    public void onScannedRobot(ScannedRobotEvent event)
    {

            standingTime ++;
            count = 0;
            if(nextToBorder)
            {
                if(standingTime > 10)
                {
                    standingTime = 0;

                    Random r = new Random();
                    int low = -300;
                    int high = 300;
                    distance  = r.nextInt(high-low) + low;
                    ahead(distance);
                    if(distance > 0)
                    {
                        turnAng = 40;
                    }
                    else
                        turnAng = -40;

                }

                //turn gun to enemy Bot;
                var position = new Point2D.Double(getX(), getY());
                var enemyPosition = GetEnemyPosition(event);
                var absoluteBearing = absoluteBearing(position.x, position.y, enemyPosition.x, enemyPosition.y);
                setTurnGunRight(absoluteBearing - getGunHeading());

                if(this.getEnergy() > 10 && event.getDistance() <= 700) {


                    double firepower;
                    // fire
                    if (this.getEnergy() > 50) {
                        firepower = Math.max(400 / event.getDistance(), 1);
                    } else
                        firepower = 1;

                    fire(firepower);
                }
                else
                    standingTime += 10;

        }
    }


    public void onHitWall(HitWallEvent event) {
        out.println("turn");

        if(distance > 0)
        {
            back(30);
            turnRight( -90);
            waitFor( new TurnCompleteCondition(this));
        }
        else
        {
            back(-30);
            turnRight(90);
            waitFor( new TurnCompleteCondition(this));
        }
        waitFor(new MoveCompleteCondition(this));

    }


    public void onHitRobot(HitRobotEvent event)
    {
        if (event.getBearing() > -10.0D && event.getBearing() < 10.0D)
        {
            this.fire(3.0D);
        }

        this.back(300);
        waitFor(new MoveCompleteCondition(this));
        goToBorder(arenaSize);
    }


    private double getDistanceRobotPoint(Point2D.Double target) {
        return Math.sqrt(Math.pow(target.x-getX(),2) + Math.pow(target.y-getY(),2));
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
