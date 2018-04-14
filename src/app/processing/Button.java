package app.processing;

import static processing.core.PConstants.CENTER;

public class Button {
    private final int x, y, w, h;
    private final Applet context;
    private OnClickListener clickListener;
    private OnStateChange onStateChangeListener;
    private String text = null;
    private boolean active = false;

    /**
     * Construtor
     * @param context O {@link Applet} utilizado. Normalmente usa-se a classe {@link Main}
     * @param xx Posição 'x' do botão
     * @param yy Posição 'y' do botão
     * @param ww Tamanho horizontal do botão
     * @param hh Tamanho vertical do botão
     * @param clickListener Callback chamado quando se clica no botão
     */
    Button(Applet context, int xx, int yy, int ww, int hh, OnClickListener clickListener) {
        this.context = context;
        this.x = xx;
        this.y = yy;
        this.w = ww;
        this.h = hh;
        this.clickListener = clickListener;
    }

    /**
     * Mostra o botão na tela
     */
    public void display() {
        context.fill(Color.WHITE);
        context.stroke(Color.YELLOW);
        context.rect(x - context.width / 2, y - context.height / 2, w, h);

        context.textSize(15);
        context.textAlign(CENTER, CENTER);
        context.fill(Color.BLACK);
        if (text != null) {
            context.text(text, x + w/2 - context.width / 2, y - context.height / 2 + h/2);
        }

        context.fill(Color.BRIGHT_GREEN);
        context.text(active ? 1 : 0, x + w/2 - context.width / 2, y - context.height / 2 - 20);

    }

    /**
     * Função para invocar o callback quando o botão é clicado
     */
    private void callListener() {
        clickListener.onClickListener(this);
    }

    public void onStateChange(OnStateChange listener) {
        onStateChangeListener = listener;
    }

    /**
     * Adiciona um texto no botão
     * @param text O texto a ser adicionado
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Método necessário para saber se o clique do mouse foi feito no botão
     * @return true caso tenho sido. Falso, caso contrário
     */
    public boolean isMouseInside() {
        return context.mouseX > x & context.mouseX < x + w & context.mouseY > y & context.mouseY < y + h;
    }

    /**
     * Altera o estado do botão de ativo para inativo e vice-versa
     */
    public void toggle() {
        active = !active;
    }

    /**
     * Wrapper para o clique do mouse. Altera o estado do botão e chama seu callback
     */
    public void listenForMouse() {
        if (isMouseInside()) {
            toggle();
            callListener();
        }
    }

    /**
     * Altera o estado do botão
     * @param active O estado do botão. True para ativo, false para inativo
     */
    public void setActive(boolean active) {
        if(this.active = active) {
            callListener();
        }

        // Chama o callback
        if (onStateChangeListener != null) {
            onStateChangeListener.onStateChange(active);
        }

    }

    /**
     * @return se o botão está ativo ou não
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Interface para o callback de clique
     */
    public interface OnClickListener {
        void onClickListener(Button button);
    }

    public interface OnStateChange {
        void onStateChange(boolean state);
    }
}
