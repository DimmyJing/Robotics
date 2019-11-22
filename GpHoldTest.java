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
    private ToggleVal powerToggle, servoToggle, stoneToggle, autoAdjToggle;
    private int refPos, refPosArtic;

    private Servo swingL, swingR, stoneL, stoneR;
    private double[] in = {0,0};



    public GpTest() {
        powerToggle = new ToggleVal();
        servoToggle = new ToggleVal();
        stoneToggle = new ToggleVal();
        autoAdjToggle = new ToggleVal();
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

        pivoL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivoR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        articulating.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.update();
        refPos = pivoR.getCurrentPosition() - 20;
        refPosArtic = articulating.getCurrentPosition() - 100;
        articulating.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        articulating.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        pivoR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pivoR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double refPosArtic = 90;
        double refPos = pivoR.getCurrentPosition();
        telemetry.addData("articPos", articulating.getCurrentPosition());

        waitForStart();

        while(opModeIsActive()){

            normalOps();
            extension.setPower(-gamepad2.left_stick_y/2);

            //FLATTEN OUT ARTICULATING JOINT (currently too jitterry)
            double error = Math.pow(((refPosArtic - (int)((refPos - pivoR.getCurrentPosition()) * 228/2240)) - articulating.getCurrentPosition()),3)/313;
            telemetry.addData("actPwr", maxabs(error, 0.3));
            if(autoAdjToggle.update(gamepad2.left_bumper)){
            if(articulating.getCurrentPosition() > (refPosArtic)){
                articulating.setPower(-0.7);
            } else {
                articulating.setPower(maxabs(error, 0.3));
            }
            } else {
                articulating.setPower(-gamepad2.right_stick_y/2.5);
            }

            telemetry.addData("Encoder pos pivo", pivoR.getCurrentPosition());
            telemetry.addData("Encoder pos artic", articulating.getCurrentPosition());
            if(!gamepad1.x){
            boolean motionAllowed = false;
            //WHEN A IS BEING HELD, DO NOT UPDATE POWER - HOLD @ WHATEVER WAS LAST ASSIGNED
            if(pivoR.getCurrentPosition() < (refPos - 500)){
                if(gamepad1.right_stick_y > 0){
                    motionAllowed = true;
                }
            } else if (pivoR.getCurrentPosition() > refPos){
                if(gamepad1.right_stick_y < 0){
                    motionAllowed = true;
                }
            } else {
                motionAllowed = true;
            }
            if(motionAllowed){
            pivoR.setPower(gamepad1.right_stick_y/2.8);
            pivoL.setPower(-gamepad1.right_stick_y/2.8);
            } else {
                pivoR.setPower(0);
                pivoL.setPower(0);
            }
          } else {
              //CODE TO CONFORM TO WHATEVER INITIAL POSITION IS
              // GET INITIAL POSITION ONCE, IF ENCODER GOES BELOW THEN INCREMENTALLY INCREASE PWR, VICE VERSA
              if(pivoR.getCurrentPosition() < (refPos - 500) || pivoR.getCurrentPosition() > (refPos + 5)){
                  pivoR.setPower(0);
                  pivoL.setPower(0);
              }
          }

          if(stoneToggle.update(gamepad2.x)){
              stoneL.setPosition(0.5);
              stoneR.setPosition(0.05);
              telemetry.addData("stone", "active");
          } else {
              stoneL.setPosition(0.35);
              stoneR.setPosition(0.2);
              telemetry.addData("stone", "disabled");
          }
        }
    }

    public void normalOps() {
        in[0] = gamepad1.left_stick_x;
        in[1] = gamepad1.left_stick_y;
        double div = 1;
        boolean precise = powerToggle.update(gamepad1.right_bumper);
        if(precise){
            div = 4;
        } else {
            div = 1;
        }

        if (gamepad1.right_trigger + gamepad1.left_trigger > 0){
            FL.setPower((gamepad1.right_trigger - gamepad1.left_trigger)/div);
            BL.setPower((gamepad1.right_trigger - gamepad1.left_trigger)/div);
            FR.setPower((gamepad1.right_trigger - gamepad1.left_trigger)/div);
            BR.setPower((gamepad1.right_trigger - gamepad1.left_trigger)/div);
        } else {
                FL.setPower((in[0] - in[1])/(div * 1.15));
                FR.setPower((in[0] + in[1])/(div * 1.15));
                BL.setPower((-in[0] - in[1])/div);
                BR.setPower((-in[0] + in[1])/div);
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
