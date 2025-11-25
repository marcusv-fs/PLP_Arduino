package utils;

import java.util.*;
import java.io.*;

/**
 * Utility class that writes an Arduino .ino sketch from collected data.
 * Expected inputs:
 *  - outputPins: a Set containing Integer (or parsable) pin numbers for OUTPUT pins (digital)
 *  - inputPins: a Set containing Integer (or parsable) pin numbers for INPUT_PULLUP pins (digital)
 *  - analogOutputPins: a Set containing Integer (or parsable) pin numbers for OUTPUT pins (PWM)
 *  - analogInputPins: a Set containing Integer (or parsable) pin numbers for INPUT pins (analog A0-A5)
 *  - loopBody: the body of the loop() as a String (already contains proper indentation and newlines)
 *  - outFilename: the path to the .ino output file
 */
public class ArduinoSketchEmitter {

    public static void writeSketch(Set<?> outputPins, Set<?> inputPins, Set<?> analogOutputPins, Set<?> analogInputPins, String loopBody, String outFilename) {
        if (outFilename == null || outFilename.trim().isEmpty()) {
            outFilename = "generated_sketch.ino";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("// Código gerado pela DSL Arduino (português)\n");
        sb.append("#include <Arduino.h>\n\n");
        sb.append("void setup() {\n");

        // write pinMode lines for analog input pins (A0-A5) as INPUT
        for (Object pObj : analogInputPins) {
            int p;
            if (pObj instanceof Integer) p = ((Integer)pObj).intValue();
            else p = Integer.parseInt(pObj.toString());
            sb.append("  pinMode(A" + p + ", INPUT);\n");
        }

        // write pinMode lines for analog output pins (PWM: 3,5,6,9,10,11) as OUTPUT
        for (Object pObj : analogOutputPins) {
            int p;
            if (pObj instanceof Integer) p = ((Integer)pObj).intValue();
            else p = Integer.parseInt(pObj.toString());
            sb.append("  pinMode(" + p + ", OUTPUT);\n");
        }

        // write pinMode lines for digital output pins (LEDs) as OUTPUT
        for (Object pObj : outputPins) {
            int p;
            if (pObj instanceof Integer) p = ((Integer)pObj).intValue();
            else p = Integer.parseInt(pObj.toString());
            // Skip if already added as analog output
            if (!analogOutputPins.contains(p)) {
                sb.append("  pinMode(" + p + ", OUTPUT);\n");
            }
        }

        // write pinMode lines for digital input pins (botões) as INPUT_PULLUP
        for (Object pObj : inputPins) {
            int p;
            if (pObj instanceof Integer) p = ((Integer)pObj).intValue();
            else p = Integer.parseInt(pObj.toString());
            sb.append("  pinMode(" + p + ", INPUT_PULLUP);\n");
        }

        sb.append("  Serial.begin(9600);\n");
        sb.append("}\n\n");
        sb.append("void loop() {\n");
        sb.append(loopBody);
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
