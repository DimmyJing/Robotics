package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;


@Autonomous (name = "AutoLeftOp", group= "Linear Opmode")

public class AutoLeftOp extends LinearOpMode {
    public DcMotor left_drive;
    public DcMotor right_drive;
    public DcMotor latching;
    private Servo claiming;

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private static final String VUFORIA_KEY = "AVFqPUj/////AAABmUZq2lNDVkH3oO3zcUMuhFcPxlq7hd423wLEOcLpQlY5b/ASq1SQUZ3mrQoe0f1fsWJzB6Y8MtKD0qwvNR569fy85lgYP4C1A+0hfLtHK0LkzP4pDjQntey02WT7N3wLXjliJYqMxXPdSrxn2/+XMlNmjA7/FlrMP9UesjD5srMCKKy8ERG1NF6qK3B+Hbw+U6L10ojIaODz3YoOgurGyOHt3FXScmdhyeQYBCJrTSPWBDwlis9dYAVZgIvuKFsXKVYr8JUdWfkGLbt+11orICZb1MoYMop+3b6I/Pl2fx0urnUqYjgbGIDOaL7Atziss+A6sSgm9QCiyzHxrdk4oqUcCnvQ2oioyDWCNjcR9t4N";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    private int goldPosition = 0;
    boolean unlocked = true;

    public void turn_right(double speed, long time) {
        left_drive.setPower(speed * 0.25);
        right_drive.setPower(speed * -0.25);
        sleep(time);
        left_drive.setPower(0);
        right_drive.setPower(0);
    }



    public void turn_left(double speed, long time) {
        left_drive.setPower(speed * -0.25);
        right_drive.setPower(speed * 0.25);
        sleep(time);
        left_drive.setPower(0);
        right_drive.setPower(0);
    }


    // todo: write your code here
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
        claiming = hardwareMap.get(Servo.class, "claiming");
        latching = hardwareMap.get(DcMotor.class , "latching");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        left_drive.setDirection(DcMotor.Direction.FORWARD);
        right_drive.setDirection(DcMotor.Direction.REVERSE);
        claiming.setPosition(0.1);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        if(opModeIsActive()){

            if (tfod != null) {
                tfod.activate();
            }


            while(opModeIsActive()){

                if (tfod != null) {

                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());

                        if (updatedRecognitions.size() == 3) {
                            int goldMineralX = -1;
                            int silverMineral1X = -1;
                            int silverMineral2X = -1;

                            for (Recognition recognition : updatedRecognitions) {
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = (int) recognition.getLeft();
                                } else if (silverMineral1X == -1) {
                                    silverMineral1X = (int) recognition.getLeft();
                                } else {
                                    silverMineral2X = (int) recognition.getLeft();
                                }
                            }
                            if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                                if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {

                                    telemetry.addData("Gold Mineral Position", "Left");
                                    goldPosition = 0;

                                } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {

                                    telemetry.addData("Gold Mineral Position", "Right");
                                    goldPosition = 1;

                                } else {

                                    telemetry.addData("Gold Mineral Position", "Center");
                                    goldPosition = 2;

                                }
                            }
                        }

                        telemetry.update();
                    }

                }

                //Movement code in here
                if(unlocked){

                    if(goldPosition == 0){

                        turn_left(0.25, 500);
                        unlocked = false;

                    } else if(goldPosition == 1){

                        right_drive.setPower(0.4);
                        left_drive.setPower(0.4);

                        unlocked = false;
                    } else if (goldPosition == 2){

                        unlocked = false;
                    }
                }
            }
        }

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

