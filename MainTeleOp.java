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
  private int pwrToggleCnt = 0;
  private int lastPwrCnt = 0;
  private boolean precise = false;

  private Servo swingL, swingR;


  public void runOpMode(){

    FL = hardwareMap.get(DcMotor.class, "FL");
    FR = hardwareMap.get(DcMotor.class, "FR");
    BL = hardwareMap.get(DcMotor.class, "BL");
    BR = hardwareMap.get(DcMotor.class, "BR");

    swingL = hardwareMap.get(Servo.class, "swingL");
    swingR = hardwareMap.get(Servo.class, "swingR");

    g1 = gamepad1;
    g2 = gamepad2;
    double[] in = {0,0};


    waitForStart();

    while(opModeIsActive()){
      //Divide by 4.15 because sin(x) + cos(x) has a max of +/-4.15. Want to scale down max power to +/-1.
      in[0] = g1.left_stick_x;
      in[1] = g1.left_stick_y;

      if(g1.a){
        pwrToggleCnt++;
        if(pwrToggleCnt > 150){
          pwrToggleCnt = 0;
        }
      } else {

      if(pwrToggleCnt != lastPwrCnt){
        precise = !precise;
        lastPwrCnt = pwrToggleCnt;
      }

      if(g1.right_trigger > 0 && g1.left_trigger == 0){
        FL.setPower(g1.right_trigger);
        BL.setPower(g1.right_trigger);
        FR.setPower(g1.right_trigger);
        BR.setPower(g1.right_trigger);
      } else if (g1.right_trigger == 0 && g1.left_trigger > 0){
        FL.setPower(-g1.left_trigger);
        BL.setPower(-g1.left_trigger);
        FR.setPower(-g1.left_trigger);
        BR.setPower(-g1.left_trigger);
      } else {

        if(!precise){
        FL.setPower(in[0] - in[1]);
        FR.setPower((in[0] + in[1]));
        BL.setPower((-in[0] - in[1]));
        BR.setPower((-in[0] + in[1]));
        } else {
        FL.setPower((in[0] - in[1])/4);
        FR.setPower((in[0] + in[1])/4);
        BL.setPower((-in[0] - in[1])/4);
        BR.setPower((-in[0] + in[1])/4);
        }
      }

      if(g1.left_bumper){
        swingL.setPosition(1);
        swingR.setPosition(0.8);
      } else {
        swingL.setPosition(0);
        swingR.setPosition(0);
      }

      telemetry.addData("pwrToggle: ", precise);
      telemetry.addData("g1 stats: ", Arrays.toString(in));
      telemetry.update();


    }
    }


  }


}
