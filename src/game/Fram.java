package game;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import game.Block;
import game.DFS;

public class Fram extends JFrame  {
	private static final int blockSize = 80; 
	private int row,column,leftBlocks;		//格子数目
	private JPanel gridPanel;	//格子面板
	private Block[] blocksArray;
	private JButton restart,prompt;

	private ButtonListener buttonListener;
	private LabelListener labelListener;
	
	private Block lastClickedBlock;
	private DFS dfs;
	private Block[] key;
	public Fram(int row,int column){
		this.row = row;
		this.column = column;
		leftBlocks = row*column;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize((column+1)*blockSize,(row+2)*blockSize);
		setLayout(null);
		
		restart = new JButton("restart");
		prompt 	= new JButton("prompt");
		buttonListener = new ButtonListener();
		labelListener = new LabelListener();
		lastClickedBlock = null;
		dfs = new DFS(this);
		key = new Block[2];
		
		
		addButton();
		addBlockPanel();
		isDeadlocks();
		setVisible(true);
	}
	
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
	public Vector<Point> getNeighbour(Point pos) {
		//该坐标所在位置
		int x = (int)pos.getX(),
			y = (int)pos.getY();
		Point[] around = {new Point(x+1, y),new Point(x, y+1),new Point(x-1,y),new Point(x, y-1)};
		Vector<Point> result = new Vector<Point>();
		
		for(Point p:around){
			int t_x = (int)p.getX(),
				t_y = (int)p.getY();
			if(t_x<0||t_x>=column||t_y<0||t_y>=row){
				continue;
			}else{
				//System.out.println(blocksArray[t_x*t_y].getPos());
				if(blocksArray[t_y*column+t_x].getId()==-1){
					result.addElement(p);
				}
			}
		}
		return result;
	}
	
	
	//按钮添加到窗体并且设定位置
	private void addButton(){
		restart.addActionListener(buttonListener);
		prompt.addActionListener(buttonListener);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
		buttonPanel.add(restart);
		buttonPanel.add(prompt);
		add(buttonPanel);
		buttonPanel.setBounds(0, 0, this.getSize().width,40);
	}
	//面板添加到窗体并且设置位置
	private void addBlockPanel() {
		if(gridPanel!=null){
			remove(gridPanel);
			initBlockPanel();
			add(gridPanel);
			setVisible(true);
		}else {
			initBlockPanel();
			add(gridPanel);
		}
		gridPanel.setLocation((this.getSize().width-gridPanel.getSize().width)/2,(this.getSize().height-gridPanel.getSize().height)/2);	
	}
	//初始化格子面板
	private void initBlockPanel(){
		//格子上下左右之间的间隙
		int gap = 2;
		//初始化面板，用格子布局
		gridPanel = new JPanel(new GridLayout(row, column ,gap,gap));
		//设置面板大小
		gridPanel.setSize(column*blockSize+(column-1)*gap,row*blockSize+(row-1)*gap);
		
		initBlocks();
		for(int i = 0;i<row;i++){
			for(int j = 0;j<column;j++){
				//绑定监听器，设置位置，添加到面板中
				blocksArray[i*column+j].addMouseListener(labelListener);
				blocksArray[i*column+j].setPos(j,i);
				gridPanel.add(blocksArray[i*column+j]);
			}
		}
		
	}
	//初始化所有格子，每两个格子填充同一张图片，以乱序的形式返回
	private void initBlocks(){
		//读取所有的图片
		
		String path = "imgs/";
		File[] 		files 		= new File(path).listFiles();
		ImageIcon[] imageIcons 	= new ImageIcon[files.length];
		for(int i = 0;i<files.length;i++)
			imageIcons[i] = new ImageIcon(files[i].getPath(),Integer.toString(i));
		
		//为每个labels加上图片
		Block[] blocks= new Block[row*column];
		int n = 0;
		for(int i = 0;i<blocks.length;i+=2){
			ImageIcon tIcon = imageIcons[n%imageIcons.length];
			blocks[i]	=new Block(i, tIcon,Integer.parseInt(tIcon.getDescription()));
			blocks[i+1]	=new Block(i+1,tIcon,Integer.parseInt(tIcon.getDescription()));
			n++;
		}
		//设置边框
		for (Block block : blocks) {
			block.setBorder(Color.black, 2);
		}
		//打乱顺序
		List<Block> blockList = Arrays.asList(blocks);
		Collections.shuffle(blockList);
		
		blocksArray = blockList.toArray(new Block[blockList.size()]);
	}

	private boolean isDeadlocks(){
		for(int i=0;i<row*column;i++){
			if(blocksArray[i].getId()==-1)continue;
			for(int j = 0;j<row*column;j++){
				if(i==j||blocksArray[i].getId()==-1||!blocksArray[i].compare(blocksArray[j]))continue;
				if(check(blocksArray[i], blocksArray[j])){
					key[0] = blocksArray[i];
					key[1] = blocksArray[j];
					return false;
				}
			}
		}
		key[0] = key[1] = null;
		return true;
	}
	
	
	private boolean check(Block block,Block curr) {
		if(dfs.isNeighbor(block.getPos(),curr.getPos())) return true;
		if(dfs.isConnect(block.getPos(), curr.getPos())) return true;
		//if(dfs.canGetEdge(block.getPos())&&dfs.canGetEdge(curr.getPos()))return true;
		return false;
	}
	
	class ButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==restart){
				addBlockPanel();
				leftBlocks = row*column;
			}else if(e.getSource()==prompt){
				if(key[0]!=null&&key[1]!=null){
					key[0].setBorder(Color.yellow,2);
					key[1].setBorder(Color.yellow,2);
				}
			}
		}
	}
	
	
	
	class LabelListener implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) {
			Block curr = ((Block)e.getSource());
			if(curr.getId()==-1)return;
			
			//检测点击的对象是否不同于上一个点击的对象
			if(lastClickedBlock!=null && !e.getSource().equals(lastClickedBlock)){	
				////检测两者图片是否相同 相同则进行连同检测
				if( curr.compare(lastClickedBlock) && check(lastClickedBlock,curr)){
					lastClickedBlock.remove();
					curr.remove();
					lastClickedBlock = null;
					leftBlocks-=2;
					if(leftBlocks<=0){
						JOptionPane.showMessageDialog(Fram.this,
								"you win!", "", JOptionPane.INFORMATION_MESSAGE);
					}else{
						if(isDeadlocks())
							JOptionPane.showMessageDialog(null, "no way to go", "please restart", JOptionPane.ERROR_MESSAGE); 
					}
				}else{
					lastClickedBlock.setBorder(Color.BLACK, 2);
					lastClickedBlock = curr;
					lastClickedBlock.setBorder(Color.red, 3);
				}
				
			}else{
				lastClickedBlock=curr;
				curr.setBorder(Color.red, 3);
			}
		}


		@Override
		public void mouseEntered(MouseEvent e) {
			//如果当前的block不是被选中点那个则改变颜色
			Block curr = (Block)e.getSource();
			if(!curr.equals(lastClickedBlock)&&curr.getId()!=-1){
				((Block)e.getSource()).setBorder(Color.blue,2);
			}

		}

		@Override
		public void mouseExited(MouseEvent e) {
			//如果当前的block不是被选中点那个则改变颜色
			Block curr = (Block)e.getSource();
			if(!curr.equals(lastClickedBlock)&&curr.getId()!=-1){
				((Block)e.getSource()).setBorder(Color.black,2);
			}
			
		}
		
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		
	}
	
}








