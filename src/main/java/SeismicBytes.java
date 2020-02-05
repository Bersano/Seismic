import me.tongfei.progressbar.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

class SeismicBytes {

    private File file_sgy;

    private byte[] sgyInBytes;

    private byte[] headerByBytes = new byte[3200];
    private String[] binaryHeaderByStringArray = new String[197];
    private String[] traceHeaderByStringArray = new String[93];


    SeismicBytes(File file_sgy) {
        this.file_sgy = file_sgy;
        sgyInBytes = new byte[(int)file_sgy.length()];
        try { sgyInBytes = Files.readAllBytes(file_sgy.toPath()); }
        catch (IOException e) { e.printStackTrace(); }
    }


    long getTraces_byBytes() { return (file_sgy.length() - 3200 - 400) / (240 + getSamplesInEveryTrace_byBytes() * 4); }

    long getSamplesInEveryTrace_byBytes() {

        byte[] k = new byte[2];
        k[0] = sgyInBytes[3220];
        k[1] = sgyInBytes[3221];

        return ByteBuffer.wrap(k).getShort();
    }


    String getTextHeader_byBytes(String coding) {
        StringBuilder sb = new StringBuilder();

        byte[] txtHeaderInBytes = new byte[3200];
        System.arraycopy(sgyInBytes, 0, txtHeaderInBytes, 0, 3200);

        // if (UTF-8) == 3200 symbols, else use UTF-8, else use CP-1047 (EBCDIC)

        char[] headerNewEncoding = null;

        switch (coding) {
            case "UTF8":
                headerNewEncoding = (new String(txtHeaderInBytes, StandardCharsets.UTF_8)).toCharArray();
                break;

            case "ASCII":
                headerNewEncoding = (new String(txtHeaderInBytes, StandardCharsets.US_ASCII)).toCharArray();
                break;

            case "EBCDIC":
                try { headerNewEncoding = (new String(txtHeaderInBytes, "cp1047")).toCharArray(); }
                catch (UnsupportedEncodingException e) { e.printStackTrace(); }
                break;

            default:
        }

        List<Character> k = new LinkedList<Character>();
        for (int elem = 0; elem < headerNewEncoding.length; elem++)
            k.add(headerNewEncoding[elem]);

        if (k.size() < 3200) {
            int ne = 3200 - k.size();
            for (int i = 0; i < ne; i++)
                k.add(' ');
        }


        //40 lines, one line = 80 characters
        for (int line = 0; line < 40; line++) {
            for (int chars = line * 80; chars < line * 80 + 80; chars++)
                sb.append(k.get(chars));
            sb.append("\n");
        }

        return sb.toString();
    }


    String[] getBinaryHeader_byBytes() {
        byte[] bytesForInt = new byte[12];
        byte[] bytesForShort = new byte[388];

        System.arraycopy(sgyInBytes, 3200, bytesForInt, 0, 12);
        System.arraycopy(sgyInBytes, 3212, bytesForShort, 0, 388);

        for (int i = 0; i < 3; i++) {
            byte[] b = new byte[4];

            System.arraycopy(bytesForInt, i*4, b, 0, 4);
            binaryHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getInt());
        }

        for (int i = 0; i < 194; i++) {
            byte[] b = new byte[2];

            System.arraycopy(bytesForShort, i*2, b, 0, 2);
            binaryHeaderByStringArray[i+3] = String.valueOf((ByteBuffer.wrap(b).getShort()));
        }

