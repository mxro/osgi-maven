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
package com.ning.http.multipart;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import com.ning.http.client.RandomAccessBody;

public class MultipartBody implements RandomAccessBody {

	private final byte[] boundary;
	private final long contentLength;
	private final List<com.ning.http.client.Part> parts;
	private final List<RandomAccessFile> files;
	private int startPart;

	ByteArrayInputStream currentStream;
	int currentStreamPosition;
	boolean endWritten;
	boolean doneWritingParts;
	FileLocation fileLocation;
	FilePart currentFilePart;
	FileChannel currentFileChannel;

	enum FileLocation {
		NONE, START, MIDDLE, END
	}

	public MultipartBody(final List<com.ning.http.client.Part> parts,
			final String boundary, final String contentLength) {
		this.boundary = MultipartEncodingUtil.getAsciiBytes(boundary
				.substring("multipart/form-data; boundary=".length()));
		this.contentLength = Long.parseLong(contentLength);
		this.parts = parts;

		files = new ArrayList<RandomAccessFile>();

		startPart = 0;
		currentStreamPosition = -1;
		endWritten = false;
		doneWritingParts = false;
		fileLocation = FileLocation.NONE;
		currentFilePart = null;
	}

	@Override
	public void close() throws IOException {
		for (final RandomAccessFile file : files) {
			file.close();
		}
	}

	@Override
	public long getContentLength() {
		return contentLength;
	}

	@Override
	public long read(final ByteBuffer buffer) throws IOException {
		try {
			int overallLength = 0;

			final int maxLength = buffer.capacity();

			if (startPart == parts.size() && endWritten) {
				return overallLength;
			}

			boolean full = false;
			while (!full && !doneWritingParts) {
				com.ning.http.client.Part part = null;
				if (startPart < parts.size()) {
					part = parts.get(startPart);
				}
				if (currentFileChannel != null) {
					overallLength += currentFileChannel.read(buffer);

					if (currentFileChannel.position() == currentFileChannel
							.size()) {
						currentFileChannel.close();
						currentFileChannel = null;
					}

					if (overallLength == maxLength) {
						full = true;
					}
				} else if (currentStreamPosition > -1) {
					overallLength += writeToBuffer(buffer, maxLength
							- overallLength);

					if (overallLength == maxLength) {
						full = true;
					}
					if (startPart == parts.size()
							&& currentStream.available() == 0) {
						doneWritingParts = true;
					}
				} else if (part instanceof StringPart) {
					final StringPart currentPart = (StringPart) part;

					initializeStringPart(currentPart);

					startPart++;
				} else if (part instanceof com.ning.http.client.StringPart) {
					final StringPart currentPart = generateClientStringpart(part);

					initializeStringPart(currentPart);

					startPart++;
				} else if (part instanceof FilePart) {
					if (fileLocation == FileLocation.NONE) {
						currentFilePart = (FilePart) part;
						initializeFilePart(currentFilePart);
					} else if (fileLocation == FileLocation.START) {
						initializeFileBody(currentFilePart);
					} else if (fileLocation == FileLocation.MIDDLE) {
						initializeFileEnd(currentFilePart);
					} else if (fileLocation == FileLocation.END) {
						startPart++;
						if (startPart == parts.size()
								&& currentStream.available() == 0) {
							doneWritingParts = true;
						}
					}
				} else if (part instanceof com.ning.http.client.FilePart) {
					if (fileLocation == FileLocation.NONE) {
						currentFilePart = generateClientFilePart(part);
						initializeFilePart(currentFilePart);
					} else if (fileLocation == FileLocation.START) {
						initializeFileBody(currentFilePart);
					} else if (fileLocation == FileLocation.MIDDLE) {
						initializeFileEnd(currentFilePart);
					} else if (fileLocation == FileLocation.END) {
						startPart++;
						if (startPart == parts.size()
								&& currentStream.available() == 0) {
							doneWritingParts = true;
						}
					}
				} else if (part instanceof com.ning.http.client.ByteArrayPart) {
					final com.ning.http.client.ByteArrayPart bytePart = (com.ning.http.client.ByteArrayPart) part;

					if (fileLocation == FileLocation.NONE) {
						currentFilePart = generateClientByteArrayPart(bytePart);

						initializeFilePart(currentFilePart);
					} else if (fileLocation == FileLocation.START) {
						initializeByteArrayBody(currentFilePart);
					} else if (fileLocation == FileLocation.MIDDLE) {
						initializeFileEnd(currentFilePart);
					} else if (fileLocation == FileLocation.END) {
						startPart++;
						if (startPart == parts.size()
								&& currentStream.available() == 0) {
							doneWritingParts = true;
						}
					}
				}
			}

			if (doneWritingParts) {
				if (currentStreamPosition == -1) {
					final ByteArrayOutputStream endWriter = new ByteArrayOutputStream();

					Part.sendMessageEnd(endWriter, boundary);

					initializeBuffer(endWriter);
				}

				if (currentStreamPosition > -1) {
					overallLength += writeToBuffer(buffer, maxLength
							- overallLength);

					if (currentStream.available() == 0) {
						currentStream.close();
						currentStreamPosition = -1;
						endWritten = true;
					}
				}
			}
			return overallLength;

		} catch (final Exception e) {

			return 0;
		}
	}

