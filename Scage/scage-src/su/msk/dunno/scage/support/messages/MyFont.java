package su.msk.dunno.scage.support.messages;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class MyFont
{
    public UnicodeFont uFont = null;
    
    public MyFont() throws SlickException
    {
        String fontPath = "fonts/DroidSans.ttf";
        uFont = new UnicodeFont(fontPath , 20, false, false);
        uFont.addAsciiGlyphs();
        uFont.addGlyphs(400, 600);
        uFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        uFont.loadGlyphs();
    }
}
