package su.msk.dunno.blame.support;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

// adaptation of the algorithmes from the Jakub Debski's C++ library "RoguelikeLib 0.4 (ñ)"
public class GenLib
{
	public static int LevelElementWall  = '#';
	public static int LevelElementCorridor  = '.';
	public static int LevelElementGrass  = '"';
	public static int LevelElementPlant  = '&';
	public static int LevelElementRoom  = ',';
	public static int LevelElementDoorClose  = '+';
	public static int LevelElementDoorOpen  = '/';
	public static int LevelElementWater  = '~';
	public static int LevelElementStation  = 'A';
    public static int LevelElementCorridor_value  = Integer.MAX_VALUE - 2; // Some algorithms (like pathfinding) needs values instead of tiles
    public static int LevelElementRoom_value  = Integer.MAX_VALUE - 1;
    public static int LevelElementWall_value  = Integer.MAX_VALUE;

    private static Random rng = new Random();

    //---------------------------- Map Generators ----------------------------------------------

	public static int[][] createRDM(int N_x, int N_y, int num_stations)
	{
		int[][] map = new int[N_x][N_y];
    	for(int i = 0; i < N_x; i++)
        {
        	for(int j = 0; j < N_y; j++)
        	{
        		map[i][j] = GenLib.LevelElementRoom;
        	}
        }

		create4Rooms(map, 1, 1, N_x-2, N_y-2);
		addStations(map, num_stations);
		createSolidEdges(map);
		return map;
	}

    // default CreateAntNest
    public static int[][] CreateAntNest(int N_x, int N_y)
    {
        return CreateAntNest(N_x, N_y, false);
    }

    public static int[][] CreateAntNest(int N_x, int N_y, boolean with_rooms)
    {
        int[][] level = new int[N_x][N_y];
        for(int i = 0; i < N_x; i++)
        {
        	for(int j = 0; j < N_y; j++)
        	{
        		level[i][j] = LevelElementWall;
        	}
        }

        int x, y;

        level[N_x/2][N_y/2] = LevelElementCorridor;

        double x1, y1;
        double k;
        double dx, dy;
        int px, py;

        for (int object = 0; object < N_x*N_y/3; ++object)
        {
            // degree
            k = Math.random()*360 * 3.1419532 / 180;
            // position on ellipse by degree
            x1 = N_x/2 + N_x*Math.sin(k);
            y1 = N_y/2 + (N_y/2)*Math.cos(k);

            // object will move not too horizontal and not too vertival
            do {
                dx = Math.random()*100;
                dy = Math.random()*100;
            } while ((Math.abs(dx) < 10 && Math.abs(dy) < 10));
            dx -= 50;
            dy -= 50;
            dx /= 100;
            dy /= 100;

            int counter = 0;
            while (true) {
                // didn't catch anything after 1000 steps (just to avoid infinite loops)
                if (counter++ > 1000) {
                    object--;
                    break;
                }
                // move object by small step
                x1 += dx;
                y1 += dy;

                // change float to int

                px = (int) x1;
                py = (int) y1;

                // go through the border to the other side

                if (px < 0) {
                    px = N_x - 1;
                    x1 = px;
                }
                if (px > N_x - 1) {
                    px = 0;
                    x1 = px;
                }
                if (py < 0) {
                    py = N_y - 1;
                    y1 = py;
                }
                if (py > N_y - 1) {
                    py = 0;
                    y1 = py;
                }

                // if object has something to catch, then catch it

                if ((px > 0 && level[px - 1][py] == LevelElementCorridor) ||
                        (py > 0 && level[px][py - 1] == LevelElementCorridor) ||
                        (px < N_x-1 && level[px + 1][py] == LevelElementCorridor) ||
                        (py < N_y-1 && level[px][py+1] == LevelElementCorridor)) {
                    level[px][py] = LevelElementCorridor;
                    break;
                }
            }

        }

        if (with_rooms) {
            // add halls at the end of corridors
            for (y = 1; y < N_y - 1; y++) {
                for (x = 1; x < N_x - 1; x++) {
                    if ((x > N_x/2 - 10 && x < N_x/2 + 10 && y > N_y/2 - 5 && y < N_y/2 + 5) || level[x][y] == LevelElementWall) {
                        continue;
                    }

                    int neighbours = CountNeighboursOfType(level, LevelElementCorridor, new Point(x, y));

                    if (neighbours == 1) {
                        for (px = -1; px <= 1; px++) {
                            for (py = -1; py <= 1; py++) {
                                level[x+px][y+py] = LevelElementRoom;
                            }
                        }
                    }
                }
            }
        } // end of if (with_rooms)
        return level;
    }

