package app.processing;

import processing.core.PGraphics;
import processing.core.PShape;

import java.util.Arrays;

public class Shape extends PShape {
    protected Applet context;
    private double x = 0;
    private double y = 0;
    private float height = 0;

    public Shape() {
        super();
    }

    public Shape(Applet context, PGraphics g, int kind, float[] p) {
        super(g, kind, p);
        this.context = context;

        if(p.length <= 3) {
            height = p[1];
        } else {
            x = p[0];
            y = p[1];
            height = p[3];
        }
    }

    public void addChild(String name, PShape shape) {
        addChild(name, shape, false);
    }

    public void addChild(String name, PShape shape, boolean override) {
        if (getChild(shape.getName()) != null) {
            if (override) {
                removeChild(shape);
                shape.setName(name);
                addChild(shape);
            }
        } else {
            shape.setName(name);
            addChild(shape);
        }

    }

    public void removeChild(String name) {
        Shape child;
        if ((child = getChild(name)) != null) {
            removeChild(child);
        }
    }

    public void removeChild(PShape shape) {
        if (shape != null) {
            removeChild(getChildIndex(shape));
        }
    }

    @Override
    public void setName(String name) {
        super.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setFill(int color, double transparency) {
        color &= ((int) (transparency * 0x100) * 0x1000000) | 0xFFFFFF;
        super.setFill(color);
    }

    public float[] getAbsoluteParams() {
        float[] out = this.getParams();
        out[0] += context.getX();
        out[1] += context.getY();

        return out;
    }

    @Override
    public void translate(float x, float y) {
        super.translate(x, y);

        this.x += x;
        this.y += y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public float getHeight() {
        return Math.abs(height);
    }

    @Override
    public Shape[] getChildren() {
        // TODO: Analyze to see if (children.length-1) will work
        return Arrays.copyOf(children, children.length-1, Shape[].class);
    }

    @Override
    public Shape getChild(String target) {
        return (Shape) super.getChild(target);
    }

    public void reorderChildren(Shape... shapes) {
        for (Shape shape : shapes) {
            removeChild(shape);
        }

        for (Shape shape : shapes) {
            addChild(shape.getName(), shape);
        }
    }

}
