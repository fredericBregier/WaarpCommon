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

package org.waarp.common.tar;

import org.junit.Test;
import org.waarp.common.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ZipUtilityTest {

    @Test
    public void createZipFromDirectory() throws IOException {
        File dir = new File("/tmp/tests/dir");
        FileUtils.createDir(dir);
        File test = new File(dir, "test");
        test.createNewFile();
        File test2 = new File(dir, "test2");
        test2.createNewFile();
        File zip = new File(dir.getParentFile(), "testzip.zip");
        assertTrue(ZipUtility.createZipFromDirectory(dir.getAbsolutePath(), zip.getAbsolutePath(), false));
        FileUtils.forceDeleteRecursiveDir(dir);

        List<String> list = ZipUtility.unZip(zip, dir);
        assertTrue(list.size() == 2);
        File[] arrayFile = FileUtils.getFiles(dir);
        assertTrue(arrayFile.length == 2);
        int cpt = 0;
        for (File file : arrayFile) {
            for (String name : list) {
                if (name.equals(file.getName())) {
                    cpt++;
                    break;
                }
            }
        }
        assertTrue(cpt == 2);
        FileUtils.forceDeleteRecursiveDir(dir);
        FileUtils.deleteDir(dir);
        FileUtils.delete(zip);
    }

    @Test
    public void createZipFromFiles() throws IOException {
        File dir = new File("/tmp/tests/dir");
        FileUtils.createDir(dir);
        File test = new File(dir, "test");
        test.createNewFile();
        File test2 = new File(dir, "test2");
        test2.createNewFile();
        File[] arrayFile = dir.listFiles();
        List<File> listFile = new ArrayList<File>();
        for (File file : arrayFile) {
            listFile.add(file);
        }
        File zip = new File(dir.getParentFile(), "testzip.zip");
        assertTrue(ZipUtility.createZipFromFiles(listFile, zip.getAbsolutePath()));
        FileUtils.forceDeleteRecursiveDir(dir);

        List<String> list = ZipUtility.unZip(zip, dir);
        assertTrue(list.size() == 2);
        arrayFile = FileUtils.getFiles(dir);
        assertTrue(arrayFile.length == 2);
        int cpt = 0;
        for (File file : arrayFile) {
            for (String name : list) {
                if (name.equals(file.getName())) {
                    cpt++;
                    break;
                }
            }
        }
        assertTrue(cpt == 2);
        FileUtils.delete(zip);
        assertTrue(ZipUtility.createZipFromFiles(arrayFile, zip.getAbsolutePath()));
        FileUtils.forceDeleteRecursiveDir(dir);

        list = ZipUtility.unZip(zip, dir);
        assertTrue(list.size() == 2);
        File[] arrayFiles2 = arrayFile = FileUtils.getFiles(dir);
        assertTrue(arrayFiles2.length == 2);
        cpt = 0;
        for (File file : arrayFile) {
            for (File file2 : arrayFiles2) {
                if (file2.getName().equals(file.getName())) {
                    cpt++;
                    break;
                }
            }
        }
        assertTrue(cpt == 2);

        FileUtils.forceDeleteRecursiveDir(dir);
        FileUtils.deleteDir(dir);
        FileUtils.delete(zip);
    }
}