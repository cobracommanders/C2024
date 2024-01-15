package org.team498.C2024.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight extends SubsystemBase{
    private double x;
    private double y;
    private double area;
    private double minimum;
    private double maximum;

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");

    public void periodic(){
        x = tx.getDouble(0.0);
        y = ty.getDouble(0.0);
        area = ta.getDouble(0.0);
        maximum = 0;
        minimum = 0;
    }

    public boolean isCentered(){
        if (getX() > maximum || getX() < minimum)
        return false;
        return true;
    }

    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double getArea(){
        return area;
    }
}