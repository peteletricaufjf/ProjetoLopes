package app.processing;

import processing.core.PConstants;

import javax.swing.*;
import java.util.ArrayList;

public class ConveyorBelt extends Shape {

    private static final int size = 500;
    private final DropDown dropDown;

    private float dropSpeed = 0;
    private float dropT = 0;
    private boolean isDrop = false;
    private Timer dropTimer;

    private ArrayList<Sensor> sensors = new ArrayList<>();

    private Recipient object;

    /**
     * Construtor
     * @param name Nome do Shape
     * @param context Contexto da aplicação. {@link Applet} que costuma ser a classe {@link Main}
     */
    public ConveyorBelt (String name, Applet context) {
        super.context = context;
        setName(name);

        new Structure();
        new BaseSensors();
        dropDown = new DropDown();
        new MovingObject();

        reposition();
    }

    /**
     * Atualiza a posição da esteira na tela
     */
    private void reposition() {
        translate(-size/2, 0);
    }

    /**
     * Movimenta a caixa
     * @param dx A "velocidade" do objeto. Costuma ser entre [-1, 1], mas nada impede de ser maior que 1
     */
    public void moveObject(double dx) {
        if(context.buttons.get(2).isActive()){
            object.setDirection(Recipient.Direction.STOP);
        } else {
            object.setDirection(Recipient.Direction.POSITIVE);
        }
        object.move(dx);
    }

    /**
     * @return Retorna o tamanho da esteira
     */
    @Override
    public float getWidth() {
        return size;
    }

    /**
     * Mostra a esteira na tela
     */
    public void display() {
        context.shape(this);
        context.shape(object);
    }

    /**
     * Inicializa os sensores
     */
    public void turnSensorsOn() {
        new Thread(() -> {
            for (Sensor sensor : sensors) {

                boolean isActive = false;
                switch (sensor.getType()) {
                    case Sensor.HORIZONTAL:
                        Shape drop = getChild("drop");
                        isActive = drop != null && Math.abs(sensor.getVerticalPosition() - drop.getY()) < drop.getHeight();
                        break;
                    case Sensor.VERTICAL:
                        isActive = (sensor.getHorizontalPosition() - object.getPosX()) > 0 && (sensor.getHorizontalPosition() - object.getPosX()) < object.getSizeX();
                        break;
                }

                if (isActive) {
                    sensor.setActive(true);
                    sensor.setStroke(Color.YELLOW);
                } else {
                    sensor.setActive(false);
                    sensor.setStroke(Color.RED);
                }
            }
        }).start();
    }

    /**
     * Mostra os valores dos sensores na tela
     */
    public void displaySensorsTable() {
        if (Applet.DEBUG) {
            context.insideMatrix(() -> {
                context.center();
                for (int i = 0; i < sensors.size(); i++) {
                    context.writeInPage("S" + i, 30 + 40 * i, 10);
                    context.writeInPage(sensors.get(i).isActive() ? 1 : 0, 30 + 40 * i, 40);
                }
            });
        }
    }

    /**
     * Inicializa e mostra os botões dos controladores na tela
     */
    public void displayControllers() {
        int spacing = 60;
        Button forw = new Button(context, 200 + spacing*0, context.height - 70, 40, 40, button -> {});
        Button back = new Button(context, 200 + spacing*1, context.height - 70, 40, 40, button -> {});
        Button stop = new Button(context, 200 + spacing*2, context.height - 70, 40, 40, button -> {});
        Button drop = new Button(context, 200 + spacing*3, context.height - 70, 40, 40, button -> {});

        forw.setText(">>");
        back.setText("<<");
        stop.setText("||");
        drop.setText("\\/");

        context.addButton(forw);
        context.addButton(back);
        context.addButton(stop);
        context.addButton(drop);
    }

    /**
     * @return Uma {@link String} com os valores dos sensores. 1 para ativo, 0 para inativo
     */
    public String getActiveSensors() {
        StringBuilder out = new StringBuilder();
        for (Sensor sensor : sensors) {
            out.append(sensor.isActive() ? 1 : 0);
        }
        return out.toString();
    }

    /**
     * Inicializa a classe
     */
    public void run() {
        display();
        turnSensorsOn();
        moveObject(((Main) context).getSpeed());
        dropDown.moveDrop();
    }

    public void init() {
        displayControllers();

        context.buttons.get(3).onStateChange(state -> {
            if (state) {
                dropTimer.start();
            } else {
                dropTimer.stop();
            }
        });
    }

    public void restart() {
        object.move(0);
        object.restart();
    }


    /**
     * Classe helper para criar os sensores
     */
    private static class Sensor extends Shape {
        public static final int VERTICAL = 0;
        public static final int HORIZONTAL = 1;

        private float horizontalPosition;
        private float verticalPosition;
        private boolean active;
        private int type;

        private Sensor(Shape shape, float x, float y, int type) {
            this(shape, x, y, 0, type);
        }

        private Sensor(Shape shape, float x, float y, float z, int type) {
            super(shape.context, shape.context.g, shape.getKind(), shape.getParams());

            this.translate(x, y, z);

            this.horizontalPosition = x;
            this.verticalPosition = y;
            this.active = false;
            this.type = type;

            setStroke(Color.RED);
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public float getHorizontalPosition() {
            return horizontalPosition;
        }

        public float getVerticalPosition() {
            return verticalPosition;
        }

        public int getType() {
            return type;
        }

        public static Sensor createShape(Applet context, float x, float y, int type) {
            float sensorSize = 3;
            return new Sensor(context.createShape(PConstants.SPHERE, sensorSize, sensorSize, sensorSize), x, y, type);
        }

        public static Sensor createShape(Applet context, float x, float y, float z, int type) {
            float sensorSize = 3;
            return new Sensor(context.createShape(PConstants.SPHERE, sensorSize, sensorSize, sensorSize), x, y, z, type);
        }
    }

