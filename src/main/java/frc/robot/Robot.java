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
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

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
  private CANSparkMax leftDriveMotor1;        //CAN ID:   7
  private CANSparkMax leftDriveMotor2;        //CAN ID:   8
  private CANSparkMax rightDriveMotor1;       //CAN ID:   5
  private CANSparkMax rightDriveMotor2;       //CAN ID:   6
  private RelativeEncoder encoder;            //dont know which motor to use for encoder yet

  //SHOOTER VARIABLES:
  private WPI_TalonFX shooterMotor;               //CAN ID:   1

  //INTAKE VARIABLES:
  private WPI_TalonSRX intakeMotor;               //CAN ID:   3 
  private WPI_VictorSPX outerIntakeMotor;         //CAN ID:   0
  //private WPI_VictorSPX intakeExtensionMotor;     //CAN ID:   1
  private WPI_TalonSRX intakeExtensionMotor;      //CAN ID:   5

  private DigitalInput intakeSwitch;              //DIO Port: 4
  private DigitalInput intakeExtensionChannel;    //DIO Port: 5
  private DigitalInput intakeExtensionSwitch;     //DIO Port: 6  
  private SingleChannelEncoder intakeExtensionEncoder; 
  private Timer intakeTimer;              

  //CLASS VARIABLES:
  private Joystick joystick;        
  private Drive drive;
  private Limelight limelight;
  private Shooter shooter;
  private Intake intake;
  private AHRS navX;
  private Autonomous autonomous;


  //USED FOR ENCODER TESTING:
  //private final double countsPerFoot = -11.7935016;

  /*SHOOTER TEST VALUES:

  TRIAL 1 (11.8V ~ 12.2V)-
    5  ft.  N/A
    8  ft.  N/A
    10 ft.  ~0.750   ~4850
    11 ft.  ~0.775   ~5000
    12 ft.  ~0.790   ~5130
    13 ft.  ~0.790   ~5130
    14 ft.  ~0.840   ~5450
    15 ft.  ~0.870   ~5630
    16 ft.  ~0.920   ~5900
    17 ft.  ~0.950   ~6050
    18 ft.  ~1.000   ~6380
    19 ft.  N/A
    20 ft.  N/A

  TRIAL 2 () -
    5  ft.  N/A
    8  ft.  0.720    ~4620
    9  ft.  0.730    ~4690
    10 ft.  0.750    ~4840
    11 ft.  0.775    ~5000
    12 ft.  0.800    ~5190
    13 ft.  0.820    ~5330
    14 ft.  0.850    ~5520
    15 ft.  0.890    ~5720
    16 ft.  0.930    ~5950
    17 ft.  0.975    ~6200 (SOMEWHAT CONSISTENT)   
    18 ft.  1.000    ~6380 (INCONSISTENT)
    19 ft.
    20 ft.



    rim to ll = 139.25 in =  11.6041667 ft
    angle offset of ll from 12ft = 2.52 degrees
    angle of limelight = ???
    height of target = 8.6666667
  */


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
    
    //sets the motors to be in brake mode
    leftDriveMotor1.setIdleMode(IdleMode.kBrake);
    leftDriveMotor2.setIdleMode(IdleMode.kBrake);
    rightDriveMotor1.setIdleMode(IdleMode.kBrake);
    rightDriveMotor2.setIdleMode(IdleMode.kBrake);
  
    //SHOOTER:
    shooterMotor = new WPI_TalonFX(1);

    //INTAKE:
    intakeMotor = new WPI_TalonSRX(3);
    intakeSwitch = new DigitalInput(4);
    intakeMotor.setNeutralMode(NeutralMode.Brake);

    intakeExtensionMotor = new WPI_TalonSRX(5);
    intakeExtensionChannel = new DigitalInput(5);
    intakeExtensionSwitch = new DigitalInput(6);
    intakeExtensionEncoder = new SingleChannelEncoder(intakeExtensionMotor, intakeExtensionChannel);
    
    outerIntakeMotor = new WPI_VictorSPX(0);
    outerIntakeMotor.setNeutralMode(NeutralMode.Brake);

    intakeTimer = new Timer();

    //CLASSES:
    joystick = new Joystick(0);
    drive = new Drive(leftDriveMotor1, leftDriveMotor2, rightDriveMotor1, rightDriveMotor2);
    navX = new AHRS(Port.kMXP);
    navX.reset();

    limelight = new Limelight();
    shooter = new Shooter(limelight, shooterMotor);

    intake = new Intake(intakeMotor, intakeExtensionMotor, outerIntakeMotor, intakeExtensionEncoder, intakeSwitch, intakeExtensionSwitch, intakeTimer);
    
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
    shooter.displayValues();
    //SmartDashboard.putBoolean("Ball?", intake.cargoCheck());
    //SmartDashboard.putBoolean("Arm Down", intake.armIsDown());
    //SmartDashboard.putNumber("Gyro Yaw", navX.getYaw());

    //USED FOR ENCODER TESTING
    //SmartDashboard.putNumber("Encoder Value", encoder.getPosition());

    //USED TO CHECK IF SHOOTER SENSOR IS WORKING FOR NOW
    //double shooterRPM = (shooterMotor.getSelectedSensorVelocity() * 600)/2048;
    //SmartDashboard.putNumber("Shooter RPM", shooterRPM);

    //SmartDashboard.putNumber("Motor Output", shooterMotor.get()); 
    //SmartDashboard.putNumber("Set Speed", SmartDashboard.getNumber("Set Speed", 0));
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
    autonomous.reset();
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

    autonomous.display();
    autonomous.run();
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    limelight.setTrackingMode();
    autonomous.deleteDisplays();
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    //DRIVE:
    if(joystick.getRawButton(1)){
      drive.arcadeControl(joystick.getX() + shooter.getAlignSpeed(), joystick.getY() + shooter.getRangeSpeed());
    }
    else{
      drive.arcadeControl(joystick.getX(), joystick.getY()); 
    }

    //SHOOTER (LOW HUB ONLY) (DRIVER)
    if(joystick.getRawButton(1)){
      //shooter.setTesting();
      //shooter.setManual(0.4/*SmartDashboard.getNumber("Set Speed", 0)*/);
      //shooter.setLowHubShoot();
      shooter.setUpperHubShoot();
    }
    else if(joystick.getRawButton(6)){
      shooter.setLowHubShoot();
    }
    else if(joystick.getRawButton(7)){
      shooter.setLaunchPadShoot();
    }
    else{
      shooter.setStop();
    }

    //INTAKE (TEMPORARY) (DRIVER)
    if(joystick.getRawButton(2)){
      intake.setFeedingMode();;
    }
    else if(joystick.getPOV() == 0){
      intake.setOutakeMode();;
    }
    else if(joystick.getRawButton(5)){
      intake.setIntakeMode();
    }
    else if(joystick.getPOV() == 180){
      intake.setOverrideMode();
    }
    else{
      intake.setIntakeStopMode();;
    }

    //LIMELIGHT (DRIVER)
    if(joystick.getRawButton(11)){
      limelight.setDrivingMode();
    }
    else if(joystick.getRawButton(12)){
      limelight.setTrackingMode();
    }

    //INTAKE EXTENSION ARM (FOR AUTO TESTING CONVENIENCE)
    if(joystick.getRawButton(3)){
      intake.setRetract();
    }
    else if(joystick.getRawButton(4)){
      intake.setExtend();
    }
    else{
      intake.setArmStopMode();;
    }

    //NAVX RESET (FOR AUTO TESTING CONVENIENCE)
    if(joystick.getRawButton(7)){
      navX.reset();
    }

    //RUN!!!
    shooter.run();
    limelight.run();
    intake.intakeRun(); ; 

    //USED FOR ENCODER TESTING
    /*
    if(joystick.getRawButton(7)){
      encoder.setPosition(0);
    }

    if(joystick.getRawButton(9)){
      if(encoder.getPosition() < 5 * countsPerFoot){
        drive.tankRun(0, 0);
      }
      else{
        drive.tankRun(0.505, 0.5);
      }
    }

    else if(joystick.getRawButton(10)){
      if(encoder.getPosition() >= 0){
        drive.tankRun(0, 0);
      }
      else{
        drive.tankRun(-0.505, -0.5);
      }
    }
    */
  
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    autonomous.reset();
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
    if(joystick.getRawButton(1)){
      SmartDashboard.putString("ROUTINE", "NOTHING");
      autonomous.setNothing();
    }

    else if(joystick.getRawButton(2)){
      SmartDashboard.putString("ROUTINE", "ONE BALL");
      autonomous.setOneBall();
    }

    else if(joystick.getRawButton(3)){
      SmartDashboard.putString("ROUTINE", "TWO BALL A");
      autonomous.setTwoBallA();
    }

    else if(joystick.getRawButton(4)){
      SmartDashboard.putString("ROUTINE", "TWO BALL B");
      autonomous.setTwoBallB();
    }

    else if(joystick.getRawButton(5)){
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
