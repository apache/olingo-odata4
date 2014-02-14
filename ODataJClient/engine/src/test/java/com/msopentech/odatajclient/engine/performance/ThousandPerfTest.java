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

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class ThousandPerfTest extends BasicPerfTest {

    @Override
    public void readAtomViaLowerlevelLibs() throws ParserConfigurationException, SAXException, IOException {
        for (int i = 0; i < 1000; i++) {
            super.readAtomViaLowerlevelLibs();
        }
    }

    @Override
    public void readAtomViaOdataJClient() {
        for (int i = 0; i < 1000; i++) {
            super.readAtomViaOdataJClient();
        }
    }

    @Override
    public void writeAtomViaLowerlevelLibs()
            throws ParserConfigurationException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {

        for (int i = 0; i < 1000; i++) {
            super.writeAtomViaLowerlevelLibs();
        }
    }

    @Override
    public void writeAtomViaOdataJClient() throws IOException {
        for (int i = 0; i < 1000; i++) {
            super.writeAtomViaOdataJClient();
        }
    }

    @Override
    public void readJSONViaLowerlevelLibs() throws IOException {
        for (int i = 0; i < 1000; i++) {
            super.readJSONViaLowerlevelLibs();
        }
    }

    @Override
    public void readJSONViaOdataJClient() {
        for (int i = 0; i < 1000; i++) {
            super.readJSONViaOdataJClient();
        }
    }

    @Override
    public void writeJSONViaLowerlevelLibs() throws IOException {
        for (int i = 0; i < 1000; i++) {
            super.writeJSONViaLowerlevelLibs();
        }
    }

    @Override
    public void writeJSONViaOdataJClient() throws IOException {
        for (int i = 0; i < 1000; i++) {
            super.writeJSONViaOdataJClient();
        }
    }
}
