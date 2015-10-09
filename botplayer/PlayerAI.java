import com.sun.deploy.util.StringUtils;

import java.util.*;

public class PlayerAI extends ClientAI {
	ArrayList<Wall> wallsList;
	ArrayList<PowerUp> powerupsList;
	Gameboard gameboard ;
	int height , width, gridHeight, gridWidth, totalNumTurns, targetX, targetY;
	String targetType = "";
	Opponent opponent;
	Player player;
	PowerUp powerup;
	int playerX;
	int playerY;
	boolean firstMove = true;
	Turret targetTurret;

	//all cells that need to be checked out
	HashMap<String,Cell> openList = new HashMap<String,Cell>();
	HashMap<String,Cell> closeList = new HashMap<String, Cell>();
	ArrayList<Cell> shortestPath = new ArrayList<Cell>();
	Cell nextCell, nextBulletCell;
	public PlayerAI() {
		//Write your initialization here
	}

	@Override
	public Move getMove(Gameboard gameboard, Opponent opponent, Player player) throws NoItemException, MapOutOfBoundsException {
		this.gameboard = gameboard;
		this.opponent = opponent;
		this.player = player;

		System.out.println("LASER COUNT: " + player.getLaserCount());
		if(player.getLaserCount()>0){
			return Move.LASER;
		}

		if(firstMove){
			totalNumTurns = gameboard.getTurnsRemaining();
			firstMove = false;
		}



		playerX = player.getX();
		playerY = player.getY();


		/*//dodge bullet/laser
		if(nextBulletCell!=null){
			nextBulletCell = null;
			System.out.println("BBB next bullet move forward");
			return Move.FORWARD;
		}

		for(Bullet b:gameboard.getBullets()){


			if(playerY==b.getY()){
				if(!gameboard.isWallAtTile(playerX,playerY-1)){
					nextBulletCell = new Cell(playerX,playerY-1);
				}else{
					nextBulletCell = new Cell(playerX,playerY+1);
				}
				break;
			}else if(playerX==b.getX()){
				if(!gameboard.isWallAtTile(playerX-1, playerY)){
					nextBulletCell=new Cell(playerX-1,playerY);
				}else{
					nextBulletCell = new Cell(playerX+1, playerY);
				}
				break;
			}
		}

		if(nextBulletCell!=null) {
		System.out.println("BBB BULLET LOCATION: " + nextBulletCell.x + "," + nextBulletCell.y);
			Direction dir2 = null;
			if (playerX == nextBulletCell.x - 1 || (playerX == width - 1 && nextBulletCell.x == 0) && (playerY == nextBulletCell.y)) {
				dir2 = Direction.RIGHT;
				if (player.getDirection() != dir2) {
					return Move.FACE_RIGHT;
				}
			} else if (playerX == nextBulletCell.x + 1 || (playerX == 0 && nextBulletCell.x == width - 1) && (playerY == nextBulletCell.y)) {
				dir2 = Direction.LEFT;
				if (player.getDirection() != dir2) {
					return Move.FACE_LEFT;
				}
			} else if (playerY == nextBulletCell.y + 1 || (playerY == 0 && nextBulletCell.y == height - 1) && (playerX == nextBulletCell.x)) {
				dir2 = Direction.UP;
				if (player.getDirection() != dir2) {
					return Move.FACE_UP;
				}
			} else if (playerY == nextBulletCell.y - 1 || (playerY == height - 1 && nextBulletCell.y == 0) && (playerX == nextBulletCell.x)) {
				dir2 = Direction.DOWN;
				if (player.getDirection() != dir2) {
					return Move.FACE_DOWN;
				}
			}else{
				System.out.println("BBB next bullet move forward2");
				return Move.FORWARD;
			}
		}
*/

			shortestPath = new ArrayList<Cell>();
			openList = new HashMap<String, Cell>();
			closeList = new HashMap<String, Cell>();

			this.height = gameboard.getHeight();
			this.width = gameboard.getWidth();
			this.gridHeight =2;
			this.gridWidth = 2;

		if(gameboard.getCurrentTurnNumber() > totalNumTurns/4){
			System.out.println("TURRET TIME");

			for(Turret t: gameboard.getTurrets()){
				if ((t.getX() < gridWidth + playerX || t.getX() >= gridWidth - playerX) && (t.getY() < gridHeight + playerY) || t.getY() >= gridHeight - playerY) {
					targetX = t.getX();
					targetY = t.getY();
					targetType = "turret";
				}
			}

		}else {
			powerupsList = gameboard.getPowerUps();
			for (PowerUp p : powerupsList) {
				if ((p.getX() < gridWidth + playerX || p.getX() >= gridWidth - playerX) && (p.getY() < gridHeight + playerY) || p.getY() >= gridHeight - playerY) {
					this.powerup = p;
					break;
				}
			}
			if (this.powerup == null) {
				System.out.println("NO POWERUP");
				return Move.SHOOT;
			}
			targetX = powerup.getX();
			targetY = powerup.getY();
			targetType="powerup";
		}

			System.out.println(targetX+",target,"+targetY);

			Cell parent = new Cell(playerX,playerY);
			openList.put(playerX + "," + playerY, parent); //add player position to open list;

			Cell current = null;

			while(true){
				int lowestF = 10000;
				for(Cell c : openList.values()){

					if(c.F < lowestF){
						lowestF = c.F;
						current = c;
						System.out.println("current: "+current.x+","+current.y+"    F:"+current.F);
					}
				}

				openList.remove(current.x+","+current.y);
				closeList.put(current.x+","+current.y, current);

				findShortestPath(current, current.x, current.y, targetX, targetY);


				System.out.println("OPENLIST");
				for(String close:openList.keySet()){
					System.out.println(close);
				}
				System.out.println("CLOSEDLIST");
				for(String close:closeList.keySet()){
					System.out.println(close);
				}

				if(openList.isEmpty())
					break;
				if(current.x == targetX && current.y == targetY)
					break;



			}

			Cell destCell = closeList.get(targetX+","+targetY);
			System.out.println("SHORTEST PATH");

			while(true){
				if(destCell.x==parent.x && destCell.y==parent.y){
					break;
				}
				System.out.println(destCell.x + "," + destCell.y);
				shortestPath.add(destCell);
				destCell = destCell.parent;
			}



		nextCell = shortestPath.get(shortestPath.size() - 1);
		Direction dir = null;
		if (playerX == nextCell.x - 1 || (playerX == width - 1 && nextCell.x == 0) && (playerY == nextCell.y)) {
			dir = Direction.RIGHT;
			if (player.getDirection() != dir) {
				return Move.FACE_RIGHT;
			}
		} else if (playerX == nextCell.x + 1 || (playerX == 0 && nextCell.x == width-1) && (playerY == nextCell.y)) {
			dir = Direction.LEFT;
			if (player.getDirection() != dir) {
				return Move.FACE_LEFT;
			}
		}else if (playerY == nextCell.y + 1 || (playerY == 0 && nextCell.y == height-1) && (playerX == nextCell.x)) {
			dir = Direction.UP;
			if (player.getDirection() != dir) {
				return Move.FACE_UP;
			}
		}else if (playerY== nextCell.y - 1 || (playerY == height-1 && nextCell.y == 0) && (playerX == nextCell.x)) {
			dir = Direction.DOWN;
			if (player.getDirection() != dir) {
				return Move.FACE_DOWN;
			}
		}




		shortestPath.remove(shortestPath.size()-1);
		return Move.FORWARD;



	}


