package game;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import game.Fram;
public class DFS {
	private Fram fram;
	private int row,column;
	//private Point start,end;
	private Stack<Point> sPoints;
	private Map<Point, Boolean> mPoint;
	
	public DFS(Fram f) {
		fram = f;		
		row = fram.getRow();
		column = fram.getColumn();
		sPoints = new Stack<Point>();
		mPoint = new HashMap<Point,Boolean>();
	}
	
	//栈直接实现搜索路径；
	public boolean isConnect(Point start,Point end){
		sPoints.push(start);
		while(!sPoints.empty()){
			Point curr = sPoints.pop();
			Vector<Point> neighbour = fram.getNeighbour(curr);
			for(Point point : neighbour){
				if(isNeighbor(point, end)){
					mPoint.clear();
					return true;
				}else{
					if(!mPoint.containsKey(point)){
						mPoint.put(point, true);
						sPoints.push(point);
					}
				}
			}
		}
		
		mPoint.clear();
		return false;
		
	}
	
	
		//检测是否是邻居
	public boolean isNeighbor(Point point,Point otherPoint){
		if(point.getX()==otherPoint.getX()){
			return Math.abs(point.getY()-otherPoint.getY())==1;
		}else if(point.getY()==otherPoint.getY()){
			return Math.abs(point.getX()-otherPoint.getX())==1;
		}
		return false;
	}
	
	public boolean canGetEdge(Point start){
		int x = (int)start.getX(),
			y = (int)start.getY();
		//当前点的周围边缘四个点
		Point[]	edge = {new Point(column, y),new Point(x, row),new Point(-1, y),new Point(x, -1)};
		for(Point p : edge){
			//如果当点在边缘，直接和周围做对比，返回true
			if(isNeighbor(start, p))return true;
			//如果当前点在内部，检测能否到达周围边缘四个点，能则返回true；
			if(isConnect(start, p))return true;
		}
		return false;
		
	}
	
}
