// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//MOTOR IMPORTS:
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

//OTHER IMPORTS:
import edu.wpi.first.wpilibj.Joystick;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DigitalInput;
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


  //drive
  private CANSparkMax leftDriveMotor1;
  private CANSparkMax leftDriveMotor2;
  private CANSparkMax rightDriveMotor1;
  private CANSparkMax rightDriveMotor2;
  private RelativeEncoder encoder;
  //shooter + intake
  private WPI_TalonFX shooterMotor;
  private WPI_TalonSRX intakeMotor;
  private DigitalInput intakeSwitch;
  private Timer intakeTimer;

  //CLASS VARIABLES:
  private Drive drive;
  private Joystick joystick;
  private Limelight limelight;
  private Shooter shooter;
  private Intake intake;
  private AHRS navX;
  private Autonomous autonomous;
  
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    //MOTOR INITIALIZATIONS:
    
    //drive
    leftDriveMotor1 = new CANSparkMax(6, MotorType.kBrushless);         //using ports on practice robot
    leftDriveMotor2 = new CANSparkMax(15, MotorType.kBrushless);  
    rightDriveMotor1 = new CANSparkMax(16, MotorType.kBrushless);
    rightDriveMotor2 = new CANSparkMax(3, MotorType.kBrushless);
    encoder = rightDriveMotor2.getEncoder();
  
    //shooter + intake
    shooterMotor = new WPI_TalonFX(1);
    intakeMotor = new WPI_TalonSRX(8);
    intakeSwitch = new DigitalInput(2);
    intakeTimer = new Timer();

    //CLASS INITIALIZATIONS:
    drive = new Drive(leftDriveMotor1, leftDriveMotor2, rightDriveMotor1, rightDriveMotor2);
    joystick = new Joystick(0);
    limelight = new Limelight();
    intake = new Intake(intakeMotor, intakeSwitch, intakeTimer);
    shooter = new Shooter(limelight, shooterMotor, drive);
    navX = new AHRS(Port.kMXP);
    autonomous = new Autonomous(drive, shooter, intake, encoder, navX);
    
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
    //intake.displayMethod();
    shooter.displayValues();
    SmartDashboard.putNumber("Shooter Output", shooterMotor.get());
    SmartDashboard.putNumber("Encoder Counts", encoder.getPosition());
    //autonomous.display();
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

    autonomous.run();
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    limelight.setDrivingMode();
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    drive.arcadeControl(joystick.getX(), joystick.getY()); 

    if(joystick.getRawButton(1)){
      shooter.setLowHubShoot();
      //shooterMotor.set(-.795);
    }
    /*else if(joystick.getRawButton(3)){
      //shooter.setUpperHubShoot();
    }
    else if(joystick.getRawButton(4)){
      //shooter.setLaunchPadShoot();
    }*/
    else{
      shooter.setStop();
      //shooterMotor.set(0);
    }
    //shooterMotor.set(joystick.getRawAxis(3));

    if(joystick.getRawButton(2)){
      intake.setFeedingMode();
      //intake.setOverrideMode();
    }
    else if(joystick.getRawButton(3)){
      intake.setIntakeMode();;
    }
    else if(joystick.getPOV() == 0){
      intake.setOutakeMode();
    }
    else{
      intake.setStopMode();
    }

    if(joystick.getRawButton(9)){
      limelight.setDrivingMode();
    }
    else if(joystick.getRawButton(10)){
      limelight.setTrackingMode();
    }

    if(joystick.getRawButton(7)){
      encoder.setPosition(0);
    }

    limelight.run();
    shooter.run();
    intake.run();
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    autonomous.reset();
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
      SmartDashboard.putString("ROUTINE", "THREE BALL");
      autonomous.setThreeBall();
    }
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
