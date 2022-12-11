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
import java.util.StringTokenizer;

/**
 *
 * @author djart
 */
public class AlisPathHandler {
    
    /**
     *  This method tries to find a path containing locator
     */
    public static File getPath(String locator) {
        String classpath = System.getProperty("java.class.path");
        StringTokenizer tok = new StringTokenizer(classpath, System.getProperty("path.separator"));
        
        String pathseparator = System.getProperty("file.separator");
        
        while (tok.hasMoreTokens()) {
            String path = tok.nextToken();
            if (path.toLowerCase().endsWith(".jar") ) {
                int seppos = path.lastIndexOf(pathseparator);
                if (seppos>=0) path = path.substring(0, seppos);
                else path = ".";
            }
            
            if (!path.endsWith(pathseparator)) path = path + pathseparator;
            
            // try to find our locator
            final var file = new File(path + locator);
			boolean exists = file.exists();
            if (exists) {
                return file;
            }
            
        }
        
        // if everything else failed, try 'pwd' as the last resort
        boolean exists = (new File(System.getProperty("user.dir"), locator)).exists();
        if (exists) {
            return new File(System.getProperty("user.dir"));
        }
        
        return null;
    }
    
}
