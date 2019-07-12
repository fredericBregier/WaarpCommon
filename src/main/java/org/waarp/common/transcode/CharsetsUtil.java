/**
 * This file is part of Waarp Project.
 * <p>
 * Copyright 2009, Frederic Bregier, and individual contributors by the @author tags. See the COPYRIGHT.txt in the
 * distribution for a full listing of individual contributors.
 * <p>
 * All Waarp Project is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * Waarp is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Waarp .  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.waarp.common.transcode;

import org.waarp.common.logging.WaarpLogger;
import org.waarp.common.logging.WaarpLoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

/**
 * Helper to print in output the Charsets available in the JVM.<br>
 * <br>
 * -html will output HTML format<br> -text (default) will output TEXT format<br> -csv will output CSV (comma separated)
 * format<br>
 * <br>
 * Allow also to transcode one file to another: all arguments mandatory<br> -from filename charset<br> -to filename
 * charset<br>
 *
 * @author Frederic Bregier
 */
public class CharsetsUtil {
    /**
     * Internal Logger
     */
    private static final WaarpLogger logger = WaarpLoggerFactory
            .getLogger(CharsetsUtil.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        int format = 1; // TEXT
        boolean transcode = false;
        String fromFilename = null;
        String fromCharset = null;
        String toFilename = null;
        String toCharset = null;
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-html")) {
                    format = 0;
                } else if (args[i].equalsIgnoreCase("-text")) {
                    format = 1;
                } else if (args[i].equalsIgnoreCase("-csv")) {
                    format = 2;
                } else if (args[i].equalsIgnoreCase("-to")) {
                    i++;
                    toFilename = args[i];
                    i++;
                    toCharset = args[i];
                } else if (args[i].equalsIgnoreCase("-from")) {
                    i++;
                    fromFilename = args[i];
                    i++;
                    fromCharset = args[i];
                }
            }
            transcode = (toCharset != null && toFilename != null && fromCharset != null && fromFilename != null);
        }
        if (transcode) {
            boolean status = transcode(fromFilename, fromCharset, toFilename, toCharset, 16384);
            System.out.println("Transcode: " + status);
        } else {
            printOutCharsetsAvailable(format);
        }
    }

    /**
     * @param format 0 = html, 1 = text, 2 = csv
     */
    public static void printOutCharsetsAvailable(int format) {
        SortedMap<String, Charset> map = Charset.availableCharsets();
        Set<Entry<String, Charset>> set = map.entrySet();
        switch (format) {
        case 0:
            System.out
                    .println(
                            "<html><body><table border=1><tr><th>Name</th><th>CanEncode</th><th>IANA Registered</th><th>Aliases</th></tr>");
            break;
        case 1:
            System.out.println("Name\tCanEncode\tIANA Registered\tAliases");
            break;
        case 2:
            System.out.println("Name,CanEncode,IANA Registered,Aliases");
            break;
        }
        for (Entry<String, Charset> entry : set) {
            Charset charset = entry.getValue();
            String aliases = null;
            switch (format) {
            case 0:
                aliases = "<ul>";
                break;
            case 1:
                aliases = "[ ";
                break;
            case 2:
                aliases = "[ ";
                break;
            }
            Set<String> aliasCharset = charset.aliases();
            for (String string : aliasCharset) {
                switch (format) {
                case 0:
                    aliases += "<li>" + string + "</li>";
                    break;
                case 1:
                    aliases += string + " ";
                    break;
                case 2:
                    aliases += string + " ";
                    break;
                }
            }
            switch (format) {
            case 0:
                aliases += "</ul>";
                break;
            case 1:
                aliases += "]";
                break;
            case 2:
                aliases += "]";
                break;
            }
            switch (format) {
            case 0:
                System.out.println("<tr><td>" + entry.getKey() +
                                   "</td><td>" + charset.canEncode() +
                                   "</td><td>" + charset.isRegistered() +
                                   "</td><td>" + aliases + "</td>");
                break;
            case 1:
                System.out.println(entry.getKey() +
                                   "\t" + charset.canEncode() +
                                   "\t" + charset.isRegistered() +
                                   "\t" + aliases);
                break;
            case 2:
                System.out.println(entry.getKey() +
                                   "," + charset.canEncode() +
                                   "," + charset.isRegistered() +
                                   "," + aliases);
                break;
            }
        }
        switch (format) {
        case 0:
            System.out.println("</table></body></html>");
            break;
        case 1:
            break;
        case 2:
            break;
        }
    }

    /**
     * Method to transcode one file to another using 2 different charsets
     *
     * @param srcFilename
     * @param fromCharset
     * @param toFilename
     * @param toCharset
     * @param bufferSize
     *
     * @return True if OK, else False (will log the reason)
     */
    public static boolean transcode(String srcFilename, String fromCharset, String toFilename, String toCharset,
                                    int bufferSize) {
        boolean success = false;
        File from = new File(srcFilename);
        File to = new File(toFilename);
        FileInputStream fileInputStream = null;
        InputStreamReader reader = null;
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter writer = null;
        try {
            fileInputStream = new FileInputStream(from);
            reader = new InputStreamReader(fileInputStream, fromCharset);
            fileOutputStream = new FileOutputStream(to);
            writer = new OutputStreamWriter(fileOutputStream, toCharset);
            char[] cbuf = new char[bufferSize];
            int read = reader.read(cbuf);
            while (read > 0) {
                writer.write(cbuf, 0, read);
                read = reader.read(cbuf);
            }
            success = true;
        } catch (FileNotFoundException e) {
            logger.warn("File not found", e);
        } catch (UnsupportedEncodingException e) {
            logger.warn("Unsupported Encoding", e);
        } catch (IOException e) {
            logger.warn("File IOException", e);
        }
        try {
            if (reader != null) {
                reader.close();
            } else if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (IOException e) {
        }
        try {
            if (writer != null) {
                writer.flush();
                writer.close();
            } else if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (IOException e) {
        }
        return success;
    }

}
