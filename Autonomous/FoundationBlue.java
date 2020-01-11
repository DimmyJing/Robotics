package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import static java.lang.System.currentTimeMillis;

@Autonomous(name="FoundationBlue", group="Linear Opmode")
public class FoundationBlue extends LinearOpMode {
    private final int TOLERANCE = 10;

    private DcMotor FL, FR, BL, BR, pivotL, pivotR, extension, articulating;
    private DcMotor[] drives;

    private Servo swingL, swingR, stoneL, stoneR;
    // Vidith I'm watching you
    private double[] getPowers(double x, double y) {
        return new double[]{x - y, x + y, -x - y, -x + y};
    }

    private void moveRev(double revs, double dirX, double dirY) {
        double[] powers = getPowers(dirX, dirY);
        for (int i = 0; i < drives.length; i++) {
            int initialPosition = drives[i].getCurrentPosition();
            drives[i].setTargetPosition(initialPosition + (int)(powers[i] * 244 * revs));
            drives[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drives[i].setPower(powers[i]);
        }
        boolean done = false;
        long begin = System.currentTimeMillis();
        while (!done && (FL.isBusy()&&FR.isBusy()&&BL.isBusy()&&BR.isBusy())) {
            done = true;
            for (int i = 0; i < drives.length; i++)
                if (Math.abs(drives[i].getCurrentPosition() - powers[i] * 244 * revs) > TOLERANCE)
                    done = false;
            if (System.currentTimeMillis() - begin > revs * 700 / Math.max(dirX, dirY))
                done = true;
            telemetry.addData("FL", FL.getCurrentPosition());
            telemetry.addData("FR", FR.getCurrentPosition());
            telemetry.addData("BL", BL.getCurrentPosition());
            telemetry.addData("BR", BR.getCurrentPosition());
            telemetry.update();
        }
        for (DcMotor i : drives) {
            i.setPower(0);
            i.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    // true for clockwise, false for counterclockwise
    // 1 for every 90 degrees
    private void rotateRev(double revs, boolean dir, double power) {
        int rotateAmount = dir ? 585 : -585;
        for (int i = 0; i < drives.length; i++) {
            drives[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            drives[i].setTargetPosition((int)(rotateAmount * revs));
            drives[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drives[i].setPower(dir ? power : -power);
        }
        boolean done = false;
        while (!done && (FL.isBusy()&&FR.isBusy()&&BL.isBusy()&&BR.isBusy())) {
            for (int i = 0; i < drives.length; i++)
                if (Math.abs(drives[i].getCurrentPosition() - rotateAmount * revs) < TOLERANCE)
                    done = true;
            telemetry.addData("FL", FL.getCurrentPosition());
            telemetry.addData("FR", FR.getCurrentPosition());
            telemetry.addData("BL", BL.getCurrentPosition());
            telemetry.addData("BR", BR.getCurrentPosition());
            telemetry.update();
        }
        for (DcMotor i : drives) {
            i.setPower(0);
            i.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    // false for up, true for down
    private void setFoundationServo(boolean state) {
        if (state) {
            swingL.setPosition(0.978);
            swingR.setPosition(0.725);
        } else {
            swingL.setPosition(0.75);
            swingR.setPosition(0.55);
        }
    }

    public void runOpMode() {
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        drives = new DcMotor[]{FL, FR, BL, BR};

        //ARM MOTORS
        pivotL = hardwareMap.get(DcMotor.class, "pivotL");
        pivotR = hardwareMap.get(DcMotor.class, "pivotR");
        extension = hardwareMap.get(DcMotor.class, "extension");
        articulating = hardwareMap.get(DcMotor.class, "articulating");

        //FOR FOUNDATION MOVEMENT
        swingL = hardwareMap.get(Servo.class, "swingL");
        swingR = hardwareMap.get(Servo.class, "swingR");

        //FOR STONE GRABBING
        stoneL = hardwareMap.get(Servo.class, "stoneL");
        stoneR = hardwareMap.get(Servo.class, "stoneR");

        for (DcMotor i : drives)
            i.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pivotL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        pivotR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        pivotL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivotR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.update();

        setFoundationServo(false);

        waitForStart();

        moveRev(5, 0, -1);
        rotateRev(1, false, 0.3);
        moveRev(2, 0, -1);
        setFoundationServo(true);
        sleep(1000);
        moveRev(1, 0, 1);
        rotateRev(1, false, 0.3);
        moveRev(4, 0, -1);
        setFoundationServo(false);
        sleep(1000);
        rotateRev(1, true, 0.3);
        moveRev(4.2, 0, 1);
    }
}
