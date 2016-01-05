package game;

import java.awt.Color;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Block extends JLabel{
	private int id;
	private int picNumber;//块所对应的图片
	private Point point;
	public Block(){
		id = -1;
		picNumber = -1;
	}
	public Block(int id,ImageIcon icon,int picNumber){
		this.id = id;
		super.setIcon(icon);
		this.picNumber = picNumber;
		point = new Point(0,0);
	}
	public boolean compare(Block another){
		//System.out.println(id+" "+picNumber+" "+another.id+" "+another.picNumber);
		return picNumber == another.picNumber;
	}
	public  int  getId() {
		return id;
	}
	public void remove(){
		if(id==-1)return;
		id = -1;
		picNumber = -1;
		super.setIcon(null);
		this.setBorder(null);
	}
	public void setPos(int x,int y) {
		point.setLocation(x, y);
	}
	public Point getPos() {
		return new Point(point);
	}
	public void setBorder(Color color,int weight) {
		setBorder(BorderFactory.createLineBorder(color, weight));
	}
}