package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import java.util.Arrays;


public class PIDVals implements Runnable{

    private DcMotor mot;
    private Thread t;
    private int sp; //set point
    public double dRaw = 0;
    public double iRaw;
    
    private double[] trapezoids;
    private double[] derivatives;
    
    public PIDVals(DcMotor mot, int sp){
        this.mot = mot;
        this.sp = sp;
        trapezoids = new double[5];
        derivatives = new double[5];
        Arrays.fill(trapezoids, -1);
        Arrays.fill(derivatives, -1);
    }
    
    public void run(){
      
        
        for(int a = 0; a < 5; a++){
            try{
            double initPos = mot.getCurrentPosition(); //For derivative
            double initErr = mot.getCurrentPosition() - sp; //For integral
            
            double initTime = System.nanoTime();
            
            while(System.nanoTime() < initTime + (5 * Math.pow(10, 6))){}
            
            //Derivative update
            refresh(derivatives, (mot.getCurrentPosition() - initPos) * 200);
            dRaw = average(derivatives);
            
            //Integral update
            double trapArea = ((initErr + (mot.getCurrentPosition() - sp)))/400;
            System.out.println(trapArea);
            refresh(trapezoids, trapArea + sum(trapezoids));
            iRaw = trapezoids[trapezoids.length - 1];
            
            }catch(Exception e){}
        }
       
    }
    
    public void start(){
        if(t == null){
            t = new Thread(this, "");
            t.start();
        }
    }
    
    private double sum(double[] arr){
        double res = 0;
        for(double idx : arr){
            if(idx != -1){
                res += idx;
            }
        }
        return res;
    }
    //Push a new value into the array 
    private void refresh(double[] arr, double newVal){
        for(int idx = 1; idx < arr.length; idx++){
            arr[idx - 1] = arr[idx];
        }
        arr[arr.length - 1] = newVal;
    }
    private double average(double[] arr){
        double res = 0;
        int divCnt = 0;
        for(double idx : arr){
            if(idx != -1){
                res += idx;
                divCnt++;
            }
        }
        return res/(double)divCnt;
    }
}
