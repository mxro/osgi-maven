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
package com.ning.http.client.extra;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import com.ning.http.client.resumable.ResumableListener;

/**
 * A {@link com.ning.http.client.listener.TransferListener} which use a
 * {@link RandomAccessFile} for storing the received bytes.
 */
public class ResumableRandomAccessFileListener implements ResumableListener {
	private final RandomAccessFile file;

	public ResumableRandomAccessFileListener(final RandomAccessFile file) {
		this.file = file;
	}

	/**
	 * This method uses the last valid bytes written on disk to position a
	 * {@link RandomAccessFile}, allowing resumable file download.
	 * 
	 * @param buffer
	 *            a {@link ByteBuffer}
	 * @throws IOException
	 */
	@Override
	public void onBytesReceived(final ByteBuffer buffer) throws IOException {
		file.seek(file.length());
		file.write(buffer.array());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onAllBytesReceived() {
		if (file != null) {
			try {
				file.close();
			} catch (final IOException e) {
				;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long length() {
		try {
			return file.length();
		} catch (final IOException e) {
			;
		}
		return 0;
	}

}