 // default CreateCaves
    public static int[][] CreateCaves(int N_x, int N_y)
    {
        return CreateCaves(N_x, N_y, 1, 0.65f);
    }
    public static int[][] CreateCaves(int N_x, int N_y, int iterations, float density)
    {
    	int[][] level = new int[N_x][N_y];
    	for(int i = 0; i < N_x; i++)
        {
        	for(int j = 0; j < N_y; j++)
        	{
        		level[i][j] = LevelElementRoom;
        	}
        }

        // create a game of life cave

        int x, y;

        for (int fill = 0; fill < (N_x * N_y * density); fill++)
        {
            level[Random(N_x)][Random(N_y)] = LevelElementWall;
        }

        for (int iteration = 0; iteration < iterations; iteration++)
        {
            for (x = 0; x < N_x; x++)
            {
                for (y = 0; y < N_y; y++)
                {
                    int neighbours = CountNeighboursOfType(level, LevelElementWall, new Point(x, y));

                    if (level[x][y] == LevelElementWall)
                    {
                        if (neighbours < 4)
                        {
                            level[x][y] = LevelElementRoom;
                        }
                    }
                    else
                    {
                        if (neighbours > 4)
                        {
                            level[x][y] = LevelElementWall;
                        }
                    }

                    if (x == 0 || x == N_x-1 || y == 0 || y == N_y-1)
                    {
                        level[x][y] = LevelElementWall;
                    }
                }
            }
        }

        ConnectClosestRooms(level, true);
        ConvertValuesToTiles(level);

        return level;
    }

 // default CreateMines
    public static int[][] CreateMines(int N_x, int N_y)
    {
        return CreateMines(N_x, N_y, 10);
    }
    public static int[][] CreateMines(int N_x, int N_y, int max_number_of_rooms)
    {
        int[][] level = new int[N_x][N_y];
    	for(int i = 0; i < N_x; i++)
        {
        	for(int j = 0; j < N_y; j++)
        	{
        		level[i][j] = LevelElementWall;
        	}
        }

        int x, y, sx, sy;

        LinkedList<SRoom> rooms = new LinkedList<SRoom>();

        SRoom room = new SRoom();
        SRoom m = new SRoom();

        int random_number;
        int diff_x, diff_y;

        Point p = new Point();
        Point p1 = new Point();
        Point p2 = new Point();

        // Place rooms

        for (int room_number = 0; room_number < max_number_of_rooms; ++room_number) {
            // size of room
            sx = Random(5) + 6;
            sy = Random(5) + 6;
            if (FindOnMapRandomRectangleOfType(level, LevelElementWall, p, new Point(sx + 4, sy + 4))) {
                p.x += 2;
                p.y += 2;
                // Connect the room to existing one

                if (rooms.size() > 0) {

                    m = rooms.get(Random(rooms.size()));

                    // center of this room
                    p1.x = p.x + sx / 2;
                    p1.y = p.y + sy / 2;
                    // center of second room
                    p2.x = m.corner1.x + (m.corner2.x - m.corner1.x) / 2;
                    p2.y = m.corner1.y + (m.corner2.y - m.corner1.y) / 2;
                    // found the way to connect rooms

                    diff_x = p2.x - p1.x;
                    diff_y = p2.y - p1.y;

                    if (diff_x < 0) {
                        diff_x = -diff_x;
                    }
                    if (diff_y < 0) {
                        diff_y = -diff_y;
                    }

                    x = p1.x;
                    y = p1.y;

                    while (!(diff_x == 0 && diff_y == 0)) {
                        if (RandomLowerThatLimit(diff_x, diff_x + diff_y)) // move horizontally
                        {
                            diff_x--;
                            if (x > p2.x) {
                                x--;
                            } else {
                                x++;
                            }
                        } else {
                            diff_y--;
                            if (y > p2.y) {
                                y--;
                            } else {
                                y++;
                            }
                        }
                        // Check what is on that position
                        if (level[x][y] == LevelElementRoom)
                        {
                            break;
                        }
                        else if (level[x][y] == LevelElementCorridor)
                        {
                            if (CoinToss()) {
                                break;
                            }
                        }

                        level[x][y] = LevelElementCorridor;
                    }
                }
                // add to list of rooms

                room.corner1.x = p.x;
                room.corner1.y = p.y;
                room.corner2.x = p.x + sx;
                room.corner2.y = p.y + sy;
                room.type = room_number;
                rooms.addLast(room);

                // draw_room

                int room_type = Random(4);
                if (sx == sy) {
                    room_type = 3;
                }

                if (room_type != 2) {
                    for (y = 0; y < sy; y++) {
                        for (x = 0; x < sx; x++) {
                            switch (room_type) {
                                case 0: // rectangle room
                                case 1:
                                    level[p.x+x][p.y+y] = LevelElementRoom;
                                    break;
                                case 3: // round room
                                    if (Distance(sx / 2, sx / 2, x, y) < sx / 2)
                                    {
                                        level[p.x+x][p.y+y] = LevelElementRoom;
                                    }
                                    break;
                            }
                        }
                    }
                } // end if
                else // typ==2 - Diamond
                {
                    for (y = 0; y <= sy / 2; y++) {
                        for (x = 0; x <= sx / 2; x++) {
                            if (y >= x) {
                                level[p.x+x+sx/2][p.y+y] = LevelElementRoom;
                                level[p.x+x+sx/2][p.y+sy-y] = LevelElementRoom;
                                level[p.x+sx/2-x][p.y+y] = LevelElementRoom;
                                level[p.x+sx/2-x][p.y+sy-y] = LevelElementRoom;
                            }
                        }
                    }
                }
            } // end of room addition
        }

        return level;
    }

