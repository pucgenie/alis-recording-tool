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

import java.io.UnsupportedEncodingException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Line;

/**
 *
 * @author djart
 */
public class AlisSound {
    
    public static String getMixerDescriptionByID(int id) {
        String desc = null;
        Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
        
        desc = aInfos[id].getDescription();
        
        return desc;
    }
    
    public static int getMixerIDByName(String name) {
        int id = -1;
        Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
        
        for (int i = 0; i < aInfos.length; i++) {
            if (aInfos[i].getName().equals(name)) {
                id = i;
                break;
            }
        }
        
        return id;
    }
    
    public static int getMixerIDByName(String name, boolean bPlayback) {
        int id = -1;
        Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
        
        for (int i = 0; i < aInfos.length; i++) {
            if (aInfos[i].getName().equals(name) && mixerExists(i, bPlayback)) {
                id = i;
                break;
            }
        }
        
        return id;
    }
    
    public static int getMixerIDByOrder(int order, boolean bPlayback) {
        int id = -1;
        Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
        int total = -1;
        
        for (int i = 0; i < aInfos.length; i++) {
            Mixer mixer = AudioSystem.getMixer(aInfos[i]);
            Line.Info lineInfo = new Line.Info(bPlayback ?
                                               SourceDataLine.class :
                                               TargetDataLine.class);
            if (mixer.isLineSupported(lineInfo)) {
                total++;
                if (total == order) {
                    return i;
                }
            }
        }
        
        return id;
    }
    
    public static String getMixerName(int id) {
        String name = null;
        Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
        
        name = aInfos[id].getName();
        return name;
    }
    
    public static String getMixerName(int order, boolean bPlayback) {
        String name = null;
        Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
        int total = -1;
        
        for (int i = 0; i < aInfos.length; i++) {
            Mixer mixer = AudioSystem.getMixer(aInfos[i]);
            Line.Info lineInfo = new Line.Info(bPlayback ?
                                               SourceDataLine.class :
                                               TargetDataLine.class);
            if (mixer.isLineSupported(lineInfo)) {
                total++;
                if (total == order) {
                    name = aInfos[i].getName();
                    if (System.getProperty("user.language").equals("el")) { // fix for greek mixer names
                        byte[] b=null;
                        try {
                            b = name.getBytes("ISO-8859-1");
                            name = new String(b, "ISO-8859-7");
                        } catch (UnsupportedEncodingException ex) {
                            ex.printStackTrace();
                        }
                    }
                    
                    return name;
                }
            }
        }
        
        return name;
    }
    
    public static int getMixersNum() {
        Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
        
        return aInfos.length;
    }
    
    public static int getMixersNum(boolean bPlayback) {
        Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
        int total = 0;
        
        for (int i = 0; i < aInfos.length; i++) {
                Mixer mixer = AudioSystem.getMixer(aInfos[i]);
                Line.Info lineInfo = new Line.Info(bPlayback ?
                                                   SourceDataLine.class :
                                                   TargetDataLine.class);
                if (mixer.isLineSupported(lineInfo)) {
                    total++;
                }
        }
        
        return total;
    }
    
    public static boolean mixerExists(int id, boolean bPlayback) {
        boolean b = false;
        Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
        
        if (id >= 0 && id < aInfos.length) {
            Mixer mixer = AudioSystem.getMixer(aInfos[id]);
            Line.Info lineInfo = new Line.Info(bPlayback ?
                                               SourceDataLine.class :
                                               TargetDataLine.class);
            if (mixer.isLineSupported(lineInfo)) {
                b = true;
            }
        }
        
        return b;
    }

}
