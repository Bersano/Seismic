import java.io.*;
import java.nio.charset.StandardCharsets;

class Seismic {

    private File file_sgy;

    private byte[] headerByBytes = new byte[3200];
    private String[] binaryHeaderByStringArray = new String[197];
    private String[] traceHeaderByStringArray = new String[93];


    Seismic(File file_sgy) { this.file_sgy = file_sgy; }

    long getTraces() {
        return (file_sgy.length() - 3600) / (240 + getSamplesInEveryTrace() * 4);
    }

    long getSamplesInEveryTrace() {
        long samplesInEveryTrace = 0;

        try {
            RandomAccessFile stream = new RandomAccessFile(file_sgy, "r");
            stream.seek(3220);
            samplesInEveryTrace = stream.readShort();
            stream.close();
        } catch (IOException e) { e.printStackTrace(); }

        return samplesInEveryTrace;
    }

    String getTextHeader() {
        StringBuilder sb = new StringBuilder();
        try {
            RandomAccessFile stream = new RandomAccessFile(file_sgy, "r");

            for (int k = 0; k < 3200; k++)
                headerByBytes[k] = stream.readByte();

            // if (UTF-8) == 3200 symbols, else use UTF-8, else use CP-1047 (EBCDIC)
            char[] headerNewEncoding = new String(headerByBytes, StandardCharsets.UTF_8).toCharArray();
            if (headerNewEncoding.length != 3200)
                headerNewEncoding = new String(headerByBytes, "cp1047").toCharArray();

            //40 lines, one line = 80 characters
            for (int line = 0; line < 40; line++) {
                for (int chars = line * 80; chars < line * 80 + 80; chars++)
                    sb.append(headerNewEncoding[chars]);
                sb.append("\n");
            }
            stream.close();
        } catch (IOException e) { e.printStackTrace(); }

        return sb.toString();
    }

    String[] getBinaryHeader() {
        try {
            RandomAccessFile stream = new RandomAccessFile(file_sgy, "r");
            stream.seek(3200);

            for (int i = 0; i < 3; i++)
                binaryHeaderByStringArray[i] = String.valueOf(stream.readInt());

            for (int i = 0; i < 194; i++)
                binaryHeaderByStringArray[i+3] = String.valueOf(stream.readShort());

            stream.close();
        } catch (IOException e) { e.printStackTrace(); }

        return binaryHeaderByStringArray;
    }

    String getDescriptionByElementInBinaryHeader(int elem) {
        String[] description = new String[150];

        description[0] = "Job Identificator Number";                    //4byte 3201-3204
        description[1] = "Line number";                                 //  -   3205-3208
        description[2] = "Reel number";                                 //4byte 3209-3212
        description[3] = "# data traces per record";                    //2byte 3213-3214
        description[4] = "# aux traces per record";                     //  -   3215-3216
        description[5] = "Sample interval (micros*) for reel";          //  -   3217-3218
        description[6] = "Sample interval (micros*) for field";         //  -   3219-3220
        description[7] = "Number samples per data traces for reel";     //  -   3221-3222
        description[8] = "Number samples per data traces for field";    //  -   3223-3224
        description[9] = "Data sample format code";                     //  -   3225-3226
        description[10] = "CDP fold";                                   //  -   3227-3228
        description[11] = "Trace sorting code";                         //  -   3229-3230
        description[12] = "Vertical sum code";                          //  -   3231-3232
        description[13] = "Sweep frequency at start";                   //  -   3233-3234
        description[14] = "Sweep frequency at end";                     //  -   3235-3236
        description[15] = "Sweep length (ms)";                          //  -   3237-3238
        description[16] = "Sweep type code";                            //  -   3239-3240
        description[17] = "Trace number of sweep channel";              //  -   3241-3242
        description[18] = "Sweep trace taper length at start, ms";      //  -   3243-3244
        description[19] = "Sweep trace taper length at end, ms";        //  -   3245-3246
        description[20] = "Taper type";                                 //  -   3247-3248
        description[21] = "Correlated data traces";                     //  -   3249-3250
        description[22] = "Binary gain recovered";                      //  -   3251-3252
        description[23] = "Amplitude recovery method";                  //  -   3253-3254
        description[24] = "Measuriment system (1-m / 2-feet)";          //  -   3255-3256
        description[25] = "Impulse signal";                             //  -   3257-3258
        description[26] = "Vibratory polarity code";                    //  -   3259-3260

        description[147] = "SEG-Y format revision number";              //  -   3501-3502   //разобраться с SEG-Y revision 1 or 0 or 256?
        description[148] = "Fixed length trace flag";                   //  -   3503-3504
        description[149] = "Number of 3200-byte";                       //  -   3505-3506

        String returnString;

        if (elem > description.length-1) returnString = "Unsign";
        else {
            if (description[elem] == null) returnString = "Unsign";
            else returnString = description[elem];
        }

        return returnString;
    }