 // default CreateMaze
    public static int[][] CreateMaze(int N_x, int N_y)
    {
        return CreateMaze(N_x, N_y, false);
    }
    public static int[][] CreateMaze(int N_x, int N_y, boolean allow_loops)
    {
        int[][] level = new int[N_x][N_y];
    	for(int i = 0; i < N_x; i++)
        {
        	for(int j = 0; j < N_y; j++)
        	{
        		level[i][j] = LevelElementCorridor;
        	}
        }

        LinkedList<Point> drillers = new LinkedList<Point>();
        drillers.addLast(new Point(N_x/2, N_y/2));
        while (drillers.size() > 0)
        {
            for (int i = 0; i < drillers.size(); i++)
            {
                boolean remove_driller = false;
                Point m = drillers.get(i);

                switch (Random(4)) {
                    case 0:
                        m.y -= 2;
                        if (m.y < 0 || (level[m.x][m.y] == LevelElementWall))
                        {
                            boolean b;
                            if (Random(5) == 0) {
                                b = false;
                            } else {
                                b = true;
                            }

                            if (!allow_loops || (allow_loops && b)) {
                                remove_driller = true;
                                break;
                            }
                        }
                        level[m.x][m.y+1] = LevelElementWall;
                        break;
                    case 1:
                        m.y += 2;
                        if (m.y >= N_y || level[m.x][m.y] == LevelElementWall)
                        {
                            remove_driller = true;
                            break;
                        }
                        level[m.x][m.y-1] = LevelElementWall;
                        break;
                    case 2:
                        m.x -= 2;
                        if (m.x < 0 || level[m.x][m.y] == LevelElementWall)
                        {
                            remove_driller = true;
                            break;
                        }
                        level[m.x+1][m.y] = LevelElementWall;
                        break;
                    case 3:
                        m.x += 2;
                        if (m.x >= N_x || level[m.x][m.y] == LevelElementWall)
                        {
                            remove_driller = true;
                            break;
                        }
                        level[m.x-1][m.y] = LevelElementWall;
                        break;
                    }
                if (remove_driller) {
                    drillers.remove(m);
                } else {
                    drillers.addLast(new Point(m.x, m.y));
                    drillers.addLast(new Point(m.x, m.y));

                    level[m.x][m.y] = LevelElementWall;
                    ++i;
                }
            }
        }

        return level;
    }

    // default CreateStandartDungeon
    public static int[][] CreateStandardDunegon(int N_x, int N_y)
    {
        return CreateStandardDunegon(N_x, N_y, 50, true);
    }

	public static int[][] CreateStandardDunegon(int N_x, int N_y, int max_number_of_rooms, boolean with_doors)
	{
        int[][] level = new int[N_x][N_y];
        for(int i = 0; i < N_x; i++)
        {
        	for(int j = 0; j < N_y; j++)
        	{
        		level[i][j] = LevelElementWall;
        	}
        }

        Point p = new Point();
        Point room_size = new Point();

        // place rooms
        for (int room_number = 0; room_number < max_number_of_rooms; ++room_number) {
            // size of room
            room_size.x = Random(5) + 8;
            room_size.y = Random(5) + 5;
            if (FindOnMapRandomRectangleOfType(level, LevelElementWall, p, room_size)) {
                for (int x = 1; x < room_size.x - 1; x++) {
                    for (int y = 1; y < room_size.y - 1; y++) {
                        level[p.x+x][p.y+y] = LevelElementRoom;
                    }
                }
            }
        }

        ConnectClosestRooms(level, true, true); // changes tiles to values
        ConvertValuesToTiles(level);
        if (with_doors) {
            AddDoors(level, 1, 0);
        }

        return level;
    }

    //--------------------------------------------------------------------------

	private static void create4Rooms(int[][] map, int startx, int starty, int endx, int endy)
	{
		Point p = getRandomPos(map, startx+1, starty+1, endx-1, endy-1);
		int x = startx;
		int y = p.y;
		while(x <= endx/* && map[x][y] != 1*/)
		{
			map[x][y] = GenLib.LevelElementWall;
			x++;
			//drawField(map, field);
		}
		x = p.x;
		y = starty;
		while(y <= endy/* && map[x][y] != 1*/)
		{
			map[x][y] = GenLib.LevelElementWall;
			y++;
			//drawField(map, field);
		}
		if((p.x-1 - startx > 1+Math.random()*5 && p.y-1 - starty > 1+Math.random()*5) && p.x-1 >= 0 && p.y-1 >= 0)
			create4Rooms(map, startx, starty, p.x-1, p.y-1);
		if((endx - (p.x+1) > 1+Math.random()*5 && p.y-1 - starty > 1+Math.random()*5) && p.x+1 < map.length && p.y-1 >= 0)
			create4Rooms(map, p.x+1, starty, endx, p.y-1);
		if((p.x-1 - startx > 1+Math.random()*5 && endy - (p.y+1) > 1+Math.random()*5) && p.y+1 < map[0].length && p.x-1 >= 0)
			create4Rooms(map, startx, p.y+1, p.x-1, endy);
		if((endx - (p.x+1) > 1+Math.random()*5 && endy - (p.y+1) > 1+Math.random()*5) && p.x+1 < map.length && p.y+1 < map[0].length)
			create4Rooms(map, p.x+1, p.y+1, endx, endy);

		create3Doors(map, p, startx, starty, endx, endy);
	}

