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

import java.io.IOException;
import java.io.File;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Line;



public class SimpleMp3Recorder extends Thread
{
    private TargetDataLine		m_line;
    private AudioFileFormat.Type	m_targetType;
    private AudioInputStream	m_audioInputStream;
    private File			m_outputFile;
        
    private static boolean DEBUG = false;
    private static boolean dumpExceptions=false;
    private static boolean quiet=false;
        
    private static final AudioFormat.Encoding MPEG1L3 = new AudioFormat.Encoding("MPEG1L3");
    private static final AudioFileFormat.Type MP3 = new AudioFileFormat.Type("MP3", "mp3");

    public SimpleMp3Recorder(TargetDataLine line,
                             AudioFileFormat.Type targetType,
                             File file) {
        m_line = line;
        m_audioInputStream = new AudioInputStream(line);
        m_targetType = targetType;
        m_outputFile = file;
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
        AudioInputStream ais = null;
        try {
            ais = getConvertedStream(m_audioInputStream, MPEG1L3);
        } catch (Throwable t) {
            if (dumpExceptions) {
                t.printStackTrace();
            } else if (!quiet) {
                System.out.println("Error: " + t.getMessage());
            }
        }

        try {
            AudioSystem.write(ais, m_targetType, m_outputFile);
        }
        catch (IOException e) {
                e.printStackTrace();
        }
    }

    public static AudioInputStream getConvertedStream(
        AudioInputStream sourceStream,
        AudioFormat.Encoding targetEncoding)
                throws Exception {
        AudioFormat sourceFormat = sourceStream.getFormat();
        if (!quiet) {
            System.out.println("Input format: " + sourceFormat);
        }

        // construct a converted stream
        AudioInputStream targetStream = null;
        if (!AudioSystem.isConversionSupported(targetEncoding, sourceFormat)) {
            if (DEBUG && !quiet) {
                System.out.println("Direct conversion not possible.");
                System.out.println("Trying with intermediate PCM format.");
            }
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
            throw new Exception("conversion not supported");
        }
        if (!quiet) {
            if (DEBUG) {
                System.out.println("Got converted AudioInputStream: " + targetStream.getClass().getName());
            }
            System.out.println("Output format: " + targetStream.getFormat());
        }
        return targetStream;
    }

    private static void printUsageAndExit() {
        out("SimpleMp3Recorder: usage:");
        out("\tjava SimpleMp3Recorder -h");
        out("\tjava SimpleMp3Recorder <mixer number> <audiofile>");
        System.exit(0);
    }

    private static void out(String strMessage) {
        System.out.println(strMessage);
    }
    
    public static void listMixers() {
        out("Available Mixers:");
        Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
        int total = aInfos.length - 1;
        out("Total Mixers: " + total);
        for (int i = 0; i < aInfos.length; i++) {
                out(i + ": " + aInfos[i].getName());
        }
        if (aInfos.length == 0) {
                out("[No mixers available]");
        }
    }
    
    public static void listMixers(boolean bPlayback) {
        out("Available Supported Mixers:");
        Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
        int total = aInfos.length - 1;
        out("Total Mixers: " + total);
        for (int i = 0; i < aInfos.length; i++) {
                Mixer mixer = AudioSystem.getMixer(aInfos[i]);
                Line.Info lineInfo = new Line.Info(bPlayback ?
                                                   SourceDataLine.class :
                                                   TargetDataLine.class);
                if (mixer.isLineSupported(lineInfo)) {
                        out(i + ": " + aInfos[i].getName());
                }
        }
        if (aInfos.length == 0) {
                out("[No mixers available]");
        }
    }
    
    public static Mixer.Info getMixerInfo(int mixernum) {
        Mixer.Info[] aInfos = AudioSystem.getMixerInfo();
        
        if (mixernum >= 0 && mixernum < aInfos.length) {
            return aInfos[mixernum];
        }
        
        return null;
    }
    
    public static void main(String[] args) {
        if (args.length != 2 || args[0].equals("-h")) {
                printUsageAndExit();
        }

        int     mixerNum = Integer.parseInt(args[0]);
        String	strFilename = args[1];
        File	outputFile = new File(strFilename);
        
        AudioFormat	audioFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                44100.0F, 16, 1, 2, 44100.0F, false);
        System.setProperty("tritonus.lame.bitrate", "112");

        DataLine.Info	info = new DataLine.Info(TargetDataLine.class, audioFormat);
        TargetDataLine	targetDataLine = null;
        if (mixerNum == -1) {
            try {
                targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
                targetDataLine.open(audioFormat);
            }
            catch (LineUnavailableException e) {
                out("unable to get a recording line");
                e.printStackTrace();
                System.exit(1);
            }
        }
        else {
            Mixer.Info mixerInfo = getMixerInfo(mixerNum);
            if (mixerInfo == null) {
                out("mixer not found: " + mixerNum);
                System.exit(1);
            }
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            try {
                targetDataLine = (TargetDataLine) mixer.getLine(info);
                targetDataLine.open(audioFormat);
            }
            catch (LineUnavailableException e) {
                out("unable to get a recording line");
                e.printStackTrace();
                System.exit(1);
            }
            
        }

        AudioFileFormat.Type targetType = MP3;

        SimpleMp3Recorder recorder = new SimpleMp3Recorder(
                targetDataLine,
                targetType,
                outputFile);
        
        recorder.listMixers(true);
        recorder.listMixers(false);
        recorder.listMixers();

        out("Press ENTER to start the recording.");
        try { System.in.read(); }
        catch (IOException e) { e.printStackTrace(); }
        
        recorder.start();
        out("Recording...");

        
        out("Press ENTER to stop the recording.");
        try { System.in.read(); }
        catch (IOException e) { e.printStackTrace(); }

        /*int i=0;
        while(i <= 100000) {
                System.out.println(i);
                i++;
        }*/

        recorder.stopRecording();
        out("Recording stopped.");
    }
}


