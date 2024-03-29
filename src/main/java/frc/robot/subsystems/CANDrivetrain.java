// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import static frc.robot.Constants.DrivetrainConstants.*;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

/* This class declares the subsystem for the robot drivetrain if controllers are connected via CAN. Make sure to go to
 * RobotContainer and uncomment the line declaring this subsystem and comment the line for PWMDrivetrain.
 *
 * The subsystem contains the objects for the hardware contained in the mechanism and handles low level logic
 * for control. Subsystems are a mechanism that, when used in conjuction with command "Requirements", ensure
 * that hardware is only being used by 1 command at a time.
 */
public class CANDrivetrain extends SubsystemBase {
  /*Class member variables. These variables represent things the class needs to keep track of and use between
  different method calls. */
  DifferentialDrive m_drivetrain;
  RelativeEncoder m_leftEncoder; 
  RelativeEncoder m_rightEncoder; 
  DifferentialDriveKinematics m_kinematics; 

  /*Constructor. This method is called when an instance of the class is created. This should generally be used to set up
   * member variables and perform any configuration or set up necessary on hardware.
   */
  public CANDrivetrain() {
    CANSparkMax leftFront = new CANSparkMax(kLeftFrontID, MotorType.kBrushless);
    CANSparkMax leftRear = new CANSparkMax(kLeftRearID, MotorType.kBrushless);
    CANSparkMax rightFront = new CANSparkMax(kRightFrontID, MotorType.kBrushless);
    CANSparkMax rightRear = new CANSparkMax(kRightRearID, MotorType.kBrushless);

    /*Sets current limits for the drivetrain motors. This helps reduce the likelihood of wheel spin, reduces motor heating
     *at stall (Drivetrain pushing against something) and helps maintain battery voltage under heavy demand */
    leftFront.setSmartCurrentLimit(kCurrentLimit);
    leftRear.setSmartCurrentLimit(kCurrentLimit);
    rightFront.setSmartCurrentLimit(kCurrentLimit);
    rightRear.setSmartCurrentLimit(kCurrentLimit);

    // Set the rear motors to follow the front motors.
    leftRear.follow(leftFront);
    rightRear.follow(rightFront);

    // Invert the left side so both side drive forward with positive motor outputs
    leftFront.setInverted(false);
    rightFront.setInverted(true);

    // Put the front motors into the differential drive object. This will control all 4 motors with
    // the rears set to follow the fronts
    m_drivetrain = new DifferentialDrive(leftFront, rightFront);
    m_leftEncoder = leftFront.getEncoder(); 
    m_leftEncoder.setPositionConversionFactor(kDistanceFactor); 
    m_leftEncoder.setVelocityConversionFactor(kVelocityFactor); 
    m_kinematics = new DifferentialDriveKinematics(kDistanceBetweenWheels); 
    m_rightEncoder = rightFront.getEncoder(); 
    m_rightEncoder.setPositionConversionFactor(kDistanceFactor); 
    m_rightEncoder.setVelocityConversionFactor(kVelocityFactor); 
  }

  /*Resets the distance travelled to 0. */
  public void resetEncoder() {
    m_leftEncoder.setPosition(0); 
    m_rightEncoder.setPosition(0); 
  }

  /*Returns the distance traveled by the robot from 0. */
  public double getDistance() {
    return m_leftEncoder.getPosition(); 
  }

  public Command arcadeDriveCommand(CommandXboxController controller) {
    return Commands.run(
      () -> arcadeDrive(
                    -MathUtil.applyDeadband(controller.getLeftY(), 0.1), -MathUtil.applyDeadband(controller.getRightX(), 0.1)), this);
  }

  /*Method to control the drivetrain using arcade drive. Arcade drive takes a speed in the X (forward/back) direction
   * and a rotation about the Z (turning the robot about it's center) and uses these to control the drivetrain motors */
  public void arcadeDrive(double speed, double rotation) {
    m_drivetrain.arcadeDrive(speed, rotation);
  }

  @Override
  public void periodic() {
    /*This method will be called once per scheduler run. It can be used for running tasks we know we want to update each
     * loop such as processing sensor data. Our drivetrain is simple so we don't have anything to put here */
  }
}
