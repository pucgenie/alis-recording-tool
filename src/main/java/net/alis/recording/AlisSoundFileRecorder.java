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
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
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
public class AlisSoundFileRecorder extends Thread {
    private TargetDataLine m_line;
    private AudioFileFormat.Type m_targetType;
    private AudioInputStream m_audioInputStream;
    private File m_outputFile;
        
    private static final AudioFormat.Encoding MPEG1L3 = new AudioFormat.Encoding("MPEG1L3");
    private static final AudioFileFormat.Type MP3 = new AudioFileFormat.Type("MP3", "mp3");
    private static final AudioFileFormat.Type WAV = AudioFileFormat.Type.WAVE;
    private static final String mp3extension = ".mp3";
    private static final String wavextension = ".wav";
    
    public AlisSoundFileRecorder(File recordpath, int lineID, String lineLang, String recordType) throws LineUnavailableException {
        String fileextension = null;
        AudioFormat	audioFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                48000.0F, 16, 1, 2, 48000.0F, false);
        
        DataLine.Info	info = new DataLine.Info(TargetDataLine.class, audioFormat);
        TargetDataLine	targetDataLine = null;
        
        Mixer.Info[] aInfos = AudioSystem.getMixerInfo();
        Mixer.Info mixerInfo = aInfos[lineID];
        Mixer mixer = AudioSystem.getMixer(mixerInfo);

        targetDataLine = (TargetDataLine) mixer.getLine(info);
        targetDataLine.open(audioFormat);

        m_line = targetDataLine;
        m_audioInputStream = new AudioInputStream(targetDataLine);
        
        if (recordType.equals("mp3")) {
            m_targetType = MP3;
            fileextension = mp3extension;
        }
        else if (recordType.equals("wav")) {
            m_targetType = WAV;
            fileextension = wavextension;
        }
        else {
            m_targetType = MP3;
            fileextension = mp3extension;
        }
        
        String filename = null;
        if ( lineLang.length() > 0 ) {
            filename = lineID + "-" + lineLang;
        }
        else {
            filename = lineID + "-" + "line";
        }
        m_outputFile = new File(recordpath, filename + fileextension);
    }
    
    public void start() {
        m_line.start();
        super.start();
    }

    public void stopRecording() {
        m_line.stop();
        m_line.close();
    }
    
    public void run() {
        if (m_targetType == MP3) {
            AudioInputStream ais = null;
            try {
                ais = getConvertedStream(m_audioInputStream, MPEG1L3);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                AudioSystem.write(ais, m_targetType, m_outputFile);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (m_targetType == WAV) {
            try {
                AudioSystem.write(m_audioInputStream, m_targetType, m_outputFile);
            }
            catch (IOException e) { 
                e.printStackTrace(); 
            }
        }
        
            
    }
    
    public static AudioInputStream getConvertedStream(
        AudioInputStream sourceStream,
        AudioFormat.Encoding targetEncoding)
                throws UnsupportedOperationException {
        AudioFormat sourceFormat = sourceStream.getFormat();

        // construct a converted stream
        AudioInputStream targetStream = null;
        if (!AudioSystem.isConversionSupported(targetEncoding, sourceFormat)) {
            AudioFormat intermediateFormat = new AudioFormat(
                                                 AudioFormat.Encoding.PCM_SIGNED,
                                                 sourceFormat.getSampleRate(),
                                                 16,
                                                 sourceFormat.getChannels(),
                                                 2 * sourceFormat.getChannels(), // frameSize
                                                 sourceFormat.getSampleRate(),
                                                 false);
            if (AudioSystem.isConversionSupported(intermediateFormat, sourceFormat)) {
                // intermediate conversion is supported
                sourceStream = AudioSystem.getAudioInputStream(intermediateFormat, sourceStream);
            }
        }
        targetStream = AudioSystem.getAudioInputStream(targetEncoding, sourceStream);
        if (targetStream == null) {
            throw new UnsupportedOperationException("conversion not supported");
        }
        return targetStream;
    }
    
}
