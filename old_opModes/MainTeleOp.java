package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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

@Disabled
@TeleOp(name="MainTeleOp", group="Linear Opmode")

public class MainTeleOp extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    public DcMotor left_drive;
    public DcMotor right_drive;
    public DcMotor latchL;
    public DcMotor latchR;
    private Servo claiming;
    private Servo latchSecure;
    private CRServo intakeL;
    private CRServo intakeR;
    boolean latchSecureToggle = true;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        left_drive = hardwareMap.get(DcMotor.class, "left_drive");
        right_drive = hardwareMap.get(DcMotor.class, "right_drive");
        latchL = hardwareMap.get(DcMotor.class , "latchLeft");
        latchR = hardwareMap.get(DcMotor.class , "latchRight");
        claiming = hardwareMap.get(Servo.class, "claiming");
        latchSecure = hardwareMap.get(Servo.class , "latchingSecure");
        intakeL = hardwareMap.get(CRServo.class, "leftRoller");
        intakeR = hardwareMap.get(CRServo.class, "rightRoller");
        //latching = hardwareMap.get(DcMotor.class , "latching");


        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        left_drive.setDirection(DcMotor.Direction.REVERSE);
        right_drive.setDirection(DcMotor.Direction.FORWARD);
        claiming.setPosition(0.1);
        left_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left_drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right_drive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right_drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        //int encoderPosition = latching.getCurrentPosition();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        boolean x_btn_hold = false;
        boolean y_btn_hold = false;

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // Send calculated power to wheels
            //latching.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            left_drive.setPower(gamepad1.left_stick_y/2);
            right_drive.setPower(gamepad1.right_stick_y/2);
            latchR.setPower(0.75 * gamepad2.left_stick_y);
            latchL.setPower(0.75 * -gamepad2.left_stick_y);


            telemetry.addData("Encoder L", left_drive.getCurrentPosition());
            telemetry.addData("Encoder R", right_drive.getCurrentPosition());

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Left Stick: " + gamepad1.left_stick_y);
            telemetry.addData("Status", "Right Stick: " + gamepad1.right_stick_y);
            telemetry.addData("latched ", latchSecureToggle);


            telemetry.update();

            if(gamepad2.x && !x_btn_hold) {
                x_btn_hold = true;
                latchSecureToggle = !latchSecureToggle;
                sleep(150);     //For the latch lock
            }
            else if (x_btn_hold)
                x_btn_hold = false;

            if (gamepad2.y) {
                latchL.setPower(-0.45);
                latchR.setPower(0.45);   //Holding the latchArm in position
            }

            if(latchSecureToggle) {
                latchSecure.setPosition(0.7);
            }
            else {
                latchSecure.setPosition(0.45);
            }

            if (gamepad2.a){
                intakeR.setPower(1);
                intakeL.setPower(-1);
            }
            else if (gamepad2.b){
                intakeR.setPower(-1);
                intakeL.setPower(1);   //Roller code
            } else {
                intakeR.setPower(0);
                intakeL.setPower(0);
            }
        }
    }
}

