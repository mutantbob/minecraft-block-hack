package com.purplefrog.minecraft.view3d;

import com.purplefrog.minecraftExplorer.*;

import javax.media.opengl.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Created by thoth on 10/20/14.
 */
public class BlocksPanel
    extends JPanel
{
    private final BlockViewer bv;
    Map<JComponent, Runnable> perTab = new HashMap<JComponent, Runnable>();


    public BlocksPanel(GLCanvas canvas, BlockViewer bv)
    {
        super(new BorderLayout() );

        Font bigSerif = new Font("serif", Font.PLAIN, 24);

        this.bv = bv;

        add(canvas, BorderLayout.CENTER);

        final JTabbedPane tabs = new JTabbedPane();
        add(tabs, BorderLayout.SOUTH);

        {
            JPanel p = new JPanel();
            p.add(new JLabel("Farm"));
            tabs.addTab("farm", p);

            perTab.put(p, new Runnable()
            {
                @Override
                public void run()
                {
                    pickFarm();
                }
            });
        }

        {
            JPanel p = new JPanel();

            final JSpinner startW = new JSpinner(new SpinnerNumberModel(
                53,0, 255, 1
            ));
            startW.setFont(bigSerif);
            p.add(startW);
            startW.addChangeListener(new ChangeListener()
            {
                @Override
                public void stateChanged(ChangeEvent changeEvent)
                {
                    pickPerBlockdata(startW);
                }
            });

            tabs.add("per blockData", p);

            perTab.put(p, new Runnable()
            {
                @Override
                public void run()
                {
                    pickPerBlockdata(startW);
                }
            });
        }

        {
            JPanel p = new JPanel();

            final JTextField startW = new JTextField("0", 10);
            startW.setFont(bigSerif);
            p.add(startW);
            startW.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent actionEvent)
                {
                    pick8x8(startW);
                }
            });

            tabs.add("8x8 blocks", p);

            perTab.put(p, new Runnable()
            {
                @Override
                public void run()
                {
                    pick8x8(startW);
                }
            });
        }

        tabs.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent changeEvent)
            {
                Component c = tabs.getSelectedComponent();

                Runnable r = perTab.get(c);
                SwingUtilities.invokeLater(r);
            }
        });

        tabs.setSelectedIndex(1);
    }

    public void pickFarm()
    {
        try {
            bv.setDataSet(ModelAndViews.farm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pick8x8(JTextField startW)
    {
        int baseBT = Integer.parseInt(startW.getText());
        try {
            bv.setDataSet(ModelAndViews.blocks8x8parade(baseBT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pickPerBlockdata(JSpinner startW)
    {
        int baseBT = Integer.parseInt(startW.getValue().toString());
        try {
            bv.setDataSet(ModelAndViews.blocksPerData(baseBT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