	private void initializeByteArrayBody(final FilePart filePart)
			throws IOException {

		final ByteArrayOutputStream output = generateByteArrayBody(filePart);

		initializeBuffer(output);

		fileLocation = FileLocation.MIDDLE;
	}

	private void initializeFileEnd(final FilePart currentPart)
			throws IOException {

		final ByteArrayOutputStream output = generateFileEnd(currentPart);

		initializeBuffer(output);

		fileLocation = FileLocation.END;

	}

	private void initializeFileBody(final FilePart currentPart)
			throws IOException {

		if (FilePartSource.class.isAssignableFrom(currentPart.getSource()
				.getClass())) {

			final FilePartSource source = (FilePartSource) currentPart
					.getSource();

			final File file = source.getFile();

			final RandomAccessFile raf = new RandomAccessFile(file, "r");
			files.add(raf);

			currentFileChannel = raf.getChannel();

		} else {
			final PartSource partSource = currentPart.getSource();

			final InputStream stream = partSource.createInputStream();

			final byte[] bytes = new byte[(int) partSource.getLength()];

			stream.read(bytes);

			currentStream = new ByteArrayInputStream(bytes);

			currentStreamPosition = 0;
		}

		fileLocation = FileLocation.MIDDLE;
	}

	private void initializeFilePart(final FilePart filePart) throws IOException {

		filePart.setPartBoundary(boundary);

		final ByteArrayOutputStream output = generateFileStart(filePart);

		initializeBuffer(output);

		fileLocation = FileLocation.START;
	}

	private void initializeStringPart(final StringPart currentPart)
			throws IOException {
		currentPart.setPartBoundary(boundary);

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		Part.sendPart(outputStream, currentPart, boundary);

		initializeBuffer(outputStream);
	}

	private int writeToBuffer(final ByteBuffer buffer, final int length)
			throws IOException {

		final int available = currentStream.available();

		final int writeLength = Math.min(available, length);

		final byte[] bytes = new byte[writeLength];

		currentStream.read(bytes);

		buffer.put(bytes);

		if (available <= length) {
			currentStream.close();
			currentStreamPosition = -1;
		} else {
			currentStreamPosition += writeLength;
		}

		return writeLength;
	}

	private void initializeBuffer(final ByteArrayOutputStream outputStream)
			throws IOException {

		currentStream = new ByteArrayInputStream(outputStream.toByteArray());

		currentStreamPosition = 0;

	}

