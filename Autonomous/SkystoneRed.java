package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import java.util.Arrays;
import java.lang.reflect.Array;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import java.util.List;
import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

@Autonomous(name="SkystoneRed", group="Linear Opmode")
public class SkystoneRed extends LinearOpMode {
    
    private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Stone";
    private static final String LABEL_SECOND_ELEMENT = "Skystone";

    private static final String VUFORIA_KEY =
            "AVFqPUj/////AAABmUZq2lNDVkH3oO3zcUMuhFcPxlq7hd423wLEOcLpQlY5b/ASq1SQUZ3mrQoe0f1fsWJzB6Y8MtKD0qwvNR569fy85lgYP4C1A+0hfLtHK0LkzP4pDjQntey02WT7N3wLXjliJYqMxXPdSrxn2/+XMlNmjA7/FlrMP9UesjD5srMCKKy8ERG1NF6qK3B+Hbw+U6L10ojIaODz3YoOgurGyOHt3FXScmdhyeQYBCJrTSPWBDwlis9dYAVZgIvuKFsXKVYr8JUdWfkGLbt+11orICZb1MoYMop+3b6I/Pl2fx0urnUqYjgbGIDOaL7Atziss+A6sSgm9QCiyzHxrdk4oqUcCnvQ2oioyDWCNjcR9t4N";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    
    private List<Recognition> recs;
    
    private final int TOLERANCE = 10;

    private DcMotor FL, FR, BL, BR, pivotL, pivotR, extension, articulating;
    private DcMotor[] drives;

    private Servo swingL, swingR, stoneL, stoneR, skystone;
    private ColorSensor color;
    private DistanceSensor dist;
    private int colVal = -1;

    private double[] getPowers(double x, double y) {
        return new double[]{x - y, x + y, -x - y, -x + y};
    }

    private void moveRev(double revs, double dirX, double dirY) {
        double[] powers = getPowers(dirX, dirY);
        for (int i = 0; i < drives.length; i++) {
            int initialPosition = drives[i].getCurrentPosition();
            drives[i].setTargetPosition(initialPosition + (int)(powers[i] * 244 * revs));
            drives[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drives[i].setPower(powers[i]);
        }
        boolean done = false;
        long begin = System.currentTimeMillis();
        while (!done && (FL.isBusy()&&FR.isBusy()&&BL.isBusy()&&BR.isBusy())) {
            done = true;
            for (int i = 0; i < drives.length; i++)
                if (Math.abs(drives[i].getCurrentPosition() - powers[i] * 244 * revs) > TOLERANCE)
                    done = false;
            if (System.currentTimeMillis() - begin > revs * 700 / Math.max(dirX, dirY))
                done = true;
            telemetry.addData("FL", FL.getCurrentPosition());
            telemetry.addData("FR", FR.getCurrentPosition());
            telemetry.addData("BL", BL.getCurrentPosition());
            telemetry.addData("BR", BR.getCurrentPosition());
            telemetry.update();
        }
        for (DcMotor i : drives) {
            i.setPower(0);
            i.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    // true for clockwise, false for counterclockwise
    // 1 for every 90 degrees
    private void rotateRev(double revs, boolean dir, double power) {
        int rotateAmount = dir ? 585 : -585;
        for (int i = 0; i < drives.length; i++) {
            drives[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            drives[i].setTargetPosition((int)(rotateAmount * revs));
            drives[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drives[i].setPower(dir ? power : -power);
        }
        boolean done = false;
        while (!done && (FL.isBusy()&&FR.isBusy()&&BL.isBusy()&&BR.isBusy())) {
            for (int i = 0; i < drives.length; i++)
                if (Math.abs(drives[i].getCurrentPosition() - rotateAmount * revs) < TOLERANCE)
                    done = true;
            telemetry.addData("FL", FL.getCurrentPosition());
            telemetry.addData("FR", FR.getCurrentPosition());
            telemetry.addData("BL", BL.getCurrentPosition());
            telemetry.addData("BR", BR.getCurrentPosition());
            telemetry.update();
        }
        for (DcMotor i : drives) {
            i.setPower(0);
            i.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    // false for up, true for down
    private void setFoundationServo(boolean state) {
        if (state) {
            swingL.setPosition(0.978);
            swingR.setPosition(0.725);
        } else {
            swingL.setPosition(0.75);
            swingR.setPosition(0.55);
        }
    }

    public void runOpMode() {
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        drives = new DcMotor[]{FL, FR, BL, BR};
        
        drives[0].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drives[1].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drives[2].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drives[3].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        //ARM MOTORS
        pivotL = hardwareMap.get(DcMotor.class, "pivotL");
        pivotR = hardwareMap.get(DcMotor.class, "pivotR");
        extension = hardwareMap.get(DcMotor.class, "extension");
        articulating = hardwareMap.get(DcMotor.class, "articulating");

        //FOR FOUNDATION MOVEMENT
        swingL = hardwareMap.get(Servo.class, "swingL");
        swingR = hardwareMap.get(Servo.class, "swingR");

        //FOR STONE GRABBING
        stoneL = hardwareMap.get(Servo.class, "stoneL");
        stoneR = hardwareMap.get(Servo.class, "stoneR");
        
        skystone = hardwareMap.get(Servo.class, "skystoneServo");
        
        color = hardwareMap.get(ColorSensor.class, "color");
        dist = hardwareMap.get(DistanceSensor.class, "dist");

        for (DcMotor i : drives)
            i.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pivotL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        pivotR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        pivotL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivotR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.update();

        setFoundationServo(false);
        color.enableLed(false);
        
        while(!opModeIsActive()){
            telemetry.addData("col ", dist.getDistance(DistanceUnit.CM));
            telemetry.update();
        }
        
        telemetry.update();

        waitForStart();

        moveRev(7.6, -0.5, 0);
        telemetry.addData("dist ", dist.getDistance(DistanceUnit.CM));
        telemetry.update();
        sleep(500);
        if(dist.getDistance(DistanceUnit.CM) < 10){
            moveRev(0.2, 0.5, 0);
        } else if (dist.getDistance(DistanceUnit.CM) > 15){
            moveRev(0.2, -0.2, 0);
        }
        sleep(500);
        double offset = 0;
        for(int i = 0; i < 2; i++){
            long initTime = System.currentTimeMillis();
            
            while((System.currentTimeMillis() - initTime) < 500){}
              
            if(skystoneFound()){
                break;
            }
            moveRev(0.75, 0, 1);
            offset += 0.75;
            sleep(500);
        }
        if(offset == 0){
            moveRev(0.25, 0, -0.5);
        }
        moveRev(0.7, -1, 0);
        //moveRev(0.1, 0, -1);
       // moveRev(0.1, -1, 0);
        
        skystone.setPosition(0.66);
        sleep(1000);
        
        moveRev(1.7, 1, 0);
        moveRev(5 + offset, 0, -1);
        
        skystone.setPosition(0.3);
        sleep(1000);
        
        moveRev(7.7 + offset, 0, 1);
        moveRev(1.7, -1, 0);
        skystone.setPosition(0.66);
        sleep(1000);
        moveRev(1.7, 1, 0);
        moveRev(7.7 + offset, 0, -1);
        skystone.setPosition(0.3);
        sleep(1000);
        
    }
    
     private void initVuforia() {
       
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.8;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
    
    private boolean skystoneFound(){
       
       if(color.green() < 28){
           return true;
       }
       return false;
    }
}
