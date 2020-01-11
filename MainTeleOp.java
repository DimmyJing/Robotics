package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="MainTeleOp", group="Linear Opmode")

public class MainTeleOp extends LinearOpMode {
    class ToggleVal {
        private boolean prevState;
        private boolean state;

        ToggleVal() {
            prevState = false;
            state = false;
        }

        boolean update(boolean nextState) {
            if (nextState && prevState != nextState)
                state = !state;
            prevState = nextState;
            return state;
        }
    }

    private DcMotor FL, FR, BL, BR, pivotL, pivotR, extension, articulating;
    private DcMotor[] pivots;
    private DcMotor[] motors;
    private ToggleVal powerToggle, servoToggle, stoneToggle, autoAdjToggle;

    private Servo swingL, swingR, stoneL, stoneR;
    private double[] in = {0,0};

    private int refPosArtic, refPos;

    public MainTeleOp() {
        powerToggle = new ToggleVal();
        servoToggle = new ToggleVal();
        stoneToggle = new ToggleVal();
        autoAdjToggle = new ToggleVal();
    }

    private boolean setStone(boolean state) {
        if (state) {
            stoneL.setPosition(0.5);
            stoneR.setPosition(0.05);
        } else {
            stoneL.setPosition(0.35);
            stoneR.setPosition(0.2);
        }
        return state;
    }

    private boolean setSwing(boolean state) {
        if (state) {
            swingL.setPosition(0.978);
            swingR.setPosition(0.725);
        } else {
            swingL.setPosition(0.8);
            swingR.setPosition(0.6);
        }
        return state;
    }

    public void runOpMode() {
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        motors = new DcMotor[]{FL, FR, BL, BR};

        //ARM MOTORS
        pivotL = hardwareMap.get(DcMotor.class, "pivotL");
        pivotR = hardwareMap.get(DcMotor.class, "pivotR");
        extension = hardwareMap.get(DcMotor.class, "extension");
        articulating = hardwareMap.get(DcMotor.class, "articulating");

        pivots = new DcMotor[]{pivotL, pivotR};
        pivotL.setDirection(Direction.FORWARD);
        pivotR.setDirection(Direction.REVERSE);
        for (DcMotor i : pivots) {
            i.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            i.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            i.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        //FOR FOUNDATION MOVEMENT
        swingL = hardwareMap.get(Servo.class, "swingL");
        swingR = hardwareMap.get(Servo.class, "swingR");

        //FOR STONE GRABBING
        stoneL = hardwareMap.get(Servo.class, "stoneL");
        stoneR = hardwareMap.get(Servo.class, "stoneR");

        articulating.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        articulating.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        articulating.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        refPos = 0;
        refPosArtic = 90;

        waitForStart();

        while(opModeIsActive()){
            normalOps();
            balanceOps();
        }
    }

    void normalOps() {
        in[0] = gamepad1.left_stick_x;
        in[1] = gamepad1.left_stick_y;
        double div = powerToggle.update(gamepad1.right_bumper) ? 4 : 1;

        double[] powers = new double[]{
                (in[0] - in[1]) / (div * 1.15),
                (in[0] + in[1]) / (div * 1.15),
                (-in[0] - in[1]) / div,
                (-in[0] + in[1]) / div,
        };

        if (gamepad1.right_trigger + gamepad1.left_trigger > 0)
            for (DcMotor i : motors)
                i.setPower((gamepad1.right_trigger - gamepad1.left_trigger) / div);
        else
            for (int i = 0; i < motors.length; i++)
                motors[i].setPower(powers[i]);

        telemetry.addData("Swing:", setSwing(servoToggle.update(gamepad1.left_bumper)) ? "DOWN" : "UP");
        telemetry.update();
    }

    void balanceOps() {
        extension.setPower(-gamepad2.left_stick_y/2);

        /*
        //FLATTEN OUT ARTICULATING JOINT (currently too jitterry)
        double error = Math.pow(refPosArtic - (refPos - pivotR.getCurrentPosition()) * 228 / 2240 - articulating.getCurrentPosition(), 3) / 313;
        telemetry.addData("actPwr", maxabs(error, 0.3));

        if(autoAdjToggle.update(gamepad2.left_bumper))
            if(articulating.getCurrentPosition() > (refPosArtic))
                articulating.setPower(-0.7);
            else
                articulating.setPower(maxabs(error, 0.3));
        else
            articulating.setPower(-gamepad2.right_stick_y/2.5);

        telemetry.addData("Encoder pos pivot", pivotR.getCurrentPosition());
        telemetry.addData("Encoder pos articulating", articulating.getCurrentPosition());

        if(!gamepad1.x) {
            boolean motionAllowed = false;
            //WHEN A IS BEING HELD, DO NOT UPDATE POWER - HOLD @ WHATEVER WAS LAST ASSIGNED
            if(pivotR.getCurrentPosition() < refPos - 500)
                if(gamepad1.right_stick_y > 0)
                    motionAllowed = true;
                else if (pivotR.getCurrentPosition() > refPos)
                    if(gamepad1.right_stick_y < 0)
                        motionAllowed = true;
                    else
                        motionAllowed = true;

            if(motionAllowed)
                for (DcMotor i : pivots)
                    i.setPower(gamepad1.right_stick_y / 2.8);
            else
                for (DcMotor i : pivots)
                    i.setPower(0);
        } else {
            //CODE TO CONFORM TO WHATEVER INITIAL POSITION IS
            // GET INITIAL POSITION ONCE, IF ENCODER GOES BELOW THEN INCREMENTALLY INCREASE PWR, VICE VERSA
            if(pivotR.getCurrentPosition() < refPos - 500 || pivotR.getCurrentPosition() > refPos + 5)
                for (DcMotor i : pivots)
                    i.setPower(0);
        }
         */

        telemetry.addData("Stone:", setStone(stoneToggle.update(gamepad2.x)) ? "active" : "disabled");
        telemetry.update();
    }

    double maxabs(double in, double lim){
        return Math.abs(in) > Math.abs(lim) ? in : lim;
    }
}