	@Override
	public long transferTo(final long position, final long count,
			final WritableByteChannel target) throws IOException {

		long overallLength = 0;

		if (startPart == parts.size()) {
			return contentLength;
		}

		int tempPart = startPart;

		for (final com.ning.http.client.Part part : parts) {
			if (part instanceof Part) {
				overallLength += handleMultiPart(target, (Part) part);
			} else {
				overallLength += handleClientPart(target, part);
			}

			tempPart++;
		}
		final ByteArrayOutputStream endWriter = new ByteArrayOutputStream();

		Part.sendMessageEnd(endWriter, boundary);

		overallLength += writeToTarget(target, endWriter);

		startPart = tempPart;

		return overallLength;
	}

	private long handleClientPart(final WritableByteChannel target,
			final com.ning.http.client.Part part) throws IOException {

		if (part.getClass().equals(com.ning.http.client.StringPart.class)) {
			final StringPart currentPart = generateClientStringpart(part);

			return handleStringPart(target, currentPart);
		} else if (part.getClass().equals(com.ning.http.client.FilePart.class)) {
			final FilePart filePart = generateClientFilePart(part);

			return handleFilePart(target, filePart);
		} else if (part.getClass().equals(
				com.ning.http.client.ByteArrayPart.class)) {
			final com.ning.http.client.ByteArrayPart bytePart = (com.ning.http.client.ByteArrayPart) part;

			final FilePart filePart = generateClientByteArrayPart(bytePart);

			return handleByteArrayPart(target, filePart, bytePart.getData());
		}

		return 0;
	}

	private FilePart generateClientByteArrayPart(
			final com.ning.http.client.ByteArrayPart bytePart) {
		final ByteArrayPartSource source = new ByteArrayPartSource(
				bytePart.getFileName(), bytePart.getData());

		final FilePart filePart = new FilePart(bytePart.getName(), source,
				bytePart.getMimeType(), bytePart.getCharSet());
		return filePart;
	}

	private FilePart generateClientFilePart(final com.ning.http.client.Part part)
			throws FileNotFoundException {
		final com.ning.http.client.FilePart currentPart = (com.ning.http.client.FilePart) part;

		final FilePart filePart = new FilePart(currentPart.getName(),
				currentPart.getFile(), currentPart.getMimeType(),
				currentPart.getCharSet());
		return filePart;
	}

	private StringPart generateClientStringpart(
			final com.ning.http.client.Part part) {
		final com.ning.http.client.StringPart stringPart = (com.ning.http.client.StringPart) part;

		final StringPart currentPart = new StringPart(stringPart.getName(),
				stringPart.getValue(), stringPart.getCharset());
		return currentPart;
	}

	private long handleByteArrayPart(final WritableByteChannel target,
			final FilePart filePart, final byte[] data) throws IOException {

		final ByteArrayOutputStream output = generateByteArrayBody(filePart);
		return writeToTarget(target, output);
	}