        return binaryHeaderByStringArray;
    }

    String getDescriptionByElementInBinaryHeader_byBytes(int elem) {
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
    String[] getTraceHeader_byBytes(long traceNumber) {
        if (traceNumber > getTraces_byBytes()) System.out.println("TraceNumber > allTracesInFile");
        else {

            byte[] bytesPerTrace = new byte[240];

            System.arraycopy(sgyInBytes, (int) (3600 + traceNumber * (240 + getSamplesInEveryTrace_byBytes() * 4)),
                    bytesPerTrace, 0, 240);


            for (int i = 0; i < 7; i++) {
                byte[] b = new byte[4];

                System.arraycopy(bytesPerTrace, i * 4, b, 0, 4);
                traceHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getInt());
            }

            for (int i = 7; i < 11; i++) {
                byte[] b = new byte[2];

                System.arraycopy(bytesPerTrace, 28 + (i - 7) * 2, b, 0, 2);
                traceHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getShort());
            }

            for (int i = 11; i < 19; i++) {
                byte[] b = new byte[4];

                System.arraycopy(bytesPerTrace, 28 + 8 + (i - 11) * 4, b, 0, 4);
                traceHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getInt());
            }

            for (int i = 19; i < 21; i++) {
                byte[] b = new byte[2];

                System.arraycopy(bytesPerTrace, 28 + 8 + 32 + (i - 19) * 2, b, 0, 2);
                traceHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getShort());
            }

            for (int i = 21; i < 25; i++) {
                byte[] b = new byte[4];

                System.arraycopy(bytesPerTrace, 28 + 8 + 32 + 4 + (i - 21) * 4, b, 0, 4);
                traceHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getInt());
            }

            for (int i = 25; i < 71; i++) {
                byte[] b = new byte[2];

                System.arraycopy(bytesPerTrace, 28 + 8 + 32 + 4 + 16 + (i - 25) * 2, b, 0, 2);
                traceHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getShort());
            }

            for (int i = 71; i < 76; i++) {
                byte[] b = new byte[4];

                System.arraycopy(bytesPerTrace,
                        28 + 8 + 32 + 4 + 16 + 92 + (i - 71) * 4, b, 0, 4);
                traceHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getInt());
            }

            for (int i = 76; i < 78; i++) {
                byte[] b = new byte[2];

                System.arraycopy(bytesPerTrace,
                        28 + 8 + 32 + 4 + 16 + 92 + 20 + (i - 76) * 2, b, 0, 2);
                traceHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getShort());
            }

            byte[] tmp_b = new byte[4];
            System.arraycopy(bytesPerTrace,
                    28 + 8 + 32 + 4 + 16 + 92 + 20 + 2, tmp_b, 0, 4);
            traceHeaderByStringArray[78] = String.valueOf(ByteBuffer.wrap(tmp_b).getInt());

            for (int i = 79; i < 85; i++) {
                byte[] b = new byte[2];

                System.arraycopy(bytesPerTrace,
                        28 + 8 + 32 + 4 + 16 + 92 + 20 + 2 + 4 + (i - 79) * 2, b, 0, 2);
                traceHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getShort());
            }

            for (int i = 85; i < 87; i++) {
                byte[] b = new byte[4];

                System.arraycopy(bytesPerTrace,
                        28 + 8 + 32 + 4 + 16 + 92 + 20 + 2 + 4 + 12 + (i - 85) * 4, b, 0, 4);
                traceHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getInt());
            }

            for (int i = 87; i < 93; i++) {
                byte[] b = new byte[2];

                System.arraycopy(bytesPerTrace,
                        28 + 8 + 32 + 4 + 16 + 92 + 20 + 2 + 4 + 12 + 8 + (i - 87) * 2, b, 0, 2);
                traceHeaderByStringArray[i] = String.valueOf(ByteBuffer.wrap(b).getShort());
            }

        }
        return traceHeaderByStringArray;
    }

    // stay as is
    String getDescriptionByElementInTraceHeader_byBytes(int elem) {
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

    float[] getTraceData_byBytes(int traceNumber) {
        float[] traceDataByOneTrace = new float[(int)getSamplesInEveryTrace_byBytes()];

        SinglePrecision s_precision = new SinglePrecision();

        for (int sample = 0; sample < (int)getSamplesInEveryTrace_byBytes(); sample++) {
            byte[] b = new byte[4];

            System.arraycopy(sgyInBytes, 3600 + (traceNumber * 240) + (traceNumber - 1) *
                    ((int)getSamplesInEveryTrace_byBytes()*4) + (sample*4), b, 0, 4);

            traceDataByOneTrace[sample] = s_precision.getIBM32(ByteBuffer.wrap(b).getFloat());
        }

        return traceDataByOneTrace;
    }

    float[][] getAllTraceData_byBytes() {
        float[][] out = new float[(int)getTraces_byBytes()][(int)getSamplesInEveryTrace_byBytes()];
        try (ProgressBar pb = new ProgressBar("Reading SGY", getTraces_byBytes(), ProgressBarStyle.ASCII)) {
            for (int i = 0; i < getTraces_byBytes(); i++) {
                out[i] = getTraceData_byBytes(i);
                pb.step();
            }
        }
        return out;
    }

    String[][] getAllTraceHeaders() {
        String[][] new_arr = new String[(int)getTraces_byBytes()][(int)getSamplesInEveryTrace_byBytes()];
        try (ProgressBar pb = new ProgressBar("Reading SGY", getTraces_byBytes(), ProgressBarStyle.ASCII)) {
            for (int i = 0; i < getTraces_byBytes(); i++) {
                new_arr[i] = getTraceHeader_byBytes(i);
                pb.step();
            }
        }
        return new_arr;
    }
}