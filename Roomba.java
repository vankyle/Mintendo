import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.Timer;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

//First level of our game
public class Roomba implements GameState {
	boolean isRotating = false;

	Game game;
	Timer timer;
	boolean rotating;
	
	public Roomba(Game thegame) {
		game = thegame;
		rotating = false;
	}

	@Override
	public void roomba(Stage stage, BorderPane borderPane) {
		
		RoombaObject player = new RoombaObject(50, 0, 0, 1, 1, true, 200, 200, new Point2D(0, -1));
		borderPane.getChildren().add(player);
		
		Rectangle bareMinimumBar = new Rectangle(75, 50, 300, 25);
		bareMinimumBar.setFill(Color.TRANSPARENT);
		bareMinimumBar.setStrokeWidth(4);
		bareMinimumBar.setStroke(Color.BLACK);
		borderPane.getChildren().add(bareMinimumBar);
		
		Rectangle bareMinimumThreshold = new Rectangle(150, 50, 50, 25);
		bareMinimumThreshold.setFill(Color.TRANSPARENT);
		bareMinimumThreshold.setStrokeWidth(4);
		bareMinimumThreshold.setStroke(Color.BLACK);
		borderPane.getChildren().add(bareMinimumThreshold);
		
		WorkMeter workMeter = new WorkMeter(76, 52, 50, 21);
		borderPane.getChildren().add(0, workMeter);
		
		LinkedList<Trail> trails = new LinkedList<Trail>();
		LinkedList<Clutter> clutterlist = new LinkedList<Clutter>();
		AnimationTimer animTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            	if(!rotating)
            	{
            		player.update();
            		
            		Trail trail = new Trail(player.getLoc().getX(), player.getLoc().getY()+200, player.getRadius()-20, player);
            		borderPane.getChildren().add(0, trail);
            		trails.add(trail);
            	}
            	if(trails.size() > 400)
            	{
            		borderPane.getChildren().remove(trails.get(0));
            		trails.remove(0);
            	}
            	for (Trail prevTrail : trails)
            			prevTrail.update();
            	
        		workMeter.update();
            	
            	double rand = Math.random();
            	if (rand < 0.05 && clutterlist.size() < 500)
            	{
            		Clutter clutter = new Clutter();
            		borderPane.getChildren().add(0, clutter);
            		clutterlist.add(clutter);
            	}
            	
            	for (Clutter clut : clutterlist)
            	{
            		//TODO: Figure out why we need the +200 +200 xy for everything... Something to do with JavaFX translations. Idk :(
            		//TODO: Sometimes things just stop rendering in JavaFX like clutter opacity wont change, trails stop, roomba freezes, ect
            		//TODO: Figure out why removing clutter from clutterlist yields exceptions. Race condition with animation timer??
            		if((!clut.pickedup) && clut.inCircle(player.centeredx+200, player.centeredy+200, player.getRadius()))
            		{
            			clut.setOpacity(0);
            			borderPane.getChildren().remove(clut);
            			workMeter.addWork(10);
            		}
            	}
            	
            	//TODO: Add count down timer for when player is between bare minimum threshold
            	//Once countdown reaches 0, next level. If player leaves threshold, timer resets.
            	if(workMeter.score > 75 && workMeter.score < 125)
            		System.out.println(workMeter.score);
            	
            	

            	
            	
            	//TODO: Add battery life and bare minimum meters to update each tick
            }
        };
        animTimer.start();
        
		borderPane.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {	
			public void handle (MouseEvent event) {
				timer = new Timer(75, new ActionListener() {
					@Override
	                public void actionPerformed(ActionEvent e) {
						
						rotating = true;
						//TODO: Fix bug where pressing both RMB and LMB at the same time causes non-stop spin
						if (event.getButton() == MouseButton.PRIMARY && !(event.getButton() == MouseButton.SECONDARY))
							player.mouseRight();
						else if (event.getButton() == MouseButton.SECONDARY && !(event.getButton() == MouseButton.PRIMARY))
							player.mouseLeft();
	                }
	            });
	            timer.start();
			}
		});
		
		borderPane.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {	
			public void handle (MouseEvent event) {
				if (timer != null) {
	                timer.stop();
	                rotating = false;
	            }
			}
		});
		
		
		//TODO: Remove everything and event handlers before continuing to next level
	}
	
}
