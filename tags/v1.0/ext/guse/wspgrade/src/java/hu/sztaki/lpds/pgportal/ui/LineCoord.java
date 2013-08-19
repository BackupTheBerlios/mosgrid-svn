/* Copyright 2007-2011 MTA SZTAKI LPDS, Budapest

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
/*
 * LineCoord.java
 * Egy vonal koordinatai
 * (ket job portjait osszekoto vonal)
 */

package hu.sztaki.lpds.pgportal.ui;

/**
 * Egy vonal koordinatai
 * (ket job portjait osszekoto vonal)
 *
 * @author krisztian karoczkai
 */
public class LineCoord {
    
    String x0="0",x1="0",y0="0",y1="0";
    
    /**
     * Class constructor
     * @param pX0 X0 koordinata
     * @param pY0 Y0 koordinata
     * @param pX1 X1 koordinata
     * @param pY1 Y1 koordinata
     */
    public LineCoord(String pX0,String pY0, String pX1, String pY1) {
        x0=pX0;
        x1=pX1;
        y0=pY0;
        y1=pY1;
    }
    
    /**
     * X0 koordinata lekerdezese
     * @return X0 koordinata
     */
    public String getX0(){return x0;}
    
    /**
     * X1 koordinata lekerdezese
     * @return X1 koordinata
     */
    public String getX1(){return x1;}
    
    /**
     * Y0 koordinata lekerdezese
     * @return Y0 koordinata
     */
    public String getY0(){return y0;}
    
    /**
     * Y1 koordinata lekerdezese
     * @return Y1 koordinata
     */
    public String getY1(){return y1;}
    
}
