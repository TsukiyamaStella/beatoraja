package bms.player.beatoraja.decide;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import bms.player.beatoraja.MainState;
import bms.player.beatoraja.play.BMSPlayer;
import bms.player.beatoraja.skin.*;

/**
 * 曲決定部分のスキン
 *
 * @author exch
 */
public class MusicDecideSkin extends Skin {

    public MusicDecideSkin(float srcw, float srch, float dstw, float dsth) {
        super(srcw, srch, dstw, dsth);
    }

	public MusicDecideSkin(Rectangle r) {
        super(1280, 720, r.width, r.height);

        SkinImage bg = new SkinImage(MainState.IMAGE_STAGEFILE);
        setDestination(bg, 0, 0, 0, 1280, 720, 0, 255,255,255,255, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        this.add(bg);
        SkinText genre = new SkinText("skin/VL-Gothic-Regular.ttf",0,20, 2);
		genre.setReferenceID(MainState.STRING_GENRE);
        setDestination(genre, 0, 300, 420, 18, 18, 0, 255,255,255,255, 0, 0, 0, 0, 2000, 0, 0, 0, 0);
        setDestination(genre, 2000, 380, 420, 18, 18, 0, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        this.add(genre);
        SkinText title = new SkinText("skin/VL-Gothic-Regular.ttf",0,24, 2);
		title.setReferenceID(MainState.STRING_FULLTITLE);
        setDestination(title, 0, 340, 360, 18, 18, 0, 255,255,255,255, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        this.add(title);
        SkinText artist = new SkinText("skin/VL-Gothic-Regular.ttf",0,20, 2);
        artist.setReferenceID(MainState.STRING_ARTIST);
        setDestination(artist, 0, 380, 300,18, 18, 0, 255,255,255,255, 0, 0, 0, 0, 2000, 0, 0, 0, 0);
        setDestination(artist, 2000, 300, 300,18, 18, 0, 255,255,255,255, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        this.add(artist);
        
		Texture nt = new Texture("skin/system.png");
		SkinImage fi = new SkinImage(new TextureRegion[]{new TextureRegion(nt,0,0,8,8)},0);
        setDestination(fi, 0, 0, 0,1280, 720, 0, 0,255,255,255, 0, 0, 0, 0, 500, BMSPlayer.TIMER_FADEOUT, 0, 0, 0);
        setDestination(fi, 500, 0, 0,1280, 720, 0, 255,255,255,255, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        add(fi);

        setFadeout(500);
        setScene(3000);
        setInput(500);
    }
}
