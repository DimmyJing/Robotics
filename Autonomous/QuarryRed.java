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

@Autonomous(name="QuarryRed", group="Linear Opmode")

public class MainTeleOp extends LinearOpMode{

  private DcMotor FL, FR, BL, BR;


  public void runOpMode(){

    FL = hardwareMap.get(DcMotor.class, "FL");
    FR = hardwareMap.get(DcMotor.class, "FR");
    BL = hardwareMap.get(DcMotor.class, "BL");
    BR = hardwareMap.get(DcMotor.class, "BR");


    waitForStart();

    if(opModeIsActive()){


    }


  }

  public void move_lat(int revs, double power){

    FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    FR.setTargetPosition((int)(-244 * revs));
    FL.setTargetPosition((int)(-244 * revs));
    BR.setTargetPosition((int)(-244 * revs));
    BL.setTargetPosition((int)(-244 * revs));


    FR.setPower(power);
    FL.setPower(-power);
    BL.setPower(power);
    BR.setPower(power);

  }

  public void move_long(){



  }


}
