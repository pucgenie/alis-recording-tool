/*
 *   Copyright (c) 2006 by Thanos Kyritsis
 *
 *   This file is part of Alis Recording Tool
 *
 *   Alis Recording Tool is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, version 2 of the License.
 *
 *   Alis Recording Tool is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Alis Recording Tool; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package alisrecording;

import alisrecording.AlisPathHandler;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author djart
 */
public class AlisCommons {
    
    public static final String scriptsPath = "scripts";
    public static final String libPath = "lib";
    public static final String mainPath = AlisPathHandler.getPath(libPath);
    public static final String homePath = System.getProperty("user.home");
    public static final String pathseparator = System.getProperty("file.separator");
    
    public static final String recPathProperty = "rec.path";
    public static final String propertiesDir = ".alisrectool";
    public static final String propertiesFile = "properties.conf";
    public static final String recScriptStart = "alis-start-record.sh";
    public static final String recScriptStop = "alis-stop-record.sh";
    public static final String cardScriptCount = "alis-count-cards.sh";
    public static final String cardScriptList = "alis-list-cards.sh";
    public static final String cardScriptTest = "alis-test-card.sh";
    
    public static final String recTempPrefix = "alisrec_temp_";
    public static final String recDirPrefix = "alisrec_";
    
    public static final String currentMetadata = "alis.xml";
    public static final String dtdMetadata = "alice.dtd";
    public static final String dateFormat = "yyyyMMdd-HHmmss";
    
    public static final String recordMethod = "record.method";
    public static final String recordMethodAlsa = "ALSA System (Linux only)";
    public static final String recordMethodJava = "Java Sound API (OS Portable)";
    public static final String recordMethodAlsaTab = "ALSA Properties";
    public static final String recordMethodJavaTab = "Java Sound Properties";
    public static final String recordWaveProperty = "record.in.wave";
    
    public static final String cardsNumber = "cards.number";
    public static final String cardIDProperty =".id";
    public static final String cardLangProperty = ".lang";
    
    public static final String linesNumber = "lines.number";
    public static final String lineIDProperty = ".id";
    public static final String lineLangProperty = ".lang";
    public static final String lineEnabled = ".enabled";
    
    public static final String Version = "0.7.0";
    
    public static String replaceStr(String inputStr, String patternStr, String replacementStr) {
        // Compile regular expression
        Pattern pattern = Pattern.compile(patternStr);
        
        // Replace all occurrences of pattern in input
        Matcher matcher = pattern.matcher(inputStr);
        String outputStr = matcher.replaceAll(replacementStr);
        return outputStr;
    }
    
    public static String getRecordPath() {
        String recordpath = AlisProperties.loadProps().getProperty(AlisCommons.recPathProperty, "");
        
        if (!recordpath.equals("")) {
            if (!recordpath.endsWith(AlisCommons.pathseparator)) {
                recordpath = recordpath + AlisCommons.pathseparator;
            }
        }
        
        return recordpath;
    }
    
    public static int checkRecordPath() {
        String recordpath = getRecordPath();
        int err = 0;
        
        if (recordpath.equals("")) {
            err = 1;
            return err;
        }
        
        boolean exists = (new File(recordpath)).exists();
        if (!exists) {
            err = 2;
            return err;
        }
        
        boolean writable = (new File(recordpath)).canWrite();
        if (!writable) {
            err = 3;
            return err;
        }
        
        return err;
    }
    
    public static String formatSeconds(int seconds) {
        NumberFormat formatter = new DecimalFormat("00");
        int hours = 0;
        int minutes = 0;
        
        minutes = seconds / 60;
        seconds %= 60;
        hours = minutes / 60;
        minutes %= 60;
        
        String Shours = formatter.format(hours);
        String Sminutes = formatter.format(minutes);
        String Sseconds = formatter.format(seconds);
        
        String formated = Shours + ":" + Sminutes + ":" + Sseconds;
        return formated;
    }
}
