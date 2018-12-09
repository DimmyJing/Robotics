package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous (name = "AutoStraightOp", group= "Linear Opmode")

public class AutoStraightOp extends LinearOpMode {
    public DcMotor left_drive;
    public DcMotor right_drive;
    public DcMotor latchingLeft;
    public DcMotor latchingRight;
    public ColorSensor minSense;
    public Servo latchLock;

    private Servo claiming;

    public void turn_left(double speed, long time) {
        left_drive.setPower(speed * 0.3);
        right_drive.setPower(speed * -0.3);
        sleep(time);
        left_drive.setPower(0);
        right_drive.setPower(0);
    }


    public void turn_right(double speed, long time) {
        left_drive.setPower(speed * -0.3);
        right_drive.setPower(speed * 0.3);
        sleep(time);
        left_drive.setPower(0);
        right_drive.setPower(0);
    }


    public void move_forward_rev(double revs, double power) {
        right_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        right_drive.setTargetPosition((int)(-244 * revs));
        left_drive.setTargetPosition((int)(-244 * revs));

        right_drive.setPower(power);
        left_drive.setPower(power);

        while((right_drive.isBusy())||(left_drive.isBusy())){}
        right_drive.setPower(0);
        left_drive.setPower(0);

        right_drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        left_drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void turn_right_rev(double revs, double power) {
        right_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        right_drive.setTargetPosition((int)(244 * revs));
        left_drive.setTargetPosition((int)(-244 * revs));

        right_drive.setPower(-power);
        left_drive.setPower(power);

        while((right_drive.isBusy())||(left_drive.isBusy())){}
        right_drive.setPower(0);
        left_drive.setPower(0);

        right_drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        left_drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void turn_left_rev(double revs, double power) {
        right_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        right_drive.setTargetPosition((int)(-244 * revs));
        left_drive.setTargetPosition((int)(244 * revs));

        right_drive.setPower(power);
        left_drive.setPower(-power);

        while((right_drive.isBusy())||(left_drive.isBusy())){}
        right_drive.setPower(0);
        left_drive.setPower(0);

        right_drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        left_drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void move_forward(double power, long time) {
        right_drive.setPower(power);
        left_drive.setPower(power);
        sleep(time);
        right_drive.setPower(0);
        left_drive.setPower(0);
    }

    public void unlatch() {
        latchingLeft.setPower(-0.35);
        latchingRight.setPower(0.35);
        sleep(500);
        latchLock.setPosition(0.45);
        sleep(600);
        latchingLeft.setPower(-0.04);     //LANDING WORKS
        latchingRight.setPower(0.04);
        sleep(2100);
        latchingLeft.setPower(0.2);
        latchingRight.setPower(-0.2);
        sleep(300);
        latchingLeft.setPower(0);
        latchingRight.setPower(0);
        sleep(500);
        latchLock.setPosition(0.45);
        right_drive.setPower(-0.2);
        sleep(500);
        left_drive.setPower(0.2);
        sleep(1000);
        left_drive.setPower(0);
        right_drive.setPower(0);
        latchingRight.setPower(0.4);
        latchingLeft.setPower(-0.4);
        sleep(450);
        latchingRight.setPower(0);
        latchingLeft.setPower(0);
        sleep(300);
        turn_left_rev(2.05, 0.6);
        sleep(500);
    }

    public void initialize() {
        right_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_drive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_drive.setPower(0.8);
        left_drive.setPower(0.8);
        while((right_drive.isBusy())||(left_drive.isBusy())){}
        right_drive.setPower(0);
        left_drive.setPower(0);
        right_drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        left_drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // todo: write your code here
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        left_drive = hardwareMap.get(DcMotor.class, "left_drive");
        right_drive = hardwareMap.get(DcMotor.class, "right_drive");
        latchingLeft = hardwareMap.get(DcMotor.class, "latchLeft");
        latchingRight = hardwareMap.get(DcMotor.class, "latchRight");
        claiming = hardwareMap.get(Servo.class, "claiming");
        latchLock = hardwareMap.get(Servo.class, "latchingSecure");
        minSense = hardwareMap.get(ColorSensor.class , "colorSens");
        left_drive.setDirection(DcMotor.Direction.REVERSE);
        right_drive.setDirection(DcMotor.Direction.FORWARD);

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        left_drive.setDirection(DcMotor.Direction.REVERSE);
        right_drive.setDirection(DcMotor.Direction.FORWARD);
        claiming.setPosition(0.1);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        initialize();
        unlatch();

        move_forward_rev(2.4, 0.8);
        sleep(500);
        telemetry.addData("Red: ", minSense.red());
        telemetry.addData("Green: ", minSense.green());
        telemetry.update();

        if (false) {

        }
        else {
            move_forward_rev(-2.4, -0.8);
            sleep(500);
            turn_right_rev(0.31, 0.8);
            sleep(500);
            move_forward_rev(2, 0.8);
            sleep(500);
            telemetry.addData("Red: ", minSense.red());
            telemetry.addData("Green: ", minSense.green());
            telemetry.update();
            if (false) {
            }
            else {
                move_forward_rev(-2, -0.8);
                sleep(500);
                turn_right_rev(0.35, 0.8);
                sleep(500);
                move_forward_rev(2.3, 0.8);
                sleep(500);

                telemetry.addData("Red: ", minSense.red());
                telemetry.addData("Green: ", minSense.green());
                telemetry.update();
            }
        }
    }
}