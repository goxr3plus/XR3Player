package aa_test_code_for_future_updates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Demostrates an Example of TableRow KeyListener now working
 *
 */
public class TableRowKeyListenerExample extends Application {

    @Override
    public void start(Stage primaryStage) {
	TableView<Item> table = new TableView<>();
	table.getItems().addAll(createData());

	TableColumn<Item, Item> deleteCol = createTableColumn("Delete", ReadOnlyObjectWrapper<Item>::new);
	deleteCol.setCellFactory(this::createDeleteCell);

	table.getColumns().addAll(Arrays.asList(createTableColumn("Name", Item::nameProperty),
		createTableColumn("Value", Item::valueProperty), deleteCol));

	// A row factory that returns a row that disables itself whenever the
	// item it displays has a value less than 5:

	table.setRowFactory(tv -> {
	    TableRow<Item> row = new TableRow<>();

	    //---------------------------------------Key Listener HERE IS NOT WORKING AT ALL FOR SOME REASON---------------------------------------------
	    row.setFocusTraversable(true);
	    row.setOnKeyReleased(k -> {
		System.out.println("Key Released....");

	    });
	    row.setOnKeyPressed(k -> {
		System.out.println("Key Released....");

	    });

	    return row;
	});
	BorderPane root = new BorderPane(table);
	Scene scene = new Scene(root, 600, 400);
	primaryStage.setScene(scene);
	primaryStage.show();
    }

    //----------------------------------IGNORE THE BELOW------------------------------------------------
    private List<Item> createData() {
	Random rng = new Random();
	List<Item> data = new ArrayList<>();
	for (int i = 1; i <= 20; i++) {
	    data.add(new Item("Item " + i, rng.nextInt(10)));
	}
	return data;
    }

    private <S, T> TableColumn<S, T> createTableColumn(String name, Function<S, ObservableValue<T>> propertyMapper) {
	TableColumn<S, T> col = new TableColumn<>(name);
	col.setCellValueFactory(cellData -> propertyMapper.apply(cellData.getValue()));
	return col;
    }

    private TableCell<Item, Item> createDeleteCell(TableColumn<Item, Item> col) {
	ObservableList<Item> itemList = col.getTableView().getItems();
	TableCell<Item, Item> cell = new TableCell<>();
	cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	Button button = new Button("Delete");
	button.setOnAction(event -> itemList.remove(cell.getItem()));
	cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(button));
	return cell;
    }

    /**
     * Item for the TableView
     *
     */
    public static class Item {
	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final IntegerProperty value = new SimpleIntegerProperty(this, "value");

	public final StringProperty nameProperty() {
	    return this.name;
	}

	public final java.lang.String getName() {
	    return this.nameProperty().get();
	}

	public final void setName(final java.lang.String name) {
	    this.nameProperty().set(name);
	}

	public final IntegerProperty valueProperty() {
	    return this.value;
	}

	public final int getValue() {
	    return this.valueProperty().get();
	}

	public final void setValue(final int value) {
	    this.valueProperty().set(value);
	}

	public Item(String name, int value) {
	    setName(name);
	    setValue(value);
	}
    }

    public static void main(String[] args) {
	launch(args);
    }
}