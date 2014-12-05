package com.purplefrog.minecraftExplorer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * A minecraft world save directory contains directories like data, region, playerdata, DIM1, and files like level.dat.
 *
 * <p>
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/11/13
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorldPicker
    extends JPanel
{
    private final List<File> files;
    private File pickedDirectory;

    public WorldPicker(List<File> files_)
    {
        super(new BorderLayout());

        setBorder(BorderFactory.createTitledBorder("minecraft worlds"));

        this.files =files_;
        final JList candidateList = new JList(new AbstractListModel()
        {
            @Override
            public int getSize()
            {
                return files.size();
            }

            @Override
            public Object getElementAt(int i)
            {
                return files.get(i).getName();
            }
        });
        candidateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane pane = new JScrollPane(candidateList);

        add(pane);

        JPanel bottom = new JPanel(new FlowLayout());
        add(bottom, BorderLayout.SOUTH);

        JButton ok = new JButton("OK");
        bottom.add(ok);
        ok.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                int idx = candidateList.getSelectedIndex();
                setPickedDirectory(idx<0 ? null : files.get(idx));
            }
        });

        JButton cancel = new JButton("Cancel");
        bottom.add(cancel);
        cancel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                setPickedDirectory(null);
            }
        });
    }

    public static File pickSaveDir()
    {
        return pickSaveDir(true);
    }

    public static File pickSaveDir(boolean throwIfNull)
    {
        File saves = savesDir();

        if (true) {
            JDialog dlg = new JDialog();
            dlg.setModal(true);

            List<File> candidates = candidateDirectories(saves);

            File rval;
            if (candidates.isEmpty()) {
                rval = null;
            } else {
                WorldPicker wp = new WorldPicker(candidates);
                dlg.getContentPane().add(wp);

                dlg.pack();
                dlg.setVisible(true);

                rval = wp.getPickedDirectory();
            }
            if (throwIfNull && rval==null)
                throw new NullPointerException("no minecraft save selected, or no saves exist in "+savesDir());

            return rval;
        }

        if (false)
            return new File(saves, "2013-Aug-31");
        if (false)
            return new File(saves, "snapshot");
        if (false)
            return new File(saves, "2013-09-scratch");
        return menger5();
    }

    public File getPickedDirectory()
    {
        return pickedDirectory;
    }

    public void setPickedDirectory(File pickedDirectory)
    {
        this.pickedDirectory = pickedDirectory;
        Frame fr = JOptionPane.getFrameForComponent(this);
        fr.setVisible(false);
    }

    public static List<File> candidateDirectories(File saves)
    {
        File[] subdirs = saves.listFiles();
        if (subdirs ==null)
            return new LinkedList<File>();

        List<File> rval = new ArrayList<File>();
        for (File subdir : subdirs) {
            if (couldBeMinecraftWorld(subdir))
                rval.add(subdir);
        }

        return rval;
    }

    private static boolean couldBeMinecraftWorld(File candidate)
    {
        File regions = new File(candidate, "region");
        return regions.exists() && regions.isDirectory();
    }

    public static File menger5()
    {
        return new File(savesDir(), "menger-5");
    }

    public static File savesDir()
    {
        String os = System.getProperty("os.name");
        // XXX what is the right directory on a Windows machine?
        if (os.toLowerCase().contains("windows"))
            return new File("C:\\i have no idea");
        else
            return new File(System.getProperty("user.home"), ".minecraft/saves");
    }

}