	void findShortestPath(Cell parent,int x, int y, int destX, int destY){

		System.out.println("Enter find shortest path : " + x + "," + y + "    " + destX + "," + destY + "    height,width:" + height + "," + width);
		if(parent == null)
			System.out.println("Parent is NULL");
		else
			System.out.println("Parent:" + parent.x +","+parent.y);
		try {
			if (y+1 > height-1) {
				//System.out.println("going down");
				if(!gameboard.isWallAtTile(x, 0)){
					//System.out.println("going down2");
					Cell c = createCell(x, 0, destX, destY);
					checkadjacent(c, parent);
					System.out.println("F : "+c.F+"  G: "+c.G+"   H: "+c.H);
				}
			}else{
				//System.out.println("going down3");
				if(!gameboard.isWallAtTile(x, y + 1)){
					//System.out.println("going down4");
					Cell c = createCell(x, y + 1, destX, destY);
					checkadjacent(c, parent);
					System.out.println("F : "+c.F+"  G: "+c.G+"   H: "+c.H);
				}
			}

			if (y-1 < 0) {
				//System.out.println("going up");
				if(!gameboard.isWallAtTile(x, height-1)){
					//System.out.println("going up2");
					Cell c = createCell(x, height - 1,destX, destY);
					checkadjacent(c, parent);
					System.out.println("F : "+c.F+"  G: "+c.G+"   H: "+c.H);
				}
			}else{
				//System.out.println("going up3");
				if(!gameboard.isWallAtTile(x, y - 1)){
					//System.out.println("going up4");
					Cell c = createCell(x, y - 1, destX, destY);
					checkadjacent(c, parent);
					System.out.println("F : "+c.F+"  G: "+c.G+"   H: "+c.H);
				}
			}

			if (x+1 > width-1 ) {
				//System.out.println("going right");
				if(!gameboard.isWallAtTile(0, y)){
					//System.out.println("going right2");
					Cell c = createCell(0, y, destX, destY);
					checkadjacent(c, parent);
					System.out.println("F : "+c.F+"  G: "+c.G+"   H: "+c.H);
				}
			}else{
				//System.out.println("going right3");
				if(!gameboard.isWallAtTile(x+1, y)){
					//System.out.println("going right4");
					Cell c = createCell(x + 1, y, destX, destY);
					checkadjacent(c, parent);
					System.out.println("F : "+c.F+"  G: "+c.G+"   H: "+c.H);
				}
			}

			if (x-1 <0) {
				//System.out.println("going left");
				if(!gameboard.isWallAtTile(width-1, y)){
					//System.out.println("going left2");
					Cell c = createCell(width - 1, y, destX, destY);
					checkadjacent(c, parent);
					System.out.println("F : "+c.F+"  G: "+c.G+"   H: "+c.H);
				}
			} else {
				//System.out.println("going left3");
				if (!gameboard.isWallAtTile(x-1, y)){
					//System.out.println("going left4");
					Cell c = createCell(x-1,y, destX,destY);
					checkadjacent(c, parent);
					System.out.println("F : "+c.F+"  G: "+c.G+"   H: "+c.H);
				}
			}

		}catch(MapOutOfBoundsException e){
			System.out.println("map out of bound exception");
		}

	}

