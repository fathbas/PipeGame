import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class Level {

	private GridPane level;
	private String levelName;
	private ArrayList<CellPane> cellList;
	static final DataFormat cellPanes = new DataFormat("Cell's on the grid pane");
	private CellPane draggingCell;
	private CellPane targetCell;
	private int counter;
	private CellPane starterCell;
	private CellPane endCell;
	private CellPane currentCell;
	private CellPane cameFromCell;
	private CellPane temp;
	private boolean isLevelCompleted = false;
	private Path path = new Path();
	private Circle circle = new Circle(10, Color.RED);
	private int mX;
	private int mY;
	private int lX;
	private int lY;
	private int hX;
	private boolean animation = false;

	// empty constructor
	public Level() {

	}

	public Level(String levelName) throws FileNotFoundException {
		this.levelName = levelName;
		level = new GridPane();
		cellList = new ArrayList<CellPane>();
		counter = 0;
		this.levelConstructor();
		this.levelEvents();
	}

	public GridPane levelConstructor() throws FileNotFoundException {
		File levelTxt = new File("levels/" + getLevelName() + ".txt");
		Scanner scan = new Scanner(levelTxt);

		// read level text file
		while (scan.hasNext()) {
			String line = scan.nextLine();

			// check empty lines
			if (line.equals("")) {
				continue;
			}

			String[] lineArray = line.split(",");
			int cellId = Integer.parseInt(lineArray[0]) - 1;
			String type = lineArray[1];
			String property = lineArray[2];

			// create a CellPane
			CellPane cell = new CellPane(cellId, type, property);

			// check type and property, add image
			switch (type) {
			case "Starter":
				if (property.equals("Horizontal"))
					cell.getChildren()
							.add(new ImageView(new Image("images/starterHorizontal.jpg", 200, 200, true, true)));
				else if (property.equals("Vertical"))
					cell.getChildren()
							.add(new ImageView(new Image("images/starterVertical.jpg", 200, 200, true, true)));
				break;
			case "End":
				if (property.equals("Horizontal"))
					cell.getChildren().add(new ImageView(new Image("images/endHorizontal.jpg", 200, 200, true, true)));
				else if (property.equals("Vertical"))
					cell.getChildren().add(new ImageView(new Image("images/endVertical.jpg", 200, 200, true, true)));
				break;
			case "Empty":
				if (property.equals("none"))
					cell.getChildren().add(new ImageView(new Image("images/empty.jpg", 200, 200, true, true)));
				else if (property.equals("Free"))
					cell.getChildren().add(new ImageView(new Image("images/free.jpg", 200, 200, true, true)));
				break;
			case "Pipe":
				if (property.equals("Horizontal"))
					cell.getChildren().add(new ImageView(new Image("images/horizontal.jpg", 200, 200, true, true)));
				else if (property.equals("Vertical"))
					cell.getChildren().add(new ImageView(new Image("images/vertical.jpg", 200, 200, true, true)));
				else if (property.equals("00"))
					cell.getChildren().add(new ImageView(new Image("images/00.jpg", 200, 200, true, true)));
				else if (property.equals("01"))
					cell.getChildren().add(new ImageView(new Image("images/01.jpg", 200, 200, true, true)));
				else if (property.equals("10"))
					cell.getChildren().add(new ImageView(new Image("images/10.jpg", 200, 200, true, true)));
				else if (property.equals("11"))
					cell.getChildren().add(new ImageView(new Image("images/11.jpg", 200, 200, true, true)));
				break;
			case "PipeStatic":
				if (property.equals("Horizontal"))
					cell.getChildren()
							.add(new ImageView(new Image("images/staticHorizontal.jpg", 200, 200, true, true)));
				else if (property.equals("Vertical"))
					cell.getChildren().add(new ImageView(new Image("images/staticVertical.jpg", 200, 200, true, true)));
				else if (property.equals("00"))
					cell.getChildren().add(new ImageView(new Image("images/00static.jpg", 200, 200, true, true)));
				else if (property.equals("01"))
					cell.getChildren().add(new ImageView(new Image("images/01static.jpg", 200, 200, true, true)));
				else if (property.equals("10"))
					cell.getChildren().add(new ImageView(new Image("images/10static.jpg", 200, 200, true, true)));
				else if (property.equals("11"))
					cell.getChildren().add(new ImageView(new Image("images/11static.jpg", 200, 200, true, true)));
				break;
			}

			cellList.add(cell);
			level.add(cell, cellId % 4, cellId / 4);
		}
		scan.close();

		return level;
	}

	public void levelEvents() {
		getLevel().getChildren().forEach(item -> item.setOnDragDetected(e -> dragDetected(e, (CellPane) item)));
		getLevel().getChildren().forEach(item -> item.setOnDragOver(e -> dragOver(e, (CellPane) item)));
		getLevel().getChildren().forEach(item -> item.setOnDragDropped(e -> dragDropped(e, (CellPane) item)));
		getLevel().getChildren().forEach(item -> item.setOnDragDone(e -> dragDone(e, (CellPane) item)));
	}

	private void dragDetected(MouseEvent e, CellPane item) {

		// only empty and pipe cells can move
		if (!((CellPane) item).getType().equals("Starter") && !((CellPane) item).getType().equals("End")
				&& !((CellPane) item).getType().equals("PipeStatic") && !((CellPane) item).getProperty().equals("Free")
				&& isLevelCompleted == false) {
			Dragboard dragBoard = item.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.put(cellPanes, (CellPane) item);
			dragBoard.setContent(content);
			draggingCell = (CellPane) item;
		} else {
			e.consume();
		}
	}

	private void dragOver(DragEvent e, CellPane item) {
		Dragboard dragBoard = e.getDragboard();

		// cells can only move to free spaces and to left, right, down or up
		if (e.getGestureSource() != (CellPane) item && dragBoard.hasContent(cellPanes)
				&& ((CellPane) item).getProperty().equals("Free")
				&& (((CellPane) item).getCellId() == draggingCell.getCellId() + 1
						|| ((CellPane) item).getCellId() == draggingCell.getCellId() - 1
						|| ((CellPane) item).getCellId() == draggingCell.getCellId() + 4
						|| ((CellPane) item).getCellId() == draggingCell.getCellId() - 4)) {
			e.acceptTransferModes(TransferMode.ANY);
		} else {
			e.consume();
		}
	}

	private void dragDropped(DragEvent e, CellPane item) {
		boolean dragCompleted = false;
		Dragboard dragBoard = e.getDragboard();
		targetCell = (CellPane) item;

		// if target cell is valid swap cells
		if (dragBoard.hasContent(cellPanes) && !(targetCell.getType().equals("Starter"))
				&& !(targetCell.getType().equals("End")) && !(targetCell.getType().equals("PipeStatic"))) {
			getLevel().getChildren().remove(targetCell);
			getLevel().getChildren().remove(draggingCell);
			getLevel().add(targetCell, draggingCell.getCellId() % 4, draggingCell.getCellId() / 4);
			getLevel().add(draggingCell, targetCell.getCellId() % 4, targetCell.getCellId() / 4);
			int temp = targetCell.getCellId();
			targetCell.setCellId(draggingCell.getCellId());
			cellList.set(draggingCell.getCellId(), targetCell);
			draggingCell.setCellId(temp);
			cellList.set(temp, draggingCell);
			dragCompleted = true;
			counter++;
		}
		e.setDropCompleted(dragCompleted);
		e.consume();
	}

	private void dragDone(DragEvent e, CellPane item) {

		System.out.println(counter);

		// after each move control to is the level completed
		cameFromCell = null;
		currentCell = null;
		findStarterAndEndCell();
		isLevelComplete();
		//if level completed is true 
		if (isLevelCompleted) {
			//add circle on pane and create animation 
			level.getChildren().add(circle);

			PathTransition pathTransition = new PathTransition();
			pathTransition.setNode(circle);
			pathTransition.setPath(path);
			pathTransition.setDuration(Duration.millis(5000));
			pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
			pathTransition.setCycleCount(1);
			pathTransition.setAutoReverse(false);
			pathTransition.setOnFinished(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					animation = true;
				}
			});

			pathTransition.play();
		//	pathTransition.getOnFinished();

		} else {
			//clear circle road which in pane 
			path.getElements().clear();
		}

		e.consume();
	}

	public void isLevelComplete() {
		// control cameFromCell and currentCell is null or not
		if (cameFromCell != null && currentCell != null) {

			System.out.println("CameFromCell " + cameFromCell.getCellId());
			System.out.println("Current Cell " + currentCell.getCellId());

			// determine currentCell type
			switch (currentCell.getProperty()) {
			case "Vertical":
				// if the ball is coming from up
				if (cameFromCell.getCellId() == currentCell.getCellId() - 4) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() + 4).getProperty().equals("00")
							|| getCell(currentCell.getCellId() + 4).getProperty().equals("01")
							|| getCell(currentCell.getCellId() + 4).getProperty().equals("Vertical")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() + 4);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("00") && path != null) {
							mY += 200;
							lY += 100;
							hX -= 100;

							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(hX));
							lX -= 100;

						}
						if (currentCell.getProperty().equals("01") && path != null) {
							mY += 200;
							lY += 100;
							hX += 100;

							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(hX));
							lX += 100;

						}
						if (currentCell.getProperty().equals("Vertical") && path != null) {
							mY += 200;
							lY += 200;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));

						}
					} else {
						return;
					}
				}

				// if the ball is coming from down
				if (cameFromCell.getCellId() == currentCell.getCellId() + 4) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() - 4).getProperty().equals("11")
							|| getCell(currentCell.getCellId() - 4).getProperty().equals("10")
							|| getCell(currentCell.getCellId() - 4).getProperty().equals("Vertical")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() - 4);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("11") && path != null) {
							mY -= 200;
							lY -= 100;
							hX += 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(hX));
							lX += 100;

						}
						if (currentCell.getProperty().equals("10") && path != null) {
							mY -= 200;
							lY -= 100;
							hX -= 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(hX));
							lX -= 100;

						}
						if (currentCell.getProperty().equals("Vertical") && path != null) {
							if (currentCell.getType().equals("Pipe") || currentCell.getType().equals("PipeStatic")) {
								mY -= 200;
								lY -= 200;
								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new LineTo(lX, lY));
							}
							if (currentCell.getType().equals("End")) {
								mY -= 200;
								lY -= 70;
								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new LineTo(lX, lY));

							}
						}
					} else {
						return;
					}
				}
				break;

			case "Horizontal":
				// if the ball is coming from left
				if (cameFromCell.getCellId() == currentCell.getCellId() - 1) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() + 1).getProperty().equals("00")
							|| getCell(currentCell.getCellId() + 1).getProperty().equals("10")
							|| getCell(currentCell.getCellId() + 1).getProperty().equals("Horizontal")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() + 1);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("00") && path != null) {
							System.out.println(lX);
							mX += 200;
							hX += 100;
							lX += 100;
							lY -= 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));

						}
						if (currentCell.getProperty().equals("10") && path != null) {
							mX += 200;
							hX += 100;
							lX += 100;
							lY += 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));

						}
						if (currentCell.getProperty().equals("Horizontal") && path != null) {
							if (currentCell.getType().equals("Pipe") || currentCell.getType().equals("PipeStatic")) {
								mX += 200;
								hX += 200;
								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new HLineTo(hX));
								lX += 200;
							}
							if (currentCell.getType().equals("End")) {
								mX += 200;
								hX += 70;
								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new HLineTo(hX));

							}
						}
					} else {
						return;
					}
				}

				// if the ball is coming from right
				if (cameFromCell.getCellId() == currentCell.getCellId() + 1) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() - 1).getProperty().equals("01")
							|| getCell(currentCell.getCellId() - 1).getProperty().equals("11")
							|| getCell(currentCell.getCellId() - 1).getProperty().equals("Horizontal")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() - 1);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("11") && path != null) {
							mX -= 200;
							hX -= 100;
							lX -= 100;
							lY += 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));

						}
						if (currentCell.getProperty().equals("01") && path != null) {
							mX -= 200;
							hX -= 100;
							lX -= 100;
							lY -= 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));
						}
						if (currentCell.getProperty().equals("Horizontal") && path != null) {
							mX -= 200;
							hX -= 200;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							lX -= 200;
						}
					} else {
						return;
					}
				}
				break;

			case "00":
				// if the ball is coming from up
				if (cameFromCell.getCellId() == currentCell.getCellId() - 4) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() - 1).getProperty().equals("01")
							|| getCell(currentCell.getCellId() - 1).getProperty().equals("11")
							|| getCell(currentCell.getCellId() - 1).getProperty().equals("Horizontal")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() - 1);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("11") && path != null) {
							mX -= 100;
							mY += 100;
							hX -= 100;
							lX -= 100;
							lY += 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));

						}
						if (currentCell.getProperty().equals("01") && path != null) {
							mX -= 100;
							mY += 100;
							hX -= 100;
							lX -= 100;
							lY -= 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));
						}
						if (currentCell.getProperty().equals("Horizontal") && path != null) {
							mX -= 100;
							mY += 100;
							hX -= 200;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							lX -= 200;
						}
					} else {
						return;
					}
				}

				// if the ball is coming from left
				if (cameFromCell.getCellId() == currentCell.getCellId() - 1) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() - 4).getProperty().equals("10")
							|| getCell(currentCell.getCellId() - 4).getProperty().equals("11")
							|| getCell(currentCell.getCellId() - 4).getProperty().equals("Vertical")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() - 4);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("10") && path != null) {
							mX += 100;
							mY -= 100;
							lY -= 100;
							hX -= 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(hX));
							lX -= 100;

						}
						if (currentCell.getProperty().equals("11") && path != null) {
							mX += 100;
							mY -= 100;
							lY -= 100;
							hX += 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(hX));
							lX += 100;
						}
						if (currentCell.getProperty().equals("Vertical") && path != null) {
							if (currentCell.getType().equals("Pipe") || currentCell.getType().equals("PipeStatic")) {
								mX += 100;
								mY -= 100;
								lY -= 200;

								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new LineTo(lX, lY));
							}
							if (currentCell.getType().equals("End")) {
								mX += 100;
								mY -= 100;
								lY -= 70;
								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new LineTo(lX, lY));

							}

						}
					} else {
						return;
					}
				}
				break;

			case "01":
				// if the ball is coming from up
				if (cameFromCell.getCellId() == currentCell.getCellId() - 4) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() + 1).getProperty().equals("00")
							|| getCell(currentCell.getCellId() + 1).getProperty().equals("10")
							|| getCell(currentCell.getCellId() + 1).getProperty().equals("Horizontal")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() + 1);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("10") && path != null) {
							mX += 100;
							mY += 100;
							hX += 100;
							lX += 100;
							lY += 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));

						}
						if (currentCell.getProperty().equals("00") && path != null) {
							mX += 100;
							mY += 100;
							hX += 100;
							lX += 100;
							lY -= 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));

						}
						if (currentCell.getProperty().equals("Horizontal") && path != null) {
							if (currentCell.getType().equals("Pipe") || currentCell.getType().equals("PipeStatic")) {
								mX += 100;
								mY += 100;
								hX += 200;
								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new HLineTo(hX));
								lX += 200;
							}
							if (currentCell.getType().equals("End")) {
								mX += 100;
								mY += 100;
								hX += 70;
								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new HLineTo(hX));

							}
						}
					} else {
						return;
					}
				}

				// if the ball is coming from right
				if (cameFromCell.getCellId() == currentCell.getCellId() + 1) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() - 4).getProperty().equals("10")
							|| getCell(currentCell.getCellId() - 4).getProperty().equals("11")
							|| getCell(currentCell.getCellId() - 4).getProperty().equals("Vertical")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() - 4);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("10") && path != null) {
							mX -= 100;
							mY -= 100;
							lY -= 100;
							hX -= 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(-100));
							lX -= 100;

						}
						if (currentCell.getProperty().equals("11") && path != null) {
							mX -= 100;
							mY -= 100;
							lY -= 100;
							hX += 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(-100));
							lX += 100;
						}
						if (currentCell.getProperty().equals("Vertical") && path != null) {
							if (currentCell.getType().equals("Pipe") || currentCell.getType().equals("PipeStatic")) {
								mX -= 100;
								mY -= 100;
								lY -= 200;
								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new LineTo(lX, lY));

							}
							if (currentCell.getType().equals("End")) {
								mX -= 100;
								mY -= 100;
								lY -= 70;
								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new LineTo(lX, lY));

							}
						}
					} else {
						return;
					}
				}
				break;

			case "10":
				// if the ball is coming from left
				if (cameFromCell.getCellId() == currentCell.getCellId() - 1) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() + 4).getProperty().equals("00")
							|| getCell(currentCell.getCellId() + 4).getProperty().equals("01")
							|| getCell(currentCell.getCellId() + 4).getProperty().equals("Vertical")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() + 4);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("00") && path != null) {
							mX += 100;
							mY += 100;
							lY += 100;
							hX -= 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(hX));
							lX -= 100;
						}
						if (currentCell.getProperty().equals("01") && path != null) {
							mX += 100;
							mY += 100;
							lY += 100;
							hX += 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(hX));
							lX += 100;

						}
						if (currentCell.getProperty().equals("Vertical") && path != null) {
							mX += 100;
							mY += 100;
							lY += 200;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
						}
					} else {
						return;
					}
				}

				// if the ball is coming from down
				if (cameFromCell.getCellId() == currentCell.getCellId() + 4) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() - 1).getProperty().equals("01")
							|| getCell(currentCell.getCellId() - 1).getProperty().equals("11")
							|| getCell(currentCell.getCellId() - 1).getProperty().equals("Horizontal")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() - 1);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("01") && path != null) {
							mX -= 100;
							mY -= 100;
							hX -= 100;
							lX -= 100;
							lY -= 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));
						}
						if (currentCell.getProperty().equals("11") && path != null) {
							mX -= 100;
							mY -= 100;
							hX -= 100;
							lX -= 100;
							lY += 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));

						}
						if (currentCell.getProperty().equals("Horizontal") && path != null) {
							mX -= 100;
							mY -= 100;
							hX -= 200;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							lX -= 200;
						}
					} else {
						return;
					}
				}
				break;

			case "11":
				// if the ball is coming from down
				if (cameFromCell.getCellId() == currentCell.getCellId() + 4) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() + 1).getProperty().equals("00")
							|| getCell(currentCell.getCellId() + 1).getProperty().equals("10")
							|| getCell(currentCell.getCellId() + 1).getProperty().equals("Horizontal")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() + 1);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("00") && path != null) {
							mX += 100;
							mY -= 100;
							hX += 100;
							lX += 100;
							lY -= 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));

						}
						if (currentCell.getProperty().equals("10") && path != null) {
							mX += 100;
							mY -= 100;
							hX += 100;
							lX += 100;
							lY += 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new HLineTo(hX));
							path.getElements().add(new LineTo(lX, lY));

						}
						if (currentCell.getProperty().equals("Horizontal") && path != null) {
							if (currentCell.getType().equals("Pipe") || currentCell.getType().equals("PipeStatic")) {
								mX += 100;
								mY -= 100;
								hX += 200;
								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new HLineTo(hX));
								lX += 200;
							}
							if (currentCell.getType().equals("End")) {
								mX += 100;
								mY -= 100;
								hX += 70;
								path.getElements().add(new MoveTo(mX, mY));
								path.getElements().add(new HLineTo(hX));

							}
						}
					} else {
						return;
					}
				}

				// if the ball is coming from right
				if (cameFromCell.getCellId() == currentCell.getCellId() + 1) {
					// determine the next path is appropriate or not
					if (getCell(currentCell.getCellId() + 4).getProperty().equals("00")
							|| getCell(currentCell.getCellId() + 4).getProperty().equals("01")
							|| getCell(currentCell.getCellId() + 4).getProperty().equals("Vertical")) {
						temp = currentCell;
						currentCell = getCell(currentCell.getCellId() + 4);
						cameFromCell = temp;
						//determine and create the coordinate and path for animation 
						if (currentCell.getProperty().equals("00") && path != null) {
							mX -= 100;
							mY += 100;
							lY += 100;
							hX -= 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(hX));
							lX -= 100;
						}
						if (currentCell.getProperty().equals("01") && path != null) {
							mX -= 100;
							mY += 100;
							lY += 100;
							hX += 100;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
							path.getElements().add(new HLineTo(hX));
							lX += 100;

						}
						if (currentCell.getProperty().equals("Vertical") && path != null) {
							mX -= 100;
							mY += 100;
							lY += 200;
							path.getElements().add(new MoveTo(mX, mY));
							path.getElements().add(new LineTo(lX, lY));
						}
					} else {
						return;
					}
				}
				break;
			}
			if (currentCell.getCellId() == endCell.getCellId()) {
				System.out.println("Current Cell: " + currentCell.getCellId());
				System.out.println("End Cell: " + endCell.getCellId());
				isLevelCompleted = true;
				System.out.println(isLevelCompleted);
			} else {
				isLevelComplete();
			}
		} else {
			return;
		}
	}

	// find cell with specific cell id
	public CellPane getCell(int cellId) {
		CellPane cell = null;
		for (int i = 0; i < cellList.size(); i++) {
			if (cellId == i) {
				cell = cellList.get(i);
			}
		}
		return cell;
	}

	// find starter and end cell
	public void findStarterAndEndCell() {
		// find starter cell
		for (int i = 0; i < cellList.size(); i++) {
			if (cellList.get(i).getType().equals("Starter")) {
				starterCell = cellList.get(i);
			}
		}

		// find end cell
		for (int i = 0; i < cellList.size(); i++) {
			if (cellList.get(i).getType().equals("End")) {
				endCell = cellList.get(i);
			}
		}

		// if starter cell is vertical
		if (starterCell.getProperty().equals("Vertical")) {
			// determine the next path is appropriate or not
			if (getCell(starterCell.getCellId() + 4).getProperty().equals("00")
					|| getCell(starterCell.getCellId() + 4).getProperty().equals("01")
					|| getCell(starterCell.getCellId() + 4).getProperty().equals("Vertical")) {
				currentCell = getCell(starterCell.getCellId() + 4);
				cameFromCell = starterCell;
				//determine and create starterCell's coordinate and path for animation 
				if (starterCell.getCellId() == 0) {
					mX = 100;
					mY = 0;
					lX = 100;
					lY = 100;
					hX = 100;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));

				}
				if (starterCell.getCellId() == 1) {
					mX = 300;
					mY = 0;
					lX = 300;
					lY = 100;
					hX = 300;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));

				}
				if (starterCell.getCellId() == 2) {
					mX = 500;
					mY = 0;
					lX = 500;
					lY = 100;
					hX = 500;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));

				}
				if (starterCell.getCellId() == 3) {
					mX = 700;
					mY = 0;
					lX = 700;
					lY = 100;
					hX = 700;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));

				}
				if (starterCell.getCellId() == 4) {
					mX = 100;
					mY = 200;
					lX = 100;
					lY = 300;
					hX = 100;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));

				}
				if (starterCell.getCellId() == 5) {
					mX = 300;
					mY = 200;
					lX = 300;
					lY = 300;
					hX = 300;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
				if (starterCell.getCellId() == 6) {
					mX = 500;
					mY = 200;
					lX = 500;
					lY = 300;
					hX = 500;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
				if (starterCell.getCellId() == 7) {
					mX = 700;
					mY = 200;
					lX = 700;
					lY = 300;
					hX = 700;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
				if (starterCell.getCellId() == 8) {
					mX = 100;
					mY = 400;
					lX = 100;
					lY = 500;
					hX = 100;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
				if (starterCell.getCellId() == 9) {
					mX = 300;
					mY = 400;
					lX = 300;
					lY = 500;
					hX = 300;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
				if (starterCell.getCellId() == 10) {
					mX = 500;
					mY = 400;
					lX = 500;
					lY = 500;
					hX = 500;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
				if (starterCell.getCellId() == 11) {
					mX = 700;
					mY = 400;
					lX = 700;
					lY = 500;
					hX = 700;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
				if (starterCell.getCellId() == 12) {
					mX = 100;
					mY = 600;
					lX = 100;
					lY = 700;
					hX = 100;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
				if (starterCell.getCellId() == 13) {
					mX = 300;
					mY = 600;
					lX = 300;
					lY = 700;
					hX = 300;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
				if (starterCell.getCellId() == 14) {
					mX = 500;
					mY = 600;
					lX = 500;
					lY = 700;
					hX = 500;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
				if (starterCell.getCellId() == 15) {
					mX = 700;
					mY = 600;
					lX = 700;
					lY = 700;
					hX = 700;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
				//determine and create the coordinate and path for animation 
				if (currentCell.getProperty().equals("00") && path != null) {
					mY += 100;
					lY += 100;
					hX -= 100;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
					path.getElements().add(new HLineTo(hX));
					lX -= 100;
				}
				if (currentCell.getProperty().equals("01") && path != null) {
					mY += 100;
					lY += 100;
					hX += 100;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
					path.getElements().add(new HLineTo(hX));
					lX += 100;
				}
				if (currentCell.getProperty().equals("Vertical") && path != null) {
					mY += 100;
					lY += 200;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new LineTo(lX, lY));
				}
			}
		}

		// if starter cell is horizontal
		if (starterCell.getProperty().equals("Horizontal")) {
			// determine the next path is appropriate or not
			if (getCell(starterCell.getCellId() - 1).getProperty().equals("01")
					|| getCell(starterCell.getCellId() - 1).getProperty().equals("11")
					|| getCell(starterCell.getCellId() - 1).getProperty().equals("Horizontal")) {
				currentCell = getCell(starterCell.getCellId() + 4);
				cameFromCell = starterCell;
				//determine and create starterCell's coordinate and path for animation 
				if (starterCell.getCellId() == 0) {
					mX = 100;
					mY = 0;
					hX = 0;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 0;

				}
				if (starterCell.getCellId() == 1) {
					mX = 300;
					mY = 0;
					lX = 300;
					lY = 100;
					hX = 200;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 200;

				}
				if (starterCell.getCellId() == 2) {
					mX = 500;
					mY = 0;
					lX = 500;
					lY = 100;
					hX = 400;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 400;

				}
				if (starterCell.getCellId() == 3) {
					mX = 700;
					mY = 0;
					lX = 700;
					lY = 100;
					hX = 600;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 600;

				}
				if (starterCell.getCellId() == 4) {
					mX = 100;
					mY = 200;
					lX = 100;
					lY = 300;
					hX = 0;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 0;

				}
				if (starterCell.getCellId() == 5) {
					mX = 300;
					mY = 200;
					lX = 300;
					lY = 300;
					hX = 200;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 200;
				}
				if (starterCell.getCellId() == 6) {
					mX = 500;
					mY = 200;
					lX = 500;
					lY = 300;
					hX = 400;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 400;
				}
				if (starterCell.getCellId() == 7) {
					mX = 700;
					mY = 200;
					lX = 700;
					lY = 300;
					hX = 600;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 600;
				}
				if (starterCell.getCellId() == 8) {
					mX = 100;
					mY = 400;
					lX = 100;
					lY = 500;
					hX = 0;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 0;
				}
				if (starterCell.getCellId() == 9) {
					mX = 300;
					mY = 400;
					lX = 300;
					lY = 500;
					hX = 200;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 200;
				}
				if (starterCell.getCellId() == 10) {
					mX = 500;
					mY = 400;
					lX = 500;
					lY = 500;
					hX = 400;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 400;
				}
				if (starterCell.getCellId() == 11) {
					mX = 700;
					mY = 400;
					lX = 700;
					lY = 500;
					hX = 600;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 600;
				}
				if (starterCell.getCellId() == 12) {
					mX = 100;
					mY = 600;
					lX = 100;
					lY = 700;
					hX = 0;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 0;
				}
				if (starterCell.getCellId() == 13) {
					mX = 300;
					mY = 600;
					lX = 300;
					lY = 700;
					hX = 200;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 200;
				}
				if (starterCell.getCellId() == 14) {
					mX = 500;
					mY = 600;
					lX = 500;
					lY = 700;
					hX = 400;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 400;
				}
				if (starterCell.getCellId() == 15) {
					mX = 700;
					mY = 600;
					lX = 700;
					lY = 700;
					hX = 600;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX = 600;
				}
				//determine and create the coordinate and path for animation 
				if (currentCell.getProperty().equals("11") && path != null) {
					mX -= 100;
					hX -= 100;
					lY += 100;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					path.getElements().add(new LineTo(lX, lY));
					lX -= 100;
				}
				if (currentCell.getProperty().equals("01") && path != null) {
					mX -= 100;
					hX -= 100;
					lY -= 100;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					path.getElements().add(new LineTo(lX, lY));
					lX -= 100;
				}
				if (currentCell.getProperty().equals("Horizontal") && path != null) {
					mX -= 100;
					hX -= 200;
					path.getElements().add(new MoveTo(mX, mY));
					path.getElements().add(new HLineTo(hX));
					lX -= 200;
				}
			}
		}

	}

	public GridPane getLevel() {
		return level;
	}

	public void setLevel(GridPane level) {
		this.level = level;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public ArrayList<CellPane> getCellList() {
		return cellList;
	}

	public void setCellList(ArrayList<CellPane> cellList) {
		this.cellList = cellList;
	}

	public CellPane getDraggingCell() {
		return draggingCell;
	}

	public void setDraggingCell(CellPane draggingCell) {
		this.draggingCell = draggingCell;
	}

	public CellPane getTargetCell() {
		return targetCell;
	}

	public void setTargetCell(CellPane targetCell) {
		this.targetCell = targetCell;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public CellPane getStarterCell() {
		return starterCell;
	}

	public void setStarterCell(CellPane starterCell) {
		this.starterCell = starterCell;
	}

	public CellPane getEndCell() {
		return endCell;
	}

	public void setEndCell(CellPane endCell) {
		this.endCell = endCell;
	}

	public CellPane getCurrentCell() {
		return currentCell;
	}

	public void setCurrentCell(CellPane currentCell) {
		this.currentCell = currentCell;
	}

	public CellPane getCameFromCell() {
		return cameFromCell;
	}

	public void setCameFromCell(CellPane cameFromCell) {
		this.cameFromCell = cameFromCell;
	}

	public CellPane getTemp() {
		return temp;
	}

	public void setTemp(CellPane temp) {
		this.temp = temp;
	}

	public boolean isLevelCompleted() {
		return isLevelCompleted;
	}

	public void setLevelCompleted(boolean isLevelCompleted) {
		this.isLevelCompleted = isLevelCompleted;
	}

	public static DataFormat getCellpanes() {
		return cellPanes;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Circle getCircle() {
		return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}

	public int getmX() {
		return mX;
	}

	public void setmX(int mX) {
		this.mX = mX;
	}

	public int getmY() {
		return mY;
	}

	public void setmY(int mY) {
		this.mY = mY;
	}

	public int getlX() {
		return lX;
	}

	public void setlX(int lX) {
		this.lX = lX;
	}

	public int getlY() {
		return lY;
	}

	public void setlY(int lY) {
		this.lY = lY;
	}

	public int gethX() {
		return hX;
	}

	public void sethX(int hX) {
		this.hX = hX;
	}

	public boolean isAnimation() {
		return animation;
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

}
