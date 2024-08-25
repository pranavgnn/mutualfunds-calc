package components.piechart;

import java.awt.Color;

public class PieData {
    
    public double getValues() {
        return values;
    }

    public void setValues(double values) {
        this.values = values;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public PieData(double values, Color color) {
        this.values = values;
        this.color = color;
    }

    public PieData() {
    }

    private double values;
    private Color color;
}