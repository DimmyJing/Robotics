package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.Arrays;
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

    private DcMotor FL, FR, BL, BR, pivoL, pivoR;
    private ToggleVal powerToggle;
    private ToggleVal servoToggle;

    private Servo swingL, swingR;
    private double[] in = {0,0};

    public MainTeleOp() {
        powerToggle = new ToggleVal();
        servoToggle = new ToggleVal();
    }

    public void runOpMode() {
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        pivoL = hardwareMap.get(DcMotor.class, "pivotL");
        pivoR = hardwareMap.get(DcMotor.class, "pivotR");

        swingL = hardwareMap.get(Servo.class, "swingL");
        swingR = hardwareMap.get(Servo.class, "swingR");

        telemetry.addData("Welcome Drivers. Operate me Well", null);
        telemetry.update();

        waitForStart();

        while(opModeIsActive()){

            normalOps();
            pivoL.setPower(gamepad2.left_stick_y/3);
            pivoR.setPower(-gamepad2.left_stick_y/3);

            /*
                while(gamepad1.right_stick_y != 0){
                    normalOps();
                    pivotMotor.setPower(gamepad1.right_stick_y);
                    pivoted = true;
                }
                if(pivoted){
                    int dtheta = encoderticks;
                    articulating.setTargetPosition(-detheta? * revRatio);
                    articulating.setPower((dtheta/abs(dtheta)) * 0.2);
                    boolean interrupted = false;
                    while(articulating.isBusy()){
                        normalOps();
                        if(gamepad1.right_stick_y != 0){
                            interrupted = true;
                            articulating.setPower(0);
                            break;
                        }
                    }
                    if(!interrupted){
                        articulating.setPower(0);
                        pivotMotor.resetEncoder();
                        articulating.resetEncoder();
                    }
                }
            */

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
