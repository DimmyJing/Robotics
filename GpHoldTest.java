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

@TeleOp(name="GpTest", group="Linear Opmode")

public class GpTest extends LinearOpMode {
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
    private ToggleVal powerToggle, servoToggle, stoneToggle;
    private boolean autoadjust = true;
    private int refPos, refPosArtic;

    private Servo swingL, swingR, stoneL, stoneR;
    private double[] in = {0,0};



    public GpTest() {
        powerToggle = new ToggleVal();
        servoToggle = new ToggleVal();
        stoneToggle = new ToggleVal();
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
        pivoL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        articulating.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pivoL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivoR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        articulating.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.update();
        refPos = pivoR.getCurrentPosition() - 20;
        refPosArtic = articulating.getCurrentPosition() - 100;
        waitForStart();

        while(opModeIsActive()){

            normalOps();
            extension.setPower(-gamepad2.right_stick_y/2);

            //FLATTEN OUT ARTICULATING JOINT (currently too jitterry)
            articulating.setPower(maxabs((refPosArtic + (int)((pivoR.getCurrentPosition() - refPos) * 288/2240) - articulating.getCurrentPosition())/25, 0.4));


            telemetry.addData("Encoder pos pivo", pivoR.getCurrentPosition());
            telemetry.addData("Encoder pos artic", articulating.getCurrentPosition());
            if(!gamepad1.x){
            boolean motionAllowed = false;
            //WHEN A IS BEING HELD, DO NOT UPDATE POWER - HOLD @ WHATEVER WAS LAST ASSIGNED
            if(pivoR.getCurrentPosition() < (refPos - 500)){
                if(gamepad1.right_stick_y < 0){
                    motionAllowed = true;
                }
            } else if (pivoR.getCurrentPosition() > refPos){
                if(gamepad1.right_stick_y > 0){
                    motionAllowed = true;
                }
            } else {
                motionAllowed = true;
            }
            if(motionAllowed){
            pivoR.setPower(-gamepad1.right_stick_y/2.8);
            pivoL.setPower(gamepad1.right_stick_y/2.8);
            } else {
                pivoR.setPower(0);
                pivoL.setPower(0);
            }
          } else {
              //CODE TO CONFORM TO WHATEVER INITIAL POSITION IS
              // GET INITIAL POSITION ONCE, IF ENCODER GOES BELOW THEN INCREMENTALLY INCREASE PWR, VICE VERSA
              if(pivoR.getCurrentPosition() < (refPos - 500) || pivoR.getCurrentPosition() > refPos){
                  pivoR.setPower(0);
                  pivoL.setPower(0);
              }
          }

          if(stoneToggle.update(gamepad2.x)){
              stoneL.setPosition(0.7);
              stoneR.setPosition(0.3);
          } else {
              stoneL.setPosition(0.55);
              stoneR.setPosition(0.45);
          }
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

    public double maxabs(double in, double lim){
        if(in < -lim){
            return -lim;
        }
        if (in > lim){
            return lim;
        }
        return in;
    }
}
