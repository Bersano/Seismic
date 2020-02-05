class SinglePrecision {

    private double getDecimalValueOfMantissa(String input) {
        double decimalValueOfMantissa = 0.0;
        char[] inputToCharArray = input.toCharArray();
        for (int k = 0; k < inputToCharArray.length-1; k++)
            if (inputToCharArray[k] == '1') decimalValueOfMantissa = decimalValueOfMantissa + Math.pow(2.0, -1.0*(k+1));
        return decimalValueOfMantissa;
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
        char[] chars = getStringBits(inputIBM32).toCharArray();

        char signIBM32 = chars[0];

        char[] expIBM32_char = new char[7];
        for (int k = 0; k < 7; k++)
            expIBM32_char[k] = chars[k+1];
        String expIBM32 = String.valueOf(expIBM32_char);

        char[] mantissaIBM32_char = new char[24];
        for (int k = 0; k < 24; k++)
            mantissaIBM32_char[k] = chars[k+8];
        String mantissaIBM32 = String.valueOf(mantissaIBM32_char);


        float returnFloat = (float)(Math.pow(-1.0, (double)signIBM32) * Math.pow(16.0, (Integer.parseInt(expIBM32, 2) - 64)) * getDecimalValueOfMantissa(mantissaIBM32));
        return returnFloat;
    }

    //for checking input(IEEE754) -> output(IEEE754), input == output
    float getIEEE754(float inputIEEE754) {
        char[] chars = getStringBits(inputIEEE754).toCharArray();
        char signIEEE754 = chars[0];

        StringBuilder sbExpIEEE754 = new StringBuilder();
        for (int k = 1; k < 9; k++)
            sbExpIEEE754.append(chars[k]);

        String expIEEE754 = sbExpIEEE754.toString();

        StringBuilder sbMantissaIEEE754 = new StringBuilder();
        for (int k = 1; k < 24; k++)
            sbMantissaIEEE754.append(chars[k+8]);

        String mantissaIEEE754 = sbMantissaIEEE754.toString();

        float returnFloat = (float)(Math.pow(-1.0, (double)signIEEE754) * Math.pow(2.0, (Integer.parseInt(expIEEE754, 2) - 127) ) * (1.0 + getDecimalValueOfMantissa(mantissaIEEE754)));
        return returnFloat;
    }
}