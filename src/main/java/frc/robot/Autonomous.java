package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Timer;
import com.revrobotics.RelativeEncoder;

public class Autonomous {

    //SENSOR VARIABLES:
    private RelativeEncoder encoder;
    private AHRS gyro;

    //CLASS VARIABLES:
    private Drive drive;
    private Shooter shooter;
    private Intake intake;
    private Timer timer;
    private Limelight limelight;

    //COUNTER VARIABLES:
    private int oneBallCounter = 0;
    private int twoBallACounter = 0;
    private int twoBallBCounter = 0;
    private int threeBallCounter = 0;
    //private int threeBallLowCounter = 0;

    //CONSTANTS:
    private final double encCountsPerFoot = 11.1029532;
    private final double revDelay = 0.150;
    
    public Autonomous(Drive newDrive, Shooter newShooter, Intake newIntake, RelativeEncoder newEncoder, AHRS newGyro, Limelight newLimelight){
        drive = newDrive;       
        shooter = newShooter;
        intake = newIntake;
        encoder = newEncoder;
        gyro = newGyro;
        limelight = newLimelight;
        timer = new Timer();
    }

    private enum routines{
        NOTHING, ONEBALL, TWOBALL_A, TWOBALL_B, THREEBALL
    }

    private routines routineState = routines.NOTHING;

    public void setNothing(){
        routineState = routines.NOTHING;
    }

    public void setOneBall(){
        routineState = routines.ONEBALL;
    }
    
    public void setTwoBallA(){
        routineState = routines.TWOBALL_A;
    }

    public void setTwoBallB(){
        routineState = routines.TWOBALL_B;
    }

    public void setThreeBall(){
        routineState = routines.THREEBALL;
    }

    /*(REDACTED!!!)
    public void setThreeBallLow(){
        routineState = routines.THREEBALLLOW;
    }
    */


    public void display(){
        //SENSOR VALUES:
        SmartDashboard.putNumber("Encoder Counts", encoder.getPosition());
        SmartDashboard.putNumber("Gyro Yaw", gyro.getYaw());

        //ROUTINE COUNTERS:
        SmartDashboard.putNumber("One Ball Counter", oneBallCounter);
        SmartDashboard.putNumber("Two Ball A Counter", twoBallACounter);
        SmartDashboard.putNumber("Two Ball B Counter", twoBallBCounter);
        SmartDashboard.putNumber("Three Ball Counter", threeBallCounter);
        //SmartDashboard.putNumber("Three Ball Low Counter", threeBallLowCounter);
    }

    public void deleteDisplays(){
        SmartDashboard.delete("Encoder Counts");
        SmartDashboard.delete("Gyro Yaw");
        SmartDashboard.delete("One Ball Counter");
        SmartDashboard.delete("Two Ball A Counter");
        SmartDashboard.delete("Two Ball B Counter");
        SmartDashboard.delete("Three Ball Counter");
        //SmartDashboard.delete("Three Ball Low Counter");
    }


    public void reset(){
        limelight.setTrackingMode();
        encoder.setPosition(0);
        gyro.reset();
        timer.stop();
        timer.reset();
    
        oneBallCounter = 0;
        twoBallACounter = 0;
        twoBallBCounter = 0;
        threeBallCounter = 0;
        //threeBallLowCounter = 0;
    }

    //Converts feet into encoder counts
    private double convertFeetToEncoderCounts(double feet){
        return feet * encCountsPerFoot;
    }

    //Routine to do nothing
    private void nothing(){

    }

