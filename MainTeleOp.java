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
  private int servoCnt = 0;

  private boolean precise = false;
  private boolean servoDn = false;

  private Servo swingL, swingR;
  private double[] in = {0,0};

  public void runOpMode(){

    FL = hardwareMap.get(DcMotor.class, "FL");
    FR = hardwareMap.get(DcMotor.class, "FR");
    BL = hardwareMap.get(DcMotor.class, "BL");
    BR = hardwareMap.get(DcMotor.class, "BR");

    swingL = hardwareMap.get(Servo.class, "swingL");
    swingR = hardwareMap.get(Servo.class, "swingR");

    g1 = gamepad1;
    g2 = gamepad2;


    waitForStart();

    while(opModeIsActive()){

      normalOps(); // need seperate method to continue normal functions in embedded while loops
      /*
      while(gamepad1.right_stick_y != 0){
      normalOps();
      pivotMotor.setPower(gamepad1.right_stick_y);
      pivoted = true;

      }

      if(pivoted){
          int dtheta = encoderticks;
          articulating.setTargetPosition(-detheta? * revRatio);
          articulating.setPower((dtheta/abs(dtheta)) * 0.2);
          boolean interrupted = false;
          while(articulating.isBusy()){
            normalOps();
            if(gamepad1.right_stick_y != 0){
            interrupted = true;
            articulating.setPower(0);
            break;
          }
        }
        if(!interrupted){
        articulating.setPower(0);
        pivotMotor.resetEncoder();
        articulating.resetEncoder();
      }
      }

      */
      telemetry.addData("pwrToggle: ", precise);
      telemetry.addData("g1 stats: ", Arrays.toString(in));
      telemetry.update();


    }
    }

    public void normalOps(){
      in[0] = g1.left_stick_x;
      in[1] = g1.left_stick_y;

      if(g1.a){
        pwrToggleCnt++;
        if(pwrToggleCnt > 150){
          pwrToggleCnt = 0;
        }
      } else {

      if(pwrToggleCnt != 0){
        precise = !precise;
        pwrToggleCnt = 0;
      }

      if(g1.right_trigger > 0 || g1.left_trigger > 0){
        FL.setPower(g1.right_trigger - g1.left_trigger);
        BL.setPower(g1.right_trigger - g1.left_trigger);
        FR.setPower(g1.right_trigger - g1.left_trigger);
        BR.setPower(g1.right_trigger - g1.left_trigger);
      } else {

        if(!precise){
        //Fast mode
        FL.setPower(in[0] - in[1]);
        FR.setPower((in[0] + in[1]));
        BL.setPower((-in[0] - in[1]));
        BR.setPower((-in[0] + in[1]));
        } else {
        //Precise mode
        FL.setPower((in[0] - in[1])/4);
        FR.setPower((in[0] + in[1])/4);
        BL.setPower((-in[0] - in[1])/4);
        BR.setPower((-in[0] + in[1])/4);
        }
      }
    }
    if(g2.left_bumper) {
      servoCnt++;
      if(servoCnt > 150){
        servoCnt = 0;
      }
    } else {

      if(servoCnt != 0){
        servoDn = !servoDn;
        servoCnt = 0;
      }

      if(servoDn){
        swingL.setPosition(1);
        swingR.setPosition(0.8);
      } else {
        swingL.setPosition(0);
        swingR.setPosition(0);
      }
    }

  }


}
