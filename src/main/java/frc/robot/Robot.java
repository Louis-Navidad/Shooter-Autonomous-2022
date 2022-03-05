// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
// spooky pookie cuddlemuffin i love edrich soooooooo much 

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//MOTOR IMPORTS:
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

//SENSOR IMPORTS:
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SPI.Port;

//OTHER IMPORTS:
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();


  //DRIVE VARIABLES:
  private CANSparkMax leftDriveMotor1;    //CAN ID:   7
  private CANSparkMax leftDriveMotor2;    //CAN ID:   8
  private CANSparkMax rightDriveMotor1;   //CAN ID:   5
  private CANSparkMax rightDriveMotor2;   //CAN ID:   6
  private RelativeEncoder encoder;        //dont know which motor to use for encoder yet

  //SHOOTER + INTAKE VARIABLES:
  private WPI_TalonFX shooterMotor;       //CAN ID:   1
  private WPI_TalonSRX intakeMotor;       //CAN ID:   3 
  private DigitalInput intakeSwitch;      //DIO Port: ?  
  private Timer intakeTimer;              

  //CLASS VARIABLES:
  private Joystick joystick;        
  private Drive drive;
  private Limelight limelight;
  private Shooter shooter;
  private Intake intake;
  private AHRS navX;
  private Autonomous autonomous;

  private double setSpeed;
  
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    
    //DRIVE:
    leftDriveMotor1 = new CANSparkMax(7, MotorType.kBrushless);         
    leftDriveMotor2 = new CANSparkMax(8, MotorType.kBrushless);  
    rightDriveMotor1 = new CANSparkMax(5, MotorType.kBrushless);
    rightDriveMotor2 = new CANSparkMax(6, MotorType.kBrushless);
    encoder = leftDriveMotor1.getEncoder();
    
    //sets the motors to be in coast mode
    leftDriveMotor1.setIdleMode(IdleMode.kCoast);
    leftDriveMotor2.setIdleMode(IdleMode.kCoast);
    rightDriveMotor1.setIdleMode(IdleMode.kCoast);
    rightDriveMotor2.setIdleMode(IdleMode.kCoast);
  
    //SHOOTER + INTAKE:
    shooterMotor = new WPI_TalonFX(1);
    intakeMotor = new WPI_TalonSRX(3);
    intakeSwitch = new DigitalInput(2);   //NOT ON ROBOT YET
    intakeTimer = new Timer();

    //CLASSES:
    joystick = new Joystick(0);
    drive = new Drive(leftDriveMotor1, leftDriveMotor2, rightDriveMotor1, rightDriveMotor2);
    navX = new AHRS(Port.kMXP);

    limelight = new Limelight();    //NOT ON ROBOT YET
    shooter = new Shooter(limelight, shooterMotor, drive);

    intake = new Intake(intakeMotor, intakeSwitch, intakeTimer); 
    
    autonomous = new Autonomous(drive, shooter, intake, encoder, navX, limelight);
    
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    //shooter.displayValues();

    //USED TO CHECK IF SHOOTER SENSOR IS WORKING FOR NOW
    double shooterRPM = (shooterMotor.getSelectedSensorVelocity() * 600)/2048;
    SmartDashboard.putNumber("Shooter RPM", shooterRPM);

    //SmartDashboard.putNumber("Set Speed", SmartDashboard.getNumber("Set Speed", 0));
    SmartDashboard.putNumber("Motor Output", shooterMotor.get()); 
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    //autonomous.reset();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }

    //autonomous.display();
    //autonomous.run();
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    //limelight.setDrivingMode;
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    //DRIVE:
    drive.arcadeControl(joystick.getX(), joystick.getY()); 

    if(joystick.getRawButton(1)){
      //shooter.setTesting();
      //shooter.setManual(SmartDashboard.getNumber("Set Speed", 0));
      shooter.setLowHubShoot();
    }
    else{
      shooter.setStop();
    }

    //TEST INTAKE
    if(joystick.getRawButton(2)){
      intakeMotor.set(-1);
    }
    else if(joystick.getPOV() == 0){
      intakeMotor.set(0.5);
    }
    else{
      intakeMotor.stopMotor();
    }

    shooter.run();
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
    if(joystick.getRawButton(5)){
      SmartDashboard.putString("ROUTINE", "NOTHING");
      autonomous.setNothing();
    }
    else if(joystick.getRawButton(6)){
      SmartDashboard.putString("ROUTINE", "ONE BALL");
      autonomous.setOneBall();
    }
    else if(joystick.getRawButton(3)){
      SmartDashboard.putString("ROUTINE", "TWO BALL");
      autonomous.setTwoBall();
    }
    else if(joystick.getRawButton(4)){
      SmartDashboard.putString("ROUTINE", "THREE BALL LOW");
      autonomous.setThreeBallLow();
    }
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
