package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;

public class Shooter {

    private MotorController intakeMotor;
    private MotorController shooterMotor;
    
    public Shooter(MotorController newIntakeMotor, MotorController newShooterMotor){
        intakeMotor = newIntakeMotor;
        shooterMotor = newShooterMotor;
    }

    private enum state{
        STOP, INTAKE, SHOOT
    }

    private state shooterState = state.STOP;

    public void stop(){
        shooterState = state.STOP;
    }

    public void intake(){
        shooterState = state.INTAKE;
    }

    public void shoot(){
        shooterState = state.SHOOT;
    }

    private void stopMotors(){
        intakeMotor.stopMotor();
        shooterMotor.stopMotor();
    }

    private void setIntake(){
        intakeMotor.set(0.5);       //placeholder speed for now
    }

    private void setShoot(){
        shooterMotor.set(0.5);      //placeholder speed for now
    }

    public void run(){
        switch(shooterState){
            case STOP:
                stopMotors();
            break;
            case INTAKE:
                setIntake();
            break;
            case SHOOT:
                setShoot();
            break;
        }

    }

}
