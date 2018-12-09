package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;



@Autonomous(name="ClaimRight", group="Linear Opmode")

public class ClaimRight extends LinearOpMode {

    public DcMotor left_drive;
    public DcMotor right_drive;
    public DcMotor latchingLeft;
    public DcMotor latchingRight;
    public Servo latchLock;
    public Servo claiming;

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;

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

    public void unlatch() {
        latchingLeft.setPower(-0.35);
        latchingRight.setPower(0.35);
        sleep(500);
        latchLock.setPosition(0.45);
        sleep(2000);
        latchingLeft.setPower(-0.04);     //LANDING WORKS
        latchingRight.setPower(0.04);
        sleep(500);
        claiming.setPosition(0);
        sleep(1600);

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
        claiming.setPosition(0);
        sleep(800);
        // claiming.setPosition(0);

        right_drive.setPower(0.2);
        sleep(700);
        left_drive.setPower(-0.2);
        sleep(1000);
        left_drive.setPower(0);
        right_drive.setPower(0);
    }

    @Override
    public void runOpMode() {

        left_drive = hardwareMap.get(DcMotor.class, "left_drive");
        right_drive = hardwareMap.get(DcMotor.class, "right_drive");
        latchingLeft = hardwareMap.get(DcMotor.class, "latchLeft");
        latchingRight = hardwareMap.get(DcMotor.class, "latchRight");
        claiming = hardwareMap.get(Servo.class, "claiming");
        latchLock = hardwareMap.get(Servo.class, "latchingSecure");

        left_drive.setDirection(DcMotor.Direction.REVERSE);
        right_drive.setDirection(DcMotor.Direction.FORWARD);
        waitForStart();

        unlatch();


        move_forward_rev(-1.5, 0.8);
        move_forward_rev(0.8, 0.8);
        turn_right_rev(1.2, 0.6);
        move_forward_rev(2.1, 0.8);
        turn_left_rev(0.55, 0.6);
        move_forward_rev(4.7, 0.8);
        turn_right_rev(1.05, 0.6);
        move_forward_rev(0.4, 0.8);
        //turn_right_rev(1, 0.7)


        for(int i = 1; i<= 4; i++){
            claiming.setPosition(0);
            sleep(600);
            claiming.setPosition(0.4);
            sleep(600);
        }
    }
}

