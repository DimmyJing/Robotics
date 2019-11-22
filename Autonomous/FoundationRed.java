package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name="FoundationRed", group="Linear Opmode")

public class FoundationRed extends LinearOpMode {
    private DcMotor FL, FR, BL, BR, pivotL, pivotR, extension, articulating;
    private DcMotor[] drives;

    private Servo swingL, swingR, stoneL, stoneR;

    private double[] getPowers(double x, double y) {
        return new double[]{x - y, x + y, -x - y, -x + y};
    }

    private void moveRev(double revs, double dirX, double dirY) {
        double[] powers = getPowers(dirX, dirY);
        for (int i = 0; i < drives.length; i++) {
            drives[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            drives[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drives[i].setTargetPosition((int) (powers[i] * 244 * revs));
            drives[i].setPower(powers[i]);
        }
        boolean done = false;
        while (!done && (FL.isBusy()||FR.isBusy()||BL.isBusy()||BR.isBusy())) {
            for (int i = 0; i < drives.length; i++)
                if (Math.abs(drives[i].getCurrentPosition() - powers[i] * 244 * revs) < 2)
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
            drives[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drives[i].setTargetPosition((int)(rotateAmount * revs));
            drives[i].setPower(dir ? power : -power);
        }
        boolean done = false;
        while (!done && (FL.isBusy()||FR.isBusy()||BL.isBusy()||BR.isBusy())) {
            for (int i = 0; i < drives.length; i++)
                if (Math.abs(drives[i].getCurrentPosition() - rotateAmount * revs) < 2)
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

        waitForStart();

        moveRev(2, 0, -1);
        moveRev(3, 1, 0);
        moveRev(2.5, 0, -1);
        swingL.setPosition(0.978);
        swingR.setPosition(0.725);
        sleep(1000);
        moveRev(4, 0, 1);
        swingL.setPosition(0.75);
        swingR.setPosition(0.55);
        sleep(1000);
        moveRev(5, -1, 0);



    }
}