	private static void create3Doors(int[][] map, Point p, int startx, int starty, int endx, int endy)
	{
		int x = startx + (int)(Math.random()*(p.x-startx));
		int y = p.y;
		while((x == p.x) || (y-1 >= 0 && map[x][y-1] == GenLib.LevelElementWall) ||
			  (y+1 < map[0].length && map[x][y+1] == GenLib.LevelElementWall))
		{
			x = startx + (int)(Math.random()*(p.x-startx));
		}
		if(Math.random() < 0.3)map[x][y] = GenLib.LevelElementDoorClose;
		else map[x][y] = GenLib.LevelElementRoom;

		x = p.x + (int)(Math.random()*(endx+1-p.x));
		y = p.y;
		while((x == p.x) || (y-1 >= 0 && map[x][y-1] == GenLib.LevelElementWall) ||
			  (y+1 < map[0].length && map[x][y+1] == GenLib.LevelElementWall))
		{
			x = p.x + (int)(Math.random()*(endx+1-p.x));
		}
		if(Math.random() < 0.3)map[x][y] = GenLib.LevelElementDoorClose;
		else map[x][y] = GenLib.LevelElementRoom;

		x = p.x;
		y = starty + (int)(Math.random()*(endy-starty));
		while((y == p.y) || (x-1 >= 0 && map[x-1][y] == GenLib.LevelElementWall) ||
			  (x+1 < map.length && map[x+1][y] == GenLib.LevelElementWall))
		{
			y = starty + (int)(Math.random()*(endy-starty));
		}
		if(Math.random() < 0.3)map[x][y] = GenLib.LevelElementDoorClose;
		else map[x][y] = GenLib.LevelElementRoom;
	}

	private static void createSolidEdges(int[][] map)
	{
		for(int i = 0; i < map.length; i++)
		{
			map[i][0] = GenLib.LevelElementWall;
			map[i][map[0].length-1] = GenLib.LevelElementWall;
		}
		for(int j = 0; j < map[0].length; j++)
		{
			map[0][j] = GenLib.LevelElementWall;
			map[map.length-1][j] = GenLib.LevelElementWall;
		}
	}

	private static void addStations(int[][] map, int num)
	{
		int out_r = Math.max(9, Math.min(map.length, map[0].length)/num);
		LinkedList<Point> station_points = new LinkedList<Point>();
		for(int i = 0; i < num; i++)
		{
			Point p = addStation(map, out_r, station_points);
			if(p != null)station_points.add(p);
		}
	}

	public static Point addStation(int[][] map, int out_r, LinkedList<Point> prev_points)
    {
    	int N_x = map.length;
    	int N_y = map[0].length;
    	float even = 0;

    	Point p = new Point((int)(Math.random()*(N_x-2))+1,
    						(int)(Math.random()*(N_y-2))+1);
    	int count = 10;
    	boolean isChosen = false;
    	while(!isChosen)
    	{
    		count--;
    		if(count < 0)return null;
    		p = new Point((int)(Math.random()*(N_x-2))+1,
						  (int)(Math.random()*(N_y-2))+1);
    		isChosen = true;
    		if(p.x == 4 || p.x == N_x-5 || p.y == 4 || p.y == N_y-5)
    		{
    			isChosen = false;
    		}
    		if(isChosen)
    		{
    			for(Point prev: prev_points)
        		{
        			if(p.getDist2(prev) < out_r*out_r)
        			{
        				isChosen = false;
        				break;
        			}
        		}
    		}
    	}

        int min_x = p.x-4;
        int max_x = p.x+4;
        int min_y = p.y-4;
        int max_y = p.y+4;
        for(int i = min_x; i <= max_x; i++)
        {
        	for(int j = min_y; j <= max_y; j++)
        	{
        		if(i >= 0 && i < N_x && j >= 0 && j < N_y &&
        		   (map[i][j] == GenLib.LevelElementWall || map[i][j] == GenLib.LevelElementDoorClose))
        			map[i][j] = GenLib.LevelElementRoom;
        	}
        }
        for(int i = min_x+1; i <= max_x-1; i++)
        {
        	even++;
        	if(i >= 0 && i < N_x)
        	{
        		if(even == 4 && min_y >= 0)map[i][min_y+1] = GenLib.LevelElementDoorClose;
    	        else if(min_y+1 >= 0)map[i][min_y+1] = GenLib.LevelElementWall;
        		if(even == 4 && max_y < N_y)map[i][max_y-1] = GenLib.LevelElementDoorClose;
        		else if(max_y-1 < N_y)map[i][max_y-1] = GenLib.LevelElementWall;
        	}
        }
        for(int j = min_y+2; j <= max_y-2; j++)
        {
        	even++;
        	if(j >= 0 && j < N_y)
        	{
        		if(even == 10 && min_x >= 0)map[min_x+1][j] = GenLib.LevelElementDoorClose;
        		else if(min_x+1 >= 0)map[min_x+1][j] = GenLib.LevelElementWall;
        		if(even == 10 && max_x < N_x)map[max_x-1][j] = GenLib.LevelElementDoorClose;
            	else if(max_x-1 < N_x)map[max_x-1][j] = GenLib.LevelElementWall;
        	}
        }
        map[p.x][p.y] = GenLib.LevelElementStation;
        return p;
    }

