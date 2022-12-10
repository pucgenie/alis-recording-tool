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

import java.io.*;
import javax.sound.sampled.*;

public class MyMp3Recorder {

    FormatControls formatControls = new FormatControls();
    Capture capture = new Capture();
    AudioInputStream audioInputStream;
    String errStr;
    double duration, seconds;
    String fileName = "untitled";
    
    private static boolean DEBUG = false;
    private static boolean dumpExceptions=false;
    private static boolean traceConverters=false;
    private static boolean quiet=false;
    
    private static final AudioFormat.Encoding	MPEG1L3 = new AudioFormat.Encoding("MPEG1L3");
    private static final AudioFileFormat.Type	MP3 = new AudioFileFormat.Type("MP3", "mp3");

    public void open() {
        
        System.out.println("Press ENTER to start the recording.");
        try { System.in.read(); }
        //System.getProperties().getProperty("line.seperator");
        catch (IOException e) { e.printStackTrace();}

        capture.start();
        System.out.println("Recording...");

        System.out.println("Press ENTER to stop the recording.");
        try { System.in.read(); }
        catch (IOException e) { e.printStackTrace();}
        capture.stop();
        System.out.println("Recording stopped.");

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

    public void saveToFile(String name, AudioFileFormat.Type fileType) {

        if (audioInputStream == null) {
            System.out.println("No loaded audio to save");
            return;
        }

        // reset to the beginnning of the captured data
        try {
            audioInputStream.reset();
        } catch (Exception e) { 
            System.out.println("Unable to reset stream " + e);
            return;
        }

        File file = new File(fileName = name);
        try {
            if (AudioSystem.write(audioInputStream, fileType, file) == -1) {
                throw new IOException("Problems writing to file");
            }
        } catch (Exception ex) { System.out.println(ex.toString()); }
    }
    
    public void saveToMp3File(String name) {

        if (audioInputStream == null) {
            System.out.println("No loaded audio to save");
            return;
        }

        // reset to the beginnning of the captured data
        try {
            audioInputStream.reset();
        } catch (Exception e) { 
            System.out.println("Unable to reset stream " + e);
            return;
        }
        
        AudioFileFormat.Type targetType = MP3;
        AudioInputStream ais = null;
        try {
            ais = getConvertedStream(audioInputStream, MPEG1L3);
        } catch (Throwable t) {
            if (dumpExceptions) {
                t.printStackTrace();
            } else if (!quiet) {
                System.out.println("Error: " + t.getMessage());
            }
        }

        File file = new File(fileName = name);
        try {
            if (AudioSystem.write(ais, targetType, file) == -1) {
                throw new IOException("Problems writing to file");
            }
        } catch (Exception ex) { System.out.println(ex.toString()); }
    }

    /** 
     * Reads data from the input channel and writes to the output stream
     */
    class Capture implements Runnable {

        TargetDataLine line;
        Thread thread;

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

        public void run() {

            duration = 0;
            audioInputStream = null;
            
            // define the required attributes for our line, 
            // and make sure a compatible line is supported.

            AudioFormat format = formatControls.getFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, 
                format);
                        
            if (!AudioSystem.isLineSupported(info)) {
                shutDown("Line matching " + info + " not supported.");
                return;
            }

            // get and open the target data line for capture.

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

            // play back the captured audio data
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead;
            
            line.start();

            //int i=0;
            //while (i <= 100) {
            while (thread != null) {
                if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                    break;
                }
                out.write(data, 0, numBytesRead);
                //i++;
                //System.out.println(i);
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

            // load bytes into the audio input stream for playback

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
            System.out.println("all should be ok!");
            //saveToFile("lala.wav", AudioFileFormat.Type.WAVE);
            saveToMp3File("lala.mp3" );
        }
    } // End class Capture
 

    /**
     * Controls for the AudioFormat.
     */
    class FormatControls {

        public AudioFormat getFormat() {

            AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
            float rate = 44100.0F;
            int sampleSize = 16;
            boolean bigEndian = false;
            int channels = 1;
            
            //System.setProperty("tritonus.lame.vbr", "false");
            //System.setProperty("tritonus.lame.bitrate", "112");

            return new AudioFormat(encoding, rate, sampleSize, 
                          channels, (sampleSize/8)*channels, rate, bigEndian);
        }

    } // End class FormatControls

    public static void main(String s[]) {
        MyMp3Recorder mymp3recorder = new MyMp3Recorder();
        mymp3recorder.open();
    }

}