    //Routine to TAXI and shoot preload into the UPPER HUB
    private void oneBall(){
        switch(oneBallCounter){

            case 0:
                //TAXI
                if(Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(4)){
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    oneBallCounter++;
                }
                else{
                    drive.tankRun(-0.6, -0.6);
                }
            break;

            case 1:
                //REV SHOOTER, INTAKE DOWN, ALIGN, GET IN RANGE
                if(shooter.checkRPM() && intake.armIsDown() && shooter.checkAligned() && shooter.checkInRange()){
                    drive.arcadeRun(0, 0);
                    encoder.setPosition(0);
                    oneBallCounter++;
                }
                else{
                    //DELETE LATER:
                    shooter.setTesting();
                    shooter.setManual(0.6);
                    //
                    
                    intake.setExtend();
                    //shooter.setUpperHubShoot();
                    //drive.arcadeRun(shooter.alignSpeed, shooter.getInRangeSpeed);
                    drive.arcadeRun(shooter.getAlignSpeed(), shooter.getRangeSpeed());
                }
            break;

            case 2:
                //FEED BALL AND SHOOT
                if(intake.cargoCheck() && timer.get() >= revDelay){
                    timer.stop();
                    timer.reset();

                    shooter.setStop();
                    intake.setIntakeStopMode();
                    oneBallCounter++;
                }
                else{
                    timer.start();
                    intake.setFeedingMode();
                }
            break;

            /*
            case 3:
                //BRING INTAKE AT MIDWAY POINT
                if(intake.atMidway()){
                    intake.setArmStopMode();
                    oneBallCounter++;
                }
                else{
                    intake.setMidway();
                }
            break;
            */
        }
    }

    //USES BEGINNING PORTION OF THE THREE BALL METHOD
    private void twoBallA(){
        switch(twoBallACounter){

            case 0: 
                //BACK UP A BIT TO GET INTO SHOOTING RANGE (MAY NOT NEED)
                if (Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(1.75)) {
                    drive.tankRun(0, 0); 
                    encoder.setPosition(0); 
                    twoBallACounter++; 
                } 
                else {
                    drive.tankRun(-0.6, -0.6); 
                }
            break; 
            
            case 1:
                //REV SHOOTER, INTAKE DOWN, ALIGN, GET IN RANGE
                if(shooter.checkRPM() && intake.armIsDown() && shooter.checkAligned() && shooter.checkInRange()){                
                    drive.arcadeRun(0, 0);
                    encoder.setPosition(0);
                    twoBallACounter++;
                }
                else{
                    //DELETE LATER:
                    shooter.setTesting();
                    shooter.setManual(0.6);
                    //

                    intake.setExtend();
                    //shooter.setUpperHubShoot();
                    //drive.arcadeRun(shooter.alignSpeed, shooter.getInRangeSpeed);
                    drive.arcadeRun(shooter.getAlignSpeed(), shooter.getRangeSpeed());
                }
            break;

            case 2:
                //FEED BALL IN TO SHOOT
                if(intake.cargoCheck() && timer.get() >= revDelay){
                    timer.stop();
                    timer.reset();

                    intake.setIntakeStopMode();
                    shooter.setStop();
                    twoBallACounter++;
                }

                else{
                    timer.start();
                    intake.setFeedingMode();
                }    
            break;

            case 3:
                //TURN LEFT TO FACE BALL BEHIIND
                 if(gyro.getYaw() < -166f && gyro.getYaw() > -170){
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    twoBallACounter++;
                }

                else{
                    if(gyro.getYaw() < 90 && gyro.getYaw() >= -166f){
                        drive.tankRun(-0.6, 0.6);
                    }
                    else if(gyro.getYaw() > 90f && gyro.getYaw() < 180f || gyro.getYaw() > -180f && gyro.getYaw() <= -170){
                        drive.tankRun(0.35, -0.35);
                    }
                }                                                 
            break;

            case 4:     
                //MOVE FORWARD AND INTAKE UNTIL BALL IS IN OR AFTER TRAVELLING A CERTAIN DISTANCE(DECIDED IT WOULD BE BETTER TO REMOVE CARGO CHECK)                                           
                if(/*!intake.cargoCheck() || */Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(1.75)){       
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    twoBallACounter++;
                }
                else{
                    intake.setIntakeMode();
                    drive.tankRun(0.65, 0.65);
                }
            break;
            
            case 5:
                //BACK UP A BIT TO HAVE SPACE TO TURN
                if(Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(0.5)){           
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    twoBallACounter++;
                }
                else{
                    drive.tankRun(-0.5, -0.5);
                }
            break;                                                               

            case 6:
                //TURN LEFT TO FACE HUB 
                if(gyro.getYaw() > 15f && gyro.getYaw() < 20f){                              
                    drive.tankRun(0, 0);   
                    encoder.setPosition(0);
                    twoBallACounter++;
                }
                else{
                    if(gyro.getYaw() >= 20f || gyro.getYaw() > -180f && gyro.getYaw() <-90f){
                        drive.tankRun(-0.75, 0.75);
                    }
                    else if(gyro.getYaw() <= 15f){
                        drive.tankRun(0.4, -0.4);
                    }
                }
            break;

            case 7:      
                //REV SHOOTER, ALIGN, GET IN RANGE                                                                               
                if(shooter.checkRPM() && shooter.checkAligned() && shooter.checkInRange()){
                    drive.arcadeRun(0, 0);
                    encoder.setPosition(0);
                    twoBallACounter++;
                }
                else{
                    //DELETE LATER:
                    shooter.setTesting();
                    shooter.setManual(0.6);
                    //

                    //shooter.setUpperHubShoot();
                    //drive.arcadeRun(shooter.alignSpeed, shooter.getInRangeSpeed);
                    drive.arcadeRun(shooter.getAlignSpeed(), shooter.getRangeSpeed());
                }      
            break;
            
            case 8:             
                //FEED BALL IN TO SHOOT                                                                 
                if(intake.cargoCheck() && timer.get() >= revDelay){
                    timer.stop();
                    timer.reset();

                    shooter.setStop();
                    intake.setIntakeStopMode();
                    twoBallACounter++;
                }
                else{
                    timer.start();
                    intake.setFeedingMode();
                }
            break;

            /*
            case 9:
            //BRING INTAKE MIDWAY
                if(intake.atMidway()){
                    intake.setArmStopMode();
                }
                else{
                    intake.setMidway();
                }
            break;
            */
        }
    }

