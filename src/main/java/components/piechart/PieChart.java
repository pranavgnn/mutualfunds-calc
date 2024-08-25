package components.piechart;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import components.piechart.PieData;



public class PieChart extends JComponent {

    private final List<PieData> models;
    private float padding = 0.2f;
    private float thickness = 0.4f;

    public PieChart() {
        models = new ArrayList<>();
        setForeground(new Color(60, 60, 60));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        double width = getWidth();
        double height = getHeight();
        double size = Math.min(width, height);
        
        size -= size * padding;
        
        double x = (width - size) / 2;
        double y = (height - size) / 2;
        double totalValue = getTotalvalue();
        double drawAngle = 90;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        
        for (int i = 0; i < models.size(); i++) {
            PieData data = models.get(i);
            double angle = data.getValues() * 360 / totalValue;
            Area area = new Area(new Arc2D.Double(x, y, size, size, drawAngle, -angle, Arc2D.PIE));
            double s1 = size * (1 - thickness);
            double x1 = (width - s1) / 2;
            double y1 = (height - s1) / 2;
            
            area.subtract(new Area(new Ellipse2D.Double(x1, y1, s1, s1)));
            g2.setColor(data.getColor());
            g2.fill(area);
            drawAngle -= angle;
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private double getTotalvalue() {
        double max = 0;
        
        for (PieData data : models) {
            max += data.getValues();
        }
        
        return max;
    }

    public float getPadding() {
        return padding;
    }

    public void setPadding(float padding) {
        this.padding = padding;
        repaint();
    }

    public void clearData() {
        models.clear();
        repaint();
    }

    public void addData(PieData data) {
        models.add(data);
    }
}