package com.acid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Created by Paul on 1/10/2017.
 */
public class DrumData extends InstrumentData {
    private final int[][] rhythm = new int[7][16];
    public final DrumData parent;
    public DrumData child;
    public static DrumData currentSequence;

    public DrumData() {
        for (int y1 = 0; y1 < 7; y1++) {
            for (int x1 = 0; x1 < 16; x1++) {
                rhythm[y1][x1] = Statics.output.getSequencer().rhythm[y1][x1];
            }
        }
        System.out.println("copying drums " + this);
        this.parent = currentSequence;
        if (this.parent != null) this.parent.child = this;
        currentSequence = this;
        pixmap = drawPixmap(300, 300);
        region = new TextureRegion(new Texture(pixmap));
        region.flip(false, true);
    }

    public void refresh() {
        for (int y1 = 0; y1 < 7; y1++) {
            for (int x1 = 0; x1 < 16; x1++) {
                Statics.output.getSequencer().rhythm[y1][x1] = rhythm[y1][x1];
            }
        }
        System.out.println("restoring drums " + this);
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 7; j++) {
                if (rhythm[j][i] == 0) {

                } else {
                    s += j + "";
                }
            }
            s += " ";
        }
        return s;
    }

    public static void setcurrentSequence(DrumData sd){
        if (sd!= null) {
            currentSequence = sd;
            currentSequence.refresh();
        }
    }

    public static void undo() {
        if (currentSequence != null && currentSequence.parent != null) {
            currentSequence = currentSequence.parent;
            currentSequence.refresh();
        }
    }

    public static void redo() {
        if (currentSequence != null && currentSequence.child != null) {
            currentSequence = currentSequence.child;
            currentSequence.refresh();
        }
    }

    public Pixmap drawPixmap(int w, int h) {
        FrameBuffer drawBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
        drawBuffer.begin();
        Color c = ColorHelper.rainbowDark();
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
        ShapeRenderer renderer = new ShapeRenderer();
        renderer.getProjectionMatrix().setToOrtho2D(0, 0, w, h);


        float skipx = ((float) w / 16f);
        float skipy = (float) h / 7f;
        render(renderer, skipx, skipy);
        Pixmap pixmap1 = ScreenUtils.getFrameBufferPixmap(0, 0, w, h);
        Pixmap pixmap = new Pixmap((int) w, (int) h, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        pixmap.drawPixmap(pixmap1, 0, 0);
        drawBuffer.end();
        drawBuffer.dispose();

        return pixmap;
    }

    public static void render(ShapeRenderer renderer1, float skipx, float skipy) {
        renderer1.begin(ShapeRenderer.ShapeType.Filled);
        renderer1.setColor(ColorHelper.rainbowLight());
        for (int r = 0; r < Statics.output.getSequencer().rhythm.length; r++) {
            renderer1.setColor(ColorHelper.rainbowLight());
            for (int r1 = 0; r1 < 16; r1++) {
                if (Statics.output.getSequencer().rhythm[r][r1] > 0) {
                    renderer1.rect(r1 * skipx + 2, (r)
                            * skipy + 2, skipx - 4, skipy - 4);
                }
            }
        }
        renderer1.end();
    }
}
