package utils;

import java.util.*;
import java.io.*;

/**
 * Utility class that writes an Arduino .ino sketch from collected data.
 * Expected inputs:
 *  - pins: a Set containing Integer (or parsable) pin numbers
 *  - loopBody: the body of the loop() as a String (already contains proper indentation and newlines)
 *  - outFilename: the path to the .ino output file
 */
public class ArduinoSketchEmitter {

    public static void writeSketch(Set pins, String loopBody, String outFilename) {
        if (outFilename == null || outFilename.trim().isEmpty()) {
            outFilename = "generated_sketch.ino";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("// Código gerado pela DSL Arduino (português)\n");
        sb.append("#include <Arduino.h>\n\n");
        sb.append("void setup() {\n");

        // write pinMode lines for each pin
        for (Object pObj : pins) {
            int p;
            if (pObj instanceof Integer) p = ((Integer)pObj).intValue();
            else p = Integer.parseInt(pObj.toString());
            sb.append("  pinMode(" + p + ", OUTPUT);\n");
        }

        sb.append("}\n\n");
        sb.append("void loop() {\n");
        sb.append(loopBody);
        sb.append("  // repetir com pequeno atraso\n");
        sb.append("  delay(100);\n");
        sb.append("}\n");

        // attempt to write the file
        try (FileWriter fw = new FileWriter(outFilename)) {
            fw.write(sb.toString());
            System.out.println("Sketch escrito em: " + outFilename);
        } catch (IOException e) {
            System.err.println("Erro ao escrever sketch: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
