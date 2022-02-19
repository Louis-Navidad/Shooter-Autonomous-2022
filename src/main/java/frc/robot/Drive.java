package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drive {

    private MotorControllerGroup leftSide;
    private MotorControllerGroup rightSide;
    private DifferentialDrive diffDrive;

    public Drive(MotorController left1, MotorController left2, MotorController right1, MotorController right2){
        leftSide = new MotorControllerGroup(left1, left2);
        rightSide = new MotorControllerGroup(right1, right2);
        diffDrive = new DifferentialDrive(leftSide, rightSide);
    }

    private double deadzone(double input){
        if(Math.abs(input) > 0.2){
            return input;
        }
        else{
            return 0;
        }
    }

    public void arcadeControl(double xChannel, double yChannel){
        diffDrive.arcadeDrive(deadzone(-xChannel), deadzone(yChannel));
    }

    public void arcadeRun(double xChannel, double yChannel){
        diffDrive.arcadeDrive(-xChannel, yChannel);
    }

    public void tankRun(double leftChannel, double rightChannel){
        diffDrive.tankDrive(-leftChannel, rightChannel);
    }
}