	public static Point getRandomPos(int[][]map, int startx, int starty, int endx, int endy)
	{
		int i, j;
		int count = 1000;
		i = startx + (int)(Math.random()*(endx+1 - startx));
		j = starty + (int)(Math.random()*(endy+1 - starty));
		while(map[i][j] != GenLib.LevelElementRoom)
		{
			if(count < 0)return null;
			i = startx + (int)(Math.random()*(endx+1 - startx));
			j = starty + (int)(Math.random()*(endy+1 - starty));
			count--;
		}
		return new Point(i, j);
	}

    private static boolean FindOnMapRandomRectangleOfType(int[][] level, int type, Point pos, Point size)
    {
        LinkedList<Point> positions = new LinkedList<Point>();
        FindOnMapAllRectanglesOfType(level, type, size, positions);
        if (positions.size() == 0)
        {
            return false;
        }

        // get position of Random rectangle
        int rnd = Random(positions.size());
        pos.set(positions.get(rnd));

        return true;
    }

	private static void FindOnMapAllRectanglesOfType(int[][] level, int type, Point size, LinkedList<Point> positions )
	{
        int N_x = level.length;
        int N_y = level[0].length;
		int[][] good_points = new int[N_x][N_y];

        // count horizontals

        for (int y = 0; y < N_y; ++y)
        {
            int horizontal_count = 0;
            for (int x = 0; x < N_x; ++x)
            {
                if (level[x][y] == type)
                {
                    horizontal_count++;
                }
                else
                {
                    horizontal_count = 0;
                }

                if (horizontal_count == size.x)
                {
                    good_points[x-size.x + 1][y] = 1;
                    horizontal_count--;
                }
            }
        }

        // count verticals

        for (int x = 0; x < N_x; ++x)
        {
            int vertical_count = 0;
            for (int y = 0; y < N_y; ++y)
            {
                if (good_points[x][y] == 1)
                {
                    vertical_count++;
                } else {
                    vertical_count = 0;
                }

                if (vertical_count == size.y) {
                    positions.addLast(new Point(x, y - size.y + 1));
                    vertical_count--;
                }
            }
        }
    }

    // default ConnectClosestRooms
    private static void ConnectClosestRooms(int[][] level, boolean with_doors)
    {
        ConnectClosestRooms(level, with_doors, false);
    }

