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

package net.alis.recording;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.alis.recording.AlisCommons;
import net.alis.recording.AlisPathHandler;

/**
 *
 * @author djart
 */
public class AlisProperties {
    
    public static Properties loadProps() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(AlisCommons.homePath +
                    AlisCommons.pathseparator + AlisCommons.propertiesDir + 
                    AlisCommons.pathseparator + AlisCommons.propertiesFile));
        } catch (FileNotFoundException ex) {
            boolean pathsuccess = (new File(AlisCommons.homePath +
                    AlisCommons.pathseparator + AlisCommons.propertiesDir)).mkdirs();
            File file = new File(AlisCommons.homePath +
                    AlisCommons.pathseparator + AlisCommons.propertiesDir + 
                    AlisCommons.pathseparator + AlisCommons.propertiesFile);
            try {
                boolean success = file.createNewFile();
            } catch (IOException ex2) {
                ex2.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return properties;
    }
    
    public static void saveProps(Properties properties) {
        try {
            properties.store(new FileOutputStream(AlisCommons.homePath +
                    AlisCommons.pathseparator + AlisCommons.propertiesDir + 
                    AlisCommons.pathseparator + AlisCommons.propertiesFile), null);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