    // returns string array
    String[] getTraceHeader(long traceNumber) {
        if (traceNumber > getTraces()) System.out.println("TraceNumber > allTracesInFile");
        else {
            try {
                RandomAccessFile stream = new RandomAccessFile(file_sgy, "r");
                stream.seek(3600 + (traceNumber) * (240 + getSamplesInEveryTrace() * 4));
                for (int k = 0; k < 7; k++)
                    traceHeaderByStringArray[k] = String.valueOf(stream.readInt());

                for (int k = 7; k < 11; k++)
                    traceHeaderByStringArray[k] = String.valueOf(stream.readShort());

                for (int k = 11; k < 19; k++)
                    traceHeaderByStringArray[k] = String.valueOf(stream.readInt());

                for (int k = 19; k < 21; k++)
                    traceHeaderByStringArray[k] = String.valueOf(stream.readShort());

                for (int k = 21; k < 25; k++)
                    traceHeaderByStringArray[k] = String.valueOf(stream.readInt());

                for (int k = 25; k < 71; k++)
                    traceHeaderByStringArray[k] = String.valueOf(stream.readShort());

                for (int k = 71; k < 76; k++)
                    traceHeaderByStringArray[k] = String.valueOf(stream.readInt());

                for (int k = 76; k < 78; k++)
                    traceHeaderByStringArray[k] = String.valueOf(stream.readShort());

                traceHeaderByStringArray[78] = String.valueOf(stream.readInt());

                for (int k = 79; k < 85; k++)
                    traceHeaderByStringArray[k] = String.valueOf(stream.readShort());

                for (int k = 85; k < 87; k++)
                    traceHeaderByStringArray[k] = String.valueOf(stream.readInt());

                for (int k = 87; k < 93; k++)
                    traceHeaderByStringArray[k] = String.valueOf(stream.readShort());

                stream.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
        return traceHeaderByStringArray;
    }

    // stay as is
    String getDescriptionByElementInTraceHeader(int elem) {
        String[] description = new String[89];

        description[0] = "Trace sequence number within line";                       //1-4       4byte
        description[1] = "Trace sequence number within reel";                       //5-8       -
        description[2] = "FFID - Original Field Record number";                     //9-12      -
        description[3] = "Trace number within field record";                        //13-16     -
        description[4] = "SP - Energy source point number";                         //17-20     -
        description[5] = "CDP ensamble number";                                     //21-24     -
        description[6] = "Trace number";                                            //25-28     4byte
        description[7] = "Trace identification code";                               //29-30     2byte
        description[8] = "Number of vertically summed traces";                      //31-32     -
        description[9] = "Number of horizontally stacked traces";                   //33-34     -
        description[10] = "Data use (1-production, 2-test)";                        //35-36     2byte
        description[11] = "Distance from source point to recieve group";            //37-40     4byte
        description[12] = "Reciever group elevation";                               //41-44     -
        description[13] = "Surface elevation at source";                            //45-48     -
        description[14] = "Source depth below surface";                             //49-52     -
        description[15] = "Datum elevation at reciever group";                      //53-56     -
        description[16] = "Datum elevation at source";                              //57-60     -
        description[17] = "Water depth at source";                                  //61-64     -
        description[18] = "Water depth at group";                                   //65-68     4byte
        description[19] = "Scaler to all elevations & depth";                       //69-70     2byte
        description[20] = "Scaler to all coordinates";                              //71-72     2byte
        description[21] = "Source X coordinate";                                    //73-76     4byte
        description[22] = "Source Y coordinate";                                    //77-80     -
        description[23] = "Group X coordinate";                                     //81-84     -
        description[24] = "Group Y coordinate";                                     //85-88     4byte
        description[25] = "Coordinate units (1-lenm/ft; 2-secarc)";                 //89-90     2byte
        description[26] = "Weathering velocity";                                    //91-92     -
        description[27] = "Subweathering velocity";                                 //93-94     -
        description[28] = "Uphole time at source";                                  //95-96     -
        description[29] = "Uphole time at group";                                   //97-98     -
        description[30] = "Source static correction";                               //99-100    -
        description[31] = "Group static correlation";                               //101-102   -
        description[32] = "Total static correlation";                               //103-104   -
        description[33] = "Lag time A";                                             //105-106   -
        description[34] = "Lag time B";                                             //107-108   -
        description[35] = "Delay Recording time";                                   //109-110   -
        description[36] = "Mute time start";                                        //111-112   -
        description[37] = "Mute time end";                                          //113-114   -
        description[38] = "Number of samples in this trace";                        //115-116   -
        description[39] = "Sample interval in ms for this trace";                   //117-118   -
        description[40] = "Gain type of field instruments";                         //119-120   -
        description[41] = "Instrument gain";                                        //121-122   -
        description[42] = "Instrument gain constant";                               //123-124   -
        description[43] = "Correlated (1-yes; 2-no)";                               //125-126   -
        description[44] = "Sweep frequency at start";                               //127-128   -
        description[45] = "Sweep frequency at end";                                 //129-130   -
        description[46] = "Sweep length in ms";                                     //131-132   -
        description[47] = "Sweep type 1-lin; 2-parabol; 3-exp; 4-other";            //133-134   -
        description[48] = "Sweep trace taper length at start in ms";                //135-136   -
        description[49] = "Sweep trace taper length at start in ms";                //137-138   -
        description[50] = "Taper type (1-lin; 2-cos; 3-other)";                     //139-140   -
        description[51] = "Alias filter frequency, if used";                        //141-142   -
        description[52] = "Alias filter slope";                                     //143-144   -
        description[53] = "Notch filter frequency (Hz), if used";                   //145-146   -
        description[54] = "Notch filter slope (dB/octave)";                         //147-148   -
        description[55] = "Low cut frequency, if used";                             //149-150   -
        description[56] = "Hight cut frequency, if used";                           //151-152   -
        description[57] = "Low cut slope";                                          //153-154   -
        description[58] = "High cut slope";                                         //155-156   -
        description[59] = "Year data recorded";                                     //157-158   -
        description[60] = "Day of year";                                            //159-160   -
        description[61] = "Hour of day";                                            //161-162   -
        description[62] = "Minute of hour";                                         //163-164   -
        description[63] = "Second of minute";                                       //165-166   -
        description[64] = "Time basis (1-local; 2-GMT; 3-other)";                   //167-168   -
        description[65] = "Trace weighting factor";                                 //169-170   -
        description[66] = "Geophone group number of roll sw pos1";                  //171-172   -
        description[67] = "Geophone group number of trace";                         //173-174   -
        description[68] = "Geophone group number of last trace";                    //175-176   -
        description[69] = "Gap size (total # of group dropped)";                    //177-178   -
        description[70] = "Overtravel asscos w taper of beg/end line";              //179-180   2byte
        description[71] = "CDP X";                                                  //181-184   4byte
        description[72] = "CDP Y";                                                  //185-188   -
        description[73] = "Inline Number";                                          //189-192   -
        description[74] = "Crossline Number";                                       //193-196   -
        description[75] = "Shot Point Number";                                      //197-200   4byte
        description[76] = "Shot Point Scalar";                                      //201-202   2byte
        description[77] = "Trace value measurement unit";                           //203-204   2byte
        description[78] = "Transduction constant. Mantissa (Mantissa*10**integer)"; //205-208   4byte
        description[79] = "Transduction constant. Complement integer";              //209-210   2byte
        description[80] = "Transduction units";                                     //211-212   -
        description[81] = "Device/Trace Identifier";                                //213-214   -
        description[82] = "Scalar to be applied";                                   //215-216   -
        description[83] = "Source type/orientation";                                //217-218   -
        description[84] = "Source Energy Direction";                                //219-220   2byte
        description[85] = "Source Energy Direction";                                //221-224   4byte
        description[86] = "Source Measurement. Mantissa";                           //225-228   4byte
        description[87] = "Source Measurement. Complement integer";                 //229-230   2byte
        description[88] = "Source Measurement Unit";                                //231-232   -

        String returnString;

        if (elem > description.length-1) returnString = "Unsign";
        else {
            if (description[elem] == null) returnString = "Unsign";
            else returnString = description[elem];
        }

        return returnString;
    }

    float[] getTraceData(long traceNumber) {
        float[] traceDataByOneTrace = new float[(int)getSamplesInEveryTrace()];

        try {
            RandomAccessFile stream = new RandomAccessFile(file_sgy, "r");

            stream.seek(3600 + (traceNumber * 240) + (traceNumber - 1) * (getSamplesInEveryTrace() * 4));

            for (int sample = 0; sample < (int)getSamplesInEveryTrace(); sample++)
                traceDataByOneTrace[sample] = new SinglePrecision().getIBM32(stream.readFloat());


            stream.close();
        } catch (IOException e) { e.printStackTrace(); }

        return traceDataByOneTrace;
    }

    String[][] getAllTraceHeaders() {
        String[][] new_arr = new String[(int)getTraces()][(int)getSamplesInEveryTrace()];
        for (int i = 0; i < getTraces(); i++)
            new_arr[i] = getTraceHeader(i);
        return new_arr;
    }

    String[][] getAllXYForEveryTrace() {
        String[][] new_arr = new String[(int)getTraces()][2];
        for (int i = 0; i < getTraces(); i++) {
            new_arr[i][0] = getTraceHeader(i)[21];
            new_arr[i][1] = getTraceHeader(i)[22];
        }
        return new_arr;
    }






    static class SinglePrecision {

        private double getDecimalValueOfMantissa(String input) {
            double decimalValueOMantissa = 0.0;
            char[] inputToCharArray = input.toCharArray();
            for (int k = 0; k < inputToCharArray.length-1; k++)
                if (inputToCharArray[k] == '1') decimalValueOMantissa = decimalValueOMantissa + Math.pow(2.0, -1.0*(k+1));
            return decimalValueOMantissa;
        }

        private String getStringBits(float input) {
            String returnString;
            if (Integer.toBinaryString(Float.floatToIntBits(input)).length() != 32) {
                char[] normalisedNumber32length = new char[32];
                int lackOf = 32 - Integer.toBinaryString(Float.floatToIntBits(input)).length();

                for (int k = 0; k < lackOf; k++)
                    normalisedNumber32length[k] = '0';

                for (int k = lackOf; k < 32; k++)
                    normalisedNumber32length[k] = Integer.toBinaryString(Float.floatToIntBits(input)).toCharArray()[k-lackOf];

                StringBuilder ssBuildString = new StringBuilder();
                for (char elem : normalisedNumber32length) ssBuildString.append(elem);
                returnString = ssBuildString.toString();
            } else returnString = Integer.toBinaryString(Float.floatToIntBits(input));
            return returnString;
        }

        float getIBM32(float inputIBM32) {
            float returnFloat;
            char signIBM32 = getStringBits(inputIBM32).toCharArray()[0];

            StringBuilder sbExpIBM32 = new StringBuilder();
            for (int k = 1; k < 8; k++)
                sbExpIBM32.append(getStringBits(inputIBM32).toCharArray()[k]);

            String expIBM32 = sbExpIBM32.toString();

            StringBuilder sbMantissaIBM32 = new StringBuilder();
            for (int k = 1; k < 25; k++)
                sbMantissaIBM32.append(getStringBits(inputIBM32).toCharArray()[k+7]);

            String mantissaIBM32 = sbMantissaIBM32.toString();

            returnFloat = (float)(Math.pow(-1.0, (double)signIBM32) * Math.pow(16.0, (Integer.parseInt(expIBM32, 2) - 64)) * getDecimalValueOfMantissa(mantissaIBM32));
            return returnFloat;
        }

        //for checking input(IEEE754) -> output(IEEE754), input == output
        float getIEEE754(float inputIEEE754) {
            float returnFloat;
            char signIEEE754 = getStringBits(inputIEEE754).toCharArray()[0];

            StringBuilder sbExpIEEE754 = new StringBuilder();
            for (int k = 1; k < 9; k++)
                sbExpIEEE754.append(getStringBits(inputIEEE754).toCharArray()[k]);

            String expIEEE754 = sbExpIEEE754.toString();

            StringBuilder sbMantissaIEEE754 = new StringBuilder();
            for (int k = 1; k < 24; k++)
                sbMantissaIEEE754.append(getStringBits(inputIEEE754).toCharArray()[k+8]);

            String mantissaIEEE754 = sbMantissaIEEE754.toString();

            returnFloat = (float)(Math.pow(-1.0, (double)signIEEE754) * Math.pow(2.0, (Integer.parseInt(expIEEE754, 2) - 127) ) * (1.0 + getDecimalValueOfMantissa(mantissaIEEE754)));
            return returnFloat;
        }
    }
}