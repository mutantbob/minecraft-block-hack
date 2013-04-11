package com.purplefrog.minecraftExplorer;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: thoth
 * Date: 4/11/13
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class Maze
{
    public final int width;
    public final int height;
    byte[] cells;
    /**
     * per-cell bit set.
     * n=1; s=2; e=4; w=8
     */
    byte[] walls;

    public Maze(int width, int height)
    {
        this.width = width;
        this.height = height;
        cells = new byte[width*height];
        walls = new byte[width*height];
        Arrays.fill(walls, (byte) 0xf);
    }

    public void fill(Random r)
    {
        Point start = null;
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                if (getCellCode(x, y) ==0) {
                    start = new Point(x,y);
                    break;
                }
            }
        }

        Set<Point> boundary = new HashSet<Point>();

        maybeExpandBoundary(boundary, start.x, start.y, r);

        while (!boundary.isEmpty()) {
            Point p0 = pickOne(boundary, r);
            maybeExpandBoundary(boundary, p0.x, p0.y, r);
        }
    }

    public static Point pickOne(Collection<Point> src, Random r)
    {
        int doomed = r.nextInt(src.size());

        int i=0;
        for (Iterator<Point> iterator = src.iterator(); iterator.hasNext(); i++) {
            Point next = iterator.next();
            if (i==doomed) {
                Point rval = next;
                iterator.remove();
                return rval;
            }
        }
        throw new IllegalArgumentException("wtf?");
    }


    public void maybeExpandBoundary(Set<Point> boundary, int x, int y, Random r)
    {
        int idx1 = y*width+x;

        cells[idx1] = 1;

        List<EdgeCandidate> doomed  = new ArrayList<EdgeCandidate>();

        if (x+1 < width) {
            maybeAddCell(boundary, x + 1, y, new EdgeCandidate(idx1, 4), doomed);
        }

        if (y+1 < height) {
            maybeAddCell(boundary, x, y+1, new EdgeCandidate(idx1, 1), doomed);
        }

        if (x >0) {
            maybeAddCell(boundary, x - 1, y, new EdgeCandidate(idx1, 8), doomed);
        }

        if (y >0) {
            maybeAddCell(boundary, x, y-1, new EdgeCandidate(idx1, 2), doomed);
        }

        if (!doomed.isEmpty()) {
            EdgeCandidate toDestroy = doomed.get(r.nextInt(doomed.size()));
            knockDownWall(toDestroy);
        }

    }

    public void knockDownWall(EdgeCandidate toDestroy)
    {
        knockDownEdgeWall(toDestroy);

        walls[toDestroy.idx2] &= ~ toDestroy.wallflag2;
    }

    /**
     * for walls on the boundary where there is no cell on the other side
     * @param toDestroy
     */
    public void knockDownEdgeWall(EdgeCandidate toDestroy)
    {
        walls[toDestroy.idx1] &= ~ toDestroy.wallflag1;
    }

    void maybeAddCell(Set<Point> boundary, int x, int y, EdgeCandidate edgeCandidate, List<EdgeCandidate> doomed)
    {
        if (getCellCode(x, y) ==0) {
            boundary.add(new Point(x, y));
        } else {
            doomed.add(edgeCandidate);
        }
    }

    private byte getCellCode(int x, int y)
    {
        return cells[y*width + x];
    }

    public String textDiagram()
    {
        char space = ' ';
        char vWall = '|';
        char hWall = '-';
        char pillar = '+';

        pillar = hWall =vWall = '#';
        int stride = 3*width+1;
        char[] diagram = new char[ ( 3*height+1) * (stride) ];

        for (int y=0; y<height; y++) {
            int anchor = (height-y-1)*3*stride;
            for (int x=0; x<width; x++) {
                int pos = anchor + x*3;
                int pos_ = y*width+x;

                diagram[pos] = pillar;
                diagram[pos+1] = (walls[pos_]&1)==0 ? space :hWall;
                diagram[pos+2] = pillar;
                diagram[pos+stride] = (walls[pos_]&8)==0 ? space : vWall;
                diagram[pos+stride+1] = space;
                diagram[pos+stride+2] = (walls[pos_]&4)==0 ? space : vWall;
                diagram[pos+2*stride] = pillar;
                diagram[pos+2*stride+1] = (walls[pos_]&2)==0 ? space :hWall;
                diagram[pos+2*stride+2] = pillar;
            }
            diagram[ anchor + stride -1] = '\n';
            diagram[ anchor +2*stride -1] = '\n';
            diagram[ anchor +3*stride -1] = '\n';
        }

        return new String(diagram);
    }

    public boolean hasNorthWall(int x, int y)
    {
        return 0 != (1&walls[y*width+x]);
    }

    public boolean hasSouthWall(int x, int y)
    {
        return 0 != (2&walls[y*width+x]);
    }

    public boolean hasEastWall(int x, int y)
    {
        return 0 != (4&walls[y*width+x]);
    }

    public boolean hasWestWall(int x, int y)
    {
        return 0 != (8&walls[y*width+x]);
    }
    public class EdgeCandidate
    {
        int idx1;
        int wallflag1;
        int idx2;
        int wallflag2;

        public EdgeCandidate(int x, int y, int wallFlag)
        {
            this(y*width +x, wallFlag);
        }

        public EdgeCandidate(int idx1, int wallflag1)
        {
            this.idx1 = idx1;
            this.wallflag1 = wallflag1;
            switch(wallflag1) {
                case 1:
                    wallflag2 = 2;
                    idx2 = idx1+width;
                    break;
                case 2:
                    wallflag2 = 1;
                    idx2 = idx1-width;
                    break;
                case 4:
                    wallflag2 = 8;
                    idx2 = idx1+1;
                    break;
                case 8:
                    wallflag2 = 4;
                    idx2 = idx1-1;
                    break;
                default:
                    throw new IllegalArgumentException("bad wall flag "+wallflag1);
            }
        }
    }
}
