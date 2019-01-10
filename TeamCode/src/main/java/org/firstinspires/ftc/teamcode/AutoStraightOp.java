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


@Autonomous (name = "AutoStraightOp", group= "Linear Opmode")

public class AutoStraightOp extends LinearOpMode {
    private DcMotor left_drive;
    private DcMotor right_drive;
    private DcMotor latchingLeft;
    private DcMotor latchingRight;
    private Servo latchLock;

    private Servo claiming;

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold";
    private static final String LABEL_SILVER_MINERAL = "Silver";

    private static final String VUFORIA_KEY = "AVFqPUj/////AAABmUZq2lNDVkH3oO3zcUMuhFcPxlq7hd423wLEOcLpQlY5b/ASq1SQUZ3mrQoe0f1fsWJzB6Y8MtKD0qwvNR569fy85lgYP4C1A+0hfLtHK0LkzP4pDjQntey02WT7N3wLXjliJYqMxXPdSrxn2/+XMlNmjA7/FlrMP9UesjD5srMCKKy8ERG1NF6qK3B+Hbw+U6L10ojIaODz3YoOgurGyOHt3FXScmdhyeQYBCJrTSPWBDwlis9dYAVZgIvuKFsXKVYr8JUdWfkGLbt+11orICZb1MoYMop+3b6I/Pl2fx0urnUqYjgbGIDOaL7Atziss+A6sSgm9QCiyzHxrdk4oqUcCnvQ2oioyDWCNjcR9t4N";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    private void move_forward_rev(double revs, double power) {
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

    private void turn_right_rev(double revs, double power) {
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

    private void turn_left_rev(double revs, double power) {
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

    private void unlatch() {
        // Move the latch lock out
        latchingLeft.setPower(-0.35);
        latchingRight.setPower(0.35);
        latchLock.setPosition(0.4);
        sleep(800);
        latchingLeft.setPower(-0.02);
        latchingRight.setPower(0.02);
        claiming.setPosition(0.1);
        sleep(800);
        latchingLeft.setPower(0.1);
        latchingRight.setPower(-0.1);
        sleep(400);
        turn_left_rev(0.3, 1);
        latchingLeft.setPower(-0.8);
        latchingRight.setPower(0.8);
        sleep(330);
        latchingLeft.setPower(0);
        latchingRight.setPower(0);
        latchLock.setPosition(0.6);
    }

    private boolean isGold() {
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

    private void first_path() {
        move_forward_rev(3, 1);
        /*
        move_forward_rev(2.6, 1);
        turn_right_rev(0.9, 1);
        move_forward_rev(3.0, 1);
        claiming.setPosition(0.5);
        sleep(1000);
        move_forward_rev(-8.0, 1);
        */
    }

    private void second_path() {
        move_forward_rev(3, 1);
        /*
        move_forward_rev(4, 1);
        sleep(100);
        turn_left_rev(0.5, 1);
        sleep(100);
        move_forward_rev(0.8, 1);
        sleep(100);
        turn_right_rev(0.87, 1);
        claiming.setPosition(0.5);
        sleep(1000);
        move_forward_rev(-7, 1);
        */
    }

    private void third_path() {
        move_forward_rev(3, 1);
        /*
        move_forward_rev(3, 1);
        turn_left_rev(1, 1);
        move_forward_rev(3.2, 1);
        turn_right_rev(1, 1);
        move_forward_rev(0.6, 1);
        claiming.setPosition(0.5);
        sleep(1000);
        move_forward_rev(-7.6, 1);
        */
    }

    public void runOpMode() {
        initVuforia();
        if (ClassFactory.getInstance().canCreateTFObjectDetector())
            initTfod();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        left_drive = hardwareMap.get(DcMotor.class, "left_drive");
        right_drive = hardwareMap.get(DcMotor.class, "right_drive");
        latchingLeft = hardwareMap.get(DcMotor.class, "latchLeft");
        latchingRight = hardwareMap.get(DcMotor.class, "latchRight");
        claiming = hardwareMap.get(Servo.class, "claiming");
        latchLock = hardwareMap.get(Servo.class, "latchingSecure");
        left_drive.setDirection(DcMotor.Direction.REVERSE);
        right_drive.setDirection(DcMotor.Direction.FORWARD);
        left_drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right_drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        claiming.setPosition(0.3);

        waitForStart();

        if (tfod != null)
            tfod.activate();
        if (opModeIsActive()) {
            unlatch();
            move_forward_rev(-1, 1);
            turn_right_rev(2, 1);
            int goldPlace = -1;
            if (isGold())
                goldPlace = 0;
            else {
                turn_right_rev(0.5, 1);
                sleep(800);
                if (isGold())
                    goldPlace = 1;
                else {
                    turn_right_rev(0.4, 1);
                    goldPlace = 2;
                }
            }
            switch(goldPlace) {
                case 0:
                    first_path();
                    break;
                case 1:
                    second_path();
                    break;
                case 2:
                    third_path();
                    break;
            }
        }
        if(tfod != null)
            tfod.shutdown();
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
