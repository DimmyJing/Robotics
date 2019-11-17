package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.lang.reflect.Array;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import java.util.Arrays;

@TeleOp(name="MainTeleOp", group="Linear Opmode")

public class MainTeleOp extends LinearOpMode {
    private class ToggleVal {
        private boolean prevState;
        private boolean state;

        public ToggleVal() {
            prevState = false;
            state = false;
        }

        public boolean update(boolean nextState) {
            if (nextState && prevState != nextState)
                state = !state;
            prevState = nextState;
            return state;
        }

        public boolean getState() {
            return state;
        }
    }

    private DcMotor FL, FR, BL, BR, pivoL, pivoR, extension, articulating;
    private ToggleVal powerToggle, servoToggle, autoHoldToggle;
    private boolean autoadjust = true;

    private Servo swingL, swingR, stoneL, stoneR;
    private double[] in = {0,0};



    public MainTeleOp() {
        powerToggle = new ToggleVal();
        servoToggle = new ToggleVal();
        autoHoldToggle = new ToggleVal();
    }

    public void runOpMode() {
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");

        //ARM MOTORS
        pivoL = hardwareMap.get(DcMotor.class, "pivotL");
        pivoR = hardwareMap.get(DcMotor.class, "pivotR");
        extension = hardwareMap.get(DcMotor.class, "extension");
        articulating = hardwareMap.get(DcMotor.class, "articulating");

        //FOR FOUNDATION MOVEMENT
        swingL = hardwareMap.get(Servo.class, "swingL");
        swingR = hardwareMap.get(Servo.class, "swingR");

        //FOR STONE GRABBING
        stoneL = hardwareMap.get(Servo.class, "stoneL");
        stoneR = hardwareMap.get(Servo.class, "stoneR");


        telemetry.addData("Welcome Drivers. Operate me Well", null);
        pivoR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        pivoR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        pivoL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivoR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.update();

        waitForStart();

        while(opModeIsActive()){

            normalOps();
            extension.setPower(-gamepad2.right_stick_y/2);

            pivoR.setPower(-gamepad2.left_stick_y);
            pivoL.setPower(gamepad2.left_stick_y);

        }
    }

    public void normalOps() {
        in[0] = gamepad1.left_stick_x;
        in[1] = gamepad1.left_stick_y;

        boolean precise = powerToggle.update(gamepad1.a);

        if (gamepad1.right_trigger + gamepad1.left_trigger > 0){
            FL.setPower(gamepad1.right_trigger - gamepad1.left_trigger);
            BL.setPower(gamepad1.right_trigger - gamepad1.left_trigger);
            FR.setPower(gamepad1.right_trigger - gamepad1.left_trigger);
            BR.setPower(gamepad1.right_trigger - gamepad1.left_trigger);
        } else {
            if(precise){
                telemetry.addData("Drive Mode: ", "PRECISION");
                FL.setPower((in[0] - in[1])/4);
                FR.setPower((in[0] + in[1])/4);
                BL.setPower((-in[0] - in[1])/4);
                BR.setPower((-in[0] + in[1])/4);
            } else {
                telemetry.addData("Drive Mode: ", "SPEED");
                FL.setPower(in[0] - in[1]);
                FR.setPower((in[0] + in[1]));
                BL.setPower((-in[0] - in[1]));
                BR.setPower((-in[0] + in[1]));
            }
        }
        if (servoToggle.update(gamepad1.left_bumper)) {
            telemetry.addData("Servo Position: ", "DOWN");
            swingL.setPosition(0.978);
            swingR.setPosition(0.725);
        } else {
            telemetry.addData("Servo Position: ", "UP");
            swingL.setPosition(0.8);
            swingR.setPosition(0.6);
        }
        telemetry.update();
    }
}
