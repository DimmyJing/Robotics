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

@TeleOp(name="MainTeleOp", group="Linear Opmode")

class MainTeleOp extends LinearOpMode{

  private DCMotor FL, FR, BL, BR;
  Gamepad g1, g2;


  public void runOpMode(){

    FL = hardwareMap.get(DcMotor.class, "FL");
    FR = hardwareMap.get(DcMotor.class, "FR");
    BL = hardwareMap.get(DcMotor.class, "BL");
    BR = hardwareMap.get(DcMotor.class, "BR");
    g1 = gamepad1;
    g2 = gamepad2;

    waitForStart();

    while(opModeIsActive()){
      //Divide by 4.15 because sin(x) + cos(x) has a max of +/-4.15. Want to scale down max power to +/-1.
      FL.setPower((g1.right_stick_x + g1.right_stick_y)/4.15);
      FR.setPower((g1.right_stick_x - g1.right_stick_y)/4.15);
      BL.setPower((g1.right_stick_y - g1.right_stick_x)/4.15);
      BR.setPower((-g1.right_stick_y - g1.right_stick_x)/4.15);


    }


  }


}
