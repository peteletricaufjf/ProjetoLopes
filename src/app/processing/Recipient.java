package app.processing;

import processing.core.PApplet;
import processing.core.PConstants;

public class Recipient extends Shape {
    private PApplet context;

    private final float maxX;
    private final float minX;
    private final float beltWidth;
    private float posX = 0;
    private int posY = 0;
    private int sizeX = 40;
    private int sizeY = 30;
    private int direction = 1;

    public Recipient(String name, PApplet context, float beltWidth) {
        this.context = context;
        this.beltWidth = beltWidth;
        this.minX = -beltWidth/2;
        this.maxX = beltWidth/2 - sizeX;

        setName(name);
        create();
    }

    private void create() {
        Shape box = ((Applet) context).createShape(PConstants.BOX, sizeX, sizeY, 50);
        box.translate(minX + sizeX/2, -sizeY/2);
        box.setFill(Color.RED, 0.5);

        addChild("moving_object", box);

        posX = (int) minX;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void move(double speed) {
        double move = direction * speed;
        posX += move;

        if (posX <= minX){
            direction = Direction.STOP;
            move += 1;
            posX += 1;
        } else if (posX >= maxX) {
            direction = Direction.STOP;
            move -= 1;
            posX -= 1;
        }

        context.pushMatrix();
        translate((float) move, 0);
        context.popMatrix();
    }

    public float getPosX() {
        return posX + (int) beltWidth/2;
    }

    public int getSizeX() {
        return sizeX;
    }

    public void restart() {
        Shape box = getChild("moving_object");
        box.translate(-getPosX(), 0);
        posX = (int) minX;
    }

    interface Direction {
        int POSITIVE = 1;
        int NEGATIVE = -1;
        int STOP = 0;
    }
}

