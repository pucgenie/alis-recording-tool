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

/**
 *
 * @author djart
 */
public class AlisCheckBox extends javax.swing.JCheckBox {
    
    private int value;

    public AlisCheckBox(int i) {
        value = i;
        
        this.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlisCheckBoxActionPerformed(evt);
            }
        });
    }
    
    private void AlisCheckBoxActionPerformed(java.awt.event.ActionEvent evt){
        System.out.println(value + ": action performed");
    }
    
    // The below code can be used to create and add checkboxes in a boxed 
    // JPanel.
    /*private void initCheckBoxes() {

        int totalcheckboxes = 15;
        
        AlisCheckBox[] checkboxArr = new AlisCheckBox[totalcheckboxes];
        
        for (int i=0; i<totalcheckboxes; i++) {
            checkboxArr[i] = new AlisCheckBox(i);
            checkboxArr[i].setText(i + ": Live [plughw:0,0]");
            checkboxArr[i].setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            checkboxArr[i].setMargin(new java.awt.Insets(0, 0, 0, 0));
            jPanel3.add(checkboxArr[i]);
            jPanel3.add(Box.createVerticalStrut(6));
        }
    }*/
    
}
