package su.msk.dunno.scage.support.messages;

import org.newdawn.slick.SlickException;
import su.msk.dunno.scage.support.messages.unicode.ColorEffect;
import su.msk.dunno.scage.support.messages.unicode.UnicodeFont;

public class SlickUnicodeFont
{
    private UnicodeFont uFont = null;
    
    public SlickUnicodeFont(String font_path, int size, int glyph_from, int glyph_to) throws SlickException
    {
        uFont = new UnicodeFont(font_path , size, false, false);
        uFont.addAsciiGlyphs();
        uFont.addGlyphs(glyph_from, glyph_to);     // other alphabets (cyrillic by default)
        uFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        uFont.loadGlyphs();
    }

    public void drawString(float x, float y, String message, org.newdawn.slick.Color color)
    {
        uFont.drawString(x, y, message, color);
    }
}