	private ByteArrayOutputStream generateByteArrayBody(final FilePart filePart)
			throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		Part.sendPart(output, filePart, boundary);
		return output;
	}

	private long handleFileEnd(final WritableByteChannel target,
			final FilePart filePart) throws IOException {

		final ByteArrayOutputStream endOverhead = generateFileEnd(filePart);

		return this.writeToTarget(target, endOverhead);
	}

	private ByteArrayOutputStream generateFileEnd(final FilePart filePart)
			throws IOException {
		final ByteArrayOutputStream endOverhead = new ByteArrayOutputStream();

		filePart.sendEnd(endOverhead);
		return endOverhead;
	}

	private long handleFileHeaders(final WritableByteChannel target,
			final FilePart filePart) throws IOException {
		filePart.setPartBoundary(boundary);

		final ByteArrayOutputStream overhead = generateFileStart(filePart);

		return writeToTarget(target, overhead);
	}

	private ByteArrayOutputStream generateFileStart(final FilePart filePart)
			throws IOException {
		final ByteArrayOutputStream overhead = new ByteArrayOutputStream();

		filePart.setPartBoundary(boundary);

		filePart.sendStart(overhead);
		filePart.sendDispositionHeader(overhead);
		filePart.sendContentTypeHeader(overhead);
		filePart.sendTransferEncodingHeader(overhead);
		filePart.sendEndOfHeader(overhead);
		return overhead;
	}

	private long handleFilePart(final WritableByteChannel target,
			final FilePart filePart) throws IOException {
		final FilePartStallHandler handler = new FilePartStallHandler(
				filePart.getStalledTime(), filePart);

		handler.start();

		if (FilePartSource.class.isAssignableFrom(filePart.getSource()
				.getClass())) {
			int length = 0;

			length += handleFileHeaders(target, filePart);
			final FilePartSource source = (FilePartSource) filePart.getSource();

			final File file = source.getFile();

			final RandomAccessFile raf = new RandomAccessFile(file, "r");
			files.add(raf);

			final FileChannel fc = raf.getChannel();

			final long l = file.length();
			int fileLength = 0;
			long nWrite = 0;
			synchronized (fc) {
				while (fileLength != l) {
					if (handler.isFailed()) {

						throw new FileUploadStalledException();
					}
					try {
						nWrite = fc.transferTo(fileLength, l, target);

						if (nWrite == 0) {

							try {
								fc.wait(50);
							} catch (final InterruptedException e) {

							}
						} else {
							handler.writeHappened();
						}
					} catch (final IOException ex) {
						final String message = ex.getMessage();

						// http://bugs.sun.com/view_bug.do?bug_id=5103988
						if (message != null
								&& message
										.equalsIgnoreCase("Resource temporarily unavailable")) {
							try {
								fc.wait(1000);
							} catch (final InterruptedException e) {

							}

							continue;
						} else {
							throw ex;
						}
					}
					fileLength += nWrite;
				}
			}
			handler.completed();

			fc.close();

			length += handleFileEnd(target, filePart);

			return length;
		} else {
			return handlePartSource(target, filePart);
		}
	}

	private long handlePartSource(final WritableByteChannel target,
			final FilePart filePart) throws IOException {

		int length = 0;

		length += handleFileHeaders(target, filePart);

		final PartSource partSource = filePart.getSource();

		final InputStream stream = partSource.createInputStream();

		try {
			int nRead = 0;
			while (nRead != -1) {
				// Do not buffer the entire monster in memory.
				final byte[] bytes = new byte[8192];
				nRead = stream.read(bytes);
				if (nRead > 0) {
					final ByteArrayOutputStream bos = new ByteArrayOutputStream(
							nRead);
					bos.write(bytes, 0, nRead);
					writeToTarget(target, bos);
				}
			}
		} finally {
			stream.close();
		}
		length += handleFileEnd(target, filePart);

		return length;
	}

	private long handleStringPart(final WritableByteChannel target,
			final StringPart currentPart) throws IOException {

		currentPart.setPartBoundary(boundary);

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		Part.sendPart(outputStream, currentPart, boundary);

		return writeToTarget(target, outputStream);
	}

	private long handleMultiPart(final WritableByteChannel target,
			final Part currentPart) throws IOException {

		currentPart.setPartBoundary(boundary);

		if (currentPart.getClass().equals(StringPart.class)) {
			return handleStringPart(target, (StringPart) currentPart);
		} else if (currentPart.getClass().equals(FilePart.class)) {
			final FilePart filePart = (FilePart) currentPart;

			return handleFilePart(target, filePart);
		}
		return 0;
	}

	private long writeToTarget(final WritableByteChannel target,
			final ByteArrayOutputStream byteWriter) throws IOException {

		int written = 0;
		int maxSpin = 0;
		synchronized (byteWriter) {
			final ByteBuffer message = ByteBuffer
					.wrap(byteWriter.toByteArray());
			while ((target.isOpen()) && (written < byteWriter.size())) {
				final long nWrite = target.write(message);
				written += nWrite;
				if (nWrite == 0 && maxSpin++ < 10) {

					try {
						byteWriter.wait(1000);
					} catch (final InterruptedException e) {

					}
				} else {
					if (maxSpin >= 10) {
						throw new IOException("Unable to write on channel "
								+ target);
					}
					maxSpin = 0;
				}
			}
		}
		return written;
	}

}
