/*
 * Copyright (c) 2019, to individual contributors by the @author tags.
 * See the COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 *
 * This file is part of Waarp Project.
 *
 * All Waarp Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Waarp is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Waarp . If not, see
 * <http://www.gnu.org/licenses/>.
 */

package org.waarp.common.filemonitor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.waarp.common.filemonitor.FileMonitor.FileItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class FileMonitorTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFileMonitor() throws InterruptedException, IOException {
        File statusFile = new File("/tmp/status.txt");
        statusFile.delete();
        File stopFile = new File("/tmp/stop.txt");
        stopFile.delete();
        File directory = new File("/tmp/monitor");
        File fileTest = new File(directory, "test.txt");
        directory.mkdirs();
        fileTest.delete();
        final AtomicInteger countNew = new AtomicInteger();
        final AtomicInteger countDelete = new AtomicInteger();
        final AtomicInteger countCheck = new AtomicInteger();
        FileMonitorCommandRunnableFuture commandValidFile = new FileMonitorCommandRunnableFuture() {
            @Override
            public void run(FileItem file) {
                System.out.println("File New: " + file.file.getAbsolutePath());
                countNew.incrementAndGet();
                setFileItem(file);
                finalize(true, 0);
            }
        };
        FileMonitorCommandRunnableFuture commandRemovedFile = new FileMonitorCommandRunnableFuture() {
            @Override
            public void run(FileItem file) {
                System.err.println("File Del: " + file.file.getAbsolutePath());
                setFileItem(file);
                countDelete.incrementAndGet();
            }
        };
        FileMonitorCommandRunnableFuture commandCheckIteration = new FileMonitorCommandRunnableFuture() {
            @Override
            public void run(FileItem unused) {
                System.err.println("Check done");
                countCheck.incrementAndGet();
            }
        };
        FileMonitor fileMonitor = new FileMonitor("testDaemon", statusFile, stopFile, directory, null, 100, null, false,
                                                  commandValidFile, commandRemovedFile, commandCheckIteration);
        commandValidFile.setMonitor(fileMonitor);

        fileMonitor.start();
        Thread.sleep(500);
        System.err.println("Create file: " + fileTest.getAbsolutePath());
        FileWriter fileWriterBig = new FileWriter(fileTest);
        for (int i = 0; i < 100; i++) {
            fileWriterBig.write("a");
        }
        fileWriterBig.flush();
        fileWriterBig.close();
        Thread.sleep(1000);

        System.err.println("Delete file: " + fileTest.getAbsolutePath());
        fileTest.delete();
        Thread.sleep(500);

        System.err.println("Create stopFile: " + stopFile.getAbsolutePath());
        fileWriterBig = new FileWriter(stopFile);
        fileWriterBig.write('a');
        fileWriterBig.flush();
        fileWriterBig.close();
        Thread.sleep(500);

        fileMonitor.waitForStopFile();
        assertTrue("Should be > 0", countCheck.get() > 0);
        assertTrue("Should be > 0", countDelete.get() > 0);
        assertTrue("Should be > 0", countNew.get() > 0);
        System.out.println(fileMonitor.getStatus());
    }

}