	private static void ConnectClosestRooms(int[][] level, boolean with_doors, boolean straight_connections)
	{
		int N_x = level.length;
        int N_y = level[0].length;

        FillDisconnectedRoomsWithDifferentValues(level);
        LinkedList<LinkedList<Point>> rooms = new LinkedList<LinkedList<Point>>();

        for (int y = 0; y < N_y; ++y)
        {
            for (int x = 0; x < N_x; ++x)
            {
                if (level[x][y] != LevelElementWall_value)
                {
                    if (level[x][y] >= rooms.size())
                    {
                        rooms.addLast(new LinkedList<Point>());
                    }

                    if (CountNeighboursOfType(level, LevelElementWall_value, new Point(x, y), false) > 0) // only border cells without diagonals
                    {
                        rooms.get(level[x][y]).addLast(new Point(x, y));
                    }
                }
            }
        }

        Collections.shuffle(rooms);


        if (rooms.size() < 2) {
            return;
        }

        // for warshall algorithm
        // set the connection matrix


        LinkedList<LinkedList<Boolean>> room_connections = new LinkedList<LinkedList<Boolean>>();
        LinkedList<LinkedList<Boolean>> transitive_closure = new LinkedList<LinkedList<Boolean>>();
        LinkedList<LinkedList<Integer>> distance_matrix = new LinkedList<LinkedList<Integer>>();
        LinkedList<LinkedList<Pair>> closest_cells_matrix = new LinkedList<LinkedList<Pair>>();

        for (int i = 0; i < rooms.size(); i++)
            room_connections.add(new LinkedList<Boolean>());
        for (int i = 0; i < rooms.size(); i++)
            transitive_closure.add(new LinkedList<Boolean>());
        for (int i = 0; i < rooms.size(); i++)
            distance_matrix.add(new LinkedList<Integer>());
        for (int i = 0; i < rooms.size(); i++)
            closest_cells_matrix.add(new LinkedList<Pair>());


        for (int a = 0; a < rooms.size(); ++a) {
            for (int i = 0; i < rooms.size(); i++)
                room_connections.get(a).add(true);
            for (int i = 0; i < rooms.size(); i++)
                transitive_closure.get(a).add(true);
            for (int i = 0; i < rooms.size(); i++)
                distance_matrix.get(a).add(0);
            for (int i = 0; i < rooms.size(); i++)
                closest_cells_matrix.get(a).add(make_pair(new Point(-1, -1), new Point(-1, -1)));

            for (int b = 0; b < rooms.size(); ++b) {
                room_connections.get(a).set(b, false);
                distance_matrix.get(a).set(b, Integer.MAX_VALUE);
            }
        }

        // find the closest cells for each room - Random closest cell

        for (int room_a = 0; room_a < (int) rooms.size(); ++room_a) {
            for (int room_b = 0; room_b < (int) rooms.size(); ++room_b) {
                if (room_a == room_b) {
                    continue;
                }

                Pair closest_cells = make_pair(new Point(), new Point());

                for (int m = 0; m < rooms.get(room_a).size(); m++) {
                    // for each boder cell in room_a try each border cell of room_b
                    int x1 = rooms.get(room_a).get(m).x;
                    int y1 = rooms.get(room_a).get(m).y;

                    for (int k = 0; k < rooms.get(room_b).size(); k++) {
                        int x2 = rooms.get(room_b).get(k).x;
                        int y2 = rooms.get(room_b).get(k).y;

                        int dist_ab = (int)Distance(x1, y1, x2, y2);

                        if (dist_ab < distance_matrix.get(room_a).get(room_b) || (dist_ab == distance_matrix.get(room_a).get(room_b) && CoinToss())) {
                            closest_cells = make_pair(new Point(x1, y1), new Point(x2, y2));
                            distance_matrix.get(room_a).set(room_b, dist_ab);
                        }
                    }
                }
                closest_cells_matrix.get(room_a).set(room_b, closest_cells);
            }
        }

        // Now connect the rooms to the closest ones

        for (int room_a = 0; room_a < (int) rooms.size(); ++room_a) {
            int min_distance = Integer.MAX_VALUE;
            int closest_room = room_a;
            for (int room_b = 0; room_b < (int) rooms.size(); ++room_b) {
                if (room_a == room_b) {
                    continue;
                }
                int distance = distance_matrix.get(room_a).get(room_b);
                if (distance < min_distance) {
                    min_distance = distance;
                    closest_room = room_b;
                }
            }

            // connect room_a to closest one
            Pair closest_cells = make_pair(new Point(), new Point());
            closest_cells = closest_cells_matrix.get(room_a).get(closest_room);

            int x1 = closest_cells.first.x;
            int y1 = closest_cells.first.y;
            int x2 = closest_cells.second.x;
            int y2 = closest_cells.second.y;

            if (room_connections.get(room_a).get(closest_room) == false && AddCorridor(level, x1, y1, x2, y2, straight_connections)) {
                room_connections.get(room_a).set(closest_room, true);
                room_connections.get(closest_room).set(room_a, true);
            }
        }

        // The closest rooms connected. Connect the rest until all areas are connected
        for (int to_connect_a = 0; to_connect_a != -1;) {
            int a, b, c;
            int to_connect_b;

            for (a = 0; a < rooms.size(); a++) {
                for (b = 0; b < rooms.size(); b++) {
                    transitive_closure.get(a).set(b, room_connections.get(a).get(b));
                }
            }

            for (a = 0; a < rooms.size(); a++) {
                for (b = 0; b < rooms.size(); b++) {
                    if (transitive_closure.get(a).get(b) == true && a != b) {
                        for (c = 0; c < rooms.size(); c++) {
                            if (transitive_closure.get(b).get(c) == true) {
                                transitive_closure.get(a).set(c, true);
                                transitive_closure.get(c).set(a, true);
                            }
                        }
                    }
                }
            }

            // Check if all rooms are connected
            to_connect_a = -1;
            for (a = 0; a < rooms.size() && to_connect_a == -1; ++a) {
                for (b = 0; b < rooms.size(); b++) {
                    if (a != b && transitive_closure.get(a).get(b) == false) {
                        to_connect_a = (int) a;
                        break;
                    }
                }
            }

            if (to_connect_a != -1) {
                // connect rooms a & b
                do {
                    to_connect_b = Random(rooms.size());
                } while (to_connect_b == to_connect_a);
                Pair closest_cells = make_pair(new Point(), new Point());
                closest_cells = closest_cells_matrix.get(to_connect_a).get(to_connect_b);

                int x1 = closest_cells.first.x;
                int y1 = closest_cells.first.y;
                int x2 = closest_cells.second.x;
                int y2 = closest_cells.second.y;

                AddCorridor(level, x1, y1, x2, y2, straight_connections);

                room_connections.get(to_connect_a).set(to_connect_b, true);
                room_connections.get(to_connect_b).set(to_connect_a, true);
            }
        }
    }

	private static int FillDisconnectedRoomsWithDifferentValues(int[][] level)
	{
		int N_x = level.length;
		int N_y = level[0].length;
        for (int y = 0; y < N_y; ++y)
        {
            for (int x = 0; x < N_x; ++x)
            {
                if (level[x][y] == LevelElementRoom)
                {
                    level[x][y] = LevelElementRoom_value;
                }
                else if (level[x][y] == LevelElementWall)
                {
                    level[x][y] = LevelElementWall_value;
                }
            }
        }

        int room_number = 0;

        for (int y = 0; y < N_y; ++y)
        {
            for (int x = 0; x < N_x; ++x)
            {
                if (level[x][y] == LevelElementRoom_value)
                {
                    FloodFill(level, new Point(x, y), room_number++);
                }
            }
        }
        return room_number;
    }

    // default FloodFill
    private static boolean FloodFill(int[][] level, Point position, int value)
    {
        return FloodFill(level, position, value, true, 0, new Point(-1, -1));
    }

