package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import java.util.List;


@Autonomous (name = "AutoLeftOp", group= "Linear Opmode")

public class AutoLeftOp extends LinearOpMode {
    public DcMotor left_drive;
    public DcMotor right_drive;
    public DcMotor latchingLeft;
    public DcMotor latchingRight;
    public Servo latchLock;

    private Servo claiming;

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold";
    private static final String LABEL_SILVER_MINERAL = "Silver";

    private static final String VUFORIA_KEY = "AVFqPUj/////AAABmUZq2lNDVkH3oO3zcUMuhFcPxlq7hd423wLEOcLpQlY5b/ASq1SQUZ3mrQoe0f1fsWJzB6Y8MtKD0qwvNR569fy85lgYP4C1A+0hfLtHK0LkzP4pDjQntey02WT7N3wLXjliJYqMxXPdSrxn2/+XMlNmjA7/FlrMP9UesjD5srMCKKy8ERG1NF6qK3B+Hbw+U6L10ojIaODz3YoOgurGyOHt3FXScmdhyeQYBCJrTSPWBDwlis9dYAVZgIvuKFsXKVYr8JUdWfkGLbt+11orICZb1MoYMop+3b6I/Pl2fx0urnUqYjgbGIDOaL7Atziss+A6sSgm9QCiyzHxrdk4oqUcCnvQ2oioyDWCNjcR9t4N";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    public void turn_left(double speed, long time) {
        left_drive.setPower(speed);
        right_drive.setPower(-speed);
        sleep(time);
        left_drive.setPower(0);
        right_drive.setPower(0);
    }

    public void turn_right(double speed, long time) {
        left_drive.setPower(-speed);
        right_drive.setPower(speed);
        sleep(time);
        left_drive.setPower(0);
        right_drive.setPower(0);
    }

    public void move_forward(double power, long time) {
        right_drive.setPower(-power);
        left_drive.setPower(-power);
        sleep(time);
        right_drive.setPower(0);
        left_drive.setPower(0);
    }

    public void unlatch() {
        // Move the latch lock out
        latchingLeft.setPower(-0.35);
        latchingRight.setPower(0.35);
        sleep(100);
        latchLock.setPosition(0.45);
        sleep(700);
        latchingLeft.setPower(-0.04);     //LANDING WORKS
        latchingRight.setPower(0.04);
        // Initialize claiming
        claiming.setPosition(0);
        sleep(900);
        latchingLeft.setPower(0.2);
        latchingRight.setPower(-0.2);
        sleep(100);
        latchingLeft.setPower(0);
        latchingRight.setPower(0);
        // Lock latch arm
        latchLock.setPosition(0.45);
        // Wiggle Out
        move_forward(-0.2, 100);
        right_drive.setPower(-0.3);
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
        sleep(400);
        // Adjust position
        right_drive.setPower(0.2);
        sleep(500);
        left_drive.setPower(-0.35);
        sleep(735);
        right_drive.setPower(0);
        left_drive.setPower(0);
    }

    public boolean isGold() {
        while (true) {
            List<Recognition> updatedRecognition = tfod.getUpdatedRecognitions();
            if (updatedRecognition != null)
                for (Recognition rec : updatedRecognition)
                    if (rec.getLabel().equals("Gold"))
                        return true;
                    else
                        return false;
        }
    }

    public void runOpMode() {
        initVuforia();
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        }

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
        left_drive.setDirection(DcMotor.Direction.REVERSE);
        right_drive.setDirection(DcMotor.Direction.FORWARD);

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        left_drive.setDirection(DcMotor.Direction.REVERSE);
        right_drive.setDirection(DcMotor.Direction.FORWARD);
        claiming.setPosition(0.1);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        if (tfod != null) {
            tfod.activate();
        }
        if (opModeIsActive()) {
            unlatch();
            move_forward(-0.8, 650);
            turn_right(0.8, 640);
            int goldPlace = -1;
            if (isGold()) {
                goldPlace = 0;
            }
            else {
                turn_right(0.8, 160);
                sleep(400);
                if (isGold()) {
                    goldPlace = 1;
                }
                else {
                    turn_right(0.8, 160);
                    goldPlace = 2;
                }
            }
        }
        move_forward(1, 1000);
        if(tfod != null){
            tfod.shutdown();
        }
    }

    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

}
