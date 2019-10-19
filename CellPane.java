
import java.io.Serializable;

import javafx.scene.layout.StackPane;

public class CellPane extends StackPane implements Serializable {

	private static final long serialVersionUID = 1L;
	private int cellId;
	private String type;
	private String property;

	public CellPane() {

	}

	public CellPane(int cellId, String type, String property) {
		this.cellId = cellId;
		this.type = type;
		this.property = property;
	}

	public int getCellId() {
		return cellId;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
