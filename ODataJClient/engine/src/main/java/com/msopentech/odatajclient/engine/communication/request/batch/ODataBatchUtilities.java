/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.communication.request.batch;

import com.msopentech.odatajclient.engine.communication.header.ODataHeaders;
import com.msopentech.odatajclient.engine.communication.request.ODataStreamer;
import com.msopentech.odatajclient.engine.utils.ODataBatchConstants;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for batch requests and responses.
 */
public class ODataBatchUtilities {

    public static enum BatchItemType {

        NONE,
        CHANGESET,
        RETRIEVE

    }

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ODataBatchUtilities.class);

    /**
     * Response line syntax.
     */
    private static final Pattern RESPONSE_PATTERN =
            Pattern.compile("HTTP/\\d\\.\\d (\\d+) (.*)", Pattern.CASE_INSENSITIVE);

    /**
     * Reads batch part taking source and delimiter (boundary) from given batch controller.
     * <p>
     * Usually used to consume/discard useless lines.
     *
     * @param batchController batch controller.
     * @param checkCurrent if 'TRUE' the current line will be included into the delimiter verification.
     * @return latest read line.
     */
    public static String readBatchPart(final ODataBatchController batchController, final boolean checkCurrent) {
        return readBatchPart(batchController, null, -1, checkCurrent);
    }

    /**
     * Reads the given number of line from the given batch wrapped into the batch controller.
     * <p>
     * Usually used to consume/discard useless lines.
     *
     * @param batchController batch controller.
     * @param count number of batch line to be read.
     * @return latest read line.
     */
    public static String readBatchPart(final ODataBatchController batchController, final int count) {
        return readBatchPart(batchController, null, count, true);
    }

    /**
     * Reads batch part taking source and delimiter (boundary) from given batch controller.
     * <p>
     * Usually used to read an entire batch part.
     *
     * @param batchController batch controller.
     * @param os destination stream of batch part (null to discard).
     * @param checkCurrent if 'TRUE' the current line will be included into the delimiter verification.
     * @return latest read line.
     */
    public static String readBatchPart(
            final ODataBatchController controller, final OutputStream os, final boolean checkCurrent) {

        return readBatchPart(controller, os, -1, checkCurrent);
    }

    /**
     * Reads batch part taking source and delimiter (boundary) from given batch controller.
     * <p>
     * Usually used to read an entire batch part.
     *
     * @param batchController batch controller.
     * @param os destination stream of batch part (null to discard).
     * @param count number of batch line to be read.
     * @param checkCurrent if 'TRUE' the current line will be included into the delimiter verification.
     * @return latest read line.
     */
    public static String readBatchPart(
            final ODataBatchController controller, final OutputStream os, final int count, final boolean checkCurrent) {

        String currentLine;

        synchronized (controller.getBatchLineIterator()) {
            currentLine = checkCurrent ? controller.getBatchLineIterator().getCurrent() : null;

            if (count < 0) {
                try {

                    boolean notEndLine = isNotEndLine(controller, currentLine);

                    while (controller.isValidBatch() && notEndLine && controller.getBatchLineIterator().hasNext()) {

                        currentLine = controller.getBatchLineIterator().nextLine();
                        LOG.debug("Read line '{}' (end-line '{}')", currentLine, controller.getBoundary());

                        notEndLine = isNotEndLine(controller, currentLine);

                        if (notEndLine && os != null) {
                            os.write(currentLine.getBytes(ODataConstants.UTF8));
                            os.write(ODataStreamer.CRLF);
                        }
                    }

                } catch (IOException e) {
                    LOG.error("Error reading batch part", e);
                    throw new IllegalStateException(e);
                }

            } else {
                for (int i = 0;
                        controller.isValidBatch() && controller.getBatchLineIterator().hasNext() && i < count; i++) {
                    currentLine = controller.getBatchLineIterator().nextLine();
                }
            }
        }

        return currentLine;
    }

    /**
     * Reads headers from the batch starting from the given position.
     *
     * @param iterator batch iterator.
     * @return Map of header name in header values.
     */
    public static Map<String, Collection<String>> readHeaders(final ODataBatchLineIterator iterator) {
        final Map<String, Collection<String>> target =
                new TreeMap<String, Collection<String>>(String.CASE_INSENSITIVE_ORDER);

        readHeaders(iterator, target);
        return target;
    }

    /**
     * Reads headers from the batch starting from the given position.
     * <p>
     * Retrieved headers will be added to the map given by target parameter.
     *
     * @param iterator batch iterator.
     * @param target destination of the retrieved headers.
     */
    public static void readHeaders(
            final ODataBatchLineIterator iterator, final Map<String, Collection<String>> target) {

        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            readBatchPart(new ODataBatchController(iterator, null), baos, true);

            final LineIterator headers = IOUtils.lineIterator(new ByteArrayInputStream(baos.toByteArray()),
                    ODataConstants.UTF8);
            while (headers.hasNext()) {
                final String line = headers.nextLine().trim();
                if (StringUtils.isNotBlank(line)) {
                    addHeaderLine(line, target);
                }
            }
        } catch (Exception e) {
            LOG.error("Error retrieving headers", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Parses and adds the given header line to the given target map.
     *
     * @param headerLine header line to be added.
     * @param targetMap target map.
     */
    public static void addHeaderLine(final String headerLine, final Map<String, Collection<String>> targetMap) {
        final int sep = headerLine.indexOf(':');
        if (sep > 0 && sep < headerLine.length() - 1) {
            final String key = headerLine.substring(0, sep).trim();
            final Collection<String> value;
            if (targetMap.containsKey(key)) {
                value = targetMap.get(key);
            } else {
                value = new HashSet<String>();
                targetMap.put(key, value);
            }
            value.add(headerLine.substring(sep + 1, headerLine.length()).trim());
        }
    }

    /**
     * Retrieved batch boundary from the given content-type header values.
     *
     * @param contentType content-types.
     * @return batch boundary.
     */
    public static String getBoundaryFromHeader(final Collection<String> contentType) {
        final String boundaryKey = ODataBatchConstants.BOUNDARY + "=";

        if (contentType == null || contentType.isEmpty() || !contentType.toString().contains(boundaryKey)) {
            throw new IllegalArgumentException("Invalid content type");
        }

        final String headerValue = contentType.toString();

        final int start = headerValue.indexOf(boundaryKey) + boundaryKey.length();
        int end = headerValue.indexOf(';', start);

        if (end < 0) {
            end = headerValue.indexOf(']', start);
        }

        final String res = headerValue.substring(start, end);
        return res.startsWith("--") ? res : "--" + res;
    }

    /**
     * Retrieves response line from the given position.
     *
     * @param iterator batch iterator.
     * @return retrieved response line.
     */
    public static Map.Entry<Integer, String> readResponseLine(final ODataBatchLineIterator iterator) {
        final String line = readBatchPart(new ODataBatchController(iterator, null), 1);
        LOG.debug("Response line '{}'", line);

        final Matcher matcher = RESPONSE_PATTERN.matcher(line.trim());

        if (matcher.matches()) {
            return new AbstractMap.SimpleEntry<Integer, String>(Integer.valueOf(matcher.group(1)), matcher.group(2));
        }

        throw new IllegalArgumentException("Invalid response line '" + line + "'");
    }

    /**
     * Retrieves headers of the next batch item.
     *
     * @param iterator batch line iterator.
     * @param boundary batch boundary.
     * @return batch item headers.
     */
    public static Map<String, Collection<String>> nextItemHeaders(
            final ODataBatchLineIterator iterator, final String boundary) {

        final Map<String, Collection<String>> headers =
                new TreeMap<String, Collection<String>>(String.CASE_INSENSITIVE_ORDER);

        final String line = ODataBatchUtilities.readBatchPart(new ODataBatchController(iterator, boundary), true);

        if (line != null && line.trim().equals(boundary)) {
            ODataBatchUtilities.readHeaders(iterator, headers);
        }

        LOG.debug("Retrieved batch item headers {}", headers);
        return headers;
    }

    /**
     * Retrieves item type from item headers.
     *
     * @param headers batch item headers.
     * @return batch item type.
     */
    public static BatchItemType getItemType(final Map<String, Collection<String>> headers) {

        final BatchItemType nextItemType;

        final String contentType = headers.containsKey(ODataHeaders.HeaderName.contentType.toString())
                ? headers.get(ODataHeaders.HeaderName.contentType.toString()).toString() : StringUtils.EMPTY;

        if (contentType.contains(ODataBatchConstants.MULTIPART_CONTENT_TYPE)) {
            nextItemType = BatchItemType.CHANGESET;
        } else if (contentType.contains(ODataBatchConstants.ITEM_CONTENT_TYPE)) {
            nextItemType = BatchItemType.RETRIEVE;
        } else {
            nextItemType = BatchItemType.NONE;
        }

        LOG.debug("Retrieved next item type {}", nextItemType);
        return nextItemType;
    }

    /**
     * Checks if the given line is the expected end-line.
     *
     * @param controller batch controller.
     * @param line line to be checked.
     * @return 'TRUE' if the line is not the end-line; 'FALSE' otherwise.
     */
    private static boolean isNotEndLine(final ODataBatchController controller, final String line) {
        return line == null
                || (StringUtils.isBlank(controller.getBoundary()) && StringUtils.isNotBlank(line))
                || (StringUtils.isNotBlank(controller.getBoundary()) && !line.startsWith(controller.getBoundary()));
    }
}
