package ComedyStudios;

import robocode.*;

import javax.swing.text.Position;
import java.awt.geom.Point2D;


public class ComedyStudiosBot extends AdvancedRobot
{
    Point2D.Double arenaSize;
    boolean nextToBorder;
    double turnAng = 15;
    String enemyName;
    double enemyHealth;
    int count;
    RobotStatus robotStatus;
    int lastRoundWithScans;

    public void run()
    {
        arenaSize = new Point2D.Double(getBattleFieldWidth(), getBattleFieldWidth());

        // search for a border to crawl to !DONE : search for the closest wall
        while(true)
        {
            ;
            if(!nextToBorder)
            {
                goToBorder(arenaSize);
            }

            else if(count > 300)
            {
                setTurnGunRight(turnAng);
            }
            else setTurnGunRight(1);
            count++;
            this.out.println(count);
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
        count = 300;
    }


    public void onScannedRobot(ScannedRobotEvent event)
    {
        count = 0;
        if(nextToBorder)
        {
            //TODO: fixt retarded shaky gun movement

            //turn gun to enemy Bot;
            var position = new Point2D.Double(getX(), getY());
            var enemyPosition = GetEnemyPosition(event);
            var absoluteBearing = absoluteBearing(position.x, position.y, enemyPosition.x, enemyPosition.y);
            setTurnGunRight(absoluteBearing - getGunHeading());

            // fire
            if(this.getEnergy() > 50)
            {
                fire(Math.max(400/event.getDistance(), 1));
            }
            else
                fire(1);

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
                ahead(moveDistance);
                this.enemyHealth = event.getEnergy();
                this.out.println("the enemy has Shot");
            }
            //if enemy shoots avoid and shoot back;
        }
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
