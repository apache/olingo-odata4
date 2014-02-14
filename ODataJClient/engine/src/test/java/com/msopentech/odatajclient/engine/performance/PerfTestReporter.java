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
package com.msopentech.odatajclient.engine.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public final class PerfTestReporter {

    private static final String XLS = "odatajclient-perf.xls";

    public static void main(final String[] args) throws Exception {
        // 1. check input directory
        final File reportdir = new File(args[0] + File.separator + "target" + File.separator + "surefire-reports");
        if (!reportdir.isDirectory()) {
            throw new IllegalArgumentException("Expected directory, got " + args[0]);
        }

        // 2. read test data from surefire output
        final File[] xmlReports = reportdir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith("-output.txt");
            }
        });

        final Map<String, Map<String, Double>> testData = new TreeMap<String, Map<String, Double>>();

        for (File xmlReport : xmlReports) {
            final BufferedReader reportReader = new BufferedReader(new FileReader(xmlReport));
            try {
                while (reportReader.ready()) {
                    String line = reportReader.readLine();
                    final String[] parts = line.substring(0, line.indexOf(':')).split("\\.");

                    final String testClass = parts[0];
                    if (!testData.containsKey(testClass)) {
                        testData.put(testClass, new TreeMap<String, Double>());
                    }

                    line = reportReader.readLine();

                    testData.get(testClass).put(
                            parts[1], Double.valueOf(line.substring(line.indexOf(':') + 2, line.indexOf('['))));
                }
            } finally {
                IOUtils.closeQuietly(reportReader);
            }
        }

        // 3. build XSLX output (from template)
        final HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(
                args[0] + File.separator + "src" + File.separator + "test" + File.separator + "resources"
                + File.separator + XLS));


        for (Map.Entry<String, Map<String, Double>> entry : testData.entrySet()) {
            final Sheet sheet = workbook.getSheet(entry.getKey());

            int rows = 0;

            for (Map.Entry<String, Double> subentry : entry.getValue().entrySet()) {
                final Row row = sheet.createRow(rows++);

                Cell cell = row.createCell(0);
                cell.setCellValue(subentry.getKey());

                cell = row.createCell(1);
                cell.setCellValue(subentry.getValue());
            }
        }

        final FileOutputStream out = new FileOutputStream(args[0] + File.separator + "target" + File.separator + XLS);
        try {
            workbook.write(out);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