	private static boolean FloodFill(int[][] level, Point position, int value, boolean diagonal, int gradient, Point end)
	{
		int N_x = level.length;
		int N_y = level[0].length;
        // flood fill room
        int area_value = level[position.x][position.y];
        level[position.x][position.y] = value;

        LinkedList<Point> positions = new LinkedList<Point>();
        Point m = new Point();
        positions.addLast(position);


        for (int i = 0; i < positions.size();) {
            m = positions.get(i);

            // Fill only to the end?
            if (end.x != -1 && end.equals(m)) {
                break;
            }

            int pos_x = m.x;
            int pos_y = m.y;

            int this_value = level[pos_x][pos_y];

            if (pos_x > 0)
            {
                if (level[pos_x-1][pos_y] == area_value)
                {
                    level[pos_x-1][pos_y] = this_value + gradient;
                    positions.addLast(new Point(pos_x - 1, pos_y));
                }
            }

            if (pos_x < N_x-1)
            {
                if (level[pos_x+1][pos_y] == area_value)
                {
                    level[pos_x+1][pos_y] = this_value + gradient;
                    positions.addLast(new Point(pos_x + 1, pos_y));
                }
            }

            if (pos_y > 0)
            {
                if (level[pos_x][pos_y-1] == area_value)
                {
                    level[pos_x][pos_y-1] = this_value + gradient;
                    positions.addLast(new Point(pos_x, pos_y - 1));
                }
            }

            if (pos_y < N_y-1)
            {
                if (level[pos_x][pos_y+1] == area_value)
                {
                    level[pos_x][pos_y+1] = this_value + gradient;
                    positions.addLast(new Point(pos_x, pos_y + 1));
                }
            }

            if (diagonal)
            {
                if (pos_x > 0 && pos_y > 0)
                {
                    if (level[pos_x-1][pos_y-1] == area_value)
                    {
                        level[pos_x-1][pos_y-1] = this_value + gradient;
                        positions.addLast(new Point(pos_x - 1, pos_y - 1));
                    }
                }

                if (pos_x < N_x-1 && pos_y < N_y-1)
                {
                    if (level[pos_x+1][pos_y+1] == area_value)
                    {
                        level[pos_x+1][pos_y+1] = this_value + gradient;
                        positions.addLast(new Point(pos_x + 1, pos_y + 1));
                    }
                }

                if (pos_x < N_x-1 && pos_y > 0)
                {
                    if (level[pos_x+1][pos_y-1] == area_value)
                    {
                        level[pos_x+1][pos_y-1] = this_value + gradient;
                        positions.addLast(new Point(pos_x + 1, pos_y - 1));
                    }
                }

                if (pos_x > 0 && pos_y < N_y-1)
                {
                    if (level[pos_x-1][pos_y+1] == area_value)
                    {
                        level[pos_x-1][pos_y+1] = this_value + gradient;
                        positions.addLast(new Point(pos_x - 1, pos_y + 1));
                    }
                }
            }

            m = positions.remove(i);
        }

        return true;
    }

	// default CountNeighboursOfType
    private static int CountNeighboursOfType(int[][] level, int type, Point pos) {
        return CountNeighboursOfType(level, type, pos, true);
    }

    private static int CountNeighboursOfType(int[][] level, int type, Point pos, boolean diagonal)
    {
    	int N_x = level.length;
    	int N_y = level[0].length;
        int neighbours = 0;
        if (pos.y > 0) {
            if (level[pos.x][pos.y-1] == type) // N
            {
                neighbours++;
            }
        }

        if (pos.x < N_x-1)
        {
            if (level[pos.x+1][pos.y] == type) // E
            {
                neighbours++;
            }
        }

        if (pos.x > 0 && pos.y < N_y-1)
        {
            if (level[pos.x][pos.y+1] == type) // S
            {
                neighbours++;
            }
        }

        if (pos.x > 0 && pos.y > 0)
        {
            if (level[pos.x-1][pos.y] == type) // W
            {
                neighbours++;
            }
        }

        if (diagonal) {
            if (pos.x > 0 && pos.y > 0) {
                if (level[pos.x-1][pos.y-1] == type) // NW
                {
                    neighbours++;
                }
            }

            if (pos.x < (int) N_x - 1 && pos.y > 0) {
                if (level[pos.x+1][pos.y-1] == type) // NE
                {
                    neighbours++;
                }
            }

            if (pos.x < N_x-1 && pos.y < N_y-1) // SE
            {
                if (level[pos.x+1][pos.y+1] == type)
                {
                    neighbours++;
                }
            }


            if (pos.x > 0 && pos.y < N_y-1)
            {
                if (level[pos.x-1][pos.y+1] == type) // SW
                {
                    neighbours++;
                }
            }
        }

        return neighbours;
    }

    private static Pair make_pair(Point first, Point second) {
        Pair result = new Pair();
        result.first = first;
        result.second = second;

        return result;
    }

    public static double Distance(int x1, int y1, int x2, int y2) {
        int dX = x2 - x1;
        int dY = y2 - y1;
        return java.lang.Math.sqrt(dX * dX + dY * dY);
    }
    public static double Distance(Point p1, Point p2) {
        return Distance(p1.x, p1.y, p2.x, p2.y);
    }

    private static boolean CoinToss()
    {
        return Random(2) != 0;
    }

 // default AddCoridor
    private static boolean AddCorridor(int[][] level, int start_x1, int start_y1, int start_x2, int start_y2)
    {
        return AddCorridor(level, start_x1, start_y1, start_x2, start_y2, false);
    }

