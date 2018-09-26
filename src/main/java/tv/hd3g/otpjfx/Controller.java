/*
 * This file is part of otpjfx.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * Copyright (C) hdsdi3g for hd3g.tv 2018
 * 
*/
package tv.hd3g.otpjfx;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Controller {
	
	void startApp(Stage stage, BorderPane root) {
		Scene scene = new Scene(root);
		// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		// stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		
		stage.setScene(scene);
		stage.show();
		
		stage.setOnCloseRequest(event -> {
			System.exit(0);
		});
		
		btnclose.setOnAction(event -> {
			event.consume();
			System.exit(0);
		});
		
		table_col_name.setCellValueFactory(p -> {
			return new ReadOnlyStringWrapper(p.getValue().item.getName());
		});
		table_col_value.setCellValueFactory(p -> {
			return new ReadOnlyStringWrapper(p.getValue().last_value);
		});
	}
	
	/*
	 **********************
	 * JAVAFX CONTROLS ZONE
	 **********************
	 */
	
	@FXML
	private Button btnclose;
	
	@FXML
	private TableView<ComputedItem> table;
	
	@FXML
	private TableColumn<ComputedItem, String> table_col_name;
	
	@FXML
	private TableColumn<ComputedItem, String> table_col_value;
	
	@FXML
	private ProgressIndicator progress;
	
	public TableView<ComputedItem> getTable() {
		return table;
	}
	
	public ProgressIndicator getProgress() {
		return progress;
	}
	
	static class ComputedItem {
		final ConfigItem item;
		String last_value = "??? ???";
		
		ComputedItem(ConfigItem item) {
			this.item = item;
		}
		
	}
}