    //Routine to TAXI, shoot into UPPER HUB, INTAKE cargo and shoot into UPPER HUB, take opp. alliance cargo and shoot towards HANGAR
    private void twoBallB(){
        switch(twoBallBCounter){ 

            case 0:
                //TAXI
                if(Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(7)){
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    twoBallBCounter++;
                }
                else{
                    drive.tankRun(-0.6, -0.6);
                }
            break;

            case 1:
                //REV SHOOTER, INTAKE DOWN, ALIGN, GET IN RANGE                        
                if(shooter.checkRPM() && intake.armIsDown() && shooter.checkAligned() && shooter.checkInRange()){
                    drive.arcadeRun(0, 0);
                    encoder.setPosition(0);
                    twoBallBCounter++;
                }
                else{
                    //DELETE LATER:
                    shooter.setTesting();
                    shooter.setManual(0.6);
                    //
                    
                    intake.setExtend();
                    //shooter.setUpperHubShoot();
                    //drive.arcadeRun(shooter.alignSpeed, shooter.getInRangeSpeed);
                    drive.arcadeRun(shooter.getAlignSpeed(), shooter.getRangeSpeed());
                }      
            break;
                
            case 2:
                //FEED BALL AND SHOOT
                if(intake.cargoCheck() && timer.get() >= revDelay){
                    timer.stop();
                    timer.reset();

                    shooter.setStop();
                    intake.setIntakeStopMode();
                    twoBallBCounter++;
                }
                else{
                    timer.start();
                    intake.setFeedingMode();
                }
            break;
            
            case 3:     
                //TURN RIGHT TO FACE CARGO
                if(gyro.getYaw() > 48f && gyro.getYaw() < 53f ){
                    drive.tankRun(0, 0);   
                    encoder.setPosition(0);
                    twoBallBCounter++;
                }
                else{
                    if(gyro.getYaw() <= 48f){
                        drive.tankRun(0.5, -0.5);
                    }
                    else if(gyro.getYaw() >= 53f){
                        drive.tankRun(-0.35, 0.35);
                    }
                }
            break;

            case 4:     
                //MOVE FORWARD AND INTAKE UNTIL CARGO GOES IN OR TRAVELS A CERTAIN DISTANCE
                if(!intake.cargoCheck() || encoder.getPosition() >= convertFeetToEncoderCounts(5)){
                    drive.tankRun(0, 0);
                    //intake.setIntakeStopMode();
                    twoBallBCounter++;
                }
                else{
                    intake.setIntakeMode();
                    drive.tankRun(0.5, 0.5);
                }
            break;

            case 5:
                //MOVE BACK TO INITIAL POSITION (SKIPPED!!! USELESS!!!)
                if(encoder.getPosition() <= 1 && encoder.getPosition() >= -1){
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    twoBallBCounter++;
                }
                else{
                    drive.tankRun(-0.5, -0.5);
                    //(SKIPPING STEP BECAUSE ITS USELESS!!!)
                    twoBallBCounter++;
                }
            break;

            case 6:
                //TURN LEFT TO FACE UPPER HUB
                if(gyro.getYaw() < 3f && gyro.getYaw() > -3f){
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    twoBallBCounter++;
                }
                else{
                    if(gyro.getYaw() >= 3f){
                        drive.tankRun(-0.5, 0.5);
                    }
                    else if(gyro.getYaw() <= -3f){
                        drive.tankRun(0.35, -0.35);
                    }
                }
            break; 

            case 7:
                //REV SHOOTER, ALIGN, GET IN RANGE
                if(shooter.checkRPM() && shooter.checkAligned() && shooter.checkInRange()){
                    drive.arcadeRun(0, 0);
                    encoder.setPosition(0);
                    twoBallBCounter++;
                }
                else{
                    //DELETE LATER:
                    shooter.setTesting();
                    shooter.setManual(0.6);
                    //

                    //shooter.setUpperHubShoot();
                    //drive.arcadeRun(shooter.alignSpeed, shooter.getInRangeSpeed);
                    drive.arcadeRun(shooter.getAlignSpeed(), shooter.getRangeSpeed());
                }
            break;

            case 8:
                //IF SENSOR IS NOT TRIGGERED, FEED BALL IN REGARDLESS FOR HALF-SECOND
                if(timer.get() >= 0.25){
                    timer.stop();
                    timer.reset();
                    twoBallBCounter++;
                }
                else{
                    timer.start();
                    intake.setOverrideMode();
                }
            break;

            case 9:
                //SET STATE TO FEEDING MODE SO IT STOPS WHEN IT SENSES BALL HAS BEEN SHOT
                if(intake.cargoCheck()){
                    shooter.setStop();
                    intake.setIntakeStopMode();
                    twoBallBCounter++;
                }
                else{
                    intake.setFeedingMode();   
                }
            break;


            //////////////////////////////////////NEW CODE//////////////////////////////////////
            
            case 10:
                //TURN TO FACE OTHER ALLIANCE BALL
                if(gyro.getYaw() < -68f && gyro.getYaw() > -71f){
                    drive.tankRun(0.0, 0.0);
                    encoder.setPosition(0);
                    twoBallBCounter++;
                }
                else{
                    if(gyro.getYaw() >= -68f){
                        drive.tankRun(-0.5, 0.5);
                    }
                    else if(gyro.getYaw() <= -71  ){
                        drive.tankRun(0.35, -0.35);
                    }
                }
            break;
            
            case 11:    
                //MOVE FORWARD AND INTAKE UNTIL CARGO GOES IN OR TRAVELS A CERTAIN DISTANCE
                if(!intake.cargoCheck() || encoder.getPosition() >= convertFeetToEncoderCounts(2)){
                    drive.tankRun(0, 0);
                    intake.setIntakeStopMode();
                    encoder.setPosition(0);
                    twoBallBCounter++;
                }
                else{
                    intake.setIntakeMode();
                    drive.tankRun(0.6, 0.6);
                }
            break;

            
            case 12:
                //FACES TOWARDS THE HANGAR
                if (gyro.getYaw() > 150f && gyro.getYaw() < 155f){
                    drive.tankRun(0.0,0.0);
                    encoder.setPosition(0);
                    twoBallBCounter++;
                }
                else{
                    if(gyro.getYaw() > 155f || gyro.getYaw() < 0){
                        drive.tankRun(-0.75, 0.75);
                    }
                    else if(gyro.getYaw() < 150f && gyro.getYaw() > 0){
                        drive.tankRun(0.35, -0.35);
                    }
                }   
            break;
                
            /*
            case 13:
                //REV SHOOTER
                if(shooter.checkRPM()){
                    twoBallBCounter++;
                }
                else{
                    //DELETE LATER:
                    //shooter.setTesting();
                    //shooter.setManual(0.6);
                    //
                    shooter.setLowHubShoot();
                }
            break;
            
            case 14:
                //FACES TOWARDS THE CLOSEST BALL
                if(gyro.getYaw() < -70f && gyro.getYaw() > -75f){
                    drive.tankRun(0.0, 0.0);
                    encoder.setPosition(0.0);
                    twoBallBCounter++;
                }
                else{
                    if(gyro.getYaw() < -75f){
                        drive.tankRun(0.75, -.75);
                    }
                    else if(gyro.getYaw() > -70f){
                        drive.tankRun(-0.35, 0.35);
                    }
                }
            break;

            case 15:
                //BRING INTAKE TO MIDWAY
                if(intake.atMidway()){
                    intake.setArmStopMode();
                    twoBallBCounter++;
                }

                else{
                    intake.setMidway();
                }
            break;
            */
        } 
    }

    

