package su.msk.dunno.scage.support.messages;

import org.newdawn.slick.SlickException;
import su.msk.dunno.scage.support.messages.unicode.ColorEffect;
import su.msk.dunno.scage.support.messages.unicode.UnicodeFont;

public class MyFont
{
    public UnicodeFont uFont = null;
    
    public MyFont() throws SlickException
    {
        String fontPath = "fonts/DroidSans.ttf";
        uFont = new UnicodeFont(fontPath , 20, false, false);
        uFont.addAsciiGlyphs();
        uFont.addGlyphs(1024,1279);
        uFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        uFont.loadGlyphs();
    }

    public void drawString(float x, float y, String message, org.newdawn.slick.Color color)
    {
        uFont.drawString(x, y, message, color);
    }
}
