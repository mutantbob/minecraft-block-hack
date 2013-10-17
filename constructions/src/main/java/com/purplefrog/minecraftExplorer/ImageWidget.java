package com.purplefrog.minecraftExplorer;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 10/17/13
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageWidget
    extends JComponent
{
    private final ImageIcon img;

    public ImageWidget(ImageIcon imageIcon)
    {
        img = imageIcon;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Dimension sz = getSize();
        g.drawImage(img.getImage(), 0,0, sz.width, sz.height, null);
    }
}