    /*(REDACTED!!!)
    //Routine to TAXI, shoot preload into LOW HUB, INTAKE two balls and shoot them into UPPER HUB
    private void threeBallLow(){        
        switch(threeBallLowCounter){ 

            case 0: 
                //BACK UP
                if (Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(1.225)) {
                    drive.tankRun(0, 0); 
                    encoder.setPosition(0); 
                    threeBallLowCounter++; 
                } else {
                    drive.tankRun(-0.6, -0.6); 
                }
            break; 

            case 1:
                //REV SHOOTER, INTAKE DOWN
                if(shooter.checkRPM() && intake.armIsDown()){
                    threeBallLowCounter++;
                }
                else{
                    //DELETE LATER:
                    //shooter.setTesting();
                    //shooter.setManual(0.6);
                    //

                    intake.setExtend();
                    shooter.setLowHubShoot();
                }
            break;

            case 2:
                //FEED BALL IN
                if(intake.cargoCheck()){
                    intake.setIntakeStopMode();
                    shooter.setStop();
                    threeBallLowCounter++;
                }

                else{
                    intake.setFeedingMode();
                }
            break;

            case 3:
                //TURN RIGHT TO FACE BALL BY THE WALL
                if(gyro.getYaw() < 142f && gyro.getYaw() > 137f){
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    threeBallLowCounter++;
                }

                else{
                    if(gyro.getYaw() > 142f){
                        drive.tankRun(-0.35, 0.35);
                    }
                    else if(gyro.getYaw() < 137f){
                        drive.tankRun(0.55, -0.55);
                    }
                }
            break;

            case 4:
                //MOVE AND INTAKE UNTIL CARGO IS IN OR AFTER TRAVELING A CERTAIN DISTANCE
                if(!intake.cargoCheck() || Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(6.4)){       
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    //gyro.reset();
                    threeBallLowCounter++;
                }
                else{
                    intake.setIntakeMode();
                    drive.tankRun(0.65, 0.65);
                }
            break;

            case 5:
                //BACK UP SO WE HAVE SPACE TO TURN
                if(Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(0.8)){           
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    threeBallLowCounter++;
                }
                else{
                    drive.tankRun(-0.6, -0.6);
                }
            break;                                                                  

            case 6:
                //TURN LEFT TO FACE HUB
                if(gyro.getYaw() > 3f && gyro.getYaw() < 8f){           //(old values > -155 && < -150) 
                    drive.tankRun(0, 0);   
                    encoder.setPosition(0);
                    threeBallLowCounter++;
                }
                else{
                    
                    if(gyro.getYaw() > 8f){
                        drive.tankRun(-0.75, 0.75);
                    }
                    else if(gyro.getYaw() < 3f){
                        drive.tankRun(0.35, -0.35);
                    }
                }
            break;

            case 7:    
                //REV SHOOTER, ALIGN                                                                                  
                if(shooter.checkRPM() && shooter.checkAligned()){
                    drive.arcadeRun(0, 0);
                    encoder.setPosition(0);
                    threeBallLowCounter++;
                }
                else{
                    //DELETE LATER:
                        shooter.setTesting();
                        shooter.setManual(0.6);
                    //

                    //shooter.setUpperHubShoot();
                    drive.arcadeRun(shooter.alignSpeed, 0);
                }      
            break;
            
            case 8:  
                //FEED BALL IN TO SHOOT                                                                        
                if(intake.cargoCheck()){
                    shooter.setStop();
                    intake.setIntakeStopMode();
                    threeBallLowCounter++;
                }
                else{
                    intake.setFeedingMode();
                }
            break;                                                                                  

            case 9:                                 
                //TURN LEFT TO FACE THIRD BALL                                                 
                if(gyro.getYaw() > -91f && gyro.getYaw() < -86f){   //(old values > 112 && < 114)                         
                    drive.tankRun(0, 0);   
                    encoder.setPosition(0);
                    threeBallLowCounter++;
                }
                else{
                    if(gyro.getYaw() > -86f){
                        drive.tankRun(-0.45, 0.45);
                    }
                    else if(gyro.getYaw() < -91f){
                        drive.tankRun(0.35, -0.35);
                    }
                }
            break;

            case 10:                                                                
                //MOVE FORWARD AND INTAKE UNTIL BALL IS IN OR AFTER TRAVELLING A CERTAIN DISTANCE
                if(!intake.cargoCheck() || Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(9.5)){                              
                    drive.tankRun(0, 0);
                    intake.setIntakeStopMode();
                    threeBallLowCounter++;
                }
                else{
                    intake.setIntakeMode();
                    drive.tankRun(0.9, 0.9);
                }
            break;

            case 11:
                //TURN RIGHT TO FACE HUB
                if(gyro.getYaw() > -2f && gyro.getYaw() < 3f){              //(old values > -135 && <-130)                
                    drive.tankRun(0, 0);   
                    encoder.setPosition(0);
                    threeBallLowCounter++;
                }
                else{
                    if(gyro.getYaw() > 3f){
                        drive.tankRun(-0.35, 0.35);
                    }
                    else if(gyro.getYaw() < -2f){
                        drive.tankRun(0.75, -0.75);
                    }
                }
            break;

            case 12:                                                              
                //REV SHOOTER, ALIGN, GET IN RANGE                        
                if(shooter.checkRPM() && shooter.checkAligned() && shooter.checkInRange()){
                    drive.arcadeRun(0, 0);
                    encoder.setPosition(0);
                    threeBallLowCounter++;
                }
                else{
                    //DELETE LATER
                    shooter.setTesting();
                    shooter.setManual(0.6);
                    //

                    //shooter.setUpperHubShoot();
                    drive.arcadeRun(shooter.alignSpeed, shooter.getInRangeSpeed);
                }      
            break;

            case 13:
                //FEED THE BALL IN REGARDLESS FOR HALF-SECOND IN CASE BALL ISN'T SECURE
                if(timer.get() >= 0.5){
                    timer.stop();
                    timer.reset();
                    threeBallLowCounter++;
                }
                else{
                    timer.start();
                    intake.setOverrideMode();
                }
            break;

            case 14:            
                //SET INTAKE STATE TO FEEDING SO IT STOPS AFTER IT SENSES BALL HAS BEEN SHOT                                                                  
                if(intake.cargoCheck()){
                    shooter.setStop();
                    intake.setIntakeStopMode();
                    threeBallLowCounter++;
                }
                else{
                    intake.setFeedingMode();
                }
            break;

            case 15:
                //BRING INTAKE TO MIDWAY
                if(intake.atMidway()){
                    intake.setArmStopMode();
                    threeBallLowCounter++;
                }
                else{
                    intake.setMidway();
                }
            break;
        }
    }
    */