	Cell createCell(int x, int y , /*Cell parent,*/ int destX, int destY){
		Cell cell = new Cell(x,y);

		//cell.G = cell.parent.G +10;
		System.out.println("in create cell added G " + cell.G);
		//System.out.println("destina: " + destX + "," + destY);
		cell.H = (Math.abs(destX-cell.x) + Math.abs(destY-cell.y))*10;
		//cell.F = cell.G + cell.H;
		return cell;
	}

	void checkadjacent(Cell c, Cell parent){
		System.out.println("check adjacent: " + c.x + "," + c.y);
		if(!closeList.containsKey(c.x+","+c.y)){
			/*if((Math.abs(c.x-playerX) > gridWidth)|| (Math.abs(c.y-playerY) > gridHeight)) {
				openList.remove(parent.x+","+parent.y);
				closeList.put(parent.x+","+parent.y,parent);

			}else*/ if(openList.containsKey(c.x+","+c.y)){
				//Cell k = openList.get(c.x+","+c.y);
				if(c.G > parent.G + 10) {
					c.G = parent.G + 10;
					System.out.println("in check adj cell added G " + c.G);
					c.parent = parent;
					c.F = c.G + c.H;
				}
			}else{
				c.parent = parent;
				c.G = parent.G + 10;
				c.F = c.G + c.H;
				openList.put(c.x+","+c.y, c);
			}
		}
	}

	/*Move killTurrets(){


	}*/

}


























