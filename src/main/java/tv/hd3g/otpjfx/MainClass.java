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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

public class MainClass {
	
	public static void main(String[] args) throws Exception {
		// TODO2 remove log4j2
		Yaml yaml = new Yaml(new Constructor(ConfigItem.class));
		FileReader fr = new FileReader(new File(System.getProperty("configfile", "config.yml")));
		
		List<ConfigItem> items = StreamSupport.stream(yaml.loadAll(fr).spliterator(), false).map(o -> (ConfigItem) o).collect(Collectors.toUnmodifiableList());
		fr.close();
		
		final TimeBasedOneTimePasswordGenerator tbot_pg = new TimeBasedOneTimePasswordGenerator();
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
	
}