    private static boolean AddCorridor(int[][] level, int start_x1, int start_y1, int start_x2, int start_y2, boolean straight)
    {
    	int N_x = level.length;
    	int N_y = level[0].length;

    	if(start_x1 < 0 || start_x1 >= N_x || start_y1 < 0 || start_y1 >= N_y)
    	{
    		return false;
    	}

    	// we start from both sides
        int x1, y1, x2, y2;

        x1 = start_x1;
        y1 = start_y1;
        x2 = start_x2;
        y2 = start_y2;

        int dir_x;
        int dir_y;

        if (start_x2 > start_x1) {
            dir_x = 1;
        } else {
            dir_x = -1;
        }

        if (start_y2 > start_y1) {
            dir_y = 1;
        } else {
            dir_y = -1;
        }


        // move into direction of the other end
        boolean first_horizontal = CoinToss();
        boolean second_horizontal = CoinToss();

        while (true) {
            if (!straight) {
                first_horizontal = CoinToss();
                second_horizontal = CoinToss();
            }

            if (x1 != x2 && y1 != y2) {
                if (first_horizontal) {
                    x1 += dir_x;
                } else {
                    y1 += dir_y;
                }
            }
            // connect rooms
            if (x1 != x2 && y1 != y2) {
                if (second_horizontal) {
                    x2 -= dir_x;
                } else {
                    y2 -= dir_y;
                }
            }

            if (level[x1][y1] == LevelElementWall_value)
            {
                level[x1][y1] = LevelElementCorridor_value;
            }
            if (level[x2][y2] == LevelElementWall_value)
            {
                level[x2][y2] = LevelElementCorridor_value;
            }

            // connect corridors if on the same level
            if (x1 == x2)
            {
                while (y1 != y2)
                {
                    y1 += dir_y;
                    if (level[x1][y1] == LevelElementWall_value)
                    {
                        level[x1][y1] = LevelElementCorridor_value;
                    }
                }
                if (level[x1][y1] == LevelElementWall_value)
                {
                    level[x1][y1] = LevelElementCorridor_value;
                }
                return true;
            }
            if (y1 == y2)
            {
                while (x1 != x2)
                {
                    x1 += dir_x;
                    if (level[x1][y1] == LevelElementWall_value)
                    {
                        level[x1][y1] = LevelElementCorridor_value;
                    }
                }
                if (level[x1][y1] == LevelElementWall_value)
                {
                    level[x1][y1] = LevelElementCorridor_value;
                }
                return true;
            }
        }
        //return true;
    }

    private static int Random(int value)
    {
        //return RNG.getInt(value);
    	return rng.nextInt(value);
    }

    private static void ConvertValuesToTiles(int[][] level)
    {
    	int N_x = level.length;
    	int N_y = level[0].length;

        for (int y = 0; y < N_y; ++y)
        {
            for (int x = 0; x < N_x; ++x)
            {
                if (level[x][y] == LevelElementCorridor_value)
                {
                    level[x][y] = LevelElementCorridor;
                }
                else if (level[x][y] == LevelElementWall_value)
                {
                    level[x][y] = LevelElementWall;
                }
                else
                {
                    level[x][y] = LevelElementRoom;
                }
            }
        }
    }

    private static void AddDoors(int[][] level, float door_probability, float open_probability)
    {
    	int N_x = level.length;
    	int N_y = level[0].length;

        for (int x = 0; x < N_x; ++x)
        {
            for (int y = 0; y < N_y; ++y)
            {
                Point pos = new Point(x, y);
                int room_cells = CountNeighboursOfType(level, LevelElementRoom, pos);
                int corridor_cells = CountNeighboursOfType(level, LevelElementCorridor, pos);
                int open_door_cells = CountNeighboursOfType(level, LevelElementDoorOpen, pos);
                int close_door_cells = CountNeighboursOfType(level, LevelElementDoorClose, pos);
                int door_cells = open_door_cells + close_door_cells;

                if (level[x][y] == LevelElementCorridor)
                {
                    if ((corridor_cells == 1 && door_cells == 0 && room_cells > 0 && room_cells < 4) ||
                            (corridor_cells == 0 && door_cells == 0))
                    {
                        float exist = ((float) Random(1000)) / 1000;
                        if (exist < door_probability) {
                            float is_open = ((float) Random(1000)) / 1000;
                            if (is_open < open_probability)
                            {
                                level[x][y] = LevelElementDoorOpen;
                            }
                            else
                            {
                                level[x][y] = LevelElementDoorClose;
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean RandomLowerThatLimit(int limit, int value) {
        if (value == 0) {
            return false;
        }
        if (Random(value) < limit) {
            return true;
        }
        return false;
    }

    static class Pair extends LinkedList {
        Point first;
        Point second;

        public Pair() {
            first = new Point();
            second = new Point();
        }
    }

    static class SRoom {
        Point corner1, corner2;
        int type;

        public SRoom() {
            corner1 = new Point();
            corner2 = new Point();
            type = 0;
        }

        public boolean IsInRoom(Point pos) {
            return (pos.x >= corner1.x && pos.x <= corner2.x && pos.y >= corner1.y && pos.y <= corner2.y);
        }

        public boolean IsInRoom(int x, int y) {
            return (x >= corner1.x && x <= corner2.x && y >= corner1.y && y <= corner2.y);
        }
    }
}

class Point
{
	public int x;
	public int y;

	public Point()
	{
		x = 0;
		y = 0;
	}

	public int getDist2(Point p)
	{
		return x*p.x + y*p.y;
	}

	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void set(Point p)
	{
		x = p.x;
		y = p.y;
	}
}