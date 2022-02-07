package frc.robot;

import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import javax.print.DocFlavor.READER;

import com.kauailabs.navx.frc.AHRS;

public class Autonomous {

    //SENSOR VARIABLES:
    private RelativeEncoder encoder;
    private AHRS gyro;

    //CLASS VARIABLES:
    private Drive drive;
    private Shooter shooter;
    private Intake intake;

    //COUNTER VARIABLES:
    private int oneBallCounter = 0;
    private int twoBallCounter = 0;

    //OTHER:
    private final double encCountsPerFoot = 0;
    
    public Autonomous(Drive newDrive, Shooter newShooter, Intake newIntake, RelativeEncoder newEncoder, AHRS newGyro){
        drive = newDrive;       
        shooter = newShooter;
        intake = newIntake;
        encoder = newEncoder;
        gyro = newGyro;
    }

    private enum routines{
        NOTHING, ONEBALL, TWOBALL
    }

    private routines routineState = routines.NOTHING;

    public void setNothing(){
        routineState = routines.NOTHING;
    }

    public void setOneBall(){
        routineState = routines.ONEBALL;
    }

    public void setTwoBallAuto(){
        routineState = routines.TWOBALL;
    }

    public void display(){
        SmartDashboard.putNumber("One Ball Counter", oneBallCounter);
        SmartDashboard.putNumber("Two Ball Counter", twoBallCounter);
        SmartDashboard.putNumber("Encoder Counts", encoder.getPosition());
        SmartDashboard.putNumber("Gyro Yaw", gyro.getYaw());
    }

    public void reset(){
        encoder.setPosition(0);
        gyro.reset();
        oneBallCounter = 0;
        twoBallCounter = 0;
    }

    private double convertFeetToEncoderCounts(double feet){
        return feet * encCountsPerFoot;
    }

    private void nothing(){

    }

    private void oneBall(){
        switch(oneBallCounter){
            case 1:     //shoot preload into the upper hub
                if(intake.cargoCheck()){
                    shooter.setStop();
                    oneBallCounter++;
                }
                else{
                    shooter.setUpperHubShoot();
                }
            break;

            case 2:     //taxi
                if(encoder.getPosition() >= convertFeetToEncoderCounts(0)){
                    drive.arcadeRun(0, 0);
                    oneBallCounter++;
                }

                else{
                    drive.arcadeRun(0, 0);
                }
            break;
        }
    }

    private void twoBall(){
        switch(twoBallCounter){
            case 0:     //shoot the preload into the uppper hub                           
                if(!intake.cargoCheck()){
                    shooter.setStop();
                    twoBallCounter++;
                }
                else{
                    shooter.setUpperHubShoot();
                }      
            break;
            
            case 1:     //turn around to face the cargo ball
                if(gyro.getYaw() > 0 && gyro.getYaw() < 0 ){
                    drive.arcadeRun(0, 0);
                    twoBallCounter++;
                }
                else{
                    drive.arcadeRun(0, 0);
                }
            break;

            case 2:     //intake the ball
                if(intake.cargoCheck()){
                    drive.arcadeRun(0, 0);
                    intake.setStopMode();
                    twoBallCounter++;
                }
                else{
                    intake.setIntakeMode();
                    drive.arcadeRun(0, 0);
                }
            break;

            case 3:     //turn back to face the upper hub
                if(gyro.getYaw() > 0 && gyro.getYaw() < 0){
                    drive.arcadeRun(0, 0);
                    twoBallCounter++;
                }
                else{
                    drive.arcadeRun(0, 0);
                }
            break; 

            case 4:     //shoot the ball into the upper hub
                if(intake.cargoCheck()){
                    shooter.setStop();
                    twoBallCounter++;
                }
                else{
                    shooter.setUpperHubShoot();
                }
            break;
        }
    }

    public void run(){
        switch(routineState){
            case NOTHING:
                
            break;
            case ONEBALL:

            break;
            case TWOBALL:

            break;

        }
    }
}
