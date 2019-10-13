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

public class MainTeleOp extends LinearOpMode{

  private DcMotor FL, FR, BL, BR;
  private Gamepad g1, g2;


  public void runOpMode(){

    FL = hardwareMap.get(DcMotor.class, "FL");
    FR = hardwareMap.get(DcMotor.class, "FR");
    BL = hardwareMap.get(DcMotor.class, "BL");
    BR = hardwareMap.get(DcMotor.class, "BR");
    
    g1 = gamepad1;
    g2 = gamepad2;
    double[] in = {0,0};

    waitForStart();

    while(opModeIsActive()){
      //Divide by 4.15 because sin(x) + cos(x) has a max of +/-4.15. Want to scale down max power to +/-1.
      in[0] = g1.right_stick_x;
      in[1] = g1.right_stick_y;
      FL.setPower((in[0] - in[1])/4.15);
      FR.setPower((in[1] + in[0])/4.15);
      BL.setPower((-in[1] - in[0])/4.15);
      BR.setPower((in[1] - in[0])/4.15);
      telemetry.addData("g1 stats: ", Arrays.toString(in));
      telemetry.update();


    }


  }


}
