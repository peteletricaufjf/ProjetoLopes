package app.processing;

import processing.core.PApplet;

import java.util.ArrayList;

public class Applet extends PApplet {
    private int x;
    private int y;

    public static boolean DEBUG = false;

    protected ArrayList<Button> buttons = new ArrayList<>();

    /**
     * {@inheritDoc}
     * @return o {@link Shape} criado
     */
    @Override
    public Shape createShape(int kind, float... p) {
        return new Shape(this, this.g, kind, p);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void translate(float x, float y) {
        super.translate(x, y);

        this.x += x;
        this.y += y;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw() {
        x = 0;
        y = 0;
    }

    /**
     * @return A posição 'x' do applet
     */
    public int getX() {
        return x;
    }

    /**
     * @return A posição 'y' do applet
     */
    public int getY() {
        return y;
    }

    /**
     * Mostra o grid na tela
     * <pre>
     * Contém:
     * >> Texto da posição absoluta
     * >> Texto da posição relativa
     * >> Linhas de apoio
     * </pre>
     */
    public void showGrid() {
        int inc = 50;

        pushMatrix();

        textSize(15);
        textAlign(CENTER, BOTTOM);

        fill(Color.WHITE);
        stroke(Color.GRID_COLOR);

        for (int i = 0; i <= width; i += inc) {
            text(i-width/2, i, 50);
            text(i, i, 25);
            line(i, 0, i, height);
        }

        popMatrix();
    }

    /**
     * <pre>
     * Centraliza o Applet na tela
     * Posição (0, 0) agora é no centro da tela
     * </pre>
     */
    public void center() {
        translate(width/2, height/2);
    }

    /**
     * Escreve algum texto na página
     * @param text Texto a ser escrito
     * @param x Posição 'x' do texto
     * @param y Posição 'y' do texto
     */
    public void writeInPage(Object text, float x, float y) {
        pushMatrix();

        textSize(15);
        textAlign(CENTER, TOP);

        stroke(Color.RED);
        fill(Color.BRIGHT_GREEN);

        text(String.valueOf(text), -width / 2 + x, height / 2 - 100 + y);

        popMatrix();
    }

    /**
     * Adiciona um botão no Applet
     * @param button O {@link Button} a ser adicionado
     */
    public void addButton(Button button) {
        buttons.add(button);
    }

    /**
     * Mostra os botões na tela
     */
    public void displayButtons() {
        if (DEBUG) {
            insideMatrix(() -> {
                center();
                for (Button b : buttons) {
                    b.display();
                }
            });
        }
    }

    /**
     * Ouve por cliques do mouse nos botões
     */
    @Override
    public void mousePressed() {
        for (Button button : buttons) {
            button.listenForMouse();
        }
    }

    /**
     * [Under development]
     * Adiciona qualquer coisa na tela sem mexer nos elementos da Matriz
     * @param matrixCallback Função com os elementos a se adicionar na Matriz
     */
    public void insideMatrix(MatrixCallback matrixCallback) {
        pushMatrix();
        matrixCallback.callback();
        popMatrix();
    }

    /**
     * [Under development]
     * Interface de callback
     */
    public interface MatrixCallback {
        void callback();
    }
}
