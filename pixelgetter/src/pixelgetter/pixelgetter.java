package pixelgetter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;

import javax.imageio.ImageIO;

public class pixelgetter {
	public static void main(String[] args) throws IOException {
		final File folder = new File("DIRECT_FOLDER_PATH");
        FileWriter writer = new FileWriter("out.txt");
        
        BitSet bits = new BitSet(4488192);
        int bitI = 0;
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.getName().substring(0,3).equals("out")) {
	        	BufferedImage pic = ImageIO.read(fileEntry);
	        	for(int x = 0; x < pic.getWidth(); x++ ) {
		            for (int y = 0; y < pic.getHeight(); y++ ) {
		               int r = pic.getRGB(y,x);
		               bits.set(bitI, (r == -1));
		               bitI++;
		            }
	        	}
	        }
	    }
	    System.out.println(bitI);
	    System.out.println(bits.length());
        byte[] bytes = bits.toByteArray(); 
        System.out.println(bytes.length);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
        	sb.append('$');
            sb.append(String.format("%02X", b));
            sb.append(',');
        }
        writer.write(sb.toString());
        writer.close();
        // prints "FF 00 01 02 03 "
	}
}
