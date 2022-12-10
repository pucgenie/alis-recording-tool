/*
 *   Copyright (c) 2006 by Thanos Kyritsis
 *
 *   This file is part of Alis Recording Tool
 *
 *   Alis Recording Tool is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 2 of the License.
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

import alisrecording.AlisCommons;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author djart
 */
public class AlisXMLHandler {
    private Document doc;
    
    private String recFolder;
    private String xmlFullpath;
    public String audit;
    public String seminar;
    public String language;
    public String speaker;
    public String comments;
    public String keywds;
    public String startTime;
    public String stopTime;
    
    public AlisXMLHandler(String recFolder) {
        this.recFolder = recFolder;
        String recordpath = AlisCommons.getRecordPath();
        xmlFullpath = recordpath + recFolder + 
                AlisCommons.pathseparator + AlisCommons.currentMetadata;
        
        File xmlfile = new File(xmlFullpath);
        
        
        boolean exists = xmlfile.exists();
        if (!exists) {
            try {
                xmlfile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            audit = "";
            seminar = "";
            language = "";
            speaker = "";
            comments = "";
            keywds = "";
            startTime = "";
            stopTime = "";
            xmlWriter();
        }
        
        DocumentBuilder builder = null;
        
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
        
        InputStream is = null;
        try {
            is = new FileInputStream(xmlfile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        
        try {
            doc = builder.parse(is);
            is.close();
            speaker = getElement("synedrio", "speaker");
            language = getElement("synedrio", "language");
            seminar = getElement("synedrio", "seminar");
            audit = getElement("synedrio", "auditorium");
            keywds = getElement("synedrio", "keywords");
            comments = getElement("synedrio", "comments");
            startTime = getElement("synedrio", "starttime");
            stopTime = getElement("synedrio", "stoptime");
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private String getElement(String... args) {
        NodeList nodes;
        Element element;
        
        nodes = doc.getElementsByTagName(args[0]);
        element = (Element) nodes.item(0);

        for (int i = 1; i<=args.length-1; i++) {
            nodes = element.getElementsByTagName(args[i]);
            element = (Element) nodes.item(0);
        }


        Node child = element.getFirstChild();
        String out = "";
        if ( child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child; 
            out = cd.getData();
        }
        
        return out;
    }
    
    public static void dtdWriter(String recFolder) {
        String dtdOut = 
                "<!ELEMENT metadata (synedrio+)>" + "\n" +
                "<!ELEMENT synedrio (speaker, language, seminar, auditorium, keywords, comments, " +
                "starttime, stoptime)>" + "\n" +
                "<!ELEMENT speaker (#PCDATA)>" + "\n" +
                "<!ELEMENT language (#PCDATA)>" + "\n" +
                "<!ELEMENT seminar (#PCDATA)>" + "\n" +
                "<!ELEMENT auditorium (#PCDATA)>" + "\n" +
                "<!ELEMENT keywords (#PCDATA)>" + "\n" +
                "<!ELEMENT comments (#PCDATA)>" + "\n" +
                "<!ELEMENT starttime (#PCDATA)>" + "\n" +
                "<!ELEMENT stoptime (#PCDATA)>" + "\n";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(
                    recFolder + AlisCommons.pathseparator + AlisCommons.dtdMetadata
                    ));
            out.write( dtdOut );
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void xmlWriter() {
        String xmlOut = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
                "<synedrio>" + "\n" +
                "   <speaker>" + speaker + "</speaker>" + "\n" +
                "   <language>" + language + "</language>" + "\n" +
                "   <seminar>" + seminar + "</seminar>" + "\n" +
                "   <auditorium>" + audit + "</auditorium>" + "\n" +
                "   <keywords>" + keywds + "</keywords>" + "\n" +
                "   <comments>" + comments + "</comments>" + "\n" +
                "   <starttime>" + startTime + "</starttime>" + "\n" +
                "   <stoptime>" + stopTime + "</stoptime>" + "\n" +
                "</synedrio>";
        try {
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(xmlFullpath), "UTF8"));
            out.write( xmlOut );
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static String escapeXML(String str) {
        str = AlisCommons.replaceStr(str, "&", "");
        str = AlisCommons.replaceStr(str, "<", "");
        str = AlisCommons.replaceStr(str, ">", "");
        return str;
    }
    
    public static String escapeXMLforFilename(String str) {
        str = AlisCommons.replaceStr(str, " ", "-");
        str = AlisCommons.replaceStr(str, "`", "");
        str = AlisCommons.replaceStr(str, "~", "");
        str = AlisCommons.replaceStr(str, "!", "");
        str = AlisCommons.replaceStr(str, "@", "");
        str = AlisCommons.replaceStr(str, "#", "");
        str = AlisCommons.replaceStr(str, "\\$", "");
        str = AlisCommons.replaceStr(str, "%", "");
        str = AlisCommons.replaceStr(str, "\\^", "");
        str = AlisCommons.replaceStr(str, "\\*", "");
        str = AlisCommons.replaceStr(str, "\\(", "");
        str = AlisCommons.replaceStr(str, "\\)", "");
        str = AlisCommons.replaceStr(str, "=", "");
        str = AlisCommons.replaceStr(str, "\\+", "");
        str = AlisCommons.replaceStr(str, "\\{", "");
        str = AlisCommons.replaceStr(str, "\\}", "");
        str = AlisCommons.replaceStr(str, "\\[", "");
        str = AlisCommons.replaceStr(str, "\\]", "");
        str = AlisCommons.replaceStr(str, ";", "");
        str = AlisCommons.replaceStr(str, ":", "");
        str = AlisCommons.replaceStr(str, "'", "");
        str = AlisCommons.replaceStr(str, "\"", "");
        str = AlisCommons.replaceStr(str, ",", "");
        str = AlisCommons.replaceStr(str, "\\.", "");
        str = AlisCommons.replaceStr(str, "/", "");
        str = AlisCommons.replaceStr(str, "\\?", "");
        str = AlisCommons.replaceStr(str, "\\|", "");
        str = AlisCommons.replaceStr(str, "\\\\", "");
        return str;
    }
    
}
