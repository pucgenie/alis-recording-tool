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

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author djart
 */
public class AlisFilenameFilter implements FilenameFilter {
    private String filter;
    
    /**
     * Creates a new instance of AlisFilenameFilter
     */
    public AlisFilenameFilter(String filter) {
        this.filter = filter;
    }
    
    public boolean accept(File dir, String name) {
        return name.startsWith(filter);
    }
    
}
