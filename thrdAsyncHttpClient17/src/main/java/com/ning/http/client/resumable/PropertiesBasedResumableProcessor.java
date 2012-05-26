/*
 * Copyright (c) 2010-2012 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.ning.http.client.resumable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A
 * {@link com.ning.http.client.resumable.ResumableAsyncHandler.ResumableProcessor}
 * which use a properties file to store the download index information.
 */
public class PropertiesBasedResumableProcessor implements
		ResumableAsyncHandler.ResumableProcessor {

	private final static File TMP = new File(
			System.getProperty("java.io.tmpdir"), "ahc");
	private final static String storeName = "ResumableAsyncHandler.properties";
	private final ConcurrentHashMap<String, Long> properties = new ConcurrentHashMap<String, Long>();

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	@Override
	public void put(final String url, final long transferredBytes) {
		properties.put(url, transferredBytes);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	@Override
	public void remove(final String uri) {
		if (uri != null) {
			properties.remove(uri);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	@Override
	public void save(final Map<String, Long> map) {

		FileOutputStream os = null;
		try {

			if (!TMP.mkdirs()) {
				throw new IllegalStateException("Unable to create directory: "
						+ TMP.getAbsolutePath());
			}
			final File f = new File(TMP, storeName);
			if (!f.createNewFile()) {
				throw new IllegalStateException("Unable to create temp file: "
						+ f.getAbsolutePath());
			}
			if (!f.canWrite()) {
				throw new IllegalStateException();
			}

			os = new FileOutputStream(f);

			for (final Map.Entry<String, Long> e : properties.entrySet()) {
				os.write((append(e)).getBytes("UTF-8"));
			}
			os.flush();
		} catch (final Throwable e) {

		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}

	private static String append(final Map.Entry<String, Long> e) {
		return new StringBuffer(e.getKey()).append("=").append(e.getValue())
				.append("\n").toString();
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	@Override
	public Map<String, Long> load() {
		try {
			final Scanner scan = new Scanner(new File(TMP, storeName), "UTF-8");
			scan.useDelimiter("[=\n]");

			String key;
			String value;
			while (scan.hasNext()) {
				key = scan.next().trim();
				value = scan.next().trim();
				properties.put(key, Long.valueOf(value));
			}

		} catch (final FileNotFoundException ex) {

		} catch (final Throwable ex) {
			// Survive any exceptions

		}
		return properties;
	}
}
