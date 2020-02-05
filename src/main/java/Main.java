import java.io.File;

public class Main {
    public static void main(String[] args) {

        File file1 = new File("/Users/sukhanov_ra/_notebooks/SeismicInpainting_XY/F3_Original_Seismic_Cube.sgy");
        File file2 = new File("/Users/sukhanov_ra/Documents/Gazprom_Neft/Hackathon 2019/Хакатон/Предсказание характеристик пласта по сейсме/SeismicPartOhHackathon_2019/3D_cube_new.sgy");



        long start1 = System.currentTimeMillis();
        Seismic seismic = new Seismic(file1);
        String[][] a = seismic.getAllTraceHeaders();
        System.out.println("time = " + (System.currentTimeMillis() - start1)/10e6);


        long start2 = System.currentTimeMillis();
        SeismicBytes seismicBytes = new SeismicBytes(file1);
        String[][] b = seismicBytes.getAllTraceHeaders();
        System.out.println("time = " + (System.currentTimeMillis() - start2)/10e6);


    }
}
