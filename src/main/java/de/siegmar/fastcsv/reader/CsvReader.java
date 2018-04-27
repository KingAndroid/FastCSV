/*
 * Copyright 2015 Oliver Siegmar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siegmar.fastcsv.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main class for reading CSV data.
 *
 * @author Oliver Siegmar
 */
public final class CsvReader {

    /**
     * Field separator character (default: ',' - comma).
     */
    private char fieldSeparator = ',';

    /**
     * Text delimiter character (default: '"' - double quotes).
     */
    private char textDelimiter = '"';

    /**
     * Read first line as header line? (default: false).
     */
    private boolean containsHeader;

    /**
     * Skip empty rows? (default: true)
     */
    private boolean skipEmptyRows = true;

    /**
     * Throw an exception if CSV data contains different field count? (default: false).
     */
    private boolean errorOnDifferentFieldCount;

    /**
     * Sets the field separator character (default: ',' - comma).
     */
    public void setFieldSeparator(final char fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    /**
     * Sets the text delimiter character (default: '"' - double quotes).
     */
    public void setTextDelimiter(final char textDelimiter) {
        this.textDelimiter = textDelimiter;
    }

    /**
     * Specifies if the first line should be the header (default: false).
     */
    public void setContainsHeader(final boolean containsHeader) {
        this.containsHeader = containsHeader;
    }

    /**
     * Specifies if empty rows should be skipped (default: true).
     */
    public void setSkipEmptyRows(final boolean skipEmptyRows) {
        this.skipEmptyRows = skipEmptyRows;
    }

    /**
     * Specifies if an exception should be thrown, if CSV data contains different field count
     * (default: false).
     */
    public void setErrorOnDifferentFieldCount(final boolean errorOnDifferentFieldCount) {
        this.errorOnDifferentFieldCount = errorOnDifferentFieldCount;
    }

    /**
     * Reads an entire file and returns a CsvContainer containing the data.
     *
     * @param file    the file to read data from.
     * @param charset the character set to use - must not be {@code null}.
     * @return the entire file's data - never {@code null}.
     * @throws IOException if an I/O error occurs.
     */
    public CsvContainer read(final File file, final Charset charset) throws IOException {
        try (final Reader reader = newPathReader(file, charset)) {
            return read(reader);
        }
    }

    /**
     * Reads from the provided reader until the end and returns a CsvContainer containing the data.
     * <p>
     * This library uses built-in buffering, so you do not need to pass in a buffered Reader
     * implementation such as {@link java.io.BufferedReader}.
     * Performance may be even likely better if you do not.
     *
     * @param reader the data source to read from.
     * @return the entire file's data - never {@code null}.
     * @throws IOException if an I/O error occurs.
     */
    public CsvContainer read(final Reader reader) throws IOException {
        final CsvParser csvParser = parse(reader);

        final List<CsvRow> rows = new ArrayList<>();
        CsvRow csvRow;
        while ((csvRow = csvParser.nextRow()) != null) {
            rows.add(csvRow);
        }

        if (rows.isEmpty()) {
            return null;
        }

        final List<String> header = containsHeader ? csvParser.getHeader() : null;
        return new CsvContainer(header, rows);
    }

    /**
     * Constructs a new {@link CsvParser} for the specified arguments.
     *
     * @param file    the file to read data from.
     * @param charset the character set to use - must not be {@code null}.
     * @return a new CsvParser - never {@code null}.
     * @throws IOException if an I/O error occurs.
     */
    public CsvParser parse(final File file, final Charset charset) throws IOException {
        return parse(newPathReader(file, charset));
    }

    public CsvParser parse(Reader reader) {
        return new CsvParser(
                reader,
                fieldSeparator,
                textDelimiter,
                containsHeader,
                skipEmptyRows,
                errorOnDifferentFieldCount
        );
    }

    private static Reader newPathReader(File file, final Charset charset) throws IOException {
        return new InputStreamReader(new FileInputStream(file), charset);
    }

}
