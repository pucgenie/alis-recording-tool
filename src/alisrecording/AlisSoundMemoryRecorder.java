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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author djart
 */
public class AlisSoundMemoryRecorder implements Runnable {
    AudioInputStream audioInputStream;
    double duration, seconds;
    String errStr;

    TargetDataLine line;
    Thread thread;
    int mixerNum;
    
    public AlisSoundMemoryRecorder(int num) {
        mixerNum = num;
    }

    public void start() {
        errStr = null;
        thread = new Thread(this);
        thread.setName("Capture");
        thread.start();
    }

    public void stop() {
        thread = null;
    }

    private void shutDown(String message) {
        if ((errStr = message) != null && thread != null) {
            thread = null;
            System.err.println(errStr);
        }
    }
    
    private AudioFormat getFormat() {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 44100.0F;
        int sampleSize = 16;
        boolean bigEndian = false;
        int channels = 1;

        return new AudioFormat(encoding, rate, sampleSize, 
                      channels, (sampleSize/8)*channels, rate, bigEndian);
    }
    
    private Mixer.Info getMixerInfo(int mixernum) {
        Mixer.Info[] aInfos = AudioSystem.getMixerInfo();
        
        if (mixernum >= 0 && mixernum < aInfos.length) {
            return aInfos[mixernum];
        }
        
        return null;
    }

    public void run() {

        duration = 0;
        audioInputStream = null;

        AudioFormat format = getFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        
        if (mixerNum == -1) {
            if (!AudioSystem.isLineSupported(info)) {
                shutDown("Line matching " + info + " not supported.");
                return;
            }
            
            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format, line.getBufferSize());
            } catch (LineUnavailableException ex) { 
                shutDown("Unable to open the line: " + ex);
                return;
            } catch (SecurityException ex) { 
                shutDown(ex.toString());
                return;
            } catch (Exception ex) { 
                shutDown(ex.toString());
                return;
            }
        }
        else {
            Mixer.Info mixerInfo = getMixerInfo(mixerNum);
            if (mixerInfo == null) {
                shutDown("mixer not found: " + mixerNum);
                return;
            }
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            
            if(!mixer.isLineSupported(info)) {
                shutDown("Mixer matching" + info + " not supported.");
                return;
            }
            
            try {
                line = (TargetDataLine) mixer.getLine(info);
                line.open(format, line.getBufferSize());
            } catch (LineUnavailableException ex) { 
                shutDown("Unable to open the line: " + ex);
                return;
            } catch (SecurityException ex) { 
                shutDown(ex.toString());
                return;
            } catch (Exception ex) { 
                shutDown(ex.toString());
                return;
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead;

        line.start();

        while (thread != null) {
            if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                break;
            }
            out.write(data, 0, numBytesRead);
        }

        // we reached the end of the stream.  stop and close the line.
        line.stop();
        line.close();
        line = null;

        // stop and close the output stream
        try {
            out.flush();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // load bytes into the audio input stream
        byte audioBytes[] = out.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

        long milliseconds = (long)((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
        duration = milliseconds / 1000.0;

        try {
            audioInputStream.reset();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            return;
        }
    }

}
