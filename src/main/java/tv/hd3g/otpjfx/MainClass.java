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

import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tv.hd3g.otpjfx.Controller.ComputedItem;

public class MainClass extends Application {
	
	private static List<ConfigItem> items;
	private static TimeBasedOneTimePasswordGenerator tbot_pg;
	
	public static void main(String[] args) throws Exception {
		Yaml yaml = new Yaml(new Constructor(ConfigItem.class));
		FileReader fr = new FileReader(new File(System.getProperty("configfile", "config.yml")));
		
		items = StreamSupport.stream(yaml.loadAll(fr).spliterator(), false).map(o -> (ConfigItem) o).collect(Collectors.toUnmodifiableList());
		fr.close();
		
		tbot_pg = new TimeBasedOneTimePasswordGenerator();
		Application.launch(args);
		
		final int max_name_len = items.stream().mapToInt(i -> i.getName().length()).summaryStatistics().getMax();
		
		long time_step = tbot_pg.getTimeStep(TimeUnit.MILLISECONDS);
		long sub_time = 0;
		
		while (true) {
			
			items.forEach(i -> {
				System.out.print(String.format("%1$-" + max_name_len + "s", i.getName()));
				System.out.print(" ");
				System.out.println(i.getOneTimePassword(tbot_pg, new Date(System.currentTimeMillis()))/* + "\t" + i.getOneTimePassword(tbot_pg, later)*/);
			});
			
			LongStream.range(0l, time_step / 1000l).forEach(i -> System.out.print("="));
			System.out.println();
			
			sub_time = time_step - System.currentTimeMillis() % time_step;
			LongStream.range(0l, time_step / 1000l - sub_time / 1000l).forEach(i -> System.out.print("."));
			
			while (sub_time > 100l) {
				sub_time = time_step - System.currentTimeMillis() % time_step;
				
				if (sub_time % 1000l > 900l) {
					System.out.print(".");
				}
				Thread.sleep(100);
			}
			System.out.println();
			System.out.println();
		}
	}
	
	public void start(Stage stage) throws Exception {
		FXMLLoader d = new FXMLLoader();
		// d.setResources(ResourceBundle.getBundle(getClass().getPackage().getName() + ".messages"));
		BorderPane root = (BorderPane) d.load(getClass().getResource("view.fxml").openStream());
		
		Controller controller = d.getController();
		controller.startApp(stage, root);
		
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				updateLoop(controller);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	private void updateLoop(Controller controller) throws InterruptedException {
		controller.getTable().getItems().addAll(items.stream().map(config_item -> {
			ComputedItem ci = new ComputedItem(config_item);
			ci.last_value = config_item.getOneTimePassword(tbot_pg, new Date(System.currentTimeMillis()));
			return ci;
		}).collect(Collectors.toUnmodifiableList()));
		
		final long time_step = tbot_pg.getTimeStep(TimeUnit.MILLISECONDS);
		
		while (true) {
			final long previous_sub_time = time_step - System.currentTimeMillis() % time_step;
			
			Platform.runLater(() -> {
				controller.getTable().getItems().forEach(item -> {
					item.last_value = item.item.getOneTimePassword(tbot_pg, new Date(System.currentTimeMillis()));
				});
				controller.getTable().refresh();
			});
			
			long sub_time = previous_sub_time;
			while (sub_time > 100l) {
				sub_time = time_step - System.currentTimeMillis() % time_step;
				
				final double progress = 1d - (double) sub_time / (double) time_step;
				Platform.runLater(() -> {
					controller.getProgress().setProgress(progress);
				});
				
				Thread.sleep(100);
			}
		}
		
	}
	
}