    private class Structure {
        private static final float baseHeight = 10;
        private static final int footHeight = 100;
        private static final float baseDepth = 100;

        Structure() {
            addBase();
            addFeet(0.1, 0.5, 0.9);
        }

        /**
         * Adiciona a base da esteira
         */
        private void addBase() {
            Shape base = context.createShape(PConstants.BOX, size, baseHeight, baseDepth);
            base.translate(size / 2, baseHeight / 2);
            base.setStroke(Color.WHITE);
            addChild("base", base);
        }

        /**
         * TODO: Adicionar validação para valores menores que 1
         * Adiciona os pés da esteira
         * @param feetPosition As posições dos pés, em porcentagem
         */
        private void addFeet(double... feetPosition) {
            for (double footPosition : feetPosition) {
                float position = (float) footPosition * size;
                Shape feetA = context.createShape(PConstants.BOX, 3, footHeight, 3);
                Shape feetB = context.createShape(PConstants.BOX, 3, footHeight, 3);
                feetA.translate(position, footHeight/2 + baseHeight/2, baseDepth/4);
                feetB.translate(position, footHeight/2 + baseHeight/2, -baseDepth/4);

                feetA.setStroke(Color.WHITE);
                feetB.setStroke(Color.WHITE);
                addChild("feetA " + footPosition, feetA);
                addChild("feetB " + footPosition, feetB);
            }
        }

    }

    private class DropDown implements DropDownParams {
        private static final float mastHeight = 200;

        DropDown() {
            addDropDownMast();
            addDropDownStructure();
            addDropDownSensor();
            initTimer();
        }

        /**
         * Adiciona o poste que contém o dropDown
         */
        private void addDropDownMast() {
            Shape mast = context.createShape(PConstants.BOX, 5, mastHeight, 5);
            mast.translate(DropDownParams.posX, -mastHeight/2, -(DropDownParams.sizeZ/2 + 5/2 + 1));
            mast.setStroke(Color.WHITE);
            addChild("mast", mast);
        }

        /**
         * Adiciona o sensor do dropdown
         */
        private void addDropDownSensor() {
            Sensor sensor = Sensor.createShape(context, posX, posY - sizeY/2 + 10, -sizeZ/2, Sensor.HORIZONTAL);

            sensors.add(sensor);
            addChild("sensor " + size/2, sensor);
        }

        /**
         * Adiciona o dropdown
         */
        private void addDropDownStructure() {
            Shape dropDown = context.createShape(PConstants.BOX, sizeX, sizeY, sizeZ);
            dropDown.translate(posX, posY);
            //dropDown.setFill(Color.RED);
            addChild("dropDown", dropDown);
        }

        /**
         * Faz a bola cair em intervalos constantes.
         */
        private void initTimer() {
            new Thread(() -> {
                dropTimer = new Timer(1000, actionEvent -> {
                    createDrop();
                    moveDrop();
                });
            }).start();
        }

        /**
         * Cria a bola que cai do dropdown
         */
        private void createDrop() {
            float width = DropDownParams.sizeX / 8;

            Shape drop = context.createShape(PConstants.SPHERE, width, width, width);
            drop.translate(DropDownParams.posX, DropDownParams.sizeY);
            drop.setStroke(Color.BRIGHT_GREEN);

            dropSpeed = 0;
            dropT = 0;
            addChild("drop", drop, true);

            reorderChildren(drop, getChild("dropDown"));
        }

        /**
         * Move a bola que cai do dropdown
         */
        private void moveDrop () {
            try {
                Shape drop = getChild("drop");
//                drop.setFill(Color.BRIGHT_GREEN);
//                drop.noFill();

                if (drop.getY() + drop.getHeight() + dropSpeed > 0) {
                    removeChild(drop.getName());
                }

                // Simula a gravidade
                dropSpeed += 10 * dropT;
                dropT += 1 / 30f * Main.SPEED_CORRECTION_FACTOR;

                drop.translate(0, dropSpeed);

            } catch (Exception ignored) {}
        }
    }

    private class BaseSensors {
        BaseSensors() {
            addBaseSensors(0.25, 0.5, 0.9);
        }

        /**
         * TODO: Fazer verificação para que os valores sejam menores que 1
         * Adiciona os sensores da base
         * @param sensorPositions A posição dos sensores, em porcentagem
         */
        private void addBaseSensors(double... sensorPositions) {
            for (double sensorPosition : sensorPositions) {
                Sensor sensor = Sensor.createShape(context, (float) sensorPosition * size, 0, Sensor.VERTICAL);

                sensors.add(sensor);
                addChild("sensor " + String.valueOf(sensorPosition), sensor);
            }
        }
    }

    private class MovingObject {
        MovingObject() {
            addMovingObject();
        }

        /**
         * Adiciona a caixa que irá mover
         */
        private void addMovingObject() {
            object = new Recipient("moving object", context, size);
        }

    }

    private interface DropDownParams {
        float sizeX = 50;
        float sizeY = -80;
        float sizeZ = 50;

        float posX = size/2 - sizeX/2;
        float posY = 3*sizeY/2;
    }

}
