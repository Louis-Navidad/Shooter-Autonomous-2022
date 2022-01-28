package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;

public class Shooter {

    //MOTOR VARIABLES
    private MotorController intakeMotor;
    private MotorController shooterMotor;

    //CLASS VARIABLES
    private Limelight limelight;
    
    //CONSTANTS
    private final double cameraHeight = 0;
    private final double targetHeight = 0;
    private final double cameraAngleDegrees = 0;
    private final double shooterAngleDegrees = 0;
    private final double flywheelRadiusFeet = 0;

    public Shooter(Limelight newlimelight, MotorController newIntakeMotor, MotorController newShooterMotor){
        limelight = newlimelight;
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

    public double getDistance(){
        double heightDiff = targetHeight - cameraHeight;
        double cameraAngleRad = Math.toRadians(cameraAngleDegrees);
        double angleDiffRad = Math.toRadians(limelight.getYOffset());

        if(limelight.checkTargetSeen()){
            return (heightDiff/Math.tan(cameraAngleRad + angleDiffRad));
        }

        else{
            return -1;
        }
    }

    public double getIdealVelocity(){
        double heightDiff = targetHeight - cameraHeight;
        double distance = getDistance();
        double shooterAngleRad = Math.toRadians(shooterAngleDegrees);
        
        if(distance > 0){
            return Math.sqrt((16*distance*distance)/((Math.sin(shooterAngleRad) * Math.tan(shooterAngleRad)) - (Math.pow(Math.cos(shooterAngleRad), 2) * heightDiff))); 
        }

        else{
            return 0;
        }
    }

    public double getIdealRPM(){
        double velocity = getIdealVelocity();
        return (30 * velocity)/(Math.PI * flywheelRadiusFeet);
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
