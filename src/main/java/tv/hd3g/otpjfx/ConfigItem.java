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

import java.security.InvalidKeyException;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

public class ConfigItem {
	
	private final static Base32 base32 = new Base32();
	
	private String name;
	private String secret;
	private volatile SecretKey key;
	
	private synchronized SecretKey getSecretKey() {
		if (key == null) {
			byte[] byte_key = base32.decode(secret.trim().replace("   ", "").replace("  ", "").replace(" ", ""));
			key = new SecretKeySpec(byte_key, 0, byte_key.length, "AES");
		}
		return key;
	}
	
	String getOneTimePassword(TimeBasedOneTimePasswordGenerator tbot_pg, Date date) {
		try {
			String value = String.format("%06d", tbot_pg.generateOneTimePassword(getSecretKey(), date));
			return value.substring(0, 3) + " " + value.substring(3);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Invalid secret for " + name + ": \"" + secret + "\"", e);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name; // return this;
	}
	
	public void setSecret(String secret) {
		this.secret = secret; // return this;
	}
	
}