    private void threeBall(){
        switch(threeBallCounter){
            
            case 0: 
                //BACK UP A BIT TO GET INTO SHOOTING RANGE
                if (Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(1.75)) {
                    drive.tankRun(0, 0); 
                    encoder.setPosition(0); 
                    threeBallCounter++; 
                } 
                else {
                    drive.tankRun(-0.6, -0.6); 
                }
            break; 
            

            case 1:
                //REV SHOOTER, INTAKE DOWN, ALIGN, GET IN RANGE <-(MAY NOT NEED GET IN RANGE)
                if(shooter.checkRPM() && intake.armIsDown() && shooter.checkAligned() && shooter.checkInRange()){                
                    drive.arcadeRun(0, 0);
                    encoder.setPosition(0);
                    threeBallCounter++;
                }

                else{
                    //DELETE LATER:
                    shooter.setTesting();
                    shooter.setManual(0.6);
                    //

                    intake.setExtend();
                    //shooter.setUpperHubShoot();
                    //drive.arcadeRun(shooter.alignSpeed, shooter.getInRangeSpeed);
                    drive.arcadeRun(shooter.getAlignSpeed(), shooter.getRangeSpeed());
                }
            break;

            case 2:
                //FEED BALL IN TO SHOOT
                if(intake.cargoCheck() && timer.get() >= revDelay){
                    timer.stop();
                    timer.reset();

                    intake.setIntakeStopMode();
                    shooter.setStop();
                    threeBallCounter++;
                }

                else{
                    timer.start();
                    intake.setFeedingMode();
                }  
            break;

            case 3:
                //TURN LEFT TO FACE BALL BY THE WALL
                 if(gyro.getYaw() < -166f && gyro.getYaw() > -170){
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    threeBallCounter++;
                }

                else{
                    if(gyro.getYaw() < 90 && gyro.getYaw() >= -166f){
                        drive.tankRun(-0.6, 0.6);
                    }
                    else if(gyro.getYaw() > 90f && gyro.getYaw() < 180f || gyro.getYaw() > -180f && gyro.getYaw() <= -170){
                        drive.tankRun(0.35, -0.35);
                    }
                }                                                 
            break;

            case 4:     
                //MOVE FORWARD AND INTAKE UNTIL BALL IS IN OR TRAVELLING A CERTAIN DISTANCE                                               
                if(/*!intake.cargoCheck() || */Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(1.75)){       
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    threeBallCounter++;
                }
                else{
                    intake.setIntakeMode();
                    drive.tankRun(0.65, 0.65);
                }
            break;
            
            case 5:
                //BACK UP A BIT TO HAVE SPACE TO TURN
                if(Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(0.6)){           
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    //gyro.reset();
                    threeBallCounter++;
                }
                else{
                    drive.tankRun(-0.5, -0.5);
                }
            break;                                                               

            case 6:
                //TURN LEFT TO FACE HUB 
                if(gyro.getYaw() > 20f && gyro.getYaw() < 30f){                              
                    drive.tankRun(0, 0);   
                    encoder.setPosition(0);
                    threeBallCounter++;
                }
                else{
                    if(gyro.getYaw() >= 30f || gyro.getYaw() > -180f && gyro.getYaw() <-90f){
                        drive.tankRun(-0.75, 0.75);
                    }
                    else if(gyro.getYaw() <= 20f){
                        drive.tankRun(0.4, -0.4);
                    }
                }
            break;

            case 7:      
                //REV SHOOTER, ALIGN, GET IN RANGE                                                                              
                if(shooter.checkRPM() && shooter.checkAligned() && shooter.checkInRange()){
                    drive.arcadeRun(0, 0);
                    encoder.setPosition(0);
                    threeBallCounter++;
                }
                else{
                    //DELETE LATER:
                        shooter.setTesting();
                        shooter.setManual(0.6);
                    //

                    //shooter.setUpperHubShoot();
                    //drive.arcadeRun(shooter.alignSpeed, shooter.getInRangeSpeed);
                    drive.arcadeRun(shooter.getAlignSpeed(), shooter.getRangeSpeed());
                }      
            break;
            
            case 8:             
                //FEED BALL IN TO SHOOT                                                                 
                if(intake.cargoCheck() && timer.get() >= revDelay){
                    timer.stop();
                    timer.reset();

                    shooter.setStop();
                    intake.setIntakeStopMode();
                    threeBallCounter++;
                }
                else{
                    timer.start();
                    intake.setFeedingMode();
                }
            break;                                                                                  

            case 9:                         
                //TURN LEFT TO FACE THIRD BALL                                                        
                if(gyro.getYaw() < -57f && gyro.getYaw() > -60f){                             
                    drive.tankRun(0, 0);   
                    encoder.setPosition(0);
                    threeBallCounter++;
                }
                else{
                    if(gyro.getYaw() >= -57f){
                        drive.tankRun(-0.45, 0.45);
                    }
                    else if(gyro.getYaw() <= -60f){
                        drive.tankRun(0.25, -0.25);
                    }
                }
            break;

            case 10:                                                                 
                //MOVE FORWARD AND INTAKE UNTIL CARGO IS IN OR TRAVELLING A CERTAIN DISTANCE
                if(!intake.cargoCheck() || Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(9.5)){                                      
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    //intake.setIntakeStopMode();
                    threeBallCounter++;
                }
                else{
                    intake.setIntakeMode();
                    drive.tankRun(0.7, 0.7);
                }
            break;

            case 11:
                //TURN RIGHT TO FACE HUB
                if(gyro.getYaw() > 35f && gyro.getYaw() < 40f){                             
                    drive.tankRun(0, 0);   
                    encoder.setPosition(0);
                    threeBallCounter++;
                }
                else{
                    if(gyro.getYaw() > 40f){
                        drive.tankRun(-0.35, 0.35);
                    }
                    else if(gyro.getYaw() < 35f){
                        drive.tankRun(0.75, -0.75);
                    }
                }
            break;

            case 12:                                                              
                //REV SHOOTER, ALIGN, AND GET IN RANGE                      
                if(shooter.checkRPM() && shooter.checkAligned() && shooter.checkInRange()){
                    drive.arcadeRun(0, 0);
                    encoder.setPosition(0);
                    threeBallCounter++;
                }
                else{
                    //DELETE LATER
                    shooter.setTesting();
                    shooter.setManual(0.6);
                    //

                    //shooter.setUpperHubShoot();
                    //drive.arcadeRun(shooter.alignSpeed, shooter.getInRangeSpeed);
                    drive.arcadeRun(shooter.getAlignSpeed(), shooter.getRangeSpeed());
                }      
            break;
            
            case 13:  
                //FEED BALL IN REGARDLESS FOR HALF-SECOND IN CASE BALL ISN'T SECURE
                if(timer.get() >= 0.2){
                    timer.stop();
                    timer.reset();
                    threeBallCounter++;
                }
                else{
                    timer.start();
                    intake.setOverrideMode();
                }  
            break;

            case 14:
                //SET INTAKE TO FEED MODE SO IT STOPS AFTER SENSING THE BALL HAS BEEN SHOT
                if(intake.cargoCheck()){
                    shooter.setStop();
                    intake.setIntakeStopMode();
                    threeBallCounter++;
                }
                else{
                    intake.setFeedingMode();
                }
            break;  
     
            case 15:
                //FACE FOURTH BALL
                if(gyro.getYaw() < -118f && gyro.getYaw() > -121f){
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    threeBallCounter++;
                }
                else{
                    if(gyro.getYaw() < -121f && gyro.getYaw() > -120f){
                        drive.tankRun(0.45, -0.45);
                    }
                    else if(gyro.getYaw() > -118f && gyro.getYaw() < -90f){
                        drive.tankRun(-0.35, 0.35);
                    }
                    else{
                        drive.tankRun(0.6, -0.6);
                    }
                }
            break;

            case 16: 
                //MOVE FORWARD UNTIL BALL IS IN OR TRAVELLED CERTAIN DISTANCE
                if(!intake.cargoCheck() || Math.abs(encoder.getPosition()) >= convertFeetToEncoderCounts(10)){
                    drive.tankRun(0, 0);
                    encoder.setPosition(0);
                    threeBallCounter++;
                }
                else{
                    intake.setIntakeMode();
                    drive.tankRun(0.6, 0.6);
                }
            break; 
        }
    }


    public void run(){
        switch(routineState){
            case NOTHING:
                nothing();
            break;

            case ONEBALL:
                oneBall();
            break;
            
            case TWOBALL_A:
                twoBallA();
            break;

            case TWOBALL_B:
                twoBallB();
            break;

            case THREEBALL:
                threeBall();
            break;

            /*(REDACTED)
            case THREEBALLLOW:
                threeBallLow();
            break;
            */
        }

        limelight.run();
        shooter.run();
        intake.intakeRun();
    }
}
